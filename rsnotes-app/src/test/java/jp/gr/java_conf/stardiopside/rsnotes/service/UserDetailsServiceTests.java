package jp.gr.java_conf.stardiopside.rsnotes.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.TestExecutionListeners;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestExecutionListeners(
        listeners = DbUnitTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DatabaseSetup("UserDetailsServiceTests-dataset")
class UserDetailsServiceTests {

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void findByUsernameNotFound() {
        StepVerifier.create(userDetailsService.findByUsername("username_none"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findByUsernameOneAuthority() {
        StepVerifier.create(userDetailsService.findByUsername("username1"))
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getUsername()).isEqualTo("username1");
                    assertThat(user.getPassword()).isEqualTo("<<password1>>");
                    assertThat(user.isEnabled()).isTrue();
                    assertThat(user.isAccountNonExpired()).isTrue();
                    assertThat(user.isCredentialsNonExpired()).isTrue();
                    assertThat(user.isAccountNonLocked()).isTrue();
                    assertThat(user.getAuthorities())
                            .hasSize(1)
                            .extracting(GrantedAuthority::getAuthority)
                            .containsOnly("ROLE_USER");
                })
                .verifyComplete();
    }

    @Test
    void findByUsernameManyAuthorities() {
        StepVerifier.create(userDetailsService.findByUsername("username2"))
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getUsername()).isEqualTo("username2");
                    assertThat(user.getPassword()).isEqualTo("<<password2>>");
                    assertThat(user.isEnabled()).isTrue();
                    assertThat(user.isAccountNonExpired()).isTrue();
                    assertThat(user.isCredentialsNonExpired()).isTrue();
                    assertThat(user.isAccountNonLocked()).isTrue();
                    assertThat(user.getAuthorities())
                            .hasSize(3)
                            .extracting(GrantedAuthority::getAuthority)
                            .containsOnly("ROLE_USER", "ROLE_ADMIN", "ROLE_GUEST");
                })
                .verifyComplete();
    }

    @Test
    void findByUsernameNoneAuthorities() {
        StepVerifier.create(userDetailsService.findByUsername("username3"))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findByUsernameDisabledUser() {
        StepVerifier.create(userDetailsService.findByUsername("username4"))
                .assertNext(user -> {
                    assertThat(user).isNotNull();
                    assertThat(user.getUsername()).isEqualTo("username4");
                    assertThat(user.getPassword()).isEqualTo("<<password4>>");
                    assertThat(user.isEnabled()).isFalse();
                    assertThat(user.isAccountNonExpired()).isTrue();
                    assertThat(user.isCredentialsNonExpired()).isTrue();
                    assertThat(user.isAccountNonLocked()).isTrue();
                    assertThat(user.getAuthorities())
                            .hasSize(1)
                            .extracting(GrantedAuthority::getAuthority)
                            .containsOnly("ROLE_USER");
                })
                .verifyComplete();
    }
}
