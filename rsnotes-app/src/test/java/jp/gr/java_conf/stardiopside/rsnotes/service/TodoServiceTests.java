package jp.gr.java_conf.stardiopside.rsnotes.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import reactor.test.StepVerifier;

import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestExecutionListeners(
        listeners = DbUnitTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class TodoServiceTests {

    @Autowired
    private TodoService todoService;

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/empty")
    void listEmpty() {
        StepVerifier.create(todoService.list())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/one")
    void listOne() {
        StepVerifier.create(todoService.list())
                .assertNext(todo -> {
                    assertThat(todo).isNotNull();
                    assertThat(todo.getId()).isEqualTo(1);
                    assertThat(todo.getText()).isEqualTo("テストデータ1");
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void listMany() {
        var expectedIds = new long[]{90, 101, 102, 103, 104, 105, 107, 108, 109, 110, 990};
        StepVerifier.Step<Todo> step = StepVerifier.create(todoService.list());
        for (long id : expectedIds) {
            step = step.assertNext(todo -> {
                assertThat(todo).isNotNull();
                assertThat(todo.getId()).isEqualTo(id);
                assertThat(todo.getText()).isEqualTo("テストデータ" + id);
            });
        }
        step.verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findNotFound() {
        StepVerifier.create(todoService.find(1L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findOne() {
        StepVerifier.create(todoService.find(90L))
                .assertNext(todo -> {
                    assertThat(todo).isNotNull();
                    assertThat(todo.getId()).isEqualTo(90);
                    assertThat(todo.getText()).isEqualTo("テストデータ90");
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/empty")
    void findAroundEmpty() {
        StepVerifier.create(todoService.findAround(1L))
                .assertNext(around -> {
                    assertThat(around).isNotNull();
                    assertThat(around.prev()).isEqualTo(OptionalLong.empty());
                    assertThat(around.next()).isEqualTo(OptionalLong.empty());
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/one")
    void findAroundOne() {
        StepVerifier.create(todoService.findAround(1L))
                .assertNext(around -> {
                    assertThat(around).isNotNull();
                    assertThat(around.prev()).isEqualTo(OptionalLong.empty());
                    assertThat(around.next()).isEqualTo(OptionalLong.empty());
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findAroundNotFound() {
        StepVerifier.create(todoService.findAround(1L))
                .assertNext(around -> {
                    assertThat(around).isNotNull();
                    assertThat(around.prev()).isEqualTo(OptionalLong.empty());
                    assertThat(around.next()).isEqualTo(OptionalLong.of(90));
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findAroundFirst() {
        StepVerifier.create(todoService.findAround(90L))
                .assertNext(around -> {
                    assertThat(around).isNotNull();
                    assertThat(around.prev()).isEqualTo(OptionalLong.empty());
                    assertThat(around.next()).isEqualTo(OptionalLong.of(101));
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findAroundMiddle() {
        StepVerifier.create(todoService.findAround(107L))
                .assertNext(around -> {
                    assertThat(around).isNotNull();
                    assertThat(around.prev()).isEqualTo(OptionalLong.of(105));
                    assertThat(around.next()).isEqualTo(OptionalLong.of(108));
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findAroundLast() {
        StepVerifier.create(todoService.findAround(990L))
                .assertNext(around -> {
                    assertThat(around).isNotNull();
                    assertThat(around.prev()).isEqualTo(OptionalLong.of(110));
                    assertThat(around.next()).isEqualTo(OptionalLong.empty());
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/empty")
    void findWithAroundEmpty() {
        StepVerifier.create(todoService.findWithAround(1L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/one")
    void findWithAroundOne() {
        StepVerifier.create(todoService.findWithAround(1L))
                .assertNext(node -> {
                    assertThat(node).isNotNull();
                    assertThat(node.item()).isNotNull();
                    assertThat(node.item().getId()).isEqualTo(1);
                    assertThat(node.item().getText()).isEqualTo("テストデータ1");
                    assertThat(node.prev()).isEqualTo(OptionalLong.empty());
                    assertThat(node.next()).isEqualTo(OptionalLong.empty());
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findWithAroundNotFound() {
        StepVerifier.create(todoService.findWithAround(1L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findWithAroundFirst() {
        StepVerifier.create(todoService.findWithAround(90L))
                .assertNext(node -> {
                    assertThat(node).isNotNull();
                    assertThat(node.item()).isNotNull();
                    assertThat(node.item().getId()).isEqualTo(90);
                    assertThat(node.item().getText()).isEqualTo("テストデータ90");
                    assertThat(node.prev()).isEqualTo(OptionalLong.empty());
                    assertThat(node.next()).isEqualTo(OptionalLong.of(101));
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findWithAroundMiddle() {
        StepVerifier.create(todoService.findWithAround(107L))
                .assertNext(node -> {
                    assertThat(node).isNotNull();
                    assertThat(node.item()).isNotNull();
                    assertThat(node.item().getId()).isEqualTo(107);
                    assertThat(node.item().getText()).isEqualTo("テストデータ107");
                    assertThat(node.prev()).isEqualTo(OptionalLong.of(105));
                    assertThat(node.next()).isEqualTo(OptionalLong.of(108));
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("TodoServiceTests-dataset/many")
    void findWithAroundLast() {
        StepVerifier.create(todoService.findWithAround(990L))
                .assertNext(node -> {
                    assertThat(node).isNotNull();
                    assertThat(node.item()).isNotNull();
                    assertThat(node.item().getId()).isEqualTo(990);
                    assertThat(node.item().getText()).isEqualTo("テストデータ990");
                    assertThat(node.prev()).isEqualTo(OptionalLong.of(110));
                    assertThat(node.next()).isEqualTo(OptionalLong.empty());
                })
                .verifyComplete();
    }
}
