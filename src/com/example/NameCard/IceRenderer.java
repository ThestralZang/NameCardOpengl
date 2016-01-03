package com.example.NameCard;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.example.NameCard.entity.Card;
import com.example.NameCard.objects.*;
import com.example.NameCard.programs.ColorShaderProgram;
import com.example.NameCard.programs.TextureShaderProgram;
import com.example.NameCard.util.CardTextHelper;
import com.example.NameCard.util.MatrixHelper;
import com.example.NameCard.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

/**
 * Created by ZTR on 12/30/15.
 */
public class IceRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "IceRenderer";

    private final Context context;

    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private CardBox cardBox;
    private ImagePlane imagePlane;
    private PortraitImgSection portraitImgSection;
    private MainInfoSection mainInfoSection;
    private AttachedInfoSection attachedInfoSection;
    private CardSurface cardSurface;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    private boolean cardTouched = false;

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
        imagePlane = new ImagePlane();
        portraitImgSection = new PortraitImgSection();
        mainInfoSection = new MainInfoSection();
        attachedInfoSection = new AttachedInfoSection();

        Card card = new Card("张挺然","15221586750","同济大学","学生","243825915@qq.com", "aaa");
        cardSurface = new CardSurface(context,card);

        cardSurface.initTexturePrograms();

        colorProgram = new ColorShaderProgram(context);
        setLookAtM(viewMatrix, 0, 0f, 3.5f, -1f, 0f, 0f, -1.5f, 0f, 1f, 0f);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
        System.out.println("CHANGE@@@@@@@@@@@@@@@@@@@@@@@");
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        positionCamera(eyePositionX, eyePositionY, eyePositionZ, targetPositionX, targetPositionY, targetPositionZ);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);


//        positionCardInSceneAfterRotation(rotationAngleX, rotationAngleY);

        positionCard(moveDistanceX, moveDistanceY, moveDistanceZ, rotationAngleX, rotationAngleY, rotationAngleZ);


        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix);
        cardBox.bindData(colorProgram);
        cardBox.draw();


        cardSurface.draw(modelViewProjectionMatrix);

        System.out.println("DRAWING!!!");
    }

    private void positionCardInSceneAfterRotation(float angleX, float angleY) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -1.5f);
        rotateM(modelMatrix, 0, angleX, 1f, 0f, 0f);
        rotateM(modelMatrix, 0, angleY, 0f, 1f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionCard(float distanceX, float distanceY, float distanceZ, float angleX, float angleY, float angleZ){
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, distanceX, distanceY, distanceZ);
        rotateM(modelMatrix, 0, angleX, 1f, 0f, 0f);
        rotateM(modelMatrix, 0, angleY, 0f, 1f, 0f);
        rotateM(modelMatrix, 0, angleZ, 0f, 0f, 1f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionCamera(float eyeX, float eyeY, float eyeZ, float targetX, float targetY, float targetZ){
        setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, targetX, targetY, targetZ, 0f, 1f, 0f);
    }

    private int i = 0;
    public boolean raiseCard(){
        i++;
        System.out.println("Renderer Raising!!!!   "+i);
        System.out.println(rotationAngleX + "  ,  " + moveDistanceY + "  ,  " + moveDistanceZ);
        if( rotationAngleX >= 90f || moveDistanceY >= 4f || moveDistanceZ <= -2f) {
            rotationAngleX = 90f;
            moveDistanceY = 4f;
            moveDistanceZ = -2f;
            return false;
        }
        else {
            rotationAngleX += 1.8f;
            moveDistanceZ -= 0.01f;
            moveDistanceY += 0.08f;
            return true;
        }
    }

    public boolean dropCard(){
//        if( rotationAngleX == 0f && moveDistanceY == 4f && moveDistanceZ == -2f)
//            return false;
//        else {
//            rotationAngleX = rotationAngleX + 1.8f;
//            moveDistanceZ = moveDistanceZ - 0.01f;
//            moveDistanceY = moveDistanceY + 0.08f;
//            return true;
//        }
        return false;
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
