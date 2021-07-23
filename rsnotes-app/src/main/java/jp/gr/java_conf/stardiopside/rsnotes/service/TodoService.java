package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.OptionalInt;

public interface TodoService {

    Flux<Todo> list();

    Mono<Todo> find(Integer id);

    Mono<Around<OptionalInt>> findAround(Integer id);

    Mono<Node<Todo, OptionalInt>> findWithAround(Integer id);

    Mono<Todo> save(Todo todo);

    Mono<Void> delete(Integer id);

}
