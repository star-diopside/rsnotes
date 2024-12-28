package jp.gr.java_conf.stardiopside.rsnotes.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.dataset.DataSetLoader;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestExecutionListeners;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dbunit.Assertion.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestExecutionListeners(
        listeners = DbUnitTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class FileServiceTests {

    private static final String TEST_RESOURCE_PREFIX = "FileServiceTests-resources";

    private final ResourceLoader resourceLoader = new ClassRelativeResourceLoader(getClass());

    @Autowired
    private FileService fileService;

    @Autowired
    private IDatabaseConnection databaseConnection;

    @Autowired
    private DataSetLoader dataSetLoader;

    @Test
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/empty")
    void listEmpty() {
        StepVerifier.create(fileService.list())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/one")
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
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
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
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
    void findFileInfoNotFound() {
        StepVerifier.create(fileService.findFileInfo(1L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
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
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
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
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
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
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
    void findDownloadDataNotExistsData() {
        StepVerifier.create(fileService.findDownloadData(1003L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
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
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
    void findFileInfoDataNotExistsData() {
        StepVerifier.create(fileService.findFileInfoData(1003L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @WithMockUser("test_user")
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/empty")
    void saveEmpty1() throws Exception {
        var mockFilePart = mock(FilePart.class);
        when(mockFilePart.filename()).thenReturn("空ファイル1.dat");
        when(mockFilePart.headers()).thenReturn(HttpHeaders.EMPTY);
        when(mockFilePart.content()).thenReturn(Flux.fromIterable(List.of()));

        StepVerifier.create(fileService.save(mockFilePart))
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isNotNull();
                    assertThat(fileInfo.getFileName()).isEqualTo("空ファイル1.dat");
                    assertThat(fileInfo.getContentType()).isNull();
                    assertThat(fileInfo.getLength()).isEqualTo(0);
                    assertThat(fileInfo.getHashValue()).matches("[0-9a-f]{64}");
                    assertThat(fileInfo.getCreatedBy()).isEqualTo("test_user");
                    assertThat(fileInfo.getUpdatedBy()).isEqualTo("test_user");
                    assertThat(fileInfo.getVersion()).isEqualTo(0);
                })
                .verifyComplete();

        assertDatabase(dataSetLoader.loadDataSet(getClass(),
                TEST_RESOURCE_PREFIX + "/dataset/expected/saveEmpty1"));
    }

    @Test
    @WithMockUser("test_user")
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
    void saveEmpty2() throws Exception {
        var mockFilePart = mock(FilePart.class);
        when(mockFilePart.filename()).thenReturn("空ファイル2.txt");
        var headers = new HttpHeaders();
        headers.addAll(HttpHeaders.CONTENT_TYPE, List.of(
                MediaType.TEXT_PLAIN_VALUE,
                MediaType.APPLICATION_OCTET_STREAM_VALUE));
        when(mockFilePart.headers()).thenReturn(headers);
        when(mockFilePart.content()).thenReturn(Flux.fromIterable(List.of(
                DefaultDataBufferFactory.sharedInstance.allocateBuffer(0))));

        StepVerifier.create(fileService.save(mockFilePart))
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isNotNull();
                    assertThat(fileInfo.getFileName()).isEqualTo("空ファイル2.txt");
                    assertThat(fileInfo.getContentType()).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
                    assertThat(fileInfo.getLength()).isEqualTo(0);
                    assertThat(fileInfo.getHashValue()).matches("[0-9a-f]{64}");
                    assertThat(fileInfo.getCreatedBy()).isEqualTo("test_user");
                    assertThat(fileInfo.getUpdatedBy()).isEqualTo("test_user");
                    assertThat(fileInfo.getVersion()).isEqualTo(0);
                })
                .verifyComplete();

        assertDatabase(dataSetLoader.loadDataSet(getClass(),
                TEST_RESOURCE_PREFIX + "/dataset/expected/saveEmpty2"));
    }

    @Test
    @WithMockUser("test_user")
    @DatabaseSetup(TEST_RESOURCE_PREFIX + "/dataset/setup/many")
    void saveFile() throws Exception {
        String filename = "150x150.png";
        Resource resource = resourceLoader.getResource(TEST_RESOURCE_PREFIX + "/file/" + filename);
        long contentLength = resource.contentLength();

        var mockFilePart = mock(FilePart.class);
        when(mockFilePart.filename()).thenReturn(filename);
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE);
        when(mockFilePart.headers()).thenReturn(headers);
        when(mockFilePart.content()).thenReturn(DataBufferUtils.read(
                resource, DefaultDataBufferFactory.sharedInstance, 1024));

        StepVerifier.create(fileService.save(mockFilePart))
                .assertNext(fileInfo -> {
                    assertThat(fileInfo).isNotNull();
                    assertThat(fileInfo.getId()).isNotNull();
                    assertThat(fileInfo.getFileName()).isEqualTo(filename);
                    assertThat(fileInfo.getContentType()).isEqualTo(MediaType.IMAGE_PNG_VALUE);
                    assertThat(fileInfo.getLength()).isEqualTo(contentLength);
                    assertThat(fileInfo.getHashValue()).isEqualTo(getSha256sum().get(filename));
                    assertThat(fileInfo.getCreatedBy()).isEqualTo("test_user");
                    assertThat(fileInfo.getUpdatedBy()).isEqualTo("test_user");
                    assertThat(fileInfo.getVersion()).isEqualTo(0);
                })
                .verifyComplete();

        var expectedDataSet = new ReplacementDataSet(dataSetLoader.loadDataSet(getClass(),
                TEST_RESOURCE_PREFIX + "/dataset/expected/saveFile"));
        expectedDataSet.addReplacementObject(
                "[FILE]src/test/resources/jp/gr/java_conf/stardiopside/rsnotes/service/FileServiceTests-resources/file/150x150.png",
                resource.getContentAsByteArray());
        assertDatabase(expectedDataSet);
    }

    private Map<String, String> getSha256sum() {
        try (var reader = new BufferedReader(new InputStreamReader(
                resourceLoader
                        .getResource(TEST_RESOURCE_PREFIX + "/file/sha256sum.txt")
                        .getInputStream(),
                StandardCharsets.UTF_8))) {
            var p = Pattern.compile("\\s+");
            return reader.lines()
                    .map(line -> p.split(line, 2))
                    .filter(items -> items.length == 2)
                    .collect(Collectors.toUnmodifiableMap(items -> items[1], items -> items[0]));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * データベースの検証を行う。
     *
     * @param expectedDataSet 期待値のデータセット
     * @throws DatabaseUnitException 検証エラーが発生した場合
     * @throws SQLException          DBアクセスエラーが発生した場合
     */
    private void assertDatabase(IDataSet expectedDataSet) throws DatabaseUnitException, SQLException {
        var actualDataSet = databaseConnection.createDataSet(expectedDataSet.getTableNames());

        assertTable(expectedDataSet, actualDataSet,
                "file_info",
                new String[]{"file_name", "content_type", "length", "hash_value",
                        "created_by", "updated_by", "version"});
        assertTable(expectedDataSet, actualDataSet,
                "file_data",
                new String[]{"data", "created_by", "updated_by", "version"});
    }

    /**
     * テーブルデータの検証を行う。
     *
     * @param expectedDataSet    期待値のデータセット
     * @param actualDataSet      実際のデータセット
     * @param tableName          検証対象のテーブル名
     * @param includeColumnNames 検証対象のカラム名
     * @throws DatabaseUnitException 検証エラーが発生した場合
     */
    private static void assertTable(IDataSet expectedDataSet,
                                    IDataSet actualDataSet,
                                    String tableName,
                                    String[] includeColumnNames) throws DatabaseUnitException {
        assertEquals(
                new SortedTable(
                        DefaultColumnFilter.includedColumnsTable(
                                expectedDataSet.getTable(tableName),
                                includeColumnNames),
                        includeColumnNames),
                new SortedTable(
                        DefaultColumnFilter.includedColumnsTable(
                                actualDataSet.getTable(tableName),
                                includeColumnNames),
                        includeColumnNames));
    }
}
