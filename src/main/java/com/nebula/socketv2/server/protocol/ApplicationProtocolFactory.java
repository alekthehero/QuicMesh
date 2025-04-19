package com.nebula.socketv2.server.protocol;

import org.slf4j.Logger;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import tech.kwik.core.QuicConnection;
import tech.kwik.core.server.ApplicationProtocolConnection;
import tech.kwik.core.server.ApplicationProtocolConnectionFactory;


public class ApplicationProtocolFactory implements ApplicationProtocolConnectionFactory {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ApplicationProtocolFactory.class);

    private final JwtDecoder jwtDecoder;

    public ApplicationProtocolFactory(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public ApplicationProtocolConnection createConnection(String s, QuicConnection quicConnection) {
        logger.info("Creating application protocol connection: {}", quicConnection.toString());
        if (quicConnection.isDatagramExtensionEnabled()){
            System.out.println("Datagram extension is enabled");
            return new UnreliableDatagram(quicConnection);
        }
        System.out.println("Datagram extension is not enabled");
        return new ReliableConnection(quicConnection, jwtDecoder);
    }
    @Override
    public int maxConcurrentPeerInitiatedUnidirectionalStreams() {
        return 1;
    }

    @Override
    public int maxConcurrentPeerInitiatedBidirectionalStreams() {
        return 1;
    }

    @Override
    public boolean enableDatagramExtension() {
        return true;
    }
}
