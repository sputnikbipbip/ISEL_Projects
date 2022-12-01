package com.example.g18_covidExtermination.Game.model;

import com.example.g18_covidExtermination.Game.model.Level;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Loads a game level from the file read with the scanner indicated in the constructor.<br/>
 * The file contains several levels.<br/>
 * Each level has a number from 1 to N.<br/><br/>
 * The first line of description for a level must conform to the format:<br/>
 * <code>#NUMBER HEIGHT x WIDTH</code><br/>
 * Where: <code>NUMBER</code> is the level number.<br/>
 * <code>HEIGHT</code> is the number of lines.<br/>
 * <code>WIDTH</code> is the number of columns.<br/>
 * The following <code>HEIGHT</code> lines describe the cells of the level.<br/>
 * Each line contains exactly <code>WIDTH</code> characters.<br/>
 * Each character corresponds to a cell.<br/>
 */
public class Loader {
    private final Scanner in;   // Scanner used to read the file
    private int lineNumber;     // Current line number
    private String line;        // Text of current line

    private Level model;      // The loaded model
    private int height, width;   // Dimensions of current level

    /**
     * Build the loader to read it from the file through the scanner
     * @param in The scanner to use
     */
    public Loader(Scanner in) {
        this.in = in;
    }

    /**
     * Reads the level identified by the number.<br/>
     * @param levelNumber The level number
     * @return The model for the loaded level or null if level not found
     * @throws LevelFormatException If an error is found in the file
     */
    public Level load(int levelNumber) throws LevelFormatException {
        if (!findHeader(levelNumber))    // Find the header line
            return null;
        model = new Level(levelNumber,height,width);    // Build the level model
        loadGrid();                         // Load cells information
        return model;
    }

    /**
     * Reads again the level.<br/>
     * Assumes no error can found in the file
     * @param oldModel The level to reload
     */
    void reload(Level oldModel) {
        try {
            model = oldModel;
            findHeader(model.getNumber());    // Find the header line
            //model.reset();
            loadGrid();                         // Load cells information
        } catch (LevelFormatException e) {
            e.printStackTrace();
        }
    }
    /**
     * Read the square grid and instantiate each square according to its description.<br/>
     * @throws LevelFormatException If an error is found in square descriptions
     */
    private void loadGrid() throws LevelFormatException {
        try {
            for (int l = 0; l < height; ++lineNumber, ++l) {
                line = in.nextLine();                  // Read a line of cells
                if (line.length() > width)               // Verify number of cells in line
                    error("Wrong number of cells in line");
                for (int c = 0; c < line.length(); c++) {
                    char type = line.charAt(c);
                    model.put(l, c, type);           // Add cell information to the model
                }
            }
        }
        catch (LevelFormatException lfe) { throw lfe; }
        catch (Exception e) { error(e.getMessage(),e); }
    }

    /**
     * Find the header line for the level<br/>
     * Stores the dimensions of the level in <code>height</code> and <code>width</code> fields.
     * @param level The level number
     * @throws LevelFormatException If an errors is found in the file or level not found.
     */
    private boolean findHeader(int level) throws LevelFormatException {
        try {
            int idx;
            for (lineNumber = 1; ; ++lineNumber) {
                line = in.nextLine();
                if (line.length() == 0 || line.charAt(0) != '#') continue;
                if ((idx = line.indexOf(' ')) <= 1) error("Invalid header line");
                if (Integer.parseInt(line.substring(1, idx)) == level) break;
            }
            int idxSep = line.indexOf('x',idx+1);
            if (idxSep<=0) error("Missing dimensions of level "+level);
            height = Integer.parseInt(line.substring(idx+1,idxSep).trim());
            width = Integer.parseInt(line.substring(idxSep+1).trim());
        } catch (NumberFormatException e) {
            error("Invalid number:" + e.getMessage());
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    /**
     * Helper method to launch a LevelFormatException in internal methods.
     * @param msg The exception message
     * @throws LevelFormatException
     */
    private void error(String msg) throws LevelFormatException {
        throw new LevelFormatException(msg);
    }
    /**
     * Helper method to launch a LevelFormatException in internal methods.
     * @param msg The exception message
     * @param cause The internal cause of exception
     * @throws LevelFormatException
     */
    private void error(String msg, Exception cause) throws LevelFormatException {
        throw new LevelFormatException(msg,cause);
    }

    /**
     * Launched when a level loading error is detected.
     * The message describes the type of error.
     * Has the line number and the line where the error was detected.
     */
    public class LevelFormatException extends Exception {
        LevelFormatException(String msg) {
            super(msg);
        }
        LevelFormatException(String msg, Exception cause) {
            super(msg,cause);
        }
        public int getLineNumber() { return lineNumber; }
        public String getLine() { return line; }
    }
}
