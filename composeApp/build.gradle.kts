import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.buildkonfig)
}

val commonPackageName = "org.wdsl.witness"

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.browser)

            /* MEDIA 3 */
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.ktx)

            implementation(libs.play.services.location)
            implementation(libs.androidx.biometric)

            /* KTOR */
            implementation(libs.ktor.client.okhttp)

            /* WEARABLE */
            implementation(libs.play.services.wearable)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.material.icons.extended)

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.androidx.window.core)
            implementation(libs.androidx.savedstate.compose)

            /* SERIALIZATION */
            implementation(libs.serialization.core)
            implementation(libs.serialization.json)
            implementation(libs.serialization.protobuf)

            /* NAVIGATION 3 */
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)

            /* ROOM */
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.room.runtime)

            /* DATASTORE */
            implementation(libs.androidx.datastore.core)

            /* KOTLINX */
            implementation(libs.kotlinx.io.core)
            implementation(libs.kotlinx.io.okio)
            implementation(libs.kotlinx.datetime)

            /* KTOR */
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)

            /* COIL */
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)

            /* MAPLIBRE */
            implementation(libs.maplibre.compose)
            implementation(libs.maplibre.composeMaterial3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = commonPackageName
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = commonPackageName
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

buildkonfig {
    packageName = commonPackageName
    objectName = "WitnessBuildConfig"

    defaultConfigs {
        buildConfigField(
            BOOLEAN,
            "DEBUG_MODE",
            if (project.hasProperty("release")) "false" else "true",
            nullable = false,
            const = true,
        )
        buildConfigField(
            STRING,
            "PACKAGE_NAME",
            commonPackageName,
            nullable = false,
            const = true,
        )
        val lp = rootProject.file("local.properties")
        val geminiApiKey: String? =
            if (lp.exists()) {
                Properties().apply { load(lp.inputStream()) }
                    .getProperty("geminiApiKey") ?: ""
            } else ""

        buildConfigField(
            STRING,
            "GEMINI_API_KEY",
            geminiApiKey,
            nullable = false,
            const = true,
        )
    }
}
