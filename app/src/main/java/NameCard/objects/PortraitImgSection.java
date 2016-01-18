package NameCard.objects;

import NameCard.data.VertexArray;
import NameCard.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by ZTR on 1/2/16.
 */
public class PortraitImgSection extends CardSection {


    private final static float[] VERTEX_DATA = {
            //X,Y,Z,S,T
            -0.8f, 0.02f, -0.45f, 0f, 0f,
            -0.8f, 0.02f, 0.25f, 0f, 1f,
            -0.1f, 0.02f, -0.45f, 1f, 0f,
            -0.1f, 0.02f, 0.25f, 1f, 1f,
    };

    private final VertexArray vertexArray;

    public PortraitImgSection() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    @Override
    public void draw() {
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public void bindData(TextureShaderProgram textureProgram) {
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

}
