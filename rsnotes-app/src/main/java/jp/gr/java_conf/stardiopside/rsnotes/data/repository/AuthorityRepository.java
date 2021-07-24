package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Authority;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface AuthorityRepository extends ReactiveSortingRepository<Authority, Integer> {

    Flux<Authority> findByUserId(Integer userId);

}
