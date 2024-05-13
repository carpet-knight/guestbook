package guestbook.processor;

public interface PictureProcessorNotifier {
    void registerListener(PictureProcessListener listener);
}
