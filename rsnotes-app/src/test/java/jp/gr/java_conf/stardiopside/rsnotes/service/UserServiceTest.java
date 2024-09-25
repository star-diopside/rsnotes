package jp.gr.java_conf.stardiopside.rsnotes.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import jp.gr.java_conf.stardiopside.rsnotes.test.dataset.CsvDataSetLoader;
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
@DbUnitConfiguration(dataSetLoader = CsvDataSetLoader.class)
@DatabaseSetup("UserServiceTest/")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testFindByNull() {
        assertThatThrownBy(() -> userService.find(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testFindNotFound() {
        StepVerifier
                .create(userService.find(1L))
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void testFindOne() {
        StepVerifier
                .create(userService.find(100L))
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getId()).isEqualTo(100L);
                    assertThat(user.getUsername()).isEqualTo("username1");
                    assertThat(user.getPassword()).isEqualTo("<<password1>>");
                    assertThat(user.isEnabled()).isTrue();
                })
                .verifyComplete();
    }
}
