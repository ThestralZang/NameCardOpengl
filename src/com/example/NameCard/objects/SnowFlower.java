package com.example.NameCard.objects;

import android.content.Context;
import android.graphics.Color;
import com.example.NameCard.R;
import com.example.NameCard.programs.ParticleShaderProgram;
import com.example.NameCard.util.Geometry.Point;
import com.example.NameCard.util.Geometry.Vector;
import com.example.NameCard.util.TextureHelper;

import java.util.Random;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

public class SnowFlower {

	private final float angleVariance;
    private final float speedVariance;

    private Context context;

    private final Random random = new Random();

    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];
    
    
    
    private ParticleShaderProgram particleProgram;
    private ParticleSystem particleSystem;
    
    private int textureId;
    

    public SnowFlower(Context context, float angleVarianceInDegrees, float speedVariance){
        this.angleVariance = angleVarianceInDegrees;
        this.speedVariance = speedVariance;
        directionVector[0] = 0.5f;
        directionVector[1] = -1.0f;
        directionVector[2] = 0.5f;
        
        this.context = context;
        particleSystem = new ParticleSystem(10000);
        
        textureId = TextureHelper.loadTexture(context, R.drawable.snowflower_texture);
		
        System.out.println("create snow flower.");
    }

    public void addParticles(float currentTime,
                             int count) {
        for (int i = 0; i < count; i++) {
            setRotateEulerM(rotationMatrix, 0,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance);

            multiplyMV(
                    resultVector, 0,
                    rotationMatrix, 0,
                    directionVector, 0);

            float speedAdjustment = 0.25f + random.nextFloat() * speedVariance;

            Vector thisDirection = new Vector(
                    resultVector[0] * speedAdjustment,
                    resultVector[1] * speedAdjustment,
                    resultVector[2] * speedAdjustment);

            /*
            particleSystem.addParticle(position, color, direction, currentTime);
             */
            float xPos = random.nextFloat() * 20 - 10;
            float yPos = 10;
            float zPos = random.nextFloat() * 20 - 10;
//            if(random.nextFloat() > 0.5){
//            	xPos = xPos - 1f;
//            	yPos = 1f;
//            }else{
//            	xPos = -1f;
//            	yPos = yPos - 1f;
//            }
            
            Point position = new Point(xPos,yPos,zPos);
            particleSystem.addParticle(position, Color.rgb(128,128,128), thisDirection, currentTime);
        }
        //System.out.println("add a particle");
    }
    
    public void draw(float[] matrix, float currentTime){
        glEnable(GL_BLEND);
        glBlendFunc( GL_SRC_ALPHA , GL_ONE_MINUS_SRC_ALPHA ); 
        glDepthMask(false);
        particleProgram.useProgram();
        particleProgram.setUniforms(matrix, currentTime, textureId);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();
        //System.out.println("Drawing snow flower. textureId is "+ textureId);
        glDisable(GL_BLEND);
        glDepthMask(true);
    	
    }


	public void initParticleProgram() {
		// TODO Auto-generated method stub
		particleProgram = new ParticleShaderProgram(context);
	}
	public void initTextureProgram(){
		

	}
}
