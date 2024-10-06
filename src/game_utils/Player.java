package game_utils;

public abstract class Player {
    private final String gameId;

    public Player(String gameId) {
        this.gameId = gameId;
    }

    protected final int waitForTurnAndGetState() {
        try {
            return RestClient.waitForTurnAndGetState(gameId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected final void move(int field) {
        try {
            RestClient.move(gameId, field + 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void start();
}
