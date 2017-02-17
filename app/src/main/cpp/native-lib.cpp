#include <jni.h>
#include <string>
#include "usbhost.h"
#include <cassert>

extern "C"
JNIEXPORT jstring JNICALL
Java_test_usb_sonicdebris_usbtest_MainActivity_testUsbDeviceNative(JNIEnv *env, jobject thiz, jint fd, jstring jname)
{
    std::string res;

    usb_host_context* ctx = usb_host_init();
    assert(ctx != nullptr);
    usb_device* dev = usb_device_new("test",fd);
    assert(dev != nullptr);

    char* man = usb_device_get_manufacturer_name(dev);
    char* prod = usb_device_get_product_name(dev);

    res = man;
    res += " ";
    res += prod;

    free(man);
    free(prod);

    return env->NewStringUTF(res.c_str());
}
