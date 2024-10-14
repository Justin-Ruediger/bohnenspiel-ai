package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A Simple way to play the game via the console
 */
public class ManualPlayer extends Player {
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public ManualPlayer(final String gameId) {
        super(gameId);
    }

    @Override
    public void start() {
        Game g = Game.getStartingState();
        int state = waitForTurnAndGetState();
        while (state != -2) {
            state = waitForTurnAndGetState();

            g.moveNormalized(state - 1);

            System.out.println("Game:");
            System.out.println(g);
            System.out.println("What is your move");
            try {
                final int move = Integer.parseInt(input.readLine());
                g.moveNormalized(move);
                move(move);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
