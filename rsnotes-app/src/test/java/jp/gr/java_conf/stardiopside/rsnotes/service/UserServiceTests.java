package jp.gr.java_conf.stardiopside.rsnotes.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestExecutionListeners(
        listeners = DbUnitTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DatabaseSetup("UserServiceTests-dataset")
class UserServiceTests {

    @Autowired
    private UserService userService;

    @Test
    void list() {
        StepVerifier.create(userService.list())
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getId()).isEqualTo(100);
                    assertThat(user.getUsername()).isEqualTo("username1");
                    assertThat(user.getPassword()).isEqualTo("<<password1>>");
                    assertThat(user.isEnabled()).isTrue();
                })
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getId()).isEqualTo(101);
                    assertThat(user.getUsername()).isEqualTo("username2");
                    assertThat(user.getPassword()).isEqualTo("<<password2>>");
                    assertThat(user.isEnabled()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void findByNull() {
        assertThatThrownBy(() -> userService.find(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findNotFound() {
        StepVerifier.create(userService.find(1L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findOne() {
        StepVerifier.create(userService.find(100L))
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getId()).isEqualTo(100);
                    assertThat(user.getUsername()).isEqualTo("username1");
                    assertThat(user.getPassword()).isEqualTo("<<password1>>");
                    assertThat(user.isEnabled()).isTrue();
                })
                .verifyComplete();
    }
}
