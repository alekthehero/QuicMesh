package com.nebula.socketv2.server.structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Packet {

    UpdateType updateType;
    PlayerMotion motion;


}
