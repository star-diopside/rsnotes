package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface TodoRepository extends R2dbcRepository<Todo, Long> {

    <T> Mono<T> findFirstByIdLessThanOrderByIdDesc(Long id, Class<T> type);

    <T> Mono<T> findFirstByIdGreaterThanOrderByIdAsc(Long id, Class<T> type);

}
