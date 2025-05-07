package com.nebula.socketv2.server.protocol;

import com.nebula.socketv2.server.authentication.JwkAuthentication;
import org.slf4j.Logger;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import tech.kwik.core.QuicConnection;
import tech.kwik.core.server.ApplicationProtocolConnection;
import tech.kwik.core.server.ApplicationProtocolConnectionFactory;


public class ApplicationProtocolFactory implements ApplicationProtocolConnectionFactory {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ApplicationProtocolFactory.class);
    private final JwkAuthentication jwkAuthentication;

    public ApplicationProtocolFactory(JwkAuthentication authentication) {
        jwkAuthentication = authentication;
    }

    @Override
    public ApplicationProtocolConnection createConnection(String s, QuicConnection quicConnection) {
        if (quicConnection.isDatagramExtensionEnabled()){
            return new UnreliableDatagram(quicConnection, jwkAuthentication);
        }
        return new ReliableConnection(quicConnection, jwkAuthentication);
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
