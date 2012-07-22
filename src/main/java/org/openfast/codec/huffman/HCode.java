package org.openfast.codec.huffman;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.openfast.util.BitInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author chengyi Jul 17, 2012
 * 
 */
public class HCode {
    private final Logger    log         = LoggerFactory.getLogger(HCode.class);

    /** the current tree height */
    private int             esc;

    /** the size of the alphabet */
    private final int       size;

    private final int[]     pwt         = new int[256];
    private final int[]     pnd         = new int[256];

    // condition
    //
    public final static int NOT_IN_COND = 777;
    /** map symbol to a increasing size */
    private final int[]     ptv         = new int[256];
    /** map size to symbol */
    private final int[]     pts         = new int[256];

    // XXX
    private int             empty1;
    private int             empty2;

    private final HNode[]   table;

    public HCode(String condPath) throws IOException
    {
        // init
        //
        Arrays.fill(ptv, NOT_IN_COND);
        Arrays.fill(pts, NOT_IN_COND);
        for (int i = 255; i > 0; i--) {
            pwt[i] = i;
            pnd[i] = i;
        }

        // pre condition
        //
        int size = 0;
        FileInputStream cond = null;
        try {
            cond = new FileInputStream(condPath);
            while (cond.available() > 0) {
                int symbol = cond.read();
                if (symbol == -1) {
                    break;
                }
                if (ptv[symbol] == NOT_IN_COND) {
                    ptv[symbol] = size;
                    pts[size] = symbol;
                    size++;
                }
            }
        } catch (FileNotFoundException e) {
            log.error("pre-condition({}) building error", condPath, e);
            throw e;
        } finally {
            if (cond != null) {
                cond.close();
            }
        }

        if (size > 256 || size < 3) {
            throw new IllegalArgumentException("size " + size
                    + " has to be in range 3 to 256 try again");
        }
        else {
            this.size = size;
        }

        esc = 2 * size - 1;

        //  create the initial escape node
        //  at the tree root
        //
        table = new HNode[2 * size];
        for (int i = 0; i < 2 * size; i++)
        {
            table[i] = new HNode();
        }
        table[esc].weight = 1;
        table[esc].next = esc;

        log.info("esc=" + esc + " size=" + size);

        updatePreCondition(condPath);
    }

    void updatePreCondition(String condPath) throws IOException {
        FileInputStream cond = null;
        try {
            cond = new FileInputStream(condPath);
            while (cond.available() > 0) {
                int symbol = cond.read();
                if (symbol == -1) {
                    break;
                }

                symbol = ptv[symbol];
                Bits bits = encode(symbol);
                log.trace(bits.toString());
            }
        } catch (FileNotFoundException e) {
            log.error("{} not found, current path {}", condPath,
                    System.getProperty("user.dir"));
        } finally {
            if (cond != null) {
                cond.close();
            }
        }
    }

    /**
     * increment node weights and re balance the tree.
     * 
     * @param node
     */
    void increment(int node) {
        int next;
        int up = table[node].up;

        //  obviate swapping a parent with its child:
        //    increment the leaf and proceed
        //    directly to its parent.

        if (table[node].next == up) {
            table[node].weight += 2;
        } else {
            up = node;
        }

        //  slide right and go up until reaching the root

        //while( symbol = up, table[symbol].up != 0) {
        for (node = up; table[node].up != 0; node = up) {
            next = table[node].next;

            //  promote the node to group leader
            //  position by sliding right over
            //  any equal weight nodes

            while (table[node].weight == table[next].weight) {
                next = slide(node, next);
            }

            //  increase the weight of the node

            table[node].weight += 2;

            //  internal nodes go up from this
            //  initial group leader position

            if (node > size) {
                up = table[node].up;
            }

            //  slide incremented node over smaller weights to its right

            while (table[node].weight > table[next].weight) {
                next = slide(node, next);
            }

            //  symbol nodes slide over first,
            //  then go up from their
            //  final positions

            if (node < size) {
                up = table[node].up;
            }
        }

        //  increase the root's weight

        table[node].weight += 2;

    }

