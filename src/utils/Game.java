package utils;

import java.util.Arrays;

public final class Game {
    private byte pointsA;
    private final byte[] board;
    private byte pointsB;
    private boolean isPlayerATurn;

    private Winner winner = Winner.NOT_DECIDED;

    public Game(byte[] board, byte pointsA, byte pointsB, boolean isPlayerATurn) {
        this.board = board;
        this.pointsA = pointsA;
        this.pointsB = pointsB;
        this.isPlayerATurn = isPlayerATurn;
    }


    /**
     * Updates this board accourding to the provided move.
     *
     * @param localPosition  as number from 0 to 5
     * @return if move was successfully executed
     */
    public boolean moveNormalized(int localPosition) {

        return move(localPosition + (isPlayerATurn ? 0 : 6));
    }

    /**
     * Updates this board accourding to the provided move.
     *
     * @param position  as number from 0 to 11
     * @return if move was successfully executed
     */
    public boolean move(final int position) {
        if (board[position] == 0) {
            return false;
        }

        final byte stepsToMove = board[position];
        board[position] = 0;
        // Apply move to board
        for (int i = 1; i <= stepsToMove; i++) {
            final int index = (position + i) % 12;
            board[index] += 1;
        }

        byte gainedScore = 0;
        // check for scores
        int i = (position + stepsToMove) % 12;
        byte stones = board[i];
        while (stones == 2 || stones == 4 || stones == 6) {
            gainedScore += stones;
            board[i] = 0;

            i = i == 0 ? 11 : i - 1;
            stones = board[i];
        }

        if (isPlayerATurn) {
            pointsA += gainedScore;
            if (pointsA > 36 || board[6] == 0 && board[7] == 0 && board[8] == 0 && board[9] == 0 && board[10] == 0 && board[11] == 0) {
                winner = Winner.PLAYER_A;
            }
        } else {
            pointsB += gainedScore;
            if (pointsB > 36 ||board[0] == 0 && board[1] == 0 && board[2] == 0 && board[3] == 0 && board[4] == 0 && board[5] == 0) {
                winner = Winner.PLAYER_B;
            }
        }
        isPlayerATurn = !isPlayerATurn;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Arrays.equals(board, game.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }

    public byte[] getBoard() {
        return board;
    }

    public byte getPointsA() {
        return pointsA;
    }

    public byte getPointsB() {
        return pointsB;
    }

    public boolean isPlayerATurn() {
        return isPlayerATurn;
    }

    @Override
    public String toString() {
        return "Game{" +
                "board=" + Arrays.toString(board) +
                ", pointsA=" + pointsA +
                ", pointsB=" + pointsB +
                ", isPlayerATurn=" + isPlayerATurn +
                '}';
    }

    public Winner getWinner() {
        return winner;
    }


    public enum Winner {
        PLAYER_A,
        PLAYER_B,
        NOT_DECIDED
    }

    @Override
    public Game clone() {
        return new Game(board.clone(), pointsA, pointsB, isPlayerATurn);
    }

    public static Game getStartingState() {
        return new Game(new byte[]
                {
                        6, 6, 6, 6, 6, 6, // PLAYER A
                        6, 6, 6, 6, 6, 6  // PLAYER B
                }, (byte) 0, (byte) 0, true);
    }
}
