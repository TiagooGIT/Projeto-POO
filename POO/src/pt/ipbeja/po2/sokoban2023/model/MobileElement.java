package pt.ipbeja.po2.sokoban2023.model;


/**
 * The MobileElement class represents a mobile element in the Sokoban game.
 * It has a position attribute indicating its current position on the board.
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public abstract class MobileElement {
    private Position position;

    /**
     * @param position
     */
    public MobileElement(Position position) {
        this.position = position;
    }

    /**
     * @return
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position
     */
    public void setPosition(Position position) {
        this.position = position;
    }
}
