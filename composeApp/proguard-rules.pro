# =========================
# Core metadata to keep
# =========================
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod, Exceptions, SourceFile, LineNumberTable

# Respect @Keep
-keep @androidx.annotation.Keep class ** { *; }
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
    @androidx.annotation.Keep <fields>;
}

# Parcelable (@Parcelize & manual)
-keepclassmembers class ** implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# If any enums use .name() comparisons
-keepclassmembers enum ** { *; }

# Preserve source/line info (crash readability)
-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable

# =========================
# Kotlin / Coroutines
# =========================
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**

# =========================
# Koin (DI uses reflection)
# =========================
-dontwarn org.koin.**
-keep class org.koin.** { *; }
# If you use Koin annotations:
-keepclassmembers class ** {
    @org.koin.core.annotation.* <fields>;
    @org.koin.core.annotation.* <methods>;
}

# =========================
# WorkManager
# (AndroidX ships rules, but keep workers defensively)
# =========================
-keep class ** extends androidx.work.ListenableWorker { *; }
-keep class ** extends androidx.work.CoroutineWorker { *; }
-dontwarn androidx.work.**

# =========================
# Room
# (Consumer rules exist; a couple of safe extras)
# =========================
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**
-dontwarn androidx.sqlite.**
-dontwarn androidx.sqlite.db.**

# =========================
# Networking: Ktor + OkHttp/Okio
# =========================
-dontwarn io.ktor.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn okio.internal.**


# =========================
# JSON: kotlinx.serialization
# (Codegen usually fine; keep annotations just in case)
# =========================
# --- kotlinx.serialization: keep generated serializer classes & companions ---
-dontwarn kotlinx.serialization.**
#-keep @kotlinx.serialization.Serializable class * {*;}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}


# ---- CRITICAL: keep the generated $$serializer *names* (don't let R8 rename them)
-keepnames class **$$serializer

# Keep the factory methods so reflection can find them (names must stay "serializer")
-keepclassmembers class ** {
    public static kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class **$Companion {
    public kotlinx.serialization.KSerializer serializer(...);
}

# =========================
# Coil (image loading)
# =========================
-dontwarn coil.**
-dontwarn coil.decode.**
-dontwarn coil.request.**

# =========================
# Datastore (preferences / proto)
# (Usually has consumer rules; suppress noisy warnings)
# =========================
-dontwarn androidx.datastore.**
-dontwarn com.google.protobuf.**

# =========================
# AndroidX / Lifecycle / Compose
# (Compose typically needs no custom rules)
# =========================
-dontwarn androidx.lifecycle.**
# Avoid blanket -dontwarn for compose unless you hit something specific.

# =========================
# Google Credentials / Google Sign-In
# (They ship rules; suppress if you see warnings)
# =========================
-dontwarn androidx.credentials.**
-dontwarn com.google.android.libraries.identity.**
-dontwarn com.google.android.gms.**

