# ThePrice
ThePrice is a budget app I'm building to help me track my monthly payments while learning more about Compose Multiplatform


### Download

[![Get it on Google Play (Internal Testing)](https://img.shields.io/badge/Google%20Play-Internal%20Testing-yellow?logo=google-play&logoColor=white&style=for-the-badge)](https://play.google.com/apps/internaltest/4700519985762408655)

![Download on the App Store (coming soon)](https://img.shields.io/badge/App%20Store-Coming%20Soon-lightgrey?logo=apple&logoColor=white&style=for-the-badge)

### Features



#### Payment Tracking

<img width="1920" height="1052" alt="current_ThePrice" src="https://github.com/user-attachments/assets/f78dfbd9-6abb-4187-9602-5eedf4d1296e" />

<br/>

- :white_check_mark: View payments organized by due date
- :white_check_mark: Month-by-month calendar navigation
- :white_check_mark: Mark payments as paid/unpaid with a single tap
- :white_check_mark: Edit individual payment amounts and dates
- :white_check_mark: Option to update future recurring payments
- :white_check_mark: Jump back to current month quickly
- :white_large_square: View historical payments for any month


#### User Account & Authentication


https://github.com/user-attachments/assets/80e726e2-3ee0-4ec0-9fc2-0475807f080f


<br/>

- :white_check_mark: Email and password registration (I'm not sure if I'll keep it in the app)
- :white_large_square: Email and password login (I'm not sure if I'll keep it in the app)
- :white_check_mark: Google Sign-In integration
- :white_large_square: iCloud Sign-In integration
- :white_large_square: User profile management
- :white_check_mark: Secure session management
- :white_large_square: Logout functionality





#### Synchronization


https://github.com/user-attachments/assets/36599d78-a878-464d-803a-1ad8a87cea3e


<br/>

- :white_check_mark: **Offline-first**: All operations work without internet connection
- :white_check_mark: **Automatic sync**: Changes sync automatically when online
- :construction: **Real-time updates**: Receive updates from other devices instantly via SSE
- :construction: **Conflict handling**: Smart conflict resolution for concurrent edits

<br/>


### Technologies

*   **[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)** for sharing code across Android, iOS, and Desktop
*   User Interface built with **[Jetpack Compose](https://developer.android.com/jetpack/compose)** and **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)**
*   **Clean Architecture** with separation of Domain, Data, and UI layers
*   **MVI (Model-View-Intent)** pattern with **ViewModel** per screen
*   Local persistence with **[Room Database](https://developer.android.com/training/data-storage/room)**
*   Networking with **[Ktor Client](https://ktor.io/docs/client.html)** for HTTP requests and SSE
*   Type-safe navigation with **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)**
*   Image loading with **[Coil](https://coil-kt.github.io/coil/)** for user profile pictures
*   Session management with **[DataStore](https://developer.android.com/topic/libraries/architecture/datastore)**
*   Background sync with **[WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)** (Android)
*   Date/time handling with **[Kotlinx DateTime](https://github.com/Kotlin/kotlinx-datetime)**
*   JSON serialization with **[Kotlinx Serialization](https://kotlinlang.org/docs/serialization.html)**
*   **Event-driven synchronization** with real-time updates via Server-Sent Events (SSE)
*   **Offline-first architecture** with automatic sync when connectivity returns
*   Reactive UIs using **[StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/)** and **[Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)** for asynchronous operations
*   Dependency injection using **[Koin](https://insert-koin.io/)**
*   **[Google Sign-In](https://developers.google.com/identity)** integration for authentication
*   **[Material 3](https://m3.material.io/)** design components and icons

#### Testing
*   **[JUnit 4](https://junit.org/junit4/)** for unit testing framework
*   **[Mokkery](https://mokkery.dev/)** for mocking in Kotlin Multiplatform tests
*   **[Turbine](https://github.com/cashapp/turbine)** for testing Kotlin Flows
*   **[Kotlinx Coroutines Test](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/)** for testing coroutines
*   **[Compose UI Test](https://developer.android.com/jetpack/compose/testing)** for UI testing
*   **[Robolectric](http://robolectric.org/)** for Android unit tests
*   **[WorkManager Test](https://developer.android.com/topic/libraries/architecture/workmanager/how-to/integration-testing)** for testing background workers

<br/>

