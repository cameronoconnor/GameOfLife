package uk.ac.cam.cjo41.gameoflife;

/**
 * Abstract class which represents a world.
 */
public abstract class World implements Cloneable {

    private int mGeneration;
    public abstract boolean getCell(int col, int row);
    public abstract void setCell(int col, int row, boolean value);
    protected abstract void nextGenerationImpl();
    private Pattern mPattern;
    
    /**
     * Constructor - creates new Pattern from initialiser string.
     * @param init   Pattern format string
     * @throws PatternFormatException   If format string is invalid
     */
    public World(String init) throws PatternFormatException {
        mPattern = new Pattern(init);
    }
    
    /**
     * Constructor - creates new world from Pattern.
     * @param p   Pattern object
     */
    public World(Pattern p) { mPattern = p; }
    
    /**
     * Copy constructor.
     * @param w   World object
     */
    public World(World w) {
        mPattern = w.mPattern;
        mGeneration = w.mGeneration;
    }
    
    /**
     * Gets width of world.
     * @return   Width of world
     */
    public int getWidth() {
        return (mPattern.getWidth());
    }
    
    /**
     * Gets height of world.
     * @return   Height of world
     */
    public int getHeight() {
        return (mPattern.getHeight());
    }
    
    /**
     * Gets the generation number which this world represents.
     * @return   Current generation number
     */
    public int getGenerationCount() {
        return mGeneration;
    }
    
    /**
     * Gets the pattern which this world belongs to.
     * @return   Pattern
     */
    protected Pattern getPattern() {
        return mPattern;
    }
    
    /**
     * Advances to the next generation - updates the current object.
     */
    public void nextGeneration() {
        nextGenerationImpl();
        mGeneration++;
    }
    
    /**
     * Helper method - counts the live neighbours of cell (row, col).
     * @param col
     * @param row
     * @return   The number of live neighbours
     */
    protected int countNeighbours(int col, int row) {
        // counts number of live neighbours of given cell (col, row)
        int neighbours = 0;
        for (int r = row - 1; r <= row + 1; r++) {   // iterates through rows adjacent to (col, row)
            for (int c = col - 1; c <= col + 1; c++) {  // iterates through columns adjacent to (col, row)
                if (!(c == col && r == row))
                    neighbours += (getCell(c, r)) ? 1 : 0;   // increment neighbours if cell is alive
            }
        }
        return neighbours;
    }
    
    /**
     * Helper method - computes whether a cell will be alive or dead in the
     * next generation.
     * @param col
     * @param row
     * @return   True if alive, false if dead
     */
    protected boolean computeCell(int col, int row) {
        boolean liveCell = getCell(col, row);
        int neighbours = countNeighbours(col, row);
        boolean nextCell = false;
        // A live cell with less than two neighbours (underpopulation) or more than 3 neighbours (overcrowding) dies
        if (neighbours < 2 || neighbours > 3)
            nextCell = false;
        // A live cell with two or three neighbours lives (a balanced population)
        if (neighbours >= 2 && neighbours <= 3 && liveCell)
            nextCell = true;
        //A dead cell with exactly three live neighbours comes alive
        if (neighbours == 3 && !liveCell)
            nextCell = true;
        return nextCell;
    }
    
    /**
     * Clone method - returns copy of self.
     * @return   Copy of self
     */
    public World clone() {
        try {
            return (World) super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
