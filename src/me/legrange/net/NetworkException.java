package me.legrange.net;

// 2007-02-26 - Documented and added new constructor. [gideon]
// 2007-02-26 - This code is very old and not documented. [gideon]
/** Thrown when network code goes wrong. */
public abstract class NetworkException extends Exception {

    /** make a new one */
    public NetworkException(String msg) {
        super(msg);
    }

    /** make a new one */
    public NetworkException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
