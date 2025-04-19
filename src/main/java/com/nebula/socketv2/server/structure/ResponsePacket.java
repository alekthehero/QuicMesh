package com.nebula.socketv2.server.structure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePacket {

    UpdateType updateType;

    String sessionKey;

    Set<String> lostPlayers;

    Set<String> playerKeys;

    Map<String, PlayerMotion> playerMotions;

}
