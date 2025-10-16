package com.example.kafkatrade;

import java.util.UUID;

/**
 * Simple demo runner for producer & consumer.
 */
public class AppMain {

    private static final String TOPIC = "trades";
    private static final String BOOTSTRAP = "localhost:9092";

    public static void main(String[] args) throws Exception {
        // 1) Ensure topic exists
        KafkaAdminUtil.createTopicIfNotExists(BOOTSTRAP, TOPIC, 3, (short) 1);

        // 2) Start consumer in a separate thread
        TradeConsumer consumer = new TradeConsumer(BOOTSTRAP, "trade-consumer-group", TOPIC);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        // 3) Produce some sample trades
        TradeProducer producer = new TradeProducer(BOOTSTRAP, TOPIC);
        for (int i = 1; i <= 5; i++) {
            String tradeId = UUID.randomUUID().toString();
            String symbol = (i % 2 == 0) ? "BTCUSD" : "ETHUSD";
            double price = 1000 + Math.random() * 100;
            int qty = 1 + (int) (Math.random() * 10);
            producer.sendTrade(tradeId, symbol, price, qty);
            Thread.sleep(500);
        }
        // give consumer time to read
        Thread.sleep(2000);

        // 4) Shutdown
        producer.flushAndClose();
        consumer.stop();
        consumerThread.join();
        System.out.println("App finished.");
    }
}
