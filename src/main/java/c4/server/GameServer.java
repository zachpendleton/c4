package c4.server;

import c4.game.listener.HttpMatchListener;
import c4.game.listener.SocketMatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int DEFAULT_POOL_SIZE = 16;

    private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

    private final int port;

    private boolean isStarted = false;

    private int poolSize = DEFAULT_POOL_SIZE;

    private ServerSocket socket;

    private ExecutorService threadPool;

    public GameServer(int port) {
        this.port = port;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void start() {
        startServer();
        isStarted = socket.isBound();

        if (isStarted) {
            threadPool = Executors.newFixedThreadPool(poolSize);
        }

        while (isStarted) {
            try {
                final Socket connection = socket.accept();
                final GameRequestHandler handler = new GameRequestHandler(connection);
                handler.addListener(new SocketMatchListener(connection));
                handler.addListener(new HttpMatchListener("http://localhost:8080/games"));

                threadPool.execute(handler);
            } catch (IOException e) {
                // error during connection
            }
        }
    }

    public void stop() {
        try {
            isStarted = false;
            this.socket.close();
            this.threadPool.shutdown();
        } catch (IOException e) {
            // ignore
        }
    }

    private void startServer() {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            logger.error("unable to bind to {}", port);
            try {
                socket = new ServerSocket();
            } catch (IOException e1) {
                // ignore
            }
        }
    }
}
