import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TestCompanyProjects {
    private static final String BASE_URL = "http://localhost:8080/api/base-data/company-projects";

    // 国内外知名公司列表
    private static final List<Company> COMPANIES = new ArrayList<>();

    static {
        // 国内公司
        COMPANIES.add(new Company("阿里巴巴集团", "ALIBAABA", "CN", "中国-浙江-杭州", List.of(new Organization("总部", "ALI-HQ", "阿里巴巴集团总部", "Y"))));
        COMPANIES.add(new Company("腾讯控股", "TENCENT", "CN", "中国-广东-深圳", List.of(new Organization("总部", "TENCENT-HQ", "腾讯控股总部", "Y"))));
        COMPANIES.add(new Company("百度公司", "BAIDU", "CN", "中国-北京", List.of(new Organization("总部", "BAIDU-HQ", "百度公司总部", "Y"))));
        COMPANIES.add(new Company("京东集团", "JD", "CN", "中国-北京", List.of(new Organization("总部", "JD-HQ", "京东集团总部", "Y"))));
        COMPANIES.add(new Company("华为技术有限公司", "HUAWEI", "CN", "中国-广东-深圳", List.of(new Organization("总部", "HUAWEI-HQ", "华为技术有限公司总部", "Y"))));
        COMPANIES.add(new Company("小米科技有限责任公司", "XIAOMI", "CN", "中国-北京", List.of(new Organization("总部", "XIAOMI-HQ", "小米科技有限责任公司总部", "Y"))));
        COMPANIES.add(new Company("字节跳动", "BYTEDANCE", "CN", "中国-北京", List.of(new Organization("总部", "BYTEDANCE-HQ", "字节跳动总部", "Y"))));
        COMPANIES.add(new Company("网易公司", "NETEASE", "CN", "中国-浙江-杭州", List.of(new Organization("总部", "NETEASE-HQ", "网易公司总部", "Y"))));
        COMPANIES.add(new Company("拼多多", "PDD", "CN", "中国-上海", List.of(new Organization("总部", "PDD-HQ", "拼多多总部", "Y"))));
        COMPANIES.add(new Company("美团点评", "MEITUAN", "CN", "中国-北京", List.of(new Organization("总部", "MEITUAN-HQ", "美团点评总部", "Y"))));
        COMPANIES.add(new Company("中国工商银行", "ICBC", "CN", "中国-北京", List.of(new Organization("总部", "ICBC-HQ", "中国工商银行总部", "Y"))));
        COMPANIES.add(new Company("中国建设银行", "CCB", "CN", "中国-北京", List.of(new Organization("总部", "CCB-HQ", "中国建设银行总部", "Y"))));
        COMPANIES.add(new Company("中国农业银行", "ABC", "CN", "中国-北京", List.of(new Organization("总部", "ABC-HQ", "中国农业银行总部", "Y"))));
        COMPANIES.add(new Company("中国银行", "BOC", "CN", "中国-北京", List.of(new Organization("总部", "BOC-HQ", "中国银行总部", "Y"))));
        COMPANIES.add(new Company("中国移动", "CMCC", "CN", "中国-北京", List.of(new Organization("总部", "CMCC-HQ", "中国移动总部", "Y"))));
        COMPANIES.add(new Company("中国电信", "CHINA-TELECOM", "CN", "中国-北京", List.of(new Organization("总部", "CHINA-TELECOM-HQ", "中国电信总部", "Y"))));
        COMPANIES.add(new Company("中国联通", "CHINA-UNICOM", "CN", "中国-北京", List.of(new Organization("总部", "CHINA-UNICOM-HQ", "中国联通总部", "Y"))));
        COMPANIES.add(new Company("中国石油天然气集团", "CNPC", "CN", "中国-北京", List.of(new Organization("总部", "CNPC-HQ", "中国石油天然气集团总部", "Y"))));
        COMPANIES.add(new Company("中国石油化工集团", "SINOPEC", "CN", "中国-北京", List.of(new Organization("总部", "SINOPEC-HQ", "中国石油化工集团总部", "Y"))));
        COMPANIES.add(new Company("中国建筑集团", "CHINA-CONSTRUCTION", "CN", "中国-北京", List.of(new Organization("总部", "CHINA-CONSTRUCTION-HQ", "中国建筑集团总部", "Y"))));
        COMPANIES.add(new Company("中国中铁股份有限公司", "CHINA-RAILWAY", "CN", "中国-北京", List.of(new Organization("总部", "CHINA-RAILWAY-HQ", "中国中铁股份有限公司总部", "Y"))));
        COMPANIES.add(new Company("中国铁建股份有限公司", "CHINA-RAILWAY-CONSTRUCTION", "CN", "中国-北京", List.of(new Organization("总部", "CHINA-RAILWAY-CONSTRUCTION-HQ", "中国铁建股份有限公司总部", "Y"))));
        COMPANIES.add(new Company("中国中车集团", "CRRC", "CN", "中国-北京", List.of(new Organization("总部", "CRRC-HQ", "中国中车集团总部", "Y"))));
        COMPANIES.add(new Company("中国航天科技集团", "CASC", "CN", "中国-北京", List.of(new Organization("总部", "CASC-HQ", "中国航天科技集团总部", "Y"))));
        COMPANIES.add(new Company("中国航天科工集团", "CASIC", "CN", "中国-北京", List.of(new Organization("总部", "CASIC-HQ", "中国航天科工集团总部", "Y"))));
        
        // 国际公司
        COMPANIES.add(new Company("苹果公司", "APPLE", "US", "美国-加利福尼亚州-库比蒂诺", List.of(new Organization("总部", "APPLE-HQ", "苹果公司总部", "Y"))));
        COMPANIES.add(new Company("微软公司", "MICROSOFT", "US", "美国-华盛顿州-雷德蒙德", List.of(new Organization("总部", "MICROSOFT-HQ", "微软公司总部", "Y"))));
        COMPANIES.add(new Company("谷歌公司", "GOOGLE", "US", "美国-加利福尼亚州-山景城", List.of(new Organization("总部", "GOOGLE-HQ", "谷歌公司总部", "Y"))));
        COMPANIES.add(new Company("亚马逊公司", "AMAZON", "US", "美国-华盛顿州-西雅图", List.of(new Organization("总部", "AMAZON-HQ", "亚马逊公司总部", "Y"))));
        COMPANIES.add(new Company("脸书公司", "FACEBOOK", "US", "美国-加利福尼亚州-门洛帕克", List.of(new Organization("总部", "FACEBOOK-HQ", "脸书公司总部", "Y"))));
        COMPANIES.add(new Company("特斯拉公司", "TESLA", "US", "美国-加利福尼亚州-帕洛阿尔托", List.of(new Organization("总部", "TESLA-HQ", "特斯拉公司总部", "Y"))));
        COMPANIES.add(new Company("英特尔公司", "INTEL", "US", "美国-加利福尼亚州-圣克拉拉", List.of(new Organization("总部", "INTEL-HQ", "英特尔公司总部", "Y"))));
        COMPANIES.add(new Company("IBM公司", "IBM", "US", "美国-纽约州-阿蒙克", List.of(new Organization("总部", "IBM-HQ", "IBM公司总部", "Y"))));
        COMPANIES.add(new Company("甲骨文公司", "ORACLE", "US", "美国-加利福尼亚州-红木城", List.of(new Organization("总部", "ORACLE-HQ", "甲骨文公司总部", "Y"))));
        COMPANIES.add(new Company("思科系统公司", "CISCO", "US", "美国-加利福尼亚州-圣何塞", List.of(new Organization("总部", "CISCO-HQ", "思科系统公司总部", "Y"))));
        COMPANIES.add(new Company("惠普公司", "HP", "US", "美国-加利福尼亚州-帕洛阿尔托", List.of(new Organization("总部", "HP-HQ", "惠普公司总部", "Y"))));
        COMPANIES.add(new Company("戴尔公司", "DELL", "US", "美国-德克萨斯州-圆石城", List.of(new Organization("总部", "DELL-HQ", "戴尔公司总部", "Y"))));
        COMPANIES.add(new Company("通用电气公司", "GE", "US", "美国-马萨诸塞州-波士顿", List.of(new Organization("总部", "GE-HQ", "通用电气公司总部", "Y"))));
        COMPANIES.add(new Company("福特汽车公司", "FORD", "US", "美国-密歇根州-迪尔伯恩", List.of(new Organization("总部", "FORD-HQ", "福特汽车公司总部", "Y"))));
        COMPANIES.add(new Company("通用汽车公司", "GM", "US", "美国-密歇根州-底特律", List.of(new Organization("总部", "GM-HQ", "通用汽车公司总部", "Y"))));
        COMPANIES.add(new Company("波音公司", "BOEING", "US", "美国-伊利诺伊州-芝加哥", List.of(new Organization("总部", "BOEING-HQ", "波音公司总部", "Y"))));
        COMPANIES.add(new Company("辉瑞制药公司", "PFIZER", "US", "美国-纽约州-纽约", List.of(new Organization("总部", "PFIZER-HQ", "辉瑞制药公司总部", "Y"))));
        COMPANIES.add(new Company("强生公司", "JNJ", "US", "美国-新泽西州-新布朗斯维克", List.of(new Organization("总部", "JNJ-HQ", "强生公司总部", "Y"))));
        COMPANIES.add(new Company("摩根大通公司", "JPMORGAN", "US", "美国-纽约州-纽约", List.of(new Organization("总部", "JPMORGAN-HQ", "摩根大通公司总部", "Y"))));
        COMPANIES.add(new Company("高盛集团", "GOLDMAN-SACHS", "US", "美国-纽约州-纽约", List.of(new Organization("总部", "GOLDMAN-SACHS-HQ", "高盛集团总部", "Y"))));
        COMPANIES.add(new Company("花旗集团", "CITI", "US", "美国-纽约州-纽约", List.of(new Organization("总部", "CITI-HQ", "花旗集团总部", "Y"))));
        COMPANIES.add(new Company("瑞士银行", "UBS", "CH", "瑞士-苏黎世", List.of(new Organization("总部", "UBS-HQ", "瑞士银行总部", "Y"))));
        COMPANIES.add(new Company("瑞银集团", "CREDIT-SUISSE", "CH", "瑞士-苏黎世", List.of(new Organization("总部", "CREDIT-SUISSE-HQ", "瑞银集团总部", "Y"))));
        COMPANIES.add(new Company("德意志银行", "DEUTSCHE-BANK", "DE", "德国-法兰克福", List.of(new Organization("总部", "DEUTSCHE-BANK-HQ", "德意志银行总部", "Y"))));
        COMPANIES.add(new Company("西门子公司", "SIEMENS", "DE", "德国-慕尼黑", List.of(new Organization("总部", "SIEMENS-HQ", "西门子公司总部", "Y"))));
        COMPANIES.add(new Company("宝马公司", "BMW", "DE", "德国-慕尼黑", List.of(new Organization("总部", "BMW-HQ", "宝马公司总部", "Y"))));
        COMPANIES.add(new Company("大众汽车公司", "VOLKSWAGEN", "DE", "德国-沃尔夫斯堡", List.of(new Organization("总部", "VOLKSWAGEN-HQ", "大众汽车公司总部", "Y"))));
        COMPANIES.add(new Company("丰田汽车公司", "TOYOTA", "JP", "日本-爱知县-丰田市", List.of(new Organization("总部", "TOYOTA-HQ", "丰田汽车公司总部", "Y"))));
        COMPANIES.add(new Company("本田汽车公司", "HONDA", "JP", "日本-东京都-港区", List.of(new Organization("总部", "HONDA-HQ", "本田汽车公司总部", "Y"))));
        COMPANIES.add(new Company("索尼公司", "SONY", "JP", "日本-东京都-港区", List.of(new Organization("总部", "SONY-HQ", "索尼公司总部", "Y"))));
        COMPANIES.add(new Company("松下电器公司", "PANASONIC", "JP", "日本-大阪府-门真市", List.of(new Organization("总部", "PANASONIC-HQ", "松下电器公司总部", "Y"))));
    }

    public static void main(String[] args) throws Exception {
        System.out.println("开始执行自动化测试...");
        
        // 1. 获取所有现有公司/项目
        System.out.println("\n1. 获取所有现有公司/项目...");
        List<CompanyResponse> companies = getAllCompanies();
        System.out.println("当前共有 " + companies.size() + " 个公司/项目");
        
        // 2. 删除所有现有公司/项目
        System.out.println("\n2. 删除所有现有公司/项目...");
        for (CompanyResponse company : companies) {
            String companyCode = company.getCompanyProjectCode();
            if (companyCode != null) {
                deleteCompany(companyCode);
            }
        }
        
        // 3. 创建50个国内外知名公司
        System.out.println("\n3. 创建50个国内外知名公司...");
        for (int i = 0; i < COMPANIES.size(); i++) {
            Company company = COMPANIES.get(i);
            System.out.println("创建公司 " + (i + 1) + "/" + COMPANIES.size() + ": " + company.getName());
            createCompany(company);
        }
        
        // 4. 验证创建结果
        System.out.println("\n4. 验证创建结果...");
        companies = getAllCompanies();
        System.out.println("创建完成后共有 " + companies.size() + " 个公司/项目");
        
        System.out.println("\n自动化测试执行完成！");
    }

    private static List<CompanyResponse> getAllCompanies() throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            // 简单解析JSON响应
            String jsonResponse = response.toString();
            if (jsonResponse.contains("data":)) {
                // 这里简化处理，实际应该使用JSON解析库
                // 假设响应格式为 {"code": 200, "message": "success", "data": [...]}  
                return parseCompanyList(jsonResponse);
            }
        } else {
            System.out.println("获取公司列表失败: " + responseCode);
        }
        return new ArrayList<>();
    }

    private static void deleteCompany(String companyCode) throws Exception {
        URL url = new URL(BASE_URL + "/" + companyCode);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("删除公司 " + companyCode + " 成功");
        } else {
            System.out.println("删除公司 " + companyCode + " 失败: " + responseCode);
        }
    }

    private static void createCompany(Company company) throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        
        // 构建请求体
        StringBuilder linesJson = new StringBuilder("[");
        for (int i = 0; i < company.getOrganizations().size(); i++) {
            Organization org = company.getOrganizations().get(i);
            linesJson.append("{")
                    .append("\"orgCategory\":\"").append(org.getCategory()).append("\",")
                    .append("\"organizationCode\":\"").append(org.getCode()).append("\",")
                    .append("\"organizationName\":\"").append(org.getName()).append("\",")
                    .append("\"validFlag\":\"").append(org.getValidFlag()).append("\"")
                    .append("}");
            if (i < company.getOrganizations().size() - 1) {
                linesJson.append(",");
            }
        }
        linesJson.append("]");
        
        String payload = "{" +
                "\"companyProjectCode\":\"" + company.getCode() + "\","
                + "\"companyProjectName\":\"" + company.getName() + "\","
                + "\"countryCode\":\"" + company.getCountryCode() + "\","
                + "\"managementArea\":\"" + company.getManagementArea() + "\","
                + "\"enabledFlag\":\"Y\","
                + "\"lines\": " + linesJson.toString() +
                "}";
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("创建公司 " + company.getName() + " 成功");
        } else {
            System.out.println("创建公司 " + company.getName() + " 失败: " + responseCode);
            // 读取错误信息
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                String inputLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    errorResponse.append(inputLine);
                }
                System.out.println("错误信息: " + errorResponse.toString());
            }
        }
    }

    // 简化的JSON解析方法
    private static List<CompanyResponse> parseCompanyList(String jsonResponse) {
        List<CompanyResponse> companies = new ArrayList<>();
        
        // 简单提取公司项目编码
        int dataStart = jsonResponse.indexOf("data":) + 6;
        int dataEnd = jsonResponse.lastIndexOf("]") + 1;
        
        if (dataStart > 5 && dataEnd > dataStart) {
            String dataArray = jsonResponse.substring(dataStart, dataEnd);
            
            // 分割每个公司对象
            int start = 0;
            while (start < dataArray.length()) {
                int objStart = dataArray.indexOf("{", start);
                if (objStart == -1) break;
                
                int objEnd = findMatchingBrace(dataArray, objStart);
                if (objEnd == -1) break;
                
                String objStr = dataArray.substring(objStart, objEnd + 1);
                CompanyResponse company = parseCompany(objStr);
                if (company != null) {
                    companies.add(company);
                }
                
                start = objEnd + 1;
            }
        }
        
        return companies;
    }

    private static int findMatchingBrace(String s, int start) {
        int count = 1;
        for (int i = start + 1; i < s.length(); i++) {
            if (s.charAt(i) == '{') {
                count++;
            } else if (s.charAt(i) == '}') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static CompanyResponse parseCompany(String objStr) {
        CompanyResponse company = new CompanyResponse();
        
        // 提取companyProjectCode
        int codeStart = objStr.indexOf("companyProjectCode") + 20;
        if (codeStart > 19) {
            int codeEnd = objStr.indexOf('"', codeStart);
            if (codeEnd > codeStart) {
                company.setCompanyProjectCode(objStr.substring(codeStart, codeEnd));
            }
        }
        
        return company;
    }

    // 公司类
    static class Company {
        private String name;
        private String code;
        private String countryCode;
        private String managementArea;
        private List<Organization> organizations;

        public Company(String name, String code, String countryCode, String managementArea, List<Organization> organizations) {
            this.name = name;
            this.code = code;
            this.countryCode = countryCode;
            this.managementArea = managementArea;
            this.organizations = organizations;
        }

        public String getName() { return name; }
        public String getCode() { return code; }
        public String getCountryCode() { return countryCode; }
        public String getManagementArea() { return managementArea; }
        public List<Organization> getOrganizations() { return organizations; }
    }

    // 组织类
    static class Organization {
        private String category;
        private String code;
        private String name;
        private String validFlag;

        public Organization(String category, String code, String name, String validFlag) {
            this.category = category;
            this.code = code;
            this.name = name;
            this.validFlag = validFlag;
        }

        public String getCategory() { return category; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getValidFlag() { return validFlag; }
    }

    // 公司响应类
    static class CompanyResponse {
        private String companyProjectCode;

        public String getCompanyProjectCode() { return companyProjectCode; }
        public void setCompanyProjectCode(String companyProjectCode) { this.companyProjectCode = companyProjectCode; }
    }
}