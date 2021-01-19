package wirelessmesh.domain;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GooglePubsubClient {

    public void publish(String topic, ByteString eventByteString) {
        TopicName topicName = TopicName.of("akkaserverless-wirelessmesh-java", topic); // todo: get from env
        Publisher publisher = null;

        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build();
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(eventByteString).build();

            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            String messageId = messageIdFuture.get();
            System.out.println("Published message ID: " + messageId);
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Unable to publish to google pubsub-" + ex.getMessage());
        }
        finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();

                try {
                    publisher.awaitTermination(1, TimeUnit.MINUTES);
                }
                catch (Exception ex) {
                    Logger.getAnonymousLogger().log(Level.WARNING, "Unable shutdown google pubsub-" + ex.getMessage());
                }
            }
        }

    }
}
