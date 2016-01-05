package com.example.NameCard.objects;

import android.content.Context;
import com.example.NameCard.data.VertexArray;
import com.example.NameCard.data.VertexIndexArray;
import com.example.NameCard.programs.ColorShaderProgram;
import com.example.NameCard.util.Geometry.AABBbox;
import com.example.NameCard.util.Geometry.Point;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static com.example.NameCard.Constants.BYTES_PER_FLOAT;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.Matrix.*;
/**
 * Created by ZTR on 1/1/16.
 */
public class CardBox {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private ColorShaderProgram colorProgram;
    private Context context;

    private AABBbox box;

    private static final float[] VERTEX_DATA = {
            //X,Y,Z, R,G,B

            -0.8f, 0.02f, 0.45f, 0.3f, 0.2f, 0.1f,
            -0.8f, 0.02f, -0.45f, 0.3f, 0.2f, 0.1f,
            0.8f, 0.02f, -0.45f, 0.3f, 0.2f, 0.1f,
            0.8f, 0.02f, 0.45f, 0.3f, 0.2f, 0.1f,

            0f, -0.02f, 0f, 1f, 1f, 1f,
            -0.8f, -0.02f, 0.45f, 0.7f, 0.6f, 0.4f,
            -0.8f, -0.02f, -0.45f, 0.7f, 0.6f, 0.4f,
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

    public CardBox(Context context) {
        this.context = context;
        vertexArray = new VertexArray(VERTEX_DATA);
        aroundIndexArray = new VertexIndexArray(AROUND_INDEX_DATA);
        bottomIndexArray = new VertexIndexArray(BOTTOM_INDEX_DATA);

        box = new AABBbox(new Point(-0.8f,-0.02f,-0.45f),new Point(0.8f,0.02f,0.45f));
    }

    private void bindData(){
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


    public void initColorProgram(){
        colorProgram = new ColorShaderProgram(context);
    }

    public void draw(float[] matrix){
        colorProgram.useProgram();
        colorProgram.setUniforms(matrix);
        bindData();
        glDrawElements(GL_TRIANGLE_STRIP, AROUND_INDEX_DATA.length, GL_UNSIGNED_SHORT, aroundIndexArray.shortBuffer);
        glDrawElements(GL_TRIANGLE_FAN, BOTTOM_INDEX_DATA.length, GL_UNSIGNED_SHORT, bottomIndexArray.shortBuffer);
    }


    public AABBbox createAABBbox(float[] matrix){
        float[] temp = {//the temple matrix represent the card triangle
                0.8f,0.02f,0.45f,1f,
                0.8f,0.02f,-0.45f,1f,
                -0.8f,0.02f,0.45f,1f,
                -0.8f,0.02f,-0.45f,1f
        };
        multiplyMM(temp, 0, matrix, 0, temp, 0);//apply the transform matrix to the card
        float maxX = getMax(temp[0],temp[4],temp[8],temp[12]);
        float maxY = getMax(temp[1],temp[5],temp[9],temp[13]);
        float maxZ = getMax(temp[2],temp[6],temp[10],temp[14]);
        float minX = getMin(temp[0],temp[4],temp[8],temp[12]);
        float minY = getMin(temp[1],temp[5],temp[9],temp[13]);
        float minZ = getMin(temp[2],temp[6],temp[10],temp[14]);//calculate the maximum and minimum x,y,z position

        return new AABBbox(new Point(maxX,maxY,maxZ),new Point(minX,minY,minZ));//create an AABB box

    }
    public float getMax(float f1,float f2,float f3, float f4){
        float tempR1 = f1 > f2 ? f1 : f2;
        float tempR2 = f3 > f4 ? f3 : f4;
        return tempR1 > tempR2 ? tempR1 : tempR2;
    }
    public float getMin(float f1,float f2,float f3, float f4){
        float tempR1 = f1 < f2 ? f1 : f2;
        float tempR2 = f3 < f4 ? f3 : f4;
        return tempR1 < tempR2 ? tempR1 : tempR2;
    }

}
