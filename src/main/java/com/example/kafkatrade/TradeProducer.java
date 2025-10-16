package com.example.kafkatrade;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

/**
 * Produces example "trade" events to Kafka.
 */
public class TradeProducer {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    public TradeProducer(String bootstrapServers, String topic) {
        Properties p = new Properties();
        p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // Recommended for trading / idempotency in real systems:
        p.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        p.put(ProducerConfig.ACKS_CONFIG, "all");
        this.producer = new KafkaProducer<>(p);
        this.topic = topic;
    }

    /**
     * Send a fake trade event to Kafka.
     */
    public void sendTrade(String tradeId, String symbol, double price, int qty) {
        String key = tradeId;
        String value = String.format("{\"tradeId\":\"%s\",\"symbol\":\"%s\",\"price\":%.2f,\"qty\":%d}", tradeId, symbol, price, qty);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Send failed: " + exception.getMessage());
            } else {
                System.out.println("Sent trade to topic " + metadata.topic() + " partition " + metadata.partition() + " offset " + metadata.offset());
            }
        });
    }

    public void flushAndClose() {
        producer.flush();
        producer.close();
    }
}
