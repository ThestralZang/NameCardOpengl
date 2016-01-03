package com.example.NameCard;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class OpenglActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private GLSurfaceView glSurfaceView;
    private OpenglSurfaceView openglSurfaceView;
    private boolean rendererSet = false;
    private final IceRenderer iceRenderer = new IceRenderer(this);

    private boolean isRaisedUp = false;
    private boolean isRaisingUp = false;
    private boolean isDroppingDown = false;
    private boolean isDroppedDown = true;

    private int clickNum = 0;
    private long lastClickTime = 0;

    private final float DOUBLE_CLICK_TIME_INTERVAL = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        glSurfaceView = new GLSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));
        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);
            // Assign our renderer.
            glSurfaceView.setRenderer(iceRenderer);
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
            return;
        }

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return touchScreen(event);
            }
        });

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) { glSurfaceView.onPause();}
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) { glSurfaceView.onResume(); }
    }


    private boolean touchScreen(MotionEvent event){

        if (event == null) return false;

        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE:
                glSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if(isRaisedUp){
                            iceRenderer.handleTouchDrag(event.getX(), event.getY());
                        }
                    }
                });
                break;

            case MotionEvent.ACTION_DOWN:
                clickCount();
                if(clickNum == 2) System.out.println("DOUBLE_CLICK");
                glSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if(isDroppedDown && clickNum == 2){
                            while(iceRenderer.raiseCard()){
                                isDroppedDown = false;
                                isRaisingUp = true;
                            }
                            isRaisingUp = false;
                            isRaisedUp = true;
                            System.out.println("RAISED UP");
                        }else if(isRaisedUp && clickNum == 2){
                            while(iceRenderer.dropCard()){
                                isRaisedUp = false;
                                isDroppingDown = true;
                            }
                            isDroppingDown = false;
                            isDroppedDown = true;
                        }
                        iceRenderer.handleTouchPress(event.getX(), event.getY());
                    }
                });
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
