package com.nebula.socketv2.server.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@Builder
public class Motion {

    public Motion(
            @JsonProperty("x") float x,
            @JsonProperty("y") float y
    ){
    this.x = x;
    this.y = y;
    }

    private float x;
    private float y;

}
