package uk.ac.cam.cjo41.gameoflife;

/**
 * Represents a world using a boolean array.
 */
public class ArrayWorld extends World implements Cloneable {

    private boolean[][] mWorld;
    private int mHeight;
    private int mWidth;
    private boolean[] mDeadRow;
    
    /**
     * Constructor - takes a Pattern object and creates new world.
     * @param p  Pattern object
     * @throws PatternFormatException
     */
    public ArrayWorld(Pattern p) throws PatternFormatException {
        // Pattern constructor, uses replaceDeadRows()
        super(p);
        mHeight = p.getHeight();
        mWidth = p.getWidth();
        mDeadRow = new boolean[mWidth];
        mWorld = new boolean[mHeight][mWidth];
        getPattern().initialise(this);
        replaceDeadRows();
    }
    
    /**
     * Copy constructor - takes an existing ArrayWorld object.
     * @param w   ArrayWorld object
     */
    public ArrayWorld(ArrayWorld w) {
        // Copy constructor
        super(w);
        mHeight = getHeight();
        mHeight = w.mHeight;
        mWidth = getWidth();
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
     * Generates next generation and updates the local world reference.
     */
    @Override
    protected void nextGenerationImpl() {
        boolean[][] nextGeneration = new boolean[mHeight][];
        for (int y = 0; y < mHeight; ++y) {
            nextGeneration[y] = new boolean[mWorld[y].length];
            for (int x = 0; x < mWidth; ++x) {
                boolean nextCell = computeCell(x, y);
                nextGeneration[y][x] = nextCell;
            }
        }
        mWorld = nextGeneration;
        replaceDeadRows();
    }
    
    /**
     * Sets cell at (row, col) to value
     * @param col
     * @param row
     * @param value
     */
    @Override
    public void setCell(int col, int row, boolean value) {
        mWorld[row][col] = value;
    }
    
    /**
     * Gets value of cell at (row, col)
     * @param col
     * @param row
     * @return
     */
    @Override
    public boolean getCell(int col, int row) {
        // Performs bounds-checked cell value lookup
        if (col < 0 || row < 0 || row > mHeight - 1)
            return false;
        if (col > mWidth - 1)
            return false;
        else return mWorld[row][col];
    }
    
    /**
     * Cloning method - returns copy of self.
     * @return  copy of self
     */
    public ArrayWorld clone() {
        ArrayWorld cloned = (ArrayWorld) super.clone();
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
