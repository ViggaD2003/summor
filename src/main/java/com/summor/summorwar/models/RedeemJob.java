package com.summor.summorwar.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemJob {
    private String jobId;
    private RedeemJobStatus status;
    private String giftCode;
    private int total;
    private int done;
    private Instant createdAt;
    private Instant updatedAt;
    private String error;
    private List<RedeemAccountResult> results;
}
