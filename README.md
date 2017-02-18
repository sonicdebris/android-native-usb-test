# Android native usb test

This is a proof of concept, using android's libusbhost sources to access a USB device from native code.

Libusbhost is a sort of stripped down version of Libusb, it's part of the android AOSP and consists of just a source and a header file. 

Of course, being not exposed by the ndk, we must grab the sources and build it ourselves if we want to use the "library".

A tiny hack was needed in usbhost.h to include ch9.h, present in the ndk but not accessible for building. It should be safe to just copy ch9.h to our project (as I did: see ch9_custom.h), since it contains USB standard definitions and structs to hold descriptors data, none of which is actually passed to the underlying ioctl api. But I will have to double check this. :)

Currently, the app just takes the file descriptor opened in java and gets some basic info from the device.

####TODO
- Query device descriptors
- Test bulk transfers
- Add isochronous transfers support