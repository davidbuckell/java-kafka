package com.example.producer;

import com.example.avro.OrderCreated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderProducer producer;
    public OrderController(OrderProducer producer) { this.producer = producer; }

    @PostMapping
    public String create(@RequestParam String id,
                         @RequestParam String customerId,
                         @RequestParam double amount,
                         @RequestParam(required = false) String note) {

        OrderCreated event = OrderCreated.newBuilder()  // appears to be a way of creating a class and setting properties
                .setOrderId(id)
                .setCustomerId(customerId)
                .setAmount(amount)
                .setNote(note)
                .build();

        producer.send(event);
        return "OK";
    }
}