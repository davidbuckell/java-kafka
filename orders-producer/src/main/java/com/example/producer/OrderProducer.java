package com.example.producer;

import com.example.avro.OrderCreated;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    // The Confluent KafkaAvroSerializer registers the value schema under orders.v1-value and includes only the schema ID + binary Avro payload on the wire
    private static final String TOPIC = "orders.v1";
    private final KafkaTemplate<String, OrderCreated> template;

    // Spring Kafka supports KafkaTemplate which creates a producer
    public OrderProducer(KafkaTemplate<String, OrderCreated> template) {
        this.template = template;
    }

    public void send(OrderCreated evt) {
        // key = orderId for partitioning
        template.send(TOPIC, evt.getOrderId().toString(), evt);
    }
}
