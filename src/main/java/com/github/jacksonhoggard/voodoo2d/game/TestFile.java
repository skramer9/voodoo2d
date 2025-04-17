package com.github.jacksonhoggard.voodoo2d.engine.testing;

import com.github.jacksonhoggard.voodoo2d.engine.mapping.Map;
import com.github.jacksonhoggard.voodoo2d.engine.mapping.Layer;

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

    private List<String> guideContent;
    private static final String GUIDE_FILE_PATH = "guide/game_guide.txt";
    private Map gameMap;

    /**
     * Constructor initializes the guide text and map info.
     */
    public TestFile(Map gameMap) {
        this.gameMap = gameMap;
        guideContent = new ArrayList<>();
        loadGuideText();
    }

    /**
     * Loads the game guide text from the resource file.
     */
    private void loadGuideText() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(GUIDE_FILE_PATH);
            if (inputStream != null) {
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
     */
    public String getGuideSection(String sectionTitle) {
        StringBuilder section = new StringBuilder();
        boolean inSection = false;

        for (String line : guideContent) {
            if (line.startsWith("## ")) {
                if (inSection) break;
                if (line.substring(3).trim().equalsIgnoreCase(sectionTitle)) {
                    inSection = true;
                }
            } else if (inSection) {
                section.append(line).append("\n");
            }
        }

        return section.length() > 0 ? section.toString() : null;
    }

    /**
     * Displays guide text in a formatted way.
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
     * Displays map information extracted from the provided Map instance.
     */
    public void displayMapInfo() {
        System.out.println("=============== MAP INFORMATION ===============");
        if (gameMap != null) {
            Layer[] layers = gameMap.getLayers();
            System.out.println("Number of Layers: " + layers.length);
            for (int i = 0; i < layers.length; i++) {
                System.out.println("- Layer " + (i + 1)); // No getName() available, using fallback
            }

            System.out.println("(TileSet information not available in this version)");
        } else {
            System.out.println("Map information is not available.");
        }
        System.out.println("===============================================");
    }

    /**
     * Example usage of this class.
     */
    public static void main(String[] args) {
        Map loadedMap = null; // Load your Map object here if available

        TestFile guideTest = new TestFile(loadedMap);

        guideTest.displayGuide();
        guideTest.displayMapInfo();

        String controlsSection = guideTest.getGuideSection("Game Controls");
        if (controlsSection != null) {
            System.out.println("\n=== Game Controls Section ===");
            System.out.println(controlsSection);
        }
    }
}
