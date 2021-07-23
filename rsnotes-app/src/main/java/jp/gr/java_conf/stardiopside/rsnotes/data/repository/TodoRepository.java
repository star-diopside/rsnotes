package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface TodoRepository extends ReactiveSortingRepository<Todo, Integer> {

    <T> Flux<T> findByIdLessThan(Integer id, Pageable pageable, Class<T> type);

    <T> Flux<T> findByIdGreaterThan(Integer id, Pageable pageable, Class<T> type);

}
