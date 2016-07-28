package c4.docker;

import c4.docker.exception.DockerException;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class DockerImageTest {
    private static final String EXISTING_CONTAINER = "docker.insops.net/zachp/bot";

    private static final String NONEXTANT_CONTAINER = "docker.insops.net/zachp/notfound";

    private DockerContainer container;

    @After
    public void cleanUpContainer() {
        if (container != null) {
            container.kill();
        }
    }

    @Test
    public void itCanStartANewContainer() {
        DockerImage image = new DockerImage(EXISTING_CONTAINER);
        container = image.run();

        assertNotNull(container.getProcess());
    }

    @Test
    public void itNotifiesTheCallerWhenPullFails() throws DockerException {
        DockerImage image = new DockerImage(NONEXTANT_CONTAINER);

        assertFalse(image.pull());
    }
}