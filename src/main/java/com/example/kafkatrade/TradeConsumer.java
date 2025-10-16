package com.example.kafkatrade;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Simple consumer to read trade events.
 */
public class TradeConsumer implements Runnable {

    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private volatile boolean running = true;

    public TradeConsumer(String bootstrapServers, String groupId, String topic) {
        this.topic = topic;
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        p.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // manual commit example
        p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new KafkaConsumer<>(p);
    }

    @Override
    public void run() {
        consumer.subscribe(Collections.singletonList(topic));
        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> r : records) {
                    System.out.printf("Consumed trade key=%s value=%s partition=%d offset=%d%n",
                            r.key(), r.value(), r.partition(), r.offset());
                }
                if (!records.isEmpty()) {
                    consumer.commitSync(); // synchronous commit for simplicity
                }
            }
        } catch (WakeupException e) {
            // ignore on shutdown
        } finally {
            consumer.close();
        }
    }

    public void stop() {
        running = false;
        consumer.wakeup();
    }
}
