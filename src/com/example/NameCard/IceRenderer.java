package com.example.NameCard;

import android.content.Context;
import android.opengl.GLSurfaceView;
import com.example.NameCard.entity.Card;
import com.example.NameCard.objects.*;
import com.example.NameCard.programs.ColorShaderProgram;
import com.example.NameCard.util.MatrixHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;
import static com.example.NameCard.Constants.*;

/**
 * Created by ZTR on 12/30/15.
 */
public class IceRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "IceRenderer";

    private final Context context;

    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] cardModelMatrix = new float[16];
    private final float[] groundModelMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] cardModelViewProjectionMatrix = new float[16];
    private final float[] groundModelViewProjectionMatrix = new float[16];

    private CardBox cardBox;
    private CardSurface cardSurface;
    private Ground ground;
    private ColorShaderProgram colorProgram;

    private float rotationAngleX = 0f;
    private float rotationAngleY = 0f;
    private float rotationAngleZ = 0f;
    private float moveDistanceX = 0f;
    private float moveDistanceY = 0f;
    private float moveDistanceZ = -1.5f;
    private float eyePositionX = 0f;
    private float eyePositionY = 3.5f;
    private float eyePositionZ = -1f;
    private float targetPositionX = 0f;
    private float targetPositionY = 0f;
    private float targetPositionZ = -1.5f;
    private float previousX;
    private float previousY;

    public IceRenderer(Context context) {
        this.context = context;

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        cardBox = new CardBox();
        ground = new Ground(context);

        Card card = new Card("Thestral","12345678900","HAHA大学","学生","12345678900@hhh.com", "aaa");
        cardSurface = new CardSurface(context, card);

        cardSurface.initTexturePrograms();
        ground.initTextureProgram();

        colorProgram = new ColorShaderProgram(context);
        setLookAtM(viewMatrix, 0, 0f, 3.5f, -1f, 0f, 0f, -1.5f, 0f, 1f, 0f);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);

        new Thread(){
            public void run(){
                while(CARD_ACTION_THREAD_FLAG){
                    try {
                        if(CARD_RAISING_UP){
                            boolean isActionLasting = raiseCard();
                            Thread.sleep(100);
                            while(isActionLasting){
                                isActionLasting = raiseCard();
                                Thread.sleep(100);
                            }
                            CARD_RAISING_UP = false;
                            CARD_RAISED_UP = true;
                        }else if(CARD_DROPPING_DOWN){
                            boolean isActionLasting = dropCard();
                            Thread.sleep(100);
                            while(isActionLasting){
                                isActionLasting = dropCard();
                                Thread.sleep(100);
                            }
                            CARD_DROPPING_DOWN = false;
                            CARD_DROPPED_DOWN = true;
                        }
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        positionCamera(eyePositionX, eyePositionY, eyePositionZ, targetPositionX, targetPositionY, targetPositionZ);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        positionCard(moveDistanceX, moveDistanceY, moveDistanceZ, rotationAngleX, rotationAngleY, rotationAngleZ);
        setIdentityM(groundModelMatrix, 0);
        multiplyMM(groundModelViewProjectionMatrix, 0, viewProjectionMatrix, 0, groundModelMatrix, 0);


        colorProgram.useProgram();
        colorProgram.setUniforms(cardModelViewProjectionMatrix);
        cardBox.bindData(colorProgram);
        cardBox.draw();


        cardSurface.draw(cardModelViewProjectionMatrix);
        ground.draw(groundModelViewProjectionMatrix);

        System.out.println("DRAWING!!!");
    }

    private void positionCard(float distanceX, float distanceY, float distanceZ, float angleX, float angleY, float angleZ){
        setIdentityM(cardModelMatrix, 0);
        translateM(cardModelMatrix, 0, distanceX, distanceY, distanceZ);
        rotateM(cardModelMatrix, 0, angleX, 1f, 0f, 0f);
        rotateM(cardModelMatrix, 0, angleY, 0f, 1f, 0f);
        rotateM(cardModelMatrix, 0, angleZ, 0f, 0f, 1f);
        multiplyMM(cardModelViewProjectionMatrix, 0, viewProjectionMatrix, 0, cardModelMatrix, 0);
    }

    private void positionCamera(float eyeX, float eyeY, float eyeZ, float targetX, float targetY, float targetZ){
        setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, targetX, targetY, targetZ, 0f, 1f, 0f);
    }

    public boolean raiseCard(){
        if( rotationAngleX >= 90f || moveDistanceY >= 4f || moveDistanceZ <= -2f) {
            rotationAngleX = 90f;
            moveDistanceY = 4f;
            moveDistanceZ = -2f;
            eyePositionY = 4f;
            eyePositionZ = 2f;
            targetPositionX = moveDistanceX;
            targetPositionY = moveDistanceY;
            targetPositionZ = moveDistanceZ;
            return false;
        }
        else {
            rotationAngleX += 1.8f;
            moveDistanceY += 0.08f;
            moveDistanceZ -= 0.01f;
            eyePositionY += 0.01f;
            eyePositionZ += 0.06f;
            targetPositionX = moveDistanceX;
            targetPositionY = moveDistanceY;
            targetPositionZ = moveDistanceZ;
            return true;
        }
    }

    public boolean dropCard(){
        if( rotationAngleX <= 0f || moveDistanceY <= 0f || moveDistanceZ >= -1.5f) {
            rotationAngleX = 0f;
            moveDistanceY = 0f;
            moveDistanceZ = -1.5f;
            eyePositionY = 3.5f;
            eyePositionZ = -1f;
            targetPositionX = moveDistanceX;
            targetPositionY = moveDistanceY;
            targetPositionZ = moveDistanceZ;
            return false;
        }
        else {
            rotationAngleX -= 1.8f;
            moveDistanceZ += 0.01f;
            moveDistanceY -= 0.08f;
            eyePositionY -= 0.01f;
            eyePositionZ -= 0.06f;
            targetPositionX = moveDistanceX;
            targetPositionY = moveDistanceY;
            targetPositionZ = moveDistanceZ;
            return true;
        }
    }

    public void handleTouchDrag(float x, float y){
        float dx = x - previousX;
        float dy = y - previousY;
        rotationAngleX = rotationAngleX + dy * 0.5f;
        rotationAngleY = rotationAngleY + dx * 0.5f;
        previousX = x;
        previousY = y;
    }

    public void handleTouchPress(float x, float y){
        previousX = x;
        previousY = y;
    }

}
