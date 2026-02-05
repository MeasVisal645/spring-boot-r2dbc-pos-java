package backend.ServiceImpl;

import backend.Service.FileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

@Service
@Log4j2
public class FileServiceImpl implements FileService {

    private final S3Client s3Client;
    private final String bucketName;
    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB

    public FileServiceImpl(S3Client s3Client, @Value("${r2.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public Mono<Void> uploadFile(String keyName, FilePart filePart) {

        return Mono.fromCallable(() -> Files.createTempFile("r2-", ".upload"))
                .flatMap(tmp ->
                        filePart.transferTo(tmp)
                                .then(Mono.fromRunnable(() -> {

                                    try {
                                        long size = Files.size(tmp);

                                        // ✅ size check here
                                        if (size > MAX_FILE_SIZE) {
                                            Files.deleteIfExists(tmp);
                                            throw new RuntimeException("File too large. Max 1MB allowed");
                                        }

                                        try (InputStream in = Files.newInputStream(tmp)) {

                                            PutObjectRequest req = PutObjectRequest.builder()
                                                    .bucket(bucketName)
                                                    .key(keyName)
                                                    .contentType(
                                                            filePart.headers().getContentType() != null
                                                                    ? Objects.requireNonNull(filePart.headers().getContentType()).toString()
                                                                    : null
                                                    )
                                                    .contentLength(size)
                                                    .build();

                                            s3Client.putObject(req, RequestBody.fromInputStream(in, size));
                                        }

                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }))
                                .subscribeOn(Schedulers.boundedElastic())
                                .doFinally(sig -> {
                                    try { Files.deleteIfExists(tmp); } catch (Exception ignored) {}
                                })
                )
                .then();
    }




    @Override
    public ResponseInputStream<GetObjectResponse> getFile(String keyName) {
        try {
            GetObjectRequest req = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            return s3Client.getObject(req);

        } catch (S3Exception e) {
            log.error("Failed to get file {} from bucket {}: {}", keyName, bucketName, e.awsErrorDetails().errorMessage(), e);
            return null;
        }
    }
}
