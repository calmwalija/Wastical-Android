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

    testInstrumentationRunner = "net.techandgraphics.wastemanagement.HiltTestRunner"

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


  sourceSets {
    named("androidTest") {
      dependencies {
        implementation("app.cash.turbine:turbine:1.1.0")
      }
    }
  }

  testOptions {
    unitTests.all {
      it.jvmArgs("-XX:+EnableDynamicAgentLoading")
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
  implementation(libs.firebase.messaging)
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

  implementation(libs.hilt.android)
  ksp(libs.hilt.android.compiler)
  implementation(libs.androidx.hilt.navigation.compose)

  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.room.paging)
  implementation(libs.androidx.room.ktx)



  implementation(libs.retrofit)
  implementation(libs.converter.gson)
  implementation(libs.logging.interceptor)
  implementation(libs.okhttp)


  implementation("com.google.mlkit:text-recognition:16.0.1")
  implementation("com.google.accompanist:accompanist-drawablepainter:0.37.2")
  implementation("com.github.yalantis:ucrop:2.2.10")

  implementation("androidx.work:work-runtime-ktx:2.10.0")
  implementation("androidx.hilt:hilt-work:1.0.0")
  ksp("androidx.hilt:hilt-compiler:1.0.0")

  implementation(libs.firebase.analytics.ktx)

  implementation("com.google.android.gms:play-services-auth:21.3.0")

  implementation("io.github.ehsannarmani:compose-charts:0.1.7")
  testImplementation(libs.androidx.room.testing)

  androidTestImplementation("com.google.dagger:hilt-android-testing:2.51")
  kspAndroidTest("com.google.dagger:hilt-compiler:2.50")
  testImplementation(kotlin("test"))

  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
  testImplementation("org.slf4j:slf4j-simple:2.0.9")
  testImplementation("io.mockk:mockk:1.13.10")
  testImplementation("net.bytebuddy:byte-buddy:1.14.13")


}