package jp.gr.java_conf.stardiopside.rsnotes.handler;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import jp.gr.java_conf.stardiopside.rsnotes.service.Around;
import jp.gr.java_conf.stardiopside.rsnotes.service.TodoService;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.net.URI;
import java.util.Map;
import java.util.OptionalInt;

@Component
public class TodosHandler {

    private final TodoService todoService;
    private final Validator validator;
    private final MessageSource messageSource;

    public TodosHandler(TodoService todoService, Validator validator, MessageSource messageSource) {
        this.todoService = todoService;
        this.validator = validator;
        this.messageSource = messageSource;
    }

    public Mono<ServerResponse> index(ServerRequest request) {
        var messageSuccess = request.session()
                .flatMap(session -> Mono.justOrEmpty(session.getAttributes().remove("messages.success")))
                .map(Object::toString);
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                .render("todos/index",
                        Map.of("todos", todoService.list(),
                                "success", messageSuccess));
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        var messageSuccess = request.session()
                .flatMap(session -> Mono.justOrEmpty(session.getAttributes().remove("messages.success")))
                .map(Object::toString);
        return parseId(request)
                .flatMap(todoService::findWithAround)
                .flatMap(todo -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                        .render("todos/show",
                                Map.of("todo", todo.getItem(),
                                        "prev", todo.getPrev(),
                                        "next", todo.getNext(),
                                        "success", messageSuccess)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                .render("todos/create", new Todo());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return bindAndValidate(request, new Todo())
                .flatMap(tuple -> save(request, tuple.getT1(), tuple.getT2(),
                        "todos/create", "messages.success-create"));
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        return parseId(request)
                .flatMap(todoService::findWithAround)
                .flatMap(todo -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                        .render("todos/edit",
                                Map.of("todo", todo.getItem(),
                                        "prev", todo.getPrev(),
                                        "next", todo.getNext())))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return parseId(request)
                .flatMap(todoService::find)
                .flatMap(todo -> bindAndValidate(request, todo))
                .flatMap(tuple -> save(request, tuple.getT1(), tuple.getT2(),
                        "todos/edit", "messages.success-update"))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        var messages = new MessageSourceAccessor(messageSource,
                request.exchange().getLocaleContext().getLocale());
        return parseId(request)
                .flatMap(i -> todoService.delete(i)
                        .doOnSuccess(v -> request.session().subscribe(session ->
                                session.getAttributes().put("messages.success",
                                        messages.getMessage("messages.success-delete"))))
                        .then(Mono.defer(() -> ServerResponse.seeOther(URI.create("/todos")).build())))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    private Mono<Integer> parseId(ServerRequest request) {
        return Mono.fromSupplier(() -> Integer.valueOf(request.pathVariable("id")))
                .onErrorResume(NumberFormatException.class, e -> Mono.empty());
    }

    private Mono<Tuple2<Todo, BindingResult>> bindAndValidate(ServerRequest request, Todo todo) {
        var binder = new WebExchangeDataBinder(todo);
        binder.setValidator(validator);
        return binder.bind(request.exchange())
                .then(Mono.fromSupplier(() -> {
                    binder.validate();
                    var bindingResult = binder.getBindingResult();
                    return Tuples.of(todo, bindingResult);
                }));
    }

    private Mono<ServerResponse> save(ServerRequest request, Todo todo, BindingResult bindingResult,
                                      String errorRender, String successMessage) {
        if (bindingResult.hasErrors()) {
            return Mono.justOrEmpty(todo.getId())
                    .flatMap(todoService::findAround)
                    .defaultIfEmpty(new Around<>(OptionalInt.empty(), OptionalInt.empty()))
                    .flatMap(a -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                            .render(errorRender,
                                    Map.of("todo", todo,
                                            BindingResult.MODEL_KEY_PREFIX + "todo", bindingResult,
                                            "prev", a.getPrev(),
                                            "next", a.getNext())));
        }

        var messages = new MessageSourceAccessor(messageSource,
                request.exchange().getLocaleContext().getLocale());

        return todoService.save(todo)
                .doOnNext(t -> request.session().subscribe(session ->
                        session.getAttributes().put("messages.success",
                                messages.getMessage(successMessage))))
                .flatMap(t -> ServerResponse.seeOther(UriComponentsBuilder
                        .fromUriString("/todos/{id}").build(t.getId())).build());
    }
}
