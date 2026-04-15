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

test('saves notes with single save action, builds backlinks, and renders graph view', async ({ page }) => {
  await page.goto('/index.html');
  await expect(page.locator('#type option')).not.toHaveCount(0);
  await expect(page.getByText('노트 편집')).toHaveCount(0);

  const suffix = `${Date.now()}-${Math.floor(Math.random() * 10000)}`;
  const aTitle = `playwright-a-${suffix}`;
  const bTitle = `playwright-b-${suffix}`;
  const aPath = `inbox/${aTitle}`;
  const bPath = `projects/${bTitle}`;

  await page.locator('#type').selectOption('inbox');
  await page.locator('#title').fill(aTitle);
  await page.locator('#tags').fill('playwright');
  await page.locator('#tags').press('Enter');
  await page.locator('#tags').fill('e2e');
  await page.locator('#tags').press('Enter');
  await page.locator('#links').fill('');
  await page.locator('#content').fill('first note content');

  await page.getByRole('button', { name: '저장(생성)' }).click();
  await expect(page.locator('#noteList')).toContainText(aPath);

  await page.locator('#type').selectOption('projects');
  await page.locator('#title').fill(bTitle);
  await page.locator('#tags').fill('graph');
  await page.locator('#tags').press('Enter');
  await page.locator('#links').fill(aPath);
  await page.locator('#content').fill('second note content');

  await page.getByRole('button', { name: '저장(생성)' }).click();
  await expect(page.locator('#noteList')).toContainText(bPath);

  await page.getByText(aTitle).first().click();
  await expect(page.locator('#backlinks')).toContainText(bPath);

  await page.goto('/graph.html');
  await expect(page.locator('svg#graph')).toBeVisible();
  await expect(page.locator('svg#graph')).toContainText(aTitle);
});

test('classifies inbox notes with source evidence into literature notes', async ({ page }) => {
  await page.goto('/index.html');

  const suffix = `${Date.now()}-${Math.floor(Math.random() * 10000)}`;
  const title = `source-note-${suffix}`;
  const expectedPath = `literature-notes/${title}`;

  await page.locator('#type').selectOption('inbox');
  await page.locator('#title').fill(title);
  await page.locator('#links').fill('');
  await page.locator('#content').fill(`저자: Martin Fowler\n출처: Refactoring\nURL: https://martinfowler.com/articles/${suffix}\n리팩토링 요약`);

  await page.getByRole('button', { name: '저장(생성)' }).click();
  await expect(page.locator('#noteList')).toContainText(expectedPath);
});
