package mcts;

import game_utils.Game;

public class Simulation {
    public final Game game;


    public Simulation(Game game) {
        this.game = game;
    }

    public void startSimulation() {
        move: while (game.getWinner() == Game.Winner.NOT_DECIDED) {
            final int move = (int) (Math.random() * 6);
            if (game.moveNormalized(move)) {
                continue; // move successful
            }

            // if random move wasn't possible, take the next possible move
            for(int i = 0; i < 6; i++) {
                if(game.moveNormalized(i)) {
                    break;
                }
            }

        }
    }

    public Game.Winner getSimulationResult() {
        return game.getWinner();
    }

}
