package mcts;

import game_utils.Game;

import java.util.Arrays;

public class Mcts {
    public static final double C = 2;
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
     *
     * @param node
     * @param runningGame
     * @return
     */
    private int[] runOn(final TreeNode node, final Game runningGame) {

        //Check if all child nodes are expanded
        for (int i = 0; i < 6; i++) {
            if (node.childNodes[i] == null) {
                // if not,  create new node
                node.childNodes[i] = new TreeNode();
                runningGame.moveNormalized(i);

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
                node.childNodes[i].winsB += res[0];


                // start backpropagation
                return res;
            }
        }

        final Double[] UCTScores;
        if (runningGame.isPlayerATurn()) {
            UCTScores = Arrays.stream(node.childNodes).parallel().map(
                            childNode -> getUCTScore(childNode.winsA, childNode.winsB, node.winsA + node.winsB))
                    .toArray(Double[]::new);
        } else {
            UCTScores = Arrays.stream(node.childNodes).parallel().map(
                            childNode -> getUCTScore(childNode.winsB, childNode.winsA, node.winsA + node.winsB))
                    .toArray(Double[]::new);
        }

        int maxIndex = 0;
        for (int i = 0; i < 6; i++) {
            if (UCTScores[maxIndex] < UCTScores[i]) {
                maxIndex = i;
            }
        }

        runningGame.moveNormalized(maxIndex);
        final int[] resultForBackpropagation = runOn(node.childNodes[maxIndex], runningGame);

        node.winsA += resultForBackpropagation[0];
        node.winsB += resultForBackpropagation[1];
        return resultForBackpropagation;
    }

    public int getBestMove() {
        final Double[] UCTScores;

        if (rootGame.isPlayerATurn()) {
            UCTScores = Arrays.stream(root.childNodes).parallel().map(
                            childNode -> getUCTScore(childNode.winsA, childNode.winsB, root.winsA + root.winsB))
                    .toArray(Double[]::new);
        } else {
            UCTScores = Arrays.stream(root.childNodes).parallel().map(
                            childNode -> getUCTScore(childNode.winsB, childNode.winsA, root.winsA + root.winsB))
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
        return (double) wonSimsA / wonSimsB + C * Math.sqrt(Math.log(numberOfParentSimulations) / (wonSimsA + wonSimsB));
    }


}
