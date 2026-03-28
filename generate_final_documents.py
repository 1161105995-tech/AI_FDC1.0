import os
import random
import datetime
import json
from docx import Document
from docx.shared import Pt, Inches
from docx.oxml.ns import qn
from docx.enum.text import WD_ALIGN_PARAGRAPH
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.enums import TA_LEFT, TA_CENTER

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

company_business = {
    "阿里巴巴集团": ["云计算服务", "电商平台", "数字媒体", "人工智能"],
    "腾讯控股": ["社交网络", "游戏开发", "云计算", "金融科技"],
    "百度集团": ["搜索引擎", "人工智能", "自动驾驶", "云计算"],
    "京东集团": ["电商平台", "物流服务", "科技创新"],
    "华为技术有限公司": ["通信设备", "智能手机", "云计算", "芯片设计"],
    "小米集团": ["智能手机", "物联网设备", "智能家居", "电子产品"],
    "字节跳动": ["社交媒体", "短视频平台", "人工智能", "内容平台"],
    "网易集团": ["网络游戏", "在线教育", "音乐流媒体", "电商"],
    "拼多多": ["社交电商", "农产品平台", "性价比商品"],
    "美团": ["本地生活服务", "外卖平台", "旅游预订", "酒店预订"],
    "中国工商银行": ["银行服务", "金融产品", "资产管理", "国际结算"],
    "中国建设银行": ["银行服务", "住房金融", "投资银行", "保险"],
    "中国农业银行": ["银行服务", "农村金融", "农业贷款", "扶贫金融"],
    "中国银行": ["银行服务", "外汇业务", "国际贸易", "跨境金融"],
    "中国移动": ["移动通信", "5G服务", "物联网", "数字内容"],
    "中国电信": ["固定电话", "宽带服务", "云计算", "卫星通信"],
    "中国联通": ["移动通信", "宽带服务", "物联网", "大数据"],
    "中国石油天然气集团": ["石油勘探", "天然气开发", "炼油化工", "管道运输"],
    "中国石油化工集团": ["石油化工", "化肥生产", "合成材料", "新能源"],
    "中国建筑集团": ["建筑工程", "房地产", "基础设施", "建筑设计"],
    "中国中铁": ["铁路建设", "桥梁隧道", "轨道交通", "工程设备"],
    "中国铁建": ["铁路建设", "公路工程", "城市轨道", "海外工程"],
    "中国中车": ["轨道交通装备", "高速列车", "城市地铁", "铁路货车"],
    "中国航天科技集团": ["航天器制造", "火箭发射", "卫星应用", "航天材料"],
    "中国航天科工集团": ["防务系统", "航天科工", "信息技术", "装备制造"],
    "苹果公司": ["智能手机", "电脑设备", "可穿戴设备", "软件服务"],
    "微软公司": ["操作系统", "办公软件", "云计算", "企业服务"],
    "谷歌公司": ["搜索引擎", "在线广告", "云计算", "人工智能"],
    "亚马逊公司": ["电商平台", "云计算服务", "人工智能", "物流服务"],
    "Meta": ["社交网络", "虚拟现实", "人工智能", "数字广告"],
    "特斯拉公司": ["电动汽车", "自动驾驶", "能源存储", "太阳能"],
    "英特尔公司": ["芯片制造", "处理器设计", "数据中心", "物联网"],
    "IBM": ["企业服务", "云计算", "人工智能", "量子计算"],
    "甲骨文公司": ["数据库软件", "企业软件", "云计算", "咨询服务"],
    "思科系统": ["网络设备", "网络安全", "云计算", "物联网"],
    "惠普公司": ["个人电脑", "打印机", "企业服务", "工作站"],
    "戴尔科技": ["电脑设备", "服务器", "存储设备", "网络安全"],
    "通用电气": ["工业设备", "航空发动机", "能源设备", "医疗设备"],
    "福特汽车": ["汽车制造", "电动汽车", "自动驾驶", "移动服务"],
    "通用汽车": ["汽车制造", "电动汽车", "自动驾驶", "共享出行"],
    "波音公司": ["民航飞机", "军用飞机", "航天器", "卫星系统"],
    "辉瑞公司": ["生物制药", "疫苗研发", "创新药物", "医疗器械"],
    "强生公司": ["医疗健康", "制药业务", "医疗器械", "消费品"],
    "摩根大通": ["投资银行", "商业银行", "资产管理", "金融科技"],
    "高盛集团": ["投资银行", "证券交易", "资产管理", "并购咨询"],
    "花旗集团": ["银行服务", "信用卡", "投资银行", "贸易金融"],
    "瑞银集团": ["财富管理", "投资银行", "资产管理", "全球金融服务"],
    "德意志银行": ["企业银行", "投资银行", "资产管理", "私募股权"],
    "西门子": ["工业自动化", "医疗设备", "能源管理", "数字化工厂"],
    "宝马集团": ["豪华汽车", "电动汽车", "摩托车", "出行服务"]
}

