attribute vec4 a_Position;
attribute vec2 a_TextureCoordinate;
attribute int a_TextureNum;

uniform mat4 u_Matrix;

varying vec2 v_TextureCoordinate;
varying int v_TextureNum;


void main() {
    gl_Position = u_Matrix * a_Position;
    v_TextureCoordinate = a_TextureCoordinate;
    v_TextureNum = a_TextureNum;

}
