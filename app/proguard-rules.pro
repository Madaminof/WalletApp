# Hilt uchun
-keep class dagger.hilt.** { *; }
-keep class com.google.dagger.** { *; }

# Room uchun
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.paging.** { *; }

# Retrofit & OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep class retrofit2.** { *; }
-keep class com.squareup.okhttp3.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }