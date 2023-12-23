package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface TodoRepository extends R2dbcRepository<Todo, Integer> {

    <T> Flux<T> findByIdLessThan(Integer id, Pageable pageable, Class<T> type);

    <T> Flux<T> findByIdGreaterThan(Integer id, Pageable pageable, Class<T> type);

}
