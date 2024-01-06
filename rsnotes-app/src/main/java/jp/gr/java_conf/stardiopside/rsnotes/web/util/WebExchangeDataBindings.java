package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public interface WebExchangeDataBindings {

    default <T> Mono<Result<T>> bind(ServerRequest request, T target) {
        return bind(request, target, binder -> {
        });
    }

    <T> Mono<Result<T>> bind(ServerRequest request, T target,
                             Consumer<WebDataBinder> dataBinderCustomizer);

    default <T> Mono<Result<T>> bind(ServerRequest request, Class<T> targetType) {
        return bind(request, targetType, binder -> {
        });
    }

    <T> Mono<Result<T>> bind(ServerRequest request, Class<T> targetType,
                             Consumer<WebDataBinder> dataBinderCustomizer);

    default <T> Mono<Result<T>> bindAndValidate(ServerRequest request, T target) {
        return bindAndValidate(request, target, binder -> {
        });
    }

    <T> Mono<Result<T>> bindAndValidate(ServerRequest request, T target,
                                        Consumer<WebDataBinder> dataBinderCustomizer);

    default <T> Mono<Result<T>> bindAndValidate(ServerRequest request, Class<T> targetType) {
        return bindAndValidate(request, targetType, binder -> {
        });
    }

    <T> Mono<Result<T>> bindAndValidate(ServerRequest request, Class<T> targetType,
                                        Consumer<WebDataBinder> dataBinderCustomizer);

    record Result<T>(T target, BindingResult bindingResult) {
        public Mono<T> getOrError() {
            return bindingResult.hasErrors()
                    ? Mono.error(new BindException(bindingResult))
                    : Mono.justOrEmpty(target);
        }
    }
}
