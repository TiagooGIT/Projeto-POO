package pt.ipbeja.po2.sokoban2023.model;

/**
 * A Keeper is just a mobile element
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
final class Keeper extends MobileElement {

    /**
     * Creates the Keeper at pos
     *
     * @param pos initial position for the Keeper
     */
    public Keeper(Position pos) {
        super(pos);
    }

    public void moveTo(Position newPosition) {
        this.setPosition(newPosition);
    }

}
