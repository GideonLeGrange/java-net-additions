package me.legrange.net;

public class InvalidAddressException extends NetworkException {

    public InvalidAddressException(String msg) {
        super(msg);
    }
    
    private static final long serialVersionUID = 0x354011482B14E864L;

}
