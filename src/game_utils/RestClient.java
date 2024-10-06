package game_utils;

import main.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class RestClient {
    static final String SERVER = Main.SERVER;
    public static String name ="A";



    public static String createGame() throws Exception {
        String url = SERVER + "/api/creategame/" + name;
        String gameID = load(url);
        System.out.println("Spiel erstellt. ID: " + gameID);

        url = SERVER + "/api/check/" + gameID + "/" + name;
        while (true) {
            Thread.sleep(500);
            String state = load(url);
            System.out.print("." + " (" + state + ")");
            if (state.equals("0") || state.equals("-1")) {
                break;
            } else if (state.equals("-2")) {
                System.out.println("time out");
                return null;
            }
        }
        return gameID;
    }

    /**
     *
     * returnValues
     * 0: The game can be started
     * -1: It's your turn
     * -2: The game is finished
     * -3: It's not your turn
     * -4: game_utils.Player 2 is missing
     * 1-12: The last move of your opponent
     */
    public static int waitForTurnAndGetState(String gameID) throws Exception {
        Thread.sleep(100);

        String url = SERVER + "/api/check/" + gameID + "/" + name;
        int state = Integer.parseInt(load(url));
        while (!((1 <= state && state <= 12) || state == -1 || state == -2)) {
            Thread.sleep(100);
            state = Integer.parseInt(load(url));
        }

        return state;
    }

    public static String[] openGames() throws Exception {
        String url = SERVER + "/api/opengames";
        String result = load(url);
        if (result.isBlank()) {
            return new String[0];
        }
        return result.split(";");
    }


    public static boolean joinGame(String gameID) throws Exception {
        String url = SERVER + "/api/joingame/" + gameID + "/" + name;
        String state = load(url);
        String url2 = SERVER + "/api/check/" + gameID + "/" + name;

        String state2 = load(url2);
        return state.equals("1"); // Returns if the join was successfull
    }


    public static void move(String gameID, int fieldID) throws Exception {
        String url = SERVER + "/api/move/" + gameID + "/" + name + "/" + fieldID;
        System.out.println("MOVE : " + fieldID);
        System.out.println(load(url));
    }


    static String load(String url) throws Exception {
        URI uri = new URI(url.replace(" ", ""));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uri.toURL().openConnection().getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        return (sb.toString());
    }
}
