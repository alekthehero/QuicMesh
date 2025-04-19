package com.nebula.socketv2.server.structure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@JsonInclude()
@NoArgsConstructor
public class PlayerMotion {

    @JsonCreator
    public PlayerMotion(
            @JsonProperty("playerName") String playerName,
            @JsonProperty("motion") Motion motion,
            @JsonProperty("isOnline") Boolean isOnline,
            @JsonProperty("updatedAt") Instant updatedAt
    ){
        this.playerName = playerName;
        this.motion = motion;
        this.isOnline = isOnline;
        this.updatedAt = updatedAt;
    }

    String playerName;
    Motion motion;
    Boolean isOnline;
    Instant updatedAt;


}
