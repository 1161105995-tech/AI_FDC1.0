export interface DashboardMetric {
  code: string
  label: string
  value: string
  trend: string
}

export interface DashboardWorkspaceStats {
  pendingCount: number
  dueTodayCount: number
  overdueCount: number
  highPriorityCount: number
}

export interface DashboardTaskItem {
  title: string
  businessType: string
  currentStep: string
  initiator: string
  deadline: string
  priority: string
  status: string
  route: string
}

export interface DashboardTaskBucket {
  key: string
  label: string
  tasks: DashboardTaskItem[]
}

export interface DashboardNoticeItem {
  id: string
  category: string
  title: string
  summary: string
  time: string
  tagType: 'danger' | 'warning' | 'primary' | 'success' | 'info'
  route: string
}

export interface DashboardRecentItem {
  id: string
  title: string
  type: string
  time: string
  route: string
}

export interface DashboardTrendPoint {
  label: string
  value: number
}

export interface DashboardTrendGroup {
  range: string
  points: DashboardTrendPoint[]
}

export interface DashboardDistributionItem {
  label: string
  value: number
  color: string
}

export interface DashboardRiskIndicator {
  label: string
  value: number
}

export interface DashboardSummary {
  metrics: DashboardMetric[]
  workspaceStats: DashboardWorkspaceStats
  workspaceBuckets: DashboardTaskBucket[]
  notifications: DashboardNoticeItem[]
  recentActivities: DashboardRecentItem[]
  archiveTrends: DashboardTrendGroup[]
  borrowTrends: DashboardTrendGroup[]
  distributions: DashboardDistributionItem[]
  riskIndicators: DashboardRiskIndicator[]
}

export interface ArchiveObject {
  id: number
  archiveCode: string
  title: string
  archiveType: string
  securityLevel: string
  retentionPeriod: string
  organizationName: string
  currentWorkflowStage: string
  currentLocationCode: string
  responsiblePerson: string
  formedDate: string
  aiClassified: boolean
  aiMetadataExtracted: boolean
}

export interface ArchiveReceipt {
  id: number
  receiptCode: string
  sourceDept: string
  archiveTitle: string
  archiveType: string
  securityLevel: string
  receiveStatus: string
  workflowInstanceCode: string
  submittedBy: string
  submittedAt: string
}

export interface CatalogTask {
  id: number
  taskCode: string
  archiveCode: string
  archiveTitle: string
  taskStatus: string
  assignee: string
  dueDate: string
}

export interface BorrowRecord {
  id: number
  borrowCode: string
  archiveCode: string
  archiveTitle: string
  borrower: string
  borrowType: string
  approvalStatus: string
  borrowStatus: string
  expectedReturnDate: string
  borrowedAt: string
}

export interface InventoryTask {
  id: number
  taskCode: string
  warehouseCode: string
  inventoryScope: string
  taskStatus: string
  abnormalCount: number
  owner: string
  dueDate: string
}

export interface DisposalRecord {
  id: number
  disposalCode: string
  archiveCode: string
  archiveTitle: string
  retentionPeriod: string
  appraisalConclusion: string
  approvalStatus: string
  disposalStatus: string
  createdAt: string
}

export interface Warehouse {
  id: number
  warehouseCode: string
  warehouseName: string
  warehouseType: string
  managerName: string
  contactPhone: string
  address: string
  areaSize?: number
  photoUrl?: string
  status: string
  description: string
  updatedAt?: string
}

export interface WarehouseSummary {
  warehouse: Warehouse
  areaCount: number
  rackCount: number
  locationCount: number
  freeLocationCount: number
  occupiedLocationCount: number
  lastUpdatedAt?: string
}

export interface WarehouseArea {
  id: number
  warehouseCode: string
  areaCode: string
  areaName: string
  sortOrder: number
  startX: number
  startY: number
  width: number
  height: number
  status: string
  updatedAt?: string
}

export interface WarehouseRack {
  id: number
  warehouseCode: string
  areaCode: string
  rackCode: string
  rackName: string
  layerCount: number
  slotCount: number
  startX: number
  startY: number
  status: string
  updatedAt?: string
}

export interface WarehouseLocation {
  id: number
  warehouseCode: string
  warehouseName: string
  areaCode: string
  shelfCode: string
  layerCode: string
  locationCode: string
  locationName: string
  status: string
  capacity: number
  occupiedCount: number
  x: number
  y: number
  width: number
  height: number
}

export interface WarehouseVisualNode {
  id: number
  warehouseCode: string
  locationCode: string
  locationName: string
  status: string
  capacity: number
  occupiedCount: number
  x: number
  y: number
  width: number
  height: number
  color: string
}

export interface WarehouseVisualization {
  warehouseCode: string
  warehouseName: string
  totalLocations: number
  occupiedLocations: number
  warningLocations: number
  nodes: WarehouseVisualNode[]
}

