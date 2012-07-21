package org.openfast.codec.huffman;

/**
 * huffman node
 * @author chengyi Jul 17, 2012
 *
 */
class HNode {
    
    /** 1:right link, 0:left link */
    int parity;
    
    /** parent node */
    int up;           // the next node up the tree
    /** left child node */
    int dnl;          // next node down-left
    /** right child node */
    int dnr;          // next node down-right
    
    // increasing weight
    //
    int weight;       // node weight
    int prev;         // next <= weight node
    int next;         // next >= weight node

}
