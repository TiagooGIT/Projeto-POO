package pt.ipbeja.po2.sokoban2023.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pt.ipbeja.po2.sokoban2023.gui.guiimages.SokobanBoardImages;
import pt.ipbeja.po2.sokoban2023.gui.guiimages.StartJavaFXGUIImages;
import pt.ipbeja.po2.sokoban2023.gui.guitext.SokobanBoardText;
import pt.ipbeja.po2.sokoban2023.gui.guitext.StartJavaFXGUIText;
import pt.ipbeja.po2.sokoban2023.model.*;


import java.util.*;

import static pt.ipbeja.po2.sokoban2023.gui.SokobanFiles.readFileToStringArray;
import static pt.ipbeja.po2.sokoban2023.model.SokobanGameModel.*;

/**
 * Start a game with a hardcoded board and labels
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public class SokobanMenuBar {
    private static Label kepperCoordinates = new Label("");
    public static Label chronometer = new Label("00:00");
    public static Timer timer = new Timer();
    public static boolean started = false;
    public static int elapsedTime = 0;
    private static final int TIMETICK = 1000;
    private static final int TIMEDIVISION = 60;
    public static String playerName = "";
    public static String levelName = "default";
    public static Optional<ButtonType> result;
    public static int currentHistoryIndex = 0;

    /**
     * Creates a menu bar with various menu options for the Sokoban game.
     *
     * @param primaryStage stage
     * @param guiType current gui type
     * @param keeperPosition Kepper position
     * @return MenuBar variable with the gui menu bar
     */
    public static VBox createMenuBar(Stage primaryStage, int guiType, Position keeperPosition) {
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        MenuBar menuBar = new MenuBar();
        Menu menuBarOption1 = new Menu("Game");
        Menu menuBarOption2 = new Menu("File");
        MenuItem menuItemOption1 = new MenuItem("Open");
        MenuItem menuItemOption2 = new MenuItem("Open Route");
        MenuItem menuItemOption3 = new MenuItem("Restart");
        MenuItem menuItemOption4 = new MenuItem("Exit");
        MenuItem menuItemOption5 = new MenuItem("Undo");
        MenuItem menuItemOption6 = new MenuItem("Redo");
        menuBarOption1.getItems().addAll(menuItemOption3, menuItemOption5, menuItemOption6, menuItemOption4);
        menuBarOption2.getItems().addAll(menuItemOption1, menuItemOption2);
        menuItemOption1.setOnAction(e -> SokobanFiles.fileChooserLevelFiles(e, primaryStage, guiType));
        menuItemOption2.setOnAction(e -> SokobanFiles.fileChooserRoute());

        guiTypeSwitch(guiType, menuItemOption3, menuItemOption4, menuItemOption5, primaryStage);

        menuBar.getMenus().addAll(menuBarOption1, menuBarOption2);

        String kepperInitialPos = keeperPosition.toString().charAt(0) + ", " + numToAlphabet(Integer.parseInt(keeperPosition.toString().substring(3, 4)));
        kepperCoordinates = new Label("Kepper Coordinates: (" + kepperInitialPos + ")");
        kepperCoordinates.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: blue;");
        kepperCoordinates.setPadding(new Insets(5));

        chronometer.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: red;");
        hBox.getChildren().addAll(menuBar, chronometer);
        vBox.getChildren().addAll(hBox, kepperCoordinates);
        return vBox;
    }

    /**
     * Handles the GUI type switch based on the given GUI type.
     *
     * @param guiType current gui type
     * @param menuItemOption3 menu bar select option 3
     * @param menuItemOption4 menu bar select option 4
     * @param menuItemOption5 menu bar select option 5
     * @param primaryStage stage
     */
    public static void guiTypeSwitch(int guiType, MenuItem menuItemOption3, MenuItem menuItemOption4, MenuItem menuItemOption5, Stage primaryStage) {
        if (guiType == 0) {
            menuItemOption3.setOnAction(e -> SokobanBoardImages.restart(primaryStage));
            menuItemOption4.setOnAction(e -> StartJavaFXGUIImages.exit());
        } else {
            menuItemOption3.setOnAction(e -> SokobanBoardText.restart(primaryStage));
            menuItemOption4.setOnAction(e -> StartJavaFXGUIText.exit());
        }
        menuItemOption5.setOnAction(e -> undoGame(primaryStage, guiType));
    }

    /**
     * Sets the cronometer and coordinates for the Sokoban game.
     * It retrieves the initial and final positions of the keeper and updates the corresponding text fields.
     * It also starts the cronometer.
     */
    public static void setCronometerAndCoordinates() {
        String newInitialPos = SokobanGameModel.initialPos.toString().charAt(0) + ", " + numToAlphabet(Integer.parseInt(SokobanGameModel.initialPos.toString().substring(3, 4)));
        String newFinalPos = SokobanGameModel.newPosition.toString().charAt(0) + ", " + numToAlphabet(Integer.parseInt(SokobanGameModel.newPosition.toString().substring(3, 4)));
        setTextKepperCoordinates(newInitialPos, newFinalPos);
        startChronometer();
    }

    /**
     * Sets the text for the keeper coordinates.
     *
     * @param initialPos Kepper initial position
     * @param newPosition Kepper final position
     */
    public static void setTextKepperCoordinates(String initialPos, String newPosition) {
        kepperCoordinates.setText(String.format("Kepper Coordinates: (%s) -> (%s)", initialPos, newPosition));
    }

    /**
     * Retrieves the best level scores from the specified file
     *
     * @param fileName
     * @return an array of Score objects representing the best level scores
     */
    public static Score[] bestLevelScores(String fileName) {
        String[] fileLines = readFileToStringArray(fileName);
        int nLevelScores = 0;
        for (int i = 0; i < fileLines.length; i++) {
            String[] lineParts = fileLines[i].split(", ");
            if (lineParts[0].equals(levelName))
                nLevelScores++;
        }
        Score[] bestLevelScoresArray = new Score[nLevelScores];

        int k = 0;
        for (int i = 0; i < fileLines.length; i++) {
            String[] lineParts = fileLines[i].split(", ");
            if (lineParts[0].equals(levelName))
                bestLevelScoresArray[k++] = (new Score(lineParts[1], lineParts[0], Integer.parseInt(lineParts[2])));
        }
        Arrays.sort(bestLevelScoresArray, new SortByScore());
        return bestLevelScoresArray;
    }

    /**
     * Starts the cronometer to measure elapsed time.
     * If the cronometer is already started, it cancels the previous timer before starting a new one.
     * The elapsed time is updated every timetick milliseconds.
     */
    public static void startChronometer() {
        if (!started) {
            if (timer != null) timer.cancel(); // Clean Timers if exists

            timer = new Timer(); // Create a new timer
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    elapsedTime++;
                    int min = elapsedTime / TIMEDIVISION;
                    int sec = elapsedTime % TIMEDIVISION;
                    String time = String.format("%02d:%02d", min, sec);
                    Platform.runLater(() -> chronometer.setText(time));
                }
            }, TIMETICK, TIMETICK);
            started = true;
        }
    }

    /**
     * Displays an end game alert if all boxes are stored.
     * Saves the current score and directions to files.
     * Switches to the score panel based on the GUI type.
     * Provides options to restart the level or exit the game.
     *
     * @param sokoban
     * @param currentStage
     * @param guiType
     */
    public static void endGameAlert(SokobanGameModel sokoban, Stage currentStage, int guiType) {
        if (sokoban.allBoxesAreStored()) {
            Score currentScore = new Score(SokobanMenuBar.playerName, SokobanMenuBar.levelName, SokobanGameModel.score);
            SokobanGameModel.saveScoresToFile(currentScore);
            SokobanGameModel.saveDirectionToFile();

            scorePanelSwitch(guiType, currentScore);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            alert.setX(bounds.getMaxX() / 2 - 300);
            alert.setTitle("");
            alert.setHeaderText("");
            alert.setContentText("Level completed!");
            ButtonType restartButton = new ButtonType("Restart");
            ButtonType exitButton = new ButtonType("Exit");

            alert.getButtonTypes().setAll(restartButton, exitButton);
            result = alert.showAndWait();
            if (guiType == 0) {
                if (result.isPresent() && result.get() == restartButton) {
                    SokobanBoardImages.restart(currentStage);
                } else {
                    StartJavaFXGUIImages.exit();
                }
            } else {
                if (result.isPresent() && result.get() == restartButton) {
                    SokobanBoardText.restart(currentStage);
                } else {
                    StartJavaFXGUIText.exit();
                }
            }
        }
    }

    /**
     * Switches to the score panel based on the GUI type.
     *
     * @param guiType current GUI type
     * @param currentScore current game score
     */
    public static void scorePanelSwitch(int guiType, Score currentScore) {
        if (guiType == 0) {
            SokobanMenuBar.setScorePanelInfoGuiImages(currentScore);
            StartJavaFXGUIImages.scoresPanel.setVisible(true);
        } else {
            SokobanMenuBar.setScorePanelInfoGuiText(currentScore);
            StartJavaFXGUIText.scoresPanel.setVisible(true);
        }
    }

    /**
     * Sets the information in the score panel for the GUI with images.
     *
     * @param currentScore
     */
    public static void setScorePanelInfoGuiImages(Score currentScore) {
        panelStructure(0);

        StartJavaFXGUIImages.scoreLabel.setText("Score: " + currentScore.getScore());
        StartJavaFXGUIImages.playerNameLabel.setText("Player name: " + currentScore.getPlayerName());
        StartJavaFXGUIImages.levelNameLabel.setText("Level name: " + currentScore.getLevelName());
        StartJavaFXGUIImages.scoreLabel.setText("Game score: " + currentScore.getScore());

        Score[] bestLevelScores = SokobanMenuBar.bestLevelScores("scores/scores.txt");

        if (bestLevelScores[0].getLevelName().equals(currentScore.getLevelName()) &&
                bestLevelScores[0].getScore() == currentScore.getScore() &&
                bestLevelScores[0].getPlayerName().equals(currentScore.getPlayerName())) {
            StartJavaFXGUIImages.scoreIsTopLabel.setVisible(true);
        } else {
            StartJavaFXGUIImages.scoreIsTopLabel.setVisible(false);
        }

        String space = "                |                ";
        if (bestLevelScores.length >= 3) {
            String bestScores = String.format("Best level scores: \n\n Player:  %s%s Score: %s\n\n Player:  %s%s Score:  %s\n\n Player:  %s%s Score:  %s",
                    bestLevelScores[0].getPlayerName(), space, bestLevelScores[0].getScore(),
                    bestLevelScores[1].getPlayerName(), space, bestLevelScores[1].getScore(),
                    bestLevelScores[2].getPlayerName(), space, bestLevelScores[2].getScore());
            StartJavaFXGUIImages.bestLevelScoresLabel.setText(bestScores);
        }
    }

    /**
     * Sets the structure and styling of the scores panel in the GUI with images.
     */
    public static void panelStructure(int guiType) {
        if(guiType == 0) {
            StartJavaFXGUIImages.scoresPanelTitleLabel.setPadding(new Insets(25, 5, 0, 130));
            StartJavaFXGUIImages.levelNameLabel.setPadding(new Insets(40, 5, 0, 35));
            StartJavaFXGUIImages.playerNameLabel.setPadding(new Insets(40, 5, 0, 35));
            StartJavaFXGUIImages.scoreLabel.setPadding(new Insets(40, 0, 0, 35));
            StartJavaFXGUIImages.scoreIsTopLabel.setPadding(new Insets(25, 0, 0, 35));
            StartJavaFXGUIImages.bestLevelScoresLabel.setPadding(new Insets(40, 5, 0, 35));

            StartJavaFXGUIImages.scoresPanelTitleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
            StartJavaFXGUIImages.scoreIsTopLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 25; -fx-text-fill: red;");
        } else {
            StartJavaFXGUIText.scoresPanelTitleLabel.setPadding(new Insets(25, 5, 0, 130));
            StartJavaFXGUIText.levelNameLabel.setPadding(new Insets(40, 5, 0, 35));
            StartJavaFXGUIText.playerNameLabel.setPadding(new Insets(40, 5, 0, 35));
            StartJavaFXGUIText.scoreLabel.setPadding(new Insets(40, 0, 0, 35));
            StartJavaFXGUIText.scoreIsTopLabel.setPadding(new Insets(25, 0, 0, 35));
            StartJavaFXGUIText.bestLevelScoresLabel.setPadding(new Insets(40, 5, 0, 35));

            StartJavaFXGUIText.scoresPanelTitleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18;");
            StartJavaFXGUIText.scoreIsTopLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 25; -fx-text-fill: red;");
        }

    }

    /**
     * Sets the score panel information in the GUI with text.
     *
     * @param currentScore
     */
    public static void setScorePanelInfoGuiText(Score currentScore) {
        panelStructure(1);

        StartJavaFXGUIText.scoreLabel.setText("Score: " + currentScore.getScore());
        StartJavaFXGUIText.playerNameLabel.setText("Player name: " + currentScore.getPlayerName());
        StartJavaFXGUIText.levelNameLabel.setText("Level name: " + currentScore.getLevelName());
        StartJavaFXGUIText.scoreLabel.setText("Game score: " + currentScore.getScore());

        Score[] bestLevelScores = SokobanMenuBar.bestLevelScores("scores/scores.txt");

        if (bestLevelScores[0].getLevelName().equals(currentScore.getLevelName()) &&
                bestLevelScores[0].getScore() == currentScore.getScore() &&
                bestLevelScores[0].getPlayerName().equals(currentScore.getPlayerName())) {
            StartJavaFXGUIText.scoreIsTopLabel.setVisible(true);
        } else {
            StartJavaFXGUIText.scoreIsTopLabel.setVisible(false);
        }

        String space = "                |                ";
        if (bestLevelScores.length >= 3) {
            String bestScores = String.format("Best level scores: \n\n Player:  %s%s Score: %s\n\n Player:  %s%s Score:  %s\n\n Player:  %s%s Score:  %s",
                    bestLevelScores[0].getPlayerName(), space, bestLevelScores[0].getScore(),
                    bestLevelScores[1].getPlayerName(), space, bestLevelScores[1].getScore(),
                    bestLevelScores[2].getPlayerName(), space, bestLevelScores[2].getScore());
            StartJavaFXGUIText.bestLevelScoresLabel.setText(bestScores);
        }
    }

    /**
     * Sets up the scores panel in the GUI
     *
     * @param scoresPanel
     * @param scoreLabel
     * @param scoreIsTopLabel
     * @param scoresPanelTitleLabel
     * @param levelNameLabel
     * @param playerNameLabel
     * @param bestLevelScoresLabel
     */
    public static void setScoresPanel(HBox scoresPanel, Label scoreLabel, Label scoreIsTopLabel, Label scoresPanelTitleLabel, Label levelNameLabel, Label playerNameLabel, Label bestLevelScoresLabel) {
        // Ask player name
        if (SokobanMenuBar.playerName.equals(""))
            SokobanMenuBar.playerName = SokobanFiles.askUserForName();

        // Scores panel
        scoresPanel.setPrefWidth(300);
        HBox scoreTopHBox = new HBox(scoreLabel, scoreIsTopLabel);
        VBox scoresPanelChild = new VBox(scoresPanelTitleLabel, levelNameLabel, playerNameLabel, scoreTopHBox, bestLevelScoresLabel);
        scoresPanel.getChildren().add(scoresPanelChild);
        scoresPanel.setVisible(false);
    }

    /**
     * Performs the undo operation in the game.
     *
     * @param primaryStage
     * @param guiType
     */
    public static void undoGame(Stage primaryStage, int guiType) {
        if (score != 0) {
            currentHistoryIndex = score - 1;

            if (currentHistoryIndex < allBoxesPositions.size() && currentHistoryIndex < allKepperPositions.size()) {
                SokobanFiles.boxesPositions = Set.of(
                        new Position(allBoxesPositions.get(currentHistoryIndex).get(0).line(), allBoxesPositions.get(currentHistoryIndex).get(0).col()),
                        new Position(allBoxesPositions.get(currentHistoryIndex).get(1).line(), allBoxesPositions.get(currentHistoryIndex).get(1).col())
                );

                SokobanFiles.keeperPosition = new Position(allKepperPositions.get(currentHistoryIndex).line(), allKepperPositions.get(currentHistoryIndex).col());

                if (guiType == 0) SokobanBoardImages.undoImages(primaryStage);
                else SokobanBoardText.undoText(primaryStage);

                System.out.println("allBoxesPositions:");
                System.out.println(allBoxesPositions);
                System.out.println("Last boxes pos:");
                System.out.println(allBoxesPositions.get(currentHistoryIndex));
            }
        }
    }
}