    public Bits compress(int symbol) {
        int toBeEncode;
        //log.info("compress {}", symbol);
        toBeEncode = ptv[symbol];
        if (toBeEncode == NOT_IN_COND)
        {
            log.error("{} not in cond", symbol);
            return Bits.NULL;
        }

        return encode(toBeEncode);
    }

    public Bits encode(int node) {
        int emit = 1;
        int bit;
        int up, idx;

        //  transform illegal symbols to zero
        //
        if (node >= size)
        {
            System.out.println("illegal symbol " + node);
            node = 0;
        }

        //  for a new symbol, direct the receiver to the escape node
        //
        if (table[node].weight > 0) {
            idx = node;
        } else {
            idx = esc;
        }

        //  accumulate the code bits by
        //  working up the tree from
        //  the node to the root
        //
        while ((up = table[idx].up) != 0)
        {
            emit <<= 1;
            emit |= table[idx].parity;
            idx = up;
        }

        Bits bitArray = new Bits();
        //  send the code, root selector bit first
        //
        for (bit = emit & 1; (emit >>= 1) != 0; bit = emit & 1)
        {
            bitArray.add(bit);
        }

        //  send identification and incorporate
        //  new symbols into the tree
        //
        if (table[node].weight == 0)
        {
            bitArray.add(sendid(node));
            split(node);
        }

        //  adjust and re-balance the tree
        //
        increment(node);

        return bitArray;
    }

    public void decompress(BitInputStream in, OutputStream out)
            throws IOException {
        while (!in.isEOF()) {
            try {
                int symbol = decode(in);
                out.write(symbol);
            } catch (IOException e) {
                break;
            }
        }
    }

    /**
     * 
     * @param in
     * @return symbol
     * @throws IOException
     */
    public int decode(BitInputStream in) throws IOException {
        int node = 2 * size - 1;

        //  work down the tree from the root
        //  until reaching either a leaf
        //     or the escape node

        while (node > esc)
        {
            int bit = in.readNoEof();
            log.info("{} ", bit);
            if (bit == 1) {
                node = table[node].dnr;
            } else {
                node = table[node].dnl;
            }
        }

        //  sent to the escape node???

        if (node == esc)
        {
            node = readid(in);
            split(node);
        }

        //  increment weights and rebalance
        //  the coding tree

        increment(node);
        int symbol = pts[node];
        if (symbol == NOT_IN_COND) {
            log.error("symbol {} is not in cond", symbol);
        }
        return symbol;
    }

    /**
     * send the bits for an escaped symbol
     * this routine modifed by David Scott since
     * it was wrong it often used more bits that
     * required for new symbol
     * 
     * @param symbol
     */
    Bits sendid(int symbol) {

        int empty = 0, max;

        int dsx, dsy, dsz = 0;

        //  count the number of empty nodes
        //  before the symbol in the table

//            while( node-- )
//              if( !huff->table[node].weight )
//                empty++;

        //  send LSB of this count first, using
        //  as many bits as are required for
        //  the maximum possible count
        /*
         * first error you sometimes need one less bit here
         * the original code off by one bit sometimes so fixed
         */
        max = esc - size;
        dsx = pnd[max];
        empty = pwt[symbol];
        pwt[dsx] = empty;
        pnd[empty] = dsx;
        empty1 = empty;
        empty += max + 1 - empty2;
        if (empty > max) {
            empty -= max + 1;
        }
        empty2 = empty1;
        dsx = max + max;
        for (dsy = 1; (dsx >>= 1) != 0; dsy <<= 1) {
            ;
        }
        dsx = dsy - max - 1;
        dsz = dsy >> 1;
        if (empty < dsx) {
            max >>= 1;
        }
        else if (empty >= dsz) {
            empty += dsx;
        }

        Bits bitArray = new Bits();
        if (max != 0)
        {
            do {
                bitArray.add(empty & 1);
                empty >>= 1;
            } while ((max >>= 1) != 0);
        }

        return bitArray;
    }

