import os
import random
import datetime

# 公司列表
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

# 文档类型
document_types = [
    "销售合同", "采购合同", "付款凭证", "应付发票", "应收发票", "收款单"
]

# 产品列表
products = [
    "Demura设备", "干式清洗机", "Panel保护膜撕膜机", "半导体显示面板",
    "LCD显示器", "OLED屏幕", "芯片", "电子元器件", "服务器", "网络设备"
]

# 生成随机金额
def generate_amount():
    return round(random.uniform(100000, 10000000), 2)

# 生成随机日期
def generate_date():
    start_date = datetime.date(2026, 1, 1)
    end_date = datetime.date(2026, 12, 31)
    days_between = (end_date - start_date).days
    random_days = random.randint(0, days_between)
    return start_date + datetime.timedelta(days=random_days)

# 生成随机合同编号
def generate_contract_id(doc_type, company):
    prefix = {
        "销售合同": "SC",
        "采购合同": "PC",
        "付款凭证": "PV",
        "应付发票": "PI",
        "应收发票": "RI",
        "收款单": "RS"
    }[doc_type]
    # 提取公司简称
    company_abbr = ''.join([c for c in company if c.isupper() or c.isdigit()])
    if not company_abbr:
        company_abbr = company[:2]
    date_str = datetime.datetime.now().strftime('%Y%m%d')
    random_num = random.randint(100, 999)
    return f"{prefix}-{company_abbr}-{date_str}-{random_num}"

# 生成随机联系电话
def generate_phone():
    return f"1{random.randint(3, 9)}{''.join([str(random.randint(0, 9)) for _ in range(9)])}"

# 生成采购合同内容
def generate_purchase_contract(seller, buyer, amount, date, doc_id):
    product = random.choice(products)
    quantity = random.randint(1, 100)
    unit_price = round(amount / quantity, 2)
    
    content = f"采购合同（{buyer}）\n"
    content += f"合同编号：{doc_id}\n"
    content += f"甲方（采购方）：{buyer}\n"
    content += f"统一社会信用代码：91440300{''.join([str(random.randint(0, 9)) for _ in range(10)])}G\n"
    content += f"法定代表人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}\n"
    content += f"地址：广东省深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号\n"
    content += f"联系人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])} 联系电话：{generate_phone()}\n"
    content += f"乙方（供应方）：{seller}\n"
    content += f"统一社会信用代码：91{''.join([str(random.randint(0, 9)) for _ in range(16)])}\n"
    content += f"法定代表人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])}\n"
    content += f"地址：广东省深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号\n"
    content += f"联系人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])} 联系电话：{generate_phone()}\n"
    content += "依据《中华人民共和国民法典》及相关法律法规，甲乙双方本着平等自愿、公平诚信、互利共赢的原则，就甲方向乙方采购半导体显示相关设备及配件事宜，经友好协商，订立本合同，以资共同信守。\n"
    content += f"一、采购标的：甲方向乙方采购{product}（产品名称、规格型号、数量、单价详见附件），合同总价款为人民币{amount}元（大写：{amount_to_chinese(amount)}），该价款为含税总价，包含货物成本、包装、运输、装卸、税费及售后服务等全部费用，甲方无需额外支付其他费用。\n"
    content += "二、质量标准：乙方提供的货物需符合国家半导体显示行业相关标准、产品说明书要求及甲方约定标准，确保为全新、未使用过的合格产品，无质量瑕疵、无破损，且符合相关采购质量规范，提供完整的质量合格证明及专业检测报告。\n"
    delivery_date = date + datetime.timedelta(days=random.randint(1, 30))
    content += f"三、交付与验收：乙方应于{delivery_date.year}年{delivery_date.month}月{delivery_date.day}日前，将货物送至甲方指定地点（深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号{buyer}指定仓库/生产车间），负责货物的装卸、摆放及初步调试。甲方在收到货物后7个工作日内完成验收，验收合格后签署验收单；若发现货物质量、数量、规格不符合约定，甲方有权要求乙方在3个工作日内更换或补足，相关费用由乙方承担。\n"
    content += "四、付款方式：合同签订后5个工作日内，甲方向乙方支付合同总价款的20%作为预付款；货物验收合格并完成初步调试后10个工作日内，甲方支付至合同总价款的95%；剩余5%作为质量保证金，自验收合格之日起12个月内无质量问题，甲方无息一次性支付。乙方需在付款前提供合法有效的增值税专用发票，否则甲方有权顺延付款。\n"
    content += "五、售后服务：乙方承诺货物质保期为12个月，质保期内出现质量问题，乙方应在收到甲方通知后24小时内响应，48小时内派员到场维修或更换，维修费用由乙方承担；质保期结束后，乙方需提供长期优惠维修服务及配件供应。\n"
    content += "六、违约责任：乙方逾期交付货物，每逾期一日按合同总价款的0.5‰支付违约金，逾期超过15日，甲方有权解除合同并要求乙方赔偿损失；乙方提供的货物质量不合格，应承担更换、退货及甲方因此遭受的生产停工、设备闲置等全部损失；甲方逾期付款，每逾期一日按未付款项的0.5‰支付违约金。\n"
    content += "七、争议解决：本合同履行中发生的争议，双方协商解决；协商不成的，提交甲方所在地有管辖权的人民法院诉讼解决。\n"
    content += "八、其他：本合同附件为本合同不可分割的组成部分，与本合同具有同等法律效力；本合同一式肆份，甲乙双方各执贰份，自双方签字盖章之日起生效，履行完毕后自动终止。\n"
    content += f"甲方（盖章）：{buyer}\n"
    content += "法定代表人/授权代表（签字）：________________________\n"
    content += f"签订日期：{date.year}年{date.month}月{date.day}日\n"
    content += f"乙方（盖章）：{seller}\n"
    content += "法定代表人/授权代表（签字）：________________________\n"
    content += f"签订日期：{date.year}年{date.month}月{date.day}日\n"
    return content

