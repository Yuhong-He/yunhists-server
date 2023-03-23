package com.example.yunhists.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BatchOperateCatFailed {

    private int catFromId;

    private int catToId;

    private int type;

    private int reason;

}
