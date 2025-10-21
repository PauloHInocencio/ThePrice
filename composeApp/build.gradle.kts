import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.internal.utils.localPropertiesFile
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.util.Properties
import java.io.File

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidxRoom)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytic)
    alias(libs.plugins.skie)
}


val localProps = Properties().apply {
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        load(localPropsFile.inputStream())
    } else {
        // Fallback dummy values for CI/environments without local.properties
        setProperty("API_BASE_URL", "https://dummy-api.example.com/api/v1/")
        setProperty("API_KEY", "dummy_api_key_for_tests")
        setProperty("GOOGLE_AUTH_CLIENT_ID_SERVER", "dummy_client_id_server")
        setProperty("GOOGLE_AUTH_CLIENT_ID_DESKTOP", "dummy_client_id_desktop")
        setProperty("GOOGLE_AUTH_CLIENT_SECRET_DESKTOP", "dummy_client_secret_desktop")
        setProperty("GOOGLE_AUTH_CLIENT_ID_IOS", "dummy_client_id_ios")
        setProperty("GOOGLE_AUTH_IOS_REDIRECT_URI", "dummy.bundle.id:/oauth2redirect/google")
    }
}

kotlin {
    applyDefaultHierarchyTemplate()  // <- ensures iosMain/iosTest exist

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            sourceSetTree.set(KotlinSourceSetTree.test)
            dependencies {
                implementation(libs.compose.ui.test.junit4.android)
                implementation(libs.compose.ui.test.manifest)
                implementation(libs.androidx.test.runner)
                //implementation(libs.robolectric)
            }
        }
        compilerOptions{
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.workmanager)
            implementation(libs.koin.androidx.compose)
            implementation(libs.kotlinx.coroutines.android)

            //implementation(libs.mockk)

            //Credential Manager
            implementation(libs.credentials)
            implementation(libs.credentials.auth)
            implementation(libs.googleid)

            //Work Manager
            implementation(libs.androidx.workmanager)

            //Ktor
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.android)

            //Room
            implementation(libs.androidx.sqlite.native)

            //Firebase
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.analytics)
        }


        commonMain.dependencies {

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.navigation)
            implementation(libs.compose.material.icons)
            implementation(compose.materialIconsExtended)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Kotlinx Date Time
            implementation(libs.kotlinx.datetime)

            // ViewModel
            implementation(libs.lifecycle.viewmodel)
            //implementation(libs.lifecycle.compose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

            // DataStore
            implementation(libs.datastore.preferences)
            implementation(libs.datastore)

            // Ktor
            implementation(libs.bundles.ktor)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            // Ktor
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.jvm)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.netty)
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.test.core)
                implementation(libs.compose.ui.test.junit4.android)
                implementation(libs.compose.ui.test.manifest)
                implementation(libs.robolectric)
                implementation(libs.androidx.workmanager.test)
                implementation(libs.mockk)
            }
        }

        commonTest.dependencies {

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)

            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.utils)
        }

        commonMain {
            // Fixes RoomDB unresolved reference 'instantiateImpl' in iosMain
            // Due to https://issuetracker.google.com/u/0/issues/342905180
            kotlin.srcDir("build/generated/ksp/metadata")
        }
    }
    task("testClasses") // work-around of the bug 'Cannot locate tasks that match :shared:testClasses'
}

android {
    namespace = "br.com.noartcode.theprice"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        resValue("string", "app_name", "ThePrice")
        applicationId = "br.com.noartcode.theprice"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "br.com.noartcode.theprice.InstrumentationTestRunner"
    }
    
    testOptions.unitTests.isIncludeAndroidResources = true
    testOptions.unitTests.isReturnDefaultValues = true

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
    buildTypes {
        getByName("release") {
            resValue("string", "app_name", "ThePrice")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            resValue("string", "app_name", "ThePrice(Debug)")
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures {
        compose = true
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "br.com.noartcode.theprice"
            packageVersion = "1.0.0"
            macOS {
                jvmArgs("-Dapple.awt.UIElement=true") // hide docker icon
                //jvmArgs("-Dapple.awt.enableTemplateImages=true") // Implementing Automatic Icon Color Switching
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    // Update: https://issuetracker.google.com/u/0/issues/342905180
    //add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroidAndroidTest",  libs.androidx.room.compiler) // Android instrumentation test
    add("kspAndroidTest",  libs.androidx.room.compiler) // Android unit test
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

buildkonfig {
    packageName = "br.com.noartcode.theprice"

    defaultConfigs{
        buildConfigField(STRING, "apiBaseUrl", localProps["API_BASE_URL"] as String)
        buildConfigField(STRING, "apiKey", localProps["API_KEY"] as String)
    }

    targetConfigs {
        create("android"){
            buildConfigField(STRING, "googleAuthClientId", localProps["GOOGLE_AUTH_CLIENT_ID_SERVER"] as String)
        }

        create("desktop") {
            buildConfigField(STRING, "googleAuthClientId", localProps["GOOGLE_AUTH_CLIENT_ID_DESKTOP"] as String)
            buildConfigField(STRING, "googleAuthSecret", localProps["GOOGLE_AUTH_CLIENT_SECRET_DESKTOP"] as String)
        }

        create("ios") {
            buildConfigField(STRING, "googleAuthClientId", localProps["GOOGLE_AUTH_CLIENT_ID_IOS"] as String)
            buildConfigField(STRING, "iosRedirectURI", localProps["GOOGLE_AUTH_IOS_REDIRECT_URI"] as String)
        }
    }
}

tasks.register("cleanDesktopDatabase") {
    group = "cleanup"
    description = "Deletes the Room database file used in the Desktop app"

    doLast {
        val dbFileName = "the_price_app.db" // Replace with your actual file name
        val dbFile = File(System.getProperty("java.io.tmpdir"), dbFileName)
        if (dbFile.exists()) {
            println("Deleting database at: ${dbFile.absolutePath}")
            val deleted = dbFile.delete()
            if (deleted) {
                println("Database deleted successfully.")
            } else {
                println("Failed to delete the database.")
            }
        } else {
            println("No database file found at: ${dbFile.absolutePath}")
        }
    }
}