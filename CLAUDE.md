# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build & Run
```bash
# Build all targets
./gradlew build

# Run on desktop
./gradlew :composeApp:run

# Build Android APK
./gradlew :composeApp:assembleDebug

# Build desktop distribution
./gradlew :composeApp:packageDmg        # macOS
./gradlew :composeApp:packageMsi        # Windows
./gradlew :composeApp:packageDeb        # Linux
```

### Testing
```bash
# Run all tests
./gradlew :composeApp:allTests

# Run common tests (shared logic)
./gradlew :composeApp:testDebugUnitTest

# Run desktop tests
./gradlew :composeApp:desktopTest

# Run iOS tests
./gradlew :composeApp:iosX64Test
./gradlew :composeApp:iosSimulatorArm64Test

# Run Android instrumented tests
./gradlew :composeApp:connectedDebugAndroidTest

# Run Android unit tests
./gradlew :composeApp:testDebugUnitTest
```

### Database Management
```bash
# Clean desktop database (useful for testing)
./gradlew cleanDesktopDatabase
```

## Project Architecture

### High-Level Structure
This is a **Kotlin Multiplatform** project using **Clean Architecture** with the following layers:

- **Domain Layer** (`domain/`): Business logic, entities, use cases, and repository interfaces
- **Data Layer** (`data/`): Repository implementations, local/remote data sources, DTOs, and mappers  
- **UI Layer** (`ui/`): Compose UI, ViewModels, navigation, and platform-specific UI code

### Key Architectural Patterns

#### 1. Clean Architecture with Use Cases
- Each business operation is encapsulated in a use case (e.g., `InsertBill`, `UpdatePaymentStatus`)
- Use cases coordinate between repositories and contain business logic
- ViewModels depend on use cases, not directly on repositories

#### 2. Event-Driven Synchronization
- **EventSyncQueue**: Persistent queue for sync operations
- **SyncEventsWorker**: Background worker that processes sync events
- **Server-Sent Events**: Real-time updates from server are received via HomeViewModel

#### 3. Repository Pattern with Local-First Strategy
- **Local-First**: All read operations flow through local data sources
- **Dual Operations**: Separate methods for local and remote operations
- **Fetch-Then-Store**: Remote fetch operations store results locally

#### 4. Dependency Injection with Koin
- **commonModule()**: Shared dependencies (use cases, repositories, data sources)
- **dispatcherModule()**: Coroutine dispatchers
- **viewModelsModule()**: ViewModels with their dependencies
- **Platform-specific modules**: Platform-specific implementations

### Key Components

#### Synchronization System
- **Local changes** → `EventSyncQueue` → `SyncEventsWorker` → Remote API
- **Remote changes** → Server-sent events → `HomeViewModel` → Local storage
- **Conflict resolution**: Currently uses last-write-wins (needs improvement)

#### Database Schema
- **Room Database** with entities: `BillEntity`, `PaymentEntity`, `EventEntity`
- **Platform-specific database creation**: Android uses Room, Desktop/iOS use SQLite
- **Schema versioning**: Schemas stored in `/schemas` directory

#### Authentication
- **Platform-specific auth**: Android uses Credential Manager, Desktop uses OAuth2 server
- **Session management**: `SessionStorage` handles tokens and user data
- **Auto-login**: Credentials are persisted across app launches

### Platform-Specific Implementations

#### Android (`androidMain/`)
- **WorkManager**: Background sync using `ISyncBillWorker` implementations (components are not being used)
- **Credential Manager**: Google Sign-In integration
- **Material 3**: Android-specific UI components

#### iOS (`iosMain/`, `nativeMain/`)
- **No background sync**: Limited to foreground operations
- **Native iOS auth**: Platform-specific authentication flow
- **UIKit integration**: Native iOS UI components where needed

#### Desktop (`desktopMain/`)
- **Ktor server**: Local OAuth2 callback server for authentication
- **Swing integration**: Desktop-specific UI elements
- **File system**: Direct database file access

### Testing Strategy

#### Unit Tests (`commonTest/`)
- **Repository tests**: Test data layer coordination
- **Use case tests**: Test business logic
- **Domain model tests**: Test entity behavior
- **Mocking**: Uses Mokkery for mocking platform-specific components

#### Integration Tests (`androidInstrumentedTest/`)
- **Database tests**: Test Room database operations
- **Worker tests**: Test background sync functionality
- **UI tests**: Test Compose UI components

#### Test Configuration
- **CommonTestModule**: Shared test dependencies with mocked implementations
- **Platform-specific test modules**: Platform-specific test setup
- **StandardTestDispatcher**: Deterministic coroutine testing

