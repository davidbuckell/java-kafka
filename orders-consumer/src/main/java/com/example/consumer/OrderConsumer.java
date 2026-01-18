package com.example.consumer;

import com.example.avro.OrderCreated;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    // with specific.avro.reader: true set in application.yml the Kafka avro deserializer returns an instance of OrderCreated
    @KafkaListener(topics = "orders.v1", groupId = "orders-consumer-group")
    public void handle(OrderCreated payload) {
        System.out.printf("Consumed: id=%s customer=%s amount=%.2f note=%s%n",
                payload.getOrderId(), payload.getCustomerId(), payload.getAmount(), payload.getNote());
    }
}
// shouldn't have to commit message to update offset?