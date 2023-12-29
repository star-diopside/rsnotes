package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface WebExchangeDataBindings {

    <T> Mono<T> bind(ServerRequest request, T target);

    <T> Mono<Tuple2<T, BindingResult>> bindAndValidate(ServerRequest request, T target);

}
