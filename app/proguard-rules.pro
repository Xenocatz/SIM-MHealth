# Add project specific ProGuard rules here.

-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# ========== CRITICAL FIX for Retrofit Generic Types ==========
# This fixes: java.lang.Class cannot be cast to java.lang.reflect.ParameterizedType

# Keep generic signature of Call, Response
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# ========== Retrofit Core ==========
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-dontwarn javax.annotation.**
-keep class retrofit2.** { *; }

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep API Service
-keep interface com.example.sim_mhealth.data.api.ApiService { *; }

# ========== Gson ==========
-keepattributes Signature
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

# Keep all model classes used with Gson
-keep class com.example.sim_mhealth.data.api.** { <fields>; }

# Prevent Gson from stripping generic types
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ========== Keep ALL API Request/Response Classes ==========
-keep class com.example.sim_mhealth.data.api.LoginRequest { *; }
-keep class com.example.sim_mhealth.data.api.LoginResponse { *; }
-keep class com.example.sim_mhealth.data.api.LoginData { *; }
-keep class com.example.sim_mhealth.data.api.User { *; }
-keep class com.example.sim_mhealth.data.api.RegisterRequest { *; }
-keep class com.example.sim_mhealth.data.api.RegisterResponse { *; }
-keep class com.example.sim_mhealth.data.api.RegisterData { *; }
-keep class com.example.sim_mhealth.data.api.PasienResponse { *; }
-keep class com.example.sim_mhealth.data.api.PasienDetail { *; }
-keep class com.example.sim_mhealth.data.api.OnBoardingRequest { *; }
-keep class com.example.sim_mhealth.data.api.OnBoardingResponse { *; }
-keep class com.example.sim_mhealth.data.api.OnBoardingData { *; }
-keep class com.example.sim_mhealth.data.api.UpdateProfileRequest { *; }
-keep class com.example.sim_mhealth.data.api.UpdateProfileResponse { *; }
-keep class com.example.sim_mhealth.data.api.PengingatResponse { *; }
-keep class com.example.sim_mhealth.data.api.PengingatDetailResponse { *; }
-keep class com.example.sim_mhealth.data.api.PengingatItem { *; }
-keep class com.example.sim_mhealth.data.api.PengingatDetail { *; }
-keep class com.example.sim_mhealth.data.api.DetailPengingat { *; }
-keep class com.example.sim_mhealth.data.api.CreatePengingatRequest { *; }
-keep class com.example.sim_mhealth.data.api.UpdatePengingatRequest { *; }
-keep class com.example.sim_mhealth.data.api.CreatePengingatResponse { *; }
-keep class com.example.sim_mhealth.data.api.UpdatePengingatResponse { *; }
-keep class com.example.sim_mhealth.data.api.DeletePengingatResponse { *; }
-keep class com.example.sim_mhealth.data.api.UpdateStatusRequest { *; }
-keep class com.example.sim_mhealth.data.api.UpdateStatusResponse { *; }
-keep class com.example.sim_mhealth.data.api.VerifyUsernameRequest { *; }
-keep class com.example.sim_mhealth.data.api.VerifyUsernameResponse { *; }
-keep class com.example.sim_mhealth.data.api.ChangePasswordRequest { *; }
-keep class com.example.sim_mhealth.data.api.ChangePasswordResponse { *; }

# ========== Keep Repository ==========
-keep class com.example.sim_mhealth.data.repository.** { *; }

# ========== OkHttp ==========
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ========== Kotlin Coroutines ==========
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ========== Kotlin Metadata ==========
-keep class kotlin.Metadata { *; }

# ========== Generative AI ==========
-keep class com.google.ai.client.generativeai.** { *; }

# ========== Enums ==========
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}