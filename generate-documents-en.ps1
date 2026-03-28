# Company list
$companies = @(
    "Alibaba Group", "Tencent Holdings", "Baidu Group", "JD.com", "Huawei Technologies Co., Ltd.",
    "Xiaomi Group", "ByteDance", "NetEase Group", "Pinduoduo", "Meituan",
    "Industrial and Commercial Bank of China", "China Construction Bank", "Agricultural Bank of China", "Bank of China", "China Mobile",
    "China Telecom", "China Unicom", "China National Petroleum Corporation", "China Petrochemical Corporation", "China State Construction Engineering",
    "China Railway Group", "China Railway Construction Corporation", "CRRC Corporation", "China Aerospace Science and Technology Corporation", "China Aerospace Science and Industry Corporation",
    "Apple Inc.", "Microsoft Corporation", "Alphabet Inc.", "Amazon.com Inc.", "Meta Platforms Inc.",
    "Tesla, Inc.", "Intel Corporation", "IBM Corporation", "Oracle Corporation", "Cisco Systems Inc.",
    "HP Inc.", "Dell Technologies", "General Electric Company", "Ford Motor Company", "General Motors Company",
    "The Boeing Company", "Pfizer Inc.", "Johnson & Johnson", "JPMorgan Chase & Co.", "Goldman Sachs Group Inc.",
    "Citigroup Inc.", "UBS Group AG", "Deutsche Bank AG", "Siemens AG", "BMW Group"
)

# Document types
$documentTypes = @(
    "Sales Contract", "Purchase Contract", "Payment Voucher", "Payable Invoice", "Receivable Invoice", "Receipt Slip"
)

# Products list
$products = @(
    "Demura Equipment", "Dry Cleaning Machine", "Panel Protective Film Peeling Machine", "Semiconductor Display Panel",
    "LCD Display", "OLED Screen", "Chip", "Electronic Components", "Server", "Network Equipment"
)

# Output directory
$outputDir = "D:\AI project\AI-search\测试文件目录"

# Ensure output directory exists
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force
}

# Generate random amount
function Get-RandomAmount {
    return [math]::Round((Get-Random -Minimum 100000 -Maximum 10000000), 2)
}

# Generate random date
function Get-RandomDate {
    $startDate = Get-Date "2026-01-01"
    $endDate = Get-Date "2026-12-31"
    $days = ($endDate - $startDate).Days
    return $startDate.AddDays((Get-Random -Minimum 0 -Maximum $days))
}

# Generate random contract ID
function Get-ContractId($docType, $company) {
    $prefixMap = @{
        "Sales Contract" = "SC"
        "Purchase Contract" = "PC"
        "Payment Voucher" = "PV"
        "Payable Invoice" = "PI"
        "Receivable Invoice" = "RI"
        "Receipt Slip" = "RS"
    }
    $prefix = $prefixMap[$docType]
    # Extract company abbreviation
    $companyAbbr = $company -replace '[^A-Z0-9]', ''
    if (-not $companyAbbr) {
        $companyAbbr = $company.Substring(0, 2)
    }
    $dateStr = Get-Date -Format "yyyyMMdd"
    $randomNum = Get-Random -Minimum 100 -Maximum 999
    return "$prefix-$companyAbbr-$dateStr-$randomNum"
}

# Generate random phone number
function Get-RandomPhone {
    return "1" + (Get-Random -Minimum 3 -Maximum 10) + ((1..9) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 }) -join ''
}

# Main function
function main() {
    $totalFiles = 1000
    Write-Host "Starting to generate $totalFiles business transaction documents..."
    
    for ($i = 1; $i -le $totalFiles; $i++) {
        # Randomly select document type
        $docType = $documentTypes | Get-Random
        
        # Randomly select two different companies
        $selectedCompanies = $companies | Get-Random -Count 2
        $seller = $selectedCompanies[0]
        $buyer = $selectedCompanies[1]
        
        # Generate random data
        $amount = Get-RandomAmount
        $date = Get-RandomDate
        $docId = Get-ContractId $docType $buyer
        
        # Generate file name
        if ($docType -in @("Sales Contract", "Purchase Contract")) {
            $companyName = if ($docType -eq "Purchase Contract") { $buyer } else { $seller }
            $fileName = "$docType-$companyName-$($date.ToString('yyyyMMdd'))"
        } else {
            $fileName = "$docType-$($date.ToString('yyyyMMdd'))-$i"
        }
        
        # Generate content
        $content = "$docType`n"
        $content += "Document ID: $docId`n"
        $content += "Date: $($date.ToString('yyyy-MM-dd'))`n"
        $content += "Seller: $seller`n"
        $content += "Buyer: $buyer`n"
        $content += "Amount: ¥$amount`n"
        $content += "Product: $($products | Get-Random)`n"
        
        # Generate DOC file
        $docFilePath = Join-Path $outputDir "$fileName.doc"
        Write-Host "Generating DOC file: $docFilePath"
        $content | Out-File -FilePath $docFilePath -Encoding UTF8
        
        # Generate PDF file
        $pdfFilePath = Join-Path $outputDir "$fileName.pdf"
        Write-Host "Generating PDF file: $pdfFilePath"
        $content | Out-File -FilePath $pdfFilePath -Encoding UTF8
        
        if ($i % 100 -eq 0) {
            Write-Host "Generated $i files"
        }
    }
    
    Write-Host "Generation completed! Generated $totalFiles DOC files and $totalFiles PDF files."
}

# Run main function
main
