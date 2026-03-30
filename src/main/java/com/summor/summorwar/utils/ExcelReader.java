package com.summor.summorwar.utils;

import com.summor.summorwar.models.GameAccount;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExcelReader {

    @Value("${sw.server:ASIA}")
    private String server;

    public List<GameAccount> readAccounts(MultipartFile file) throws IOException {

        List<GameAccount> accounts = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue;

                Cell cell = row.getCell(0);

                if (cell == null) continue;

                String hiveId = cell.toString().trim();

                if (hiveId.isEmpty()) continue;

                accounts.add(new GameAccount(hiveId, server));
            }
        }

        return accounts;
    }
}