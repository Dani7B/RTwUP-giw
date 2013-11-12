package storm.bolts;

import java.util.Map;

//import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.PageDictionary;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

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

	public void prepare(Map conf, TopologyContext context) {
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
