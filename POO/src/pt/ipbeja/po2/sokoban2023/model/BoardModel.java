package pt.ipbeja.po2.sokoban2023.model;

import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Game board contents
 *
 * @author anonymized
 * @version 2022/10/13
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public class BoardModel {
    private List<List<PositionContent>> board;

    /**
     * @param boardContent
     */
    public BoardModel(String boardContent) {
        Map<Character, PositionContent> pc =
                Map.of('F', PositionContent.FREE,
                        'W', PositionContent.WALL,
                        'E', PositionContent.END);
        List<List<PositionContent>> boardPos = new ArrayList<>();
        boardPos.add(new ArrayList<>());
        for (int i = 0; i < boardContent.length(); i++) {
            char c = boardContent.charAt(i);
            if (c == '\n') boardPos.add(new ArrayList<>());
            else boardPos.get(boardPos.size() - 1).add(pc.get(c));
        }
        this.board = Collections.unmodifiableList(boardPos);
    }

    /**
     * @return
     */
    public int nLines() {
        return this.board.size();
    }

    /**
     * @return
     */
    public int nCols() {
        return this.board.get(0).size();
    }

    /**
     * @param pos
     * @return
     */
    public PositionContent getPosContent(Position pos) {
        return this.board.get(pos.line()).get(pos.col());
    }
}
