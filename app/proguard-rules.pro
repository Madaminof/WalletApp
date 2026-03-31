# 1. R8 JARAYONINI BARQARORLASHTIRISH
# Agressiv optimizatsiyani o'chirib, barqarorlikni oshiramiz
-dontoptimize
-ignorewarnings

# 2. ASOSIY LOYIHA KODINI HIMOYA QILISH
# O'zing yozgan klasslarni R8 o'zgartirib yubormasligi uchun
-keep class dev.samandar.walletapp.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*, LineNumberTable

# 3. HILT VA DAGGER (To'liq saqlash)
-keep class dagger.hilt.** { *; }
-keep class com.google.dagger.** { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep @androidx.hilt.work.HiltWorker class * { *; }
-keep class androidx.hilt.work.** { *; }

# 4. ROOM DATABASE
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * {
    @androidx.room.Entity *;
    @androidx.room.PrimaryKey *;
    @androidx.room.ColumnInfo *;
    @androidx.room.Ignore *;
    @androidx.room.TypeConverter *;
}

# 5. RETROFIT, OKHTTP VA GSON
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**

# 6. MUAMMO KELTIRIB CHIQARUVCHI EKSPERIMENTALLAR (PDF, Excel, Chart)
# Bu kutubxonalar eski va R8 ularni o'zgartirsa build "qulashi" mumkin
-keep class com.github.PhilJay.charting.** { *; }
-keep class com.lowagie.** { *; }
-keep class jxl.** { *; }
-dontwarn com.lowagie.**
-dontwarn jxl.**
-dontwarn org.bouncycastle.**

# 7. JETPACK COMPOSE VA RESURSLAR
-keep class androidx.compose.** { *; }
-keep class androidx.ui.** { *; }
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 8. FIREBASE VA NAVIGATSIYA
-keep class com.google.firebase.** { *; }
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**