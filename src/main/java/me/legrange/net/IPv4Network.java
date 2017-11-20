package me.legrange.net;

import static java.lang.String.format;

/**
 * An IPv4 network.
 */
public class IPv4Network extends IPNetwork<IPv4Network> {

    public static final IPv4Network ALL =  new IPv4Network(new byte[4],0);

    public static IPv4Network getNetwork(String address, int mask) throws InvalidAddressException {
        if ((mask < 0) || (mask > 32)) {
            throw new InvalidAddressException(format("Invalid mask %d for IPv4 network", mask));
        }
        return new IPv4Network(stringToBytes(address), mask);
    }

    @Override
    protected IPv4Network newNetwork(byte[] address, int mask) {
        return new IPv4Network(address, mask);
    }

    @Override
    protected int bits() {
        return 32;
    }

    private static byte[] stringToBytes(String addr) throws InvalidAddressException {
        addr = addr.trim().replace(" ", "");
        String parts[] = addr.split("\\.");
        if (parts.length != 4) {
            throw new InvalidAddressException(format("Address '%s' is not a valid IPv4 address", addr));
        }
        byte bytes[] = new byte[4];
        for (int i = 0; i < 4; ++i) {
            try {
                int val = Integer.parseInt(parts[i]);
                if (val > 127) {
                    val = val - 256;
                }
                bytes[i] = (byte) val;
            } catch (NumberFormatException ex) {
                throw new InvalidAddressException(format("Address '%s' is not a valid IPv4 address", addr), ex);
            }
        }
        return bytes;
    }
    
    /** Convert the given IPv4 address in byte array from to it's string representation. 
     * 
     * @param bytes The bytes to convert. We assume it is a valid four byte array.
     * @return The text representation. 
     */
    @Override
    protected String bytesToString(byte bytes[]) {
        StringBuilder buf = new StringBuilder(15);
        for (int i =0; i < 4; ++i) { 
            if (i > 0) {
                buf.append(".");
            }
            int val = bytes[i];
            if (val < 0) {
                val = 256+val;
            }
            buf.append(val);
        }
        return buf.toString();
    }

    private IPv4Network(byte[] address, int mask) {
        super(address, mask);
    }
}
