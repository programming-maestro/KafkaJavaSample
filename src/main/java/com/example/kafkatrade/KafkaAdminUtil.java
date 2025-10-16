package com.example.kafkatrade;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Utility class to create Kafka topics if not present.
 */
public class KafkaAdminUtil {

    /**
     * Create topic if it doesn't exist. Uses the AdminClient to create the topic.
     *
     * @param bootstrapServers Kafka bootstrap servers (e.g., "localhost:9092")
     * @param topic            the topic name
     * @param partitions       number of partitions
     * @param replication      replication factor (use 1 for single-node dev)
     */
    public static void createTopicIfNotExists(String bootstrapServers, String topic, int partitions, short replication) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        try (AdminClient admin = AdminClient.create(props)) {
            NewTopic newTopic = new NewTopic(topic, partitions, replication);
            // createTopics returns a CreateTopicsResult - call all().get() to wait
            try {
                admin.createTopics(Collections.singleton(newTopic)).all().get();
                System.out.println("Topic created: " + topic);
            } catch (ExecutionException ee) {
                // Topic already exists or other error — handle
                System.out.println("Topic might already exist or creation failed: " + ee.getCause());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
