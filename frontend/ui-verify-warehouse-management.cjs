const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1680, height: 1180 } });
  const result = { success: false };
  const imagePath = path.join(__dirname, 'warehouse-upload-verify.png');
  fs.writeFileSync(imagePath, Buffer.from('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9Wn7ZlIAAAAASUVORK5CYII=', 'base64'));

  try {
    await page.goto('http://127.0.0.1:5173/base-data/warehouse', { waitUntil: 'networkidle', timeout: 120000 });
    await page.waitForSelector('text=库房查询');
    await page.waitForSelector('.warehouse-card-toolbar');

    await page.getByRole('button', { name: '新建库房' }).click();
    const dialog = page.getByRole('dialog', { name: '新建库房' });
    const suffix = Date.now().toString().slice(-6);
    const warehouseName = `图片上传验证库房-${suffix}`;

    await dialog.getByLabel('库房编码').fill(`WH-UI-${suffix}`);
    await dialog.getByLabel('库房名称').fill(warehouseName);
    await dialog.getByLabel('库房类型').fill('综合档案库');
    await dialog.getByLabel('负责人').fill('自动化验证');
    await dialog.getByLabel('面积(㎡)').fill('180');
    await dialog.getByLabel('地址').fill('自动化验证地址');
    await dialog.locator('.warehouse-photo-upload input[type=file]').setInputFiles(imagePath);
    await dialog.getByRole('button', { name: '保存' }).click();
    await page.waitForTimeout(1500);
    await page.waitForSelector(`text=${warehouseName}`);

    await page.locator('.warehouse-card').filter({ hasText: 'WH-001' }).first().click();
    await page.waitForTimeout(1000);
    await page.waitForSelector('.rack-board-card');
    await page.getByRole('button', { name: '全部收缩' }).click();
    await page.waitForTimeout(600);
    const collapsedCount = await page.locator('.rack-board-card.is-collapsed').count();
    await page.getByRole('button', { name: '全部展开' }).click();
    await page.waitForTimeout(600);
    const expandedCount = await page.locator('.rack-board-card.is-collapsed').count();

    await page.screenshot({ path: path.join(__dirname, 'warehouse-layout-refactor-verify.png'), fullPage: true });

    result.success = true;
    result.warehouseName = warehouseName;
    result.collapsedCount = collapsedCount;
    result.expandedCount = expandedCount;
    result.queryColumnCount = await page.locator('.warehouse-query-form__grid').evaluate((el) => getComputedStyle(el).gridTemplateColumns.split(' ').length);
  } catch (error) {
    result.error = String(error);
  } finally {
    await browser.close();
  }

  console.log(JSON.stringify(result));
})();