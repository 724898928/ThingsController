package com.lixin.thingscontroller;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.lixin.util.LogUtil;
import com.lixin.view.MySurfaceView;
import com.lixin.view.MyOpenGl;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    private static String className = "com.li Main2Activity";
    MyOpenGl myOpenGl;
    MySurfaceView mySurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(className,"onCreate begin");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
               WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // myOpenGl = new MyOpenGl(this);
        //myOpenGl.setRenderer(new SimpleRenderer());

        mySurfaceView = new MySurfaceView(this);
        setContentView(mySurfaceView);
        LogUtil.d(className,"onCreate end");
        // Example of a call to a native method
        // TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    protected void onPause() {
        LogUtil.d(className,"onPause begin");
        super.onPause();
        // myOpenGl.onPause();
        LogUtil.d(className,"onPause begin");
    }

    @Override
    protected void onResume() {
        LogUtil.d(className,"onResume begin");
        super.onResume();
        //  myOpenGl.onResume();
        LogUtil.d(className,"onResume begin");
    }
}
