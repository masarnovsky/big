# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep all annotations
-keepattributes *Annotation*

# Keep generic signature for reflection
-keepattributes Signature

# ===== Kotlin Coroutines =====
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ===== Room Database =====
# Keep the Room entities
-keep class com.masarnovsky.big.mvvm.model.TextEntity { *; }

# Keep the DAO interfaces
-keep interface com.masarnovsky.big.mvvm.model.TextDao { *; }

# Keep the Database class
-keep class com.masarnovsky.big.mvvm.model.TextDatabase { *; }

# Room uses annotation processing, keep these
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ===== DataStore =====
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}
-keep class com.masarnovsky.big.mvvm.model.UserPreferencesManager { *; }

# ===== Jetpack Compose =====
# Keep all Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }
-keep class androidx.compose.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}
-keep class com.masarnovsky.big.mvvm.viewmodel.MainViewModel { *; }

# ===== Enums =====
# Keep enum classes and their values
-keepclassmembers enum com.masarnovsky.big.mvvm.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    **[] $VALUES;
    public *;
}

# ===== Keep Intent Extras =====
# Prevent obfuscation of Activity classes that use Intent extras
-keep class com.masarnovsky.big.mvvm.view.** { *; }

# ===== General Android =====
# Keep all Activities, Services, and BroadcastReceivers
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# Preserve the special static methods that are required in all enumeration classes
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===== Optimization =====
# Allow optimization but preserve important attributes
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify