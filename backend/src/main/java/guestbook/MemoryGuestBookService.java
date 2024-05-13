package guestbook;

import guestbook.processor.PictureProcessListener;
import guestbook.processor.PictureProcessor;
import guestbook.processor.PictureProcessorNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("memory")
@Scope("singleton")
public class MemoryGuestBookService implements GuestBookService, PictureProcessListener {
    private final Deque<GuestBookEntry> entries;
    private final ConcurrentHashMap<String, GuestBookEntryRequest> entryMap;
    private final PictureProcessor processor;
    private final Logger logger;
    private final int CAPACITY = 10;

    public MemoryGuestBookService(PictureProcessor processor) {
        entries = new ArrayDeque<>(CAPACITY);
        entryMap = new ConcurrentHashMap<>();
        logger = LoggerFactory.getLogger(MemoryGuestBookService.class);
        this.processor = processor;

        if (processor instanceof PictureProcessorNotifier)
            ((PictureProcessorNotifier) processor).registerListener(this);
    }

    @Override
    public void schedule(GuestBookEntryRequest request) {
        entryMap.put(request.getEntry().getEntryId(), request);
        processor.schedule(request);
    }

    @Override
    public GuestBookEntryRequest getEntry(String id) {
        return entryMap.get(id);
    }

    @Override
    public Iterable<GuestBookEntry> getLastTenEntries() {
        return new LinkedList<>(entries);
    }

    @Override
    public void processingComplete(GuestBookEntryRequest request) {
        if (request.getState() != EntryRequestState.READY)
            return;

        if (entries.size() == CAPACITY)
            entries.removeLast();

        entries.addFirst(request.getEntry());
    }

    @Override
    public void pictureSizeReady(GuestBookEntryRequest request, String size) {
        // Do nothing
    }
}
