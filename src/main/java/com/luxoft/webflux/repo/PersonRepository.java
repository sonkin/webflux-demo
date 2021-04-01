package com.luxoft.webflux.repo;

import com.luxoft.webflux.domain.Person;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface PersonRepository extends R2dbcRepository<Person, Long> {

    Flux<Person> findByName(String name);
}
