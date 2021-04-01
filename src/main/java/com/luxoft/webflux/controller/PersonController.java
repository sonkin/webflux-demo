package com.luxoft.webflux.controller;

import com.luxoft.webflux.domain.Person;
import com.luxoft.webflux.repo.PersonRepository;
import com.luxoft.webflux.service.NameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonRepository personRepository;
    private final NameGenerator nameGenerator;

    @Autowired
    public PersonController(PersonRepository personRepository,
                            NameGenerator nameGenerator) {
        this.personRepository = personRepository;
        this.nameGenerator = nameGenerator;
    }

    @GetMapping
    public Flux<Person> list(@RequestParam(defaultValue = "0") Long start,
                             @RequestParam(defaultValue = "3") Long count) {
        return personRepository.findAll()
                .skip(start).take(count);
    }

    // find person for each name
    @GetMapping(path = "/streamPerson", produces = "application/stream+json")
    public Flux<Person> getStreamPerson() {
        return getStream()
                .flatMap(s->personRepository.findByName(s)
                        .defaultIfEmpty(new Person(s,"not found")));
    }

    // find person for each name
    @GetMapping(path = "/streamPersonString")
    public Flux<String> getStreamPersonString() {
        return getStream()
                .flatMap(s->personRepository.findByName(s)
                        .defaultIfEmpty(new Person(s,"not found")))
                .map(Person::toString);
    }

    // find person for each name
    @GetMapping(path = "/ssePerson", produces = "text/event-stream")
    public Flux<Person> getSSEPerson() {
        return getStream()
                .flatMap(s->personRepository.findByName(s)
                        .defaultIfEmpty(new Person(s,"not found")));
    }

    // find person for each name
    @GetMapping(path = "/buffered", produces = "application/stream+json")
    public Flux<List<Person>> getStreamPersonBuffered() {
        return getStream()
                .flatMap(s->personRepository.findByName(s)
                        .defaultIfEmpty(new Person(s,"not found")))
                .buffer(2);
    }

    @GetMapping(path = "/stream")
    public Flux<String> getStream() {
        return Flux
                .just("Sergey","Alexander","Michail", "Joseph", "Nikolay")
                .delayElements(Duration.ofSeconds(2));
    }

    @GetMapping("/names")
    public Flux<String> names(@RequestParam(defaultValue = "0") Long start,
                             @RequestParam(defaultValue = "3") Long count) {
        return personRepository
                .findAll()
                .skip(start)
                .map(p->p.getName()+" ");
    }

    @GetMapping(path = "/persons")
    public Flux<Person> persons() {
        return nameGenerator.persons()
                .delayElements(Duration.ofSeconds(1))
                .take(Duration.ofSeconds(10));
    }

    @GetMapping(path = "/stats")
    public Mono<Map<String, Long>> stats() {
        return nameGenerator
                .persons()
                .take(100)
                .groupBy(Person::getName)
                .flatMap(group -> Mono.zip(
                        Mono.just(group.key()),
                        group.count()))
                .collectMap(Tuple2::getT1, Tuple2::getT2);
    }

    @PostMapping
    public Mono<Person> add(@RequestBody Person person) {
        return personRepository.save(person);
    }
}
