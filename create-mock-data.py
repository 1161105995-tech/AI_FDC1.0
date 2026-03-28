import json
import os
import subprocess
import urllib.error
import urllib.request


BASE_URL = "http://localhost:8080/api/base-data/company-projects"
COUNTRY_DICTIONARY_URL = f"{BASE_URL}/dictionary/countries"
ORG_CATEGORY_DICTIONARY_URL = f"{BASE_URL}/dictionary/org-categories"

DB_NAME = "smart_archive"
DB_USER = "postgres"
DB_PASSWORD = "postgres"


COMPANIES = [
    {"name": "阿里巴巴集团", "code": "ALIBABA", "country": "CN", "area": "中国-浙江-杭州"},
    {"name": "腾讯控股", "code": "TENCENT", "country": "CN", "area": "中国-广东-深圳"},
    {"name": "百度集团", "code": "BAIDU", "country": "CN", "area": "中国-北京"},
    {"name": "京东集团", "code": "JD", "country": "CN", "area": "中国-北京"},
    {"name": "华为技术有限公司", "code": "HUAWEI", "country": "CN", "area": "中国-广东-深圳"},
    {"name": "小米集团", "code": "XIAOMI", "country": "CN", "area": "中国-北京"},
    {"name": "字节跳动", "code": "BYTEDANCE", "country": "CN", "area": "中国-北京"},
    {"name": "网易集团", "code": "NETEASE", "country": "CN", "area": "中国-浙江-杭州"},
    {"name": "拼多多", "code": "PDD", "country": "CN", "area": "中国-上海"},
    {"name": "美团", "code": "MEITUAN", "country": "CN", "area": "中国-北京"},
    {"name": "中国工商银行", "code": "ICBC", "country": "CN", "area": "中国-北京"},
    {"name": "中国建设银行", "code": "CCB", "country": "CN", "area": "中国-北京"},
    {"name": "中国农业银行", "code": "ABC", "country": "CN", "area": "中国-北京"},
    {"name": "中国银行", "code": "BOC", "country": "CN", "area": "中国-北京"},
    {"name": "中国移动", "code": "CMCC", "country": "CN", "area": "中国-北京"},
    {"name": "中国电信", "code": "CHINA-TELECOM", "country": "CN", "area": "中国-北京"},
    {"name": "中国联通", "code": "CHINA-UNICOM", "country": "CN", "area": "中国-北京"},
    {"name": "中国石油天然气集团", "code": "CNPC", "country": "CN", "area": "中国-北京"},
    {"name": "中国石油化工集团", "code": "SINOPEC", "country": "CN", "area": "中国-北京"},
    {"name": "中国建筑集团", "code": "CHINA-CONSTRUCTION", "country": "CN", "area": "中国-北京"},
    {"name": "中国中铁", "code": "CHINA-RAILWAY", "country": "CN", "area": "中国-北京"},
    {"name": "中国铁建", "code": "CHINA-RAILWAY-CONSTRUCTION", "country": "CN", "area": "中国-北京"},
    {"name": "中国中车", "code": "CRRC", "country": "CN", "area": "中国-北京"},
    {"name": "中国航天科技集团", "code": "CASC", "country": "CN", "area": "中国-北京"},
    {"name": "中国航天科工集团", "code": "CASIC", "country": "CN", "area": "中国-北京"},
    {"name": "苹果公司", "code": "APPLE", "country": "US", "area": "美国-加利福尼亚州-库比蒂诺"},
    {"name": "微软公司", "code": "MICROSOFT", "country": "US", "area": "美国-华盛顿州-雷德蒙德"},
    {"name": "谷歌公司", "code": "GOOGLE", "country": "US", "area": "美国-加利福尼亚州-山景城"},
    {"name": "亚马逊公司", "code": "AMAZON", "country": "US", "area": "美国-华盛顿州-西雅图"},
    {"name": "Meta", "code": "META", "country": "US", "area": "美国-加利福尼亚州-门洛帕克"},
    {"name": "特斯拉公司", "code": "TESLA", "country": "US", "area": "美国-得克萨斯州-奥斯汀"},
    {"name": "英特尔公司", "code": "INTEL", "country": "US", "area": "美国-加利福尼亚州-圣克拉拉"},
    {"name": "IBM", "code": "IBM", "country": "US", "area": "美国-纽约州-阿蒙克"},
    {"name": "甲骨文公司", "code": "ORACLE", "country": "US", "area": "美国-德克萨斯州-奥斯汀"},
    {"name": "思科系统", "code": "CISCO", "country": "US", "area": "美国-加利福尼亚州-圣何塞"},
    {"name": "惠普公司", "code": "HP", "country": "US", "area": "美国-加利福尼亚州-帕洛阿尔托"},
    {"name": "戴尔科技", "code": "DELL", "country": "US", "area": "美国-得克萨斯州-朗德罗克"},
    {"name": "通用电气", "code": "GE", "country": "US", "area": "美国-马萨诸塞州-波士顿"},
    {"name": "福特汽车", "code": "FORD", "country": "US", "area": "美国-密歇根州-迪尔伯恩"},
    {"name": "通用汽车", "code": "GM", "country": "US", "area": "美国-密歇根州-底特律"},
    {"name": "波音公司", "code": "BOEING", "country": "US", "area": "美国-弗吉尼亚州-阿灵顿"},
    {"name": "辉瑞公司", "code": "PFIZER", "country": "US", "area": "美国-纽约州-纽约"},
    {"name": "强生公司", "code": "JNJ", "country": "US", "area": "美国-新泽西州-新布朗斯维克"},
    {"name": "摩根大通", "code": "JPMORGAN", "country": "US", "area": "美国-纽约州-纽约"},
    {"name": "高盛集团", "code": "GOLDMAN-SACHS", "country": "US", "area": "美国-纽约州-纽约"},
    {"name": "花旗集团", "code": "CITI", "country": "US", "area": "美国-纽约州-纽约"},
    {"name": "瑞银集团", "code": "UBS", "country": "CH", "area": "瑞士-苏黎世"},
    {"name": "德意志银行", "code": "DEUTSCHE-BANK", "country": "DE", "area": "德国-法兰克福"},
    {"name": "西门子", "code": "SIEMENS", "country": "DE", "area": "德国-慕尼黑"},
    {"name": "宝马集团", "code": "BMW", "country": "DE", "area": "德国-慕尼黑"},
]


