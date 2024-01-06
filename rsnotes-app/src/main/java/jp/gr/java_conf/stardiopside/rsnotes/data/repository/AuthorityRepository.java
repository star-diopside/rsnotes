package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Authority;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface AuthorityRepository extends R2dbcRepository<Authority, Long> {

    Flux<Authority> findByUserId(Long userId);

}
