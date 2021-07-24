package jp.gr.java_conf.stardiopside.rsnotes.service;

import reactor.core.publisher.Mono;

public interface UserService {

    Mono<Void> create(String username, String rawPassword, String... roles);

}
