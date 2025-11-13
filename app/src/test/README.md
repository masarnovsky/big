# Unit Tests

This directory contains unit tests for the BIG Android application. These tests run on the JVM and do not require an Android device or emulator.

## Test Coverage

### ViewModel Tests (`MainViewModelTest.kt`)
- Input text management and validation
- Font selection
- Background selection and gradient randomization
- Orientation selection
- StateFlow emissions
- History management
- Edge cases (special characters, unicode, max length)

### Utility Function Tests (`UtilsTest.kt`)
- Background color generation
- Text color selection based on background
- Random gradient generation
- Enabled/disabled state opacity
- Color contrast validation

### Enum Tests (`EnumsTest.kt`)
- BackgroundColor enum validation
- Orientation enum validation
- InputFont enum validation
- GradientColor enum validation and color values
- Gradient brush generation

### Component Tests (`AutoSizeTextOptimizedTest.kt`)
- Text line balancing algorithm
- Word distribution across lines
- Edge cases (empty text, single word, special characters)
- Unicode support

### Activity Function Tests (`MainActivityKtTest.kt`)
- Input label text generation
- Character counting
- Special character handling

## Running the Tests

### From Android Studio
1. Right-click on the `test` directory
2. Select "Run 'Tests in 'com.masarnovsky.big...'"

### From Command Line
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests MainViewModelTest
```

### Generate Test Coverage Report
```bash
./gradlew testDebugUnitTest jacocoTestReport
```

## Dependencies
- JUnit 4
- MockK - For mocking Android dependencies
- Truth - For fluent assertions
- Turbine - For testing Kotlin Flows
- Robolectric - For Android framework components
- Coroutines Test - For testing coroutines
- Arch Core Testing - For testing LiveData and ViewModels

## Test Principles
- Tests are isolated and independent
- Each test has a clear, descriptive name
- Tests follow Arrange-Act-Assert pattern
- Edge cases and error conditions are tested
- Tests are fast and deterministic
