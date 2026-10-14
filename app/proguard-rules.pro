# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\<user>\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Preserve SQLite database helper reflections and models
-keepclassmembers class * extends android.database.sqlite.SQLiteOpenHelper {
    public <init>(...);
}

# Preserve serialization and model entities
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
