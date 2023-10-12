package com.example.spring6reactivemongo.services;

import com.example.spring6reactivemongo.domain.Customer;
import com.example.spring6reactivemongo.mappers.CustomerMapper;
import com.example.spring6reactivemongo.mappers.CustomerMapperImpl;
import com.example.spring6reactivemongo.model.CustomerDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.spring6reactivemongo.services.BeerServiceImplTest.getTestBeer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceImplTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerMapper customerMapper;

    CustomerDTO customerDTO;

    void setUp() {
        customerDTO = customerMapper.customerToCustomerDto(getTestCustomer());
    }


    @Test
    @DisplayName("Test Save Customer Using Block")
    void testSaveCustomerUseBlock() {

        CustomerDTO savedDto = customerService
                .saveCustomer(Mono.just(getTestCustomerDto())).block();
        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Update Customer Using Block")
    void testUpdateBlocking() {
        final String newName = "New Customer Name"; // use final so cannot mutate
        CustomerDTO savedCustomerDto = getTestCustomerDto();
        savedCustomerDto.setCustomerName(newName);

        CustomerDTO updatedDto = customerService
                .saveCustomer(Mono.just(savedCustomerDto)).block();

        // verify exists in db

        CustomerDTO fetchedDto = customerService
                .getCustomerById(updatedDto.getId()).block();
        assertThat(fetchedDto.getCustomerName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Test Update Using Reactive Streams")
    void testUpdateStreaming() {
        final String newName = "New Customer Name"; // use final so cannot mutate

        AtomicReference<CustomerDTO> atomicDto = new AtomicReference<>();

        customerService.saveCustomer(Mono.just(getSavedCustomerDto()))
                .map(savedCustomer -> {
                    savedCustomer.setCustomerName(newName);

                    return savedCustomer;
                })
                .flatMap(customerService::saveCustomer) // save updated customer
                .flatMap(savedCustomerDto -> customerService
                        .getCustomerById(savedCustomerDto.getId()))
                .subscribe(dtFromDb -> {
                    atomicDto.set(dtFromDb);
                });

        await().until(() -> atomicDto.get() != null);
        assertThat(atomicDto.get().getCustomerName()).isEqualTo(newName);
    }

    @Test
    void testDeleteCustomer() {
        CustomerDTO customerToDelete = getSavedCustomerDto();

        customerService.deleteCustomerById(customerToDelete.getId()).block();

        Mono<CustomerDTO> expectedEmptyCustomerMono = customerService
                .getCustomerById(customerToDelete.getId());

        CustomerDTO emptyCustomer = expectedEmptyCustomerMono.block();

        assertThat(emptyCustomer).isNull();
    }

    public CustomerDTO getSavedCustomerDto() {
        return customerService.saveCustomer(Mono.just(getTestCustomerDto())).block();
    }

    private static CustomerDTO getTestCustomerDto() {
        return new CustomerMapperImpl().customerToCustomerDto(getTestCustomer());
    }

    private static Customer getTestCustomer() {
        return Customer.builder()
                .customerName("Gigel")
                .build();
    }
}