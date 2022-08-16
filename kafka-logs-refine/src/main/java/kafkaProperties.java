import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;


public class kafkaProperties {
    private static Map<String, Object> kafkaPropsMap = null;


    public static Map<String, Object> getKafkaPropsMap() {
        return kafkaPropsMap;
    }



    public static void loadKafkaPropsMap() {
        kafkaPropsMap = new HashMap<String, Object>();
        kafkaPropsMap.put("kafka.topic", "spa");

        kafkaPropsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaPropsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "group4");
        kafkaPropsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaPropsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaPropsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        kafkaPropsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        kafkaPropsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        kafkaPropsMap.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "10000000");
    }

}
