package guestbook.processor;

import guestbook.GuestBookEntryRequest;

public interface PictureProcessListener {
    void processingComplete(GuestBookEntryRequest request);

    void pictureSizeReady(GuestBookEntryRequest request, String size);
}
