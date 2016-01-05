/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.example.NameCard.objects;

import android.content.Context;
import com.example.NameCard.R;
import com.example.NameCard.data.VertexArray;
import com.example.NameCard.programs.SkyboxShaderProgram;
import com.example.NameCard.util.TextureHelper;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.*;


public class Skybox {
	
	private Context context;
	private int skyboxTexture;
	private SkyboxShaderProgram skyboxProgram;
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;
    
    public Skybox(Context context) {        
    	this.context = context;
        // Create a unit cube.
        vertexArray = new VertexArray(new float[] {
            -10,  10,  10,     // (0) Top-left near
             10,  10,  10,     // (1) Top-right near
            -10, -10,  10,     // (2) Bottom-left near
             10, -10,  10,     // (3) Bottom-right near
            -10,  10, -10,     // (4) Top-left far
             10,  10, -10,     // (5) Top-right far
            -10, -10, -10,     // (6) Bottom-left far
             10, -10, -10      // (7) Bottom-right far                        
        });
        
        // 6 indices per cube side
        indexArray =  ByteBuffer.allocateDirect(6 * 6)
            .put(new byte[] {
                // Front
                1, 3, 0,
                0, 3, 2,
                
                // Back
                4, 6, 5,
                5, 6, 7,
                               
                // Left
                0, 2, 4,
                4, 2, 6,
                
                // Right
                5, 7, 1,
                1, 7, 3,
                
                // Top
                5, 1, 4,
                4, 1, 0,
                
                // Bottom
                6, 2, 7,
                7, 2, 3
            });
        indexArray.position(0);        
    }
    public void bindData() {        
        vertexArray.setVertexAttribPointer(0,
            skyboxProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, 0);               
    }
    
    public void draw(float[] matrix) {
    	glDepthFunc(GL_LEQUAL);
    	skyboxProgram.useProgram();
		skyboxProgram.setUniforms(matrix, skyboxTexture);
		bindData();
		
        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_BYTE, indexArray);
        glDepthFunc(GL_LESS);
    }
	public void initTextureProgram() {
		// TODO Auto-generated method stub
		skyboxTexture = TextureHelper.loadCubeMap(context,
                new int[]{R.drawable.left, R.drawable.right,
                        R.drawable.bottom, R.drawable.top,
                        R.drawable.front, R.drawable.back});
	}
	public void initSkyboxProgram() {
		// TODO Auto-generated method stub
		skyboxProgram = new SkyboxShaderProgram(context);
		
	}
}