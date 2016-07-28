package c4.game.listener;

import c4.game.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpMatchListener implements MatchListener {
    private static final Logger logger = LoggerFactory.getLogger(HttpMatchListener.class);
    private final URL url;
    private boolean canConnect = true;

    public HttpMatchListener(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    @Override
    public void onEvent(Match game) {
        try {
            HttpURLConnection connection = getConnection();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"))) {
                writer.write(game.toJSON());
                writer.flush();
            }

            // force request to send
            connection.getResponseCode();
        } catch (IOException e) {
            if (canConnect) {
                logger.error("unable to connect to {}", url.toString());
                canConnect = false;
            }
        }
    }

    private HttpURLConnection getConnection() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        return connection;
    }
}
