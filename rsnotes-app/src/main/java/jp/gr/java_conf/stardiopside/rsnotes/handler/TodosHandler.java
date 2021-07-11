package jp.gr.java_conf.stardiopside.rsnotes.handler;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import jp.gr.java_conf.stardiopside.rsnotes.service.TodoService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
public class TodosHandler {

    private final TodoService todoService;

    public TodosHandler(TodoService todoService) {
        this.todoService = todoService;
    }

    public Mono<ServerResponse> index(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .render("todos/index", Map.of("todos", todoService.list()));
    }

    public Mono<ServerResponse> show(ServerRequest request) {
        throw new UnsupportedOperationException();
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_HTML)
                .render("todos/create", new Todo());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        var todo = new Todo();
        var binder = new WebExchangeDataBinder(todo);
        return binder.bind(request.exchange())
                .thenReturn(todo)
                .flatMap(todoService::save)
                .flatMap(result -> ServerResponse.seeOther(URI.create("/todos")).build());
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
