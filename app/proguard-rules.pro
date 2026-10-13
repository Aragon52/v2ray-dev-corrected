# Keep the gomobile / gobind bridge for libv2ray (Xray core) intact so R8
# does not strip the JNI-referenced classes or the go.Seq runtime.
-keep class libv2ray.** { *; }
-keep class go.** { *; }
-keep class com.aiv2ray.client.service.XrayController { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
-dontwarn libv2ray.**
-dontwarn go.**
