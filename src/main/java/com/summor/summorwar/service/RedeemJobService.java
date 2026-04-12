package com.summor.summorwar.service;

import com.summor.summorwar.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedeemJobService {

    private final GiftCodeAutomationService automationService;
    private final Map<String, RedeemJob> jobs = new ConcurrentHashMap<>();

    public RedeemJob createJob(List<GameAccount> accounts, String giftCode) {
        String jobId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        List<RedeemAccountResult> results = accounts.stream()
                .map(account -> RedeemAccountResult.builder()
                        .hiveId(account.getHiveId())
                        .hiveServer(account.getHiveServer())
                        .status(RedeemAccountStatus.PENDING)
                        .build())
                .toList();

        RedeemJob job = RedeemJob.builder()
                .jobId(jobId)
                .status(RedeemJobStatus.QUEUED)
                .giftCode(giftCode)
                .total(accounts.size())
                .done(0)
                .createdAt(now)
                .updatedAt(now)
                .results(results)
                .build();

        jobs.put(jobId, job);
        return job;
    }

    public Optional<RedeemJob> getJob(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    public void startJobAsync(String jobId, List<GameAccount> accounts, String giftCode) {
        Thread.startVirtualThread(() -> runJob(jobId, accounts, giftCode));
    }

    private void runJob(String jobId, List<GameAccount> accounts, String giftCode) {
        RedeemJob job = jobs.get(jobId);
        if (job == null) {
            return;
        }

        job.setStatus(RedeemJobStatus.RUNNING);
        touch(job);

        try {
            automationService.redeemForAccounts(accounts, giftCode, new GiftCodeAutomationService.RedeemProgressListener() {
                @Override
                public void onAccountStarted(GameAccount account, int index, int total) {
                    updateAccount(jobId, index, RedeemAccountStatus.RUNNING, null, null);
                }

                @Override
                public void onAccountFinished(GameAccount account, int index, int total, boolean success, String error) {
                    RedeemAccountStatus status = success ? RedeemAccountStatus.SUCCESS : RedeemAccountStatus.FAILED;
                    updateAccount(jobId, index, status, success ? giftCode : null, error);
                    incrementDone(jobId);
                }
            });

            RedeemJob completedJob = jobs.get(jobId);
            if (completedJob != null) {
                completedJob.setStatus(RedeemJobStatus.COMPLETED);
                touch(completedJob);
            }
        } catch (Exception exception) {
            log.error("Redeem job {} failed", jobId, exception);
            RedeemJob failedJob = jobs.get(jobId);
            if (failedJob != null) {
                failedJob.setStatus(RedeemJobStatus.FAILED);
                failedJob.setError(exception.getMessage());
                touch(failedJob);
            }
        }
    }

    private void updateAccount(String jobId, int index, RedeemAccountStatus status, String code, String error) {
        RedeemJob job = jobs.get(jobId);
        if (job == null || index < 0 || index >= job.getResults().size()) {
            return;
        }

        RedeemAccountResult result = job.getResults().get(index);
        result.setStatus(status);
        result.setCode(code);
        result.setError(error);
        touch(job);
    }

    private void incrementDone(String jobId) {
        RedeemJob job = jobs.get(jobId);
        if (job == null) {
            return;
        }

        job.setDone(job.getDone() + 1);
        touch(job);
    }

    private void touch(RedeemJob job) {
        job.setUpdatedAt(Instant.now());
    }
}
