package name.mawentao.contactscard;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import NameCard.Constants;
import NameCard.OpenglSurfaceView;
import Utils.NameCard;


public class OpenglActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private OpenglSurfaceView openglSurfaceView;
    private boolean rendererSet = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //获取EditActivity传递的NameCard
        Intent intent = getIntent();
        NameCard nameCard = intent.getParcelableExtra(EditActivity.INTENT_TAG);

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

            openglSurfaceView = new OpenglSurfaceView(this, nameCard);
            openglSurfaceView.requestFocus();
            openglSurfaceView.setFocusableInTouchMode(true);
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
            return;
        }

        setContentView(openglSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) { openglSurfaceView.onPause();}
        Constants.CARD_ACTION_THREAD_FLAG = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) { openglSurfaceView.onResume(); }
        Constants.CARD_ACTION_THREAD_FLAG = true;
    }

}
