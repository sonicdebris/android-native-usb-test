package test.usb.sonicdebris.usbtest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private UsbHelper usbHelper;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setText("yo!");

        usbHelper = new UsbHelper(this);
    }

    public void testUsbDevice(int descriptor, String name) {
        Toast.makeText(this,name+" - fd: "+descriptor,Toast.LENGTH_LONG).show();
        String res = testUsbDeviceNative(descriptor,name);
        if (res != null)
            tv.append("\n"+res);
    }

    public native String testUsbDeviceNative(int fd, String name);
}
