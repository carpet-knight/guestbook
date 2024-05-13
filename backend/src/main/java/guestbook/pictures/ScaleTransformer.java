package guestbook.pictures;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.round;

@Primary
@Component
public class ScaleTransformer {
    public BufferedImage transform(BufferedImage source, int width) {
        float oldHeight = source.getHeight();
        float oldWidth = source.getWidth();

        int height = round((float) width * (oldHeight / oldWidth));


        Image tmp = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = scaledImage.createGraphics();
        graphics.drawImage(tmp, 0, 0, null);
        graphics.dispose();

        return scaledImage;
    }
}
