//package com.summor.summorwar.scheduler;
//
//import com.summor.summorwar.service.AutoRedeemService;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class RedeemScheduler {
//
//    private final AutoRedeemService service;
//
//    public RedeemScheduler(AutoRedeemService service) {
//        this.service = service;
//    }
//
//    // Chạy mỗi 30 phút
//    @Scheduled(cron = "0 */1 * * * *")
//    public void autoRun() {
//        System.out.println("=== AUTO REDEEM START ===");
//        service.process();
//        System.out.println("=== AUTO REDEEM END ===");
//    }
//}