package mcts;

import utils.CompletableThread;
import utils.Game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MctsParallelDividerThread extends MctsThread {

    public MctsParallelDividerThread(Game rootGame) {
        super(rootGame);
    }

    public MctsParallelDividerThread(TreeNode rootNode, Game rootGame) {
        super(rootNode, rootGame);
    }

    @Override
    protected synchronized Mcts doWork() {
        // I. Initialize and start Sub-Threads with possible moves
        TreeNode[] nodes = mcts.getRoot().childNodes;
        if (Arrays.stream(nodes).anyMatch(Objects::isNull)) {
            MctsThread singleThread = new MctsThread(mcts.getRoot(), mcts.rootGame);
            singleThread.start();

            try {
                wait();
            } catch (InterruptedException e) {
                return singleThread.interuptAndGetResult();
            }
            return singleThread.interuptAndGetResult();
        }


        Set<MctsThread> runningThreads = new HashSet<>();
        for (int i = 0; i < nodes.length; i++) {
            if (!nodes[i].isValidMove) {
                continue;
            }
            var localGame = mcts.rootGame.clone();
            localGame.moveNormalized(i);
            MctsThread thread = new MctsThread(nodes[i], localGame);
            thread.start();
            runningThreads.add(thread);
        }

        // II. Wait fot interupt to return value
        try {
            wait();
        } catch (InterruptedException e) {
            interrupt();
        }
        runningThreads.forEach(Thread::interrupt);

        runningThreads.stream().map(t -> t.completableFuture);

        CompletableFuture combined = CompletableFuture.allOf(runningThreads.stream().map(t -> t.completableFuture).toArray(CompletableFuture[]::new));

        // III. Return value

        // Since all the child nodes are connected to the rootMcts, we can simply return it here after all the workers stopped
        return mcts;
    }


}
