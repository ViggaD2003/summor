//package com.summor.summorwar.crawler;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class CodeCrawler {
//
//    private static final String URL =
//            "https://rosa-midman.com/giftcode-summoners-war-sky-arena";
//
//    public List<String> crawlCodes() {
//        List<String> codes = new ArrayList<>();
//
//        try {
//            Document doc = Jsoup.connect(URL).get();
//            Elements rows = doc.select("table tbody tr");
//
//            for (Element row : rows) {
//                String code = row.select("td").first().text().trim();
//                codes.add(code);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return codes;
//    }
//}