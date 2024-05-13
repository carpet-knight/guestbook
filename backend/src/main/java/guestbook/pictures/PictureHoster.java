package guestbook.pictures;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface PictureHoster {
    public String hostPicture(BufferedImage picture) throws IOException;
}
