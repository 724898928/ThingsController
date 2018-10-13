package com.lixin.view;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by li on 2018/5/21.
 */

public class GLGraphics
{
   MyOpenGl glView;
   GL10 gl10;

    public GLGraphics(MyOpenGl glView) {
        this.glView = glView;
    }

    public GL10 getGl10()
    {
        return gl10;
    }

    public void setGl10(GL10 gl10)
    {
        this.gl10 = gl10;
    }

    public int getWidth()
    {
        return glView.getWidth();
    }
    public int getHeight()
    {
        return glView.getHeight();
    }
}
