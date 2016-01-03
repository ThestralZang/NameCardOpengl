package com.example.NameCard.objects;

import com.example.NameCard.data.VertexArray;
import com.example.NameCard.data.VertexIndexArray;
import com.example.NameCard.programs.ColorShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static com.example.NameCard.Constants.BYTES_PER_FLOAT;
import static android.opengl.GLES20.glDrawElements;

/**
 * Created by ZTR on 1/1/16.
 */
public class CardBox {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            //X,Y,Z, R,G,B

            -0.8f, 0.02f, 0.45f, 0.3f, 0.2f, 0.1f,
            -0.8f, 0.02f, -0.45f, 0.3f, 0.2f, 0.1f,
            0.8f, 0.02f, -0.45f, 0.3f, 0.2f, 0.1f,
            0.8f, 0.02f, 0.45f, 0.3f, 0.2f, 0.1f,

            0f, -0.02f, 0f, 1f, 1f, 1f,
            -0.8f, -0.02f, 0.45f, 0.7f, 0.6f, 0.4f,
            -0.8f, -0.02f, -0.45f, 0.4f, 0.6f, 0.7f,
            0.8f, -0.02f, -0.45f, 0.7f, 0.6f, 0.4f,
            0.8f, -0.02f, 0.45f, 0.7f, 0.6f, 0.4f

    };

    private static final short[] AROUND_INDEX_DATA = {
            0, 5, 1, 6, 2, 7, 3, 8, 0, 5
    };

    private static final short[] BOTTOM_INDEX_DATA = {
            4, 5, 6, 7, 8, 5
    };

    private final VertexArray vertexArray;
    private final VertexIndexArray aroundIndexArray;
    private final VertexIndexArray bottomIndexArray;

    public CardBox() {
        vertexArray = new VertexArray(VERTEX_DATA);
        aroundIndexArray = new VertexIndexArray(AROUND_INDEX_DATA);
        bottomIndexArray = new VertexIndexArray(BOTTOM_INDEX_DATA);
    }

    public void bindData(ColorShaderProgram colorProgram){
        vertexArray.setVertexAttribPointer(
                0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE
        );
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                colorProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE
        );
    }

    public void draw(){
        glDrawElements(GL_TRIANGLE_STRIP, AROUND_INDEX_DATA.length, GL_UNSIGNED_SHORT, aroundIndexArray.shortBuffer);
        glDrawElements(GL_TRIANGLE_FAN, BOTTOM_INDEX_DATA.length, GL_UNSIGNED_SHORT, bottomIndexArray.shortBuffer);
    }



}
