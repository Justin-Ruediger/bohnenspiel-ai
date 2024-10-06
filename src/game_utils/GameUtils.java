package game_utils;

import mcts.Mcts;

public class GameUtils {
    public static Game getStartingState() {
        return new Game(new byte[]
                {
                        6, 6, 6, 6, 6, 6, // PLAYER A
                        6, 6, 6, 6, 6, 6  // PLAYER B
                }, (byte) 0, (byte) 0, true);
    }

    public static void main(String[] args) {
        Game g = getStartingState();
        g.move(0);
        g.move(3);
        g.move(2);
       // new Mcts(g).start();

    }

}
