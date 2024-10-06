package mcts;

import utils.Game;
import utils.GameUtils;

import java.util.Arrays;

public class Mcts {
    public static final double C = 1.4;
    public final TreeNode root;
    public final Game rootGame;

    public Mcts(Game rootGame) {
        this(new TreeNode(), rootGame);
    }

    public Mcts(final TreeNode root, Game rootGame) {
        this.root = root;
        this.rootGame = rootGame;
    }


    public void startIteration() {
        runOn(root, rootGame.clone());
    }

    /**
     * executes the algorithem on a given node and returns the result for backpropagation.
     * @return an int[2] representing [winsA, winsB]
     */
    private int[] runOn(final TreeNode node, final Game runningGame) {

        //Check if all child nodes are expanded
        for (int i = 0; i < 6; i++) {
            if (node.childNodes[i] == null) {
                // if not,  create new node
                node.childNodes[i] = new TreeNode();
                if (!runningGame.moveNormalized(i)) {
                    node.childNodes[i].isValidMove = false;
                    continue;
                }

                int[] res = new int[] {0, 0};

                for(int j = 0; j < 5; j++) {
                    Simulation sim = new Simulation(runningGame);
                    sim.startSimulation();
                    if (sim.getSimulationResult() == Game.Winner.PLAYER_A) {
                        res[0] += 1;
                    } else {
                        res[1] += 1;
                    }
                }
                node.childNodes[i].winsA += res[0];
                node.childNodes[i].winsB += res[1];

                node.winsA += res[0];
                node.winsB += res[1];


                // start backpropagation
                return res;
            }
        }

        final Double[] UCTScores;
        if (runningGame.isPlayerATurn()) {
            UCTScores = Arrays.stream(node.childNodes).parallel().map(
                            childNode -> childNode.isValidMove ? getUCTScore(childNode.winsA, childNode.winsB, node.winsA + node.winsB) : 0)
                    .toArray(Double[]::new);
        } else {
            UCTScores = Arrays.stream(node.childNodes).parallel().map(
                            childNode -> childNode.isValidMove ? getUCTScore(childNode.winsB, childNode.winsA, node.winsA + node.winsB) : 0)
                    .toArray(Double[]::new);
        }

        int maxIndex = 0;
        for (int i = 0; i < 6; i++) {
            if (UCTScores[maxIndex] < UCTScores[i]) {
                maxIndex = i;
            }
        }

        int[] resultForBackpropagation;

        if (runningGame.moveNormalized(maxIndex)) {
            try {
                resultForBackpropagation = runOn(node.childNodes[maxIndex], runningGame);
            } catch (StackOverflowError e) {
                System.err.println(e);
                resultForBackpropagation = new int[] {runningGame.isPlayerATurn() ? 0 : 1, runningGame.isPlayerATurn() ? 1 : 0};
            }
        } else {
            // Case no valid move from here, if all games have an UCT score of 0, game here is marked as loss TODO: IDea increse the amounts of enemy win to make this scenary more unlikely
            resultForBackpropagation = new int[] {runningGame.isPlayerATurn() ? 0 : 1, runningGame.isPlayerATurn() ? 1 : 0};
        }



        node.winsA += resultForBackpropagation[0];
        node.winsB += resultForBackpropagation[1];
        return resultForBackpropagation;
    }

    public int getBestMove() {
        final Double[] UCTScores;

        if (rootGame.isPlayerATurn()) {
            UCTScores = Arrays.stream(root.childNodes).parallel().map(
                            childNode -> childNode.isValidMove ? getUCTScore(childNode.winsA, childNode.winsB, root.winsA + root.winsB) : 0)
                    .toArray(Double[]::new);
        } else {
            UCTScores = Arrays.stream(root.childNodes).parallel().map(
                            childNode -> childNode.isValidMove ? getUCTScore(childNode.winsB, childNode.winsA, root.winsA + root.winsB) : 0)
                    .toArray(Double[]::new);
        }
        int maxIndex = 0;
        for (int i = 0; i < 6; i++) {
            if (UCTScores[maxIndex] < UCTScores[i]) {
                maxIndex = i;
            }
        }

        return maxIndex;
    }
    public TreeNode getRoot () {
        return root;
    }

    private double getUCTScore(int wonSimsA, int wonSimsB, int numberOfParentSimulations) {
        return (double) wonSimsA / (wonSimsA + wonSimsB) + C * Math.sqrt(Math.log(numberOfParentSimulations) / (wonSimsA + wonSimsB));
    }


}
