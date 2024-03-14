package pt.ipbeja.po2.sokoban2023.model;

import java.util.List;

/**
 * Message to be sent from the model so that the interface updates the positions in the list
 * @author anonymized
 * @version 2022/10/13
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
public record MessageToUI(List<Position> positions, String message) {}
