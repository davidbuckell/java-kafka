package com.example.producer;

import com.example.avro.OrderCreated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderProducer producer;
    public OrderController(OrderProducer producer) { this.producer = producer; }

    @PostMapping
    public String create(@RequestParam(name = "orderId") String orderId,
                         @RequestParam(name = "customerId") String customerId,
                         @RequestParam(name = "amount") double amount,
                         @RequestParam(name = "note", required = false) String note) {

        OrderCreated event = OrderCreated.newBuilder()  // appears to be a way of creating a class and setting properties
                .setOrderId(orderId)
                .setCustomerId(customerId)
                .setAmount(amount)
                .setNote(note)
                .build();

        producer.send(event);
        return "OK";
    }
}