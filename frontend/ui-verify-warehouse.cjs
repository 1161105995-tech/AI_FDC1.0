const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({ headless: true, channel: 'msedge' });
  const page = await browser.newPage();
  const suffix = Date.now().toString().slice(-6);
  const code = 'WH-UI-' + suffix;
  const name = '前端验证库房-' + code;

  page.on('console', msg => console.log('BROWSER:', msg.text()));
  page.on('pageerror', err => console.log('PAGEERROR:', err.message));

  await page.goto('http://localhost:5173/base-data/warehouse', { waitUntil: 'networkidle' });
  await page.getByText('库房列表').locator('..').getByRole('button').click();
  await page.getByRole('dialog').getByLabel('编码').fill(code);
  await page.getByRole('dialog').getByLabel('名称').fill(name);
  await page.getByRole('dialog').getByLabel('类型').fill('综合档案库');
  await page.getByRole('dialog').getByLabel('负责人').fill('前端验证员');
  await page.getByRole('dialog').getByLabel('电话').fill('13600000000');
  await page.getByRole('dialog').getByLabel('地址').fill('前端联调区');
  await page.getByRole('dialog').getByRole('button', { name: '保存' }).click();

  await page.waitForTimeout(1500);
  await page.locator('.sidebar-card').filter({ hasText: code }).first().waitFor({ timeout: 10000 });

  const createdVisible = await page.locator('.sidebar-card').filter({ hasText: code }).first().isVisible();
  console.log(JSON.stringify({ success: createdVisible, warehouseCode: code, warehouseName: name }));

  await page.screenshot({ path: 'D:/AI project/smart-archive-management-system/frontend/warehouse-ui-verify.png', fullPage: true });
  await browser.close();
})();