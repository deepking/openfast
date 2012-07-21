package org.openfast.codec.huffman;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHCode {
    
    @Test
    public void testlog() {
        Logger log = LoggerFactory.getLogger(TestHCode.class);
        
        log.info("中文哦哦哦哦哦");
        log.warn("hhhhhaaaaa");
        
    }

    @Test
    public void testCompress() {
        try {
            HCode hCode = new HCode("src/test/resources/huffman/aard");
            
            ByteArrayOutputStream codeBuf = new ByteArrayOutputStream();
            BitOutputStream code = new BitOutputStream(codeBuf);
            byte[] bytes = new byte[] {'a', 'a', 'r', 'd', 'v'};
            for (byte b : bytes) {
                Bits bits = hCode.compress(b);
                System.out.println(bits);
                code.write(bits);
            }
            code.close();
            
            System.out.println(codeBuf.toByteArray().length);
            
            // decompress
            //
            BitInputStream codeIn = new BitInputStream(new ByteArrayInputStream(codeBuf.toByteArray()));
            HCode decode = new HCode("src/test/resources/huffman/aardv");
            codeBuf = new ByteArrayOutputStream();
            decode.decompress(codeIn, codeBuf);
            
            System.out.println(new String(codeBuf.toByteArray()));
            
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void test() {
        try {
            HCode hCode = new HCode("test/resources/resource/string");
            
            ByteArrayOutputStream codeBuf = new ByteArrayOutputStream();
            BitOutputStream code = new BitOutputStream(codeBuf);
            byte[] bytes = new byte[] {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A'};
//            byte[] bytes = new byte[] {'9', '8', '7', '6', '5'};
            for (byte b : bytes) {
                Bits bits = hCode.compress(b);
                System.out.println(bits);
                code.write(bits);
            }
            code.close();
            
            System.out.println(codeBuf.toByteArray().length);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testNumber() {
        try {
            HCode hCode = new HCode("resource/number");
            
            ByteArrayOutputStream codeBuf = new ByteArrayOutputStream();
            BitOutputStream code = new BitOutputStream(codeBuf);
            byte[] bytes = new byte[] {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
//            byte[] bytes = new byte[] {'9', '8', '7', '6', '5'};
            for (byte b : bytes) {
                Bits bits = hCode.compress(b);
                System.out.println(bits);
                code.write(bits);
            }
            code.close();
            
            System.out.println(codeBuf.toByteArray().length);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
