import { expect, test } from '@playwright/test';

async function cleanupAllNotes(request: { get: Function; delete: Function }) {
  const res = await request.get('/api/notes');
  expect(res.ok()).toBeTruthy();
  const notes = (await res.json()) as Array<{ type: string; fileName: string }>;

  for (const note of notes) {
    const del = await request.delete(`/api/notes/${note.type}/${note.fileName}`);
    expect(del.ok()).toBeTruthy();
  }
}

test.beforeEach(async ({ request }) => {
  await cleanupAllNotes(request);
});

test('creates notes, loads backlinks, and renders graph view', async ({ page }) => {
  await page.goto('/index.html');
  await expect(page.locator('#type option')).not.toHaveCount(0);

  const suffix = `${Date.now()}-${Math.floor(Math.random() * 10000)}`;
  const aFile = `a-${suffix}`;
  const bFile = `b-${suffix}`;

  await page.locator('#type').selectOption('inbox');
  await page.locator('#fileName').fill(aFile);
  await page.locator('#title').fill('Playwright A');
  await page.locator('#tags').fill('playwright,e2e');
  await page.locator('#links').fill('');
  await page.locator('#content').fill('first note content');

  const createADialog = page.waitForEvent('dialog');
  await page.getByRole('button', { name: '생성' }).click();
  (await createADialog).accept();

  await expect(page.locator('#noteList')).toContainText(`inbox/${aFile}`);

  await page.locator('#type').selectOption('projects');
  await page.locator('#fileName').fill(bFile);
  await page.locator('#title').fill('Playwright B');
  await page.locator('#tags').fill('playwright,graph');
  await page.locator('#links').fill(`inbox/${aFile}`);
  await page.locator('#content').fill('second note content');

  const createBDialog = page.waitForEvent('dialog');
  await page.getByRole('button', { name: '생성' }).click();
  (await createBDialog).accept();

  await page.getByText('Playwright A').first().click();
  await expect(page.locator('#backlinks')).toContainText(`projects/${bFile}`);

  await page.goto('/graph.html');
  await expect(page.locator('svg#graph')).toBeVisible();
  await expect(page.locator('svg#graph')).toContainText('Playwright A');
});
