package storm.spouts;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.auth.AccessToken;
import org.apache.storm.topology.IRichSpout;



/** 
 * 
 * This spout listens to tweet stream, then filters the tweets by location (e.g. city of Rome)
 * and retrieves only the links contained in tweets.
 * 
 * @author Gabriele de Capoa, Gabriele Proni, Daniele Morgantini
 * 
 * **/

public class TwitterSpout implements IRichSpout {

	private static final long serialVersionUID = 1L;

	private LinkedBlockingQueue<Status> queue = null;
	private SpoutOutputCollector collector;
	private TwitterStream ts = null;
	private double[][] bbox = null;

	public void nextTuple() {
		try {
			Status retrieve = queue.take();
			URLEntity[] urls = retrieve.getURLEntities();
			for (URLEntity url : urls)
				this.collector.emit(new Values(url.getExpandedURL()));
		} catch (InterruptedException e) {
			System.err.println("ERRORE SULLO SPOUT: " + e.getMessage());
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("url"));
		
	} 

	public void close(){
		this.ts.shutdown();
	}

	public void execute(Tuple input) {
		try {
			Status retrieve = queue.take();
			URLEntity[] urls = retrieve.getURLEntities();
			for (URLEntity url : urls)
				this.collector.emit(new Values(url.getExpandedURL()));
		} catch (InterruptedException e) {
			System.err.println("ERRORE SULLO SPOUT: " + e.getMessage());
		}
		
	}

	@Override
	public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
		this.bbox = new double[2][2];
		this.bbox[0][0] = (Double) conf.get("sw0");
		this.bbox[0][1] = (Double) conf.get("sw1");
		this.bbox[1][0] = (Double) conf.get("ne0");
		this.bbox[1][1] = (Double) conf.get("ne1");
		
		this.queue = new LinkedBlockingQueue<Status>();
		this.collector = collector;
		this.ts = new TwitterStreamFactory().getInstance();
		this.ts.setOAuthConsumer((String) conf.get("consumerKey"), (String) conf.get("consumerSecret"));
		AccessToken accessToken = new AccessToken((String) conf.get("tokenKey"),(String) conf.get("tokenSecret"));
		this.ts.setOAuthAccessToken(accessToken);

		StatusListener listener = new StatusListener() {
			
			private boolean isInRange(GeoLocation gl, double[][] bbox) {
				double[] sw = bbox[0];
				double[] ne = bbox[1];
				double latitude = gl.getLatitude();
				double longitude = gl.getLongitude();
				if((latitude>=sw[1] && latitude<=ne[1])&&(longitude>=sw[0] && longitude<=ne[0]))
					return true;
				return false;
			}

			public void onException(Exception arg0) {
			}

			public void onDeletionNotice(StatusDeletionNotice arg0) {
			}

			public void onScrubGeo(long arg0, long arg1) {
			}

			public void onStallWarning(StallWarning arg0) {
			}

			public void onStatus(Status status) {
				if(status.getURLEntities().length != 0) {
            		GeoLocation gl = status.getGeoLocation();
            		if(gl == null || isInRange(gl,bbox))
            			queue.add(status);
				}
			}

			public void onTrackLimitationNotice(int arg0) {
			}

		};

		this.ts.addListener(listener);
		FilterQuery query = new FilterQuery();
		query.locations(this.bbox);
		this.ts.filter(query);
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

	@Override
	public void ack(Object msgId) {	
	}

	@Override
	public void fail(Object msgId) {	
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
