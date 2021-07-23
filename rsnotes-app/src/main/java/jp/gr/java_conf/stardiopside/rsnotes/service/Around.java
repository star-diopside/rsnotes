package jp.gr.java_conf.stardiopside.rsnotes.service;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Around<T> {

    private T prev;

    private T next;

}
