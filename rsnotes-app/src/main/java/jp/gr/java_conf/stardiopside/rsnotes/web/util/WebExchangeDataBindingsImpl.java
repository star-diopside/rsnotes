package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Component
public class WebExchangeDataBindingsImpl implements WebExchangeDataBindings {

    private final ConversionService conversionService;
    private final Validator validator;

    public WebExchangeDataBindingsImpl(ConversionService conversionService, Validator validator) {
        this.conversionService = conversionService;
        this.validator = validator;
    }

    @Override
    public <T> Mono<T> bind(ServerRequest request, T target) {
        var binder = new WebExchangeDataBinder(target);
        return binder.bind(request.exchange())
                .then(Mono.just(target));
    }

    @Override
    public <T> Mono<Result<T>> bindAndValidate(ServerRequest request, T target,
                                               Consumer<WebDataBinder> initBinder) {
        var binder = new WebExchangeDataBinder(target);
        binder.setConversionService(conversionService);
        binder.setValidator(validator);
        initBinder.accept(binder);
        return binder.bind(request.exchange())
                .then(Mono.fromSupplier(() -> {
                    binder.validate();
                    var bindingResult = binder.getBindingResult();
                    return new Result<>(target, bindingResult);
                }));
    }
}
