package io.opns.app.core;

import org.javalabs.decl.container.spi.EmbeddedHttpServer;
import org.javalabs.decl.container.spi.HttpServerProvider;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AppHttpServer extends AbstractVerticle {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppHttpServer.class);
    
    // Name of this verticle.
    private static final String NAME = "AppHttpServer";
    
    private EmbeddedHttpServer server;
    
    @Override
    public void start() throws Exception {
        // Since this verticle is starting an embedded http server, therefore get the vert.x server.xml file
        String configFile = config().getString("config");
        
        server = HttpServerProvider.get().create(configFile);
        server.start(getVertx());
    }

    @Override
    public void stop() throws Exception {
        server.stop(0);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Stopped http server verticle {}", NAME);
        }
    }
}
