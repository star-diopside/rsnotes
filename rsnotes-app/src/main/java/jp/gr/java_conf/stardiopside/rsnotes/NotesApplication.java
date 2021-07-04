package jp.gr.java_conf.stardiopside.rsnotes;

import jp.gr.java_conf.stardiopside.rsnotes.handler.HomeHandler;
import jp.gr.java_conf.stardiopside.rsnotes.handler.TodosHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@SpringBootApplication
public class NotesApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotesApplication.class, args);
    }

    @Bean
    public RouterFunction<ServerResponse> route(HomeHandler homeHandler, TodosHandler todosHandler) {
        return RouterFunctions.route()
                .GET("/", request -> ServerResponse.temporaryRedirect(URI.create("/home")).build())
                .GET("/home", homeHandler::index)
                .GET("/todos", todosHandler::index)
                .build();
    }
}
