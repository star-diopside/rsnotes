package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public interface WebExchangeDataBindings {

    <T> Mono<T> bind(ServerRequest request, T target);

    <T> Mono<Result<T>> bindAndValidate(ServerRequest request, T target);

    public record Result<T>(T target, BindingResult bindingResult) {
    }
}
