package guestbook.pictures;

import java.util.LinkedHashMap;
import java.util.Map;

public class PictureSizeSettings {
    public static Map<String, Integer> getSizes() {
        Map<String, Integer> sizes = new LinkedHashMap<>();
        sizes.put("small", 300);
        sizes.put("medium", 700);
        sizes.put("large", 1200);
        return sizes;
    }
}
