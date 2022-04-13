
import java.time.LocalDateTime;

public class Reply implements java.io.Serializable {
	
	String fromName;
	String fromHost;
	int fromPort;
	String toName;
	String toHost;
	int toPort;
	LocalDateTime date; 

}