def request_json(url, method="GET", payload=None):
    data = None
    headers = {}
    if payload is not None:
        data = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        headers["Content-Type"] = "application/json; charset=utf-8"

    request = urllib.request.Request(url, data=data, method=method, headers=headers)
    with urllib.request.urlopen(request) as response:
        body = response.read().decode("utf-8")
        result = json.loads(body) if body else {}
        if isinstance(result, dict) and result.get("success") is False:
            raise RuntimeError(result.get("message") or "接口返回失败")
        return result


def get_all_companies():
    try:
        result = request_json(BASE_URL)
        return result.get("data", [])
    except Exception as exc:
        print(f"获取公司/项目列表失败: {exc}")
        return []


def get_country_dictionaries():
    result = request_json(f"{BASE_URL}/countries")
    return result.get("data", [])


def get_org_categories():
    result = request_json(f"{BASE_URL}/org-categories")
    return result.get("data", [])


def ensure_country_dictionary(country_code, country_name):
    current = {item["countryCode"] for item in get_country_dictionaries()}
    if country_code in current:
        return

    payload = {
        "countryCode": country_code,
        "countryName": country_name,
        "sortOrder": 100,
        "enabledFlag": "Y",
    }
    request_json(COUNTRY_DICTIONARY_URL, method="POST", payload=payload)
    print(f"已补充国家字典: {country_name} ({country_code})")


def ensure_org_category(category_code, category_name):
    current = {item["categoryCode"] for item in get_org_categories()}
    if category_code in current:
        return

    payload = {
        "categoryCode": category_code,
        "categoryName": category_name,
        "sortOrder": 100,
        "enabledFlag": "Y",
    }
    request_json(ORG_CATEGORY_DICTIONARY_URL, method="POST", payload=payload)
    print(f"已补充组织类别: {category_name} ({category_code})")


def clear_company_project_tables():
    sql = (
        "DELETE FROM md_company_project_line;"
        "DELETE FROM md_company_project;"
    )
    command = [
        "psql",
        "-h",
        "localhost",
        "-U",
        DB_USER,
        "-d",
        DB_NAME,
        "-c",
        sql,
    ]
    env = os.environ.copy()
    env["PGPASSWORD"] = DB_PASSWORD
    result = subprocess.run(command, capture_output=True, text=True, env=env, check=False)
    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip() or result.stdout.strip() or "清空数据库失败")
    print("已直接清空 md_company_project / md_company_project_line")


def delete_company(company_code):
    try:
        request_json(f"{BASE_URL}/{company_code}", method="DELETE")
        print(f"已删除: {company_code}")
        return True
    except Exception as exc:
        print(f"删除失败 {company_code}: {exc}")
        return False


def create_company(company):
    payload = {
        "companyProjectCode": company["code"],
        "companyProjectName": company["name"],
        "countryCode": company["country"],
        "managementArea": company["area"],
        "enabledFlag": "Y",
        "lines": [
            {
                "orgCategory": company["code"],
                "organizationCode": company["code"],
                "organizationName": company["name"],
                "validFlag": "Y",
            }
        ],
    }

    try:
        request_json(BASE_URL, method="POST", payload=payload)
        print(f"已创建: {company['name']} ({company['code']})")
        return True
    except urllib.error.HTTPError as exc:
        detail = exc.read().decode("utf-8", errors="ignore")
        print(f"创建失败 {company['name']}: HTTP {exc.code} {detail}")
        return False
    except Exception as exc:
        print(f"创建失败 {company['name']}: {exc}")
        return False


def main():
    print("开始重建公司/项目 mock 数据")
    print(f"目标公司数量: {len(COMPANIES)}")

    companies = get_all_companies()
    print(f"当前已有 {len(companies)} 条公司/项目数据")

    print("正在补齐国家字典和组织类别字典...")
    ensure_country_dictionary("CH", "瑞士")
    ensure_country_dictionary("JP", "日本")
    for company in COMPANIES:
        ensure_org_category(company["code"], company["name"])

    print("正在清空现有公司/项目数据...")
    clear_company_project_tables()

    companies_after_delete = get_all_companies()
    print(f"清空后剩余 {len(companies_after_delete)} 条")

    print("正在写入新的 50 条公司数据...")
    created = 0
    for index, company in enumerate(COMPANIES, start=1):
        print(f"[{index:02d}/{len(COMPANIES)}] {company['name']}")
        if create_company(company):
            created += 1

    final_companies = get_all_companies()
    print(f"写入成功 {created} 条")
    print(f"当前系统共有 {len(final_companies)} 条公司/项目数据")


if __name__ == "__main__":
    main()
