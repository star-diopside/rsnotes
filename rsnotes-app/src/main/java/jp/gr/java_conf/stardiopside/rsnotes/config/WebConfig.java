package jp.gr.java_conf.stardiopside.rsnotes.config;

import jp.gr.java_conf.stardiopside.rsnotes.handler.HomeHandler;
import jp.gr.java_conf.stardiopside.rsnotes.handler.TodosHandler;
import jp.gr.java_conf.stardiopside.rsnotes.handler.UsersHandler;
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
                        .POST(todosHandler::save)
                        .PUT("/{id}", todosHandler::update)
                        .DELETE("/{id}", todosHandler::delete))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routeUsers(UsersHandler usersHandler) {
        return RouterFunctions.route()
                .path("/users", builder -> builder
                        .GET("/create", usersHandler::create)
                        .GET("/{id}", usersHandler::show)
                        .GET("/{id}/edit", usersHandler::edit)
                        .GET(usersHandler::index)
                        .POST(usersHandler::save)
                        .PUT("/{id}", usersHandler::update)
                        .DELETE("/{id}", usersHandler::delete))
                .build();
    }
}
