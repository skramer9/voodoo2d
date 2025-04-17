package com.github.jacksonhoggard.voodoo2d.engine.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * TestFile class for handling game guide text.
 * This class is responsible for loading and displaying the guide text
 * that helps users navigate the game interface and mechanics.
 */
public class TestFile {

    // Store the guide text content
    private List<String> guideContent;

    // File path to the guide text file in resources directory
    private static final String GUIDE_FILE_PATH = "guide/game_guide.txt";

    /**
     * Constructor initializes the guide text.
     */
    public TestFile() {
        guideContent = new ArrayList<>();
        loadGuideText();
    }

    /**
     * Loads the game guide text from the resource file.
     */
    private void loadGuideText() {
        try {
            // Get the resource as an input stream
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(GUIDE_FILE_PATH);

            if (inputStream != null) {
                // Read the text file line by line
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                String line;
                while ((line = reader.readLine()) != null) {
                    guideContent.add(line);
                }

                reader.close();
                inputStream.close();
            } else {
                System.err.println("Guide text file not found: " + GUIDE_FILE_PATH);
            }
        } catch (IOException e) {
            System.err.println("Error loading guide text: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the entire guide text as a single string.
     *
     * @return String containing the entire guide text
     */
    public String getCompleteGuideText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : guideContent) {
            stringBuilder.append(line).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Returns a specific section of the guide text.
     *
     * @param sectionTitle The title of the section to return
     * @return String containing the requested section or null if not found
     */
    public String getGuideSection(String sectionTitle) {
        StringBuilder section = new StringBuilder();
        boolean inSection = false;

        for (String line : guideContent) {
            // Check if this line is a section header
            if (line.startsWith("## ")) {
                // If we were in the target section and found a new section, we're done
                if (inSection) {
                    break;
                }

                // Check if this is the section we're looking for
                if (line.substring(3).trim().equalsIgnoreCase(sectionTitle)) {
                    inSection = true;
                }
            }
            // If we're in the target section, add this line to our result
            else if (inSection) {
                section.append(line).append("\n");
            }
        }

        return section.length() > 0 ? section.toString() : null;
    }

    /**
     * Displays guide text in a formatted way.
     * This method can be called to show instructions to the player.
     */
    public void displayGuide() {
        System.out.println("==============================================");
        System.out.println("               GAME GUIDE                    ");
        System.out.println("==============================================");

        for (String line : guideContent) {
            System.out.println(line);
        }

        System.out.println("==============================================");
    }

    /**
     * Example usage of this class.
     */
    public static void main(String[] args) {
        TestFile guideTest = new TestFile();

        // Display the entire guide
        guideTest.displayGuide();

        // Or get specific sections
        String controlsSection = guideTest.getGuideSection("Game Controls");
        if (controlsSection != null) {
            System.out.println("\nControls Section:");
            System.out.println(controlsSection);
        }
    }
}