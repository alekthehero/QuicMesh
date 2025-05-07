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

    public Jwt authenticate(InputStream inputStream, OutputStream outputStream) {
        try {
            byte[] read = inputStream.readAllBytes();
            String token = new String(read);
            Jwt jwt = null;
            if ((jwt = validToken(token)) != null) {
                ResponsePacket response = ResponsePacket.builder()
                        .updateType(UpdateType.AUTHENTICATE)
                        .build();
                outputStream.write(mapper.writeValueAsBytes(response));
                outputStream.flush();
                return jwt;
            }
            outputStream.close();
            return null;
        } catch (IOException e) {
            logger.error("Error reading from stream: " + e.getMessage());
            return null;
        }
    }

    public Jwt validToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt;
        } catch (Exception exception) {
            logger.error("Error decoding token: " + token);
            logger.error("Token verification failed: " + exception.getMessage());
            return null;
        }
    }
}
