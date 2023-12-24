package jp.gr.java_conf.stardiopside.rsnotes.service;

public record Node<E, K>(E item, K prev, K next) {
}
