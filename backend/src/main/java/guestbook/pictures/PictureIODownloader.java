package guestbook.pictures;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

@Component
public class PictureIODownloader implements PictureDownloader {
    @Override
    @NotNull
    public BufferedImage downloadPicture(URL pictureUrl) throws IOException {
        BufferedImage downloaded = ImageIO.read(pictureUrl);

        // From the docs:
        // If no registered ImageReader claims to be able to read the resulting stream, null is returned.

        if (downloaded == null)
            throw new IOException("Could not read picture file");

        return downloaded;
    }
}