# 生成销售合同内容
def generate_sales_contract(seller, buyer, amount, date, doc_id):
    product = random.choice(products)
    quantity = random.randint(1, 100)
    unit_price = round(amount / quantity, 2)
    
    content = f"销售合同（{seller}）\n"
    content += f"合同编号：{doc_id}\n"
    content += f"甲方（卖方）：{seller}\n"
    content += f"统一社会信用代码：91440300{''.join([str(random.randint(0, 9)) for _ in range(10)])}G\n"
    content += f"法定代表人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}\n"
    content += f"地址：广东省深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号\n"
    content += f"联系人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])} 联系电话：{generate_phone()}\n"
    content += f"乙方（买方）：{buyer}\n"
    content += f"统一社会信用代码：91{''.join([str(random.randint(0, 9)) for _ in range(16)])}\n"
    content += f"法定代表人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])}\n"
    content += f"地址：广东省深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号\n"
    content += f"联系人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])} 联系电话：{generate_phone()}\n"
    content += "依据《中华人民共和国民法典》及相关法律法规，甲乙双方本着平等自愿、公平诚信、互利共赢的原则，就甲方向乙方销售半导体显示相关设备及配件事宜，经友好协商，订立本合同，以资共同信守。\n"
    content += f"一、销售标的：甲方向乙方销售{product}（产品名称、规格型号、数量、单价详见附件），合同总价款为人民币{amount}元（大写：{amount_to_chinese(amount)}），该价款为含税总价，包含货物成本、包装、运输、装卸、税费及售后服务等全部费用，乙方无需额外支付其他费用。\n"
    content += "二、质量标准：甲方提供的货物需符合国家半导体显示行业相关标准、产品说明书要求及乙方约定标准，确保为全新、未使用过的合格产品，无质量瑕疵、无破损，且符合相关采购质量规范，提供完整的质量合格证明及专业检测报告。\n"
    delivery_date = date + datetime.timedelta(days=random.randint(1, 30))
    content += f"三、交付与验收：甲方应于{delivery_date.year}年{delivery_date.month}月{delivery_date.day}日前，将货物送至乙方指定地点（深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号{buyer}指定仓库/生产车间），负责货物的装卸、摆放及初步调试。乙方在收到货物后7个工作日内完成验收，验收合格后签署验收单；若发现货物质量、数量、规格不符合约定，乙方有权要求甲方在3个工作日内更换或补足，相关费用由甲方承担。\n"
    content += "四、付款方式：合同签订后5个工作日内，乙方向甲方支付合同总价款的20%作为预付款；货物验收合格并完成初步调试后10个工作日内，乙方支付至合同总价款的95%；剩余5%作为质量保证金，自验收合格之日起12个月内无质量问题，乙方无息一次性支付。甲方需在付款前提供合法有效的增值税专用发票，否则乙方有权顺延付款。\n"
    content += "五、售后服务：甲方承诺货物质保期为12个月，质保期内出现质量问题，甲方应在收到乙方通知后24小时内响应，48小时内派员到场维修或更换，维修费用由甲方承担；质保期结束后，甲方需提供长期优惠维修服务及配件供应。\n"
    content += "六、违约责任：甲方逾期交付货物，每逾期一日按合同总价款的0.5‰支付违约金，逾期超过15日，乙方有权解除合同并要求甲方赔偿损失；甲方提供的货物质量不合格，应承担更换、退货及乙方因此遭受的生产停工、设备闲置等全部损失；乙方逾期付款，每逾期一日按未付款项的0.5‰支付违约金。\n"
    content += "七、争议解决：本合同履行中发生的争议，双方协商解决；协商不成的，提交乙方所在地有管辖权的人民法院诉讼解决。\n"
    content += "八、其他：本合同附件为本合同不可分割的组成部分，与本合同具有同等法律效力；本合同一式肆份，甲乙双方各执贰份，自双方签字盖章之日起生效，履行完毕后自动终止。\n"
    content += f"甲方（盖章）：{seller}\n"
    content += "法定代表人/授权代表（签字）：________________________\n"
    content += f"签订日期：{date.year}年{date.month}月{date.day}日\n"
    content += f"乙方（盖章）：{buyer}\n"
    content += "法定代表人/授权代表（签字）：________________________\n"
    content += f"签订日期：{date.year}年{date.month}月{date.day}日\n"
    return content

