package storage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

/**
 * This class has a collection of all the URLs. It returns the stringified
 * version of the TopNelements in the map.
 * 
 * @author Daniele Morgantini, Gabriele de Capoa, Gabriele Proni
 * 
 */
public class PageDictionary {

	private static PageDictionary instance;
	private Map<DomainPageCouple, Integer> dictionary;
	private Gson gson;

	private PageDictionary() {
		this.dictionary = new ConcurrentHashMap<DomainPageCouple, Integer>();
		this.gson = new Gson();
	}

	public static synchronized PageDictionary getInstance() {
		if (instance == null)
			instance = new PageDictionary();
		return instance;
	}

	/**
	 * Adds a linked page to the dictionary
	 * 
	 * @param domain and page
	 * @return the updated counter
	 * 
	 */
	public int addToDictionary(String domain, String page) {
		DomainPageCouple dp = new DomainPageCouple(domain, page);
		Integer count = this.dictionary.get(dp);
		if (count == null) {
			count = 1;
			this.dictionary.put(dp, count);
		} else {
			count++;
			this.dictionary.put(dp, count);
		}
		return count;
	}

	/**
	 * Returns the stringified version of topNelements in the dictionary
	 * 
	 */
	public String getTopNelementsStringified(long topN) {
		/* Ordering all the pages by counter */
		DictionaryValueComparator bvc = new DictionaryValueComparator(dictionary);
		TreeMap<DomainPageCouple, Integer> sorted_map = new TreeMap<DomainPageCouple, Integer>(bvc);
		sorted_map.putAll(dictionary);

		/* Retrieving the topN pages and split them between appropriate domains */
		int i = 0;
		Map<String,DomainPageList> topNList = new HashMap<String,DomainPageList>();
		for (Map.Entry<DomainPageCouple, Integer> dp : sorted_map.entrySet()) {
			if (i < topN) {
				String domain = dp.getKey().getDomain();
				String page = dp.getKey().getPage();
				String count = dp.getValue().toString() + " times";
				
				DomainPageList dpl = topNList.get(domain);
				if(dpl == null)
					dpl = new DomainPageList();
				
				dpl.addPageCountToList(page, count);
				topNList.put(domain, dpl);
			} else
				break;
			i++;
		}
		return this.gson.toJson(topNList);
	}

	public Integer removeFromDictionary(String domain, String page) {
		DomainPageCouple dp = new DomainPageCouple(domain, page);
		return this.dictionary.remove(dp);
	}

}

class DictionaryValueComparator implements Comparator<DomainPageCouple> {

	Map<DomainPageCouple, Integer> base;

	public DictionaryValueComparator(Map<DomainPageCouple, Integer> base) {
		this.base = base;
	}

	public int compare(DomainPageCouple a, DomainPageCouple b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		}
	}
}