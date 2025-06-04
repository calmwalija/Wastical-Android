plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "net.techandgraphics.deps"
  compileSdk = 35

  defaultConfig {
    minSdk = 27
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlinOptions {
    jvmTarget = "17"
  }
}


dependencies {

  // Compose + UI dependencies exposed to app module
  api(libs.androidx.core.ktx)
  api(libs.androidx.lifecycle.runtime.ktx)
  api(libs.androidx.activity.compose)
  api(platform(libs.androidx.compose.bom))
  api(libs.androidx.ui)
  api(libs.androidx.ui.graphics)
  api(libs.androidx.ui.tooling.preview)
  api(libs.androidx.material3)

  debugApi(libs.androidx.ui.tooling)
  debugApi(libs.androidx.ui.test.manifest)

  // Navigation dependencies
  api(libs.androidx.navigation.runtime.ktx)
  api(libs.androidx.navigation.compose)

  // Paging dependencies
  api(libs.androidx.paging.runtime)
  api(libs.androidx.paging.compose)

  // DataStore preferences
  api(libs.androidx.datastore.preferences)

  // Kotlinx Serialization
  api(libs.kotlinx.serialization.json)

  // Image loading with Coil
  api(libs.coil.compose)

  // JWT
  api(libs.jjwt)

  // Hilt (Dependency Injection) dependencies, but without KSP here
  api(libs.hilt.android)
  api(libs.androidx.hilt.navigation.compose)

  // Room database dependencies
  api(libs.androidx.room.runtime)
  api(libs.androidx.room.paging)
  api(libs.androidx.room.ktx)

  // Ktor HTTP client dependencies
  api(libs.ktor.client.core)
  api(libs.ktor.client.cio)
  api(libs.ktor.client.websockets)
  api(libs.ktor.client.content.negotiation)
  api(libs.ktor.client.logging)
  api(libs.ktor.client.okhttp)
  api(libs.ktor.serialization.gson)

  // Firebase messaging and analytics
  api(libs.firebase.messaging)
  api(libs.firebase.analytics.ktx)

  // Miscellaneous libraries
  api(libs.text.recognition)
  api(libs.accompanist.drawablepainter)
  api(libs.ucrop)
  api(libs.play.services.auth)

  // WorkManager dependencies exposed to app module
  api(libs.androidx.work.runtime.ktx)
  api(libs.androidx.hilt.work)

}
