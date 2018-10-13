package com.lixin.gameInterfaceImp;

import android.graphics.Bitmap;

import com.lixin.gameInterface.Graphics;
import com.lixin.gameInterface.Pixmap;

/**
 * Created by li on 2018/9/1.
 */

public class AndroidPixmap implements Pixmap {
    private Bitmap bitmap;
    private Graphics.PixmapFormat format;

    public AndroidPixmap(Bitmap bitmap, Graphics.PixmapFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public Graphics.PixmapFormat getFormat() {
        return format;
    }

    @Override
    public void dispose() {
        bitmap.recycle();
    }
}
