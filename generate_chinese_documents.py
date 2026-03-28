import os
import random
import datetime
import json
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle
from reportlab.lib import colors
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.enums import TA_LEFT, TA_CENTER, TA_JUSTIFY

companies = [
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
]

document_types = [
    "销售合同", "采购合同", "付款凭证", "应付发票", "应收发票", "收款单"
]

products = [
    "Demura设备", "干式清洗机", "Panel保护膜撕膜机", "半导体显示面板",
    "LCD显示器", "OLED屏幕", "芯片", "电子元器件", "服务器", "网络设备",
    "光伏组件", "锂电池", "传感器", "工业机器人", "数控机床"
]

def generate_amount():
    return round(random.uniform(50000, 5000000), 2)

def generate_date():
    start_date = datetime.date(2026, 1, 1)
    end_date = datetime.date(2026, 12, 31)
    days_between = (end_date - start_date).days
    random_days = random.randint(0, days_between)
    return start_date + datetime.timedelta(days=random_days)

def generate_phone():
    return f"1{random.randint(3,9)}{random.randint(100000000, 999999999)}"

def generate_credit_code():
    return f"91440300{random.randint(1000000000, 9999999999)}"

def generate_contract_id(doc_type, company_abbr):
    prefix_map = {
        "销售合同": "SC", "采购合同": "PC", "付款凭证": "PV",
        "应付发票": "PI", "应收发票": "RI", "收款单": "RS"
    }
    prefix = prefix_map.get(doc_type, "DOC")
    date_str = datetime.datetime.now().strftime("%Y%m%d")
    random_num = random.randint(100, 999)
    return f"{prefix}-{company_abbr}-{date_str}-{random_num}"

def amount_to_chinese(amount):
    digits = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
    units = ['', '拾', '佰', '仟']
    big_units = ['', '万', '亿']

    amount_int = int(amount)
    amount_str = str(amount_int)
    decimal_str = f"{amount - amount_int:.2f}".split('.')[1]

    result = ""
    unit_index = 0
    big_unit_index = 0

    for i in range(len(amount_str) - 1, -1, -1):
        digit = int(amount_str[i])
        if digit != 0:
            result = digits[digit] + units[unit_index % 4] + result
        elif i > 0 and int(amount_str[i-1]) != 0:
            result = digits[digit] + result

        unit_index += 1
        if unit_index % 4 == 0 and i > 0:
            result = big_units[big_unit_index] + result
            big_unit_index += 1

    result += "元"
    if decimal_str == '00':
        result += "整"
    else:
        if decimal_str[0] != '0':
            result += digits[int(decimal_str[0])] + "角"
        if decimal_str[1] != '0':
            result += digits[int(decimal_str[1])] + "分"

    return result

def create_pdf_file(file_path, content_lines):
    doc = SimpleDocTemplate(
        file_path,
        pagesize=A4,
        rightMargin=2*cm,
        leftMargin=2*cm,
        topMargin=2*cm,
        bottomMargin=2*cm
    )

    styles = getSampleStyleSheet()

    try:
        pdfmetrics.registerFont(TTFont('Microsoft YaHei', 'C:/Windows/Fonts/msyh.ttc'))
        font_name = 'Microsoft YaHei'
    except:
        try:
            pdfmetrics.registerFont(TTFont('SimHei', 'C:/Windows/Fonts/simhei.ttf'))
            font_name = 'SimHei'
        except:
            font_name = 'Helvetica'

    style = ParagraphStyle(
        'Custom',
        parent=styles['Normal'],
        fontName=font_name,
        fontSize=10,
        leading=14,
        alignment=TA_LEFT
    )

    title_style = ParagraphStyle(
        'Title',
        parent=styles['Normal'],
        fontName=font_name,
        fontSize=14,
        leading=18,
        alignment=TA_CENTER,
        spaceAfter=12
    )

    story = []
    for line in content_lines:
        if line.strip():
            if line in ["采购合同", "销售合同", "付款凭证", "增值税专用发票", "收款单"]:
                story.append(Paragraph(line, title_style))
            else:
                story.append(Paragraph(line, style))
            story.append(Spacer(1, 3))
        else:
            story.append(Spacer(1, 6))

    doc.build(story)

