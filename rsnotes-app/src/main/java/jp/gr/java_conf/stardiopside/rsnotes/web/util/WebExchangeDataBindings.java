package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public interface WebExchangeDataBindings {

    <T> Mono<T> bind(ServerRequest request, T target);

    default <T> Mono<Result<T>> bindAndValidate(ServerRequest request, T target) {
        return bindAndValidate(request, target, binder -> {
        });
    }

    <T> Mono<Result<T>> bindAndValidate(ServerRequest request, T target,
                                        Consumer<WebDataBinder> initBinder);

    record Result<T>(T target, BindingResult bindingResult) {
    }
}
