package com.folks.app.main;

import org.javalabs.decl.container.ContainerConfig;
import com.folks.app.core.AppContainer;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class.
 */
public class FolksAppMain {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FolksAppMain.class);
    
    private static final CountDownLatch LATCH = new CountDownLatch(1);
    
    public static void main(String[] args) throws Exception {
        if (System.getProperties().containsKey("pause")) {
            sleep();
        }
        AppContainer container = new AppContainer();
        container.setup(new ContainerConfig());
        
        addShutdownHook(container);
    }

    private static void addShutdownHook(final AppContainer container) {
        Runtime.getRuntime().addShutdownHook(new Thread(
            () -> {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Shutdown Hook is invoked");
                }
                //container.undeployAll();
                container.destroy();
                LATCH.countDown();
        }));
    }
    
    /**
     * Sleep for sometime in order to do some pre-bootstrap work.
     */
    protected static void sleep() {
        try {
            Thread.sleep(6000);
        }
        catch (InterruptedException e) {
            // Do Nothing
        }
    }
}
