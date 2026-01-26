package com.example.producer;

import com.example.avro.OrderCreated;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderProducer orderProducer;

    @Test
    void shouldCreateOrderAndSendToKafka() throws Exception {
        // When: POST request with order parameters
        mockMvc.perform(post("/orders")
                        .param("orderId", "1001")
                        .param("customerId", "c-42")
                        .param("amount", "49.95")
                        .param("note", "vip"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // Then: Verify OrderProducer.send() was called with correct OrderCreated event
        verify(orderProducer).send(argThat(event ->
                event.getOrderId().toString().equals("1001") &&
                        event.getCustomerId().toString().equals("c-42") &&
                        event.getAmount() == 49.95 &&
                        event.getNote().toString().equals("vip")
        ));
    }

    @Test
    void shouldCreateOrderWithoutOptionalNote() throws Exception {
        // When: POST request without optional note parameter
        mockMvc.perform(post("/orders")
                        .param("orderId", "1002")
                        .param("customerId", "c-100")
                        .param("amount", "99.99"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // Then: Verify OrderProducer.send() was called with null note
        verify(orderProducer).send(argThat(event ->
                event.getOrderId().toString().equals("1002") &&
                        event.getCustomerId().toString().equals("c-100") &&
                        event.getAmount() == 99.99 &&
                        event.getNote() == null
        ));
    }

    @Test
    void shouldReturnBadRequestWhenMandatoryParameterMissing() throws Exception {
        // When: POST request missing mandatory orderId parameter
        mockMvc.perform(post("/orders")
                        .param("customerId", "c-42")
                        .param("amount", "49.95"))
                .andExpect(status().isBadRequest());
    }
}