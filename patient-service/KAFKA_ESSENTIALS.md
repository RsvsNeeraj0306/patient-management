# Kafka Setup - Essential Guide

## 🚀 Quick Start

### Docker Compose Configuration
```yaml
kafka:
  container_name: kafka
  image: bitnami/kafka:latest
  restart: unless-stopped
  environment:
      KAFKA_CFG_NODE_ID: 0
      KAFKA_CFG_PROCESS_ROLES: controller,broker
      KAFKA_CFG_CONTROLLER_QUORUM_VOTERS: 0@kafka:9093
      KAFKA_CFG_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094
      KAFKA_CFG_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094
      KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,EXTERNAL:PLAINTEXT
      KAFKA_CFG_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_CFG_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_CLUSTER_ID: my-kafka-cluster
  ports:
      - "9092:9092"
      - "9094:9094"
  networks:
    - patient-network
```

## 🔑 Key Connection Details

### From Spring Boot Applications (Internal)
```properties
spring.kafka.bootstrap-servers=kafka:9092
```

### From Host Machine (External)
```
localhost:9092
```

## ⚡ Essential Commands

### Start/Stop
```bash
# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View Kafka logs
docker-compose logs kafka
```

### Kafka Operations
```bash
# Enter Kafka container
docker-compose exec kafka bash

# List topics
kafka-topics.sh --bootstrap-server localhost:9092 --list

# Create topic
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic patient-events --partitions 3 --replication-factor 1
```

## 🎯 Spring Boot Integration

### Producer Configuration
```java
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers:kafka:9092}")
    private String bootstrapServers;
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

### Consumer Configuration
```java
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers:kafka:9092}")
    private String bootstrapServers;
    
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "patient-management-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
```

### Publishing Events
```java
@Service
public class EventPublisher {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public void publishEvent(String topic, String key, Object event) {
        kafkaTemplate.send(topic, key, event);
    }
}
```

### Consuming Events
```java
@Component
public class EventConsumer {
    @KafkaListener(topics = "patient-created", groupId = "billing-service")
    public void handlePatientCreated(PatientCreatedEvent event) {
        // Handle the event
        log.info("Processing patient: {}", event.getPatientId());
    }
}
```

## 📋 Recommended Topics for Patient Management

```
Core Topics:
├── patient-created
├── patient-updated  
├── appointment-scheduled
├── billing-processed
└── payment-received
```

## 🔧 Quick Troubleshooting

```bash
# Check if Kafka is running
docker-compose ps kafka

# View recent logs
docker-compose logs --tail=20 kafka

# Test connection
docker-compose exec patient-service telnet kafka 9092
```

## ⚠️ Important Notes

1. **Connection**: Use `kafka:9092` from containers, `localhost:9092` from host
2. **Restart Policy**: `unless-stopped` ensures auto-restart on failure
3. **Network**: All services must be on `patient-network`
4. **Topics**: Create topics before producing messages
5. **Security**: Current setup uses PLAINTEXT (development only)

---

*This is a simplified version. See `KAFKA_SETUP_GUIDE.mdx` for complete documentation.*
