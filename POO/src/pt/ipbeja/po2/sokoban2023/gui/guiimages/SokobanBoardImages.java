package pt.ipbeja.po2.sokoban2023.gui.guiimages;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pt.ipbeja.po2.sokoban2023.gui.SokobanMenuBar;
import pt.ipbeja.po2.sokoban2023.images.ImageType;
//import nifty.sokoban.model.*;
import pt.ipbeja.po2.sokoban2023.model.*;

import java.awt.Toolkit;
import java.util.Map;

/**
 * Game interface. Just a GridPane of images. No menu.
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * * Nº: 20296 and 19932
 * * @version 2023/06/14
 * * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public class SokobanBoardImages extends GridPane implements SokobanView {
    private final SokobanGameModel sokoban;
    final Map<ImageType, Image> imageTypeToImage;
    private static final int SQUARE_SIZE = 80;

    /**
     * Create a sokoban board with labels and handle keystrokes
     */
    public SokobanBoardImages(SokobanGameModel sokoban,
                              String keeperImageFilename,
                              String boxImageFilename,
                              String boxEndImageFilename,
                              String wallImageFilename,
                              String freeImageFilename,
                              String endImageFilename) {

        this.imageTypeToImage =
                Map.of(ImageType.KEEPER, new Image("images/" + keeperImageFilename),
                        ImageType.BOX, new Image("images/" + boxImageFilename),
                        ImageType.BOXEND, new Image("images/" + boxEndImageFilename),
                        ImageType.WALL, new Image("images/" + wallImageFilename),
                        ImageType.END, new Image("images/" + endImageFilename),
                        ImageType.FREE, new Image("images/" + freeImageFilename));

        this.sokoban = sokoban;
        this.buildGUI();

        this.setOnKeyPressed(event ->
        {
            final Map<KeyCode, Direction> keyToDir = Map.of(
                    KeyCode.UP, Direction.UP,
                    KeyCode.DOWN, Direction.DOWN,
                    KeyCode.LEFT, Direction.LEFT,
                    KeyCode.RIGHT, Direction.RIGHT);
            Direction direction = keyToDir.get(event.getCode());
            if (direction != null && !sokoban.moveKeeper(direction)) {
                SokobanBoardImages.this.couldNotMove();
            } else {
                SokobanMenuBar.setCronometerAndCoordinates();
            }
        });

    }

    /**
     * Build the interface
     */
    private void buildGUI() {
        assert (this.sokoban != null);

        // create one label for each position
        for (int line = 0; line < this.sokoban.getNLines(); line++) {
            for (int col = 0; col < this.sokoban.getNCols(); col++) {
                Label label = new Label();
                label.setMinWidth(SQUARE_SIZE);
                label.setMinHeight(SQUARE_SIZE);
                ImageType imgType = this.sokoban.imageForPosition(new Position(line, col));
                label.setGraphic(this.createImageView(this.imageTypeToImage.get(imgType)));
                this.add(label, col, line); // add label to GridPane
            }
        }
        this.requestFocus();
    }

    /**
     * Create image view
     * @param image
     * @return
     */
    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(SQUARE_SIZE);
        imageView.setFitHeight(SQUARE_SIZE);
        return imageView;
    }

    /**
     * Signal that keeper could not move
     */
    private void couldNotMove() {
        Toolkit.getDefaultToolkit().beep(); // did not move
    }

    /**
     * action to restart the program
     */
    public static void restart(Stage currentStage) {
        currentStage.close(); // Close the current stage
        Platform.runLater(() -> {
            try {
                if (SokobanMenuBar.timer != null) {
                    SokobanMenuBar.timer.cancel();
                }
                SokobanMenuBar.chronometer.setText("00:00");
                SokobanMenuBar.started = false;
                SokobanMenuBar.elapsedTime = 0;
                StartJavaFXGUIImages newApplication = new StartJavaFXGUIImages();
                newApplication.start(currentStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Reverts the images in the GUI to the previous state by restarting the JavaFX application with images.
     * @param currentStage
     */
    public static void undoImages(Stage currentStage) {
        currentStage.close(); // Close the current stage
        Platform.runLater(() -> {
            try {
                StartJavaFXGUIImages newApplication = new StartJavaFXGUIImages();
                newApplication.start(currentStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the GUI with the given message and handles the end game alert.
     * @param messageToUI
     */
    @Override
    public void update(MessageToUI messageToUI) {
        buildGUI();
        Stage currentStage = (Stage) getScene().getWindow();
        SokobanMenuBar.endGameAlert(this.sokoban, currentStage, 0);
    }
}
