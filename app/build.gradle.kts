import org.jetbrains.kotlin.konan.properties.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  id("com.google.devtools.ksp")
  id("com.diffplug.spotless")
  id("dagger.hilt.android.plugin")
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

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)


  implementation(libs.androidx.navigation.runtime.ktx)
  implementation(libs.androidx.navigation.compose)


  implementation(libs.androidx.datastore.preferences)

  implementation(libs.kotlinx.serialization.json)

  implementation(libs.jjwt)

  implementation(libs.coil.compose)


  implementation(libs.androidx.paging.runtime)
  implementation(libs.androidx.paging.compose)

  implementation(libs.retrofit)
  implementation(libs.converter.gson)
  implementation(libs.logging.interceptor)

  implementation(libs.okhttp)
  implementation(libs.hilt.android)
  ksp(libs.hilt.android.compiler)
  implementation(libs.androidx.hilt.navigation.compose)

  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.room.paging)
  implementation(libs.androidx.room.ktx)


  implementation(libs.ktor.client.core)
  implementation(libs.ktor.client.cio)
  implementation(libs.ktor.client.websockets)

  implementation("com.google.mlkit:text-recognition:16.0.1")
  implementation("io.github.mr0xf00:easycrop:0.1.1")
  implementation ("com.google.accompanist:accompanist-drawablepainter:0.37.2")


}