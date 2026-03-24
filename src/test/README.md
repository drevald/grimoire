# Translation Tests

This directory contains tests for the Grimoire translation functionality.

## Test Files

### 1. TranslateHandlerTest.java
Unit tests for the `TranslateHandler` class.
- Tests provider configuration (GET and POST)
- Tests word and translator setup
- Provides examples for integration testing

### 2. TranslationIntegrationTest.java
**Integration tests that verify actual translations using a mock HTTP server.**

This test suite uses WireMock to simulate translation APIs and verifies:
- ✅ "dog" translates to "собака" (Russian)
- ✅ "cat" translates to "кошка" (Russian)
- ✅ "hello" translates to "привет" (Russian)
- ✅ GET requests work correctly
- ✅ POST JSON requests work correctly
- ✅ POST form-encoded requests work correctly
- ✅ Error handling when API fails

## Running the Tests

### Run all tests
```bash
./gradlew test
```

### Run only translation tests
```bash
./gradlew test --tests "org.helico.sm.handler.*"
```

### Run a specific test
```bash
# Test "dog" -> "собака" translation
./gradlew test --tests "org.helico.sm.handler.TranslationIntegrationTest.testTranslation_Dog_To_Sobaka_GET"

# Test POST JSON translation
./gradlew test --tests "org.helico.sm.handler.TranslationIntegrationTest.testTranslation_Dog_To_Sobaka_POST_JSON"
```

### Run with verbose output
```bash
./gradlew test --tests "org.helico.sm.handler.TranslationIntegrationTest" --info
```

### Run tests continuously (watch mode)
```bash
./gradlew test --continuous
```

## Test Reports

After running tests, view the HTML report:
```
build/reports/tests/test/index.html
```

Or open in browser:
```bash
# Windows
start build/reports/tests/test/index.html

# Linux/Mac
open build/reports/tests/test/index.html
```

## What the Tests Verify

### TranslationIntegrationTest

#### Test 1: GET Request Translation
```java
@Test
testTranslation_Dog_To_Sobaka_GET()
```
- Sets up mock API endpoint: `GET /translate?text=dog&from=en&to=ru`
- Configures GET provider
- Calls translation method
- **Verifies**: "dog" → "собака"

#### Test 2: POST JSON Translation
```java
@Test
testTranslation_Dog_To_Sobaka_POST_JSON()
```
- Sets up mock API endpoint: `POST /translate`
- Expects JSON body: `{"text":"dog","source":"en","target":"ru"}`
- Configures POST JSON provider
- **Verifies**: "dog" → "собака" via POST

#### Test 3: POST Form-Encoded Translation
```java
@Test
testTranslation_Cat_To_Koshka_POST_FormEncoded()
```
- Sets up mock API endpoint: `POST /translate`
- Expects form body: `text=cat&from=en&to=ru`
- Configures POST form-encoded provider
- **Verifies**: "cat" → "кошка" via form POST

#### Test 4: Multiple Translations
```java
@Test
testMultipleTranslations()
```
- Tests translating multiple words in sequence
- **Verifies**: "dog" → "собака", "cat" → "кошка", "hello" → "привет"

#### Test 5: Error Handling
```java
@Test
testTranslation_APIError_ReturnsNull()
```
- Simulates API returning 404 error
- **Verifies**: Handler returns null on error

## Dependencies

The tests use:
- **JUnit 5** - Test framework
- **Mockito** - Mocking framework
- **WireMock** - HTTP server mocking
- **Spring Boot Test** - Spring testing support

## Mock API Endpoints

WireMock server runs on `localhost:8089` during tests and simulates:
- Translation API responses
- Different content types (JSON, form-encoded)
- Error scenarios (404, 500)

## Writing New Tests

To add a new translation test:

```java
@Test
@DisplayName("Test your translation")
public void testMyTranslation() throws Exception {
    // 1. Setup mock API
    stubFor(get(urlPathEqualTo("/translate"))
        .withQueryParam("text", equalTo("your-word"))
        .willReturn(aResponse()
            .withBody("перевод")
            .withStatus(200)));

    // 2. Configure provider
    TranslatorProvider provider = new TranslatorProvider();
    provider.setMethod("GET");
    provider.setReqPattern("{0}");
    provider.setResPattern("http://localhost:8089/translate?text={0}&from={1}&to={2}");

    // 3. Call translation
    String result = callFetchTranslation("your-word", provider, "en", "ru");

    // 4. Verify result
    assertEquals("перевод", result);
}
```

## Continuous Integration

These tests can be run in CI/CD pipelines:

```yaml
# GitHub Actions example
- name: Run Translation Tests
  run: ./gradlew test --tests "org.helico.sm.handler.*"
```

## Troubleshooting

### Tests fail with "Connection refused"
- WireMock server failed to start
- Check if port 8089 is available
- Try rerunning the tests

### Tests fail with "Method not found"
- Reflection is used to access private `fetchTranslation` method
- Ensure method signature hasn't changed in `TranslateHandler`

### WireMock dependency issues
- Ensure `build.gradle` has: `testImplementation 'org.wiremock:wiremock:3.3.1'`
- Run `./gradlew clean build` to refresh dependencies

## Test Coverage

To generate test coverage report:
```bash
./gradlew test jacocoTestReport
```

View report at: `build/reports/jacoco/test/html/index.html`
