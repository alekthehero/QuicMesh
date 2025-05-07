package com.nebula.socketv2.server.protocol;

import com.nebula.socketv2.server.authentication.JwkAuthentication;
import org.slf4j.Logger;
import org.springframework.security.oauth2.jwt.Jwt;
import tech.kwik.core.QuicConnection;
import tech.kwik.core.QuicStream;
import tech.kwik.core.server.ApplicationProtocolConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReliableConnection implements ApplicationProtocolConnection {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ReliableConnection.class);

    private final JwkAuthentication jwkAuthentication;

    private QuicConnection quicConnection;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Jwt jwt = null;
    private String user = "user";

    public ReliableConnection(QuicConnection quicConnection, JwkAuthentication jwkAuthentication) {
        this.quicConnection = quicConnection;
        this.jwkAuthentication = jwkAuthentication;
    }

    @Override
    public void acceptPeerInitiatedStream(QuicStream stream) {
        inputStream = stream.getInputStream();
        outputStream = stream.getOutputStream();
        logger.info(quicConnection.hashCode() + ": accepted peer initiated stream: " + stream.getStreamId());
        if (jwt == null) {
            jwt = jwkAuthentication.authenticate(inputStream, outputStream);
            if (jwt == null) {
                logger.warn(quicConnection.hashCode() + ": Authentication failed");
                quicConnection.close();
                return;
            }
            user = jwt.getClaims().get("username").toString();
        }
        processMessage(stream);
    }

    private void processMessage(QuicStream stream) {
        try {
            InputStream inputStream = stream.getInputStream();
            OutputStream outputStream = stream.getOutputStream();
            byte[] bytes = new byte[1024];
            int numRead = inputStream.read(bytes);
            String message = new String(bytes, 0, numRead);
            logger.info(user + ": Received Reliable message: " + message);
            if (message.equals("ping")) {
                outputStream.write("pong".getBytes());
                outputStream.flush();
                outputStream.close();
                logger.info(user + ": Sent Reliable message: pong");
                quicConnection.close();
                logger.info(user + ": Closed connection");
                return;
            }
            outputStream.write("Where my ping at".getBytes());
            outputStream.flush();
            outputStream.close();
            logger.info(user + ": Sent Reliable message: Where my ping at");
            quicConnection.close();
            logger.info(user + ": Closed connection");


        } catch (IOException e) {
            logger.error("Error reading from stream: " + e.getMessage());
        }
    }
}
