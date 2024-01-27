package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Objects;
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
    public <T> Mono<Result<T>> bind(ServerRequest request, T target,
                                    Consumer<WebDataBinder> dataBinderCustomizer) {
        var binder = new WebExchangeDataBinder(target);
        binder.setConversionService(conversionService);
        dataBinderCustomizer.accept(binder);
        return binder.bind(request.exchange())
                .then(Mono.fromSupplier(() -> new Result<>(target, binder.getBindingResult())));
    }

    @Override
    public <T> Mono<Result<T>> bind(ServerRequest request, Class<T> targetType,
                                    Consumer<WebDataBinder> dataBinderCustomizer) {
        var binder = new WebExchangeDataBinder(null);
        binder.setTargetType(ResolvableType.forClass(targetType));
        binder.setConversionService(conversionService);
        dataBinderCustomizer.accept(binder);
        return binder.construct(request.exchange())
                .then(binder.bind(request.exchange()))
                .then(Mono.fromSupplier(() -> new Result<>(
                        Objects.requireNonNull(targetType.cast(binder.getTarget())),
                        binder.getBindingResult())));
    }

    @Override
    public <T> Mono<Result<T>> bindAndValidate(ServerRequest request, T target,
                                               Consumer<WebDataBinder> dataBinderCustomizer) {
        var binder = new WebExchangeDataBinder(target);
        binder.setConversionService(conversionService);
        binder.setValidator(validator);
        dataBinderCustomizer.accept(binder);
        return binder.bind(request.exchange())
                .then(Mono.fromSupplier(() -> {
                    binder.validate();
                    var bindingResult = binder.getBindingResult();
                    return new Result<>(target, bindingResult);
                }));
    }

    @Override
    public <T> Mono<Result<T>> bindAndValidate(ServerRequest request, Class<T> targetType,
                                               Consumer<WebDataBinder> dataBinderCustomizer) {
        var binder = new WebExchangeDataBinder(null);
        binder.setTargetType(ResolvableType.forClass(targetType));
        binder.setConversionService(conversionService);
        binder.setValidator(validator);
        dataBinderCustomizer.accept(binder);
        return binder.construct(request.exchange())
                .then(binder.bind(request.exchange()))
                .then(Mono.fromSupplier(() -> {
                    binder.validate();
                    var target = Objects.requireNonNull(targetType.cast(binder.getTarget()));
                    var bindingResult = binder.getBindingResult();
                    return new Result<>(target, bindingResult);
                }));
    }
}
