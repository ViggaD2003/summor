package com.summor.summorwar.controller;

import com.summor.summorwar.models.GameAccount;
import com.summor.summorwar.models.RedeemJob;
import com.summor.summorwar.service.ExcelService;
import com.summor.summorwar.service.RedeemJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/giftcode")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GiftCodeController {
    private final ExcelService excelService;
    private final RedeemJobService redeemJobService;

    @PostMapping(value = "/redeem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> redeemGiftCode(
            @RequestParam("file") MultipartFile file,
            @RequestParam("code") String giftCode
    ) {
        String normalizedGiftCode = giftCode == null ? "" : giftCode.trim().toUpperCase();
        if (normalizedGiftCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gift code must not be blank");
        }

        List<GameAccount> accounts = excelService.readExcel(file);

        if (accounts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Excel file does not contain any account");
        }

        RedeemJob job = redeemJobService.createJob(accounts, normalizedGiftCode);
        redeemJobService.startJobAsync(job.getJobId(), accounts, normalizedGiftCode);

        return Map.of(
                "jobId", job.getJobId(),
                "status", job.getStatus(),
                "total", job.getTotal(),
                "message", "Redeem job has been queued"
        );
    }

    @GetMapping("/redeem/{jobId}")
    public RedeemJob getRedeemJob(@PathVariable String jobId) {
        return redeemJobService.getJob(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
    }
}
