package guestbook;

public class GuestBookEntryRequest {
    private final GuestBookEntry entry;
    private EntryRequestState state;
    private String statusMessage;

    public GuestBookEntryRequest(GuestBookEntry _entry) {
        this.entry = _entry;
        this.state = EntryRequestState.PENDING;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public EntryRequestState getState() {
        return state;
    }

    public void setState(EntryRequestState state) {
        this.state = state;
    }

    public GuestBookEntry getEntry() {
        return entry;
    }
}
