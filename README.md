# Kafka Trading Backend — Example

A simple Java + Kafka (Maven) example to demonstrate producing and consuming trade events. Intended for local development and experimentation (single-node KRaft Kafka).

## Recommended versions
- Java: **JDK 25** (OpenJDK build of your choice). See Oracle/Adoptium downloads for the latest builds.
- Kafka: **Apache Kafka 4.0.1** (KRaft mode, no ZooKeeper).
- IDE: **IntelliJ IDEA 2025.2** (Community or Ultimate)
- Build: **Maven**

(Verified at time of writing: Kafka 4.0.1 released Oct 13, 2025; JDK 25 available Sep 2025). :contentReference[oaicite:7]{index=7}

## Project layout
<see folder tree in repository>

## Build (local)
1. Install Java 25 and Maven.
2. Build jar:
   ```bash
   mvn clean package


-----------------------------------------------------------------------------------------------------------------------
Running Kafka locally (recommended: Docker + KRaft)
-----------------------------------------------------------------------------------------------------------------------
    - Kafka 4.x uses KRaft (no ZooKeeper). 
    - For local dev, use the included docker/docker-compose.yml

-----------------------------------------------------------------------------------------------------------------------
Quick steps (one-time init + start)
-----------------------------------------------------------------------------------------------------------------------
    1. Create kafka-data dir at docker/kafka-data (compose maps it) or keep the default path in the compose.
    2. Generate cluster id & format storage (one-time) — see README earlier for commands. This uses kafka-storage random-uuid and kafka-storage format.
    3. Start the broker:
        docker-compose -f docker/docker-compose.yml up -d
    4. Verify Kafka is listening on localhost:9092.
-----------------------------------------------------------------------------------------------------------------------
Running the app with Docker
-----------------------------------------------------------------------------------------------------------------------
    1. mvn clean package
    2. docker build -t kafka-trading-backend:0.1.0 .
    3. Run the container with network that can reach Kafka (for example --network host or attach to same docker network):

        docker run --network host kafka-trading-backend:0.1.0

-----------------------------------------------------------------------------------------------------------------------
What the sample does
-----------------------------------------------------------------------------------------------------------------------
    - KafkaAdminUtil creates topic trades (3 partitions) if it does not exist.
    - TradeProducer sends sample trade events (JSON strings).
    - TradeConsumer consumes trades and prints them.

-----------------------------------------------------------------------------------------------------------------------
Production notes / next steps
-----------------------------------------------------------------------------------------------------------------------
    - Single-node KRaft is dev-only. For production, run a multi-node controller quorum (>=3) for reliability.
    - Use secure connections (TLS) and authentication (SASL) in production.
    - For trading backends: consider exactly-once semantics and idempotent producers, transactional producers, schema registry (Avro/Protobuf) and partitioning strategy aligned to your trade routing.
    - Use monitoring: JMX metrics, Prometheus, Grafana.
    - Consider Confluent Platform or managed Kafka (MSK, Confluent Cloud) if you want fully-managed options.

-----------------------------------------------------------------------------------------------------------------------
Troubleshooting
-----------------------------------------------------------------------------------------------------------------------
    - If topic creation fails, try creating with CLI in the container:

        docker exec -it kafka bash
        bin/kafka-topics --create --topic trades --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092

    - If Kafka won't start: ensure kafka-storage format was run with the same cluster id and volume permissions are correct.

-----------------------------------------------------------------------------------------------------------------------
Helpful links
-----------------------------------------------------------------------------------------------------------------------

    - Kafka downloads / releases (4.0.1).
    - Kafka KRaft docs — generate cluster id & format storage.
    - Kafka Quickstart.