export interface WarehouseManagementResponse {
  warehouse: Warehouse
  areas: WarehouseArea[]
  racks: WarehouseRack[]
  locations: WarehouseLocation[]
}

export interface WorkflowDefinition {
  code: string
  name: string
  scenario: string
  status: string
}

export interface RuleDefinition {
  code: string
  name: string
  category: string
  expression: string
}

export interface AiCapability {
  code: string
  name: string
  status: string
  description: string
}

export interface SecurityPolicy {
  policyCode: string
  name: string
  category: string
  status: string
}
export interface DocumentTypeTreeNode {
  id: number
  typeCode: string
  typeName: string
  description?: string
  enabledFlag: string
  parentCode?: string
  levelNum: number
  ancestorPath?: string
  sortOrder: number
  deleteFlag: string
  createdBy: number
  creationDate: string
  lastUpdatedBy: number
  lastUpdateDate: string
  children: DocumentTypeTreeNode[]
}

export interface PermissionPoint {
  code: string
  name: string
  action: string
  description: string
}

export interface DataDimension {
  code: string
  name: string
  description: string
}

export interface DocumentTypePermissionPreview {
  moduleCode: string
  moduleName: string
  permissionPoints: PermissionPoint[]
  dataDimensions: DataDimension[]
}

export interface AuditRecord {
  id: number
  moduleCode: string
  moduleName: string
  businessType: string
  businessKey: string
  operationType: string
  operationSummary: string
  beforeSnapshot?: string
  afterSnapshot?: string
  operatorId: number
  operatorName: string
  operationTime: string
}

export interface CompanyProjectLine {
  id: number
  lineNo: number
  orgCategory: string
  organizationCode: string
  organizationName: string
  validFlag: string
  deleteFlag: string
  createdBy: number
  creationDate: string
  lastUpdatedBy: number
  lastUpdateDate: string
}

export interface CompanyProjectSummary {
  id: number
  companyProjectCode: string
  companyProjectName: string
  countryCode: string
  managementArea: string
  enabledFlag: string
  organizationCount: number
  lastUpdatedBy: number
  lastUpdateDate: string
}

export interface CompanyProjectDetail {
  id: number
  companyProjectCode: string
  companyProjectName: string
  countryCode: string
  managementArea: string
  enabledFlag: string
  deleteFlag: string
  createdBy: number
  creationDate: string
  lastUpdatedBy: number
  lastUpdateDate: string
  lines: CompanyProjectLine[]
}

export interface CompanyProjectOrgCategory {
  categoryCode: string
  categoryName: string
}

export interface CountryOption {
  countryCode: string
  countryName: string
}

export interface DocumentOrganizationCityOption {
  countryCode: string
  cityCode: string
  cityName: string
}

export interface DocumentOrganizationSummary {
  id: number
  documentOrganizationCode: string
  documentOrganizationName: string
  description: string
  countryCode: string
  cityCode?: string
  enabledFlag: string
  lastUpdatedBy: number
  lastUpdateDate: string
}

export interface DocumentOrganizationDetail {
  id: number
  documentOrganizationCode: string
  documentOrganizationName: string
  description: string
  countryCode: string
  cityCode?: string
  enabledFlag: string
  deleteFlag: string
  createdBy: number
  creationDate: string
  lastUpdatedBy: number
  lastUpdateDate: string
}

export interface CountryDictionaryItem {
  id: number
  countryCode: string
  countryName: string
  sortOrder: number
  enabledFlag: string
}

export interface OrgCategoryDictionaryItem {
  id: number
  categoryCode: string
  categoryName: string
  sortOrder: number
  enabledFlag: string
}


export interface ArchiveFlowRuleOption {
  code: string
  name: string
}

export interface SecurityLevelOption {
  securityLevelCode: string
  securityLevelName: string
}

export interface ArchiveFlowRuleSummary {
  id: number
  companyProjectCode: string
  companyProjectName: string
  documentTypeCode: string
  documentTypeName: string
  customRule?: string
  archiveDestination?: string
  archiveDestinationName?: string
  documentOrganizationCode: string
  documentOrganizationName: string
  retentionPeriodYears: number
  securityLevelCode: string
  securityLevelName: string
  externalDisplayFlag: string
  enabledFlag: string
  lastUpdatedBy: number
  lastUpdateDate: string
}

export interface ArchiveFlowRuleDetail {
  id: number
  companyProjectCode: string
  documentTypeCode: string
  customRule?: string
  archiveDestination?: string
  documentOrganizationCode: string
  retentionPeriodYears: number
  securityLevelCode: string
  externalDisplayFlag: string
  enabledFlag: string
  deleteFlag: string
  createdBy: number
  creationDate: string
  lastUpdatedBy: number
  lastUpdateDate: string
}

