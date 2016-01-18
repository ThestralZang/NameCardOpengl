precision mediump float;

varying int v_TextureNum;
varying vec2 v_TextureCoordinate;

uniform sampler2D u_PortraitTexture;
uniform sampler2D u_MainInfoTexture;
uniform sampler2D u_AttachedInfoTexture;

void main() {

    if(v_TextureNum == 0){
        gl_FragColor = texture2D(u_PortraitTexture, v_TextureCoordinate);
    }else if(v_TextureNum == 1){
        gl_FragColor = texture2D(u_MainInfoTexture, v_TextureCoordinate);
    }else if(v_TextureNum == 2){
        gl_FragColor = texture2D(u_AttachedInfoTexture, v_TextureCoordinate);
    }

}
