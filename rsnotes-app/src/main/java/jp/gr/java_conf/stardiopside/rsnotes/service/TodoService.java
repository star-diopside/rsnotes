package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.OptionalInt;

public interface TodoService {

    Flux<Todo> list();

    Mono<Node<Todo, OptionalInt>> find(Integer id);

    Mono<Todo> save(Todo todo);

}
