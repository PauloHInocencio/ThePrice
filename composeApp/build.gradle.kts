import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidxRoom)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
}

kotlin {
    androidTarget {

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant{
            sourceSetTree.set(KotlinSourceSetTree.test)
            dependencies {
                implementation(libs.compose.ui.test.junit4.android)
                implementation(libs.compose.ui.test.manifest)
                implementation(libs.androidx.test.runner)
                implementation(libs.roboletric)
            }
        }

        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
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
            implementation(libs.koin.androidx.compose)
            implementation(libs.kotlinx.coroutines.android)

            implementation(libs.mockk)

            implementation(libs.credentials)
            implementation(libs.credentials.auth)
            implementation(libs.googleid)

            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.android)
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

        nativeMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.jvm)
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.test.core)
                implementation(libs.junit)
                implementation(libs.roboletric)
            }
        }

        commonTest.dependencies {

            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)

            implementation(libs.kotlin.test)
            implementation(libs.turbine)
            implementation(libs.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
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
        applicationId = "br.com.noartcode.theprice"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    testOptions.unitTests.isIncludeAndroidResources = true

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
            isMinifyEnabled = false
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
