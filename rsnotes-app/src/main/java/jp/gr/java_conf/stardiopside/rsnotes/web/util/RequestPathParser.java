package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Slf4j
public final class RequestPathParser {

    private RequestPathParser() {
    }

    public static Mono<Long> parseLong(ServerRequest request, String variableName) {
        return parseNumber(request, () -> Long.valueOf(request.pathVariable(variableName)));
    }

    private static <T extends Number> Mono<T> parseNumber(ServerRequest request, Supplier<T> supplier) {
        return Mono.fromSupplier(supplier)
                .onErrorResume(NumberFormatException.class, e -> {
                    log.atDebug().setCause(e)
                            .log(() -> request.exchange().getLogPrefix() + e.getMessage());
                    return Mono.empty();
                });
    }

    public static Mono<Long> parseId(ServerRequest request) {
        return parseLong(request, "id");
    }
}