business_products = {
    "云计算服务": ["云服务器", "云存储", "CDN加速", "数据库服务", "网络安全设备"],
    "电商平台": ["电商平台服务", "支付系统", "物流管理系统", "大数据分析工具"],
    "数字媒体": ["视频流媒体服务", "数字内容平台", "在线广告系统"],
    "人工智能": ["AI芯片", "机器学习平台", "智能语音助手", "计算机视觉系统"],
    "社交网络": ["社交平台服务器", "即时通讯系统", "内容审核系统"],
    "游戏开发": ["游戏服务器", "游戏引擎", "虚拟现实设备", "云游戏平台"],
    "金融科技": ["支付系统", "风控系统", "区块链平台", "智能客服"],
    "搜索引擎": ["搜索引擎服务器", "自然语言处理系统", "知识图谱系统"],
    "自动驾驶": ["自动驾驶芯片", "激光雷达", "高精度地图", "车载计算平台"],
    "通信设备": ["5G基站", "光通信设备", "交换机", "路由器"],
    "智能手机": ["智能手机配件", "屏幕模组", "摄像头模组", "电池"],
    "物联网设备": ["传感器", "智能网关", "物联网模组", "边缘计算设备"],
    "智能家居": ["智能音箱", "智能灯泡", "智能门锁", "家庭网关"],
    "短视频平台": ["视频处理服务器", "内容推荐系统", "直播设备"],
    "在线教育": ["在线教育平台", "智能白板", "学习管理系统"],
    "音乐流媒体": ["音乐流媒体服务", "音频处理设备", "版权管理系统"],
    "本地生活服务": ["本地服务平台", "智能调度系统", "商家管理系统"],
    "外卖平台": ["外卖配送系统", "智能派单系统", "实时定位设备"],
    "旅游预订": ["旅游预订平台", "酒店管理系统", "景区智能设备"],
    "银行服务": ["银行核心系统", "ATM机", "智能柜台", "风控系统"],
    "金融产品": ["理财系统", "保险管理系统", "资产评估系统"],
    "资产管理": ["资产管理系统", "投资分析系统", "量化交易平台"],
    "移动通信": ["移动通信设备", "5G手机", "基站设备", "核心网设备"],
    "5G服务": ["5G基站设备", "5G手机", "网络规划系统", "信号测试设备"],
    "新能源": ["光伏组件", "储能系统", "充电桩", "氢能源设备"],
    "石油勘探": ["石油勘探设备", "钻井系统", "地质分析仪器", "油田服务设备"],
    "天然气开发": ["天然气液化设备", "燃气轮机", "管道监测系统"],
    "炼油化工": ["炼油设备", "化工反应器", "分离设备", "催化剂"],
    "管道运输": ["智能管道系统", "防腐材料", "管道检测机器人", "SCADA系统"],
    "石油化工": ["化工产品", "塑料粒子", "合成橡胶", "化肥"],
    "合成材料": ["高性能纤维", "复合材料", "工程塑料", "特种橡胶"],
    "建筑工程": ["建筑材料", "施工设备", "BIM系统", "建筑机器人"],
    "房地产": ["智能楼宇系统", "物业管理平台", "安防监控系统"],
    "基础设施": ["桥梁构件", "隧道设备", "道路材料", "管廊系统"],
    "建筑设计": ["设计软件", "渲染工作站", "3D打印设备"],
    "铁路建设": ["铁路施工设备", "轨道材料", "桥梁构件", "隧道掘进机"],
    "桥梁隧道": ["桥梁监测系统", "隧道照明设备", "防水材料", "加固系统"],
    "轨道交通": ["轨道交通信号系统", "接触网设备", "轨道车辆配件"],
    "工程设备": ["盾构机", "架桥机", "起重机", "混凝土设备"],
    "轨道交通装备": ["列车车厢", "转向架", "牵引系统", "制动系统"],
    "高速列车": ["高速列车", "高铁信号系统", "轨道无缝钢轨", "高速道岔"],
    "城市地铁": ["地铁车辆", "屏蔽门系统", "综合监控系统", "售检票系统"],
    "铁路货车": ["铁路货车车厢", "转向架", "制动装置", "连接器"],
    "航天器制造": ["卫星平台", "推进系统", "太阳能电池板", "姿态控制系统"],
    "火箭发射": ["运载火箭", "发射控制系统", "地面支持设备", "遥测系统"],
    "卫星应用": ["卫星通信终端", "导航模块", "遥感数据处理系统", "卫星电话"],
    "航天材料": ["碳纤维复合材料", "高温合金", "特种涂料", "密封材料"],
    "防务系统": ["雷达系统", "导弹防御系统", "指挥控制系统", "电子战设备"],
    "信息技术": ["信息安全设备", "数据加密系统", "网络战设备"],
    "装备制造": ["精密加工设备", "数控机床", "工业机器人", "3D打印设备"],
    "电脑设备": ["台式机", "笔记本电脑", "工作站", "显示器"],
    "可穿戴设备": ["智能手表", "无线耳机", "AR眼镜", "健康监测设备"],
    "软件服务": ["企业软件", "云服务平台", "开发者工具"],
    "操作系统": ["操作系统软件", "办公套件", "开发工具"],
    "办公软件": ["办公软件订阅", "云协作平台", "企业通讯软件"],
    "企业服务": ["企业资源规划系统", "客户关系管理系统", "供应链管理系统"],
    "在线广告": ["广告投放平台", "数据分析系统", "精准营销系统"],
    "电动汽车": ["动力电池", "电机控制器", "车载充电机", "热管理系统"],
    "能源存储": ["储能电池系统", "电池管理系统", "能量回收系统"],
    "太阳能": ["光伏逆变器", "太阳能电池板", "支架系统", "监控系统"],
    "芯片制造": ["光刻机", "刻蚀设备", "沉积设备", "检测设备"],
    "处理器设计": ["CPU芯片", "GPU芯片", "AI加速器", "FPGA芯片"],
    "数据中心": ["服务器", "存储系统", "网络设备", "冷却系统"],
    "数据库软件": ["数据库管理系统", "数据仓库", "数据分析平台"],
    "企业软件": ["ERP系统", "CRM系统", "人力资源系统", "财务系统"],
    "咨询服务": ["管理咨询系统", "数据分析服务", "数字化转型咨询"],
    "网络设备": ["交换机", "路由器", "防火墙", "无线接入点"],
    "网络安全": ["入侵检测系统", "安全审计系统", "加密设备", "身份认证系统"],
    "个人电脑": ["台式电脑", "笔记本电脑", "平板电脑", "配件"],
    "打印机": ["激光打印机", "喷墨打印机", "多功能一体机", "扫描仪"],
    "工作站": ["图形工作站", "计算工作站", "存储工作站"],
    "服务器": ["机架式服务器", "塔式服务器", "刀片服务器", "超融合系统"],
    "存储设备": ["企业级硬盘", "SAN存储", "NAS存储", "备份系统"],
    "工业设备": ["工业电机", "变频器", "PLC控制系统", "工业传感器"],
    "航空发动机": ["涡扇发动机", "涡轴发动机", "发动机控制系统", "维修设备"],
    "能源设备": ["燃气轮机", "蒸汽轮机", "发电机", "变压器"],
    "医疗设备": ["医学影像设备", "体外诊断设备", "手术机器人", "监护设备"],
    "汽车制造": ["汽车零部件", "冲压设备", "焊接设备", "涂装设备"],
    "移动服务": ["车联网系统", "共享出行平台", "自动驾驶系统"],
    "民航飞机": ["民航客机", "飞机发动机", "航电系统", "座椅系统"],
    "军用飞机": ["战斗机", "运输机", "预警机", "无人机"],
    "航天器": ["卫星平台", "空间站模块", "载人飞船", "货运飞船"],
    "卫星系统": ["通信卫星", "导航卫星", "遥感卫星", "卫星地面站"],
    "生物制药": ["生物制剂", "疫苗", "抗体药物", "细胞治疗产品"],
    "疫苗研发": ["疫苗生产设备", "冷链运输系统", "实验室设备"],
    "创新药物": ["新药研发服务", "临床试验系统", "药物筛选设备"],
    "医疗器械": ["植入物", "诊断试剂", "医疗机器人", "康复设备"],
    "医疗健康": ["医疗信息系统", "远程诊疗平台", "健康管理软件"],
    "制药业务": ["原料药", "制剂设备", "包装设备", "质量检测设备"],
    "消费品": ["保健品", "化妆品", "个人护理产品", "母婴用品"],
    "投资银行": ["并购咨询系统", "估值分析系统", "项目管理系统"],
    "证券交易": ["证券交易系统", "量化投资平台", "风险管理系统"],
    "商业银行": ["核心银行系统", "信贷管理系统", "反洗钱系统"],
    "财富管理": ["财富管理系统", "投资顾问系统", "客户关系管理系统"],
    "全球金融服务": ["跨境支付系统", "外汇交易系统", "全球资金管理系统"],
    "企业银行": ["企业信贷系统", "现金管理系统", "贸易融资系统"],
    "私募股权": ["私募管理系统", "项目估值系统", "投资者关系系统"],
    "工业自动化": ["工业控制系统", "SCADA系统", "MES系统", "工业物联网"],
    "数字化工厂": ["数字孪生系统", "智能排产系统", "质量追溯系统"],
    "豪华汽车": ["豪华轿车", "SUV车型", "高性能发动机", "智能驾驶系统"],
    "摩托车": ["摩托车", "电动摩托车", "骑行装备", "维修设备"],
    "出行服务": ["汽车共享平台", "充电服务网络", "车联网服务平台"]
}

