package jp.gr.java_conf.stardiopside.rsnotes.config;

import jp.gr.java_conf.stardiopside.rsnotes.handler.HomeHandler;
import jp.gr.java_conf.stardiopside.rsnotes.handler.TodosHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

@Configuration
public class WebConfig {

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route()
                .GET("/", request -> ServerResponse.temporaryRedirect(URI.create("/home")).build())
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routeHome(HomeHandler homeHandler) {
        return RouterFunctions.route()
                .GET("/home", homeHandler::index)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routeTodos(TodosHandler todosHandler) {
        return RouterFunctions.route()
                .path("/todos", builder -> builder
                        .GET("/create", todosHandler::create)
                        .GET("/{id}", todosHandler::show)
                        .GET("/{id}/edit", todosHandler::edit)
                        .GET(todosHandler::index)
                        .POST("/{id}", todosHandler::update)
                        .POST("/{id}/delete", todosHandler::delete)
                        .POST(todosHandler::save))
                .build();
    }
}
