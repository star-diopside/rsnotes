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
    public Mono<Todo> find(Integer id) {
        return todoRepository.findById(id);
    }

    @Override
    public Mono<Around<OptionalInt>> findAround(Integer id) {
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
        return Mono.zip(prev, next)
                .map(t -> new Around<>(t.getT1(), t.getT2()));
    }

    @Override
    public Mono<Node<Todo, OptionalInt>> findWithAround(Integer id) {
        var todo = find(id);
        var around = findAround(id);
        return Mono.zip(todo, around)
                .map(t -> new Node<>(t.getT1(), t.getT2().getPrev(), t.getT2().getNext()));
    }

    @Override
    public Mono<Todo> save(Todo todo) {
        return todoRepository.save(todo);
    }

    @Override
    public Mono<Void> delete(Integer id) {
        return todoRepository.deleteById(id);
    }
}
