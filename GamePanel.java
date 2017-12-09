package uk.ac.cam.cjo41.gameoflife;

import javax.swing.*;
import java.awt.*;

/**
 * Represents the panel showing the game board.
 */
public class GamePanel extends JPanel {

    private World mWorld = null;
    
    /**
     * Creates game board from world
     *
     * @param g   java.awt.Graphics object
     */
    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // Paint the background white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Returns if no mWorld has been specified
        if (mWorld == null) return;

        // Gets sizes and stores
        int worldWidth = mWorld.getWidth();
        int worldHeight = mWorld.getHeight();
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();
        
        // Calculates maximum side size
        int dx = panelWidth / worldWidth;
        int dy = panelHeight / worldHeight;
        int side = Math.min(dx, dy);
        
        // Calculates number of pixels in 'gaps'
        int leftOverX = panelWidth - side * worldWidth;
        int leftOverY = panelHeight - side * worldHeight;
        int leftOver = Math.min(leftOverX, leftOverY);

        for (int row = 0; row < worldHeight; row++) {
            // Calculates height of given row and y position
            int mHeight = side;
            int ypos = row * side;
            if (row < leftOver) {
                ypos += row;
                mHeight++;
            }
            else ypos += leftOver;
            
            for (int col = 0; col < worldWidth; col++) {
                // Calculates width of given column and x position
                int mWidth = side;
                int xpos = col * side;
                if (col < leftOver) {
                    xpos += col;
                    mWidth++;
                }
                else xpos += leftOver;
                
                // Draws rectangle
                if (mWorld.getCell(col, row)) {
                    g.setColor(Color.BLACK);
                    g.fillRect( xpos, ypos, mWidth, mHeight);
                }
                else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect( xpos, ypos, mWidth, mHeight);
                }
            }
        }
    }
    
    /**
     * Takes a world as parameter and displays that world in the panel.
     *
     * @param w   World object
     */
    public void display(World w) {
        mWorld = w;
        repaint();
    }
}