def get_company_abbr(company_name):
    abbr_map = {
        "阿里巴巴集团": "ALBB", "腾讯控股": "TCEHY", "百度集团": "BIDU", "京东集团": "JD",
        "华为技术有限公司": "HW", "小米集团": "XM", "字节跳动": "BDTD", "网易集团": "NTES",
        "拼多多": "PDD", "美团": "MT", "中国工商银行": "ICBC", "中国建设银行": "CCB",
        "中国农业银行": "ABC", "中国银行": "BOC", "中国移动": "CMCC", "中国电信": "CT",
        "中国联通": "CU", "中国石油天然气集团": "CNPC", "中国石油化工集团": "SINOPEC",
        "中国建筑集团": "CSCEC", "中国中铁": "CREC", "中国铁建": "CRCC", "中国中车": "CRRC",
        "中国航天科技集团": "CASC", "中国航天科工集团": "CASIC", "苹果公司": "AAPL",
        "微软公司": "MSFT", "谷歌公司": "GOOGL", "亚马逊公司": "AMZN", "Meta": "META",
        "特斯拉公司": "TSLA", "英特尔公司": "INTC", "IBM": "IBM", "甲骨文公司": "ORCL",
        "思科系统": "CSCO", "惠普公司": "HP", "戴尔科技": "DELL", "通用电气": "GE",
        "福特汽车": "F", "通用汽车": "GM", "波音公司": "BA", "辉瑞公司": "PFE",
        "强生公司": "JNJ", "摩根大通": "JPM", "高盛集团": "GS", "花旗集团": "C",
        "瑞银集团": "UBS", "德意志银行": "DB", "西门子": "SIE", "宝马集团": "BMW"
    }
    return abbr_map.get(company_name, company_name[:2].upper())

def generate_purchase_contract_content(seller, buyer, amount, date, doc_id):
    product = random.choice(products)
    chinese_amount = amount_to_chinese(amount)

    lines = [
        f"采购合同（{buyer}）",
        f"合同编号：{doc_id}",
        f"甲方（采购方）：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"法定代表人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"联系人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])} 联系电话：{generate_phone()}",
        f"乙方（供应方）：{seller}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"法定代表人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"联系人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])} 联系电话：{generate_phone()}",
        "依据《中华人民共和国民法典》及相关法律法规，甲乙双方本着平等自愿、公平诚信、互利共赢的原则，就甲方向乙方采购半导体显示相关设备及配件事宜，经友好协商，订立本合同，以资共同信守。",
        f"一、采购标的：甲方向乙方采购{product}（产品名称、规格型号、数量、单价详见附件），合同总价款为人民币{amount}元（大写：{chinese_amount}），该价款为含税总价，包含货物成本、包装、运输、装卸、税费及售后服务等全部费用，甲方无需额外支付其他费用。",
        "二、质量标准：乙方提供的货物需符合国家半导体显示行业相关标准、产品说明书要求及甲方约定标准，确保为全新、未使用过的合格产品，无质量瑕疵、无破损，且符合相关采购质量规范，提供完整的质量合格证明及专业检测报告。",
    ]
    delivery_date = date + datetime.timedelta(days=random.randint(15, 45))
    lines.append(f"三、交付与验收：乙方应于{delivery_date.year}年{delivery_date.month}月{delivery_date.day}日前，将货物送至甲方指定地点（深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号{buyer}指定仓库/生产车间），负责货物的装卸、摆放及初步调试。甲方在收到货物后7个工作日内完成验收，验收合格后签署验收单；若发现货物质量、数量、规格不符合约定，甲方有权要求乙方在3个工作日内更换或补足，相关费用由乙方承担。")
    lines.extend([
        "四、付款方式：合同签订后5个工作日内，甲方向乙方支付合同总价款的20%作为预付款；货物验收合格并完成初步调试后10个工作日内，甲方支付至合同总价款的95%；剩余5%作为质量保证金，自验收合格之日起12个月内无质量问题，甲方无息一次性支付。乙方需在付款前提供合法有效的增值税专用发票，否则甲方有权顺延付款。",
        "五、售后服务：乙方承诺货物质保期为12个月，质保期内出现质量问题，乙方应在收到甲方通知后24小时内响应，48小时内派员到场维修或更换，维修费用由乙方承担；质保期结束后，乙方需提供长期优惠维修服务及配件供应。",
        "六、违约责任：乙方逾期交付货物，每逾期一日按合同总价款的0.5‰支付违约金，逾期超过15日，甲方有权解除合同并要求乙方赔偿损失；乙方提供的货物质量不合格，应承担更换、退货及甲方因此遭受的生产停工、设备闲置等全部损失；甲方逾期付款，每逾期一日按未付款项的0.5‰支付违约金。",
        "七、争议解决：本合同履行中发生的争议，双方协商解决；协商不成的，提交甲方所在地有管辖权的人民法院诉讼解决。",
        "八、其他：本合同附件为本合同不可分割的组成部分，与本合同具有同等法律效力；本合同一式肆份，甲乙双方各执贰份，自双方签字盖章之日起生效，履行完毕后自动终止。",
        f"甲方（盖章）：{buyer}",
        "法定代表人/授权代表（签字）：________________________",
        f"签订日期：{date.year}年{date.month}月{date.day}日",
        f"乙方（盖章）：{seller}",
        "法定代表人/授权代表（签字）：________________________",
        f"签订日期：{date.year}年{date.month}月{date.day}日",
    ])
    return lines

