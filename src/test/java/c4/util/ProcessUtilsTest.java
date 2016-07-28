package c4.util;

import org.junit.Test;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ProcessUtilsTest {
    @Test
    public void itFetchesThePidOfChildProcess() throws IOException {
        Process child = spawnProcess("sleep 2");
        int pid = ProcessUtils.getPid(child);

        assertTrue(pid > 0);
    }

    @Test
    public void itKillsLivingProcesses() throws IOException, InterruptedException {
        Process child = spawnProcess("sleep 20");

        ProcessUtils.kill(child);
        child.waitFor(5, TimeUnit.SECONDS);

        assertFalse(child.isAlive());
    }

    private Process spawnProcess(String command) throws IOException {
        return Runtime.getRuntime().exec(command);
    }
}
