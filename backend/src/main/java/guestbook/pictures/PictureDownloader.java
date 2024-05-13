package guestbook.pictures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public interface PictureDownloader {
    BufferedImage downloadPicture(URL pictureUrl) throws IOException;
}
