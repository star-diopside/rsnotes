package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public final class RequestPathParser {

    private RequestPathParser() {
    }

    public static Mono<Integer> parseInt(ServerRequest request, String variableName) {
        return Mono.fromSupplier(() -> Integer.valueOf(request.pathVariable(variableName)))
                .onErrorResume(NumberFormatException.class, e -> Mono.empty());
    }

    public static Mono<Integer> parseId(ServerRequest request) {
        return parseInt(request, "id");
    }
}
