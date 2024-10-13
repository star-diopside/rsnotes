package jp.gr.java_conf.stardiopside.rsnotes.web.handler;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import jp.gr.java_conf.stardiopside.rsnotes.service.TodoService;
import jp.gr.java_conf.stardiopside.rsnotes.web.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.mock.web.server.MockWebSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RenderingResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class TodosHandlerTests {

    @Autowired
    private TodosHandler todosHandler;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TodoService todoService;

    @Nested
    class ServerTests {

        @ParameterizedTest
        @MethodSource("todosProvider")
        void index(List<Todo> todos) {
            when(todoService.list()).thenReturn(Flux.fromIterable(todos));

            var request = MockServerRequest.builder()
                    .uri(URI.create("/todos"))
                    .build();

            StepVerifier.create(todosHandler.index(request))
                    .assertNext(serverResponse -> {
                        assertThat(serverResponse).isInstanceOfSatisfying(RenderingResponse.class, response -> {
                            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                            assertThat(response.name()).isEqualTo("todos/index");
                            assertThat(response.model()).containsOnlyKeys("todos", "success");
                            assertThat(response.model().get("todos"))
                                    .isInstanceOfSatisfying(Flux.class, flux -> {
                                        StepVerifier.create(((Flux<?>) flux).cast(Todo.class))
                                                .expectNextSequence(todos)
                                                .verifyComplete();
                                    });
                            assertThat(response.model().get("success"))
                                    .isInstanceOfSatisfying(Mono.class, mono -> {
                                        StepVerifier.create((Mono<?>) mono)
                                                .expectNextCount(0)
                                                .verifyComplete();
                                    });
                        });
                    })
                    .verifyComplete();

            verify(todoService, times(1)).list();
        }

        @ParameterizedTest
        @MethodSource("todosProvider")
        void indexWithMessage(List<Todo> todos) {
            when(todoService.list()).thenReturn(Flux.fromIterable(todos));

            var session = new MockWebSession();
            session.getAttributes()
                    .put(Constants.MESSAGE_KEY_SUCCESS, "テストメッセージ");
            var request = MockServerRequest.builder()
                    .uri(URI.create("/todos"))
                    .session(session)
                    .build();

            StepVerifier.create(todosHandler.index(request))
                    .assertNext(serverResponse -> {
                        assertThat(serverResponse).isInstanceOfSatisfying(RenderingResponse.class, response -> {
                            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                            assertThat(response.name()).isEqualTo("todos/index");
                            assertThat(response.model()).containsOnlyKeys("todos", "success");
                            assertThat(response.model().get("todos"))
                                    .isInstanceOfSatisfying(Flux.class, flux -> {
                                        StepVerifier.create(((Flux<?>) flux).cast(Todo.class))
                                                .expectNextSequence(todos)
                                                .verifyComplete();
                                    });
                            assertThat(response.model().get("success"))
                                    .isInstanceOfSatisfying(Mono.class, mono -> {
                                        StepVerifier.create(((Mono<?>) mono).cast(Object.class))
                                                .expectNext("テストメッセージ")
                                                .verifyComplete();
                                    });
                        });
                    })
                    .verifyComplete();

            assertThat(session.getAttributes())
                    .doesNotContainKey(Constants.MESSAGE_KEY_SUCCESS);

            verify(todoService, times(1)).list();
        }

        @Test
        void indexError() {
            when(todoService.list()).thenReturn(Flux.error(RuntimeException::new));

            var session = new MockWebSession();
            session.getAttributes()
                    .put(Constants.MESSAGE_KEY_SUCCESS, "テストメッセージ");
            var request = MockServerRequest.builder()
                    .uri(URI.create("/todos"))
                    .session(session)
                    .build();

            StepVerifier.create(todosHandler.index(request))
                    .assertNext(serverResponse -> {
                        assertThat(serverResponse).isInstanceOfSatisfying(RenderingResponse.class, response -> {
                            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                            assertThat(response.name()).isEqualTo("todos/index");
                            assertThat(response.model()).containsOnlyKeys("todos", "success");
                            assertThat(response.model().get("todos"))
                                    .isInstanceOfSatisfying(Flux.class, flux -> {
                                        StepVerifier.create((Flux<?>) flux)
                                                .verifyError();
                                    });
                            assertThat(response.model().get("success"))
                                    .isInstanceOfSatisfying(Mono.class, mono -> {
                                        StepVerifier.create(((Mono<?>) mono).cast(Object.class))
                                                .expectNext("テストメッセージ")
                                                .verifyComplete();
                                    });
                        });
                    })
                    .verifyComplete();

            assertThat(session.getAttributes())
                    .doesNotContainKey(Constants.MESSAGE_KEY_SUCCESS);

            verify(todoService, times(1)).list();
        }

        static Stream<List<Todo>> todosProvider() {
            return TodosHandlerTests.todosProvider();
        }
    }

    @Nested
    class ClientTests {

        @ParameterizedTest
        @MethodSource("todosProvider")
        @WithMockUser
        void index(List<Todo> todos) {
            when(todoService.list()).thenReturn(Flux.fromIterable(todos));

            webTestClient
                    .get().uri("/todos")
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.TEXT_HTML);

            verify(todoService, times(1)).list();
        }

        @Test
        @WithMockUser
        void indexError() {
            when(todoService.list()).thenReturn(Flux.error(RuntimeException::new));

            webTestClient
                    .get().uri("/todos")
                    .exchange()
                    .expectStatus().is5xxServerError();

            verify(todoService, times(1)).list();
        }

        static Stream<List<Todo>> todosProvider() {
            return TodosHandlerTests.todosProvider();
        }
    }

    static Stream<List<Todo>> todosProvider() {
        return Stream.of(
                List.of(),
                List.of(
                        Todo.builder().id(1L).text("test1").build()),
                List.of(
                        Todo.builder().id(11L).text("test11").build(),
                        Todo.builder().id(12L).text("test12").build(),
                        Todo.builder().id(13L).text("test13").build(),
                        Todo.builder().id(14L).text("test14").build(),
                        Todo.builder().id(15L).text("test15").build()));
    }
}
