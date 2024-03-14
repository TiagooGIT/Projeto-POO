package pt.ipbeja.po2.sokoban2023.model;

import java.util.Comparator;

/**
 * Comparator implementation for sorting Score objects based on their scores in ascending order.
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public class SortByScore implements Comparator<Score> {
    @Override
    public int compare(Score a, Score b) {
        return a.score - b.score;
    }
}
