package jp.gr.java_conf.stardiopside.rsnotes.handler;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
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

import java.util.Map;

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
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                .render("todos/index", Map.of("todos", todoService.list()));
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        var messageSuccess = request.session()
                .flatMap(session -> Mono.justOrEmpty(session.getAttributes().remove("messages.success")))
                .map(Object::toString);
        return Mono.fromSupplier(() -> Integer.valueOf(request.pathVariable("id")))
                .onErrorResume(NumberFormatException.class, e -> Mono.empty())
                .flatMap(id -> todoService.find(id))
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
        var todo = new Todo();
        var binder = new WebExchangeDataBinder(todo);
        binder.setValidator(validator);
        return binder.bind(request.exchange())
                .then(Mono.defer(() -> {
                    binder.validate();
                    var bindingResult = binder.getBindingResult();

                    if (bindingResult.hasErrors()) {
                        return ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                                .render("todos/create",
                                        Map.of("todo", todo,
                                                BindingResult.MODEL_KEY_PREFIX + "todo", bindingResult));
                    }

                    var messageSourceAccessor = new MessageSourceAccessor(messageSource,
                            request.exchange().getLocaleContext().getLocale());

                    return todoService.save(todo)
                            .doOnSuccess(t -> request.session().subscribe(session ->
                                    session.getAttributes().put("messages.success",
                                            messageSourceAccessor.getMessage("messages.success-create"))))
                            .flatMap(t -> ServerResponse.seeOther(UriComponentsBuilder
                                    .fromUriString("/todos/{id}").build(t.getId())).build());
                }));
    }

    public Mono<ServerResponse> edit(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        throw new UnsupportedOperationException();
    }
}
