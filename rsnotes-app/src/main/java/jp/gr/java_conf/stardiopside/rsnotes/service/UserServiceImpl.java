package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Authority;
import jp.gr.java_conf.stardiopside.rsnotes.data.entity.User;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.AuthorityRepository;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

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
    @Transactional
    public Mono<Void> create(String username, String rawPassword, String... roles) {
        return userRepository
                .save(User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword))
                        .enabled(true)
                        .build())
                .map(u -> Arrays.stream(roles)
                        .map(r -> Authority.builder()
                                .userId(u.getId())
                                .authority("ROLE_" + r)
                                .build())
                        .collect(Collectors.toList()))
                .flatMapMany(authorityRepository::saveAll)
                .then();
    }
}
