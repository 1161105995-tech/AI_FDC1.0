import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestCompanies {
    private static final String BASE_URL = "http://localhost:8080/api/base-data/company-projects";

    public static void main(String[] args) {
        try {
            System.out.println("Starting automated test...");
            
            // 1. Get all existing companies
            System.out.println("\n1. Getting all existing companies...");
            int existingCount = getCompanyCount();
            System.out.println("Current total companies: " + existingCount);
            
            // 2. Create 50 well-known companies
            System.out.println("\n2. Creating 50 well-known companies...");
            
            // Domestic companies
            createCompany("Alibaba Group", "ALIBAABA", "CN", "China-Zhejiang-Hangzhou");
            createCompany("Tencent Holdings", "TENCENT", "CN", "China-Guangdong-Shenzhen");
            createCompany("Baidu Inc.", "BAIDU", "CN", "China-Beijing");
            createCompany("JD.com", "JD", "CN", "China-Beijing");
            createCompany("Huawei Technologies", "HUAWEI", "CN", "China-Guangdong-Shenzhen");
            createCompany("Xiaomi Corporation", "XIAOMI", "CN", "China-Beijing");
            createCompany("ByteDance", "BYTEDANCE", "CN", "China-Beijing");
            createCompany("NetEase Inc.", "NETEASE", "CN", "China-Zhejiang-Hangzhou");
            createCompany("Pinduoduo", "PDD", "CN", "China-Shanghai");
            createCompany("Meituan Dianping", "MEITUAN", "CN", "China-Beijing");
            createCompany("Industrial and Commercial Bank of China", "ICBC", "CN", "China-Beijing");
            createCompany("China Construction Bank", "CCB", "CN", "China-Beijing");
            createCompany("Agricultural Bank of China", "ABC", "CN", "China-Beijing");
            createCompany("Bank of China", "BOC", "CN", "China-Beijing");
            createCompany("China Mobile", "CMCC", "CN", "China-Beijing");
            createCompany("China Telecom", "CHINA-TELECOM", "CN", "China-Beijing");
            createCompany("China Unicom", "CHINA-UNICOM", "CN", "China-Beijing");
            createCompany("China National Petroleum Corporation", "CNPC", "CN", "China-Beijing");
            createCompany("Sinopec Group", "SINOPEC", "CN", "China-Beijing");
            createCompany("China State Construction Engineering", "CHINA-CONSTRUCTION", "CN", "China-Beijing");
            createCompany("China Railway Group", "CHINA-RAILWAY", "CN", "China-Beijing");
            createCompany("China Railway Construction Corporation", "CHINA-RAILWAY-CONSTRUCTION", "CN", "China-Beijing");
            createCompany("CRRC Corporation", "CRRC", "CN", "China-Beijing");
            createCompany("China Aerospace Science and Technology Corporation", "CASC", "CN", "China-Beijing");
            createCompany("China Aerospace Science and Industry Corporation", "CASIC", "CN", "China-Beijing");
            
            // International companies
            createCompany("Apple Inc.", "APPLE", "US", "USA-California-Cupertino");
            createCompany("Microsoft Corporation", "MICROSOFT", "US", "USA-Washington-Redmond");
            createCompany("Google LLC", "GOOGLE", "US", "USA-California-Mountain View");
            createCompany("Amazon.com Inc.", "AMAZON", "US", "USA-Washington-Seattle");
            createCompany("Meta Platforms Inc.", "FACEBOOK", "US", "USA-California-Menlo Park");
            createCompany("Tesla Inc.", "TESLA", "US", "USA-California-Palo Alto");
            createCompany("Intel Corporation", "INTEL", "US", "USA-California-Santa Clara");
            createCompany("IBM Corporation", "IBM", "US", "USA-New York-Armonk");
            createCompany("Oracle Corporation", "ORACLE", "US", "USA-California-Redwood City");
            createCompany("Cisco Systems Inc.", "CISCO", "US", "USA-California-San Jose");
            createCompany("HP Inc.", "HP", "US", "USA-California-Palo Alto");
            createCompany("Dell Technologies", "DELL", "US", "USA-Texas-Round Rock");
            createCompany("General Electric Company", "GE", "US", "USA-Massachusetts-Boston");
            createCompany("Ford Motor Company", "FORD", "US", "USA-Michigan-Dearborn");
            createCompany("General Motors Company", "GM", "US", "USA-Michigan-Detroit");
            createCompany("The Boeing Company", "BOEING", "US", "USA-Illinois-Chicago");
            createCompany("Pfizer Inc.", "PFIZER", "US", "USA-New York-New York");
            createCompany("Johnson & Johnson", "JNJ", "US", "USA-New Jersey-New Brunswick");
            createCompany("JPMorgan Chase & Co.", "JPMORGAN", "US", "USA-New York-New York");
            createCompany("Goldman Sachs Group Inc.", "GOLDMAN-SACHS", "US", "USA-New York-New York");
            createCompany("Citigroup Inc.", "CITI", "US", "USA-New York-New York");
            createCompany("UBS Group AG", "UBS", "CH", "Switzerland-Zurich");
            createCompany("Credit Suisse Group AG", "CREDIT-SUISSE", "CH", "Switzerland-Zurich");
            createCompany("Deutsche Bank AG", "DEUTSCHE-BANK", "DE", "Germany-Frankfurt");
            createCompany("Siemens AG", "SIEMENS", "DE", "Germany-Munich");
            createCompany("BMW AG", "BMW", "DE", "Germany-Munich");
            createCompany("Volkswagen Group", "VOLKSWAGEN", "DE", "Germany-Wolfsburg");
            createCompany("Toyota Motor Corporation", "TOYOTA", "JP", "Japan-Aichi-Toyota");
            
            // 3. Verify creation result
            System.out.println("\n3. Verifying creation result...");
            int finalCount = getCompanyCount();
            System.out.println("Total companies after creation: " + finalCount);
            
            System.out.println("\nAutomated test completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getCompanyCount() throws Exception {
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
            
            String jsonResponse = response.toString();
            // Count the number of companyProjectCode occurrences
            int count = 0;
            int index = jsonResponse.indexOf("companyProjectCode");
            while (index != -1) {
                count++;
                index = jsonResponse.indexOf("companyProjectCode", index + 1);
            }
            return count;
        } else {
            System.out.println("Failed to get company list: " + responseCode);
            return 0;
        }
    }

    private static void createCompany(String name, String code, String countryCode, String managementArea) throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        
        String payload = "{" +
                "\"companyProjectCode\":\"" + code + "\","
                + "\"companyProjectName\":\"" + name + "\","
                + "\"countryCode\":\"" + countryCode + "\","
                + "\"managementArea\":\"" + managementArea + "\","
                + "\"enabledFlag\":\"Y\","
                + "\"lines\": [{" +
                "\"orgCategory\":\"Headquarters\","
                + "\"organizationCode\":\"" + code + "-HQ\","
                + "\"organizationName\":\"" + name + " Headquarters\","
                + "\"validFlag\":\"Y\""
                + "}]}";
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Created company " + name + " successfully");
        } else {
            System.out.println("Failed to create company " + name + ": " + responseCode);
            // Read error message
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                String inputLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    errorResponse.append(inputLine);
                }
                System.out.println("Error message: " + errorResponse.toString());
            }
        }
    }
}