package me.legrange.net;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author gideon
 */
public abstract class IPNetwork<N extends IPNetwork> implements Serializable {

    /** return the address of this network */
    public String getAddress() throws InvalidAddressException {
        return longToString(address);
    }

    /** return the short mask of this network */
    public int getShortMask() {
        return (int) longMaskToShort(mask);
    }

    /** return the network after this one (with the same size) */
    public N getNetworkAfter() throws NetworkException {
        int shortMask = longMaskToShort(mask);
        long add = 1;
        for (int i = 0; i < (BITS - shortMask); ++i) {
            add = add << 1;
        }
        return newNetwork(address.add(BigInteger.valueOf(add)), mask);
    }
    
    /** return the network before this one (with the same size) */
    public N getNetworkBefore() throws NetworkException {
        int shortMask = longMaskToShort(mask);
        long add = 1;
        for (int i = 0; i < (BITS - shortMask); ++i) {
            add = add << 1;
        }
        return newNetwork(address.subtract(BigInteger.valueOf(add)), mask);
    }

    /** return the network with the given mask which surrounds (contains, encloses) this network */
    public N getEnclosingNetwork(int mask) throws NetworkException {
        if ((mask < 0) || (mask > 1)) {
            throw new InvalidAddressException(String.format("'%d' is not a valid netmask.", mask));
        }
        if (mask > longMaskToShort(this.mask)) {
            throw new InvalidAddressException(String.format("Network '%s' cannot be contained in a /%d network.", toString(), mask));
        }
        BigInteger longMask = shortMaskToLong(mask);
        BigInteger addr = address.and(longMask);
        return newNetwork(addr, longMask);
    }

    /** return true if the given addresss is contained in this network */
    public boolean containsAddress(String address) throws NetworkException {
        return (stringToLong(address)).and(mask).equals(this.address);
    }

    /** return true if the given network is a subnet of this network
     * @param net The 'inside' IPv6 Network
     * @return the result of the test
     * @throws NetworkException 
     */
    public boolean contains(N net) throws NetworkException {
        return (net.mask.compareTo(mask) >= 0) && net.address.and(mask).equals(address);
    }

       
    protected IPNetwork(BigInteger address, BigInteger mask, int bits) {
        this.BITS = bits;
        this.address = address;
        this.mask = mask;
    }

    protected abstract N newNetwork(BigInteger address, BigInteger mask);

 
    private BigInteger bytesToLong(byte bytes[]) {
        return new BigInteger(bytes);
    }

    private BigInteger shortMaskToLong(int mask) {
        BigInteger all = BigInteger.ONE.shiftLeft(BITS).subtract(BigInteger.ONE);
        BigInteger more = all.shiftLeft(BITS - mask);
        return more.and(all);
    }

    private String longToString(BigInteger address) throws InvalidAddressException {
        try {
            String addr = InetAddress.getByAddress(address.toByteArray()).toString();
            return addr;
        } catch (UnknownHostException e) {
            throw new InvalidAddressException(String.format("Invalid address found in internal representation. BUG?"));
        }
    }

    private int longMaskToShort(BigInteger mask) {
        return BITS - mask.bitCount();
    }

    private BigInteger stringToLong(String net) throws InvalidAddressException {
        try {
            InetAddress addr = InetAddress.getByName(net);
            if (addr.getAddress().length != (BITS / 8)) {
                throw new InvalidAddressException(String.format("Invalid address '%s', not in the correct address family.", net));
            }
            byte bytes[] = addr.getAddress();
            return bytesToLong(bytes);
        } catch (UnknownHostException e) {
            throw new InvalidAddressException(String.format("Invalid address '%s'.", net));
        }
    }
    
    private int BITS;
    protected BigInteger address;
    protected BigInteger mask;
    private static final long serialVersionUID = 2012123101;
}
