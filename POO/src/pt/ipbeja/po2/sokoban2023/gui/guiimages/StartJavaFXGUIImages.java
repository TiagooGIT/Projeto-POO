package pt.ipbeja.po2.sokoban2023.gui.guiimages;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pt.ipbeja.po2.sokoban2023.gui.SokobanFiles;
import pt.ipbeja.po2.sokoban2023.gui.SokobanMenuBar;
import pt.ipbeja.po2.sokoban2023.model.BoardModel;
import pt.ipbeja.po2.sokoban2023.model.Level;
import pt.ipbeja.po2.sokoban2023.model.Position;
import pt.ipbeja.po2.sokoban2023.model.SokobanGameModel;

import java.util.Scanner;
import java.util.Set;


/**
 * Start a game with a hardcoded board and images
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public class StartJavaFXGUIImages extends Application {
    public static HBox scoresPanel = new HBox();
    public static Label scoresPanelTitleLabel = new Label("Score");
    public static Label levelNameLabel = new Label();
    public static Label playerNameLabel = new Label();
    public static Label scoreLabel = new Label();
    public static Label scoreIsTopLabel = new Label("TOP!");
    public static Label bestLevelScoresLabel = new Label();
    public static SokobanGameModel sokoban;

    /**
     * Starts the Sokoban game by setting up the GUI and initializing the game model.
     * @param primaryStage
     */
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(e -> exit());
        sokoban = new SokobanGameModel(new Level(SokobanFiles.keeperPosition, SokobanFiles.boxesPositions, SokobanFiles.boardContent));

        SokobanBoardImages sokobanBoardImages =
                new SokobanBoardImages(sokoban,
                        "keeper.png",
                        "box.png",
                        "boxend.png",
                        "wall.png",
                        "free.png",
                        "end.png"
                );

        SokobanMenuBar.setScoresPanel(scoresPanel, scoreLabel, scoreIsTopLabel, scoresPanelTitleLabel, levelNameLabel, playerNameLabel, bestLevelScoresLabel);

        VBox vBox = new VBox(SokobanMenuBar.createMenuBar(primaryStage, 0, SokobanFiles.keeperPosition), sokobanBoardImages);
        HBox hBox = new HBox(vBox, scoresPanel);
        primaryStage.setScene(new Scene(hBox));

        sokoban.registerView(sokobanBoardImages);
        sokobanBoardImages.requestFocus(); // to remove focus from first button
        primaryStage.show();

        SokobanMenuBar.chronometer.setPadding(new Insets(5, 5, 5, (int) primaryStage.getWidth() - 470));
    }

    /**
     * @param args not used
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * action to exit the program
     */
    public static void exit() {
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
    }
}


