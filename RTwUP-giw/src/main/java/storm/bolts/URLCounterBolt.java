package storm.bolts;

import java.util.Map;

//import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.PageDictionary;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;

/**
 * This bolt counts the URL occurrences.
 * 
 * @author Gabriele de Capoa, Gabriele Proni, Daniele Morgantini
 * 
 */

public class URLCounterBolt extends BaseBasicBolt {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(URLCounterBolt.class);
	private PageDictionary counts;

	@Override
	public void prepare(Map<String,Object> topoConf, TopologyContext context) {
		//PropertyConfigurator.configure("src/main/resources/log4j.properties");
		this.counts = PageDictionary.getInstance();
	}
	

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String domain = input.getStringByField("expanded_url_domain");
		String path = input.getStringByField("expanded_url_complete");
		Integer count = this.counts.addToDictionary(domain,	path);

		String message = "Domain: " + domain + " URL: " + path + " Count: "	+ count;
		LOGGER.info(message);
		collector.emit(new Values(message));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("message"));
	}

}
