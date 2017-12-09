package uk.ac.cam.cjo41.gameoflife;

/**
 * Exception - raised when a pattern isn't found (when searching by name).
 */
public class PatternNotFound extends Exception {
    
    /**
     * Constructor
     * @param msg
     */
    public PatternNotFound(String msg) {
        super(msg);
    }

}
