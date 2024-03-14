package pt.ipbeja.po2.sokoban2023.model;

import java.util.Objects;

/**
 * A Box is just a mobile element
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
final class Box extends MobileElement {
    /**
     * Creates a Box at pos
     *
     * @param pos initial position for the box
     */
    public Box(Position pos) {
        super(pos);
    }

    /**
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(getPosition());
    }

    /**
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        Box box = (Box) obj;
        if (this == obj) {
            return true;
        }
        return getPosition().equals(box.getPosition());

    }
}
