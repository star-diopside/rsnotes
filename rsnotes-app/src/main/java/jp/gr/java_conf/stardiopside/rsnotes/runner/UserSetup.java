package jp.gr.java_conf.stardiopside.rsnotes.runner;

import jp.gr.java_conf.stardiopside.rsnotes.data.repository.UserRepository;
import jp.gr.java_conf.stardiopside.rsnotes.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserSetup implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserSetup(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.count()
                .flatMap(count -> count == 0
                        ? userService.create("admin", "admin", "ADMIN", "USER")
                        : Mono.empty())
                .subscribe();
    }
}
