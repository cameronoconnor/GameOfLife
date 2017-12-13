package uk.ac.cam.cjo41.gameoflife;

/**
 * Represents a world using a boolean array.
 */
public class World implements Cloneable {

    private boolean[][] mWorld;
    private int mHeight;
    private int mWidth;
    private boolean[] mDeadRow;
    private int mGeneration;
    private Pattern mPattern;

    /**
     * Constructor - takes a Pattern object and creates new world.
     * @param p  Pattern object
     * @throws PatternFormatException
     */
    public World(Pattern p) throws PatternFormatException {
        // Pattern constructor, uses replaceDeadRows()
        mPattern = p;
        mHeight = p.getHeight();
        mWidth = p.getWidth();
        mDeadRow = new boolean[mWidth];
        mWorld = new boolean[mHeight][mWidth];
        mPattern.initialise(this);
        replaceDeadRows();
    }

    /**
     * Copy constructor - takes an existing World object.
     * @param w   World object
     */
    public World(World w) {
        // Copy constructor
        mHeight = w.mHeight;
        mWidth = w.mWidth;
        mDeadRow = w.mDeadRow;
        // Deep copy of mWorld
        mWorld = new boolean[mHeight][mWidth];
        for (int i=0; i<mHeight; i++) {
            if (w.mWorld[i] == w.mDeadRow)
                mWorld[i] = mDeadRow;
            else
                mWorld[i] = w.mWorld[i].clone();
        }
    }

    /**
     * Replaces rows which are entirely dead with a reference to a single
     * (all-false) boolean array - saves memory
     */
    private void replaceDeadRows() {
        for (int i=0; i < mHeight; i++) {
            boolean[] row = mWorld[i];
            boolean allDead = true;
            for (boolean val : row) {
                if (val) {
                    allDead = false;
                    break;
                }
            }
            if (allDead) mWorld[i] = mDeadRow;
        }
    }

    /**
     * Gets width of world.
     * @return   Width of world
     */
    public int getWidth() {
        return mWidth;
    }

    /**
     * Gets height of world.
     * @return   Height of world
     */
    public int getHeight() {
        return mHeight;
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
     * Generates next generation and updates the local world reference.
     */
    protected void nextGeneration() {
        boolean[][] nextGeneration = new boolean[mHeight][];
        for (int y = 0; y < mHeight; ++y) {
            nextGeneration[y] = new boolean[mWorld[y].length];
            for (int x = 0; x < mWidth; ++x) {
                boolean nextCell = computeCell(x, y);
                nextGeneration[y][x] = nextCell;
            }
        }
        mWorld = nextGeneration;
        mGeneration++;
        replaceDeadRows();
    }

    /**
     * Sets cell at (row, col) to value
     * @param col
     * @param row
     * @param value
     */
    public void setCell(int col, int row, boolean value) {
        mWorld[row][col] = value;
    }

    /**
     * Gets value of cell at (row, col)
     * @param col
     * @param row
     * @return
     */
    public boolean getCell(int col, int row) {
        // Performs bounds-checked cell value lookup
        if (col < 0 || row < 0 || row > mHeight - 1)
            return false;
        if (col > mWidth - 1)
            return false;
        else return mWorld[row][col];
    }

    /**
     * Helper method - counts the live neighbours of cell (row, col).
     * @param col
     * @param row
     * @return   The number of live neighbours
     */
    private int countNeighbours(int col, int row) {
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
    private boolean computeCell(int col, int row) {
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
     * Cloning method - returns copy of self.
     * @return  copy of self
     */
    public World clone() throws CloneNotSupportedException {
        World cloned = (World) super.clone();
        // Deep clone mWorld
        cloned.mWorld = new boolean[mHeight][];
        for (int i=0; i<mHeight; i++) {
            if (mWorld[i] == mDeadRow)
                cloned.mWorld[i] = cloned.mDeadRow;
            else
                cloned.mWorld[i] = mWorld[i].clone();
        }
        return cloned;
    }
}
