// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  dependencies {
    classpath(libs.hilt.android.gradle.plugin)
    classpath(libs.spotless.plugin.gradle)
  }
}
allprojects {
  tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
  }
}
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.android.library) apply false
  id("com.google.devtools.ksp") version "2.0.0-1.0.21"
  alias(libs.plugins.google.gms.google.services) apply false
}