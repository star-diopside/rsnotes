package jp.gr.java_conf.stardiopside.rsnotes.web.handler;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import jp.gr.java_conf.stardiopside.rsnotes.service.Around;
import jp.gr.java_conf.stardiopside.rsnotes.service.TodoService;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.RequestPathParser;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.WebExchangeDataBindings;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

@Component
public class TodosHandler {

    private final TodoService todoService;
    private final WebExchangeDataBindings webExchangeDataBindings;
    private final MessageSource messageSource;

    public TodosHandler(TodoService todoService, WebExchangeDataBindings webExchangeDataBindings,
                        MessageSource messageSource) {
        this.todoService = todoService;
        this.webExchangeDataBindings = webExchangeDataBindings;
        this.messageSource = messageSource;
    }

    public Mono<ServerResponse> index(ServerRequest request) {
        var messageSuccess = request.session()
                .flatMap(session -> Mono.justOrEmpty(session.getAttributes().remove("messages.success")))
                .map(Object::toString);
        return ServerResponse.ok().render("todos/index",
                Map.of("todos", todoService.list(),
                        "success", messageSuccess));
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        var messageSuccess = request.session()
                .flatMap(session -> Mono.justOrEmpty(session.getAttributes().remove("messages.success")))
                .map(Object::toString);
        return RequestPathParser.parseId(request)
                .flatMap(todoService::findWithAround)
                .flatMap(todo -> ServerResponse.ok().render("todos/show",
                        Map.of("todo", todo.item(),
                                "prev", todo.prev(),
                                "next", todo.next(),
                                "success", messageSuccess)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return ServerResponse.ok().render("todos/create", new Todo());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return webExchangeDataBindings.bindAndValidate(request, new Todo())
                .flatMap(r -> save(request, r.target(), r.bindingResult(),
                        "todos/create", "messages.success-create"));
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        return RequestPathParser.parseId(request)
                .flatMap(todoService::findWithAround)
                .flatMap(todo -> ServerResponse.ok().render("todos/edit",
                        Map.of("todo", todo.item(),
                                "prev", todo.prev(),
                                "next", todo.next())))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return RequestPathParser.parseId(request)
                .flatMap(id -> webExchangeDataBindings.bindAndValidate(request, Todo.builder().id(id).build()))
                .flatMap(r -> save(request, r.target(), r.bindingResult(),
                        "todos/edit", "messages.success-update"))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        var messages = new MessageSourceAccessor(messageSource,
                request.exchange().getLocaleContext().getLocale());
        return RequestPathParser.parseId(request)
                .flatMap(id -> webExchangeDataBindings.bind(request, Todo.builder().id(id).build()))
                .map(Optional::of).defaultIfEmpty(Optional.empty())
                .flatMap(o -> o
                        .map(todo -> todoService.delete(todo)
                                .doOnSuccess(v -> request.session().subscribe(session ->
                                        session.getAttributes().put("messages.success",
                                                messages.getMessage("messages.success-delete"))))
                                .then(ServerResponse.seeOther(URI.create("/todos")).build()))
                        .orElse(ServerResponse.notFound().build()));
    }

    private Mono<ServerResponse> save(ServerRequest request, Todo todo, BindingResult bindingResult,
                                      String errorRender, String successMessage) {
        if (bindingResult.hasErrors()) {
            return Mono.justOrEmpty(todo.getId())
                    .flatMap(todoService::findAround)
                    .defaultIfEmpty(new Around<>(OptionalLong.empty(), OptionalLong.empty()))
                    .flatMap(a -> ServerResponse.ok().render(errorRender,
                            Map.of("todo", todo,
                                    BindingResult.MODEL_KEY_PREFIX + "todo", bindingResult,
                                    "prev", a.prev(),
                                    "next", a.next())));
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