# 生成付款凭证内容
def generate_payment_voucher(payer, payee, amount, date, doc_id):
    content = f"付款凭证\n"
    content += f"凭证编号：{doc_id}\n"
    content += f"付款日期：{date.year}年{date.month}月{date.day}日\n"
    content += f"付款方：{payer}\n"
    content += f"统一社会信用代码：91440300{''.join([str(random.randint(0, 9)) for _ in range(10)])}G\n"
    content += f"开户银行：{random.choice(['中国工商银行', '中国建设银行', '中国农业银行', '中国银行'])}\n"
    content += f"银行账号：{''.join([str(random.randint(0, 9)) for _ in range(19)])}\n"
    content += f"收款方：{payee}\n"
    content += f"统一社会信用代码：91{''.join([str(random.randint(0, 9)) for _ in range(16)])}\n"
    content += f"开户银行：{random.choice(['中国工商银行', '中国建设银行', '中国农业银行', '中国银行'])}\n"
    content += f"银行账号：{''.join([str(random.randint(0, 9)) for _ in range(19)])}\n"
    content += f"付款金额：人民币{amount}元（大写：{amount_to_chinese(amount)}）\n"
    content += f"付款用途：{random.choice(['采购货款', '服务费用', '设备款', '材料款'])}（合同编号：{doc_id.replace('PV', 'PC')}）\n"
    content += f"付款方式：银行转账\n"
    content += f"转账凭证号：{''.join([str(random.randint(0, 9)) for _ in range(20)])}\n"
    content += "备注：本付款凭证一式两份，付款方和收款方各执一份，作为财务记账凭证。\n"
    content += f"付款方（盖章）：{payer}\n"
    content += "财务负责人（签字）：________________________\n"
    content += f"日期：{date.year}年{date.month}月{date.day}日\n"
    return content

