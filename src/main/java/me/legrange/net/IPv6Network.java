package me.legrange.net;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.UnknownHostException;

/** An IPv6 network */
public class IPv6Network extends IPNetwork {

    
    public static IPv6Network getByAddress(String address, int mask) throws NetworkException {
        if ((mask < 0) || (mask > 128)) {
            throw new InvalidAddressException(String.format("'%d' is not a valid IPv6 mask.", mask));
        }
        try {
            return new IPv6Network(new BigInteger(Inet6Address.getByName(address).getAddress()), BigInteger.valueOf(mask));
        } catch (UnknownHostException ex) {
            throw new InvalidAddressException(String.format("%s is not a valid address", address));
        }
    }

    @Override
    protected IPv6Network newNetwork(BigInteger address, BigInteger mask) {
        return new IPv6Network(address, mask);
    }

    private IPv6Network(BigInteger address, BigInteger mask) {
        super(address, mask, 128);
    }

}
