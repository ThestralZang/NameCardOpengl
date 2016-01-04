package com.example.NameCard;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import static com.example.NameCard.Constants.*;

/**
 * Created by ZTR on 1/3/16.
 */
public class OpenglSurfaceView extends GLSurfaceView {

    private IceRenderer iceRenderer;
    private int clickNum = 0;
    private long lastClickTime = 0;

    private final float DOUBLE_CLICK_TIME_INTERVAL = 800;

    public OpenglSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        iceRenderer = new IceRenderer(context);
        setRenderer(iceRenderer);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if(CARD_RAISED_UP){
                    iceRenderer.handleTouchDrag(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_DOWN:
                clickCount();
                if(clickNum == 2) System.out.println("DOUBLE_CLICK");
                if(CARD_DROPPED_DOWN && clickNum == 2){
                    CARD_DROPPED_DOWN = false;
                    CARD_RAISING_UP = true;
                }else if(CARD_RAISED_UP && clickNum == 2){
                    CARD_RAISED_UP = false;
                    CARD_DROPPING_DOWN = true;
                }
                iceRenderer.handleTouchPress(event.getX(), event.getY());
                break;

            default:break;

        }
        return true;
    }

    private void clickCount() {

        long clickInterval = System.currentTimeMillis() - lastClickTime;

        if(clickNum == 0){
            clickNum++;
            lastClickTime = System.currentTimeMillis();
        } else if(clickNum == 1 && clickInterval > DOUBLE_CLICK_TIME_INTERVAL){
            clickNum = 0;
        } else if(clickNum == 1 && clickInterval <= DOUBLE_CLICK_TIME_INTERVAL) {
            clickNum++;
        } else if(clickNum == 2) {
            clickNum = 0;
        }
    }
}
