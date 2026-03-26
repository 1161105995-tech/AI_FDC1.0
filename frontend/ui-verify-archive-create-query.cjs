const { chromium } = require('playwright');
(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const result = { success: false };
  try {
    await page.goto('http://127.0.0.1:5173/archive-management/create', { waitUntil: 'networkidle', timeout: 120000 });
    const menuText = await page.locator('.menu').innerText();
    const createText = await page.locator('body').innerText();
    await page.goto('http://127.0.0.1:5173/archive-management/query', { waitUntil: 'networkidle', timeout: 120000 });
    const queryText = await page.locator('body').innerText();
    await page.screenshot({ path: 'D:/AI project/smart-archive-management-system/frontend/archive-create-query-verify.png', fullPage: true });
    result.success = true;
    result.hasQueryMenu = menuText.includes('档案查询');
    result.hasFlow = createText.includes('移交/核销') && createText.includes('已归档');
    result.hasAutoUpload = createText.includes('先上传原始文件') && createText.includes('点击或拖拽上传电子文件');
    result.queryStandalone = queryText.includes('档案查询') && queryText.includes('知识问答');
  } catch (error) {
    result.error = String(error);
  } finally {
    await browser.close();
    console.log(JSON.stringify(result));
  }
})();
