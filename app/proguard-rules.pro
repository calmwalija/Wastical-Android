# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep generic type information and runtime annotations (needed by Retrofit/Gson)
-keepattributes Signature,Exceptions,InnerClasses,EnclosingMethod
-keepattributes Signature,Exceptions,InnerClasses,EnclosingMethod,MethodParameters
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations
-keepattributes AnnotationDefault

# ---- Retrofit 2 ----
# Keep Retrofit interfaces and their method annotations
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

# Retrofit and OkHttp/Okio
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# Kotlin coroutines / suspend support for Retrofit
-keep class kotlin.coroutines.** { *; }
-dontwarn kotlin.coroutines.**
-keep class kotlin.Unit

# Ensure Retrofit Kotlin extensions (if referenced) are kept
-keep class retrofit2.KotlinExtensions { *; }
-keep class retrofit2.KotlinExtensions$* { *; }

# Optionally keep Retrofit/OkHttp classes (safe with R8 shrinking)
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# ---- Gson ----
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken

# Keep model classes used only reflectively by Gson/Retrofit & Gson/Room (e.g., MonthYear)
-keep class net.techandgraphics.** { *; }

# If you have enums serialized by name via Gson
-keepclassmembers enum * { *; }

# WorkManager/Hilt/Firebase provide consumer rules; keep common annotations just in case
-dontwarn dagger.**
-dontwarn javax.inject.**
-dontwarn androidx.lifecycle.**
