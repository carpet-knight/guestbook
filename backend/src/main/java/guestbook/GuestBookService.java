package guestbook;

public interface GuestBookService {
    void schedule(GuestBookEntryRequest request);

    GuestBookEntryRequest getEntry(String id);

    Iterable<GuestBookEntry> getLastTenEntries();
}
