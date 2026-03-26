const { chromium } = require('playwright');
(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  const result = { success: false };
  try {
    await page.goto('http://127.0.0.1:5173/dashboard', { waitUntil: 'networkidle', timeout: 120000 });
    await page.waitForSelector('.menu', { timeout: 120000 });
    const menuText = await page.locator('.menu').innerText();
    result.success = true;
    result.menuText = menuText;
    result.hasGarbled = /�|Ã|å|é/.test(menuText);
    await page.screenshot({ path: 'D:/AI project/smart-archive-management-system/frontend/menu-check.png', fullPage: true });
  } catch (error) {
    result.error = String(error);
    try { await page.screenshot({ path: 'D:/AI project/smart-archive-management-system/frontend/menu-check-error.png', fullPage: true }); } catch {}
  } finally {
    await browser.close();
    console.log(JSON.stringify(result, null, 2));
  }
})();
