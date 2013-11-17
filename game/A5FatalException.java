package game;

/******************************************************************************
 * A5FatalException.java
 *
 * A simple runtime exception class to be used to replace System.exit(), assert,
 * and any statements that abnormally terminate the program. It is intended
 * for abnormal termination only and should not be used for normal exception
 * handling.
 *
 * The exception is a sub-class of RuntimeException and hence doesn't have to
 * be caught or declared "throws". 
 */

public class A5FatalException extends RuntimeException {
	// to make the compiler happy. See Serialization in Java for more info on what this means
	public static final long serialVersionUID = 1L;
	
	public A5FatalException() { super(); }
    public A5FatalException(String msg) { super(msg); }
}
