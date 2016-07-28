package c4.game;

import c4.docker.DockerContainer;

import java.io.*;
import java.util.Scanner;

public class DockerPlayer implements Player, Closeable {
    private final InputStream in;

    private final String name;

    private final OutputStream out;

    private final DockerContainer container;

    public DockerPlayer(DockerContainer container) {
        this.container = container;
        this.in = container.getInputStream();
        this.name = container.getImageName();
        this.out = container.getOutputStream();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNextMove() {
        Scanner scanner = new Scanner(in, "UTF-8");
        return scanner.nextInt();
    }

    @Override
    public void sendMessage(String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.write(message);
        writer.write("\n");
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        container.kill();
    }
}
