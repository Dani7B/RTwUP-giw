package expansion;

//import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import static org.testng.Assert.assertEquals;


/**
 * @author Daniele Morgantini, Matteo Moci ( matteo (dot) moci (at) gmail (dot) com ), Gabriele de Capoa
 */

public class UrlExpansionTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlExpansionTestCase.class);
    
    private static URL expandUrl(URL shortened) throws IOException {

 	   final HttpURLConnection connection = (HttpURLConnection) shortened.openConnection(Proxy.NO_PROXY); 
     
        final String temp = connection.getHeaderField("Location");
        
        URL expandedUrl = null;
 		if (temp != null){
 			connection.disconnect();
 			LOGGER.info(temp);
 			expandedUrl = expandUrl(new URL(temp));
 		}
 		else{
 			connection.getHeaderFields();
 			expandedUrl = connection.getURL();
 			LOGGER.info(expandedUrl.getHost());
 	        LOGGER.info(expandedUrl.toString());
 			connection.disconnect();
 		}
 		
        return expandedUrl;
    }
    
    @Test
    public void shouldExpand_bitly() throws IOException {

        final String shortened = "http://bit.ly/12UK40Y";
        final URL shortenedUrl = new URL(shortened);
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);

        final URL expandedUrl = expandUrl(shortenedUrl);

        assertEquals(expandedUrl, expectedUrl);
    }
    
    @Test
    public void shouldExpand_tinyurl() throws IOException {

        final String shortened = "http://tinyurl.com/opn5s25";
        final URL shortenedUrl = new URL(shortened);
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);

        final URL expandedUrl = expandUrl(shortenedUrl);

        assertEquals(expandedUrl, expectedUrl);
    }

    @Test
    public void shouldExpand_googl() throws IOException {

        final String shortened = "http://goo.gl/6jPEK";
        final URL shortenedUrl = new URL(shortened);
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);
		
		final URL expandedUrl = expandUrl(shortenedUrl);

        assertEquals(expandedUrl, expectedUrl);
    }

    @Test
    public void shouldNotExpand() throws IOException {

        final String shortened = "https://github.com/Dani7B/RTwUP";
        final URL shortenedUrl = new URL(shortened);
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);

        final URL expandedUrl = expandUrl(shortenedUrl);

        assertEquals(expandedUrl, expectedUrl);
    }
    
    @Test
    public void shouldExpandUrlsShortenedSeveralTimes() throws IOException {
    
        final String shortenedSeveralTimes = "http://bit.ly/111udQI";
        final URL shortenedUrl = new URL(shortenedSeveralTimes);
        final String expected = "https://github.com/Dani7B/RTwUP";
        final URL expectedUrl = new URL(expected);

        final URL expandedUrl = expandUrl(shortenedUrl);

        assertEquals(expandedUrl, expectedUrl);
    }
}
