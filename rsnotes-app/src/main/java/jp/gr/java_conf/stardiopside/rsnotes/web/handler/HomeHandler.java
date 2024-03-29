package jp.gr.java_conf.stardiopside.rsnotes.web.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class HomeHandler {

    public Mono<ServerResponse> index(ServerRequest request) {
        return ServerResponse.ok().render("home/index");
    }
}
