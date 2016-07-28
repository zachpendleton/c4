package c4.server;

import c4.docker.DockerImage;
import c4.docker.exception.DockerException;
import c4.game.DockerPlayer;
import c4.game.Match;
import c4.game.Player;
import c4.game.listener.MatchListener;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class GameRequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GameRequestHandler.class);

    private static final String DEFAULT_BOT = "docker.insops.net/zachp/bot:latest";

    private final Socket connection;

    private final List<MatchListener> listeners = new ArrayList<>();

    private PrintWriter writer;

    public GameRequestHandler(Socket connection) {
        this.connection = connection;
        try {
            this.writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        } catch (IOException e) {
            logger.error("could not initialize socket writer");
            this.writer = null;
        }
    }

    public void addListener(MatchListener listener) {
        listeners.add(listener);
    }

    @Override
    public void run() {
        MDC.put("requestId", UUID.randomUUID().toString());
        Match match = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
            String requestBody = reader.readLine();
            if (requestBody == null) {
                logger.info("empty request body");
                writer.println("ERR_EMPTY_REQUEST");
                return;
            }
            String[] playerNames = requestBody.split(";");
            if (playerNames.length != 2) {
                playerNames = new String[]{playerNames[0], DEFAULT_BOT};
            }

            logger.info(requestBody);

            DockerImage d1 = pullDockerImage(playerNames[0]);
            DockerImage d2 = pullDockerImage(playerNames[1]);

            match = new Match(new DockerPlayer(d1.run()),
                    new DockerPlayer(d2.run()));
            while (!match.isWon()) {
                MDC.put("currentPlayer", match.getCurrentPlayer().getName());
                notifyListeners(match);
                match.play();
            }
            notifyListeners(match);
            MDC.remove("currentPlayer");

        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (DockerException e) {
            writer.println("{\"status\": \"ERR_DOCKER_PULL\", \"message\": \"" + e.getMessage() +"\"");
        } finally {
            closePlayers(match);
            IOUtils.closeQuietly(connection);
        }

        MDC.clear();
    }

    private void closePlayers(Match match) {
        if (match == null) return;

        for (Player player : match.getPlayers()) {
            try {
                ((DockerPlayer) player).close();
            } catch (IOException e) {
                logger.error("could not close player {}", player.getName());
            }
        }
    }

    private void notifyListeners(Match match) {
        for (MatchListener listener : listeners) {
            listener.onEvent(match);
        }
    }

    private DockerImage pullDockerImage(String imageName) throws DockerException {
        DockerImage image = new DockerImage(imageName);
        image.pull();

        return image;
    }
}
