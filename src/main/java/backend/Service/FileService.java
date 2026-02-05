package backend.Service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public interface FileService {

    Mono<Void> uploadFile(String keyName, FilePart file);

    ResponseInputStream<GetObjectResponse> getFile(String keyName);
}
