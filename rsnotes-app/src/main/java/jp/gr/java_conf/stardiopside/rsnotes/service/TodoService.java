package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.OptionalLong;

public interface TodoService {

    Flux<Todo> list();

    Mono<Todo> find(Long id);

    Mono<Around<OptionalLong>> findAround(Long id);

    Mono<Node<Todo, OptionalLong>> findWithAround(Long id);

    Mono<Todo> save(Todo todo);

    Mono<Void> delete(Todo todo);

}
