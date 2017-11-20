package me.legrange.net;

import static java.lang.String.format;

/**
 * An IPv6 network
 */
public class IPv6Network extends IPNetwork {
    
        public static final IPv6Network ALL =  new IPv6Network(new byte[16],0);

    
    public static IPv6Network getNetwork(String address, int mask) throws InvalidAddressException {
        if ((mask < 0) || (mask > 128)) {
            throw new InvalidAddressException(format("Invalid mask %d for IPv6 network", mask));
        }
        return new IPv6Network(stringToBytes(address), mask);
    }

    @Override
    protected IPNetwork newNetwork(byte[] address, int mask) {
        return new IPv6Network(address, mask);
    }

    @Override
    protected int bits() {
        return 128;
    }

    private static byte[] stringToBytes(String addr) throws InvalidAddressException {
        addr = addr.trim();
        String parts[] = addr.split(":");
        byte bytes[] = new byte[16];
        int i = 0;
        while (i < parts.length) {
            String part = parts[i];
            if (part.isEmpty()) {
                if (i == 0) {
                    i++;
                    continue;
                }
                else {
                    break; // we've encountered :: so stop parsing from the front
                }
            }
            try {
                byte bs[] = partToBytes(part);
                bytes[i * 2] = bs[0];
                bytes[i * 2 + 1] = bs[1];
                i++;
            } catch (NumberFormatException ex) {
                throw new InvalidAddressException(format("Address '%s' is not a valid IPv6 address", addr), ex);

            }
        }
        int j = parts.length - 1; 
        int e = 7;
        while (j > i) {
            byte bs[] = partToBytes(parts[j]);
            bytes[e*2] = bs[0];
            bytes[e*2+1] = bs[1];
            j--;
            e--;
        }
        return bytes;
    }

    @Override
    protected String bytesToString(byte bytes[]) {
        StringBuilder buf = new StringBuilder(39);
        for (int i = 0; i < 8; ++i) {
            if (i > 0) {
                buf.append(":");
            }
            int val = (byteToUnsignedInt(bytes[i*2]) << 8) + byteToUnsignedInt(bytes[i*2+1]);
            buf.append(format("%x", val));
        }
        return buf.toString();
    }

    /**
     * Converts a single part (between the ':'s) of an IPv6 address to a two
     * byte array.
     *
     * @param part The part to convert
     * @return The two bytes
     * @throws NumberFormatException if there is invalid (non hex) text in the part
     */
    private static byte[] partToBytes(String part) throws NumberFormatException {
        byte bytes[] = new byte[2];
        int val = Integer.valueOf(part, 16);
        bytes[0] = unsignedIntToByte(val & 0xFF00);
        bytes[1] = unsignedIntToByte(val & 0xFF);
        return bytes;
    }
    
    private static byte unsignedIntToByte(int val) {
        if (val > 127) {
            val = 256 - val;
        }
        return (byte)val;
    }
    
    private int byteToUnsignedInt(byte b) {
        int val = b;
        if (val < 0) {
            val = val + 256; 
        }
        return val;
    }

    private IPv6Network(byte[] address, int mask) {
        super(address, mask);
    }
 
}
