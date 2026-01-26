package com.example.consumer;

import com.example.avro.OrderCreated;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    private OrderConsumer orderConsumer;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        orderConsumer = new OrderConsumer();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldConsumeAndProcessOrderCreatedEvent() {
        // Given: An OrderCreated event
        OrderCreated event = OrderCreated.newBuilder()
                .setOrderId("1001")
                .setCustomerId("c-42")
                .setAmount(49.95)
                .setNote("vip")
                .build();

        // When: Consumer handles the event
        orderConsumer.handle(event);

        // Then: Verify the message was processed and logged
        String output = outputStreamCaptor.toString().trim();
        assertThat(output).contains("Consumed:");
        assertThat(output).contains("id=1001");
        assertThat(output).contains("customer=c-42");
        assertThat(output).contains("amount=49.95");
        assertThat(output).contains("note=vip");
    }

    @Test
    void shouldHandleEventWithNullNote() {
        // Given: An OrderCreated event with null note
        OrderCreated event = OrderCreated.newBuilder()
                .setOrderId("1002")
                .setCustomerId("c-100")
                .setAmount(99.99)
                .setNote(null)
                .build();

        // When: Consumer handles the event
        orderConsumer.handle(event);

        // Then: Verify the message was processed with null note
        String output = outputStreamCaptor.toString().trim();
        assertThat(output).contains("Consumed:");
        assertThat(output).contains("id=1002");
        assertThat(output).contains("customer=c-100");
        assertThat(output).contains("amount=99.99");
        assertThat(output).contains("note=null");
    }

    @Test
    void shouldProcessMultipleMessages() {
        // Given: Multiple OrderCreated events
        OrderCreated event1 = OrderCreated.newBuilder()
                .setOrderId("1001")
                .setCustomerId("c-1")
                .setAmount(10.00)
                .setNote("first")
                .build();

        OrderCreated event2 = OrderCreated.newBuilder()
                .setOrderId("1002")
                .setCustomerId("c-2")
                .setAmount(20.00)
                .setNote("second")
                .build();

        // When: Consumer handles multiple events
        orderConsumer.handle(event1);
        orderConsumer.handle(event2);

        // Then: Verify both messages were processed
        String output = outputStreamCaptor.toString();
        assertThat(output).contains("id=1001");
        assertThat(output).contains("id=1002");
        assertThat(output).contains("note=first");
        assertThat(output).contains("note=second");
    }
}