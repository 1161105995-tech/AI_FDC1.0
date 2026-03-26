package com.smartarchive.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartarchive.archive.domain.ArchiveReceipt;
import com.smartarchive.archive.domain.BorrowRecord;
import com.smartarchive.archive.domain.CatalogTask;
import com.smartarchive.archive.domain.InventoryTask;
import com.smartarchive.archive.service.ArchiveObjectService;
import com.smartarchive.archive.service.ArchiveReceiptService;
import com.smartarchive.archive.service.BorrowRecordService;
import com.smartarchive.archive.service.CatalogTaskService;
import com.smartarchive.archive.service.InventoryTaskService;
import com.smartarchive.archivemanage.domain.ArchiveRecord;
import com.smartarchive.archivemanage.mapper.ArchiveRecordMapper;
import com.smartarchive.dashboard.dto.DashboardDistributionItem;
import com.smartarchive.dashboard.dto.DashboardMetric;
import com.smartarchive.dashboard.dto.DashboardNoticeItem;
import com.smartarchive.dashboard.dto.DashboardRecentItem;
import com.smartarchive.dashboard.dto.DashboardRiskIndicator;
import com.smartarchive.dashboard.dto.DashboardSummaryResponse;
import com.smartarchive.dashboard.dto.DashboardTaskBucket;
import com.smartarchive.dashboard.dto.DashboardTaskItem;
import com.smartarchive.dashboard.dto.DashboardTrendGroup;
import com.smartarchive.dashboard.dto.DashboardTrendPoint;
import com.smartarchive.dashboard.dto.DashboardWorkspaceStats;
import com.smartarchive.dashboard.service.DashboardService;
import com.smartarchive.warehouse.service.WarehouseService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final List<String> DISTRIBUTION_COLORS = List.of("#2563eb", "#0f766e", "#d97706", "#7c3aed", "#64748b");

    private final ArchiveObjectService archiveObjectService;
    private final WarehouseService warehouseService;
    private final ArchiveReceiptService archiveReceiptService;
    private final BorrowRecordService borrowRecordService;
    private final CatalogTaskService catalogTaskService;
    private final InventoryTaskService inventoryTaskService;
    private final ArchiveRecordMapper archiveRecordMapper;

    @Override
    public DashboardSummaryResponse getSummary() {
        LocalDate today = LocalDate.now();
        List<ArchiveRecord> archiveRecords = listArchiveRecords();
        List<ArchiveReceipt> receipts = archiveReceiptService.listReceipts();
        List<BorrowRecord> borrowRecords = borrowRecordService.listRecords();
        List<CatalogTask> catalogTasks = catalogTaskService.listTasks();
        List<InventoryTask> inventoryTasks = inventoryTaskService.listTasks();

        long archiveRecordCount = archiveRecords.size();
        long archiveObjectCount = archiveObjectService.count();
        long archiveTotal = archiveRecordCount > 0 ? archiveRecordCount : archiveObjectCount;
        long monthlyArchiveCount = archiveRecords.stream()
            .filter(item -> item.getCreationDate() != null)
            .filter(item -> item.getCreationDate().getYear() == today.getYear() && item.getCreationDate().getMonthValue() == today.getMonthValue())
            .count();
        long transferPendingCount = archiveRecords.stream().filter(item -> "CREATED".equals(item.getArchiveStatus())).count()
            + receipts.stream().filter(item -> !"COMPLETED".equalsIgnoreCase(item.getReceiveStatus())).count();
        long storagePendingCount = archiveRecords.stream().filter(item -> !"STORED".equals(item.getArchiveStatus())).count();
        long currentBorrowingCount = borrowRecords.stream().filter(this::isBorrowingActive).count();
        long overdueBorrowCount = borrowRecords.stream().filter(this::isBorrowingOverdue).count();
        long overdueCatalogCount = catalogTasks.stream().filter(item -> isOverdue(item.getDueDate(), !"COMPLETED".equalsIgnoreCase(item.getTaskStatus()))).count();
        long overdueInventoryCount = inventoryTasks.stream().filter(item -> isOverdue(item.getDueDate(), !"COMPLETED".equalsIgnoreCase(item.getTaskStatus()))).count();
        long metadataMissingCount = archiveRecords.stream().filter(this::isMetadataMissing).count();
        long parseFailedCount = archiveRecords.stream().filter(item -> "FAILED".equalsIgnoreCase(item.getParseStatus())).count();
        long duplicateSuspectCount = archiveRecords.stream()
            .collect(Collectors.groupingBy(item -> normalize(item.getDocumentName()), Collectors.counting()))
            .values().stream()
            .filter(count -> count > 1)
            .count();

        List<DashboardTaskItem> todoTasks = buildTodoTasks(receipts, borrowRecords, catalogTasks, inventoryTasks);
        List<DashboardTaskItem> initiatedTasks = buildInitiatedTasks(receipts, archiveRecords);
        List<DashboardTaskItem> processedTasks = buildProcessedTasks(borrowRecords, catalogTasks, archiveRecords);
        List<DashboardTaskItem> followingTasks = buildFollowingTasks(inventoryTasks, archiveRecords);

        DashboardWorkspaceStats workspaceStats = new DashboardWorkspaceStats(
            todoTasks.stream().filter(item -> !"已完成".equals(item.getStatus())).count(),
            todoTasks.stream().filter(item -> isDueToday(item.getDeadline())).count(),
            todoTasks.stream().filter(item -> "已超期".equals(item.getStatus())).count(),
            todoTasks.stream().filter(item -> "高优".equals(item.getPriority())).count()
        );

        return DashboardSummaryResponse.builder()
            .metrics(buildMetrics(archiveTotal, monthlyArchiveCount, transferPendingCount, storagePendingCount, currentBorrowingCount, overdueBorrowCount))
            .workspaceStats(workspaceStats)
            .workspaceBuckets(List.of(
                DashboardTaskBucket.builder().key("todo").label("我的待办").tasks(limit(todoTasks, 10)).build(),
                DashboardTaskBucket.builder().key("initiated").label("我发起的").tasks(limit(initiatedTasks, 10)).build(),
                DashboardTaskBucket.builder().key("processed").label("我处理的").tasks(limit(processedTasks, 10)).build(),
                DashboardTaskBucket.builder().key("following").label("我关注的").tasks(limit(followingTasks, 10)).build()
            ))
            .notifications(buildNotifications(overdueBorrowCount, overdueCatalogCount + overdueInventoryCount, metadataMissingCount, parseFailedCount))
            .recentActivities(buildRecentActivities(archiveRecords, receipts, borrowRecords))
            .archiveTrends(buildArchiveTrends(archiveRecords, receipts))
            .borrowTrends(buildBorrowTrends(borrowRecords))
            .distributions(buildDistributions(archiveRecords))
            .riskIndicators(List.of(
                new DashboardRiskIndicator("超期未归档数量", overdueCatalogCount),
                new DashboardRiskIndicator("借阅超期未还数量", overdueBorrowCount),
                new DashboardRiskIndicator("元数据缺失档案数", metadataMissingCount),
                new DashboardRiskIndicator("OCR/解析失败数量", parseFailedCount),
                new DashboardRiskIndicator("疑似重复档案数", duplicateSuspectCount)
            ))
            .build();
    }

    private List<ArchiveRecord> listArchiveRecords() {
        return archiveRecordMapper.selectList(new LambdaQueryWrapper<ArchiveRecord>()
            .eq(ArchiveRecord::getDeleteFlag, "N")
            .orderByDesc(ArchiveRecord::getLastUpdateDate));
    }

    private List<DashboardMetric> buildMetrics(
        long archiveTotal,
        long monthlyArchiveCount,
        long transferPendingCount,
        long storagePendingCount,
        long currentBorrowingCount,
        long overdueBorrowCount
    ) {
        long warehouseLocations = warehouseService.count();
        return List.of(
            new DashboardMetric("archive_total", "档案总量", String.valueOf(archiveTotal), "库位总数 " + warehouseLocations),
            new DashboardMetric("month_new", "本月新增归档量", String.valueOf(monthlyArchiveCount), "本月归档实时统计"),
            new DashboardMetric("transfer_pending", "待移交数量", String.valueOf(transferPendingCount), "含待接收与待流转"),
            new DashboardMetric("storage_pending", "待入库数量", String.valueOf(storagePendingCount), "待上架与待入库"),
            new DashboardMetric("borrowing", "当前借阅中数量", String.valueOf(currentBorrowingCount), "借出中和审批通过"),
            new DashboardMetric("overdue_return", "逾期未归还数量", String.valueOf(overdueBorrowCount), "高风险项优先处理")
        );
    }

    private List<DashboardTaskItem> buildTodoTasks(
        List<ArchiveReceipt> receipts,
        List<BorrowRecord> borrowRecords,
        List<CatalogTask> catalogTasks,
        List<InventoryTask> inventoryTasks
    ) {
        List<DashboardTaskItem> tasks = new ArrayList<>();
        receipts.stream()
            .filter(item -> !"COMPLETED".equalsIgnoreCase(item.getReceiveStatus()))
            .limit(3)
            .forEach(item -> tasks.add(DashboardTaskItem.builder()
                .title(item.getArchiveTitle())
                .businessType("归档")
                .currentStep(mapReceiptStep(item.getReceiveStatus()))
                .initiator(defaultString(item.getSubmittedBy(), "系统管理员"))
                .deadline(formatDateTime(item.getSubmittedAt() == null ? null : item.getSubmittedAt().plusDays(1)))
                .priority("高优")
                .status(resolveTimedStatus(item.getSubmittedAt() == null ? null : item.getSubmittedAt().plusDays(1), true))
                .route("/archive-management/create")
                .build()));
        borrowRecords.stream()
            .filter(item -> !"RETURNED".equalsIgnoreCase(item.getBorrowStatus()))
            .limit(3)
            .forEach(item -> tasks.add(DashboardTaskItem.builder()
                .title(item.getArchiveTitle())
                .businessType("借阅")
                .currentStep(mapBorrowStep(item))
                .initiator(defaultString(item.getBorrower(), "业务用户"))
                .deadline(formatDate(item.getExpectedReturnDate()))
                .priority(isBorrowingOverdue(item) ? "高优" : "紧急")
                .status(resolveTimedStatus(item.getExpectedReturnDate(), !"RETURNED".equalsIgnoreCase(item.getBorrowStatus())))
                .route("/archive-management/borrow")
                .build()));
        catalogTasks.stream()
            .filter(item -> !"COMPLETED".equalsIgnoreCase(item.getTaskStatus()))
            .limit(2)
            .forEach(item -> tasks.add(DashboardTaskItem.builder()
                .title(item.getArchiveTitle())
                .businessType("补录")
                .currentStep("编目处理")
                .initiator(defaultString(item.getAssignee(), "档案管理员"))
                .deadline(formatDate(item.getDueDate()))
                .priority(isOverdue(item.getDueDate(), true) ? "高优" : "普通")
                .status(resolveTimedStatus(item.getDueDate(), true))
                .route("/archive-management/query")
                .build()));
        inventoryTasks.stream()
            .filter(item -> !"COMPLETED".equalsIgnoreCase(item.getTaskStatus()))
            .limit(2)
            .forEach(item -> tasks.add(DashboardTaskItem.builder()
                .title(item.getInventoryScope())
                .businessType("审批")
                .currentStep("盘点执行")
                .initiator(defaultString(item.getOwner(), "档案管理员"))
                .deadline(formatDate(item.getDueDate()))
                .priority(item.getAbnormalCount() != null && item.getAbnormalCount() > 0 ? "紧急" : "普通")
                .status(resolveTimedStatus(item.getDueDate(), true))
                .route("/archive-management/storage")
                .build()));

        return tasks.stream()
            .sorted(Comparator.comparing(DashboardTaskItem::getDeadline, Comparator.nullsLast(String::compareTo)))
            .toList();
    }

    private List<DashboardTaskItem> buildInitiatedTasks(List<ArchiveReceipt> receipts, List<ArchiveRecord> archiveRecords) {
        List<DashboardTaskItem> tasks = new ArrayList<>();
        receipts.stream().limit(5).forEach(item -> tasks.add(DashboardTaskItem.builder()
            .title(item.getArchiveTitle())
            .businessType("归档")
            .currentStep(mapReceiptStep(item.getReceiveStatus()))
            .initiator(defaultString(item.getSubmittedBy(), "系统管理员"))
            .deadline(formatDateTime(item.getSubmittedAt()))
            .priority("普通")
            .status(mapGenericStatus(item.getReceiveStatus()))
            .route("/archive-management/create")
            .build()));
        archiveRecords.stream().limit(5).forEach(item -> tasks.add(DashboardTaskItem.builder()
            .title(item.getDocumentName())
            .businessType("归档")
            .currentStep(mapArchiveStep(item.getArchiveStatus()))
            .initiator("系统管理员")
            .deadline(formatDateTime(item.getLastUpdateDate()))
            .priority("普通")
            .status(mapArchiveStatus(item.getArchiveStatus()))
            .route("/archive-management/create")
            .build()));
        return limit(tasks, 10);
    }

    private List<DashboardTaskItem> buildProcessedTasks(List<BorrowRecord> borrowRecords, List<CatalogTask> catalogTasks, List<ArchiveRecord> archiveRecords) {
        List<DashboardTaskItem> tasks = new ArrayList<>();
        borrowRecords.stream()
            .filter(item -> "RETURNED".equalsIgnoreCase(item.getBorrowStatus()) || "REJECTED".equalsIgnoreCase(item.getApprovalStatus()))
            .limit(4)
            .forEach(item -> tasks.add(DashboardTaskItem.builder()
                .title(item.getArchiveTitle())
                .businessType("借阅")
                .currentStep("流程结束")
                .initiator(defaultString(item.getBorrower(), "业务用户"))
                .deadline(formatDate(item.getExpectedReturnDate()))
                .priority("普通")
                .status("已完成")
                .route("/archive-management/borrow")
                .build()));
        catalogTasks.stream()
            .filter(item -> "COMPLETED".equalsIgnoreCase(item.getTaskStatus()))
            .limit(3)
            .forEach(item -> tasks.add(DashboardTaskItem.builder()
                .title(item.getArchiveTitle())
                .businessType("补录")
                .currentStep("编目完成")
                .initiator(defaultString(item.getAssignee(), "档案管理员"))
                .deadline(formatDate(item.getDueDate()))
                .priority("普通")
                .status("已完成")
                .route("/archive-management/query")
                .build()));
        archiveRecords.stream()
            .filter(item -> "STORED".equalsIgnoreCase(item.getArchiveStatus()) || "BOUND".equalsIgnoreCase(item.getArchiveStatus()))
            .limit(3)
            .forEach(item -> tasks.add(DashboardTaskItem.builder()
                .title(item.getDocumentName())
                .businessType("归档")
                .currentStep(mapArchiveStep(item.getArchiveStatus()))
                .initiator("系统管理员")
                .deadline(formatDateTime(item.getLastUpdateDate()))
                .priority("普通")
                .status("已完成")
                .route("/archive-management/query")
                .build()));
        return limit(tasks, 10);
    }

    private List<DashboardTaskItem> buildFollowingTasks(List<InventoryTask> inventoryTasks, List<ArchiveRecord> archiveRecords) {
        List<DashboardTaskItem> tasks = new ArrayList<>();
        inventoryTasks.stream().limit(5).forEach(item -> tasks.add(DashboardTaskItem.builder()
            .title(item.getInventoryScope())
            .businessType("盘点")
            .currentStep("盘点跟踪")
            .initiator(defaultString(item.getOwner(), "档案管理员"))
            .deadline(formatDate(item.getDueDate()))
            .priority(item.getAbnormalCount() != null && item.getAbnormalCount() > 0 ? "紧急" : "普通")
            .status(resolveTimedStatus(item.getDueDate(), !"COMPLETED".equalsIgnoreCase(item.getTaskStatus())))
            .route("/archive-management/storage")
            .build()));
        archiveRecords.stream().limit(5).forEach(item -> tasks.add(DashboardTaskItem.builder()
            .title(item.getDocumentName())
            .businessType("归档")
            .currentStep(mapArchiveStep(item.getArchiveStatus()))
            .initiator(defaultString(item.getDutyPerson(), "责任人"))
            .deadline(formatDateTime(item.getLastUpdateDate()))
            .priority(isMetadataMissing(item) ? "紧急" : "普通")
            .status(mapArchiveStatus(item.getArchiveStatus()))
            .route("/archive-management/query")
            .build()));
        return limit(tasks, 10);
    }

    private List<DashboardNoticeItem> buildNotifications(
        long overdueBorrowCount,
        long overdueTaskCount,
        long metadataMissingCount,
        long parseFailedCount
    ) {
        List<DashboardNoticeItem> notifications = new ArrayList<>();
        if (overdueBorrowCount > 0) {
            notifications.add(DashboardNoticeItem.builder()
                .id("notice-borrow-overdue")
                .category("业务预警")
                .title("借阅超期未还数量达到 " + overdueBorrowCount)
                .summary("存在逾期未归还档案，请优先跟进归还或延借审批。")
                .time("实时")
                .tagType("danger")
                .route("/archive-management/borrow")
                .build());
        }
        if (overdueTaskCount > 0) {
            notifications.add(DashboardNoticeItem.builder()
                .id("notice-task-overdue")
                .category("流程催办")
                .title(overdueTaskCount + " 项任务已逾期")
                .summary("归档补录、盘点或其他任务已超过截止时间，建议优先处理。")
                .time("实时")
                .tagType("warning")
                .route("/dashboard?focus=workspace")
                .build());
        }
        if (metadataMissingCount > 0) {
            notifications.add(DashboardNoticeItem.builder()
                .id("notice-metadata")
                .category("系统通知")
                .title("元数据缺失档案 " + metadataMissingCount + " 份")
                .summary("建议进入档案查询页补齐标题、责任人、来源系统等关键字段。")
                .time("今天")
                .tagType("primary")
                .route("/archive-management/query")
                .build());
        }
        if (parseFailedCount > 0) {
            notifications.add(DashboardNoticeItem.builder()
                .id("notice-parse-failed")
                .category("借阅提醒")
                .title("OCR/解析失败档案 " + parseFailedCount + " 份")
                .summary("建议重新上传附件或检查文本解析结果。")
                .time("今天")
                .tagType("success")
                .route("/archive-management/create")
                .build());
        }
        if (notifications.isEmpty()) {
            notifications.add(DashboardNoticeItem.builder()
                .id("notice-default")
                .category("系统通知")
                .title("首页聚合接口已启用真实数据口径")
                .summary("当前首页已直接读取档案、借阅、移交、盘点等业务数据。")
                .time("今天")
                .tagType("primary")
                .route("/dashboard")
                .build());
        }
        return limit(notifications, 5);
    }

    private List<DashboardRecentItem> buildRecentActivities(
        List<ArchiveRecord> archiveRecords,
        List<ArchiveReceipt> receipts,
        List<BorrowRecord> borrowRecords
    ) {
        List<DashboardRecentWrapper> wrappers = new ArrayList<>();
        archiveRecords.stream().limit(3).forEach(item -> wrappers.add(new DashboardRecentWrapper(
            item.getLastUpdateDate(),
            DashboardRecentItem.builder()
                .id("archive-" + item.getArchiveId())
                .title(item.getDocumentName())
                .type("最近访问的档案")
                .time(formatDateTime(item.getLastUpdateDate()))
                .route("/archive-management/query")
                .build()
        )));
        receipts.stream().limit(3).forEach(item -> wrappers.add(new DashboardRecentWrapper(
            item.getSubmittedAt(),
            DashboardRecentItem.builder()
                .id("receipt-" + item.getId())
                .title(item.getArchiveTitle())
                .type("最近发起的业务")
                .time(formatDateTime(item.getSubmittedAt()))
                .route("/archive-management/create")
                .build()
        )));
        borrowRecords.stream().limit(3).forEach(item -> wrappers.add(new DashboardRecentWrapper(
            item.getBorrowedAt(),
            DashboardRecentItem.builder()
                .id("borrow-" + item.getId())
                .title(item.getArchiveTitle())
                .type("最近处理的任务")
                .time(formatDateTime(item.getBorrowedAt()))
                .route("/archive-management/borrow")
                .build()
        )));
        return wrappers.stream()
            .filter(item -> item.time() != null)
            .sorted(Comparator.comparing(DashboardRecentWrapper::time).reversed())
            .map(DashboardRecentWrapper::item)
            .limit(5)
            .toList();
    }

    private List<DashboardTrendGroup> buildArchiveTrends(List<ArchiveRecord> archiveRecords, List<ArchiveReceipt> receipts) {
        List<LocalDate> archiveDates = new ArrayList<>();
        archiveRecords.stream().map(ArchiveRecord::getCreationDate).filter(Objects::nonNull).map(LocalDateTime::toLocalDate).forEach(archiveDates::add);
        receipts.stream().map(ArchiveReceipt::getSubmittedAt).filter(Objects::nonNull).map(LocalDateTime::toLocalDate).forEach(archiveDates::add);
        return List.of(
            buildTrendGroup("近7天", archiveDates, 7, "MM-dd"),
            buildWeeklyTrendGroup("近30天", archiveDates, 30),
            buildMonthlyTrendGroup("近90天", archiveDates, 3)
        );
    }

    private List<DashboardTrendGroup> buildBorrowTrends(List<BorrowRecord> borrowRecords) {
        List<LocalDate> borrowDates = borrowRecords.stream()
            .map(BorrowRecord::getBorrowedAt)
            .filter(Objects::nonNull)
            .map(LocalDateTime::toLocalDate)
            .toList();
        return List.of(
            buildTrendGroup("近7天", borrowDates, 7, "MM-dd"),
            buildWeeklyTrendGroup("近30天", borrowDates, 30),
            buildMonthlyTrendGroup("近90天", borrowDates, 3)
        );
    }

    private DashboardTrendGroup buildTrendGroup(String range, List<LocalDate> dates, int days, String labelPattern) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(labelPattern, Locale.CHINA);
        List<DashboardTrendPoint> points = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            long count = dates.stream().filter(date::equals).count();
            points.add(new DashboardTrendPoint(date.format(formatter), count));
        }
        return DashboardTrendGroup.builder().range(range).points(points).build();
    }

    private DashboardTrendGroup buildWeeklyTrendGroup(String range, List<LocalDate> dates, int days) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(days - 1L);
        List<DashboardTrendPoint> points = new ArrayList<>();
        for (int week = 0; week < 5; week++) {
            LocalDate bucketStart = start.plusDays(week * 7L);
            LocalDate bucketEnd = week == 4 ? today : bucketStart.plusDays(6);
            long count = dates.stream().filter(date -> !date.isBefore(bucketStart) && !date.isAfter(bucketEnd)).count();
            String label = week == 4 ? "本周" : "第" + (week + 1) + "周";
            points.add(new DashboardTrendPoint(label, count));
        }
        return DashboardTrendGroup.builder().range(range).points(points).build();
    }

    private DashboardTrendGroup buildMonthlyTrendGroup(String range, List<LocalDate> dates, int months) {
        LocalDate today = LocalDate.now().withDayOfMonth(1);
        List<DashboardTrendPoint> points = new ArrayList<>();
        for (int i = months - 1; i >= 0; i--) {
            LocalDate month = today.minusMonths(i);
            long count = dates.stream()
                .filter(date -> date.getYear() == month.getYear() && date.getMonthValue() == month.getMonthValue())
                .count();
            points.add(new DashboardTrendPoint(month.getMonthValue() + "月", count));
        }
        return DashboardTrendGroup.builder().range(range).points(points).build();
    }

    private List<DashboardDistributionItem> buildDistributions(List<ArchiveRecord> archiveRecords) {
        if (archiveRecords.isEmpty()) {
            return List.of(
                new DashboardDistributionItem("合同档案", 0, DISTRIBUTION_COLORS.get(0)),
                new DashboardDistributionItem("项目资料", 0, DISTRIBUTION_COLORS.get(1)),
                new DashboardDistributionItem("财务档案", 0, DISTRIBUTION_COLORS.get(2)),
                new DashboardDistributionItem("电子载体", 0, DISTRIBUTION_COLORS.get(3))
            );
        }

        Map<String, Long> archiveTypeCounts = archiveRecords.stream()
            .map(item -> normalizeDisplay(item.getArchiveTypeCode()))
            .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
        Map<String, Long> carrierCounts = archiveRecords.stream()
            .map(item -> normalizeDisplay(item.getCarrierTypeCode()))
            .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
        long total = archiveRecords.size();

        List<DashboardDistributionItem> items = new ArrayList<>();
        archiveTypeCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .forEach(entry -> items.add(new DashboardDistributionItem(
                entry.getKey(),
                percent(entry.getValue(), total),
                DISTRIBUTION_COLORS.get(items.size() % DISTRIBUTION_COLORS.size())
            )));
        carrierCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(2)
            .forEach(entry -> items.add(new DashboardDistributionItem(
                entry.getKey() + "载体",
                percent(entry.getValue(), total),
                DISTRIBUTION_COLORS.get(items.size() % DISTRIBUTION_COLORS.size())
            )));
        return limit(items, 5);
    }

    private boolean isBorrowingActive(BorrowRecord item) {
        return "BORROWED".equalsIgnoreCase(item.getBorrowStatus()) || "APPLYING".equalsIgnoreCase(item.getBorrowStatus());
    }

    private boolean isBorrowingOverdue(BorrowRecord item) {
        return item.getExpectedReturnDate() != null && item.getExpectedReturnDate().isBefore(LocalDate.now()) && isBorrowingActive(item);
    }

    private boolean isMetadataMissing(ArchiveRecord item) {
        return isBlank(item.getDocumentName())
            || isBlank(item.getDutyPerson())
            || isBlank(item.getDocumentOrganizationCode())
            || isBlank(item.getArchiveDestination());
    }

    private boolean isOverdue(LocalDate dueDate, boolean active) {
        return active && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    private String resolveTimedStatus(LocalDate dueDate, boolean active) {
        if (isOverdue(dueDate, active)) {
            return "已超期";
        }
        return active ? "待处理" : "已完成";
    }

    private String resolveTimedStatus(LocalDateTime dueTime, boolean active) {
        if (active && dueTime != null && dueTime.isBefore(LocalDateTime.now())) {
            return "已超期";
        }
        return active ? "待处理" : "已完成";
    }

    private String mapReceiptStep(String status) {
        return switch (normalize(status)) {
            case "pending_review" -> "接收审核";
            case "processing" -> "处理中";
            case "completed" -> "已完成";
            default -> "待处理";
        };
    }

    private String mapBorrowStep(BorrowRecord item) {
        if ("PENDING".equalsIgnoreCase(item.getApprovalStatus())) {
            return "借阅审批";
        }
        if ("BORROWED".equalsIgnoreCase(item.getBorrowStatus())) {
            return "借出中";
        }
        return "归还登记";
    }

    private String mapArchiveStep(String status) {
        return switch (normalize(status)) {
            case "created" -> "待移交";
            case "transferred" -> "待成册";
            case "bound" -> "待入库";
            case "stored" -> "已入库";
            default -> "归档处理中";
        };
    }

    private String mapArchiveStatus(String status) {
        return switch (normalize(status)) {
            case "stored", "bound" -> "已完成";
            case "created", "transferred" -> "处理中";
            default -> "待处理";
        };
    }

    private String mapGenericStatus(String status) {
        return "completed".equals(normalize(status)) ? "已完成" : "处理中";
    }

    private boolean isDueToday(String deadline) {
        return deadline != null && deadline.startsWith(LocalDate.now().format(DATE_FORMATTER));
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(DATE_FORMATTER);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(DATE_TIME_FORMATTER);
    }

    private int percent(long part, long total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.round((part * 100.0) / total);
    }

    private <T> List<T> limit(List<T> source, int maxSize) {
        return source.stream().limit(maxSize).toList();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String defaultString(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeDisplay(String value) {
        if (isBlank(value)) {
            return "未分类";
        }
        return switch (normalize(value)) {
            case "paper" -> "纸质";
            case "electronic" -> "电子";
            case "hybrid" -> "混合";
            case "contract", "contract_archive" -> "合同档案";
            case "project", "project_archive" -> "项目资料";
            case "finance", "finance_archive" -> "财务档案";
            default -> value;
        };
    }

    private record DashboardRecentWrapper(LocalDateTime time, DashboardRecentItem item) {
    }
}
