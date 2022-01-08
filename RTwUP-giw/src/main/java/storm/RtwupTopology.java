package storm;

import storm.bolts.ExpanderBolt; 
import storm.bolts.RedisPublisherBolt;
import storm.bolts.URLCounterBolt;
import storm.spouts.TwitterSpout;

import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.utils.*;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * @author Gabriele Proni, Gabriele de Capoa, Daniele Morgantini
 * 
 */
public class RtwupTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("filteredStream", (IRichSpout) new TwitterSpout(), 1);
		builder.setBolt("expander", new ExpanderBolt(), 5).shuffleGrouping(
				"filteredStream");
		builder.setBolt("urlCounter", new URLCounterBolt(), 5).fieldsGrouping(
				"expander", new Fields("expanded_url_domain"));
		builder.setBolt("RedisPublisher", new RedisPublisherBolt(), 1).shuffleGrouping(
				"urlCounter");

		Config conf = new Config();
		conf.setDebug(true);
				
		try {

			Properties prop = new Properties();
			prop.load(RtwupTopology.class.getClassLoader().getResourceAsStream("config.properties"));
			
			conf.setNumWorkers(Integer.parseInt(prop.getProperty("workers","3")));
			conf.put("topN", Integer.parseInt(prop.getProperty("topN","10")));
			conf.put("host", prop.getProperty("host","127.0.0.1"));
			conf.put("sw0", Double.parseDouble(prop.getProperty("sw0","12.20")));
			conf.put("sw1", Double.parseDouble(prop.getProperty("sw1","41.60")));
			conf.put("ne0", Double.parseDouble(prop.getProperty("ne0","12.80")));
			conf.put("ne1", Double.parseDouble(prop.getProperty("ne1","42.10")));
			conf.put("consumerKey", prop.getProperty("consumerKey"));
			conf.put("consumerSecret", prop.getProperty("consumerSecret"));
			conf.put("tokenKey", prop.getProperty("tokenKey"));
			conf.put("tokenSecret", prop.getProperty("tokenSecret"));
				
			final String topologyName = prop.getProperty("topologyName","RTwUP");
			final boolean isProduction = Boolean.valueOf(prop.getProperty("production","true"));
			
			if(isProduction) {
				StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
			} else {
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology(topologyName, conf, builder.createTopology());
				Utils.sleep(300000);
				cluster.killTopology(topologyName);
				cluster.shutdown();
				cluster.close();
			}
 
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}
