package NameCard.objects;

import NameCard.data.VertexArray;
import NameCard.programs.TextureShaderProgram;
import NameCard.util.TextureHelper;
import android.content.Context;
import name.mawentao.contactscard.R;

import static NameCard.Constants.BYTES_PER_FLOAT;
import static android.opengl.GLES20.*;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;

/**
 * Created by ZTR on 1/8/16.
 */
public class CardAround {
    private final static int POSITION_COMPONENT_COUNT = 3;
    private final static int TEXTURE_COORDINATE_COUNT = 2;
    private final static int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATE_COUNT) * BYTES_PER_FLOAT;

    private Context context;

    private TextureShaderProgram textureProgram;
    private int textureId;

    private static final float[] VERTEX_DATA= {
            // X,Y,Z,S,T

            //back
            -0.8f, 0.02f, -0.45f, 0f, 0.044444f,
            -0.8f, -0.02f, -0.45f, 0f, 0f,
            0.8f, 0.02f, -0.45f, 0.888889f, 0.044444f,
            0.8f, -0.02f, -0.45f, 0.888889f, 0f,

            //front
            -0.8f, 0.02f, 0.45f, 0f, 0.044444f,
            -0.8f, -0.02f, 0.45f, 0f, 0f,
            0.8f, 0.02f, 0.45f, 0.888889f, 0.044444f,
            0.8f, -0.02f, 0.45f, 0.888889f, 0f,

            //left
            -0.8f, 0.02f, 0.45f, 0f, 0f,
            -0.8f, -0.02f, 0.45f, 0.022222f, 0f,
            -0.8f, 0.02f, -0.45f, 0f, 1f,
            -0.8f, -0.02f, -0.45f, 0.022222f, 1f,

            //right
            0.8f, 0.02f, 0.45f, 0f, 0f,
            0.8f, -0.02f, 0.45f, 0.022222f, 0f,
            0.8f, 0.02f, -0.45f, 0f, 1f,
            0.8f, -0.02f, -0.45f, 0.022222f, 1f,

    };

    private VertexArray vertexArray;

    public CardAround(Context context){
        vertexArray = new VertexArray(VERTEX_DATA);
        this.context = context;
    }

    private void bindData(){

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

    public void initTextureProgram(){
        textureProgram = new TextureShaderProgram(context);
        textureId = TextureHelper.loadTexture(context, R.drawable.wood_plank, GL_LINEAR_MIPMAP_NEAREST, GL_LINEAR, GL_MIRRORED_REPEAT, GL_MIRRORED_REPEAT);
    }

    public void draw(float[] matrix){
        textureProgram.useProgram();
        textureProgram.setUniforms(matrix, textureId);
        bindData();
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glDrawArrays(GL_TRIANGLE_STRIP, 4, 4);
        glDrawArrays(GL_TRIANGLE_STRIP, 8, 4);
        glDrawArrays(GL_TRIANGLE_STRIP, 12, 4);



    }
}