product_categories = {
    "科技/互联网": ["云计算服务", "电商平台", "数字媒体", "人工智能", "社交网络", "游戏开发", "金融科技", "搜索引擎", "自动驾驶", "短视频平台", "在线教育", "音乐流媒体", "本地生活服务", "外卖平台", "旅游预订", "在线广告", "软件服务", "操作系统", "办公软件", "企业服务", "数据库软件", "企业软件", "咨询服务"],
    "通信/电子": ["通信设备", "智能手机", "物联网设备", "智能家居", "移动通信", "5G服务", "芯片制造", "处理器设计", "数据中心", "网络设备", "网络安全", "个人电脑", "工作站", "服务器", "存储设备", "打印机", "电脑设备", "可穿戴设备"],
    "工业/制造": ["新能源", "石油勘探", "天然气开发", "炼油化工", "管道运输", "石油化工", "合成材料", "建筑工程", "房地产", "基础设施", "建筑设计", "铁路建设", "桥梁隧道", "轨道交通", "工程设备", "轨道交通装备", "高速列车", "城市地铁", "铁路货车", "航天器制造", "火箭发射", "卫星应用", "航天材料", "防务系统", "信息技术", "装备制造", "工业设备", "航空发动机", "能源设备", "工业自动化", "数字化工厂"],
    "汽车/交通": ["电动汽车", "能源存储", "太阳能", "汽车制造", "移动服务", "豪华汽车", "摩托车", "出行服务", "民航飞机", "军用飞机", "航天器", "卫星系统"],
    "医疗健康": ["生物制药", "疫苗研发", "创新药物", "医疗器械", "医疗健康", "制药业务", "消费品"],
    "金融": ["银行服务", "金融产品", "资产管理", "投资银行", "证券交易", "商业银行", "财富管理", "全球金融服务", "企业银行", "私募股权"],
    "消费/零售": ["电商平台", "社交电商", "性价比商品", "本地生活服务"]
}