# 生成应付发票内容
def generate_payable_invoice(seller, buyer, amount, date, doc_id):
    tax_amount = round(amount / 1.13 * 0.13, 2)
    net_amount = round(amount - tax_amount, 2)
    
    content = f"增值税专用发票\n"
    content += f"发票号码：{doc_id}\n"
    content += f"开票日期：{date.year}年{date.month}月{date.day}日\n"
    content += f"购买方：{buyer}\n"
    content += f"统一社会信用代码：91440300{''.join([str(random.randint(0, 9)) for _ in range(10)])}G\n"
    content += f"地址：广东省深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号\n"
    content += f"电话：{generate_phone()}\n"
    content += f"开户银行：{random.choice(['中国工商银行', '中国建设银行', '中国农业银行', '中国银行'])}\n"
    content += f"银行账号：{''.join([str(random.randint(0, 9)) for _ in range(19)])}\n"
    content += f"销售方：{seller}\n"
    content += f"统一社会信用代码：91{''.join([str(random.randint(0, 9)) for _ in range(16)])}\n"
    content += f"地址：广东省深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号\n"
    content += f"电话：{generate_phone()}\n"
    content += f"开户银行：{random.choice(['中国工商银行', '中国建设银行', '中国农业银行', '中国银行'])}\n"
    content += f"银行账号：{''.join([str(random.randint(0, 9)) for _ in range(19)])}\n"
    content += "货物或应税劳务、服务名称\t规格型号\t单位\t数量\t单价\t金额\t税率\t税额\n"
    content += f"{random.choice(products)}\t{''.join([str(random.randint(0, 9)) for _ in range(4)])}\t台\t{random.randint(1, 10)}\t{round(net_amount / random.randint(1, 10), 2)}\t{net_amount}\t13%\t{tax_amount}\n"
    content += f"价税合计（大写）：{amount_to_chinese(amount)}\n"
    content += f"价税合计（小写）：￥{amount}\n"
    content += "备注：本发票为应付账款凭证，请注意及时入账。\n"
    content += f"销售方（盖章）：{seller}\n"
    content += "开票人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])}\n"
    content += f"复核人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}\n"
    return content

# 生成应收发票内容
def generate_receivable_invoice(seller, buyer, amount, date, doc_id):
    tax_amount = round(amount / 1.13 * 0.13, 2)
    net_amount = round(amount - tax_amount, 2)
    
    content = f"增值税专用发票\n"
    content += f"发票号码：{doc_id}\n"
    content += f"开票日期：{date.year}年{date.month}月{date.day}日\n"
    content += f"购买方：{buyer}\n"
    content += f"统一社会信用代码：91440300{''.join([str(random.randint(0, 9)) for _ in range(10)])}G\n"
    content += f"地址：广东省深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号\n"
    content += f"电话：{generate_phone()}\n"
    content += f"开户银行：{random.choice(['中国工商银行', '中国建设银行', '中国农业银行', '中国银行'])}\n"
    content += f"银行账号：{''.join([str(random.randint(0, 9)) for _ in range(19)])}\n"
    content += f"销售方：{seller}\n"
    content += f"统一社会信用代码：91{''.join([str(random.randint(0, 9)) for _ in range(16)])}\n"
    content += f"地址：广东省深圳市光明新区塘明大道{random.randint(1, 20)}-{random.randint(1, 10)}号\n"
    content += f"电话：{generate_phone()}\n"
    content += f"开户银行：{random.choice(['中国工商银行', '中国建设银行', '中国农业银行', '中国银行'])}\n"
    content += f"银行账号：{''.join([str(random.randint(0, 9)) for _ in range(19)])}\n"
    content += "货物或应税劳务、服务名称\t规格型号\t单位\t数量\t单价\t金额\t税率\t税额\n"
    content += f"{random.choice(products)}\t{''.join([str(random.randint(0, 9)) for _ in range(4)])}\t台\t{random.randint(1, 10)}\t{round(net_amount / random.randint(1, 10), 2)}\t{net_amount}\t13%\t{tax_amount}\n"
    content += f"价税合计（大写）：{amount_to_chinese(amount)}\n"
    content += f"价税合计（小写）：￥{amount}\n"
    content += "备注：本发票为应收账款凭证，请注意及时催收。\n"
    content += f"销售方（盖章）：{seller}\n"
    content += "开票人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])}\n"
    content += "复核人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}\n"
    return content

