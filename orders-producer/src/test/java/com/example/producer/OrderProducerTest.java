package com.example.producer;

import com.example.avro.OrderCreated;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderProducerTest {

    @Mock
    private KafkaTemplate<String, OrderCreated> kafkaTemplate;

    @InjectMocks
    private OrderProducer orderProducer;

    @Test
    void shouldSendMessageToCorrectTopicWithCorrectKey() {
        // Given: An OrderCreated event
        OrderCreated event = OrderCreated.newBuilder()
                .setOrderId("1001")
                .setCustomerId("c-42")
                .setAmount(49.95)
                .setNote("vip")
                .build();

        // When: send() is called
        orderProducer.send(event);

        // Then: Verify message is sent to correct topic with orderId as key
        verify(kafkaTemplate).send("orders.v1", "1001", event);
    }

    @Test
    void shouldUseOrderIdAsPartitionKey() {
        // Given: An OrderCreated event with specific orderId
        OrderCreated event = OrderCreated.newBuilder()
                .setOrderId("9999")
                .setCustomerId("c-100")
                .setAmount(123.45)
                .setNote(null)
                .build();

        // When: send() is called
        orderProducer.send(event);

        // Then: Verify orderId "9999" is used as the message key for partitioning
        verify(kafkaTemplate).send("orders.v1", "9999", event);
    }
}