export interface LabelValueOption {
  code: string
  name: string
}

export interface DocumentTypeExtField {
  fieldId: number
  fieldCode: string
  documentTypeCode: string
  fieldName: string
  fieldType: 'TEXT' | 'DICT'
  dictCategoryCode?: string
  requiredFlag: 'Y' | 'N'
  enabledFlag: 'Y' | 'N'
  formSortOrder: number
  queryEnabledFlag: 'Y' | 'N'
  querySortOrder: number
  sourceLevel?: number
  sourceDocumentTypeCode?: string
  lastUpdateDate?: string
}

export interface ArchiveCreateOptions {
  companyProjects: LabelValueOption[]
  documentTypes: LabelValueOption[]
  archiveDestinations: LabelValueOption[]
  documentOrganizations: LabelValueOption[]
  securityLevels: LabelValueOption[]
  carrierTypes: LabelValueOption[]
  attachmentTypes: LabelValueOption[]
  archiveTypes: LabelValueOption[]
  aiModels: LabelValueOption[]
}

export interface ArchiveDefaultResolve {
  securityLevelCode?: string
  archiveDestination?: string
  documentOrganizationCode?: string
  retentionPeriodYears?: number
  countryCode?: string
}

export interface ArchiveAttachmentItem {
  attachmentId: number
  attachmentRole: 'ELECTRONIC' | 'PAPER_SCAN'
  attachmentTypeCode?: string
  attachmentSeq: number
  versionNo: number
  fileName: string
  mimeType?: string
  fileSize?: number
  remark?: string
  aiSummary?: string
  parseStatus: string
  vectorStatus: string
  creationDate?: string
}

export interface ArchiveAiParseResult {
  suggestedDocumentTypeCode?: string
  suggestedCarrierTypeCode?: string
  documentName?: string
  businessCode?: string
  sourceSystem?: string
  aiSummary?: string
  extractedTextPreview?: string
  confidence?: number
  extendedValues?: Record<string, string>
}

export interface ArchiveCreateSession {
  sessionId: number
  sessionCode: string
  createMode: 'AUTO' | 'MANUAL'
  sessionStatus: string
  documentTypeCodeGuess?: string
  carrierTypeCodeGuess?: string
  parseStatus: string
  aiSummarySnapshot?: string
  expireTime?: string
  attachments: ArchiveAttachmentItem[]
  aiParseResult?: ArchiveAiParseResult
}

export interface ArchiveRecordSummary {
  archiveId: number
  archiveCode: string
  archiveFilingCode: string
  documentTypeCode: string
  documentTypeName?: string
  companyProjectCode: string
  companyProjectName?: string
  documentName: string
  businessCode?: string
  dutyPerson: string
  dutyDepartment: string
  documentDate: string
  securityLevelCode: string
  sourceSystem?: string
  archiveDestination?: string
  originPlace?: string
  carrierTypeCode: string
  aiArchiveSummary?: string
  documentOrganizationCode: string
  retentionPeriodYears: number
  archiveTypeCode: string
  archiveStatus: string
  parseStatus: string
  vectorStatus: string
  lastUpdateDate?: string
  attachmentCount: number
  extValues: Record<string, string>
  attachments: ArchiveAttachmentItem[]
}

export interface ArchiveQueryResult {
  records: ArchiveRecordSummary[]
  queryFields: DocumentTypeExtField[]
}

export interface ArchiveAskResult {
  answer: string
  references: ArchiveRecordSummary[]
}

export interface ArchiveAiModelSummary {
  modelCode: string
  modelName: string
  modelType: 'CHAT' | 'EMBEDDING' | 'RERANK'
  modelIdentifier: string
  embeddingDimension: number
  timeoutSeconds: number
  topK: number
  scoreThreshold?: number
  enabledFlag: 'Y' | 'N'
  remark?: string
}

export interface DictionaryCategory {
  id: number
  categoryCode: string
  categoryName: string
  description?: string
  enabledFlag: 'Y' | 'N'
  itemCount: number
}

export interface DictionaryItem {
  id: number
  categoryCode: string
  itemCode: string
  itemName: string
  itemValue?: string
  sortOrder: number
  enabledFlag: 'Y' | 'N'
}

export interface AiModelConfig {
  modelConfigId?: number
  modelCode: string
  modelName: string
  modelType: 'CHAT' | 'EMBEDDING' | 'RERANK'
  apiUrl: string
  apiKey: string
  modelIdentifier: string
  promptTemplate?: string
  embeddingDimension: number
  timeoutSeconds: number
  topK: number
  scoreThreshold?: number
  officialResultCount: number
  officialScoreThreshold: number
  relatedResultCount: number
  relatedScoreThreshold: number
  enabledFlag: 'Y' | 'N'
  remark?: string
}

export interface AiModelConnectionTestResult {
  status: string
  message: string
}
