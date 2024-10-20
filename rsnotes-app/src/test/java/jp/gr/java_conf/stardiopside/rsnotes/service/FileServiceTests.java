package jp.gr.java_conf.stardiopside.rsnotes.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestExecutionListeners(
        listeners = DbUnitTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class FileServiceTests {

    @Autowired
    private FileService fileService;

    @Test
    @DatabaseSetup("FileServiceTests-dataset/empty")
    void listEmpty() {
        StepVerifier.create(fileService.list())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("FileServiceTests-dataset/one")
    void listOne() {
        StepVerifier.create(fileService.list())
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isEqualTo(10);
                    assertThat(fileInfo.getFileName()).isEqualTo("ファイル1.txt");
                    assertThat(fileInfo.getContentType()).isEqualTo("text/plain");
                    assertThat(fileInfo.getLength()).isEqualTo(99);
                    assertThat(fileInfo.getHashValue()).isEqualTo("hash_value_1");
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("FileServiceTests-dataset/many")
    void listMany() {
        StepVerifier.create(fileService.list())
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isEqualTo(1001);
                    assertThat(fileInfo.getFileName()).isEqualTo("ファイル1.txt");
                    assertThat(fileInfo.getContentType()).isEqualTo("text/plain");
                    assertThat(fileInfo.getLength()).isEqualTo(101);
                    assertThat(fileInfo.getHashValue()).isEqualTo("hash_value_1");
                })
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isEqualTo(1003);
                    assertThat(fileInfo.getFileName()).isEqualTo("ファイル3_データレコードなし.txt");
                    assertThat(fileInfo.getContentType()).isEqualTo("image/png");
                    assertThat(fileInfo.getLength()).isEqualTo(103);
                    assertThat(fileInfo.getHashValue()).isEqualTo("hash_value_3");
                })
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isEqualTo(1004);
                    assertThat(fileInfo.getFileName()).isEqualTo("ファイル4.txt");
                    assertThat(fileInfo.getContentType()).isEqualTo("application/octet-stream");
                    assertThat(fileInfo.getLength()).isEqualTo(104);
                    assertThat(fileInfo.getHashValue()).isEqualTo("hash_value_4");
                })
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isEqualTo(1012);
                    assertThat(fileInfo.getFileName()).isEqualTo("ファイル2.txt");
                    assertThat(fileInfo.getContentType()).isNull();
                    assertThat(fileInfo.getLength()).isEqualTo(102);
                    assertThat(fileInfo.getHashValue()).isEqualTo("hash_value_2");
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("FileServiceTests-dataset/many")
    void findFileInfoNotFound() {
        StepVerifier.create(fileService.findFileInfo(1L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("FileServiceTests-dataset/many")
    void findFileInfoExistsData() {
        StepVerifier.create(fileService.findFileInfo(1001L))
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isEqualTo(1001);
                    assertThat(fileInfo.getFileName()).isEqualTo("ファイル1.txt");
                    assertThat(fileInfo.getContentType()).isEqualTo("text/plain");
                    assertThat(fileInfo.getLength()).isEqualTo(101);
                    assertThat(fileInfo.getHashValue()).isEqualTo("hash_value_1");
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("FileServiceTests-dataset/many")
    void findFileInfoNotExistsData() {
        StepVerifier.create(fileService.findFileInfo(1003L))
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isEqualTo(1003);
                    assertThat(fileInfo.getFileName()).isEqualTo("ファイル3_データレコードなし.txt");
                    assertThat(fileInfo.getContentType()).isEqualTo("image/png");
                    assertThat(fileInfo.getLength()).isEqualTo(103);
                    assertThat(fileInfo.getHashValue()).isEqualTo("hash_value_3");
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("FileServiceTests-dataset/many")
    void findDownloadDataExistsData() {
        StepVerifier.create(fileService.findDownloadData(1001L))
                .assertNext(downloadData -> {
                    assertThat(downloadData).isNotNull();
                    assertThat(downloadData.fileName()).isEqualTo("ファイル1.txt");
                    assertThat(downloadData.contentType()).isEqualTo("text/plain");
                    assertThat(downloadData.data()).isEqualTo(ByteBuffer.wrap(
                            "テキストファイルデータ１".getBytes(StandardCharsets.UTF_8)));
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("FileServiceTests-dataset/many")
    void findDownloadDataNotExistsData() {
        StepVerifier.create(fileService.findDownloadData(1003L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("FileServiceTests-dataset/many")
    void findFileInfoDataExistsData() {
        StepVerifier.create(fileService.findFileInfoData(1001L))
                .assertNext(fileInfoData -> {
                    assertThat(fileInfoData).isNotNull();
                    assertThat(fileInfoData.id()).isEqualTo(1001);
                    assertThat(fileInfoData.fileName()).isEqualTo("ファイル1.txt");
                    assertThat(fileInfoData.contentType()).isEqualTo("text/plain");
                    assertThat(fileInfoData.length()).isEqualTo(101);
                    assertThat(fileInfoData.hashValue()).isEqualTo("hash_value_1");
                    assertThat(fileInfoData.createdAt()).isEqualTo(LocalDateTime.of(2024, 1, 2, 3, 4, 5));
                    assertThat(fileInfoData.updatedAt()).isEqualTo(LocalDateTime.of(2025, 6, 7, 8, 9, 10));
                    assertThat(fileInfoData.version()).isEqualTo(1);
                    assertThat(fileInfoData.fileDataId()).isEqualTo(2004);
                    assertThat(fileInfoData.fileDataUpdatedAt()).isEqualTo(LocalDateTime.of(2027, 1, 2, 16, 17, 18));
                    assertThat(fileInfoData.fileDataVersion()).isEqualTo(10);
                })
                .verifyComplete();
    }

    @Test
    @DatabaseSetup("FileServiceTests-dataset/many")
    void findFileInfoDataNotExistsData() {
        StepVerifier.create(fileService.findFileInfoData(1003L))
                .expectNextCount(0)
                .verifyComplete();
    }
}
