package com.luxoft.webflux.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.BaseStream;

@Configuration
public class SchemaLoader {

    private Mono<String> getSchema() throws URISyntaxException {
        Path path = Paths.get(ClassLoader
                .getSystemResource("schema.sql")
                .toURI());

        return Flux.using(() -> Files.lines(path),
                        Flux::fromStream, BaseStream::close)
                .reduce((line1, line2) -> line1 + "\n" + line2);
    }

    private Mono<Integer> executeSql(DatabaseClient client, String sql) {
        return client.sql(sql).fetch().rowsUpdated();
    }

    @Bean
    public ApplicationRunner seeder(DatabaseClient client) {
        return args -> getSchema()
                .flatMap(sql -> executeSql(client, sql))
                .subscribe(count -> System.out.println("Schema created"));
    }

}
