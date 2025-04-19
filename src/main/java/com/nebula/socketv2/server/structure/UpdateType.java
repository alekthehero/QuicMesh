package com.nebula.socketv2.server.structure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UpdateType {
    PLAYER_MOTION,
    PLAYER_ATTACK,
    PING,
    AUTHENTICATE;

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @JsonCreator
    public static UpdateType forValue(String value) {
        return UpdateType.valueOf(value);
    }
}
