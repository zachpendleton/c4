package c4.docker;

import c4.docker.exception.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DockerImage {
    private static final Logger logger = LoggerFactory.getLogger(DockerImage.class);

    private final String name;

    public DockerImage(String name) {
        this.name = name;
    }

    public boolean pull() throws DockerException {
        try {
            logger.info("docker pull {}", name);
            Process pullRequest = new ProcessBuilder("docker", "pull", name).start();
            pullRequest.waitFor(60, TimeUnit.SECONDS);

            return !pullRequest.isAlive() && pullRequest.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            logger.error("docker: could not pull {}", name);
            throw new DockerException("could not pull " + name);
        }
    }

    public DockerContainer run() {
        try {
            Process runningContainer = new ProcessBuilder(
                    "docker", "run",
                    "--rm",
                    "-i",
                    "-a", "stdin",
                    "-a", "stdout",
                    "-a", "stderr",
                    name).start();

            return new DockerContainer(name, runningContainer);
        } catch (IOException e) {
            // ignore
            return null;
        }
    }
}
