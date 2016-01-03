package com.example.NameCard;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by ZTR on 1/3/16.
 */
public class OpenglSurfaceView extends GLSurfaceView {

    private IceRenderer iceRenderer;

    public OpenglSurfaceView(Context context) {
        super(context);
        iceRenderer = new IceRenderer(context);
        setRenderer(iceRenderer);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);


    }
}
