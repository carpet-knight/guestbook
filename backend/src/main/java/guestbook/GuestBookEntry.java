package guestbook;

import guestbook.pictures.PictureSizeSettings;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class GuestBookEntry {
    private final String entryId;
    private final String author;
    private final String message;
    private final URL sourcePictureUrl;
    private final OffsetDateTime createdAt;
    private final Map<String, URL> resizedPictures;

    GuestBookEntry(String entryId, String author, String message, URL sourcePictureUrl, OffsetDateTime createdAt) {
        this.entryId = entryId;
        this.author = author;
        this.message = message;
        this.sourcePictureUrl = sourcePictureUrl;
        this.createdAt = createdAt;

        Map<String, Integer> sizes = PictureSizeSettings.getSizes();
        this.resizedPictures = new LinkedHashMap<>();
        for (String sizeName : sizes.keySet())
            this.resizedPictures.put(sizeName, null);
    }

    public GuestBookEntry(String author, String message, URL sourcePictureUrl, OffsetDateTime createdAt) {
        this(UUID.randomUUID().toString(), author, message, sourcePictureUrl, createdAt);
    }

    public String getEntryId() {
        return entryId;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public URL getSourcePictureUrl() {
        return sourcePictureUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return String.format("Entry[Author:'%s', Message: '%s']", this.author, this.message);
    }

    public Map<String, URL> getResizedPictures() {
        return resizedPictures;
    }
}
