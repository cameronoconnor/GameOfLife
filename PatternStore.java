package uk.ac.cam.cjo41.gameoflife;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gets patterns from a text file source (either locally or from a URL) and
 * stores them. Provides methods to get these patterns, e.g. by name, author etc.
 */
public class PatternStore {

    private List<Pattern> mPatterns = new LinkedList<>();
    private Map<String, List<Pattern>> mMapAuths = new HashMap<>();
    private Map<String, Pattern> mMapName = new HashMap<>();
    
    /**
     * Constructor - takes path to text file containing patterns.
     * @param source   Path to text file
     * @throws IOException
     */
    public PatternStore(String source) throws IOException {
        if (source.startsWith("http://")) loadFromURL(source);
        else loadFromDisk(source);
    }
    
    /**
     * Constructor - takes a Reader which has already been instantiated,
     * e.g. for testing purposes.
     * @param source   Reader object
     * @throws IOException
     */
    public PatternStore(Reader source) throws IOException {
        load(source);
    }
    
    /**
     * Reads patterns from reader passed as parameter.
     * @param r   Reader object
     * @throws IOException   BufferedReader throws exception
     */
    private void load(Reader r) throws IOException {
        BufferedReader b = new BufferedReader(r);
        String line = b.readLine();
        while (line != null) {
            try {
                // Initialises pattern from line in source file
                Pattern p = new Pattern(line);
                mPatterns.add(p);
                mMapName.put(p.getName(), p);
                // If no other patterns by same author already in list
                if (mMapAuths.get(p.getAuthor()) == null) {
                    List<Pattern> listOfAuthorsPatterns = new ArrayList<>();
                    listOfAuthorsPatterns.add(p);
                    mMapAuths.put(p.getAuthor(), listOfAuthorsPatterns);
                }
                else {
                    // Adds to pre-existing list
                    ArrayList<Pattern> listOfAuthorsPatterns = (ArrayList) mMapAuths.get(p.getAuthor());
                    listOfAuthorsPatterns.add(p);
                }
            }
            catch (PatternFormatException e) {
                System.out.println(e.getMessage());
            }
            // Reads next line
            line = b.readLine();
        }
    }
    
    /**
     * Called if path starts with "http://".
     * @param url   URL path to file
     * @throws IOException   InputStreamReader throws exception
     */
    private void loadFromURL(String url) throws IOException {
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        Reader r = new InputStreamReader(conn.getInputStream());
        load(r);
    }
    
    /**
     * Called if path doesn't start with "http://".
     * @param filename   Path to file
     * @throws IOException   FileReader throws exception
     */
    private void loadFromDisk(String filename) throws IOException {
        Reader r = new FileReader(filename);
        load(r);
    }
    
    /**
     * Gets a list of Patterns, sorted by name
     * @return   Sorted list of Patterns
     */
    public List<Pattern> getPatternsNameSorted() {
        List<Pattern> patternsToReturn = new ArrayList<>(mPatterns);
        Collections.sort(patternsToReturn);
        return patternsToReturn;
    }
    
    /**
     * Gets a list of Patterns, sorted by author
     * @return   Sorted list of Patterns
     */
    public List<Pattern> getPatternsAuthorSorted() {
        List<Pattern> patternsToReturn = new LinkedList<>();
        // Sorts patterns by author by using TreeMap
        TreeMap<String, List<Pattern>> authorsSorted = new TreeMap<>(mMapAuths);
        List<List<Pattern>> patternsSortedByAuthor = new ArrayList<>(authorsSorted.values());
        for (List<Pattern> l : patternsSortedByAuthor) {
            Collections.sort(l);
            patternsToReturn.addAll(l);
        }
        return patternsToReturn;
    }
    
    /**
     * Gets list of patterns which were created by the given author.
     * @param author   Author
     * @return   List of patterns by that author
     * @throws PatternNotFound   If no patterns by that author exist
     */
    public List<Pattern> getPatternsByAuthor(String author) throws PatternNotFound {
        if (mMapAuths.get(author) != null) {
            List<Pattern> patternsToReturn = new ArrayList<>(mMapAuths.get(author));
            Collections.sort(patternsToReturn);
            return patternsToReturn;
        }
        else throw new PatternNotFound("Pattern Not Found");
    }
    
    /**
     * Gets the Pattern which corresponds to the given name
     * @param name   Name of pattern to get
     * @return   Pattern object
     * @throws PatternNotFound   If no pattern by that name exists
     */
    public Pattern getPatternByName(String name) throws PatternNotFound {
        if (mMapName.get(name) != null)
            return mMapName.get(name);
        else
            throw new PatternNotFound("Pattern not found");
    }
    
    /**
     * Gets a list of all authors.
     * @return   List of authors
     */
    public List<String> getPatternAuthors() {
        List<String> authorsToReturn = new ArrayList<>(mMapAuths.keySet());
        Collections.sort(authorsToReturn);
        return authorsToReturn;
    }
    
    /**
     * Gets a list of pattern names, sorted lexicographically
     * @return   Sorted list of pattern names
     */
    public List<String> getPatternNames() {
        List<String> namesToReturn = new ArrayList<>(mMapName.keySet());
        Collections.sort(namesToReturn);
        return namesToReturn;
    }
    
    /**
     * Gets the patterns which can be represented by a given number of cells.
     * @param x   Number of cells
     * @return   List of Patterns
     */
    public List<String> getNumPatternsRepresentableBy(int x) {
        TreeMap<String, Pattern> patternsSorted = new TreeMap<>(mMapName);
        return patternsSorted.values().stream()
                .filter( p -> (p.getWidth() * p.getHeight() <= x))
                .map( p -> { return (p.getName() + " " + p.getHeight() + " " + p.getWidth()); })
                .collect(Collectors.toList());
    }
    
}
