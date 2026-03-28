import requests
import json

# API基础URL
BASE_URL = "http://localhost:8080/api/base-data/company-projects"

# 国内外知名公司列表
COMPANIES = [
    # 国内公司
    {"name": "阿里巴巴集团", "code": "ALIBAABA", "country": "CN", "area": "中国-浙江-杭州", "orgs": [{"category": "总部", "code": "ALI-HQ", "name": "阿里巴巴集团总部", "valid": "Y"}]},
    {"name": "腾讯控股", "code": "TENCENT", "country": "CN", "area": "中国-广东-深圳", "orgs": [{"category": "总部", "code": "TENCENT-HQ", "name": "腾讯控股总部", "valid": "Y"}]},
    {"name": "百度公司", "code": "BAIDU", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "BAIDU-HQ", "name": "百度公司总部", "valid": "Y"}]},
    {"name": "京东集团", "code": "JD", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "JD-HQ", "name": "京东集团总部", "valid": "Y"}]},
    {"name": "华为技术有限公司", "code": "HUAWEI", "country": "CN", "area": "中国-广东-深圳", "orgs": [{"category": "总部", "code": "HUAWEI-HQ", "name": "华为技术有限公司总部", "valid": "Y"}]},
    {"name": "小米科技有限责任公司", "code": "XIAOMI", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "XIAOMI-HQ", "name": "小米科技有限责任公司总部", "valid": "Y"}]},
    {"name": "字节跳动", "code": "BYTEDANCE", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "BYTEDANCE-HQ", "name": "字节跳动总部", "valid": "Y"}]},
    {"name": "网易公司", "code": "NETEASE", "country": "CN", "area": "中国-浙江-杭州", "orgs": [{"category": "总部", "code": "NETEASE-HQ", "name": "网易公司总部", "valid": "Y"}]},
    {"name": "拼多多", "code": "PDD", "country": "CN", "area": "中国-上海", "orgs": [{"category": "总部", "code": "PDD-HQ", "name": "拼多多总部", "valid": "Y"}]},
    {"name": "美团点评", "code": "MEITUAN", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "MEITUAN-HQ", "name": "美团点评总部", "valid": "Y"}]},
    {"name": "中国工商银行", "code": "ICBC", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "ICBC-HQ", "name": "中国工商银行总部", "valid": "Y"}]},
    {"name": "中国建设银行", "code": "CCB", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CCB-HQ", "name": "中国建设银行总部", "valid": "Y"}]},
    {"name": "中国农业银行", "code": "ABC", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "ABC-HQ", "name": "中国农业银行总部", "valid": "Y"}]},
    {"name": "中国银行", "code": "BOC", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "BOC-HQ", "name": "中国银行总部", "valid": "Y"}]},
    {"name": "中国移动", "code": "CMCC", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CMCC-HQ", "name": "中国移动总部", "valid": "Y"}]},
    {"name": "中国电信", "code": "CHINA-TELECOM", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CHINA-TELECOM-HQ", "name": "中国电信总部", "valid": "Y"}]},
    {"name": "中国联通", "code": "CHINA-UNICOM", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CHINA-UNICOM-HQ", "name": "中国联通总部", "valid": "Y"}]},
    {"name": "中国石油天然气集团", "code": "CNPC", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CNPC-HQ", "name": "中国石油天然气集团总部", "valid": "Y"}]},
    {"name": "中国石油化工集团", "code": "SINOPEC", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "SINOPEC-HQ", "name": "中国石油化工集团总部", "valid": "Y"}]},
    {"name": "中国建筑集团", "code": "CHINA-CONSTRUCTION", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CHINA-CONSTRUCTION-HQ", "name": "中国建筑集团总部", "valid": "Y"}]},
    {"name": "中国中铁股份有限公司", "code": "CHINA-RAILWAY", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CHINA-RAILWAY-HQ", "name": "中国中铁股份有限公司总部", "valid": "Y"}]},
    {"name": "中国铁建股份有限公司", "code": "CHINA-RAILWAY-CONSTRUCTION", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CHINA-RAILWAY-CONSTRUCTION-HQ", "name": "中国铁建股份有限公司总部", "valid": "Y"}]},
    {"name": "中国中车集团", "code": "CRRC", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CRRC-HQ", "name": "中国中车集团总部", "valid": "Y"}]},
    {"name": "中国航天科技集团", "code": "CASC", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CASC-HQ", "name": "中国航天科技集团总部", "valid": "Y"}]},
    {"name": "中国航天科工集团", "code": "CASIC", "country": "CN", "area": "中国-北京", "orgs": [{"category": "总部", "code": "CASIC-HQ", "name": "中国航天科工集团总部", "valid": "Y"}]},
    # 国际公司
    {"name": "苹果公司", "code": "APPLE", "country": "US", "area": "美国-加利福尼亚州-库比蒂诺", "orgs": [{"category": "总部", "code": "APPLE-HQ", "name": "苹果公司总部", "valid": "Y"}]},
    {"name": "微软公司", "code": "MICROSOFT", "country": "US", "area": "美国-华盛顿州-雷德蒙德", "orgs": [{"category": "总部", "code": "MICROSOFT-HQ", "name": "微软公司总部", "valid": "Y"}]},
    {"name": "谷歌公司", "code": "GOOGLE", "country": "US", "area": "美国-加利福尼亚州-山景城", "orgs": [{"category": "总部", "code": "GOOGLE-HQ", "name": "谷歌公司总部", "valid": "Y"}]},
    {"name": "亚马逊公司", "code": "AMAZON", "country": "US", "area": "美国-华盛顿州-西雅图", "orgs": [{"category": "总部", "code": "AMAZON-HQ", "name": "亚马逊公司总部", "valid": "Y"}]},
    {"name": "脸书公司", "code": "FACEBOOK", "country": "US", "area": "美国-加利福尼亚州-门洛帕克", "orgs": [{"category": "总部", "code": "FACEBOOK-HQ", "name": "脸书公司总部", "valid": "Y"}]},
    {"name": "特斯拉公司", "code": "TESLA", "country": "US", "area": "美国-加利福尼亚州-帕洛阿尔托", "orgs": [{"category": "总部", "code": "TESLA-HQ", "name": "特斯拉公司总部", "valid": "Y"}]},
    {"name": "英特尔公司", "code": "INTEL", "country": "US", "area": "美国-加利福尼亚州-圣克拉拉", "orgs": [{"category": "总部", "code": "INTEL-HQ", "name": "英特尔公司总部", "valid": "Y"}]},
    {"name": "IBM公司", "code": "IBM", "country": "US", "area": "美国-纽约州-阿蒙克", "orgs": [{"category": "总部", "code": "IBM-HQ", "name": "IBM公司总部", "valid": "Y"}]},
    {"name": "甲骨文公司", "code": "ORACLE", "country": "US", "area": "美国-加利福尼亚州-红木城", "orgs": [{"category": "总部", "code": "ORACLE-HQ", "name": "甲骨文公司总部", "valid": "Y"}]},
    {"name": "思科系统公司", "code": "CISCO", "country": "US", "area": "美国-加利福尼亚州-圣何塞", "orgs": [{"category": "总部", "code": "CISCO-HQ", "name": "思科系统公司总部", "valid": "Y"}]},
    {"name": "惠普公司", "code": "HP", "country": "US", "area": "美国-加利福尼亚州-帕洛阿尔托", "orgs": [{"category": "总部", "code": "HP-HQ", "name": "惠普公司总部", "valid": "Y"}]},
    {"name": "戴尔公司", "code": "DELL", "country": "US", "area": "美国-德克萨斯州-圆石城", "orgs": [{"category": "总部", "code": "DELL-HQ", "name": "戴尔公司总部", "valid": "Y"}]},
    {"name": "通用电气公司", "code": "GE", "country": "US", "area": "美国-马萨诸塞州-波士顿", "orgs": [{"category": "总部", "code": "GE-HQ", "name": "通用电气公司总部", "valid": "Y"}]},
    {"name": "福特汽车公司", "code": "FORD", "country": "US", "area": "美国-密歇根州-迪尔伯恩", "orgs": [{"category": "总部", "code": "FORD-HQ", "name": "福特汽车公司总部", "valid": "Y"}]},
    {"name": "通用汽车公司", "code": "GM", "country": "US", "area": "美国-密歇根州-底特律", "orgs": [{"category": "总部", "code": "GM-HQ", "name": "通用汽车公司总部", "valid": "Y"}]},
    {"name": "波音公司", "code": "BOEING", "country": "US", "area": "美国-伊利诺伊州-芝加哥", "orgs": [{"category": "总部", "code": "BOEING-HQ", "name": "波音公司总部", "valid": "Y"}]},
    {"name": "辉瑞制药公司", "code": "PFIZER", "country": "US", "area": "美国-纽约州-纽约", "orgs": [{"category": "总部", "code": "PFIZER-HQ", "name": "辉瑞制药公司总部", "valid": "Y"}]},
    {"name": "强生公司", "code": "JNJ", "country": "US", "area": "美国-新泽西州-新布朗斯维克", "orgs": [{"category": "总部", "code": "JNJ-HQ", "name": "强生公司总部", "valid": "Y"}]},
    {"name": "摩根大通公司", "code": "JPMORGAN", "country": "US", "area": "美国-纽约州-纽约", "orgs": [{"category": "总部", "code": "JPMORGAN-HQ", "name": "摩根大通公司总部", "valid": "Y"}]},
    {"name": "高盛集团", "code": "GOLDMAN-SACHS", "country": "US", "area": "美国-纽约州-纽约", "orgs": [{"category": "总部", "code": "GOLDMAN-SACHS-HQ", "name": "高盛集团总部", "valid": "Y"}]},
    {"name": "花旗集团", "code": "CITI", "country": "US", "area": "美国-纽约州-纽约", "orgs": [{"category": "总部", "code": "CITI-HQ", "name": "花旗集团总部", "valid": "Y"}]},
    {"name": "瑞士银行", "code": "UBS", "country": "CH", "area": "瑞士-苏黎世", "orgs": [{"category": "总部", "code": "UBS-HQ", "name": "瑞士银行总部", "valid": "Y"}]},
    {"name": "瑞银集团", "code": "CREDIT-SUISSE", "country": "CH", "area": "瑞士-苏黎世", "orgs": [{"category": "总部", "code": "CREDIT-SUISSE-HQ", "name": "瑞银集团总部", "valid": "Y"}]},
    {"name": "德意志银行", "code": "DEUTSCHE-BANK", "country": "DE", "area": "德国-法兰克福", "orgs": [{"category": "总部", "code": "DEUTSCHE-BANK-HQ", "name": "德意志银行总部", "valid": "Y"}]},
    {"name": "西门子公司", "code": "SIEMENS", "country": "DE", "area": "德国-慕尼黑", "orgs": [{"category": "总部", "code": "SIEMENS-HQ", "name": "西门子公司总部", "valid": "Y"}]},
    {"name": "宝马公司", "code": "BMW", "country": "DE", "area": "德国-慕尼黑", "orgs": [{"category": "总部", "code": "BMW-HQ", "name": "宝马公司总部", "valid": "Y"}]},
    {"name": "大众汽车公司", "code": "VOLKSWAGEN", "country": "DE", "area": "德国-沃尔夫斯堡", "orgs": [{"category": "总部", "code": "VOLKSWAGEN-HQ", "name": "大众汽车公司总部", "valid": "Y"}]},
    {"name": "丰田汽车公司", "code": "TOYOTA", "country": "JP", "area": "日本-爱知县-丰田市", "orgs": [{"category": "总部", "code": "TOYOTA-HQ", "name": "丰田汽车公司总部", "valid": "Y"}]},
    {"name": "本田汽车公司", "code": "HONDA", "country": "JP", "area": "日本-东京都-港区", "orgs": [{"category": "总部", "code": "HONDA-HQ", "name": "本田汽车公司总部", "valid": "Y"}]},
    {"name": "索尼公司", "code": "SONY", "country": "JP", "area": "日本-东京都-港区", "orgs": [{"category": "总部", "code": "SONY-HQ", "name": "索尼公司总部", "valid": "Y"}]},
    {"name": "松下电器公司", "code": "PANASONIC", "country": "JP", "area": "日本-大阪府-门真市", "orgs": [{"category": "总部", "code": "PANASONIC-HQ", "name": "松下电器公司总部", "valid": "Y"}]}
]

# 发送GET请求获取所有公司/项目
def get_all_companies():
    response = requests.get(BASE_URL)
    if response.status_code == 200:
        data = response.json()
        return data.get('data', [])
    else:
        print(f"获取公司列表失败: {response.status_code}")
        return []

# 发送DELETE请求删除公司/项目
def delete_company(company_code):
    url = f"{BASE_URL}/{company_code}"
    response = requests.delete(url)
    if response.status_code == 200:
        print(f"删除公司 {company_code} 成功")
    else:
        print(f"删除公司 {company_code} 失败: {response.status_code}")

# 发送POST请求创建公司/项目
def create_company(company_data):
    lines = [
        {
            "orgCategory": org["category"],
            "organizationCode": org["code"],
            "organizationName": org["name"],
            "validFlag": org["valid"]
        }
        for org in company_data["orgs"]
    ]
    payload = {
        "companyProjectCode": company_data["code"],
        "companyProjectName": company_data["name"],
        "countryCode": company_data["country"],
        "managementArea": company_data["area"],
        "enabledFlag": "Y",
        "lines": lines
    }
    response = requests.post(BASE_URL, json=payload)
    if response.status_code == 200:
        print(f"创建公司 {company_data['name']} 成功")
    else:
        print(f"创建公司 {company_data['name']} 失败: {response.status_code}")
        print(f"错误信息: {response.text}")

# 主函数
def main():
    print("开始执行自动化测试...")
    
    # 1. 获取所有现有公司/项目
    print("\n1. 获取所有现有公司/项目...")
    companies = get_all_companies()
    print(f"当前共有 {len(companies)} 个公司/项目")
    
    # 2. 删除所有现有公司/项目
    print("\n2. 删除所有现有公司/项目...")
    for company in companies:
        company_code = company.get('companyProjectCode')
        if company_code:
            delete_company(company_code)
    
    # 3. 创建50个国内外知名公司
    print("\n3. 创建50个国内外知名公司...")
    for i, company in enumerate(COMPANIES):
        print(f"创建公司 {i+1}/{len(COMPANIES)}: {company['name']}")
        create_company(company)
    
    # 4. 验证创建结果
    print("\n4. 验证创建结果...")
    companies = get_all_companies()
    print(f"创建完成后共有 {len(companies)} 个公司/项目")
    
    print("\n自动化测试执行完成！")

if __name__ == "__main__":
    main()