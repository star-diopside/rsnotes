package jp.gr.java_conf.stardiopside.rsnotes.data.value;

import java.nio.ByteBuffer;

public record DownloadData(
        String fileName,
        String contentType,
        ByteBuffer data) {
}
