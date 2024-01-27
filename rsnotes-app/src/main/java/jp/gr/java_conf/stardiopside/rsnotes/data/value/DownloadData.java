package jp.gr.java_conf.stardiopside.rsnotes.data.value;

import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;

public record DownloadData(
        String fileName,
        @Nullable String contentType,
        ByteBuffer data) {
}
