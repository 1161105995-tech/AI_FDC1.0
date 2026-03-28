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

# 产品列表
$products = @(
    "Demura设备", "干式清洗机", "Panel保护膜撕膜机", "半导体显示面板",
    "LCD显示器", "OLED屏幕", "芯片", "电子元器件", "服务器", "网络设备"
)

# 输出目录
$outputDir = "D:\AI project\AI-search\测试文件目录"

# 确保输出目录存在
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force
}

# 生成随机金额
function Get-RandomAmount {
    return [math]::Round((Get-Random -Minimum 100000 -Maximum 10000000), 2)
}

# 生成随机日期
function Get-RandomDate {
    $startDate = Get-Date "2026-01-01"
    $endDate = Get-Date "2026-12-31"
    $days = ($endDate - $startDate).Days
    return $startDate.AddDays((Get-Random -Minimum 0 -Maximum $days))
}

# 生成随机合同编号
function Get-ContractId($docType, $company) {
    $prefixMap = @{
        "销售合同" = "SC"
        "采购合同" = "PC"
        "付款凭证" = "PV"
        "应付发票" = "PI"
        "应收发票" = "RI"
        "收款单" = "RS"
    }
    $prefix = $prefixMap[$docType]
    # 提取公司简称
    $companyAbbr = $company -replace '[^A-Z0-9]', ''
    if (-not $companyAbbr) {
        $companyAbbr = $company.Substring(0, 2)
    }
    $dateStr = Get-Date -Format "yyyyMMdd"
    $randomNum = Get-Random -Minimum 100 -Maximum 999
    return "$prefix-$companyAbbr-$dateStr-$randomNum"
}

# 生成随机联系电话
function Get-RandomPhone {
    return "1" + (Get-Random -Minimum 3 -Maximum 10) + ((1..9) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 }) -join ''
}

# 金额转中文大写
function Convert-AmountToChinese($amount) {
    $digits = @('零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖')
    $units = @('', '拾', '佰', '仟')
    $bigUnits = @('', '万', '亿')
    
    $amountInt = [int]$amount
    $amountStr = $amountInt.ToString()
    $decimalStr = [math]::Round(($amount - $amountInt) * 100, 0).ToString("00")
    
    $result = ""
    $unitIndex = 0
    $bigUnitIndex = 0
    
    for ($i = $amountStr.Length - 1; $i -ge 0; $i--) {
        $digit = [int]$amountStr[$i]
        if ($digit -ne 0) {
            $result = $digits[$digit] + $units[$unitIndex % 4] + $result
        } elseif ($i -gt 0 -and [int]$amountStr[$i-1] -ne 0) {
            $result = $digits[$digit] + $result
        }
        
        $unitIndex++
        if ($unitIndex % 4 -eq 0 -and $i -gt 0) {
            $result = $bigUnits[$bigUnitIndex] + $result
            $bigUnitIndex++
        }
    }
    
    $result += "元"
    if ($decimalStr -eq "00") {
        $result += "整"
    } else {
        if ($decimalStr[0] -ne "0") {
            $result += $digits[[int]$decimalStr[0]] + "角"
        }
        if ($decimalStr[1] -ne "0") {
            $result += $digits[[int]$decimalStr[1]] + "分"
        }
    }
    
    return $result
}

