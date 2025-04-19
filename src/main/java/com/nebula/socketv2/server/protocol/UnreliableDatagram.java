package com.nebula.socketv2.server.protocol;

import org.slf4j.Logger;
import tech.kwik.core.QuicConnection;
import tech.kwik.core.server.ApplicationProtocolConnection;

public class UnreliableDatagram implements ApplicationProtocolConnection {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UnreliableDatagram.class);

    private QuicConnection quicConnection;

    public UnreliableDatagram(QuicConnection quicConnection) {
        this.quicConnection = quicConnection;
        this.quicConnection.setDatagramHandler(this::handleDatagram);
    }

    private void handleDatagram(byte[] bytes) {
        logger.info("Received datagram: " + new String(bytes) + " Length: " + bytes.length);
    }
}
