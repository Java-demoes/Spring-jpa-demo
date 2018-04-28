package com.jpa.demo.service;

import com.jpa.demo.entities.Customer;
import com.jpa.demo.repo.CustomerRepository;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * refer : http://www.baeldung.com/java-completablefuture
 */
@Component
@Transactional
// uses the same transaction when a two repo methods are called in a single method
// ideally should be done at method level
// class level annotation has lesser priority than method level
public class FuturesTester {

    @Autowired
    CustomerRepository repo;

    private static final Logger log = LoggerFactory.getLogger(FuturesTester.class);

    /**
     * using completetable future similar to future
     * create a CompletableFuture and pass it to consumers
     * and completre in future
     */
    public Future<String> calculateAsync() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            log.info(Thread.currentThread().getName());
            Thread.sleep(50);
            completableFuture.complete("Hello!");
            return null;
        });


        return completableFuture;
    }

    /**
     * cancelling the future
     */
    public Future<String> calculateAsyncWithCancellation() throws InterruptedException {
        CompletableFuture cf = new CompletableFuture();

        Executors.newCachedThreadPool().submit(() -> {
            log.info("cancelling the future");
            Thread.sleep(50);
            cf.cancel(false);

            return null;
        });
        return cf;
    }

    /**
     * The code above allows us to pick any mechanism of concurrent execution,
     * but what if we want to skip this boilerplate
     * and simply execute some code asynchronously?
     * SupplyAsync accepts suppler - which means it can process somethingand return some value
     * runAsync - accepts Runnable as ususual doesnt allow to return anything
     * <p>
     * in both method there is no arg passed to lambda
     * both accept executorService
     * private static ExecutorService service = Executors.newCachedThreadPool();
     */

    public Future<String> calculateUsingAsyncMethods() {
        CompletableFuture cf = CompletableFuture.runAsync(() -> {
            //doesnt return anything like sendEmail or so
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("this method is used to call some " +
                    "fn or do something which doesnt return anything");
        });

        return cf;
    }

    /**
     * using supply async we can process some data and return a result
     * supplyasync accepts a supplier - (suppliers - no input, return output)
     */
    public Future<Customer> calculateUsingSupplyAsync(String lastname) {
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            //doesnt Accept anything
            log.info("this method is used to call some function , get values process them and return a future object ");
            List<Customer> customers = repo.findByLastName(lastname);
            if (!customers.isEmpty()) {
                return customers.get(0);
            }
            return new Customer("empty", "empty");

        });

        return cf;
    }

    /*
     * Processing Results of Asynchronous Computations
     *
     * results returned by supplyAsync, can be processed and a future can be returned
     *
     * thenApply is used to apply the output of supplyAsync and do some processing and return something else
     * thenApplyAsync - accepets a fn and also executor(optional) , so this can be handled by new executor thread
     * thenApplyAsync - without executor will also be run on a different thread but thread is taken from application common pool
     * */

    public Future<Customer> getEmployeeWithLastName(String lastname) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            log.info("retrieved list of employees with matched lastname");
            return repo.findByLastName(lastname);
        }).thenApplyAsync(customers -> {
            if (!customers.isEmpty()) {
                log.info(String.format("retrieved %d customer(s)", customers.size()));
                return customers.get(0);
            } else {
                return new Customer("empty", "empty");
            }
        }, executorService);

        return cf;

    }

    /**
     * If you don’t need to return a value down the Future chain, you can use an instance of the Consumer functional interface.
     * Its single method takes a parameter and returns void.
     */

    public Future<Customer> insertEmployee(Customer customer) {

        CompletableFuture cf = CompletableFuture.supplyAsync(() ->
                //this is called  lambda expression
                repo.save(customer)
        ).thenAccept((customer_new) ->
            log.info(String.format("New Customer with name %s and  %d id added to the DB", customer_new.getFirstName() + " " + customer_new.getLastName(), customer_new.getId()))
        );
        // null will be returned becuase cf is not initialzed with any values as nothing gets returned
        return cf;
    }

    /**
     *
     * make completable futures sequential
     * */
    public Future<Customer> flipNames(String lastName){

        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            //this is called statement lambda
            log.info("customer with last name " + lastName+ "found!");
            return repo.findByLastName(lastName).get(0);
        }).thenCompose((cust) -> {
            // this method is used to make two async sequential
            return CompletableFuture.supplyAsync(() -> {
                return repo.save(new Customer(cust.getLastName(),cust.getFirstName()));
            });
        }).thenAccept( (cust) -> {
            // is a consumer and doesnt return anything,
            // thenApply can do some manipulations and return some value
            log.info("flipped the name of the customer "+((Customer)cust).toString() );
        });
        return cf;
    }

    /**
     *
     * make completable futures sequential - another way to do
     * */
    public Future<Customer> insertTwoCustomerSequentially(Customer custA , Customer custB){

        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            log.info("customer" + custA.toString() + "inserted!");
            return repo.save(custA);
        }).thenAcceptBoth(
                // this method is used to make two async sequential

                CompletableFuture.supplyAsync(() -> {
                    log.info("customer" + custB.toString() + "inserted!");
                    return repo.save(custB);
            }),(cust1, cust2) -> {
            // is a consumer and doesnt return anything,
            // thenApply can do some manipulations and return some value
            log.info(String.format("Two customers are inserted one after another %s to %s",((Customer) cust1).toString(),((Customer) cust2).toString()));
        });
        return cf;
    }

    /**
     * parallely fetch all the records and then combine the resutls
     *
     * */

    public Future<Customer> parallelFetch() throws ExecutionException, InterruptedException {
        CompletableFuture cf1 = CompletableFuture.supplyAsync(() -> {
            return repo.findAll();
        });
        List<CompletableFuture> futures = new ArrayList<>();
        for(Customer customer : (List<Customer>)cf1.get()){

            futures.add(CompletableFuture.supplyAsync(()->{
                log.info(String.format("Customer with id %d retrieved", customer.getId()));
                return repo.findById(customer.getId()).get();
            }));

        }

        CompletableFuture future[] = new CompletableFuture[futures.size()];
        CompletableFuture futureObj[] = futures.toArray(future);
        List<Customer> Combinedfutures = Stream.of(futureObj).map(CompletableFuture::join).map((f) -> (Customer)f ).collect(Collectors.toList());

        for(Customer  _future: Combinedfutures){
            log.info(_future.toString());

        }
        return null;
    }


    public CompletableFuture<List<Customer>> fetchAllUsingCustomRepo(){
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> repo.retriveAll());
        return cf;
    }


    //overiging with custom save behaviour
    public CompletableFuture<Customer> customSave(Customer customer){
        // custom repo CustomerRepoImpl's save
        return CompletableFuture.supplyAsync(() -> repo.save(customer));
    }







}