# 生成采购合同内容
function Get-PurchaseContract($seller, $buyer, $amount, $date, $docId) {
    $product = $products | Get-Random
    $quantity = Get-Random -Minimum 1 -Maximum 100
    $unitPrice = [math]::Round($amount / $quantity, 2)
    
    $content = "采购合同（$buyer）`n"
    $content += "合同编号：$docId`n"
    $content += "甲方（采购方）：$buyer`n"
    $content += "统一社会信用代码：91440300" + (-join ((1..10) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "G`n"
    $content += "法定代表人：" + @("张总", "李总", "王总", "赵总", "刘总") | Get-Random + "`n"
    $content += "地址：广东省深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号`n"
    $content += "联系人：" + @("小李", "小王", "小张", "小刘", "小陈") | Get-Random + " 联系电话：" + (Get-RandomPhone) + "`n"
    $content += "乙方（供应方）：$seller`n"
    $content += "统一社会信用代码：91" + (-join ((1..16) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "法定代表人：" + @("张三", "李四", "王五", "赵六", "钱七") | Get-Random + "`n"
    $content += "地址：广东省深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号`n"
    $content += "联系人：" + @("张三", "李四", "王五", "赵六", "钱七") | Get-Random + " 联系电话：" + (Get-RandomPhone) + "`n"
    $content += "依据《中华人民共和国民法典》及相关法律法规，甲乙双方本着平等自愿、公平诚信、互利共赢的原则，就甲方向乙方采购半导体显示相关设备及配件事宜，经友好协商，订立本合同，以资共同信守。`n"
    $content += "一、采购标的：甲方向乙方采购$product（产品名称、规格型号、数量、单价详见附件），合同总价款为人民币$amount元（大写：" + (Convert-AmountToChinese $amount) + "），该价款为含税总价，包含货物成本、包装、运输、装卸、税费及售后服务等全部费用，甲方无需额外支付其他费用。`n"
    $content += "二、质量标准：乙方提供的货物需符合国家半导体显示行业相关标准、产品说明书要求及甲方约定标准，确保为全新、未使用过的合格产品，无质量瑕疵、无破损，且符合相关采购质量规范，提供完整的质量合格证明及专业检测报告。`n"
    $deliveryDate = $date.AddDays((Get-Random -Minimum 1 -Maximum 31))
    $content += "三、交付与验收：乙方应于$($deliveryDate.Year)年$($deliveryDate.Month)月$($deliveryDate.Day)日前，将货物送至甲方指定地点（深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号$buyer指定仓库/生产车间），负责货物的装卸、摆放及初步调试。甲方在收到货物后7个工作日内完成验收，验收合格后签署验收单；若发现货物质量、数量、规格不符合约定，甲方有权要求乙方在3个工作日内更换或补足，相关费用由乙方承担。`n"
    $content += "四、付款方式：合同签订后5个工作日内，甲方向乙方支付合同总价款的20%作为预付款；货物验收合格并完成初步调试后10个工作日内，甲方支付至合同总价款的95%；剩余5%作为质量保证金，自验收合格之日起12个月内无质量问题，甲方无息一次性支付。乙方需在付款前提供合法有效的增值税专用发票，否则甲方有权顺延付款。`n"
    $content += "五、售后服务：乙方承诺货物质保期为12个月，质保期内出现质量问题，乙方应在收到甲方通知后24小时内响应，48小时内派员到场维修或更换，维修费用由乙方承担；质保期结束后，乙方需提供长期优惠维修服务及配件供应。`n"
    $content += "六、违约责任：乙方逾期交付货物，每逾期一日按合同总价款的0.5‰支付违约金，逾期超过15日，甲方有权解除合同并要求乙方赔偿损失；乙方提供的货物质量不合格，应承担更换、退货及甲方因此遭受的生产停工、设备闲置等全部损失；甲方逾期付款，每逾期一日按未付款项的0.5‰支付违约金。`n"
    $content += "七、争议解决：本合同履行中发生的争议，双方协商解决；协商不成的，提交甲方所在地有管辖权的人民法院诉讼解决。`n"
    $content += "八、其他：本合同附件为本合同不可分割的组成部分，与本合同具有同等法律效力；本合同一式肆份，甲乙双方各执贰份，自双方签字盖章之日起生效，履行完毕后自动终止。`n"
    $content += "甲方（盖章）：$buyer`n"
    $content += "法定代表人/授权代表（签字）：________________________`n"
    $content += "签订日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    $content += "乙方（盖章）：$seller`n"
    $content += "法定代表人/授权代表（签字）：________________________`n"
    $content += "签订日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    return $content
}

# 生成销售合同内容
function Get-SalesContract($seller, $buyer, $amount, $date, $docId) {
    $product = $products | Get-Random
    $quantity = Get-Random -Minimum 1 -Maximum 100
    $unitPrice = [math]::Round($amount / $quantity, 2)
    
    $content = "销售合同（$seller）`n"
    $content += "合同编号：$docId`n"
    $content += "甲方（卖方）：$seller`n"
    $content += "统一社会信用代码：91440300" + (-join ((1..10) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "G`n"
    $content += "法定代表人：" + @("张总", "李总", "王总", "赵总", "刘总") | Get-Random + "`n"
    $content += "地址：广东省深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号`n"
    $content += "联系人：" + @("小李", "小王", "小张", "小刘", "小陈") | Get-Random + " 联系电话：" + (Get-RandomPhone) + "`n"
    $content += "乙方（买方）：$buyer`n"
    $content += "统一社会信用代码：91" + (-join ((1..16) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "法定代表人：" + @("张三", "李四", "王五", "赵六", "钱七") | Get-Random + "`n"
    $content += "地址：广东省深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号`n"
    $content += "联系人：" + @("张三", "李四", "王五", "赵六", "钱七") | Get-Random + " 联系电话：" + (Get-RandomPhone) + "`n"
    $content += "依据《中华人民共和国民法典》及相关法律法规，甲乙双方本着平等自愿、公平诚信、互利共赢的原则，就甲方向乙方销售半导体显示相关设备及配件事宜，经友好协商，订立本合同，以资共同信守。`n"
    $content += "一、销售标的：甲方向乙方销售$product（产品名称、规格型号、数量、单价详见附件），合同总价款为人民币$amount元（大写：" + (Convert-AmountToChinese $amount) + "），该价款为含税总价，包含货物成本、包装、运输、装卸、税费及售后服务等全部费用，乙方无需额外支付其他费用。`n"
    $content += "二、质量标准：甲方提供的货物需符合国家半导体显示行业相关标准、产品说明书要求及乙方约定标准，确保为全新、未使用过的合格产品，无质量瑕疵、无破损，且符合相关采购质量规范，提供完整的质量合格证明及专业检测报告。`n"
    $deliveryDate = $date.AddDays((Get-Random -Minimum 1 -Maximum 31))
    $content += "三、交付与验收：甲方应于$($deliveryDate.Year)年$($deliveryDate.Month)月$($deliveryDate.Day)日前，将货物送至乙方指定地点（深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号$buyer指定仓库/生产车间），负责货物的装卸、摆放及初步调试。乙方在收到货物后7个工作日内完成验收，验收合格后签署验收单；若发现货物质量、数量、规格不符合约定，乙方有权要求甲方在3个工作日内更换或补足，相关费用由甲方承担。`n"
    $content += "四、付款方式：合同签订后5个工作日内，乙方向甲方支付合同总价款的20%作为预付款；货物验收合格并完成初步调试后10个工作日内，乙方支付至合同总价款的95%；剩余5%作为质量保证金，自验收合格之日起12个月内无质量问题，乙方无息一次性支付。甲方需在付款前提供合法有效的增值税专用发票，否则乙方有权顺延付款。`n"
    $content += "五、售后服务：甲方承诺货物质保期为12个月，质保期内出现质量问题，甲方应在收到乙方通知后24小时内响应，48小时内派员到场维修或更换，维修费用由甲方承担；质保期结束后，甲方需提供长期优惠维修服务及配件供应。`n"
    $content += "六、违约责任：甲方逾期交付货物，每逾期一日按合同总价款的0.5‰支付违约金，逾期超过15日，乙方有权解除合同并要求甲方赔偿损失；甲方提供的货物质量不合格，应承担更换、退货及乙方因此遭受的生产停工、设备闲置等全部损失；乙方逾期付款，每逾期一日按未付款项的0.5‰支付违约金。`n"
    $content += "七、争议解决：本合同履行中发生的争议，双方协商解决；协商不成的，提交乙方所在地有管辖权的人民法院诉讼解决。`n"
    $content += "八、其他：本合同附件为本合同不可分割的组成部分，与本合同具有同等法律效力；本合同一式肆份，甲乙双方各执贰份，自双方签字盖章之日起生效，履行完毕后自动终止。`n"
    $content += "甲方（盖章）：$seller`n"
    $content += "法定代表人/授权代表（签字）：________________________`n"
    $content += "签订日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    $content += "乙方（盖章）：$buyer`n"
    $content += "法定代表人/授权代表（签字）：________________________`n"
    $content += "签订日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    return $content
}

# 生成付款凭证内容
function Get-PaymentVoucher($payer, $payee, $amount, $date, $docId) {
    $content = "付款凭证`n"
    $content += "凭证编号：$docId`n"
    $content += "付款日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    $content += "付款方：$payer`n"
    $content += "统一社会信用代码：91440300" + (-join ((1..10) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "G`n"
    $content += "开户银行：" + @("中国工商银行", "中国建设银行", "中国农业银行", "中国银行") | Get-Random + "`n"
    $content += "银行账号：" + (-join ((1..19) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "收款方：$payee`n"
    $content += "统一社会信用代码：91" + (-join ((1..16) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "开户银行：" + @("中国工商银行", "中国建设银行", "中国农业银行", "中国银行") | Get-Random + "`n"
    $content += "银行账号：" + (-join ((1..19) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "付款金额：人民币$amount元（大写：" + (Convert-AmountToChinese $amount) + "）`n"
    $content += "付款用途：" + @("采购货款", "服务费用", "设备款", "材料款") | Get-Random + "（合同编号：$($docId -replace 'PV', 'PC')）`n"
    $content += "付款方式：银行转账`n"
    $content += "转账凭证号：" + (-join ((1..20) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "备注：本付款凭证一式两份，付款方和收款方各执一份，作为财务记账凭证。`n"
    $content += "付款方（盖章）：$payer`n"
    $content += "财务负责人（签字）：________________________`n"
    $content += "日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    return $content
}

# 生成应付发票内容
function Get-PayableInvoice($seller, $buyer, $amount, $date, $docId) {
    $taxAmount = [math]::Round($amount / 1.13 * 0.13, 2)
    $netAmount = [math]::Round($amount - $taxAmount, 2)
    
    $content = "增值税专用发票`n"
    $content += "发票号码：$docId`n"
    $content += "开票日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    $content += "购买方：$buyer`n"
    $content += "统一社会信用代码：91440300" + (-join ((1..10) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "G`n"
    $content += "地址：广东省深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号`n"
    $content += "电话：" + (Get-RandomPhone) + "`n"
    $content += "开户银行：" + @("中国工商银行", "中国建设银行", "中国农业银行", "中国银行") | Get-Random + "`n"
    $content += "银行账号：" + (-join ((1..19) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "销售方：$seller`n"
    $content += "统一社会信用代码：91" + (-join ((1..16) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "地址：广东省深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号`n"
    $content += "电话：" + (Get-RandomPhone) + "`n"
    $content += "开户银行：" + @("中国工商银行", "中国建设银行", "中国农业银行", "中国银行") | Get-Random + "`n"
    $content += "银行账号：" + (-join ((1..19) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "货物或应税劳务、服务名称`t规格型号`t单位`t数量`t单价`t金额`t税率`t税额`n"
    $product = $products | Get-Random
    $quantity = Get-Random -Minimum 1 -Maximum 11
    $unitPrice = [math]::Round($netAmount / $quantity, 2)
    $content += "$product`t" + (-join ((1..4) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`t台`t$quantity`t$unitPrice`t$netAmount`t13%`t$taxAmount`n"
    $content += "价税合计（大写）：" + (Convert-AmountToChinese $amount) + "`n"
    $content += "价税合计（小写）：￥$amount`n"
    $content += "备注：本发票为应付账款凭证，请注意及时入账。`n"
    $content += "销售方（盖章）：$seller`n"
    $content += "开票人：" + @("小李", "小王", "小张", "小刘", "小陈") | Get-Random + "`n"
    $content += "复核人：" + @("张总", "李总", "王总", "赵总", "刘总") | Get-Random + "`n"
    return $content
}

# 生成应收发票内容
function Get-ReceivableInvoice($seller, $buyer, $amount, $date, $docId) {
    $taxAmount = [math]::Round($amount / 1.13 * 0.13, 2)
    $netAmount = [math]::Round($amount - $taxAmount, 2)
    
    $content = "增值税专用发票`n"
    $content += "发票号码：$docId`n"
    $content += "开票日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    $content += "购买方：$buyer`n"
    $content += "统一社会信用代码：91440300" + (-join ((1..10) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "G`n"
    $content += "地址：广东省深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号`n"
    $content += "电话：" + (Get-RandomPhone) + "`n"
    $content += "开户银行：" + @("中国工商银行", "中国建设银行", "中国农业银行", "中国银行") | Get-Random + "`n"
    $content += "银行账号：" + (-join ((1..19) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "销售方：$seller`n"
    $content += "统一社会信用代码：91" + (-join ((1..16) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "地址：广东省深圳市光明新区塘明大道" + (Get-Random -Minimum 1 -Maximum 21) + "-" + (Get-Random -Minimum 1 -Maximum 11) + "号`n"
    $content += "电话：" + (Get-RandomPhone) + "`n"
    $content += "开户银行：" + @("中国工商银行", "中国建设银行", "中国农业银行", "中国银行") | Get-Random + "`n"
    $content += "银行账号：" + (-join ((1..19) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "货物或应税劳务、服务名称`t规格型号`t单位`t数量`t单价`t金额`t税率`t税额`n"
    $product = $products | Get-Random
    $quantity = Get-Random -Minimum 1 -Maximum 11
    $unitPrice = [math]::Round($netAmount / $quantity, 2)
    $content += "$product`t" + (-join ((1..4) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`t台`t$quantity`t$unitPrice`t$netAmount`t13%`t$taxAmount`n"
    $content += "价税合计（大写）：" + (Convert-AmountToChinese $amount) + "`n"
    $content += "价税合计（小写）：￥$amount`n"
    $content += "备注：本发票为应收账款凭证，请注意及时催收。`n"
    $content += "销售方（盖章）：$seller`n"
    $content += "开票人：" + @("小李", "小王", "小张", "小刘", "小陈") | Get-Random + "`n"
    $content += "复核人：" + @("张总", "李总", "王总", "赵总", "刘总") | Get-Random + "`n"
    return $content
}

# 生成收款单内容
function Get-ReceiptSlip($payee, $payer, $amount, $date, $docId) {
    $content = "收款单`n"
    $content += "单据编号：$docId`n"
    $content += "收款日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    $content += "收款方：$payee`n"
    $content += "统一社会信用代码：91440300" + (-join ((1..10) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "G`n"
    $content += "开户银行：" + @("中国工商银行", "中国建设银行", "中国农业银行", "中国银行") | Get-Random + "`n"
    $content += "银行账号：" + (-join ((1..19) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "付款方：$payer`n"
    $content += "统一社会信用代码：91" + (-join ((1..16) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "开户银行：" + @("中国工商银行", "中国建设银行", "中国农业银行", "中国银行") | Get-Random + "`n"
    $content += "银行账号：" + (-join ((1..19) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "收款金额：人民币$amount元（大写：" + (Convert-AmountToChinese $amount) + "）`n"
    $content += "收款用途：" + @("销售货款", "服务费用", "设备款", "材料款") | Get-Random + "（合同编号：$($docId -replace 'RS', 'SC')）`n"
    $content += "收款方式：银行转账`n"
    $content += "转账凭证号：" + (-join ((1..20) | ForEach-Object { Get-Random -Minimum 0 -Maximum 10 })) + "`n"
    $content += "备注：本收款单一式两份，收款方和付款方各执一份，作为财务记账凭证。`n"
    $content += "收款方（盖章）：$payee`n"
    $content += "财务负责人（签字）：________________________`n"
    $content += "日期：$($date.Year)年$($date.Month)月$($date.Day)日`n"
    return $content
}

# 生成文件内容
function Get-DocumentContent($docType, $seller, $buyer, $amount, $date, $docId) {
    switch ($docType) {
        "采购合同" {
            return Get-PurchaseContract $seller $buyer $amount $date $docId
        }
        "销售合同" {
            return Get-SalesContract $seller $buyer $amount $date $docId
        }
        "付款凭证" {
            return Get-PaymentVoucher $seller $buyer $amount $date $docId
        }
        "应付发票" {
            return Get-PayableInvoice $seller $buyer $amount $date $docId
        }
        "应收发票" {
            return Get-ReceivableInvoice $seller $buyer $amount $date $docId
        }
        "收款单" {
            return Get-ReceiptSlip $seller $buyer $amount $date $docId
        }
        default {
            return ""
        }
    }
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
        $docId = Get-ContractId $docType ($buyer)
        
        # 生成文件名
        if ($docType -in @("采购合同", "销售合同")) {
            $companyName = if ($docType -eq "采购合同") { $buyer } else { $seller }
            $fileName = "$docType（$companyName）-$($date.ToString('yyyyMMdd'))"
        } else {
            $fileName = "$docType-$($date.ToString('yyyyMMdd'))-$i"
        }
        
        # 生成内容
        $content = Get-DocumentContent $docType $seller $buyer $amount $date $docId
        
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
