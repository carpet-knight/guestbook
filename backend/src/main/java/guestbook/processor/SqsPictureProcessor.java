package guestbook.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guestbook.AwsSettings;
import guestbook.GuestBookEntryRequest;
import guestbook.pictures.PictureSizeSettings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Profile("sqs")
@Component
public class SqsPictureProcessor implements PictureProcessor {
    private final Logger logger = LoggerFactory.getLogger(SqsPictureProcessor.class);
    private final AwsSettings settings;

    public SqsPictureProcessor(AwsSettings settings){
        this.settings = settings;
    }

    @Override
    public void schedule(@NotNull GuestBookEntryRequest request) {
        try {
            PictureRequestMessage pictureRequestMessage = new PictureRequestMessage(request);
            ObjectMapper objectMapper = new ObjectMapper();
            String messageBody = objectMapper.writeValueAsString(pictureRequestMessage);

            settings.getSqs().sendMessage(SendMessageRequest.builder()
                    .messageBody(messageBody)
                    .queueUrl(settings.getQueueUrl())
                    .build());

            logger.info("Added entry {} to queue {}", request.getEntry().getEntryId(), settings.getQueueUrl());

        } catch (JsonProcessingException e) {
            logger.error("Couldn't serialize request to JSON", e);
        }
    }

    class PictureRequestMessage {
        private final String entryId;
        private final String sourcePictureUrl;
        private final List<Map<String, Object>> sizes;

        PictureRequestMessage(@NotNull GuestBookEntryRequest request){
            this.entryId = request.getEntry().getEntryId();
            this.sourcePictureUrl = request.getEntry().getSourcePictureUrl().toString();

            this.sizes = new LinkedList<>();

            for (Map.Entry<String, Integer> entry : PictureSizeSettings.getSizes().entrySet()){
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("size", entry.getKey());
                map.put("width", entry.getValue());
                sizes.add(map);
            }
        }

        @NotNull
        public String getEntryId() {
            return entryId;
        }

        @NotNull
        public String getSourcePictureUrl() {
            return sourcePictureUrl;
        }

        @NotNull
        public List<Map<String, Object>> getSizes() {
            return sizes;
        }
    }
}
