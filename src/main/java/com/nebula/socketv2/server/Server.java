package com.nebula.socketv2.server;

import com.nebula.socketv2.server.protocol.ApplicationProtocolFactory;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import tech.kwik.core.log.Logger;
import tech.kwik.core.server.ServerConnectionConfig;
import tech.kwik.core.server.ServerConnector;

import java.net.SocketException;
import java.security.KeyStore;
import java.security.cert.CertificateException;

@Component
public class Server {
    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Server.class);
    private static ServerConnector serverConnector;
    private static final String applicationProtocolId = "nebula";
    private static final ServerConnectionConfig serverConnectionConfig = ServerConnectionConfig.builder()
            .maxOpenPeerInitiatedBidirectionalStreams(1)
            .maxOpenPeerInitiatedUnidirectionalStreams(1)
            .build();

    private final JwtDecoder jwtDecoder;

    public Server(JwtDecoder jwtDecoder) throws Exception {
        this.jwtDecoder = jwtDecoder;

        logger.info("Server Starting on port 9001");
        try {
            KeyStore keyStore = KeyStoreLoader.loadKeyStore("keystore.jks", "454545");
            try {
                initialize(9001, keyStore, new tech.kwik.core.log.SysOutLogger());
            } catch (Exception e) {
                logger.error("Failed to initialize server", e);
            }
            logger.info("Server started: {}", serverConnector.toString());
        } catch (Exception e) {
            logger.error("Failed to start server", e);
        }
    }

    public void initialize(int port, KeyStore keyStore, Logger logger) throws SocketException, CertificateException {
        serverConnector = ServerConnector.builder()
                .withPort(port)
                .withLogger(logger)
                .withKeyStore(keyStore, "alekthehero", "454545".toCharArray())
                .withConfiguration(serverConnectionConfig)
                .build();
        serverConnector.registerApplicationProtocol(applicationProtocolId, new ApplicationProtocolFactory(jwtDecoder));
        serverConnector.start();
    }

    public void stop() {
        serverConnector.close();
    }
}
