package guestbook.models;

import guestbook.GuestBookEntry;

import java.util.LinkedList;

public class EntryResponseListModel {
    private final LinkedList<EntryResponseModel> messages;

    public EntryResponseListModel(Iterable<GuestBookEntry> entries) {
        this.messages = new LinkedList<>();

        for (GuestBookEntry entry : entries) {
            messages.add(new EntryResponseModel(entry));
        }
    }

    public Iterable<EntryResponseModel> getMessages() {
        return messages;
    }
}
