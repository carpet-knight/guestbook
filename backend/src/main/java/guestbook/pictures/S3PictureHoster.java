package guestbook.pictures;

import guestbook.AwsSettings;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

@Component
@Profile("s3")
public class S3PictureHoster extends PngPictureHoster {
    private final AwsSettings settings;

    public S3PictureHoster(AwsSettings settings){
        this.settings = settings;
    }

    @Override
    public String hostPicture(BufferedImage picture) throws IOException {
        String fileName = UUID.randomUUID().toString() + ".png";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(settings.getBucket())
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType("image/png")
                .build();

        byte[] image = createPng(picture);

        settings.getS3().putObject(request, RequestBody.fromBytes(image));

        return settings.getUrlPrefix() + "/" + fileName;
    }
}
