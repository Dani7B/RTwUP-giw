package storm;

import storm.bolts.ExpanderBolt; 
import storm.bolts.RedisPublisherBolt;
import storm.bolts.URLCounterBolt;
import storm.spouts.TwitterSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.utils.*;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
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
				
		if (args != null && args.length > 0) {
			
			conf.setNumWorkers(3);

			conf.put("topN", Integer.parseInt(args[1])); //assuming that topN is the second argument
			conf.put("host", args[2]);
			conf.put("sw0", Double.parseDouble(args[3]));
			conf.put("sw1", Double.parseDouble(args[4]));
			conf.put("ne0", Double.parseDouble(args[5]));
			conf.put("ne1", Double.parseDouble(args[6]));
			
			try {
				StormSubmitter.submitTopology(args[0], conf,
						builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		} else {
			conf.put("topN", 10);
			conf.put("host", "127.0.0.1");
			conf.put("sw0", 12.20);
			conf.put("sw1", 41.60);
			conf.put("ne0", 12.80);
			conf.put("ne1", 42.10);
			
			try{
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology("RTwUP", conf, builder.createTopology());
				Utils.sleep(300000);
				cluster.killTopology("RTwUP");
				cluster.shutdown();
				cluster.close();
			}catch (Exception e){
				System.err.println("Error");
				e.printStackTrace();
			}
		}

	}
}
