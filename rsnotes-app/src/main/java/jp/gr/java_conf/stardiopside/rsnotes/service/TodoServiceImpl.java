package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.TodoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.OptionalInt;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public Flux<Todo> list() {
        return todoRepository.findAll(Sort.by("id").ascending());
    }

    @Override
    public Mono<Node<Todo, OptionalInt>> find(int id) {
        var todo = todoRepository.findById(id);
        var prev = todoRepository
                .findByIdLessThan(id, PageRequest.of(0, 1, Sort.by("id").descending()), IdOnly.class)
                .next()
                .map(t -> OptionalInt.of(t.getId()))
                .defaultIfEmpty(OptionalInt.empty());
        var next = todoRepository
                .findByIdGreaterThan(id, PageRequest.of(0, 1, Sort.by("id").ascending()), IdOnly.class)
                .next()
                .map(t -> OptionalInt.of(t.getId()))
                .defaultIfEmpty(OptionalInt.empty());
        return Mono.zip(todo, prev, next)
                .map(t -> new Node<>(t.getT1(), t.getT2(), t.getT3()));
    }

    @Override
    public Mono<Todo> save(Todo todo) {
        return todoRepository.save(todo);
    }
}
