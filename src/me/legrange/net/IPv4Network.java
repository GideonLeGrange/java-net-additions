package me.legrange.net;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/** An IPv4 network */
public class IPv4Network extends IPNetwork<IPv4Network> {

    public static IPv4Network getByAddress(String address, int mask) throws NetworkException {
        if ((mask < 0) || (mask > 32)) {
            throw new InvalidAddressException(String.format("'%d' is not a valid IPv4 mask.", mask));
        }
        try {
            return new IPv4Network(new BigInteger(Inet4Address.getByName(address).getAddress()), BigInteger.valueOf(mask));
        } catch (UnknownHostException ex) {
            throw new InvalidAddressException(String.format("%s is not a valid address", address));
        }
    }

    public static IPv4Network getByAddress(String address, String mask) throws NetworkException {
        try {
            return getByAddress(address, Integer.parseInt(mask));
        }
        catch (NumberFormatException e) {
            throw new InvalidAddressException(String.format("%s is not a valid mask", mask));
        }
    }

    @Override
    protected IPv4Network newNetwork(BigInteger address, BigInteger mask) {
        return new IPv4Network(address, mask);
    }

    private IPv4Network(BigInteger address, BigInteger mask) {
        super(address, mask, 32);
    }

 


}
