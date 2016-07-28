package c4.game;

import c4.game.exception.BoardException;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class BoardTest {
    @Test
    public void aPlayCanBeMadeIntoAColumn() throws BoardException {
        Board board = new Board();
        board.play(1, 0);

        assertEquals(1, board.getAt(Board.ROW_COUNT - 1, 0));
    }

    @Test
    public void playsIntoTheSameColumnAreStacked() throws BoardException {
        Board board = new Board();
        board.play(1, 0);
        board.play(1, 0);

        assertEquals(1, board.getAt(Board.ROW_COUNT - 2, 0));
    }

    @Test(expected = BoardException.class)
    public void playsIntoAFullColumnThrowAnException() throws BoardException {
        Board board = new Board();

        for (int i = 0; i <= Board.ROW_COUNT; i++) {
            board.play(1, 0);
        }
    }

    @Test(expected = BoardException.class)
    public void playsIntoColumnsThatDontExistThrowAnException() throws BoardException {
        Board board = new Board();

        board.play(1, Board.COLUMN_COUNT + 1);
    }

    @Test
    public void emptyBoardsAreNotWinners() {
        Board board = new Board();
        assertFalse(board.hasWinner());
    }

    @Test
    public void detectsAHorizontalWinForPlayerOne() throws BoardException {
        Board board = new Board();
        for (int i = 0; i < 4; i++) {
            board.play(1, i);
        }

        assertTrue(board.hasWinner());
    }

    @Test
    public void detectsAHorizontalWinForPlayerTwo() throws BoardException {
        int[][] boardState = new int[][] {
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{1, 1, 1, 1, 0, 0, 0}
        };
        Board board = new Board(boardState);

        assertTrue(board.hasWinner());
    }

    @Test
    public void detectsAVerticalWinForPlayerOne() throws BoardException {
        int[][] boardState = new int[][] {
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 1, 0},
                new int[]{0, 0, 0, 0, 0, 1, 0},
                new int[]{0, 0, 0, 0, 0, 1, 0},
                new int[]{0, 0, 0, 0, 0, 1, 0}
        };
        Board board = new Board(boardState);

        assertTrue(board.hasWinner());
    }

    @Test
    public void detectsARightDiagonalWinForPlayerOne() throws BoardException {
        int[][] boardState = new int[][] {
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 1, 0, 0, 0},
                new int[]{0, 0, 1, 2, 0, 0, 0},
                new int[]{0, 1, 2, 2, 0, 0, 0},
                new int[]{1, 2, 2, 2, 0, 0, 0}
        };
        Board board = new Board(boardState);

        assertTrue(board.hasWinner());
    }

    @Test
    public void detectsALeftDiagonalWinForPlayerOne() throws BoardException {
        int[][] boardState = new int[][] {
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0, 0, 0},
                new int[]{1, 0, 0, 0, 0, 0, 0},
                new int[]{2, 1, 0, 0, 0, 0, 0},
                new int[]{2, 2, 1, 0, 0, 0, 0},
                new int[]{2, 2, 2, 1, 0, 0, 0}
        };
        Board board = new Board(boardState);

        assertTrue(board.hasWinner());
    }
}