def get_company_business_products(company_name):
    if company_name in company_business:
        businesses = company_business[company_name]
        all_products = []
        for business in businesses:
            if business in business_products:
                all_products.extend(business_products[business])
        if all_products:
            return all_products
    return ["工业品", "原材料", "设备配件", "办公用品", "电子元器件"]

document_types = ["销售合同", "采购合同", "付款凭证", "应付发票", "应收发票", "收款单"]

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

def generate_contract_id(prefix, company_abbr, seq_num):
    date_str = "2026"
    return f"{prefix}-{company_abbr}-{date_str}-{seq_num:04d}"

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

def create_docx_file(file_path, content_lines):
    doc = Document()
    for line in content_lines:
        if line.strip():
            p = doc.add_paragraph(line)
            p.paragraph_format.space_after = Pt(3)
    doc.save(file_path)

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

    style = ParagraphStyle('Custom', parent=styles['Normal'], fontName=font_name, fontSize=10, leading=14, alignment=TA_LEFT)
    story = []
    for line in content_lines:
        if line.strip():
            story.append(Paragraph(line, style))
            story.append(Spacer(1, 3))
        else:
            story.append(Spacer(1, 6))
    doc.build(story)

def generate_sales_transaction(seller, buyer, amount, date, seq_num, products):
    seller_abbr = get_company_abbr(seller)
    buyer_abbr = get_company_abbr(buyer)
    chinese_amount = amount_to_chinese(amount)
    product = random.choice(products)

    contract_id = generate_contract_id("SC", seller_abbr, seq_num)
    invoice_id = generate_contract_id("RI", seller_abbr, seq_num)
    receipt_id = generate_contract_id("RS", seller_abbr, seq_num)

    delivery_date = date + datetime.timedelta(days=random.randint(15, 45))
    invoice_date = delivery_date + datetime.timedelta(days=random.randint(3, 7))
    receipt_date = invoice_date + datetime.timedelta(days=random.randint(1, 3))

    tax_amount = round(amount / 1.13 * 0.13, 2)
    net_amount = round(amount - tax_amount, 2)

    banks = ["中国工商银行", "中国建设银行", "中国农业银行", "中国银行"]

    sales_contract_content = [
        f"销售合同（{seller}）",
        f"合同编号：{contract_id}",
        f"甲方（卖方）：{seller}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"法定代表人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}",
        f"地址：广东省深圳市南山区科技园路{random.randint(1,99)}号",
        f"联系人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])} 联系电话：{generate_phone()}",
        f"乙方（买方）：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"法定代表人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"联系人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])} 联系电话：{generate_phone()}",
        "依据《中华人民共和国民法典》及相关法律法规，甲乙双方本着平等自愿、公平诚信、互利共赢的原则，就甲方向乙方销售产品事宜，经友好协商，订立本合同，以资共同信守。",
        f"一、销售标的：甲方向乙方销售{product}（产品名称、规格型号、数量、单价详见附件），合同总价款为人民币{amount}元（大写：{chinese_amount}），该价款为含税总价，包含货物成本、包装、运输、装卸、税费及售后服务等全部费用，乙方无需额外支付其他费用。",
        "二、质量标准：甲方提供的货物需符合国家相关标准、产品说明书要求及乙方约定标准，确保为全新、未使用过的合格产品，无质量瑕疵、无破损，提供完整的质量合格证明及专业检测报告。",
        f"三、交付与验收：甲方应于{delivery_date.year}年{delivery_date.month}月{delivery_date.day}日前，将货物送至乙方指定地点，负责货物的装卸、摆放及初步调试。乙方在收到货物后7个工作日内完成验收，验收合格后签署验收单；若发现货物质量、数量、规格不符合约定，乙方有权要求甲方在3个工作日内更换或补足，相关费用由甲方承担。",
        "四、付款方式：合同签订后5个工作日内，乙方向甲方支付合同总价款的20%作为预付款；货物验收合格并完成初步调试后10个工作日内，乙方支付至合同总价款的95%；剩余5%作为质量保证金，自验收合格之日起12个月内无质量问题，乙方无息一次性支付。甲方需在付款前提供合法有效的增值税专用发票，否则乙方有权顺延付款。",
        "五、售后服务：甲方承诺货物质保期为12个月，质保期内出现质量问题，甲方应在收到乙方通知后24小时内响应，48小时内派员到场维修或更换，维修费用由甲方承担；质保期结束后，甲方需提供长期优惠维修服务及配件供应。",
        "六、违约责任：甲方逾期交付货物，每逾期一日按合同总价款的0.5‰支付违约金，逾期超过15日，乙方有权解除合同并要求甲方赔偿损失；甲方提供的货物质量不合格，应承担更换、退货及乙方因此遭受的全部损失；乙方逾期付款，每逾期一日按未付款项的0.5‰支付违约金。",
        "七、争议解决：本合同履行中发生的争议，双方协商解决；协商不成的，提交乙方所在地有管辖权的人民法院诉讼解决。",
        "八、其他：本合同附件为本合同不可分割的组成部分，与本合同具有同等法律效力；本合同一式肆份，甲乙双方各执贰份，自双方签字盖章之日起生效，履行完毕后自动终止。",
        f"甲方（盖章）：{seller}",
        "法定代表人/授权代表（签字）：________________________",
        f"签订日期：{date.year}年{date.month}月{date.day}日",
        f"乙方（盖章）：{buyer}",
        "法定代表人/授权代表（签字）：________________________",
        f"签订日期：{date.year}年{date.month}月{date.day}日",
    ]

    receivable_invoice_content = [
        "增值税专用发票",
        f"发票号码：{invoice_id}",
        f"开票日期：{invoice_date.year}年{invoice_date.month}月{invoice_date.day}日",
        f"相关合同编号：{contract_id}",
        f"购买方：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"电话：{generate_phone()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"销售方：{seller}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"地址：广东省深圳市南山区科技园路{random.randint(1,99)}号",
        f"电话：{generate_phone()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"货物或应税劳务、服务名称：{product}",
        f"规格型号：{random.randint(1000,9999)}",
        "单位：台",
        f"数量：{random.randint(1, 50)}",
        f"单价：{round(net_amount / random.randint(1, 50), 2)}",
        f"金额：{net_amount}",
        "税率：13%",
        f"税额：{tax_amount}",
        f"价税合计（大写）：{chinese_amount}",
        f"价税合计（小写）：￥{amount}",
        "备注：本发票为应收账款凭证，请注意及时催收。",
        f"销售方（盖章）：{seller}",
        f"开票人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])}",
        f"复核人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}",
    ]

    receipt_slip_content = [
        "收款单",
        f"单据编号：{receipt_id}",
        f"收款日期：{receipt_date.year}年{receipt_date.month}月{receipt_date.day}日",
        f"相关合同编号：{contract_id}",
        f"相关发票号码：{invoice_id}",
        f"收款方：{seller}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"付款方：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"收款金额：人民币{amount}元（大写：{chinese_amount}）",
        f"收款用途：销售货款（合同编号：{contract_id}）",
        "收款方式：银行转账",
        f"转账凭证号：{random.randint(10000000000000000000, 99999999999999999999)}",
        "备注：本收款单一式两份，收款方和付款方各执一份，作为财务记账凭证。",
        f"收款方（盖章）：{seller}",
        "财务负责人（签字）：________________________",
        f"日期：{receipt_date.year}年{receipt_date.month}月{receipt_date.day}日",
    ]

    return {
        "sales_contract": {"content": sales_contract_content, "file_name": f"销售合同（{seller}）-{date.strftime('%Y%m%d')}-{seq_num}"},
        "receivable_invoice": {"content": receivable_invoice_content, "file_name": f"应收发票（{seller}）-{invoice_date.strftime('%Y%m%d')}-{seq_num}"},
        "receipt_slip": {"content": receipt_slip_content, "file_name": f"收款单（{seller}）-{receipt_date.strftime('%Y%m%d')}-{seq_num}"}
    }

def generate_purchase_transaction(buyer, seller, amount, date, seq_num, products):
    buyer_abbr = get_company_abbr(buyer)
    seller_abbr = get_company_abbr(seller)
    chinese_amount = amount_to_chinese(amount)
    product = random.choice(products)

    contract_id = generate_contract_id("PC", buyer_abbr, seq_num)
    invoice_id = generate_contract_id("PI", buyer_abbr, seq_num)
    payment_id = generate_contract_id("PV", buyer_abbr, seq_num)

    delivery_date = date + datetime.timedelta(days=random.randint(15, 45))
    invoice_date = delivery_date + datetime.timedelta(days=random.randint(3, 7))
    payment_date = invoice_date + datetime.timedelta(days=random.randint(1, 3))

    tax_amount = round(amount / 1.13 * 0.13, 2)
    net_amount = round(amount - tax_amount, 2)

    banks = ["中国工商银行", "中国建设银行", "中国农业银行", "中国银行"]

    purchase_contract_content = [
        f"采购合同（{buyer}）",
        f"合同编号：{contract_id}",
        f"甲方（采购方）：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"法定代表人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}",
        f"地址：广东省深圳市南山区科技园路{random.randint(1,99)}号",
        f"联系人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])} 联系电话：{generate_phone()}",
        f"乙方（供应方）：{seller}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"法定代表人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])}",
        f"地址：广东省深圳市光明新区塘明大道{random.randint(1,20)}-{random.randint(1,10)}号",
        f"联系人：{random.choice(['张三', '李四', '王五', '赵六', '钱七'])} 联系电话：{generate_phone()}",
        "依据《中华人民共和国民法典》及相关法律法规，甲乙双方本着平等自愿、公平诚信、互利共赢的原则，就甲方向乙方采购产品事宜，经友好协商，订立本合同，以资共同信守。",
        f"一、采购标的：甲方向乙方采购{product}（产品名称、规格型号、数量、单价详见附件），合同总价款为人民币{amount}元（大写：{chinese_amount}），该价款为含税总价，包含货物成本、包装、运输、装卸、税费及售后服务等全部费用，甲方无需额外支付其他费用。",
        "二、质量标准：乙方提供的货物需符合国家相关标准、产品说明书要求及甲方约定标准，确保为全新、未使用过的合格产品，无质量瑕疵、无破损，提供完整的质量合格证明及专业检测报告。",
        f"三、交付与验收：乙方应于{delivery_date.year}年{delivery_date.month}月{delivery_date.day}日前，将货物送至甲方指定地点，负责货物的装卸、摆放及初步调试。甲方在收到货物后7个工作日内完成验收，验收合格后签署验收单；若发现货物质量、数量、规格不符合约定，甲方有权要求乙方在3个工作日内更换或补足，相关费用由乙方承担。",
        "四、付款方式：合同签订后5个工作日内，甲方向乙方支付合同总价款的20%作为预付款；货物验收合格并完成初步调试后10个工作日内，甲方支付至合同总价款的95%；剩余5%作为质量保证金，自验收合格之日起12个月内无质量问题，甲方无息一次性支付。乙方需在付款前提供合法有效的增值税专用发票，否则甲方有权顺延付款。",
        "五、售后服务：乙方承诺货物质保期为12个月，质保期内出现质量问题，乙方应在收到甲方通知后24小时内响应，48小时内派员到场维修或更换，维修费用由乙方承担；质保期结束后，乙方需提供长期优惠维修服务及配件供应。",
        "六、违约责任：乙方逾期交付货物，每逾期一日按合同总价款的0.5‰支付违约金，逾期超过15日，甲方有权解除合同并要求乙方赔偿损失；乙方提供的货物质量不合格，应承担更换、退货及甲方因此遭受的全部损失；甲方逾期付款，每逾期一日按未付款项的0.5‰支付违约金。",
        "七、争议解决：本合同履行中发生的争议，双方协商解决；协商不成的，提交甲方所在地有管辖权的人民法院诉讼解决。",
        "八、其他：本合同附件为本合同不可分割的组成部分，与本合同具有同等法律效力；本合同一式肆份，甲乙双方各执贰份，自双方签字盖章之日起生效，履行完毕后自动终止。",
        f"甲方（盖章）：{buyer}",
        "法定代表人/授权代表（签字）：________________________",
        f"签订日期：{date.year}年{date.month}月{date.day}日",
        f"乙方（盖章）：{seller}",
        "法定代表人/授权代表（签字）：________________________",
        f"签订日期：{date.year}年{date.month}月{date.day}日",
    ]

    payable_invoice_content = [
        "增值税专用发票",
        f"发票号码：{invoice_id}",
        f"开票日期：{invoice_date.year}年{invoice_date.month}月{invoice_date.day}日",
        f"相关合同编号：{contract_id}",
        f"购买方：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"地址：广东省深圳市南山区科技园路{random.randint(1,99)}号",
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
        "单位：台",
        f"数量：{random.randint(1, 50)}",
        f"单价：{round(net_amount / random.randint(1, 50), 2)}",
        f"金额：{net_amount}",
        "税率：13%",
        f"税额：{tax_amount}",
        f"价税合计（大写）：{chinese_amount}",
        f"价税合计（小写）：￥{amount}",
        "备注：本发票为应付账款凭证，请注意及时入账。",
        f"销售方（盖章）：{seller}",
        f"开票人：{random.choice(['小李', '小王', '小张', '小刘', '小陈'])}",
        f"复核人：{random.choice(['张总', '李总', '王总', '赵总', '刘总'])}",
    ]

    payment_voucher_content = [
        "付款凭证",
        f"凭证编号：{payment_id}",
        f"付款日期：{payment_date.year}年{payment_date.month}月{payment_date.day}日",
        f"相关合同编号：{contract_id}",
        f"相关发票号码：{invoice_id}",
        f"付款方：{buyer}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"收款方：{seller}",
        f"统一社会信用代码：{generate_credit_code()}",
        f"开户银行：{random.choice(banks)}",
        f"银行账号：{random.randint(1000000000000000000, 9999999999999999999)}",
        f"付款金额：人民币{amount}元（大写：{chinese_amount}）",
        f"付款用途：采购货款（合同编号：{contract_id}）",
        "付款方式：银行转账",
        f"转账凭证号：{random.randint(10000000000000000000, 99999999999999999999)}",
        "备注：本付款凭证一式两份，付款方和收款方各执一份，作为财务记账凭证。",
        f"付款方（盖章）：{buyer}",
        "财务负责人（签字）：________________________",
        f"日期：{payment_date.year}年{payment_date.month}月{payment_date.day}日",
    ]

    return {
        "purchase_contract": {"content": purchase_contract_content, "file_name": f"采购合同（{buyer}）-{date.strftime('%Y%m%d')}-{seq_num}"},
        "payable_invoice": {"content": payable_invoice_content, "file_name": f"应付发票（{buyer}）-{invoice_date.strftime('%Y%m%d')}-{seq_num}"},
        "payment_voucher": {"content": payment_voucher_content, "file_name": f"付款凭证（{buyer}）-{payment_date.strftime('%Y%m%d')}-{seq_num}"}
    }

