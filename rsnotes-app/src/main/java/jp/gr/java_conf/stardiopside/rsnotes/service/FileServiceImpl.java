package jp.gr.java_conf.stardiopside.rsnotes.service;

import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileData;
import jp.gr.java_conf.stardiopside.rsnotes.data.entity.FileInfo;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.FileDataRepository;
import jp.gr.java_conf.stardiopside.rsnotes.data.repository.FileInfoRepository;
import jp.gr.java_conf.stardiopside.rsnotes.data.value.DownloadData;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public Mono<FileInfo> save(FilePart filePart) {
        Mono<Content> content = Content.from(filePart);

        Mono<FileInfo> fileInfo = content
                .flatMap(c -> fileInfoRepository.save(FileInfo.builder()
                        .fileName(filePart.filename())
                        .contentType(filePart.headers().getFirst(HttpHeaders.CONTENT_TYPE))
                        .length(c.length())
                        .hashValue(c.hashValue())
                        .build()))
                .cache();

        return Mono.zip(fileInfo, content)
                .flatMap(t -> fileDataRepository.save(FileData.builder()
                        .fileInfoId(t.getT1().getId())
                        .data(t.getT2().data())
                        .build()))
                .then(fileInfo);
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
