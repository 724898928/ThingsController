package com.lixin.thingscontroller;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lixin.util.LogUtil;
import com.lixin.view.MySurfaceView;
import com.lixin.view.MyOpenGl;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks{

    private static String className = "com.li Main2Activity";
    MyOpenGl myOpenGl;
    MySurfaceView mySurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        String[] perms = {"Manifest.permission.CAMERA","Manifest.permission.RECORD_AUDIO","Manifest.permission.INTERNET"};
        if (checkPermission(this,perms)){
            requestPermission(this,"Need permission for camera & microphone", 0, perms);
        }

        LogUtil.d(className, "onCreate begin");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // myOpenGl = new MyOpenGl(this);
        //myOpenGl.setRenderer(new SimpleRenderer());

        mySurfaceView = new MySurfaceView(this);
        setContentView(mySurfaceView);
        LogUtil.d(className, "onCreate end");
        // Example of a call to a native method
        // TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
    }
    @Override
    protected void onPause() {
        LogUtil.d(className, "onPause begin");
        super.onPause();
        // myOpenGl.onPause();
        LogUtil.d(className, "onPause begin");
    }

    @Override
    protected void onResume() {
        LogUtil.d(className,"onResume begin");
        super.onResume();
        //  myOpenGl.onResume();
        LogUtil.d(className, "onResume begin");
    }
    /**
     *  检查权限
     *
     * @param context
     * @param perms
     * @return true 已经获取权限. false 为获取权限, 主动请求权限
     */
    public static boolean checkPermission(Activity context, String[] perms){
        return EasyPermissions.hasPermissions(context,perms);
    }

    /**
     *
     *  请求权限
     *
     * @param context Context对象
     * @param tip Context对象
     * @param requestCode 这次请求权限的唯一标示，code。
     * @param perms 这次请求权限的唯一标示，code。
     */
    public static void requestPermission(Activity context, String tip, int requestCode, String[] perms){
        EasyPermissions.requestPermissions(context,tip,requestCode,perms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode,  @NonNull List<String> perms) {
        Toast.makeText(this,"用户授权成功!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "用户授权失败!", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this ,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