def main():
    output_dir = "D:/AI project/AI-search/测试文件目录"
    total_transactions = 500

    os.makedirs(output_dir, exist_ok=True)
    print(f"开始生成{total_transactions * 2}个文档（每笔交易包含3个相关文档）...")
    print(f"输出目录: {output_dir}")

    tasks = []
    seq_num = 1

    for i in range(total_transactions):
        is_sales = random.choice([True, False])
        seller, buyer = random.sample(companies, 2)
        amount = generate_amount()
        date = generate_date()
        products = get_company_business_products(seller if is_sales else seller)

        if is_sales:
            transaction = generate_sales_transaction(seller, buyer, amount, date, seq_num, products)
            for key, doc in transaction.items():
                tasks.append({
                    "transaction_type": "销售交易",
                    "transaction_seq": seq_num,
                    "doc_type": key.replace("_", ""),
                    "seller": seller,
                    "buyer": buyer,
                    "file_name": doc["file_name"],
                    "amount": amount,
                    "contract_id": transaction["sales_contract"]["file_name"]
                })
                docx_path = os.path.join(output_dir, f"{doc['file_name']}.docx")
                pdf_path = os.path.join(output_dir, f"{doc['file_name']}.pdf")
                create_docx_file(docx_path, doc["content"])
                create_pdf_file(pdf_path, doc["content"])
        else:
            transaction = generate_purchase_transaction(buyer, seller, amount, date, seq_num, products)
            for key, doc in transaction.items():
                tasks.append({
                    "transaction_type": "采购交易",
                    "transaction_seq": seq_num,
                    "doc_type": key.replace("_", ""),
                    "seller": seller,
                    "buyer": buyer,
                    "file_name": doc["file_name"],
                    "amount": amount,
                    "contract_id": transaction["purchase_contract"]["file_name"]
                })
                docx_path = os.path.join(output_dir, f"{doc['file_name']}.docx")
                pdf_path = os.path.join(output_dir, f"{doc['file_name']}.pdf")
                create_docx_file(docx_path, doc["content"])
                create_pdf_file(pdf_path, doc["content"])

        seq_num += 1

        if (i + 1) % 50 == 0:
            print(f"已生成{(i + 1) * 2}个文档")

    print(f"生成完成！共生成{total_transactions * 2}个DOCX文件和{total_transactions * 2}个PDF文件。")

    task_file = os.path.join(output_dir, "generation_tasks.json")
    with open(task_file, 'w', encoding='utf-8') as f:
        json.dump(tasks, f, ensure_ascii=False, indent=2)
    print(f"任务清单已保存到: {task_file}")

if __name__ == "__main__":
    main()
