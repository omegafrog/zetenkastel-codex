# Playwright E2E

## 1) Install dependencies

```bash
cd e2e
npm install
npx playwright install chromium
```

## 2) Run tests

```bash
cd e2e
npx playwright test
```

or from project root:

```bash
./gradlew e2eTest
```

Playwright starts Spring Boot automatically via `bootRun` on port `18080`.
