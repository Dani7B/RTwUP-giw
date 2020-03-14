package storm.bolts;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import storage.PageDictionary;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;

/**
 * This bolt publishes the URL ranking to Redis.
 * 
 * @author Gabriele de Capoa, Gabriele Proni, Daniele Morgantini
 *
 */

public class RedisPublisherBolt extends BaseBasicBolt{

	private static final long serialVersionUID = 1L;
	private JedisPool pool = null;
	private Jedis jedis = null;

	private long topN;
	private PageDictionary counts;
	
	@Override
	public void prepare(Map<String,Object> topoConf, TopologyContext context){
		String host = (String) topoConf.get("host");
		this.pool = new JedisPool(new JedisPoolConfig(), host);
		this.jedis = this.pool.getResource();
		this.topN = (Long) topoConf.get("topN");
		this.counts = PageDictionary.getInstance();
	}
	
	public void execute(Tuple input, BasicOutputCollector collector) {
				
		String ranking = this.counts.getTopNelementsStringified(this.topN);
		this.jedis.publish("RTwUP", ranking);		
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	@Override
	public void cleanup(){
		this.jedis.close();
		this.pool.close();
		this.pool.destroy();
	}

}