# 生成收款单内容
def generate_receipt_slip(payee, payer, amount, date, doc_id):
    content = f"收款单\n"
    content += f"单据编号：{doc_id}\n"
    content += f"收款日期：{date.year}年{date.month}月{date.day}日\n"
    content += f"收款方：{payee}\n"
    content += f"统一社会信用代码：91440300{''.join([str(random.randint(0, 9)) for _ in range(10)])}G\n"
    content += f"开户银行：{random.choice(['中国工商银行', '中国建设银行', '中国农业银行', '中国银行'])}\n"
    content += f"银行账号：{''.join([str(random.randint(0, 9)) for _ in range(19)])}\n"
    content += f"付款方：{payer}\n"
    content += f"统一社会信用代码：91{''.join([str(random.randint(0, 9)) for _ in range(16)])}\n"
    content += f"开户银行：{random.choice(['中国工商银行', '中国建设银行', '中国农业银行', '中国银行'])}\n"
    content += f"银行账号：{''.join([str(random.randint(0, 9)) for _ in range(19)])}\n"
    content += f"收款金额：人民币{amount}元（大写：{amount_to_chinese(amount)}）\n"
    content += f"收款用途：{random.choice(['销售货款', '服务费用', '设备款', '材料款'])}（合同编号：{doc_id.replace('RS', 'SC')}）\n"
    content += f"收款方式：银行转账\n"
    content += f"转账凭证号：{''.join([str(random.randint(0, 9)) for _ in range(20)])}\n"
    content += "备注：本收款单一式两份，收款方和付款方各执一份，作为财务记账凭证。\n"
    content += f"收款方（盖章）：{payee}\n"
    content += "财务负责人（签字）：________________________\n"
    content += f"日期：{date.year}年{date.month}月{date.day}日\n"
    return content

# 金额转中文大写
def amount_to_chinese(amount):
    digits = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
    units = ['', '拾', '佰', '仟']
    big_units = ['', '万', '亿']
    
    amount_str = str(int(amount))
    decimal_str = str(round(amount - int(amount), 2)).split('.')[1] if '.' in str(amount) else '00'
    
    result = ''
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
    
    result += '元'
    if decimal_str == '00':
        result += '整'
    else:
        if decimal_str[0] != '0':
            result += digits[int(decimal_str[0])] + '角'
        if decimal_str[1] != '0':
            result += digits[int(decimal_str[1])] + '分'
    
    return result

# 生成文件内容
def generate_content(doc_type, seller, buyer, amount, date, doc_id):
    if doc_type == "采购合同":
        return generate_purchase_contract(seller, buyer, amount, date, doc_id)
    elif doc_type == "销售合同":
        return generate_sales_contract(seller, buyer, amount, date, doc_id)
    elif doc_type == "付款凭证":
        return generate_payment_voucher(seller, buyer, amount, date, doc_id)
    elif doc_type == "应付发票":
        return generate_payable_invoice(seller, buyer, amount, date, doc_id)
    elif doc_type == "应收发票":
        return generate_receivable_invoice(seller, buyer, amount, date, doc_id)
    elif doc_type == "收款单":
        return generate_receipt_slip(seller, buyer, amount, date, doc_id)
    else:
        return ""

# 主函数
def main():
    output_dir = "D:\AI project\AI-search\测试文件目录"
    total_files = 1000
    
    # 确保输出目录存在
    os.makedirs(output_dir, exist_ok=True)
    print(f"输出目录: {output_dir}")
    print(f"开始生成{total_files}个商业交易文档...")
    
    try:
        for i in range(total_files):
            # 随机选择文档类型
            doc_type = random.choice(document_types)
            
            # 随机选择两家不同的公司
            seller, buyer = random.sample(companies, 2)
            
            # 生成随机数据
            amount = generate_amount()
            date = generate_date()
            doc_id = generate_contract_id(doc_type, buyer if doc_type == "采购合同" else seller)
            
            # 生成文件名
            if doc_type in ["采购合同", "销售合同"]:
                company_name = buyer if doc_type == "采购合同" else seller
                file_name = f"{doc_type}（{company_name}）-{date.strftime('%Y%m%d')}"
            else:
                file_name = f"{doc_type}-{date.strftime('%Y%m%d')}-{i+1}"
            
            # 生成内容
            content = generate_content(doc_type, seller, buyer, amount, date, doc_id)
            
            # 生成DOC文件
            doc_file_path = os.path.join(output_dir, f"{file_name}.doc")
            print(f"生成DOC文件: {doc_file_path}")
            with open(doc_file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            
            # 生成PDF文件
            pdf_file_path = os.path.join(output_dir, f"{file_name}.pdf")
            print(f"生成PDF文件: {pdf_file_path}")
            with open(pdf_file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            
            if (i + 1) % 100 == 0:
                print(f"已生成{i + 1}个文件")
        
        print(f"生成完成！共生成{total_files}个DOC文件和{total_files}个PDF文件。")
    except Exception as e:
        print(f"错误: {str(e)}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()
