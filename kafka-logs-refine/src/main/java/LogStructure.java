import lombok.Data;


@Data
public class LogStructure {
    private String ip;
    private String dateTime;
    private String reqType;
    private String url;
    private String response;
    private Integer bytes;
    private String referrer;
    private String userAgent;



}
