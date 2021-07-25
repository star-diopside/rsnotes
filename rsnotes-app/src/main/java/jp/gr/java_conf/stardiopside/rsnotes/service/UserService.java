package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> create(String username, String rawPassword, String... roles);

}
