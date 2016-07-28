package c4.game.listener;

import c4.game.Match;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketMatchListener implements MatchListener {
    private final PrintWriter out;

    public SocketMatchListener(Socket connection) throws IOException {
        this.out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
    }

    @Override
    public void onEvent(Match game) {
        out.println(game.toJSON());
        out.flush();
    }
}