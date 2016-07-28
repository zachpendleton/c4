package c4;

import c4.server.GameServer;

public class Main {
    public static void main(String[] args) {
        GameServer server = new GameServer(3000);
        server.start();
    }
}
