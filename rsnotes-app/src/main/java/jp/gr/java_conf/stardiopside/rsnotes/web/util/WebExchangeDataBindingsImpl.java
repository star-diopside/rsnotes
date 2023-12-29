package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Component
public class WebExchangeDataBindingsImpl implements WebExchangeDataBindings {

    private final Validator validator;

    public WebExchangeDataBindingsImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public <T> Mono<T> bind(ServerRequest request, T target) {
        var binder = new WebExchangeDataBinder(target);
        return binder.bind(request.exchange())
                .then(Mono.just(target));
    }

    @Override
    public <T> Mono<Tuple2<T, BindingResult>> bindAndValidate(ServerRequest request, T target) {
        var binder = new WebExchangeDataBinder(target);
        binder.setValidator(validator);
        return binder.bind(request.exchange())
                .then(Mono.fromSupplier(() -> {
                    binder.validate();
                    var bindingResult = binder.getBindingResult();
                    return Tuples.of(target, bindingResult);
                }));
    }
}
