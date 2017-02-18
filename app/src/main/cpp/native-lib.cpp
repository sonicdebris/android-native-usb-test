#include <jni.h>
#include <string>
#include <sstream>
#include "usbhost.h"
#include <cassert>

#include <android/log.h>
#define LOG(...)  __android_log_print(ANDROID_LOG_INFO, "Usb test", __VA_ARGS__)

std::string toString(char* cstr)
{
    if (cstr == nullptr)
        return "null";
    std::string str(cstr);
    free(cstr);
    return str;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_test_usb_sonicdebris_usbtest_MainActivity_testUsbDeviceNative(JNIEnv *env, jobject thiz, jint fd, jstring jname)
{
    std::stringstream res;

    LOG("Create device...");

    usb_host_context* ctx = usb_host_init();
    assert(ctx != nullptr);
    usb_device* dev = usb_device_new("test",fd);
    assert(dev != nullptr);

    LOG("Get general info...");

    res << std::hex << std::endl
        << usb_device_get_vendor_id(dev) << ":"
        << std::hex << usb_device_get_product_id(dev) << "\t\t"
        << toString(usb_device_get_manufacturer_name(dev)) << " "
        << toString(usb_device_get_product_name(dev))
        << std::endl
        << "USB version:\t\t\t" << usb_device_get_version(dev) // hex!
        << std::dec << std::endl
        << "Serial number:\t\t" << toString(usb_device_get_serial(dev))
        << std::endl
        << "Writeable:\t\t\t\t" << usb_device_is_writeable(dev)
        << std::endl;

    LOG("iterate through descriptors...");

    usb_descriptor_iter it;
    usb_descriptor_iter_init(dev,&it);

    usb_descriptor_header* h = usb_descriptor_iter_next(&it);
    int i = 0;
    while (h != nullptr)
    {
        res << "USB descriptor "<< i
            << ".\t\ttype: " << ((int)h->bDescriptorType)
            << "\t\tlen: " << ((int)h->bLength) << std::endl;

        h = usb_descriptor_iter_next(&it);
        i++;
    }

    usb_host_cleanup(ctx);

    LOG("Done enumerating USB device properties");

    return env->NewStringUTF(res.str().c_str());
}
