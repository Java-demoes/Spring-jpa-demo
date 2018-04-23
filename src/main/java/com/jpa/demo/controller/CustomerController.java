package com.jpa.demo.controller;

import com.jpa.demo.repo.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.jpa.demo.entities.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@RestController
@RequestMapping(value = "/api")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository repo;


    @RequestMapping("/getAll")
    public ResponseEntity<List<Customer>> getAllCustomers() throws ExecutionException, InterruptedException {
        CompletableFuture cf = new CompletableFuture<>();
        log.info("invoked getAll");
        cf.supplyAsync(() -> {
            List<Customer> customers = (List) repo.findAll();
            log.info("done fetching");
            return cf.complete(customers);
        });
        log.info("completed get all");
        return ResponseEntity.ok((List<Customer>) cf.get());
    }

    @RequestMapping(value = "/insertCustomer", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<Customer> insertCustomer(@RequestBody Customer customer) throws ExecutionException, InterruptedException {
        CompletableFuture cf = new CompletableFuture();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        log.info("invoked insertCustomer");
        cf.supplyAsync(() -> {
            Customer newCust = repo.save(customer);
            Optional<Customer> retrievedCust = repo.findById(newCust.getId());
            if (retrievedCust.isPresent()) {
                log.info("cust retrieved");
                cf.complete(retrievedCust.get());
            }
            return new Customer("empty", "empty");
        }, executor);
        executor.shutdown();
        log.info("completed get all");
        return ResponseEntity.ok((Customer) cf.get());
    }

    @RequestMapping(value = "/getFromList", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<List<Customer>> fetchAllCustomersFromList(@RequestBody List<Long> Ids) {

        CompletableFuture response = new CompletableFuture();
        List<CompletableFuture> futures = new ArrayList<>();
        for (Long id : Ids) {

            futures.add(new CompletableFuture().supplyAsync(() -> {
                Optional<Customer> customer = repo.findById(id);
                if (customer.isPresent()) {
                    return customer.get();
                } else {
                    return new Customer("empty", "empty");
                }

            }));
        }

       // List<Customer> customers = Stream.of(futures.toArray()).map((future) -> {}).collect(Collectors.toList());


        return null;
    }


}
