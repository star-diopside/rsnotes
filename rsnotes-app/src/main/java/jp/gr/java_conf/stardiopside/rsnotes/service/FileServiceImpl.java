package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileData;
import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.FileDataRepository;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.FileInfoRepository;
import jp.gr.java_conf.stardiopside.rsnotes.data.value.DownloadData;
import jp.gr.java_conf.stardiopside.rsnotes.data.value.FileInfoData;
import jp.gr.java_conf.stardiopside.rsnotes.data.value.IdOnly;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Service
public class FileServiceImpl implements FileService {

    private final FileInfoRepository fileInfoRepository;
    private final FileDataRepository fileDataRepository;

    public FileServiceImpl(FileInfoRepository fileInfoRepository, FileDataRepository fileDataRepository) {
        this.fileInfoRepository = fileInfoRepository;
        this.fileDataRepository = fileDataRepository;
    }

    @Override
    public Flux<FileInfo> list() {
        return fileInfoRepository.findAll(Sort.by("id").ascending());
    }

    @Override
    public Mono<FileInfo> findFileInfo(Long id) {
        return fileInfoRepository.findById(id);
    }

    @Override
    public Mono<DownloadData> findDownloadData(Long id) {
        return fileInfoRepository.findDownloadDataById(id);
    }

    @Override
    public Mono<FileInfoData> findFileInfoData(Long id) {
        return fileInfoRepository.findFileInfoDataById(id);
    }

    @Override
    @Transactional
    public Mono<FileInfo> save(FilePart filePart) {
        Mono<Content> content = Content.from(filePart);

        Mono<FileInfo> fileInfo = content
                .map(c -> FileInfo.builder()
                        .fileName(filePart.filename())
                        .contentType(filePart.headers().getFirst(HttpHeaders.CONTENT_TYPE))
                        .length(c.length())
                        .hashValue(c.hashValue())
                        .build())
                .flatMap(fileInfoRepository::save)
                .cache();

        return Mono.zip(fileInfo, content)
                .map(t -> FileData.builder()
                        .fileInfoId(t.getT1().getId())
                        .data(t.getT2().data())
                        .build())
                .flatMap(fileDataRepository::save)
                .then(fileInfo);
    }

    @Override
    @Transactional
    public Mono<FileInfo> update(FilePart filePart, FileInfo fileInfo, Integer fileDataVersion) {
        Mono<FileInfo> newFileInfo = fileInfoRepository.findById(fileInfo.getId());

        Mono<FileData> newFileData;
        if (filePart != null && StringUtils.hasLength(filePart.filename())) {
            Mono<Content> content = Content.from(filePart);

            newFileInfo = Mono.zip(newFileInfo, content)
                    .map(t -> {
                        var info = t.getT1();
                        info.setFileName(filePart.filename());
                        info.setContentType(filePart.headers().getFirst(HttpHeaders.CONTENT_TYPE));
                        info.setLength(t.getT2().length());
                        info.setHashValue(t.getT2().hashValue());
                        return info;
                    });

            Mono<IdOnly> fileDataId = fileDataRepository.findByFileInfoId(fileInfo.getId(), IdOnly.class);
            newFileData = Mono.zip(fileDataId, content)
                    .map(t -> FileData.builder()
                            .id(t.getT1().id())
                            .fileInfoId(fileInfo.getId())
                            .data(t.getT2().data())
                            .version(fileDataVersion)
                            .build())
                    .flatMap(fileDataRepository::save);
        } else {
            newFileData = Mono.empty();
        }

        return newFileData.then(newFileInfo)
                .map(f -> {
                    if (StringUtils.hasLength(fileInfo.getFileName())) {
                        f.setFileName(fileInfo.getFileName());
                    }
                    f.setVersion(fileInfo.getVersion());
                    return f;
                })
                .flatMap(fileInfoRepository::save);
    }

    @Override
    public Mono<Void> delete(FileInfo fileInfo) {
        return fileInfoRepository.delete(fileInfo);
    }

    private record Content(ByteBuffer data, Integer length, String hashValue) {
        private static Mono<Content> from(FilePart filePart) {
            return filePart.content()
                    .collectList()
                    .map(dataBuffers -> {
                        int length = dataBuffers.stream()
                                .mapToInt(DataBuffer::readableByteCount)
                                .sum();
                        var byteBuffer = ByteBuffer.allocate(length);
                        for (var dataBuffer : dataBuffers) {
                            try (var iterator = dataBuffer.readableByteBuffers()) {
                                iterator.forEachRemaining(byteBuffer::put);
                            }
                        }
                        var hashValue = new DigestUtils(MessageDigestAlgorithms.SHA_256)
                                .digestAsHex(byteBuffer.flip());
                        return new Content(byteBuffer.rewind(), length, hashValue);
                    })
                    .cache();
        }
    }
}
