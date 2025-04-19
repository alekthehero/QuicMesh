package com.nebula.socketv2;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.kwik.core.QuicClientConnection;
import tech.kwik.core.QuicStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthenticationTest {

    private static final String Protocol = "nebula";

    private static AWSCognitoIdentityProvider cognitoClient;
    private static final String clientId = "4bhohvu403o13pdb345r7a9hsh";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test123";

    private static String accessToken;

    @BeforeAll
    public static void setup() {
        cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        InitiateAuthRequest initiateAuthRequest = new InitiateAuthRequest()
                .withClientId(clientId)
                .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .addAuthParametersEntry("USERNAME", USERNAME)
                .addAuthParametersEntry("PASSWORD", PASSWORD);
        accessToken = cognitoClient.initiateAuth(initiateAuthRequest)
                .getAuthenticationResult()
                .getAccessToken();
    }

    @Tag("integration")
    @Test
    public void testAuthentication() throws IOException {
        assertNotNull(accessToken);

        QuicClientConnection quicClientConnection = QuicClientConnection.newBuilder()
                .uri(URI.create("https://localhost:9001"))
                .applicationProtocol(Protocol)
                .build();
        quicClientConnection.connect();

        QuicStream quicStream = quicClientConnection.createStream(true);
        OutputStream outputStream = quicStream.getOutputStream();
        outputStream.write(accessToken.getBytes());
        outputStream.flush();
        outputStream.close();
        InputStream inputStream = quicStream.getInputStream();
        byte[] bytes = new byte[1024];
        int read = inputStream.read(bytes);
        String response = new String(bytes, 0, read);
        System.out.println(response);
    }


}
