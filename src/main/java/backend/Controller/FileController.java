package backend.Controller;

import backend.Service.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

@RestController
@Log4j2
@RequestMapping("/api/v1/file")
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public String status() {
        return "OK";
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart file) {
        return fileService.uploadFile(file.filename(), file)
                .thenReturn(ResponseEntity.status(201).body("File Uploaded Success"));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            ResponseInputStream<GetObjectResponse> s3Stream = fileService.getFile(fileName);
            if (s3Stream == null) {
                return ResponseEntity.notFound().build();
            }

            GetObjectResponse meta = s3Stream.response();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(meta.contentLength() == null ? -1 : meta.contentLength())
                    .body(new InputStreamResource(s3Stream));

        } catch (NoSuchKeyException e) {
            return ResponseEntity.notFound().build();
        } catch (S3Exception e) {
            // log it if you want
            return ResponseEntity.status(502).build(); // bad gateway / upstream error
        }
    }


}
