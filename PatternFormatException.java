package uk.ac.cam.cjo41.gameoflife;

/**
 * Exception - raised when a pattern is constructed with an invalid serial
 */
public class PatternFormatException extends Exception {
    
    /**
     * Constructor
     * @param msg
     */
    public PatternFormatException(String msg) {
        super(msg);
    }

}
