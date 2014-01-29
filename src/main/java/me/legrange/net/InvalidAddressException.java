package me.legrange.net;

/** Thrown when parsing an invalid network address */
public class InvalidAddressException extends NetworkException {

    public InvalidAddressException(String msg) {
        super(msg);
    }

    public InvalidAddressException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    

}
