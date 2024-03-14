package pt.ipbeja.po2.sokoban2023.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SokobanGameModelTest contains several test cases for the SokobanGameModel class
 *
 * @author Tiago Pacheco e Gabriel Gomes
 * Nº: 20296 and 19932
 * @version 2023/06/14
 * Based on https://en.wikipedia.org/wiki/Sokoban
 */
class SokobanGameModelTest {

    /**
     * Tests the movement of the keeper to the right.
     */
    @Test
    void testKeeperMovementToTheRight() {
        Level level = new Level();
        SokobanGameModel sokoban = new SokobanGameModel(level);
        // first a view that does not check
        sokoban.registerView(messageToUI -> {
        });

        Position initialPosition = sokoban.keeper().getPosition();
        System.out.println("Initial position of the keeper: " + initialPosition);

        assertEquals(new Position(3, 5), initialPosition);

        // move right
        sokoban.moveKeeper(Direction.RIGHT);
        Position newPosition = sokoban.keeper().getPosition();
        System.out.println("New Position of the Keeper: " + newPosition);

        // test new position
        assertEquals(new Position(3, 6), newPosition);
    }

    /**
     * Tests that the keeper does not move to an invalid position.
     */
    @Test
    void testKeeperDoesNotMoveToInvalidPosition() {
        Level level = new Level();
        SokobanGameModel sokoban = new SokobanGameModel(level);

        // First a view that does not check
        sokoban.registerView(messageToUI -> {
        });

        // Check the initial position of the keeper
        Position initialPosition = sokoban.keeper().getPosition();
        System.out.println("Initial position of the keeper: " + initialPosition);

        // Move to Invalid Position (Level)
        sokoban.moveKeeper(Direction.UP);

        // Verifies if the keeper remained in the same position
        Position newPosition = sokoban.keeper().getPosition();
        System.out.println("New Position of the Keeper: " + newPosition);

        assertEquals(initialPosition, newPosition);
    }

    /**
     * Tests the returned positions on moving to an empty space.
     */
    @Test
    void testReturnedPositionsOnMoveToEmpty() {
        Level level = new Level();
        SokobanGameModel sokoban = new SokobanGameModel(level);

        sokoban.registerView(messageToUI -> {
            List<Position> expectedPositons = List.of(new Position(3, 6), new Position(3, 5));
            assertEquals(expectedPositons, messageToUI.positions());
        });
        sokoban.moveKeeper(Direction.RIGHT);
    }

    /**
     * Tests the returned positions on moving a box.
     */
    @Test
    void testReturnedPositonsOnMoveBox() {
        Level level = new Level();
        SokobanGameModel sokoban = new SokobanGameModel(level);

        // first a view that does not check
        sokoban.registerView(messageToUI -> {
        });

        //move
        sokoban.moveKeeper(Direction.LEFT);
        sokoban.moveKeeper(Direction.LEFT);
        sokoban.moveKeeper(Direction.LEFT);

        // now a view that checks
        sokoban.registerView(messageToUI -> {
            List<Position> expectedPositons =
                    List.of(new Position(3, 3), new Position(4, 3), new Position(2, 3));
            assertEquals(expectedPositons, messageToUI.positions());
        });
        sokoban.moveKeeper(Direction.LEFT);
    }

    /**
     * Tests the winning game end scenario
     */
    @Test
    //test 3
    void testWinningGameEnd() {
        Level level = new Level();
        SokobanGameModel sokoban = new SokobanGameModel(level);

        // first a view that does not check
        sokoban.registerView(messageToUI -> {
        });

        //move
        Direction[] directions = new Direction[]{
                Direction.DOWN, Direction.LEFT, Direction.LEFT, Direction.UP, Direction.DOWN, Direction.LEFT, Direction.LEFT,
                Direction.UP, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.UP, Direction.UP, Direction.LEFT,
                Direction.DOWN, Direction.RIGHT, Direction.DOWN, Direction.DOWN, Direction.RIGHT, Direction.RIGHT, Direction.UP,
                Direction.LEFT};

        for (Direction d : directions) {
            sokoban.moveKeeper(d);
        }

        // now a view that checks
        sokoban.registerView(messageToUI -> {
            List<Position> expectedPositons =
                    List.of(new Position(4, 5), new Position(4, 4), new Position(4, 3), new Position(3, 3),
                            new Position(4, 3), new Position(4, 2), new Position(4, 1), new Position(3, 1),
                            new Position(3, 2), new Position(3, 3), new Position(3, 4), new Position(2, 4),
                            new Position(1, 4), new Position(1, 3), new Position(2, 3), new Position(2, 4),
                            new Position(3, 4), new Position(4, 4), new Position(4, 5), new Position(4, 6),
                            new Position(3, 6), new Position(3, 5));
            assertEquals(expectedPositons, messageToUI.positions());
        });
    }
}

