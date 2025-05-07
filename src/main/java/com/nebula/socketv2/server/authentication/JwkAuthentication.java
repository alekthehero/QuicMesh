package com.nebula.socketv2.server.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nebula.socketv2.server.structure.ResponsePacket;
import com.nebula.socketv2.server.structure.UpdateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class JwkAuthentication {
    private static final Logger logger = LoggerFactory.getLogger(JwkAuthentication.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final JwtDecoder jwtDecoder;

    public JwkAuthentication(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public boolean authenticate(InputStream inputStream, OutputStream outputStream) {
        try {
            byte[] read = inputStream.readAllBytes();
            String token = new String(read);
            if (validToken(token)) {
                ResponsePacket response = ResponsePacket.builder()
                        .updateType(UpdateType.AUTHENTICATE)
                        .build();
                outputStream.write(mapper.writeValueAsBytes(response));
                outputStream.flush();
                return true;
            }
            outputStream.close();
            return false;
        } catch (IOException e) {
            logger.error("Error reading from stream: " + e.getMessage());
            return false;
        }
    }

    public boolean validToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return true;
        } catch (Exception exception) {
            logger.error("Error decoding token: " + token);
            logger.error("Token verification failed: " + exception.getMessage());
            return false;
        }
    }
}
