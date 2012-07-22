package org.openfast.util;

import java.io.IOException;
import java.io.OutputStream;

import org.openfast.codec.huffman.Bits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A stream where bits can be written to.
 */
public final class BitOutputStream {
    private static final Logger log = LoggerFactory.getLogger(BitOutputStream.class);

    /** Underlying byte stream to write to */
    private OutputStream        output;

    /** Always in the range 0x00 to 0xFF */
    private int                 currentByte;

    /** Always between 0 and 7, inclusive */
    private int                 numBitsInCurrentByte;

    public BitOutputStream(OutputStream out) {
        if (out == null) {
            throw new NullPointerException("Argument is null");
        }
        output = out;
        currentByte = 0;
        numBitsInCurrentByte = 0;
    }

    /**
     * Writes a bit to the stream. The specified bit must be 0 or 1.
     * 
     * @param b
     * @throws IOException
     */
    public void write(int b) throws IOException {
        if (!(b == 0 || b == 1)) {
            throw new IllegalArgumentException("Argument must be 0 or 1");
        }
        currentByte = currentByte << 1 | b;
        numBitsInCurrentByte++;
        if (numBitsInCurrentByte == 8) {
            output.write(currentByte);
            numBitsInCurrentByte = 0;
        }
    }

    public void write(Bits bist) throws IOException {
        for (Integer bit : bist) {
            currentByte = currentByte << 1 | bit;
            numBitsInCurrentByte++;
            if (numBitsInCurrentByte == 8) {
                output.write(currentByte);
                numBitsInCurrentByte = 0;
            }
        }
    }

    /**
     * Closes this stream and the underlying OutputStream. If called when this
     * bit stream
     * is not at a byte boundary, then the minimum number of zeros (between 0
     * and 7) are
     * written as padding to reach a byte boundary.
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        int skipBits = 8 - numBitsInCurrentByte;
//        while (numBitsInCurrentByte != 0)
//            write(0);

        log.debug("skip bits {}", skipBits);
        output.write(currentByte);
        output.write(skipBits);
        output.close();
    }

}
