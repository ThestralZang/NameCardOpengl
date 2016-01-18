package NameCard.objects;

import android.content.Context;

import NameCard.data.VertexArray;
import NameCard.data.VertexIndexArray;
import NameCard.programs.ColorShaderProgram;
import NameCard.util.Geometry.AABBbox;
import NameCard.util.Geometry.Point;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.Matrix.multiplyMM;
import static NameCard.Constants.BYTES_PER_FLOAT;

/**
 * Created by ZTR on 1/1/16.
 */
public class CardBox {

    private CardBack cardBack;
    private CardAround cardAround;
    private Context context;

    private AABBbox box;


    public CardBox(Context context) {
        this.context = context;
        box = new AABBbox(new Point(-0.8f,-0.02f,-0.45f),new Point(0.8f,0.02f,0.45f));
        cardBack = new CardBack(context);
        cardAround = new CardAround(context);
    }

    public void initTextureProgram() {
        cardAround.initTextureProgram();
        cardBack.initTextureProgram();
    }

    public void draw(float[] matrix){
        cardBack.draw(matrix);
        cardAround.draw(matrix);
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
