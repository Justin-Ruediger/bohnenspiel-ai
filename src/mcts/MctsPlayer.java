package mcts;

import game_utils.Game;
import game_utils.GameUtils;
import game_utils.Player;
import main.Main;

import java.io.IOException;

public class MctsPlayer extends Player {
    public MctsPlayer(String gameId) {
        super(gameId);
    }

    @Override
    public void start() {
        Game g = GameUtils.getStartingState();
        int state = waitForTurnAndGetState();
        TreeNode newRoot = null;
        Thread mctsThread;
        while (state != -2) {
            Mcts mcts;

            if(newRoot == null) {
                mcts = new Mcts(g);
            } else {
                mcts = new Mcts(newRoot, g);
            }
            Mcts finalMcts = mcts;
            mctsThread = new Thread() {
                @Override
                public void run() {
                    while (!interrupted()) {
                        finalMcts.startIteration();
                    }
                }
            };

            state = waitForTurnAndGetState();

            if(state == -2) {
                break;
            }
            mctsThread.interrupt();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(state > 0) {
                g.move(state - 1);
            }

            if (state > 6 && state <= 12 && newRoot != null) {
                newRoot = newRoot.childNodes[state - 7];

            } else if (state > 0 && newRoot != null) {
                g.move(state - 1);
                newRoot = newRoot.childNodes[state - 1];
            }

            if(newRoot == null) {
                mcts = new Mcts(g);
            } else {
                mcts = new Mcts(newRoot, g);
            }
            Mcts finalMcts1 = mcts;
            mctsThread = new Thread() {
                @Override
                public void run() {
                    while (!interrupted()) {
                        finalMcts1.startIteration();
                    }
                }
            };

            mctsThread.start();
            try {
                Thread.sleep(3000);
                mctsThread.interrupt();
                move(mcts.getBestMove() + (g.isPlayerATurn() ? 0 : 6));

                g.moveNormalized(mcts.getBestMove());
                newRoot = mcts.getRoot().childNodes[mcts.getBestMove()];
                System.out.println(mcts.getBestMove());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
