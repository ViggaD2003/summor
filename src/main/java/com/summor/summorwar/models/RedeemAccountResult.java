package com.summor.summorwar.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemAccountResult {
    private String hiveId;
    private String hiveServer;
    private RedeemAccountStatus status;
    private String code;
    private String error;
}
