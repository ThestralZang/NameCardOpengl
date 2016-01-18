package NameCard.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static NameCard.Constants.BYTES_PER_SHORT;

/**
 * Created by ZTR on 12/30/15.
 */
public class VertexIndexArray {

    public final ShortBuffer shortBuffer;

    public VertexIndexArray(short[] vertexIndexData) {
        shortBuffer = ByteBuffer.allocateDirect(vertexIndexData.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(vertexIndexData);

        shortBuffer.position(0);
    }



}
