package mcts;

import utils.CompletableThread;
import utils.Game;

/**
 * A Thread managing all the MCTS logic for easy use in multithreading environments.
 */
public class MctsThread extends CompletableThread<Mcts> {

    public final Mcts mcts;

    public MctsThread(Game rootGame) {
        this.mcts = new Mcts(rootGame);
    }

    public MctsThread(TreeNode rootNode, Game rootGame) {
        this.mcts = new Mcts(rootNode, rootGame);
    }

    @Override
    protected Mcts doWork() {
        while (!isInterrupted()) {
            mcts.startIteration();
        }

        return mcts;
    }
}
