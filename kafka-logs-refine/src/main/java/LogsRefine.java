import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LogsRefine {
    private final static Logger logger = LoggerFactory.getLogger(LogsRefine.class);
    public static void main(String[] args) {
try {
    kafkaProperties.loadKafkaPropsMap();
    Map<String, Object> kafkaPropsMap = kafkaProperties.getKafkaPropsMap();
    KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(kafkaPropsMap);
    consumer.subscribe(Arrays.asList("spa"));

    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.1.35", 9200, "http")));
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(Long.parseLong("50")));
        logger.info("Polled " + records.count() + " On Partition " + records.partitions());

        if (records.count() == 0) {
            logger.info("Consumer.poll - no Record to consume, On Partition " + records.partitions());
            continue;
        }

        for (ConsumerRecord<String, String> record : records) {
               logger.info("Printing record: "+record.value());
            LocalDateTime now = LocalDateTime.now();
            String[] rec = record.value().split("~");
            //logger.info("Print rec value: "+rec[0]+"1 "+rec[1]+" "+rec[2]+" "+rec[3]+" "+rec[4]+" "+rec[5]+" "+rec[6]+" "+rec[7]);
            LogStructure logStruct = new LogStructure();

            if (rec.length == 6) { //CLF
                logStruct.setIp(rec[0].trim());
                logStruct.setDateTime(rec[1].trim().substring(1,rec[1].trim().length()-1));
                logStruct.setReqType(rec[2].trim());
                logStruct.setUrl(rec[3].trim());
                logStruct.setResponse(rec[4].trim());
                logStruct.setBytes(Integer.parseInt(rec[5].trim()));
                logStruct.setReferrer(null);
                logStruct.setUserAgent(null);
            } else { //ELF
                logStruct.setIp(rec[0].trim());
                logStruct.setDateTime(rec[1].trim().substring(1,rec[1].trim().length()-1));
                logStruct.setReqType(rec[2].trim());
                logStruct.setUrl(rec[3].trim());
                logStruct.setResponse(rec[4].trim());
                logStruct.setBytes(Integer.parseInt(rec[5].trim()));
                logStruct.setReferrer(rec[6].trim());
                logStruct.setUserAgent(rec[7].trim());
            }

            //-------------------Request Type Aggregation Index-----------------------------------
            HashMap<String, String> reqmap = new HashMap<>();
            reqmap.put("requestType", logStruct.getReqType());
            reqmap.put("timeStamp", logStruct.getDateTime());
            IndexRequest reqindexRequest = new IndexRequest("reqtypeagg");
            reqindexRequest.source(reqmap);
            try {
                IndexResponse response = client.index(reqindexRequest, RequestOptions.DEFAULT);
                System.out.println("response: " + response.getResult());
            } catch (Exception e) {
                System.out.println("Record inserted: " + e.getMessage());
            }

 //-------------------Response Type Aggregation Index----------------------------------------------------
            HashMap<String, String> resmap = new HashMap<>();
            resmap.put("timeStamp", logStruct.getDateTime());
            resmap.put("responseType", logStruct.getResponse());
            IndexRequest resindexRequest = new IndexRequest("restypeagg");
            resindexRequest.source(resmap);
            try {
                IndexResponse response = client.index(resindexRequest, RequestOptions.DEFAULT);
                System.out.println("response: " + response.getResult());
            } catch (Exception e) {
                System.out.println("Record inserted: " + e.getMessage());
            }


// -------------------All details Index----------------------------------------------------
//                HashMap<String,String> allmap = new HashMap<>();
//                allmap.put("timeStamp", );
//                allmap.put("responseType", );
//                IndexRequest allindexRequest = new IndexRequest("alldetailsindex");
//                resindexRequest.source(resmap);
//                try {
//                    IndexResponse response = client.index(allindexRequest, RequestOptions.DEFAULT);
//                    System.out.println("response: " + response.getResult());
//                }catch(Exception e) {
//                    System.out.println("Record inserted: " + e.getMessage());
//                }
        }
    }
}catch(Exception e){
    System.out.println("Exception: "+e);
    System.exit(1);
}
    }
}
