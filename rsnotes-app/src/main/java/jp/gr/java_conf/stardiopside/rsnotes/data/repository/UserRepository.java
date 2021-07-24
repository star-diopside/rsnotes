package jp.gr.java_conf.stardiopside.rsnotes.data.repository;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.User;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveSortingRepository<User, Integer> {

    Flux<User> findByUsernameAndEnabledIsTrue(String username);

}
