plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.kapt.plugin)
    alias(libs.plugins.kotlin.serialization.plugin)
    id("maven-publish")
}

val hamrahAdsSdkVersion = "0.1.42"

android {
    namespace = "ir.ayantech.hamrahads"
    compileSdk = 36

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "HAMRAHADS_SDK_VERSION", "\"$hamrahAdsSdkVersion\"")
    }

    buildFeatures {
        buildConfig = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

val cleanDebugLibraryClassesJar by tasks.registering {
    doLast {
        val jarFile = layout.buildDirectory.file(
            "intermediates/compile_library_classes_jar/debug/bundleLibCompileToJarDebug/classes.jar"
        ).get().asFile
        if (!jarFile.exists()) return@doLast

        repeat(30) {
            runCatching {
                jarFile.delete()
            }
            if (!jarFile.exists()) return@doLast
            Thread.sleep(200)
        }
    }
}

tasks.matching { it.name == "bundleLibCompileToJarDebug" }.configureEach {
    dependsOn(cleanDebugLibraryClassesJar)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.ayantech"
                artifactId = "HamrahAds"
                version = hamrahAdsSdkVersion
            }
        }
    }
}


dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(platform(libs.okhttp.bom))
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.datastore)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.play.services.ads.identifier)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.gif)
    implementation(libs.sdp.android)
}



