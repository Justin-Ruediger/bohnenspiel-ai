package mcts;

import utils.Game;
import utils.GameUtils;
import utils.Player;

public class MctsPlayer extends Player {
    public MctsPlayer(String gameId) {
        super(gameId);
    }

    @Override
    public void start() {
        System.out.println("Waiting for game to start...");

        int state = waitForTurnAndGetState();

        final Game currentGame = GameUtils.getStartingState();
        MctsThread mctsThread = null;

        System.out.println(currentGame);

        while (state != -2) {
            // Apply other players moves
            if (state >= 1 && state <= 12) {
                System.out.println("Enemy chose " + state);
                currentGame.move(state - 1);
                System.out.println(currentGame + "\n");
            }

            if (mctsThread == null) {
                mctsThread = new MctsThread(currentGame.clone());
                mctsThread.start();
            } else {
                Mcts result = mctsThread.interuptAndGetResult();
                // create a new MCTS instance based of the enemys move.
                mctsThread = new MctsThread(result.getRoot().childNodes[state - (currentGame.isPlayerATurn() ? 7 : 1)], currentGame.clone());
                mctsThread.start();
            }


            try {
                Thread.sleep(1700);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Mcts result = mctsThread.interuptAndGetResult();
            int bestMove = result.getBestMove() + (currentGame.isPlayerATurn() ? 0 : 6);

            final TreeNode newRootNode = result.getRoot().childNodes[result.getBestMove()];
            System.out.printf("Move %d : Certainty Values (%d : %d) \n", bestMove, newRootNode.winsA, newRootNode.winsB);
            move(bestMove);

            if(!currentGame.move(bestMove)) {
                System.err.println("Invalid Move");
            }
            System.out.println(currentGame + "\n");

            // Resume calculations based of chosen move:
            mctsThread = new MctsThread(result.getRoot().childNodes[result.getBestMove()], currentGame.clone());
            mctsThread.start();

            try {
                Thread.sleep(1000);
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
