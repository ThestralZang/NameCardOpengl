package com.example.NameCard;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.NameCard.entity.Card;
import com.example.NameCard.objects.*;
import com.example.NameCard.programs.ColorShaderProgram;
import com.example.NameCard.util.Geometry;
import com.example.NameCard.util.MatrixHelper;
import com.example.NameCard.util.Geometry.*;

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
    private int width;
    private int height;

    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] cardModelMatrix = new float[16];
    private final float[] groundModelMatrix = new float[16];
    private final float[] snowFlowerModelMatrix = new float[16];
    private final float[] skyboxModelMatrix = new float[16];
    private final float[] heightmapModelMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];
    private final float[] cardModelViewProjectionMatrix = new float[16];
    private final float[] groundModelViewProjectionMatrix = new float[16];
    private final float[] snowFlowerModelViewProjectionMatrix = new float[16];
    private final float[] skyboxModelViewProjectionMatrix = new float[16];
    private final float[] heightmapModelViewProjectionMatrix = new float[16];

    private CardBox cardBox;
    private CardSurface cardSurface;
    private Ground ground;
    private ColorShaderProgram colorProgram;

    private boolean cardPressed = false;

    private Skybox skybox;

    private SnowFlower snowFlower;
    private long globalStartTime;

    private Heightmap heightmap;

    private float rotationAngleX = DFT_CARD_ANGLE_X;
    private float rotationAngleY = DFT_CARD_ANGLE_Y;
    private float rotationAngleZ = DFT_CARD_ANGLE_Z;
    private float moveDistanceX = DFT_CARD_POSITION_X;
    private float moveDistanceY = DFT_CARD_POSITION_Y;
    private float moveDistanceZ = DFT_CARD_POSITION_Z;
    private float eyePositionX = DFT_EYE_POSITION_X;
    private float eyePositionY = DFT_EYE_POSITION_Y;
    private float eyePositionZ = DFT_EYE_POSITION_Z;
    private float targetPositionX = DFT_TARGET_POSITION_X;
    private float targetPositionY = DFT_TARGET_POSITION_Y;
    private float targetPositionZ = DFT_TARGET_POSITION_Z;
    private float cameraRotationAngle = 0;
    private float previousX;
    private float previousY;
    private float angleDeltaX;
    private float angleDeltaY;
    private float angleDeltaZ;


    public IceRenderer(Context context) {
        this.context = context;

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        cardBox = new CardBox(context);
        ground = new Ground(context);

        Card card = new Card("Thestral","12345678900","HAHA大学","学生","12345678900@hhh.com", "aaa");
        cardSurface = new CardSurface(context, card);

        cardSurface.initTexturePrograms();
        ground.initTextureProgram();
        cardBox.initColorProgram();

        final float angleVarianceInDegrees = 5f;
        final float speedVariance = 0.05f;
        globalStartTime = System.nanoTime();
        snowFlower = new SnowFlower(context, angleVarianceInDegrees,speedVariance);
        snowFlower.initTextureProgram();
        snowFlower.initParticleProgram();

        skybox = new Skybox(context);
        skybox.initTextureProgram();
        skybox.initSkyboxProgram();

        heightmap = new Heightmap(context);
        heightmap.initHeightmapProgram();

        setLookAtM(viewMatrix, 0, 0f, 3.5f, -1f, 0f, 0f, -1.5f, 0f, 1f, 0f);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;
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
                            angleDeltaX = getRotationAngleX() - DFT_CARD_ANGLE_X;
                            angleDeltaY = getRotationAngleY() - DFT_CARD_ANGLE_Y;
                            angleDeltaZ = getRotationAngleZ() - DFT_CARD_ANGLE_Z;
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
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);//反转矩阵
        positionCard(moveDistanceX, moveDistanceY, moveDistanceZ, rotationAngleX, rotationAngleY, rotationAngleZ);
        setIdentityM(groundModelMatrix, 0);
        multiplyMM(groundModelViewProjectionMatrix, 0, viewProjectionMatrix, 0, groundModelMatrix, 0);
        setIdentityM(snowFlowerModelMatrix,0);
        multiplyMM(snowFlowerModelViewProjectionMatrix, 0, viewProjectionMatrix, 0, snowFlowerModelMatrix, 0);
        setIdentityM(skyboxModelMatrix, 0);
        multiplyMM(skyboxModelViewProjectionMatrix, 0, viewProjectionMatrix, 0, skyboxModelMatrix, 0);
        setIdentityM(heightmapModelMatrix, 0);
        multiplyMM(heightmapModelViewProjectionMatrix, 0, viewProjectionMatrix, 0, heightmapModelMatrix, 0);


        skybox.draw(skyboxModelViewProjectionMatrix);

        heightmap.draw(heightmapModelViewProjectionMatrix);

        cardBox.draw(cardModelViewProjectionMatrix);
        cardSurface.draw(cardModelViewProjectionMatrix);
        //ground.draw(groundModelViewProjectionMatrix);

        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
        snowFlower.addParticles(currentTime,10);
        snowFlower.draw(snowFlowerModelViewProjectionMatrix,currentTime);



        //System.out.println("DRAWING!!!\n\n");
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

    private boolean raiseCard(){
        if( rotationAngleX >= 90f || moveDistanceY >= 4f || moveDistanceZ <= -2f) {
            rotationAngleX = 90f;
            moveDistanceY = 4f;
            moveDistanceZ = -2f;
            //eyePositionZ = 2f;
            targetPositionX = moveDistanceX;
            targetPositionY = moveDistanceY;
            targetPositionZ = moveDistanceZ;
            return false;
        }
        else {
            rotationAngleX += 1.8f;
            moveDistanceY += 0.08f;
            moveDistanceZ -= 0.01f;
            eyePositionZ += 0.06f;
            targetPositionX = moveDistanceX;
            targetPositionY = moveDistanceY;
            targetPositionZ = moveDistanceZ;
            return true;
        }
    }

    private boolean dropCard(){
        if( Math.abs(getRotationAngleX() - DFT_CARD_ANGLE_X) < angleDeltaX/50
                || Math.abs(getRotationAngleY() - DFT_CARD_ANGLE_Y) < angleDeltaY/50
                || Math.abs(getRotationAngleZ() - DFT_CARD_ANGLE_Z) < angleDeltaZ/50
                || moveDistanceY <= 0f || moveDistanceZ >= -1.5f) {
            rotationAngleX = DFT_CARD_ANGLE_X;
            rotationAngleY = DFT_CARD_ANGLE_Y;
            rotationAngleZ = DFT_CARD_ANGLE_Z;
            moveDistanceY = 0f;
            moveDistanceZ = -1.5f;
            //eyePositionZ = -1f;
            targetPositionX = moveDistanceX;
            targetPositionY = moveDistanceY;
            targetPositionZ = moveDistanceZ;
            return false;
        }
        else {
            rotationAngleX -= angleDeltaX/50;
            rotationAngleY -= angleDeltaY/50;
            rotationAngleZ -= angleDeltaZ/50;
            moveDistanceZ += 0.01f;
            moveDistanceY -= 0.08f;
            eyePositionZ -= 0.06f;
            targetPositionX = moveDistanceX;
            targetPositionY = moveDistanceY;
            targetPositionZ = moveDistanceZ;
            return true;
        }
    }

    public void handleTouchDrag(float x, float y){
        if(cardPressed){
            System.out.println("DRAG card!!");
            float dx = x - previousX;
            float dy = y - previousY;
            rotationAngleX = rotationAngleX + dy * 0.5f;
            rotationAngleY = rotationAngleY + dx * 0.5f;
        }else{
            System.out.println("rotate camera!!");
            float dx = x - previousX;
            cameraRotationAngle = cameraRotationAngle + dx * 0.005f;
            processCameraRotation(cameraRotationAngle);
        }
        previousX = x;
        previousY = y;
    }

    public void processCameraRotation(float angle){
        float r = 4.0f;
        eyePositionX = (float) (r * Math.sin(angle));
        eyePositionZ = (float) (r * Math.cos(angle))-2f;
        System.out.println("r:"+r);
        System.out.println("eyePositionX:"+eyePositionX+"eyePositionZ:"+eyePositionZ);


    }

    public void handleTouchPress(float x, float y){
        previousX = x;
        previousY = y;
        float normalizedX = (x/(float)width)*2-1f;
        float normalizedY = (y/(float)height)*2-1f;


        Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        AABBbox box = cardBox.createAABBbox(cardModelMatrix);
        cardPressed = Geometry.intersects(box, ray);
        if(cardPressed){
            System.out.println("You have pressed the card");
        }
    }

    private float getRotationAngleX(){
        while(Math.abs(rotationAngleX) >= 360f){
            if(rotationAngleX > 0){
                rotationAngleX -= 360f;
            }else{
                rotationAngleX += 360f;
            }
        }
        return rotationAngleX;
    }

    private float getRotationAngleY(){
        while(Math.abs(rotationAngleY) >= 360f){
            if(rotationAngleY > 0){
                rotationAngleY -= 360f;
            }else{
                rotationAngleY += 360f;
            }
        }
        return rotationAngleY;
    }

    private float getRotationAngleZ(){
        while (Math.abs(rotationAngleZ) >= 360f){
            if(rotationAngleZ > 0){
                rotationAngleZ -= 360f;
            }else{
                rotationAngleZ += 360f;
            }
        }
        return rotationAngleZ;
    }

    private Ray convertNormalized2DPointToRay(
            float normalizedX, float normalizedY) {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc =  {normalizedX, normalizedY,  1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        //setIdentityM(invertedViewProjectionMatrix, 0);
        multiplyMV(
                nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(
                farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point nearPointRay =
                new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);

        Point farPointRay =
                new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        Geometry.showPoint(nearPointRay);
        Geometry.showPoint(farPointRay);
        return new Ray(nearPointRay,
                Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }




}
