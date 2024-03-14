package pt.ipbeja.po2.sokoban2023.gui.guitext;


import javafx.application.Platform;
import javafx.collections.ObservableList;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
//import nifty.sokoban.model.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pt.ipbeja.po2.sokoban2023.gui.SokobanFiles;
import pt.ipbeja.po2.sokoban2023.gui.SokobanMenuBar;
import pt.ipbeja.po2.sokoban2023.gui.guiimages.StartJavaFXGUIImages;
import pt.ipbeja.po2.sokoban2023.model.*;

import java.awt.Toolkit;
import java.util.Map;
import java.util.Optional;


/**
 * Game interface. Just a GridPane of buttons. No images. No menu.
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public class SokobanBoardText extends GridPane implements SokobanView {
    private final SokobanGameModel sokoban;
    private static final int SQUARE_SIZE = 80;

    /**
     * Create a sokoban board with labels and handle keystrokes
     * @param sokoban
     */
    public SokobanBoardText(SokobanGameModel sokoban) {
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
                SokobanBoardText.this.couldNotMove();
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
                String textForButton = this.sokoban.textForPosition(new Position(line, col));
                Label label = new Label(textForButton);
                label.setMinWidth(SQUARE_SIZE);
                label.setMinHeight(SQUARE_SIZE);
                this.add(label, col, line); // add label to GridPane
            }
        }
        this.requestFocus();
    }

    /**
     * Signal that keeper could not move
     */
    private void couldNotMove() {
        Toolkit.getDefaultToolkit().beep(); // did not move
    }

    /**
     * Can be optimized using an additional matrix with all the buttons
     *
     * @param line line of label in board
     * @param col  column of label in board
     * @return the label at line, col
     */
    public Label getLabel(int line, int col) {
        ObservableList<Node> children = this.getChildren();
        for (Node node : children) {
            if (GridPane.getRowIndex(node) == line && GridPane.getColumnIndex(node) == col) {
                assert (node.getClass() == Label.class);
                return (Label) node;
            }
        }
        assert (false); // must not happen
        return null;
    }

    /**
     * action to restart the program
     *
     * @param currentStage
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
                StartJavaFXGUIText newApplication = new StartJavaFXGUIText();
                newApplication.start(currentStage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Reverts the GUI to the text-based version by restarting the JavaFX Text application
     * @param currentStage
     */
    public static void undoText(Stage currentStage) {
        currentStage.close(); // Close the current stage
        Platform.runLater(() -> {
            try {
                StartJavaFXGUIText newApplication = new StartJavaFXGUIText();
                newApplication.start(currentStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Simply updates the text for the buttons in the received positions
     *
     * @param messageToUI the sokoban model
     */
    @Override
    public void update(MessageToUI messageToUI) {
        for (Position p : messageToUI.positions()) {
            String s = this.sokoban.textForPosition(p);
            this.getLabel(p.line(), p.col()).setText(s);
        }
        Stage currentStage = (Stage) getScene().getWindow();
        SokobanMenuBar.endGameAlert(this.sokoban, currentStage, 1);
    }
}
