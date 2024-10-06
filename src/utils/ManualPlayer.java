package utils;

import main.Main;

import java.io.IOException;

public class ManualPlayer extends Player {
    public ManualPlayer(String gameId) {
        super(gameId);
    }

    @Override
    public void start() {
        Game g = GameUtils.getStartingState();
        int state = waitForTurnAndGetState();
        while (state != -2) {
            state = waitForTurnAndGetState();

            g.moveNormalized(state - 1);

            System.out.println("Game:");
            System.out.println(g);
            System.out.println("What is your move");
            try {
                final int move = Integer.parseInt(Main.input.readLine());
                g.moveNormalized(move);
                move(move);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
