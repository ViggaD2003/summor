//package com.summor.summorwar.service;
//
//import com.summor.summorwar.crawler.CodeCrawler;
//import com.summor.summorwar.selenium.CodeRedeemer;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Service
//@Slf4j
//public class AutoRedeemService {
//
//    @Autowired
//    private CodeCrawler crawler;
//
//    @Autowired
//    private CodeRedeemer redeemer;
//
//    private final Set<String> redeemedCodes = new HashSet<>();
//
//    public void process() {
//        List<String> codes = crawler.crawlCodes();
//
//        for (String code : codes) {
//            if (!redeemedCodes.contains(code)) {
//                System.out.println("Redeeming: " + code);
//                boolean result = redeemer.redeem(code);
//
//                if (result) {
//                    log.info("Redeem success: {}", code);
//                } else {
//                    log.warn("Redeem failed: {}", code);
//                }
//
//
//                redeemedCodes.add(code);
//            }
//        }
//    }
//}