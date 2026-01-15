package com.peerislands.demo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.peerislands.demo.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderProcessingScheduler {
     private final OrderService orderService;

     public OrderProcessingScheduler(OrderService orderService) {
        this.orderService = orderService;
    }
    // scheduled method: every x minutes convert PENDING -> PROCESSING
    @Scheduled(
        fixedRateString = "${orders.scheduler.rate-ms}",
        initialDelayString = "${orders.scheduler.initial-delay-ms}"
    )           
    public void movePendingOrdersToProcessing() {
       log.debug("OrderProcessingScheduler triggered");
        try {
            orderService.processPendingOrders();
        } catch (Exception ex) {
            log.error("Failed to process pending orders", ex);
        }
    }
}