#### Testing Standards (follow these when writing tests)
- **Never change production code while writing or validating tests.** This includes temporary "mutation checks". If a test exposes a bug, report it and leave the fix to a separate production change. Validate only by running Gradle test tasks.
- **Koin setup for every test that needs dependencies.** Use `class XTest : KoinTest, RobolectricTests()` with:
  ```kotlin
  @BeforeTest fun before() {
      startKoin { modules(platformTestModule(), commonModule(), dispatcherTestModule(), commonTestModule() /*, viewModelsModule(), extra test modules */) }
      Dispatchers.setMain(testDispatcher)
  }
  @AfterTest fun after() {
      Dispatchers.resetMain()
      stopKoin()
      currentTestFileName?.let { cleanupDataStoreFile(it) }
  }
  ```
  Inject the unit under test with `by inject()` and mark it with a `// Unit Under Test` comment. ViewModels come from `viewModelsModule()`; never construct them by hand.
- **Mocks live only in `ui/di/CommonTestModule.kt`.** Put each Mokkery mock inside a module function, for example `loginUserMockModule(result)`, `overdueReminderWorkerMockModule()`, `syncEventsUnauthorizedModule(event)` or `authLocalDataSourceMockModule()`. Add it to `startKoin` or load it with `loadKoinModules(...)` inside the test. Don't write hand-written fakes, `object :` fakes or inline `mock<>()` in test classes.
- **Prefer the real chain.** Use the real DataStore test file, the in-memory Room database and `ThePriceApiMock`. Mock only platform components or anything that has to be deterministic (for example the worker's queue and remote data source).
- **Mock server behavior comes from magic values in `ThePriceApiMock`.** For example `BILL_FAILED_ID`, `EXPIRED_TOKEN`, `EXPIRED_REFRESH_TOKEN` and `LOGOUT_FAILED_REFRESH_TOKEN`. Don't override the engine per test.
- **No redundant tests.** Before adding a test, check that no existing test already covers the same behavior through another path. Fold extra assertions on the same flow (for example "dismiss") into one test. Don't test unreachable or dead code, framework or library behavior, or defaults that can't fail.
- **Style:**
  - backtick names: `` `When <condition> it should <outcome>` ``;
  - `// GIVEN` / `// WHEN` / `// THEN` comments;
  - Turbine (`test { awaitItem() }`, `skipItems`, `expectNoEvents`, `ensureAllEventsConsumed`) for flows;
  - `verify`/`verifySuspend` with `VerifyMode` for mocks.
- **Pure models** (enums, value classes) are plain test classes with no Koin, like `DayMonthAndYearTest`.
- **Robolectric is required for Android framework classes.** A test that uses real Android or `java.text` classes, or `getString`, must extend `RobolectricTests()`. Otherwise Android unit tests see empty android.jar stubs.
- **Emission counts depend on DataStore and `combine` timing.** Confirm `skipItems`/`awaitItem` sequences by running the test; don't guess.
- **Validate on every platform:** `./gradlew :composeApp:desktopTest :composeApp:testDebugUnitTest :composeApp:iosSimulatorArm64Test`.

### Important Implementation Details

#### Sync Event Structure
```kotlin
data class SyncEvent(
    val id: String,
    val endpoint: String,    // "bill" or "payment"
    val action: String,      // "create", "update", "delete"
    val payload: String,     // JSON serialized entity
    val createdAt: Long,
)
```

#### Configuration Management
- **BuildKonfig**: Build-time configuration for API URLs and keys
- **local.properties**: Environment-specific configuration (not committed)
- **Required local.properties entries**:
  - `API_BASE_URL`
  - `API_KEY`  
  - `GOOGLE_AUTH_CLIENT_ID_SERVER` (Android)
  - `GOOGLE_AUTH_CLIENT_ID_DESKTOP` (Desktop)
  - `GOOGLE_AUTH_CLIENT_SECRET_DESKTOP` (Desktop)

#### Database Location
- **Android**: Standard Android database location
- **Desktop**: `System.getProperty("java.io.tmpdir")/the_price_app.db`
- **iOS**: iOS Documents directory

### Known Limitations

1. **Sync conflicts**: No sophisticated conflict resolution
2. **Retry mechanism**: Simple delay-based retry without exponential backoff
3. **iOS background sync**: Limited due to iOS restrictions
4. **Event ordering**: No guarantee of event processing order across app restarts
5. **Network awareness**: No connection state monitoring

When working on this codebase, pay special attention to the synchronization system as it's the most complex part of the architecture. Always consider both local and remote data consistency when making changes.