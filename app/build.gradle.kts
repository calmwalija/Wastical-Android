import org.jetbrains.kotlin.konan.properties.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  id("com.google.devtools.ksp")
  id("com.diffplug.spotless")
  id("dagger.hilt.android.plugin")
  alias(libs.plugins.google.gms.google.services)
}

android {
  namespace = "net.techandgraphics.wastemanagement"
  compileSdk = 35

  defaultConfig {
    applicationId = "net.techandgraphics.wastemanagement"
    minSdk = 27
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").inputStream())
    buildConfigField("String", "API_DOMAIN", """${localProperties["API_DOMAIN"]}""")

  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
  }
  spotless {
    kotlin {
      target("**/*.kt")
      trimTrailingWhitespace()
      endWithNewline()
      suppressLintsFor {
        step = "ktlint"
        shortCode = "standard:function-naming"
      }
      ktlint("1.0.0")
        .editorConfigOverride(
          mapOf(
            "indent_size" to 2,
            "continuation_indent_size" to "2",
          )
        )
    }
    format("xml") {
      target("**/*.xml")
      trimTrailingWhitespace()
      endWithNewline()
    }
  }
}

dependencies {
  implementation(project(":deps"))
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  implementation(libs.hilt.android)
  ksp(libs.hilt.android.compiler)
}