def generate_sales_contract_content(seller, buyer, amount, date, doc_id):
    product = random.choice(products)
    chinese_amount = amount_to_chinese(amount)

    lines = [
        f"销售合同（{seller}）",
        f"合同编号：{doc_id}",
        f"甲方（卖方）：{seller}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"法定代表人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"联系人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])} 联系电话：{generate_phone()}",
        f"乙方（买方）：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"法定代表人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"联系人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])} 联系电话：{generate_phone()}",
        "依据《中华人民共和国民法典》及相关法律法规，甲乙双方本着平等自愿、公平诚信、互利共赢的原则，就甲方向乙方销售半导体显示相关设备及配件事宜，经友好协商，订立本合同，以资共同信守。",
        f"一、销售标的：甲方向乙方销售{product}（产品名称、规格型号、数量、单价详见附件），合同总价款为人民币{amount}元（大写：{chinese_amount}），该价款为含税总价，包含货物成本、包装、运输、装卸、税费及售后服务等全部费用，乙方无需额外支付其他费用。",
        "二、质量标准：甲方提供的货物需符合国家半导体显示行业相关标准、产品说明书要求及乙方约定标准，确保为全新、未使用过的合格产品，无质量瑕疵、无破损，且符合相关销售质量规范，提供完整的质量合格证明及专业检测报告。",
    ]
    delivery_date = date + datetime.timedelta(days=random.randint(15, 45))
    lines.append(f"三、交付与验收：甲方应于{delivery_date.year}年{delivery_date.month}月{delivery_date.day}日前，将货物送至乙方指定地点（深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号{buyer}指定仓库/生产车间），负责货物的装卸、摆放及初步调试。乙方在收到货物后7个工作日内完成验收，验收合格后签署验收单；若发现货物质量、数量、规格不符合约定，乙方有权要求甲方在3个工作日内更换或补足，相关费用由甲方承担。")
    lines.extend([
        "四、付款方式：合同签订后5个工作日内，乙方向甲方支付合同总价款的20%作为预付款；货物验收合格并完成初步调试后10个工作日内，乙方支付至合同总价款的95%；剩余5%作为质量保证金，自验收合格之日起12个月内无质量问题，乙方无息一次性支付。甲方需在付款前提供合法有效的增值税专用发票，否则乙方有权顺延付款。",
        "五、售后服务：甲方承诺货物质保期为12个月，质保期内出现质量问题，甲方应在收到乙方通知后24小时内响应，48小时内派员到场维修或更换，维修费用由甲方承担；质保期结束后，甲方需提供长期优惠维修服务及配件供应。",
        "六、违约责任：甲方逾期交付货物，每逾期一日按合同总价款的0.5‰支付违约金，逾期超过15日，乙方有权解除合同并要求甲方赔偿损失；甲方提供的货物质量不合格，应承担更换、退货及乙方因此遭受的生产停工、设备闲置等全部损失；乙方逾期付款，每逾期一日按未付款项的0.5‰支付违约金。",
        "七、争议解决：本合同履行中发生的争议，双方协商解决；协商不成的，提交乙方所在地有管辖权的人民法院诉讼解决。",
        "八、其他：本合同附件为本合同不可分割的组成部分，与本合同具有同等法律效力；本合同一式肆份，甲乙双方各执贰份，自双方签字盖章之日起生效，履行完毕后自动终止。",
        f"甲方（盖章）：{seller}",
        "法定代表人/授权代表（签字）：________________________",
        f"签订日期：{date.year}年{date.month}月{date.day}日",
        f"乙方（盖章）：{buyer}",
        "法定代表人/授权代表（签字）：________________________",
        f"签订日期：{date.year}年{date.month}月{date.day}日",
    ])
    return lines

