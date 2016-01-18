package NameCard.objects;

import NameCard.programs.TextureShaderProgram;

import static NameCard.Constants.BYTES_PER_FLOAT;

/**
 * Created by ZTR on 1/2/16.
 */
public abstract class CardSection {

    protected final static int POSITION_COMPONENT_COUNT = 3;
    protected final static int TEXTURE_COORDINATE_COUNT = 2;
    protected final static int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATE_COUNT) * BYTES_PER_FLOAT;

    public abstract void draw();

    public abstract void bindData(TextureShaderProgram textureProgram);

}
