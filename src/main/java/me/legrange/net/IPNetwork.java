package me.legrange.net;

import java.io.Serializable;

/**
 * A representation of an IP network (v4 or v6) consisting of a network and mask
 * on which useful operations can be done.
 *
 * @author gideon
 * @param <N> The type of network represented
 */
public abstract class IPNetwork<N extends IPNetwork> implements Serializable {
    

    private final byte address[];
    private final byte longMask[];
    private final int mask;

    /**
     * Convert the given address in byte array from to it's string
     * representation.
     *
     * @param bytes The bytes to convert. We assume it is a valid bits/8 byte
     * array.
     * @return The text representation.
     */
    protected abstract String bytesToString(byte bytes[]);

    /**
     * Return the number of bits required by the implemented address family
     *
     * @return The number of bits
     */
    protected abstract int bits();

    /** Create a new network of the implemented address family. 
     * 
     * @param address The address to use
     * @param mask The short mask to apply
     * @return The network
     */
    protected abstract N newNetwork(byte[] address, int mask);

    
    /**
     * Return the address part of this network
     *
     * @return The address part
     * @throws me.legrange.net.InvalidAddressException
     */
    public String getAddress() throws InvalidAddressException {
        return bytesToString(address);
    }

    /**
     * Return the short mask of this network
     *
     * @return The short mask
     */
    public int getMask() {
        return mask;
    }

    /**
     * Return the network with the given mask which surrounds (contains,
     * encloses) this network
     *
     * @param mask The mask for the enclosing network
     * @return The enclosing network
     * @throws me.legrange.net.NetworkException
     */
    public N getEnclosingNetwork(int mask) throws NetworkException {
        if ((mask < 0) || (mask > bits()-1)) {
            throw new InvalidAddressException(String.format("'%d' is not a valid netmask.", mask));
        }
        if (mask > this.mask) {
            throw new InvalidAddressException(String.format("Network '%s' cannot be contained in a /%d network.", toString(), mask));
        }
        return newNetwork(mask(address, shortMaskToLong(mask)), mask);
    }

    /**
     * Return true if the given network is a subnet of this network
     *
     * @param net The 'inside' IPv6 Network
     * @return the result of the test
     * @throws NetworkException
     */
    public boolean contains(N net) throws NetworkException {
        IPNetwork n = net;
        boolean equals = false;
        if (n.mask >= mask) {
            byte[] masked = mask(n.address, longMask);
             equals = equals(address, masked);
        }
        return equals;
//        return (n.mask >= mask) && equals(address, mask(n.address, longMask));
    }

    @Override
    public final boolean equals(Object o) {
        if ((o != null) && (o instanceof IPNetwork)) {
            IPNetwork net = (IPNetwork) o;
            return (this.mask == net.mask) && equals(net.address, this.address);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s/%d", bytesToString(address), mask);
    }


    protected IPNetwork(byte[] address, int mask) {
        this.mask = mask;
        this.longMask = shortMaskToLong(mask);
        this.address = mask(address, longMask);
    }
    
    /** Create the byte array mask for the given short notation net mask. 
     * 
     * @param val The short mask
     * @return The long mask
     */
    private byte[] shortMaskToLong(int val) {
        byte bytes[] = new byte[bits()/8];
        int pos = 0;
        while (val > 0) {
            if ((val / 8) > 0) {
                bytes[pos] = Byte.MIN_VALUE;
                pos ++;
            }
            else {
                bytes[pos] = (byte)(2^(bits()-val)); 
            }
            val = val - 8;
        }
        return bytes;
    }
    
    /** Compare two byte arrays. 
     * 
     * @param address
     * @param mask
     * @return 
     */
    private boolean equals(byte v1[], byte v2[]) {
        if (v1.length != v2.length) {
            return false;
        }
        for (int i = 0; i < v1.length; ++i) {
                if (v1[i] != v2[i]) {
                    return false;
            }
        }
        return true;
    }
    
    /** Apply a mask to an address 
     * 
     * @param address The address to mask
     * @param mask The mask to apply
     * @return The masked address
     */
    private byte[] mask(byte[] address, byte[] mask) {
        byte[] masked = new byte[address.length];
        for (int i = 0; i < address.length; ++i) {
            masked[i] = mask(address[i], mask[i]);
        }
        return masked;
    }

    private byte mask(byte val, byte mask) {
            int v = (val < 0) ? (val *-1)+128 : val;
            int m = (mask < 0) ?(mask *-1)+128 : mask;
            int r = v & m;
            if (r > 127) return (byte)(r-256); else return (byte)r;
    }

}