def generate_payment_voucher_content(payer, payee, amount, date, doc_id):
    chinese_amount = amount_to_chinese(amount)
    banks = ["中国工商银行", "中国建设银行", "中国农业银行", "中国银行"]
    usage = random.choice(["采购货款", "服务费用", "设备款", "材料款", "工程款"])

    return [
        "付款凭证",
        f"凭证编号：{doc_id}",
        f"付款日期：{date.year}年{date.month}月{date.day}日",
        f"付款方：{payer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"收款方：{payee}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"付款金额：人民币{amount}元（大写：{chinese_amount}）",
        f"付款用途：{usage}（合同编号：{doc_id.replace('PV', 'PC')}）",
        "付款方式：银行转账",
        f"转账凭证号：{random.randint(10000000000000000000, 99999999999999999999)}",
        "备注：本付款凭证一式两份，付款方和收款方各执一份，作为财务记账凭证。",
        f"付款方（盖章）：{payer}",
        "财务负责人（签字）：________________________",
        f"日期：{date.year}年{date.month}月{date.day}日",
    ]

def generate_payable_invoice_content(seller, buyer, amount, date, doc_id):
    tax_amount = round(amount / 1.13 * 0.13, 2)
    net_amount = round(amount - tax_amount, 2)
    chinese_amount = amount_to_chinese(amount)
    banks = ["中国工商银行", "中国建设银行", "中国农业银行", "中国银行"]
    product = random.choice(products)
    quantity = random.randint(1, 50)
    unit_price = round(net_amount / quantity, 2)

    return [
        "增值税专用发票",
        f"发票号码：{doc_id}",
        f"开票日期：{date.year}年{date.month}月{date.day}日",
        f"购买方：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"电话：{generate_phone()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"销售方：{seller}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"电话：{generate_phone()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"货物或应税劳务、服务名称：{product}",
        f"规格型号：{random.randint(1000,9999)}",
        f"单位：台",
        f"数量：{quantity}",
        f"单价：{unit_price}",
        f"金额：{net_amount}",
        f"税率：13%",
        f"税额：{tax_amount}",
        f"价税合计（大写）：{chinese_amount}",
        f"价税合计（小写）：￥{amount}",
        "备注：本发票为应付账款凭证，请注意及时入账。",
        f"销售方（盖章）：{seller}",
        f"开票人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])}",
        f"复核人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}",
    ]

def generate_receivable_invoice_content(seller, buyer, amount, date, doc_id):
    tax_amount = round(amount / 1.13 * 0.13, 2)
    net_amount = round(amount - tax_amount, 2)
    chinese_amount = amount_to_chinese(amount)
    banks = ["中国工商银行", "中国建设银行", "中国农业银行", "中国银行"]
    product = random.choice(products)
    quantity = random.randint(1, 50)
    unit_price = round(net_amount / quantity, 2)

    return [
        "增值税专用发票",
        f"发票号码：{doc_id}",
        f"开票日期：{date.year}年{date.month}月{date.day}日",
        f"购买方：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"电话：{generate_phone()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"销售方：{seller}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"电话：{generate_phone()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"货物或应税劳务、服务名称：{product}",
        f"规格型号：{random.randint(1000,9999)}",
        f"单位：台",
        f"数量：{quantity}",
        f"单价：{unit_price}",
        f"金额：{net_amount}",
        f"税率：13%",
        f"税额：{tax_amount}",
        f"价税合计（大写）：{chinese_amount}",
        f"价税合计（小写）：￥{amount}",
        "备注：本发票为应收账款凭证，请注意及时催收。",
        f"销售方（盖章）：{seller}",
        f"开票人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])}",
        f"复核人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}",
    ]

def generate_receipt_slip_content(payee, payer, amount, date, doc_id):
    chinese_amount = amount_to_chinese(amount)
    banks = ["中国工商银行", "中国建设银行", "中国农业银行", "中国银行"]
    usage = random.choice(["销售货款", "服务费用", "设备款", "材料款", "工程款"])

    return [
        "收款单",
        f"单据编号：{doc_id}",
        f"收款日期：{date.year}年{date.month}月{date.day}日",
        f"收款方：{payee}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"付款方：{payer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"收款金额：人民币{amount}元（大写：{chinese_amount}）",
        f"收款用途：{usage}（合同编号：{doc_id.replace('RS', 'SC')}）",
        "收款方式：银行转账",
        f"转账凭证号：{random.randint(10000000000000000000, 99999999999999999999)}",
        "备注：本收款单一式两份，收款方和付款方各执一份，作为财务记账凭证。",
        f"收款方（盖章）：{payee}",
        "财务负责人（签字）：________________________",
        f"日期：{date.year}年{date.month}月{date.day}日",
    ]

def get_content_lines(doc_type, seller, buyer, amount, date, doc_id):
    if doc_type == "采购合同":
        return generate_purchase_contract_content(seller, buyer, amount, date, doc_id)
    elif doc_type == "销售合同":
        return generate_sales_contract_content(seller, buyer, amount, date, doc_id)
    elif doc_type == "付款凭证":
        return generate_payment_voucher_content(seller, buyer, amount, date, doc_id)
    elif doc_type == "应付发票":
        return generate_payable_invoice_content(seller, buyer, amount, date, doc_id)
    elif doc_type == "应收发票":
        return generate_receivable_invoice_content(seller, buyer, amount, date, doc_id)
    elif doc_type == "收款单":
        return generate_receipt_slip_content(seller, buyer, amount, date, doc_id)
    return []

def main():
    output_dir = "D:/AI project/AI-search/测试文件目录"
    total_files = 1000

    os.makedirs(output_dir, exist_ok=True)
    print(f"开始生成{total_files}个商业交易文档...")
    print(f"输出目录: {output_dir}")

    tasks = []
    for i in range(total_files):
        doc_type = random.choice(document_types)
        seller, buyer = random.sample(companies, 2)
        amount = generate_amount()
        date = generate_date()

        if doc_type in ["采购合同", "销售合同"]:
            company_name = buyer if doc_type == "采购合同" else seller
            company_abbr = get_company_abbr(company_name)
        else:
            company_abbr = get_company_abbr(buyer)

        doc_id = generate_contract_id(doc_type, company_abbr)

        if doc_type in ["采购合同", "销售合同"]:
            company_name = buyer if doc_type == "采购合同" else seller
            file_name = f"{doc_type}（{company_name}）-{date.strftime('%Y%m%d')}"
        else:
            file_name = f"{doc_type}-{date.strftime('%Y%m%d')}-{i+1}"

        content_lines = get_content_lines(doc_type, seller, buyer, amount, date, doc_id)

        tasks.append({
            "index": i + 1,
            "doc_type": doc_type,
            "seller": seller,
            "buyer": buyer,
            "file_name": file_name,
            "doc_id": doc_id
        })

        doc_file_path = os.path.join(output_dir, f"{file_name}.doc")
        with open(doc_file_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(content_lines))

        pdf_file_path = os.path.join(output_dir, f"{file_name}.pdf")
        try:
            create_pdf_file(pdf_file_path, content_lines)
        except Exception as e:
            print(f"PDF生成失败 {pdf_file_path}: {str(e)}")

        if (i + 1) % 50 == 0:
            print(f"已生成{i + 1}个文件")

    print(f"生成完成！共生成{total_files}个DOC文件和{total_files}个PDF文件。")

    task_file = os.path.join(output_dir, "generation_tasks.json")
    with open(task_file, 'w', encoding='utf-8') as f:
        json.dump(tasks, f, ensure_ascii=False, indent=2)
    print(f"任务清单已保存到: {task_file}")

if __name__ == "__main__":
    main()
