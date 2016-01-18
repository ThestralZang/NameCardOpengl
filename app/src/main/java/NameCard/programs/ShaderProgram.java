package NameCard.programs;

import android.content.Context;

import NameCard.util.ShaderHelper;
import NameCard.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by ZTR on 12/30/15.
 */
public class ShaderProgram {

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_TIME = "u_Time";
    protected static final String U_LIGHT_POSITION = "u_LightPosition";
    protected static final String U_SPECULAR_MATERIAL = "u_SpecularMaterial";
    protected static final String U_SHINESS = "u_Shiness";



    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";
    protected static final String A_NORMAL = "a_Normal";


    // Shader program
    protected final int program;
    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }

}
