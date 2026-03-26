const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true, channel: 'msedge' });
  const page = await browser.newPage();
  const suffix = Date.now().toString().slice(-6);
  const rackCode = 'RK' + suffix.slice(-2);
  const rackName = '前端验证柜组-' + suffix;

  page.on('console', msg => console.log('BROWSER:', msg.text()));
  page.on('pageerror', err => console.log('PAGEERROR:', err.message));

  await page.goto('http://localhost:5173/base-data/warehouse', { waitUntil: 'networkidle' });
  await page.locator('.sidebar-card.soft').first().click();
  await page.getByText('柜组列表').locator('..').getByRole('button').click();
  const dialog = page.getByRole('dialog');
  await dialog.getByLabel('柜组编码').fill(rackCode);
  await dialog.getByLabel('柜组名称').fill(rackName);
  await dialog.getByLabel('层数').fill('4');
  await dialog.getByLabel('每层格数').fill('6');
  await dialog.getByRole('button', { name: '保存' }).click();

  await page.waitForTimeout(1800);
  await page.locator('.sidebar-card.mini').filter({ hasText: rackCode }).first().waitFor({ timeout: 10000 });
  await page.locator('.rack-card').filter({ hasText: rackName }).first().waitFor({ timeout: 10000 });

  const inSidebar = await page.locator('.sidebar-card.mini').filter({ hasText: rackCode }).first().isVisible();
  const inGrid = await page.locator('.rack-card').filter({ hasText: rackName }).first().isVisible();
  console.log(JSON.stringify({ success: inSidebar && inGrid, rackCode, rackName }));

  await page.screenshot({ path: 'D:/AI project/smart-archive-management-system/frontend/rack-ui-verify.png', fullPage: true });
  await browser.close();
})();