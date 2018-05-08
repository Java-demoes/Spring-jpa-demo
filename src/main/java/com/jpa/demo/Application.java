package com.jpa.demo;

import com.jpa.demo.DTO.CustomerProjectionDTO;
import com.jpa.demo.SPECS.CustomerSpecs;
import com.jpa.demo.entities.Address;
import com.jpa.demo.entities.Customer;
import com.jpa.demo.jpaProjections.CustomerProjection;
import com.jpa.demo.repo.CustomerRepository;
import com.jpa.demo.service.FuturesTester;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.slf4j.Logger;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.springframework.data.jpa.domain.Specification.where;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@EnableAsync
@EnableJpaRepositories(basePackages = "com.jpa.demo.repo")
@EnableTransactionManagement
public class Application {
    private static final Logger log = LoggerFactory.getLogger("Application.class");

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner demo(CustomerRepository repo, FuturesTester tester) {
        return (args -> {
            repo.save(new Customer("Preetham", "Salehundam", new Address("AP", "India")));
            repo.save(new Customer("Tarun", "Madugula", new Address("telengana", "India")));
            repo.save(new Customer("Amit", "Das", new Address("Odissa", "India")));
            repo.save(new Customer("Naveen", "Ravi", new Address("Shimoga", "India")));
            log.info("inserted customers");

            log.info("--------------------------------------------");
            repo.findByFirstAndLastName("Preetham", "salehundam").ifPresent((customer) -> log.info("customer retrieved" + customer.toString()));
            log.info("--------------------------------------------");

            //added custom query
            List<Customer> resultList = repo.findByFirstNameContaining("t").get();

            resultList.forEach(System.out::println);


            // -- error occurs here--- should I enable transaction management?? --added a new property in prop file to enable lazyload//
            Optional<Customer> cust1 = repo.findById(1L);

            if (cust1.isPresent()) {
                log.info("address1" + cust1.get().getAddress());
            }
            // -- error occurs here--- //


            // using projection to pull
            //closed projection used
            repo.findByFirstName("Preetham").forEach((customer_p) -> log.info("Customer projection with firstName alone" + customer_p.getFirstName()));
            // open projection used
            repo.findByFirstNameAndLastName("Preetham", "salehundam").forEach((customer_p) -> log.info("Customer projection with fullName alone" + customer_p.getFullName()));
            // class based DTO projection
            repo.findByFirstNameAndLastNameIgnoreCase("Preetham", "SALEHUNDAM").forEach((customer_p) -> log.info("CustomerDTO projection with fullName alone" + customer_p.toString()));

            // ------//
            log.info("************");
            repo.findAll(CustomerSpecs.NameContains).forEach(System.out::println);
            repo.findAll(CustomerSpecs.NameDoesntContain).forEach(System.out::println);
            repo.findAll(where(CustomerSpecs.NameContains).or(CustomerSpecs.NameContainsR)).forEach(System.out::println);
            log.info("************");


            log.info("using EXAMPLE API");
            Customer exampleCust = new Customer("ree", "dam");
            ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
            Example<Customer> example = Example.of(exampleCust, matcher);
            repo.findAll(example).forEach(System.out::println);
            log.info("End of example API");


            Future<String> result = tester.calculateAsync();

            log.info((String) result.get());

            try {
                Future<String> result2 = tester.calculateAsyncWithCancellation();
                //Assert.assertEquals("Hello!",);
                log.info((String) result2.get());
            } catch (CancellationException ce) {
                log.error("error in CE", ce);
            } catch (Exception e) {
                log.error("Exception", e);
            }

            Future<String> result3 = tester.calculateUsingAsyncMethods();
            log.info("using runAsync" + (String) result3.get());


            // retrieve employee
            Future<Customer> result4 = tester.calculateUsingSupplyAsync("salehundam");
            log.info("employee with lastname retrieved " + ((Customer) result4.get()).toString());


            //processing the results obtained from supplyasync usig then apply
            Future<Customer> result5 = tester.getEmployeeWithLastName("Das");
            log.info(String.format("employee with lastname %s retrieved ", ((Customer) result5.get()).toString()));

            // inserting a new customer

            Future<Customer> result6 = tester.insertEmployee(new Customer("Jahnavi", "Dirisina"));
            Customer cust = Optional.ofNullable(result6.get()).orElse(new Customer("empty", "empty"));
            // this prints empty as name, becuase the result from .get() method is null, as we have
            // used thenapply which deosnt return anything
            log.info(String.format("employee with name %s inserted  ", cust.getFirstName()));


            //sequentially execute two completable futures where ones output is others input
            Future<Customer> result7 = tester.flipNames("salehundam");

            // insert two customer one after one
            Future<Customer> result8 = tester.insertTwoCustomerSequentially(new Customer("abhishek", "srinath"), new Customer("Guru", "Thejus"));

            // doing parallel jobs and combine all of them atlast
            Future<Customer> result9 = tester.parallelFetch();

            // using a custom jpa repo implementation
            CompletableFuture<List<Customer>> result10 = tester.fetchAllUsingCustomRepo();

            result10.get().forEach(System.out::println);

            //overiding with custom save behaviour
            CompletableFuture<Customer> resutl11 = tester.customSave(new Customer("Namitha", ""));
            log.info(((Customer) resutl11.get()).toString());

            RestTemplate template = new RestTemplate();
            JSONObject props = new JSONObject(template.getForObject("http://127.0.0.1:8089/props",String.class));
            System.out.println(props.toString());


        });
    }

}