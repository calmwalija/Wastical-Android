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
  id("jacoco")
}

android {
  namespace = "net.techandgraphics.wastical"
  compileSdk = 35

  defaultConfig {
    applicationId = "net.techandgraphics.wastical"
    minSdk = 27
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "net.techandgraphics.wastical.HiltTestRunner"

    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").inputStream())
    buildConfigField("String", "DEV_API_DOMAIN", """${localProperties["DEV_API_DOMAIN"]}""")
    buildConfigField("String", "PROD_API_DOMAIN", """${localProperties["PROD_API_DOMAIN"]}""")
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


jacoco {
  toolVersion = "0.8.11"
}

tasks.withType<Test> {
  finalizedBy("jacocoTestReport")
}

tasks.register<JacocoReport>("jacocoTestReport") {
  dependsOn("testDebugUnitTest")

  reports {
    xml.required.set(true)
    html.required.set(true)
  }

  val fileFilter = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*"
  )

  val javaClasses = layout.buildDirectory.dir("intermediates/javac/debug/classes")

  classDirectories.setFrom(
    javaClasses.map {
      fileTree(it) {
        exclude(fileFilter)
      }
    }
  )

  sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

  executionData.setFrom(
    layout.buildDirectory.file("jacoco/testDebugUnitTest.exec")
  )
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
  implementation(libs.androidx.core.splashscreen)



  implementation(libs.retrofit)
  implementation(libs.converter.gson)
  implementation(libs.logging.interceptor)
  implementation(libs.okhttp)


  implementation(libs.accompanist.drawablepainter)

  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.androidx.hilt.work)
  ksp(libs.androidx.hilt.compiler)

  implementation(libs.firebase.analytics.ktx)

  testImplementation(libs.androidx.room.testing)

  androidTestImplementation(libs.hilt.android.testing)
  kspAndroidTest(libs.hilt.compiler)
  testImplementation(kotlin("test"))

  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.slf4j.simple)
  testImplementation(libs.mockk)
  testImplementation(libs.byte.buddy)

  implementation(libs.cropify)

  implementation(libs.java.jwt)

  implementation(libs.accompanist.systemuicontroller)


  implementation(libs.play.services.auth)

  // QR code generation
  implementation("com.google.zxing:core:3.5.3")

}