package com.luxoft.webflux.service;

import com.luxoft.webflux.domain.Person;
import com.luxoft.webflux.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import java.util.Random;

@Component
public class NameGenerator {
    @Autowired
    private PersonRepository personRepository;

    static String[] names = {
            "Joseph", "Alexander", "Sergey", "Vasiliy"
    };
    static Random random = new Random();

    public Flux<String> names() {
        Flux<String> stream = Flux.generate(fluxSink -> {
            String name = names[random.nextInt(names.length)];
            fluxSink.next(name);
        });
        return stream;
    }

    public Flux<Person> persons() {
        return names()
                .flatMap(s->personRepository.findByName(s)
                .defaultIfEmpty(new Person(s,"not found")));
    }


}