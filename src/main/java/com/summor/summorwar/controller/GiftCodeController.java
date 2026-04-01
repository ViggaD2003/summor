package com.summor.summorwar.controller;

import com.summor.summorwar.models.GameAccount;
import com.summor.summorwar.service.ExcelService;
import com.summor.summorwar.service.GiftCodeAutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/giftcode")
@RequiredArgsConstructor
public class GiftCodeController {
    private final ExcelService excelService;
    private final GiftCodeAutomationService automationService;

    @PostMapping(value = "/redeem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String redeemGiftCode(
            @RequestParam("file") MultipartFile file,
            @RequestParam("code") String giftCode
    ) {
        List<GameAccount> accounts = excelService.readExcel(file);

        automationService.redeemForAccounts(accounts, giftCode);

        return "Automation started for " + accounts.size() + " accounts";
    }
}
