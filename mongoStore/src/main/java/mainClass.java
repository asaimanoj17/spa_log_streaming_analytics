
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.MongoClient;


import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class mainClass {

    private final static Logger logger = LoggerFactory.getLogger(mainClass.class);


    public static void main(String[] args) {

        kafkaProperties.loadKafkaPropsMap();
        Map<String, Object> kafkaPropsMap = kafkaProperties.getKafkaPropsMap();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(kafkaPropsMap);
        consumer.subscribe(Arrays.asList("spa"));

        while(true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(Long.parseLong("500")));
            logger.info("Polled "+records.count()+" On Partition "+records.partitions());

            if (records.count() == 0) {
                logger.info("Consumer.poll - no Record to consume, On Partition "+records.partitions());
                continue;
            }

            //Creating a MongoDB client
            MongoClient mongo = new MongoClient( "localhost" , 27017 );
            //Connecting to the database
            MongoDatabase database = mongo.getDatabase("faker-logs-store");

            for (ConsumerRecord<String, String> record : records) {
                logger.info("Printing record: "+record.value());

                //Preparing a document
                Document document = new Document();
                document.append("record", record.value());
                //Inserting the document into the collection
                database.getCollection("raw_logs").insertOne(document);
                logger.info("Record inserted successfully");



            }
        }
    }
}
