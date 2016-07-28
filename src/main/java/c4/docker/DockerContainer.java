package c4.docker;

import c4.util.ProcessUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class DockerContainer implements Closeable, AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainer.class);

    private final String imageName;

    private final Process process;

    DockerContainer(String imageName, Process process) {
        this.imageName = imageName;
        this.process = process;
    }

    @Override
    public void close() throws IOException {
        kill();
    }

    public InputStream getErrorStream() {
        if (process == null) return null;
        return process.getErrorStream();
    }

    public String getImageName() {
        return imageName;
    }

    public InputStream getInputStream() {
        if (process == null) return null;
        return process.getInputStream();
    }

    public OutputStream getOutputStream() {
        if (process == null) return null;
        return process.getOutputStream();
    }

    public boolean isRunning() {
        return process.isAlive();
    }

    public Process getProcess() {
        return process;
    }

    public void kill() {
        if (process == null) {
            return;
        }

        try {
            IOUtils.closeQuietly(process.getErrorStream());
            IOUtils.closeQuietly(process.getInputStream());
            IOUtils.closeQuietly(process.getOutputStream());
            process.destroy();
            process.waitFor(5, TimeUnit.SECONDS);

            if (process.isAlive()) {
                logger.debug("container {} did not shutdown gracefully; sending SIGINT", imageName);
                ProcessUtils.kill(process);
            }
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
