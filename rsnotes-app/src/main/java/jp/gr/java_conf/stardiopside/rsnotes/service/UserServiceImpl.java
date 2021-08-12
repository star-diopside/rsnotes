package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Authority;
import jp.gr.java_conf.stardiopside.rsnotes.data.entity.User;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.AuthorityRepository;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, AuthorityRepository authorityRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Flux<User> list() {
        return userRepository.findAll(Sort.by("id").ascending());
    }

    @Override
    public Mono<User> find(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public Mono<User> create(String username, String rawPassword, String... roles) {
        var user = userRepository
                .save(User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword))
                        .enabled(true)
                        .build())
                .cache();
        return user
                .flatMapMany(u -> Flux.fromStream(Arrays.stream(roles)
                        .map(r -> Authority.builder()
                                .userId(u.getId())
                                .authority("ROLE_" + r)
                                .build())))
                .flatMap(authorityRepository::save)
                .then(user);
    }
}
