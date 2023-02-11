package com.example.yunhists.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateALotCatFailed {

    private int catFromId;

    private int catToId;

    private int type;

    private int reason;

}
