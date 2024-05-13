package guestbook.pictures;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class PngPictureHoster implements PictureHoster {
    protected byte[] createPng(BufferedImage picture) throws IOException {
        // Write png into a bytearray
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(picture, "png", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return imageBytes;
    }
}
