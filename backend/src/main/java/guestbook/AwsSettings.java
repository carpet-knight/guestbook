package guestbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@ConfigurationProperties("guestbook.aws")
public class AwsSettings {
    private String bucket;
    private String urlPrefix;
    private Region region;
    private String queueUrl;
    private Environment env;
    private S3Client s3;
    private SqsClient sqs;

    @PostConstruct
    public void verify() {
        Set<String> profiles = new HashSet<>(Arrays.asList(this.env.getActiveProfiles()));

        if (profiles.contains("s3")) {
            if (bucket == null)
                throw new IllegalStateException("S3 Profile selected, please specify the bucket name");
        }

        if (profiles.contains("sqs")) {
            if (queueUrl == null)
                throw new IllegalStateException("SQS Profile selected, please specify the queue name");
        }
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String value) {
        this.bucket = value;
    }

    public String getUrlPrefix() {
        String prefix = this.urlPrefix;
        if (prefix == null) {
            if (region == null) {
                prefix = String.format("http://%s.s3.amazonaws.com", this.bucket);
            } else {
                prefix = String.format("http://%s.s3.%s.amazonaws.com", this.bucket, getRegion());
            }
        }
        return prefix;
    }

    public void setUrlPrefix(String value) {
        this.urlPrefix = value;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = Region.of(region);
    }

    public String getQueueUrl() {
        return queueUrl;
    }

    public void setQueueUrl(String value) {
        this.queueUrl = value;
    }

    public S3Client getS3() {
        if (this.s3 == null) {
            S3ClientBuilder builder = S3Client.builder();

            if (this.region != null)
                builder.region(this.region);

            this.s3 = builder.build();
        }

        return this.s3;
    }

    public SqsClient getSqs() {
        if (this.sqs == null) {
            SqsClientBuilder builder = SqsClient.builder();

            if (this.region != null)
                builder.region(this.region);

            this.sqs = builder.build();
        }

        return this.sqs;
    }

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }
}
