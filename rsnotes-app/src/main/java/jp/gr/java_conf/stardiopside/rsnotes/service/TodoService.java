package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.Todo;
import reactor.core.publisher.Flux;

public interface TodoService {

    Flux<Todo> list();

}
