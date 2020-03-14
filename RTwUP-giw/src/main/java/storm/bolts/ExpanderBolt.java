package storm.bolts;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;

/**
 * This bolt expands the URL, if it is a shortned URL, until we retrieve the
 * effective URL.
 * 
 * @author Gabriele de Capoa, Gabriele Proni
 * 
 */

public class ExpanderBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;

	public void execute(Tuple input, BasicOutputCollector collector) {
		String url = input.getStringByField("url");
		URL testingUrl;
		try {
			testingUrl = new URL(url);
			URL	newUrl = expandUrl(testingUrl);
			collector.emit(new Values(newUrl.getHost(), newUrl
					.toString()));
		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
	}
	
	private static URL expandUrl(URL shortened) throws IOException {

	 	   final HttpURLConnection connection = (HttpURLConnection) shortened.openConnection(Proxy.NO_PROXY); 
	     
	        final String temp = connection.getHeaderField("Location");
	        
	        URL expandedUrl = null;
	 		if (temp != null){
	 			connection.disconnect();
	 			expandedUrl = expandUrl(new URL(temp));
	 		}
	 		else{
	 			connection.getHeaderFields();
	 			expandedUrl = connection.getURL();
	 			connection.disconnect();
	 		}
	 		
	        return expandedUrl;
	    }

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("expanded_url_domain", "expanded_url_complete"));
	}

}
