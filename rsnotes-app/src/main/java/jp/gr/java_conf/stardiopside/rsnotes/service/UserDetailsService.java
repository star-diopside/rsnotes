package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.repository.AuthorityRepository;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Service
public class UserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public UserDetailsService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        var user = userRepository
                .findByUsername(username)
                .cache();
        var authorities = user
                .flatMapMany(u -> authorityRepository.findByUserId(u.getId()))
                .map(a -> new SimpleGrantedAuthority(a.getAuthority()));
        return Mono.zip(user, authorities.collectList().filter(Predicate.not(List::isEmpty)))
                .map(t -> new User(
                        t.getT1().getUsername(),
                        t.getT1().getPassword(),
                        t.getT1().isEnabled(),
                        true,
                        true,
                        true,
                        t.getT2()));
    }
}
