package c4.game;

import c4.game.exception.BoardException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * A Connect Four game board. Its default size is 6 rows and 7 columns, but
 * other board sizes may be supported in the future. This is a package-local
 * class &mdash; callers should use the Game class instead.
 */
class Board {
    public static final int COLUMN_COUNT = 7;

    public static final int ROW_COUNT = 6;

    @JsonProperty
    private final int[][] board;

    private final int columnCount;

    private final int rowCount;

    Board() {
        this.board = new int[ROW_COUNT][COLUMN_COUNT];
        columnCount = COLUMN_COUNT;
        rowCount = ROW_COUNT;
    }

    Board(int[][] board) {
        this.board = board;
        columnCount = board[0].length;
        rowCount = board.length;
    }

    Board(int rowCount, int columnCount) {
        board = new int[rowCount][columnCount];
        this.columnCount = columnCount;
        this.rowCount = rowCount;
    }

    public static Board createRandomSizedBoard() {
        int columnCount = (int) Math.floor(Math.random() * COLUMN_COUNT) + COLUMN_COUNT;
        int rowCount = (int) Math.floor(Math.random() * ROW_COUNT) + ROW_COUNT;

        return new Board(rowCount, columnCount);
    }

    /**
     * Get the played value at the given cell.
     *
     * @param row The zero-indexed row position.
     * @param column The zero-indexed column position.
     * @return The player number (or zero if the cell is open).
     */
    public int getAt(int row, int column) {
        return board[row][column];
    }

    /**
     * Get the winning player's number.
     * @return Player number or -1 if no winner exists.
     */
    @JsonIgnore
    public int getWinner() {
        int playerNumbers[] = new int[]{1, 2};

        for (int player : playerNumbers) {
            if (checkRow(player, Direction.HORIZONTAL) ||
                    checkRow(player, Direction.VERTICAL) ||
                    checkRow(player, Direction.DIAGONAL_LEFT) ||
                    checkRow(player, Direction.DIAGONAL_RIGHT)) {
                return player;
            }
        }

        return -1;
    }

    /**
     * Determine if a winner exists on this board.
     *
     * @return true if there is a winner, false if not.
     */
    public boolean hasWinner() {
        return getWinner() > 0;
    }

    public void play(int playerNumber, int columnNumber) throws BoardException {
        try {
            for (int row = rowCount - 1; row >= 0; row--) {
                if (board[row][columnNumber] == 0) {
                    board[row][columnNumber] = playerNumber;
                    return;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BoardException("invalid column");
        }

        throw new BoardException("column full");
    }

    /**
     * Print the board state to STDOUT
     */
    public void print() {
        print(System.out);
    }

    public void print(OutputStream out) {
        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            for (int[] row : board) {
                for (int cell : row) {
                    writer.print(cell);
                }
                writer.println("");
            }
            writer.println("");
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
    }

    private boolean checkRow(int playerNumber, Direction direction) {
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                int yPosition = row;
                int xPosition = col;
                int foundCells = 0;

                while (true) {
                    if (yPosition < 0 || xPosition < 0) {
                        break;
                    }

                    if (yPosition >= rowCount || xPosition >= columnCount) {
                        break;
                    }

                    if (board[yPosition][xPosition] != playerNumber) {
                        break;
                    }

                    foundCells++;
                    xPosition = xPosition + direction.getX();
                    yPosition = yPosition + direction.getY();

                    if (foundCells >= 4) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private enum Direction {
        HORIZONTAL (1, 0),
        VERTICAL (0, 1),
        DIAGONAL_RIGHT (1, 1),
        DIAGONAL_LEFT (-1, 1);

        private final int xStep;

        private final int yStep;

        Direction(int xStep, int yStep) {
            this.xStep = xStep;
            this.yStep = yStep;
        }

        public int getX() {
            return xStep;
        }

        public int getY() {
            return yStep;
        }
    }
}