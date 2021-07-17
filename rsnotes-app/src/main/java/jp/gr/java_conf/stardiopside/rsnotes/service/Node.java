package jp.gr.java_conf.stardiopside.rsnotes.service;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Node<E, K> {

    private E item;

    private K prev;

    private K next;

}
