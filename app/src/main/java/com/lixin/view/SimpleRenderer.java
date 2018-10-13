package com.lixin.view;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by li on 2018/5/20.
 */

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class SimpleRenderer implements GLSurfaceView.Renderer {
    private String TAG = "SimpleRenderer";
    private Random rand = new Random();
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG,"onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG,"onSurfaceChanged");

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG,"onDrawFrame");
        gl.glClearColor(rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),1);
        //清屏
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
}
