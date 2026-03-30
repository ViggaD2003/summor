package com.summor.summorwar.service;

import com.summor.summorwar.models.GameAccount;

import com.summor.summorwar.utils.ExcelReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;


@Service
@RequiredArgsConstructor
public class ExcelService {

    private final ExcelReader excelReader;

    public List<GameAccount> readExcel(MultipartFile file) {

        try {
            return excelReader.readAccounts(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read excel file", e);
        }

    }
}
