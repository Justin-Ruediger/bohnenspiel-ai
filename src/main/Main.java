package main;

import utils.Player;
import utils.RestClient;
import mcts.MctsPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public final static String SERVER = "http://bohnenspiel.informatik.uni-mannheim.de";
    public final static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        try {
            System.out.print("Name:");

            RestClient.name = input.readLine();

            final String gameId;
            while(true) {
                String[] openGames = RestClient.openGames();

                System.out.println("What game would you like to join? ([r] to reload)");
                System.out.println("[0] create new game");

                for (int i = 0; i < openGames.length; i++) {
                    System.out.printf("[%d] %s \n", i + 1, openGames[i]);
                }
                String res = input.readLine();
                if (res.equalsIgnoreCase("r")) {
                    System.out.println("reload...");
                    continue;
                }

                int value = Integer.parseInt(res);
                if (value == 0) {
                    gameId = RestClient.createGame();
                    break;
                } else {
                    RestClient.joinGame(openGames[value - 1]);
                    gameId = openGames[value - 1];
                    break;
                }
            }
            Player player = new MctsPlayer(gameId);
            player.start();
        } catch (IOException e) {
            System.err.println(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
