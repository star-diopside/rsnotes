package jp.gr.java_conf.stardiopside.rsnotes.handler;

import jp.gr.java_conf.stardiopside.rsnotes.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UsersHandler {

    private final UserService userService;

    public UsersHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> index(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                .render("users/index",
                        Map.of("users", userService.list()));
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        throw new UnsupportedOperationException();
    }
}
