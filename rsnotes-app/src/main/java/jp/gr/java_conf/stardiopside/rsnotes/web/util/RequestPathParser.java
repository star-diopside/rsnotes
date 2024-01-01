package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public final class RequestPathParser {

    private RequestPathParser() {
    }

    public static Mono<Long> parseLong(ServerRequest request, String variableName) {
        return Mono.fromSupplier(() -> Long.valueOf(request.pathVariable(variableName)))
                .onErrorResume(NumberFormatException.class, e -> Mono.empty());
    }

    public static Mono<Long> parseId(ServerRequest request) {
        return parseLong(request, "id");
    }
}
