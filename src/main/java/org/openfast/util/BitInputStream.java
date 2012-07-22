package org.openfast.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream extends InputStream {
    
    /** Underlying byte stream to read from */
    private InputStream input;

    /** Either in the range 0x00 to 0xFF, or -1 if the end of stream is reached */
    private int nextBits;

    /** Always between 0 and 7, inclusive */
    private int numBitsRemaining;

    private boolean isEndOfStream;

    public BitInputStream(InputStream in) {
        if (in == null) {
            throw new NullPointerException("Argument is null");
        }
        input = in;
        numBitsRemaining = 0;
        isEndOfStream = false;
    }

    /**
     * Reads a bit from the stream. The end of stream always occurs on a byte
     * boundary.
     * 
     * @return 0 or 1 if a bit is available, or -1 if the end of stream is
     *         reached.
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        if (isEndOfStream) {
            return -1;
        }
        if (numBitsRemaining == 0) {
            nextBits = input.read();
            if (nextBits == -1) {
                isEndOfStream = true;
                return -1;
            }

            if (input.available() == 1) {
                int skipBits = input.read();
                numBitsRemaining = 8 - skipBits;
            }
            else {
                numBitsRemaining = 8;
            }
        }
        numBitsRemaining--;
        return (nextBits >>> numBitsRemaining) & 1;
    }

    /**
     * Reads a bit from the stream.
     * 
     * @return 0 or 1 if a bit is available
     * @throws IOException if the end of stream is reached.
     */
    public int readNoEof() throws IOException {
        int result = read();
        if (result != -1) {
            return result;
        } else {
            throw new EOFException("End of stream reached");
        }
    }

    /**
     * Closes this stream and the underlying InputStream.
     * 
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        input.close();
    }
    
    @Override
    public int available() throws IOException {
        return isEndOfStream ? 0 : 1;
    }

    public boolean isEOF()
    {
        return isEndOfStream;
    }
}
