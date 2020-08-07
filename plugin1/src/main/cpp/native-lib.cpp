#include <jni.h>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_dj_plugin_plugin1_SignUtils_getSecret(JNIEnv *env, jclass type) {
    return env->NewStringUTF("123456789");
}