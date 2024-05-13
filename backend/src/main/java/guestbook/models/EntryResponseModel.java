package guestbook.models;

import guestbook.GuestBookEntry;

import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class EntryResponseModel {
    private final GuestBookEntry entry;

    public EntryResponseModel(GuestBookEntry entry) {
        this.entry = entry;
    }

    public String getId() {
        return entry.getEntryId();
    }

    public String getName() {
        return entry.getAuthor();
    }

    public String getMessage() {
        return entry.getMessage();
    }

    public Map<String, URL> getImages() {
        return entry.getResizedPictures();
    }

    public ZonedDateTime getDatetime() {
        return entry.getCreatedAt().atZoneSameInstant(ZoneId.of("UTC"));
    }
}
