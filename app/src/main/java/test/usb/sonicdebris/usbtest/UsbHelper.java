package test.usb.sonicdebris.usbtest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

public class UsbHelper extends BroadcastReceiver {

    private UsbManager um;
    private static final String ACTION_USB_PERMISSION = "sonicdebris.usb.test.USB_PERMISSION";
    private PendingIntent permissionIntent;

    private MainActivity listener;

    UsbHelper(MainActivity act) {
        listener  = act;
        register(act);
    }

    public void register(Context c) {

        // register for device dis/connection events:
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        iFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        c.registerReceiver(this,iFilter);

        // register for request permission response:
        um = (UsbManager) c.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        c.registerReceiver(this, filter);

        // prepare intent to ask for permission:
        permissionIntent = PendingIntent.getBroadcast(c, 0, new Intent(ACTION_USB_PERMISSION), 0);
    }

    private UsbDevice deviceFromIntent(Intent i) {
        return i.getParcelableExtra(UsbManager.EXTRA_DEVICE);

    }

    @Override
    public void onReceive(Context c, Intent intent) {
        String act = intent.getAction();
        if (act.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
            UsbDevice dev = deviceFromIntent(intent);
            if (null!=dev)
                requestPermission(dev);
        }
        else if (act.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
            Toast.makeText(c,"USB device detached",Toast.LENGTH_SHORT).show();
        }
        else if (act.equals(ACTION_USB_PERMISSION)) {
            UsbDevice dev = deviceFromIntent(intent);

            if (dev != null && intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                if(dev != null){
                    Toast.makeText(c,"Permission granted",Toast.LENGTH_SHORT).show();
                    UsbDeviceConnection connection = um.openDevice(dev);

                    listener.testUsbDevice(connection.getFileDescriptor(),dev.getDeviceName());
                }
            }

        }
    }

    public void requestPermission(UsbDevice dev) {
        um.requestPermission(dev,permissionIntent);
    }

//        fun register(context: Context) {
//
//            val intentFilter = IntentFilter().apply {
//
//                addAction(Intent.ACTION_HEADSET_PLUG)
//                // If headphone is already plugged, this immediately triggers onReceive
//                // with current headphone state
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    // only listen to usb devices on 5.0+ devices (no usb audio support before that)
//                    addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
//                    addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
//                    addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
//                    usbAttached = isUsbAudioDeviceConnected(context)
//                    onHeadsetStateChange(isSomethingPlugged())
//                }
//
//                if (ENABLE_BLUETOOTH_MONITORING) {
//                    addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
//                }
//
//                // Other actions we might be interested in:
//                // AudioManager.ACTION_HDMI_AUDIO_PLUG for hdmi
//            }
//            context.registerReceiver(this, intentFilter)
//        }
//
//        override fun onReceive(context: Context, intent: Intent) {
//
//            when (intent.action) {
//
//                Intent.ACTION_HEADSET_PLUG -> {
//                    val state = intent.getIntExtra("state", -1)
//                    Timber.d("Monitoring:: action headset plug: $state")
//                    usbAttached = isUsbAudioDeviceConnected(context)
//                    headphoneAttached = state == 1
//                    onHeadsetStateChange(isSomethingPlugged())
//                }
//
//                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
//                    Timber.d("Monitoring:: action audio becoming noisy")
//                    // This action is received when the last wired route is unplugged
//                    // or when bluetooth is disconnected (even if headphones or usb are plugged..)
//                    onHeadsetStateChange(isSomethingPlugged())
//                }
//
//                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
//                    Timber.d("Monitoring:: action audio usb device attached")
//                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
//                            usbAttached = isAudioDevice(device)
//
//                    if (usbAttached) {
//                        val tracker = Injector.perApp(context).mixeditorTracker()
//                        tracker.usbAudioDeviceConnected(getDeviceInfo(device))
//                    }
//
//                    onHeadsetStateChange(isSomethingPlugged())
//                }
//
//                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
//                    Timber.d("Monitoring:: usb device detached")
//                    usbAttached = false
//                    onHeadsetStateChange(isSomethingPlugged())
//                }
//
//                BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> {
//                    Timber.d("Monitoring:: bluetooth state updated")
//                    when (intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1)) {
//                        BluetoothProfile.STATE_CONNECTED -> {
//                            bluetoothAttached = true
//                            onHeadsetStateChange(isSomethingPlugged())
//                        }
//                        BluetoothProfile.STATE_DISCONNECTED -> {
//                            bluetoothAttached = false
//                            onHeadsetStateChange(isSomethingPlugged())
//                        }
//                    }
//                }
//            }
//        }
//
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        fun getDeviceInfo(d: UsbDevice) = "[${d.vendorId}:${d.productId}] ${d.manufacturerName} - ${d.productName}"
//
//        fun unregister(context: Context) {
//            try {
//                context.unregisterReceiver(this)
//            } catch (e: Exception) {
//                Timber.w(e, "Headset unregistration exception")
//            }
//        }
//
//        fun isUsbAudioDeviceConnected(c: Context) : Boolean {
//
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                return false
//            }
//
//            val manager = c.getSystemService(Context.USB_SERVICE) as UsbManager
//            manager.deviceList.values.forEach {
//                if (isAudioDevice(it)) {
//                    Timber.d(" Monitoring:: a usb device is already connected")
//                    return true
//                }
//            }
//            return false
//        }
//
//
//        @Suppress("unused")
//        val SUBCLASS_AUDIOCONTROL = 0x01
//        val SUBCLASS_AUDIOSTREAMING = 0x02
//        @Suppress("unused")
//        val SUBCLASS_MIDISTREAMING = 0x03
//        // as per USB standard, see "Universal Serial Bus Device Class Definition for Audio Devices"
//
//        fun isAudioDevice(device: UsbDevice): Boolean {
//            for (i in 0 until device.interfaceCount) {
//                val usbClass = device.getInterface(i).interfaceClass
//                val usbSubclass = device.getInterface(i).interfaceSubclass
//                if (usbClass == UsbConstants.USB_CLASS_AUDIO && usbSubclass == SUBCLASS_AUDIOSTREAMING) {
//                    return true
//                }
//            }
//            return false
//        }
//    }


}
