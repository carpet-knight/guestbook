package guestbook.processor;

import guestbook.GuestBookEntryRequest;

public interface PictureProcessor {
    void schedule(GuestBookEntryRequest request);
}
