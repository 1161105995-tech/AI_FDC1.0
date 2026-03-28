# 公司列表
$companies = @(
    "阿里巴巴集团", "腾讯控股", "百度集团", "京东集团", "华为技术有限公司",
    "小米集团", "字节跳动", "网易集团", "拼多多", "美团",
    "中国工商银行", "中国建设银行", "中国农业银行", "中国银行", "中国移动",
    "中国电信", "中国联通", "中国石油天然气集团", "中国石油化工集团", "中国建筑集团",
    "中国中铁", "中国铁建", "中国中车", "中国航天科技集团", "中国航天科工集团",
    "苹果公司", "微软公司", "谷歌公司", "亚马逊公司", "Meta",
    "特斯拉公司", "英特尔公司", "IBM", "甲骨文公司", "思科系统",
    "惠普公司", "戴尔科技", "通用电气", "福特汽车", "通用汽车",
    "波音公司", "辉瑞公司", "强生公司", "摩根大通", "高盛集团",
    "花旗集团", "瑞银集团", "德意志银行", "西门子", "宝马集团"
)

# 文档类型
$documentTypes = @(
    "销售合同", "采购合同", "付款凭证", "应付发票", "应收发票", "收款单"
)

# 输出目录
$outputDir = "D:\AI project\AI-search\测试文件目录"

# 确保输出目录存在
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force
}

# 生成随机金额
function Get-RandomAmount {
    return [math]::Round((Get-Random -Minimum 1000 -Maximum 1000000), 2)
}

# 生成随机日期
function Get-RandomDate {
    $startDate = Get-Date "2023-01-01"
    $endDate = Get-Date "2024-12-31"
    $days = ($endDate - $startDate).Days
    return $startDate.AddDays((Get-Random -Minimum 0 -Maximum $days))
}

# 生成随机合同编号
function Get-ContractId($docType) {
    $prefixMap = @{
        "销售合同" = "SC"
        "采购合同" = "PC"
        "付款凭证" = "PV"
        "应付发票" = "PI"
        "应收发票" = "RI"
        "收款单" = "RS"
    }
    $prefix = $prefixMap[$docType]
    $date = Get-Date -Format "yyyyMMdd"
    $random = Get-Random -Minimum 1000 -Maximum 9999
    return "$prefix-$date-$random"
}

# 生成文件内容
function New-DocumentContent($docType, $seller, $buyer, $amount, $date, $docId) {
    $content = "$docType`n`n"
    $content += "文档编号: $docId`n"
    $content += "日期: $($date.ToString('yyyy-MM-dd'))`n`n"

    switch ($docType) {
        "销售合同" {
            $content += "合同双方`n"
            $content += "卖方: $seller`n"
            $content += "买方: $buyer`n`n"
            $content += "合同内容`n"
            $content += "$seller与$buyer就相关业务达成如下协议:`n"
            $content += "1. 交易金额: ￥$amount`n"
            $content += "2. 交易日期: $($date.ToString('yyyy-MM-dd'))`n"
            $content += "3. 支付方式: 银行转账`n"
            $content += "4. 交付方式: 电子交付`n`n"
            $content += "双方签字`n"
            $content += "$seller (盖章)`n"
            $content += "$buyer (盖章)`n"
        }
        "采购合同" {
            $content += "合同双方`n"
            $content += "卖方: $seller`n"
            $content += "买方: $buyer`n`n"
            $content += "合同内容`n"
            $content += "$seller与$buyer就相关业务达成如下协议:`n"
            $content += "1. 交易金额: ￥$amount`n"
            $content += "2. 交易日期: $($date.ToString('yyyy-MM-dd'))`n"
            $content += "3. 支付方式: 银行转账`n"
            $content += "4. 交付方式: 电子交付`n`n"
            $content += "双方签字`n"
            $content += "$seller (盖章)`n"
            $content += "$buyer (盖章)`n"
        }
        "付款凭证" {
            $content += "付款信息`n"
            $content += "付款方: $seller`n"
            $content += "收款方: $buyer`n"
            $content += "付款金额: ￥$amount`n"
            $content += "付款日期: $($date.ToString('yyyy-MM-dd'))`n"
            $content += "付款方式: 银行转账`n"
            $content += "转账凭证号: $(Get-Random -Minimum 1000000000 -Maximum 9999999999)`n"
        }
        "应付发票" {
            $content += "发票信息`n"
            $content += "开票方: $seller`n"
            $content += "收票方: $buyer`n"
            $content += "发票金额: ￥$amount`n"
            $content += "开票日期: $($date.ToString('yyyy-MM-dd'))`n"
            $content += "发票号码: $(Get-Random -Minimum 10000000 -Maximum 99999999)`n"
            $content += "税率: 13%`n"
        }
        "应收发票" {
            $content += "发票信息`n"
            $content += "开票方: $seller`n"
            $content += "收票方: $buyer`n"
            $content += "发票金额: ￥$amount`n"
            $content += "开票日期: $($date.ToString('yyyy-MM-dd'))`n"
            $content += "发票号码: $(Get-Random -Minimum 10000000 -Maximum 99999999)`n"
            $content += "税率: 13%`n"
        }
        "收款单" {
            $content += "收款信息`n"
            $content += "收款方: $seller`n"
            $content += "付款方: $buyer`n"
            $content += "收款金额: ￥$amount`n"
            $content += "收款日期: $($date.ToString('yyyy-MM-dd'))`n"
            $content += "收款方式: 银行转账`n"
            $content += "收款凭证号: $(Get-Random -Minimum 1000000000 -Maximum 9999999999)`n"
        }
    }

    return $content
}

# 主函数
function main() {
    $totalFiles = 1000
    Write-Host "开始生成$totalFiles个商业交易文档..."
    
    for ($i = 1; $i -le $totalFiles; $i++) {
        # 随机选择文档类型
        $docType = $documentTypes | Get-Random
        
        # 随机选择两家不同的公司
        $selectedCompanies = $companies | Get-Random -Count 2
        $seller = $selectedCompanies[0]
        $buyer = $selectedCompanies[1]
        
        # 生成随机数据
        $amount = Get-RandomAmount
        $date = Get-RandomDate
        $docId = Get-ContractId $docType
        
        # 生成文件名
        $fileName = "$docType`_$seller`_$buyer`_$i"
        
        # 生成内容
        $content = New-DocumentContent $docType $seller $buyer $amount $date $docId
        
        # 生成DOC文件
        $docFilePath = Join-Path $outputDir "$fileName.doc"
        Write-Host "生成DOC文件: $docFilePath"
        $content | Out-File -FilePath $docFilePath -Encoding UTF8
        
        # 生成PDF文件
        $pdfFilePath = Join-Path $outputDir "$fileName.pdf"
        Write-Host "生成PDF文件: $pdfFilePath"
        $content | Out-File -FilePath $pdfFilePath -Encoding UTF8
        
        if ($i % 100 -eq 0) {
            Write-Host "已生成$i个文件"
        }
    }
    
    Write-Host "生成完成！共生成$totalFiles个DOC文件和$totalFiles个PDF文件。"
}

# 运行主函数
main
