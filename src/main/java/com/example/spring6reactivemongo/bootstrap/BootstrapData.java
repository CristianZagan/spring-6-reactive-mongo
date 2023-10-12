package com.example.spring6reactivemongo.bootstrap;

import com.example.spring6reactivemongo.domain.Beer;
import com.example.spring6reactivemongo.domain.Customer;
import com.example.spring6reactivemongo.repositories.BeerRepository;
import com.example.spring6reactivemongo.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;



@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        beerRepository.deleteAll()
                .doOnSuccess(success -> {
                    loadBeerData();
                })
                .subscribe();

        customerRepository.deleteAll()
                .doOnSuccess(success -> {
                    loadCustomerDate();
                }).subscribe();
    }

    private void loadBeerData() {

        beerRepository.count().subscribe(count -> {
            if (count == 0) {
                Beer beer1 = Beer.builder()
                        .beerName("Palosul lui Stefan")
                        .beerStyle("IPA")
                        .upc("123456")
                        .price(new BigDecimal("7.99"))
                        .quantityOnHand(155)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                Beer beer2 = Beer.builder()
                        .beerName("Teapa lui Vlad")
                        .beerStyle("Pilner")
                        .upc("123343")
                        .price(new BigDecimal("6.99"))
                        .quantityOnHand(200)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                Beer beer3 = Beer.builder()
                        .beerName("Cosmarul lui Baiazid")
                        .beerStyle("Lagger")
                        .upc("12441")
                        .price(new BigDecimal("5.99"))
                        .quantityOnHand(250)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                beerRepository.save(beer1).subscribe();
                beerRepository.save(beer2).subscribe();
                beerRepository.save(beer3).subscribe();
            }
        });
    }

    private void loadCustomerDate() {
        customerRepository.count().subscribe(count -> {
            if (count == 0) {
                Customer customer1 = Customer.builder()
                        .customerName("Viorel")
                        .build();


                Customer customer2 = Customer.builder()
                        .customerName("Dorel")
                        .build();


                Customer customer3 = Customer.builder()
                        .customerName("Marcel")
                        .build();


                customerRepository.save(customer1).subscribe();
                customerRepository.save(customer2).subscribe();
                customerRepository.save(customer3).subscribe();
            }
        });
    }
}
