package pt.ipbeja.po2.sokoban2023.gui;

import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pt.ipbeja.po2.sokoban2023.gui.guiimages.SokobanBoardImages;
import pt.ipbeja.po2.sokoban2023.gui.guiimages.StartJavaFXGUIImages;
import pt.ipbeja.po2.sokoban2023.gui.guitext.SokobanBoardText;
import pt.ipbeja.po2.sokoban2023.gui.guitext.StartJavaFXGUIText;
import pt.ipbeja.po2.sokoban2023.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static pt.ipbeja.po2.sokoban2023.gui.guiimages.StartJavaFXGUIImages.sokoban;

/**
 * SokobanFiles class contains methods for file operations and handling game files.
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public class SokobanFiles {
    public static Position keeperPosition = new Position(3, 5);
    public static String boardContent =
            """
                    FFWWWWFF
                    FFWFFWFF
                    WWWFFWWW
                    WFFEEFFW
                    WFFFFFFW
                    WWWWWWWW""";

    public static Set<Position> boxesPositions = Set.of(new Position(3, 2), new Position(3, 3));

    /**
     * Opens a file chooser dialog to select a level file, then executes the game with the selected file based on the GUI type
     *
     * @param event
     * @param primaryStage
     * @param guiType
     */
    public static void fileChooserLevelFiles(ActionEvent event, Stage primaryStage, int guiType) {
        // https://www.youtube.com/watch?v=hNz8Xf4tMI
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("levelFiles"));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SOK Files", "*.sok"));
        File selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            if (guiType == 0) {
                executeFileGame(selectedFile);
                SokobanBoardImages.restart(primaryStage);
            } else {
                executeFileGame(selectedFile);
                SokobanBoardText.restart(primaryStage);
            }
        } else {
            incorrectFileAlert("Invalid file", "The file you choosed is not valid!");
        }
    }


    /**
     * Opens a file chooser dialog to select a route file and executes the route based on the selected file
     */
    public static void fileChooserRoute() {
        // https://www.youtube.com/watch?v=hNz8Xf4tMI
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("routes"));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT Files", "*.txt"));
        File selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            executeRoute(selectedFile);
        } else {
            System.out.println("The file you chose is not valid!");
        }
    }

    /**
     * Write all map lines to one String variable
     *
     * @param filename file to read
     * @return string with all lines of a file
     */
    public static void executeFileGame(File filename) {
        SokobanMenuBar.levelName = filename.getName().substring(0, filename.getName().length() - 4);
        String[] fileLines = readFileToStringArray(filename.getAbsolutePath());
        String map = "";
        int numBoxes = Integer.parseInt(fileLines[2]);
        Set<Position> boxesPosSet = new HashSet<Position>();
        Position keeper = new Position(Integer.parseInt(fileLines[1].substring(0, 1)), Integer.parseInt(fileLines[1].substring(2, 3)));

        int i = 0;
        while (i < numBoxes) {
            boxesPosSet.add(new Position(Integer.parseInt((fileLines[3 + i].substring(0, 1))), Integer.parseInt(fileLines[3 + i].substring(2, 3))));
            i++;
        }
        extractMapAndKepper(fileLines, map, keeper, i);
        boxesPositions = boxesPosSet;
    }

    /**
     * Executes the route specified in the given file.
     * @param filename
     */
    public static void executeRoute(File filename) {
        String[] fileLinesRoute = readFileToStringArray(filename.getAbsolutePath());
        if (!fileLinesRoute[0].contains(SokobanMenuBar.levelName)) {
            incorrectFileAlert("Incorrect map route", "Please select a route for the current map!");
        } else {
            String[] fileLinesLevel = readFileToStringArray("levelFiles/" + fileLinesRoute[0] + ".sok");
            String map = "";
            int numBoxes = Integer.parseInt(fileLinesLevel[2]);
            Position keeper = new Position(Integer.parseInt(fileLinesRoute[1].substring(0, 1)), Integer.parseInt(fileLinesRoute[1].substring(2, 3)));
            extractMapAndKepper(fileLinesLevel, map, keeper, numBoxes);

            int numDirections = fileLinesRoute.length - 2;
            Direction[] directions = new Direction[numDirections];
            final Map<String, Direction> stringToDir = Map.of(
                    "UP", Direction.UP,
                    "DOWN", Direction.DOWN,
                    "LEFT", Direction.LEFT,
                    "RIGHT", Direction.RIGHT);
            int k = 0;
            for (int i = 2; i < fileLinesRoute.length; i++) {
                directions[k++] = stringToDir.get(fileLinesRoute[i]);
            }
            System.out.println(Arrays.deepToString(directions));

            for (Direction d : directions) {
                try {
                    Thread.sleep(200);
                    System.out.println(d);
                    sokoban.moveKeeper(d);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * read all lines to one array of Strings
     *
     * @param filename file to read
     * @return array with one line in each position
     * or empty array if error reading file
     */
    public static String[] readFileToStringArray(String filename) {
        try {
            return Files.readAllLines(Paths.get(filename)).toArray(new String[0]);
        } catch (IOException e) {
            String errorMessage = "Error reading file " + filename;
            System.out.println(errorMessage);
            return new String[0];
        }
    }

    /**
     * Prompts the user to enter a player name with less than 3 characters and returns the input.
     * @return userInput
     */
    public static String askUserForName() {
        String userInput = "";
        boolean validInput = false;
        Scanner myInput = new Scanner(System.in);
        while (!validInput) {
            System.out.print("Enter player name with less then 3 char: ");
            userInput = myInput.next();
            if (userInput.length() > 1 && userInput.length() < 4) {
                validInput = true;
            } else {
                System.out.println("Invalid name!");
            }
        }
        return userInput;
    }

    /**
     * Extracts the map and keeper information from the given file lines and updates the board content and keeper position variables accordingly.
     * @param fileLines current file lines
     * @param map variable to store the map
     * @param keeper variable to store Kepper position
     * @param mapIndex index of the map in the file
     */
    private static void extractMapAndKepper(String[] fileLines, String map, Position keeper, int mapIndex) {
        for (int j = 3 + mapIndex; j < fileLines.length; j++) {
            if (fileLines[j].contains("W") || fileLines[j].contains("F") || fileLines[j].contains("E")) {
                map += fileLines[j] + "\n";
            }
        }
        boardContent = map.substring(0, map.length() - 1);
        keeperPosition = keeper;
    }

    /**
     * Displays an alert with the specified header text and content text to indicate an incorrect file.
     * @param headerText Alert header text
     * @param contentText Alert content text
     */
    public static void incorrectFileAlert(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        alert.setX(bounds.getMaxX() / 2 - 300);

        alert.setTitle("");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        ButtonType okButton = new ButtonType("Ok");
        alert.getButtonTypes().setAll(okButton);
        alert.showAndWait();
    }
}

