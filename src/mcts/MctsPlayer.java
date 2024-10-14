package mcts;

import utils.Benchmark;
import utils.Game;
import utils.Player;

import java.util.Arrays;

public class MctsPlayer extends Player {
    public MctsPlayer(String gameId) {
        super(gameId);
    }

    @Override
    public void start() {
        System.out.println("Waiting for game to start...");

        int state = waitForTurnAndGetState();

        final Game currentGame = Game.getStartingState();
        MctsThread mctsThread = null;

        System.out.println(currentGame);

        while (state != -2) {
            Benchmark benchmark = new Benchmark();
            benchmark.start();
            // Apply other players moves
            if (state >= 1 && state <= 12) {
                System.out.println("Enemy chose field" + state);
                currentGame.move(state - 1);
                System.out.println(currentGame + "\n");
            }

            if (mctsThread == null) {
                mctsThread = new MctsParallelDividerThread(currentGame.clone());
                mctsThread.start();
            } else {
                Mcts result = mctsThread.interuptAndGetResult();
                // create a new MCTS instance based of the enemys move.
                mctsThread = new MctsParallelDividerThread(result.getRoot().childNodes[state - (currentGame.isPlayerATurn() ? 7 : 1)], currentGame.clone());
                mctsThread.start();
            }


            try {
                Thread.sleep(1700);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Mcts result = mctsThread.interuptAndGetResult();
            int normalizedBestMove = result.getBestMove();
            int bestMove = normalizedBestMove + (currentGame.isPlayerATurn() ? 0 : 6);

            final TreeNode newRootNode = result.getRoot().childNodes[normalizedBestMove];
            System.out.printf("Chosen Move %d : Simulation Values (%d : %d) \n", bestMove, newRootNode.winsA, newRootNode.winsB);
            move(bestMove);

            benchmark.stop();
            System.out.println("Own move took " + benchmark.getElapsedTimeInMillis() + "ms to compute!");

            if(!currentGame.move(bestMove)) {
                System.err.println("Invalid Move");
            }
            System.out.println(currentGame + "\n");

            // Resume calculations based of chosen move:
            mctsThread = new MctsParallelDividerThread(result.getRoot().childNodes[normalizedBestMove], currentGame.clone());
            mctsThread.start();

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Update State:
            state = waitForTurnAndGetState();
        }
        if (mctsThread != null) {
            mctsThread.interrupt();
        }
        System.out.println("Game Ended");
        System.out.println(getStateString());
    }
}
