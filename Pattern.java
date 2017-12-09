package uk.ac.cam.cjo41.gameoflife;

/**
 * Represents an initial world pattern.
 */
public class Pattern implements Comparable<Pattern> {

    private String mName;
    private String mAuthor;
    private int mWidth;
    private int mHeight;
    private int mStartCol;
    private int mStartRow;
    private String mCells;
    
    /**
     * Gets the pattern name.
     * @return   Name of pattern
     */
    public String getName() {
        return mName;
    }
    
    /**
     * Gets the pattern's author.
     * @return   Name of pattern's author.
     */
    public String getAuthor() {
        return mAuthor;
    }
    
    /**
     * Gets the pattern's width.
     * @return   Width of pattern.
     */
    public int getWidth() {
        return mWidth;
    }
    
    /**
     * Gets the pattern's height.
     * @return   Height of pattern
     */
    public int getHeight() {
        return mHeight;
    }
    
    /**
     * Gets the column at which the cell definition starts.
     * @return   Starting column
     */
    public int getStartCol() {
        return mStartCol;
    }
    
    /**
     * Gets the row at which the cell definition starts.
     * @return   Starting row
     */
    public int getStartRow() {
        return mStartRow;
    }
    
    /**
     * Gets the cell definition for this pattern.
     * @return   Cell definition
     */
    public String getCells() {
        return mCells;
    }
    
    /**
     * Constructor - creates new pattern from pattern format string.
     * @param format   Pattern format string
     * @throws PatternFormatException
     */
    public Pattern(String format) throws PatternFormatException {
        // throw exception if format empty
        if (format.equals(""))
            throw new PatternFormatException("Please specify a pattern.");

        // initialise array of args
        String[] arguments = format.split(":");

        // throws exception if wrong number of args
        if (arguments.length != 7)
            throw new PatternFormatException("Invalid pattern format: Incorrect number of fields in pattern (found " + arguments.length + ").");

        // initialises member variables
        mName = arguments[0];
        mAuthor = arguments[1];

        // throws exception if parse of width fails
        try { mWidth = Integer.parseInt(arguments[2]); }
        catch (NumberFormatException e) {
            throw new PatternFormatException("Invalid pattern format: Could not interpret the width field as a number ('" + arguments[2] + "' given).");
        }

        // throws exception if parse of height fails
        try { mHeight = Integer.parseInt(arguments[3]); }
        catch (NumberFormatException e) {
            throw new PatternFormatException("Invalid pattern format: Could not interpret the height field as a number ('" + arguments[3] + "' given).");
        }

        // throws exception if parse of startCol fails
        try { mStartCol = Integer.parseInt(arguments[4]); }
        catch (NumberFormatException e) {
            throw new PatternFormatException("Invalid pattern format: Could not interpret the startX field as a number ('" + arguments[4] + "' given).");
        }

        // throws exception if parse of startRow fails
        try { mStartRow = Integer.parseInt(arguments[5]); }
        catch (NumberFormatException e) {
            throw new PatternFormatException("Invalid pattern format: Could not interpret the startY field as a number ('" + arguments[5] + "' given).");
        }

        mCells = arguments[6];
    }
    
    /**
     * Takes a world object and initialises it, so that it represents
     * generation 0 of this pattern.
     * @param world   World to be initialised
     * @throws PatternFormatException
     */
    public void initialise(World world) throws PatternFormatException {
        String[] cellsArr = mCells.split(" ");

        for (int i = 0; i < cellsArr.length; i++) {
            char[] currentRow = cellsArr[i].toCharArray();
            for (int j = 0; j < currentRow.length; j++) {
                if (currentRow[j] == '1')
                    world.setCell(j+mStartCol, i+mStartRow, true);
                // throws exception if pattern not composed of 0, 1 or space
                else if (currentRow[j] != '0' && currentRow[j] != ' ')
                    throw new PatternFormatException("Invalid pattern format: Malformed pattern '" + mCells + "'.");

            }
        }
    }
    
    /**
     * Lexicographical comparator - by pattern name.
     * @param p   Pattern with which to compare this
     * @return    Integer as per
     */
    @Override
    public int compareTo(Pattern p) {
        if (mName.compareTo(p.mName) > 0) return 1;
        else if (mName.compareTo(p.mName) < 0) return (-1);
        else return 0;
    }
    
    /**
     * Overrides toString method - so patterns are shown correctly
     * in scrollable list
     * @return   Pattern as string
     * @see GUILife
     */
    @Override
    public String toString() {
        return (mName + " (" + mAuthor + ")");
    }
}
