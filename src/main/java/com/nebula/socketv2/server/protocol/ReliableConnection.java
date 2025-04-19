package com.nebula.socketv2.server.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nebula.socketv2.server.structure.ResponsePacket;
import com.nebula.socketv2.server.structure.UpdateType;
import org.slf4j.Logger;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import tech.kwik.core.QuicConnection;
import tech.kwik.core.QuicStream;
import tech.kwik.core.server.ApplicationProtocolConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReliableConnection implements ApplicationProtocolConnection {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(ReliableConnection.class);
    private QuicConnection quicConnection;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean authenticated = false;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtDecoder jwtDecoder;

    public ReliableConnection(QuicConnection quicConnection, JwtDecoder jwtDecoder) {
        this.quicConnection = quicConnection;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public void acceptPeerInitiatedStream(QuicStream stream) {
        inputStream = stream.getInputStream();
        outputStream = stream.getOutputStream();
        if (!authenticated) {
            if (!authenticate()) {
                logger.info("Authentication failed");
                return;
            }
            authenticated = true;
            logger.info("Authentication successful");
        }
        processMessage(stream);
    }

    private boolean authenticate() {
        try {
            byte[] bytes = new byte[1024];
            int read = inputStream.read(bytes);
            String token = new String(bytes, 0, read);
            if (validToken(token)) {
                authenticated = true;
                ResponsePacket response = ResponsePacket.builder()
                        .updateType(UpdateType.AUTHENTICATE)
                        .build();
                outputStream.write(objectMapper.writeValueAsBytes(response));
                outputStream.flush();
                return true;
            }
            logger.info("Authentication failed");
            outputStream.write("Authentication failed".getBytes());
            outputStream.flush();
            outputStream.close();
            quicConnection.close();
            return false;
        } catch (IOException e) {
            logger.error("Error reading from stream: " + e.getMessage());
            quicConnection.close();
            return false;
        }
    }

    private boolean validToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            logger.info("Token verification successful: " + jwt.getClaims());
        } catch (Exception exception) {
            logger.error("Token verification failed: " + exception.getMessage());
            return false;
        }
        return false;
    }

    private void processMessage(QuicStream stream) {
        try {
            InputStream inputStream = stream.getInputStream();
            byte[] bytes = new byte[1024];
            int numRead = inputStream.read(bytes);
            logger.info("Received message: " + new String(bytes, 0, numRead));
            OutputStream outputStream = stream.getOutputStream();
            outputStream.write("Hello, World!".getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            logger.error("Error reading from stream: " + e.getMessage());
        }
    }
}
