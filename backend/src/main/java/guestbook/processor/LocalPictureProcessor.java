package guestbook.processor;

import guestbook.EntryRequestState;
import guestbook.GuestBookEntry;
import guestbook.GuestBookEntryRequest;
import guestbook.pictures.PictureDownloader;
import guestbook.pictures.PictureHoster;
import guestbook.pictures.PictureSizeSettings;
import guestbook.pictures.ScaleTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Component
@Profile("local")
public class LocalPictureProcessor implements PictureProcessor, PictureProcessorNotifier {
    private final MemoryGuestBookWorker worker;
    private final PictureDownloader downloader;
    private final PictureHoster hoster;
    private final BlockingDeque<GuestBookEntryRequest> entryQueue;
    private final Logger logger;
    private final List<PictureProcessListener> listeners;

    LocalPictureProcessor(PictureDownloader downloader, PictureHoster hoster) {
        this.downloader = downloader;
        this.hoster = hoster;

        listeners = new LinkedList<>();
        entryQueue = new LinkedBlockingDeque<>();
        logger = LoggerFactory.getLogger(LocalPictureProcessor.class);

        this.worker = new MemoryGuestBookWorker();
        new Thread(worker).start();
    }

    @Override
    public void schedule(GuestBookEntryRequest request) {
        try {
            entryQueue.putLast(request);
        } catch (InterruptedException e) {
            logger.info("The schedule method was interrupted");
        }
    }

    @Override
    public void registerListener(PictureProcessListener listener) {
        listeners.add(listener);
    }

    private class MemoryGuestBookWorker implements Runnable {
        Map<String, Integer> pictureSizes = PictureSizeSettings.getSizes();

        @Override
        public void run() {
            GuestBookEntryRequest entry;
            while (true) {
                try {
                    entry = entryQueue.takeFirst();
                    try {
                        process(entry);
                    } catch (Exception e) {
                        entry.setState(EntryRequestState.ERROR);
                        entry.setStatusMessage(e.getMessage());
                        logger.error("Exception occurred while processing request", e);

                        for (PictureProcessListener listener : listeners) {
                            listener.processingComplete(entry);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception occurred in worker", e);
                }
            }
        }

        private void process(GuestBookEntryRequest entryRequest) throws IOException {
            GuestBookEntry entry = entryRequest.getEntry();
            logger.info("Started processing entry " + entry.getEntryId());
            BufferedImage sourceImage;
            sourceImage = downloader.downloadPicture(entry.getSourcePictureUrl());

            for (String imageSize : entry.getResizedPictures().keySet()) {
                int width = pictureSizes.get(imageSize);

                ScaleTransformer scaler = new ScaleTransformer();
                BufferedImage transformed = scaler.transform(sourceImage, width);

                URL hostedUrl;

                String hostedImage = hoster.hostPicture(transformed);
                hostedUrl = new URL(hostedImage);

                entry.getResizedPictures().put(imageSize, hostedUrl);

                for (PictureProcessListener listener : listeners) {
                    listener.pictureSizeReady(entryRequest, imageSize);
                }
            }

            entryRequest.setState(EntryRequestState.READY);

            for (PictureProcessListener listener : listeners) {
                listener.processingComplete(entryRequest);
            }

            logger.info("Completed processing entry " + entry.getEntryId());
        }
    }
}
