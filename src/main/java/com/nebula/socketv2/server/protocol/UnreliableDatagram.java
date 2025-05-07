package com.nebula.socketv2.server.protocol;

import com.nebula.socketv2.server.authentication.JwkAuthentication;
import org.slf4j.Logger;
import org.springframework.security.oauth2.jwt.Jwt;
import tech.kwik.core.QuicConnection;
import tech.kwik.core.server.ApplicationProtocolConnection;

public class UnreliableDatagram implements ApplicationProtocolConnection {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UnreliableDatagram.class);

    private final JwkAuthentication jwkAuthentication;

    private QuicConnection quicConnection;
    private Jwt jwt = null;
    private String user = "user";

    public UnreliableDatagram(QuicConnection quicConnection, JwkAuthentication jwkAuthentication) {
        this.quicConnection = quicConnection;
        this.jwkAuthentication = jwkAuthentication;

        this.quicConnection.setDatagramHandler(this::handleDatagram);
    }

    private void handleDatagram(byte[] bytes) {
        String recieved = new String(bytes);
        String token = recieved.substring(0, recieved.indexOf(":") + 1);
        String message = recieved.substring(recieved.indexOf(":") + 1);
        logger.info(quicConnection.hashCode() + ": Received datagram: " + message);
        if ((jwt = jwkAuthentication.validToken(token)) == null) {
            logger.info(quicConnection.hashCode() + ": Authentication failed");
            return;
        }
        user = jwt.getClaims().get("username").toString();
        logger.info(user + ": Authentication successful");
        if (message.equals("ping")) {
            logger.info(user + ": Sending message: pong");
            quicConnection.sendDatagram("pong".getBytes());
        }
        quicConnection.close();
    }
}