    int readid(BitInputStream in) throws IOException {
        int empty = 0, bit = 1, node, max;
        int dsx, dsy, dsz = 0;

        //  receive the code, LSB first, reading
        //  only the number of bits necessary to
        //  transmit the maximum possible value
        /*
         * again this is in error so fixing since it
         * often reads more bits that necessary
         */

        max = esc - size;
        node = max;
        if ((max >>= 1) != 0) {
            do {
                empty |= in.readNoEof() == 1 ? bit : 0;
                bit <<= 1;
            } while ((max >>= 1) != 0);
        }
        max = esc - size;
        dsx = max + max;
        for (dsy = 1; (dsx >>= 1) != 0; dsy <<= 1) {
            ;
        }
        dsx = dsy - max - 1;
        dsz = dsy >> 1;
        if (empty >= dsx && max != 0) {
            if (in.read() == 1) {
                empty += dsz - dsx;
            }
        }
        dsz = max;
        empty += empty2;
        if (empty > max) {
            empty -= max + 1;
        }
        empty2 = empty;
        max = pnd[node];
        node = pnd[empty];
        pwt[max] = empty;
        pnd[empty] = max;

        //  the count is of zero weight
        //  symbols in the table before
        //  the zero weight new symbol

//            for( node = 0; node < huff->size; node++ )
//              if( !huff->table[node].weight )
//                if( !empty-- )

        return node;

        //  oops!  our count was too big!
        /* adding a check here may do it on last but should no where else */
        //printf ( " **should never get here** \n");
        //return 0;

    }

    void split(int node) {
        int next, pair;

        //  the final symbol takes the escape node's left child
        //  tree position, and the orphan escape node links to
        //  the first symbol in the rank list (this last symbol)

        if (esc == size) {
            next = table[esc].next;
            table[esc + 1].dnl = node;
            table[esc].next = node;
            table[esc].up = 0;
            table[next].prev = node;

            table[node].up = esc + 1;
            table[node].prev = esc;
            table[node].next = next;
            table[node].parity = 0;
            table[node].weight = 0;
            return;
        }

        //  other symbols initialize a new escape node.
        //  the old escape node is promoted to parent
        //  the new escape node and the new symbol.

        //  HuffEsc is always the current escape node;
        //  promote the old escape node.

        pair = esc--;
        table[pair].dnl = esc;
        table[pair].prev = node;
        table[pair].dnr = node;

        //  create a new escape node.

        table[esc].next = node;
        table[esc].weight = 1;
        table[esc].parity = 0;
        table[esc].up = pair;

        //  initialize the new symbol to the
        //  right of the new escape node
        //  and beneath the promoted
        //  pair node at pair

        table[node].prev = esc;
        table[node].next = pair;
        table[node].parity = 1;
        table[node].weight = 0;
        table[node].up = pair;

    }

    /**
     * slide node to right over all leaves of equal weight
     * return node that follows
     * 
     * @param node
     * @param idx
     * @return node that follows
     */
    int slide(int node, int idx) {
        int prev = table[node].prev;
        int next = table[idx].next;
        int slide, up, parity;

        // find rightmost possible leaf to exchange with
        //
        if (idx < size) {
            while (table[next].weight == table[idx].weight) {
                idx = next;
                next = table[idx].next;
            }
        }

        // swap the tree positions
        // of node and idx.

        up = table[idx].up;

        if ((parity = table[idx].parity) != 0) {
            table[up].dnr = node;
        } else {
            table[up].dnl = node;
        }

        table[idx].parity = table[node].parity;
        table[idx].up = table[node].up;
        table[node].parity = parity;
        table[node].up = up;

        up = table[idx].up;

        if (table[idx].parity != 0) {
            table[up].dnr = idx;
        } else {
            table[up].dnl = idx;
        }

        //  exchange the ranking positions
        //  of node and idx.

        slide = table[idx].prev;

        //  simple swap??

        if (slide == node) {
            table[node].prev = idx;
            table[idx].next = node;
        }
        else {
            table[node].prev = slide;
            table[slide].next = node;
            slide = table[node].next;
            table[slide].prev = idx;
            table[idx].next = slide;
        }

        // we never swap the escape node nor
        // the root node -- they will always
        // have the smallest and largest
        // weights in the tree

        table[prev].next = idx;
        table[next].prev = node;

        table[idx].prev = prev;
        return table[node].next = next;

    }
}
