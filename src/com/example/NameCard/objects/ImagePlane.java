package com.example.NameCard.objects;

import com.example.NameCard.data.VertexArray;
import com.example.NameCard.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static com.example.NameCard.Constants.BYTES_PER_FLOAT;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by ZTR on 12/31/15.
 */
public class ImagePlane {

    private final static int POSITION_COMPONENT_COUNT = 3;
    private final static int TEXTURE_COORDINATE_COUNT = 2;
    private final static int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATE_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA= {
            // X,Y,Z,S,T
            -0.8f, 0.02f, 0.5f, 0f, 1f,
            -0.8f, 0.02f, -0.5f, 0f, 0f,
            0.8f, 0.02f, 0.5f, 0.8f, 1f,
            0.8f, 0.02f, -0.5f, 0.8f, 0f
    };

    private VertexArray vertexArray;

    public ImagePlane(){
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureProgram){

        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATE_COUNT,
                STRIDE
        );

    }

    public static void draw(){
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

}
