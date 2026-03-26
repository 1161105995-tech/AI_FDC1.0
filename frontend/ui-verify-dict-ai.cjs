const { chromium } = require('playwright');
(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const result = { success: false };
  try {
    await page.goto('http://127.0.0.1:5173/base-data/dictionaries', { waitUntil: 'networkidle' });
    await page.waitForSelector('text=字典分类');
    await page.waitForSelector('text=新增分类');
    const dictBody = await page.locator('body').textContent();

    await page.goto('http://127.0.0.1:5173/governance', { waitUntil: 'networkidle' });
    await page.getByRole('tab', { name: 'AI模型配置' }).click();
    await page.waitForSelector('text=模型编码');
    const govBody = await page.locator('body').textContent();

    result.success = true;
    result.hasDictionaryCategory = /ARCHIVE_CARRIER_TYPE|载体类型|档案类型/.test(dictBody || '');
    result.hasAiModel = /LOCAL_CHAT|Local Chat Summarizer|模型编码/.test(govBody || '');
    await page.screenshot({ path: 'D:/AI project/smart-archive-management-system/frontend/archive-dict-ai-verify.png', fullPage: true });
  } catch (error) {
    result.error = String(error);
    try { await page.screenshot({ path: 'D:/AI project/smart-archive-management-system/frontend/archive-dict-ai-verify-error.png', fullPage: true }); } catch {}
  } finally {
    await browser.close();
    console.log(JSON.stringify(result));
  }
})();
