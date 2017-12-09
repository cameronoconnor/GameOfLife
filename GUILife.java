package uk.ac.cam.cjo41.gameoflife;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Game class - creates graphical user interface, including a means of
 * selecting a pattern, navigating through generations and viewing the world
 * at each stage.
 */
public class GUILife extends JFrame implements ListSelectionListener {

    private World mWorld;
    private PatternStore mStore;
    private ArrayList<World> mCachedWorlds = new ArrayList<>();
    private GamePanel mGamePanel;
    private JButton mPlayButton;
    private boolean mPlaying;
    private java.util.Timer mTimer;
    
    /**
     * Constructs a new GUILife object from an initialised PatternStore
     * object. This creates a window (1024x768) and creates the required
     * interaction panels - for patterns, controls and the game board.
     *
     * @param  ps   a PatternStore object initialised with patterns
     * @see         PatternStore
     */
    public GUILife(PatternStore ps) {
        super("Game of Life");
        mStore = ps;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024,768);
        // Add panels to window
        add(createPatternsPanel(),BorderLayout.WEST);
        add(createControlPanel(),BorderLayout.SOUTH);
        add(createGamePanel(),BorderLayout.CENTER);
    }
    
    /**
     * Adds a border with title to component.
     *
     * @param  component   the component to add the border to
     * @param  title       the title which will be shown
     */
    private void addBorder(JComponent component, String title) {
        Border etch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border tb = BorderFactory.createTitledBorder(etch,title);
        component.setBorder(tb);
    }
    
    /**
     * Creates the game panel by initialising a new GamePanel object
     * and calling GamePanel.display with the current world as the parameter.
     *
     * @see    GamePanel
     */
    private JPanel createGamePanel() {
        mGamePanel = new GamePanel();
        mGamePanel.display(mWorld);
        addBorder(mGamePanel,"Game Panel");
        return mGamePanel;
    }
    
    /**
     * Gets the patterns from the `PatternStore` object, sorted by name,
     * and displays these in the left-hand panel.
     *
     * @see    PatternStore
     */
    private JPanel createPatternsPanel() {
        // Creates patterns panel with scrollable list of patterns sorted by name
        JPanel patt = new JPanel(new GridLayout(1,1));
        addBorder(patt,"Patterns");

        // Gets patterns sorted by name, adds each to String[] patternData
        List<Pattern> patterns = mStore.getPatternsNameSorted();
        Pattern[] patternData = new Pattern[patterns.size()];
        int index = 0;
        for (Pattern p : patterns) {
            patternData[index] = p;
            index++;
        }

        // Seeds JList with patternData, adds and returns
        JList<Pattern> patternJList = new JList<>(patternData);
        patternJList.addListSelectionListener(this);
        patt.add(new JScrollPane(patternJList));
        return patt;
    }
    
    /**
     * Creates the 'control panel' with back, forward and play buttons.
     */
    private JPanel createControlPanel() {
        // Creates control panel with 3 buttons of equal width
        JPanel ctrl =  new JPanel(new GridLayout(1,3));
        addBorder(ctrl,"Controls");

        // Back button
        JButton backButton = new JButton("< Back");
        backButton.addActionListener(e -> {
            if (mPlaying) runOrPause();
            moveBack();
        });
        ctrl.add(backButton);

        // Play button
        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> runOrPause());
        ctrl.add(playButton);
        mPlayButton = playButton;

        // Forward Button
        JButton forwardButton = new JButton("Forward >");
        forwardButton.addActionListener(e -> {
            if (mPlaying) runOrPause();
            moveForward();
        });
        ctrl.add(forwardButton);

        return ctrl;
    }
    
    /**
     * Moves back a generation when called.
     */
    private void moveBack() {
        int currentGeneration = mWorld.getGenerationCount();
        // Gets world from cache and displays in GamePanel
        if (currentGeneration != 0) {
            mWorld = mCachedWorlds.get(currentGeneration - 1);
            mGamePanel.display(mWorld);
            addBorder(mGamePanel,("Generation: " + mWorld.getGenerationCount()));
        }
    }
    
    /**
     * Moves forward a generation when called.
     */
    private void moveForward() {
        int currentGeneration = mWorld.getGenerationCount();
        // Gets world from cache and displays in GamePanel
        if (currentGeneration + 1 < mCachedWorlds.size()) {
            mWorld = (mCachedWorlds.get(currentGeneration + 1));
            mGamePanel.display(mWorld);
            addBorder(mGamePanel,("Generation: " + mWorld.getGenerationCount()));
        }

        // Else generates next generation and adds to cache
        else {
            mWorld = copyWorld(true);
            if (mWorld != null) mWorld.nextGeneration();
            mCachedWorlds.add(mWorld);
            mGamePanel.display(mWorld);
            addBorder(mGamePanel,("Generation: " + mWorld.getGenerationCount()));
        }
    }
    
    /**
     * Implementation of the ListSelectionListener interface method:
     * changes pattern when user makes a selection in the patterns list
     *
     * @param   e   Event, prototype as inherited from interface
     */
    public void valueChanged(ListSelectionEvent e) {
        JList<Pattern> list = (JList<Pattern>) e.getSource();
        
        Pattern p = list.getSelectedValue();
        mCachedWorlds.clear();

        try {
            mWorld = new ArrayWorld(p);
        }
        catch (PatternFormatException pfe) {
            pfe.printStackTrace();
        }

        // Adds first generation to cache, prints world
        mCachedWorlds.add(mWorld);
        mGamePanel.display(mWorld);
        addBorder(mGamePanel,"Generation: 0");
    }
    
    /**
     * Method to copy current world. Returns new copied world.
     *
     * @param   useCloning    Use `true` by default for better performance
     * @return                copy of current world
     */
    private World copyWorld(boolean useCloning) {
        if (useCloning) {
            return mWorld.clone();
        }
        else {
            return new ArrayWorld((ArrayWorld) mWorld);
        }
    }
    
    /**
     * Called when `play` button is pressed. If not currently playing, it will
     * advance one generation every 500ms. Otherwise it will pause play.
     */
    private void runOrPause() {
        if (mPlaying) {
            mTimer.cancel();
            mPlaying = false;
            mPlayButton.setText("Play");
        }
        else {
            mPlaying=true;
            mPlayButton.setText("Stop");
            mTimer = new java.util.Timer(true);
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    moveForward();
                }
            }, 0, 500);
        }
    }
    
    /**
     * Initialises PatternStore object and creates a new GUILife object.
     */
    public static void main(String[] args) throws IOException {
        PatternStore ps = new PatternStore("http://www.cl.cam.ac.uk/teaching/1617/OOProg/ticks/life.txt");
        GUILife gui = new GUILife(ps);
        gui.setVisible(true);
    }

}
