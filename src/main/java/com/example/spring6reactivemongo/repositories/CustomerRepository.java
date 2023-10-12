package com.example.spring6reactivemongo.repositories;

import com.example.spring6reactivemongo.domain.Customer;
import com.example.spring6reactivemongo.model.CustomerDTO;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

}
