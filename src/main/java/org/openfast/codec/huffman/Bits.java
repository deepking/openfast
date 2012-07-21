package org.openfast.codec.huffman;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.base.Joiner;

/**
 * bit list
 * @author chengyi Jul 17, 2012
 *
 */
public class Bits implements Iterable<Integer> {
    private ArrayList<Integer> bits = new ArrayList<Integer>();
    
    public static final Bits NULL = new Bits();
    
    public Bits() {
    }
    
    public void add(int bit) {
        if (bit == 0 || bit == 1) {
            bits.add(bit);
        }
        else {
            throw new IllegalArgumentException("bit must be 0 or 1 : " + bit);
        }
    }
    
    public void add(Bits bitArray) {
        bits.addAll(bitArray.bits);
    }
    
    public int get(int index) {
        return bits.get(index);
    }
    
    public int size() {
        return bits.size();
    }

    @Override
    public Iterator<Integer> iterator() {
        return bits.iterator();
    }
    
    public byte[] toByteArray() {
        byte[] bytes = new byte[bits.size()/8+1];
        for (int i = 0; i < bits.size(); i++) {
            int index = bytes.length - i / 8 - 1;
            bytes[index] = (byte)(bytes[index] << 1 | bits.get(i));
        }
        int remaining = 8 - bits.size() % 8;
        bytes[bytes.length-1] <<= remaining;
            
        return bytes;
    }
    
    @Override
    public String toString() {
        return Joiner.on(" ").join(bits);
    }
    
    public static void main(String[] args) {
        Bits bitArray = new Bits();
        bitArray.add(0);
        bitArray.add(0);
        bitArray.add(1);
        bitArray.add(0);
        bitArray.add(1);
        System.out.println(new String(bitArray.toByteArray()));
    }
}
