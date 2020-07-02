import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

/**
 * This class tests uses Twitter Stream API to connect to the Twitter service
 * and prints on screen only the statuses with links tweeted from Rome urban
 * area.
 * 
 * @author Daniele Morgantini
 * 
 **/
public class Test {

	private static double[][] bbox;

	public static void main(String[] args) throws IOException {

		File file = new File("resources/config.properties");
		FileInputStream fis = new FileInputStream(file);

		Properties prop = new Properties();
		prop.load(fis);

		final double[] sw = new double[] { Double.parseDouble(prop.getProperty("sw0", "12.20")),
				Double.parseDouble(prop.getProperty("sw1", "41.60")) };
		final double[] ne = new double[] { Double.parseDouble(prop.getProperty("ne0", "12.80")),
				Double.parseDouble(prop.getProperty("ne1", "42.10")) };
		bbox = new double[][] { sw, ne };

		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

		twitterStream.setOAuthConsumer(prop.getProperty("consumerKey"), prop.getProperty("consumerSecret"));
		AccessToken accessToken = new AccessToken(prop.getProperty("tokenKey"), prop.getProperty("tokenSecret"));
		twitterStream.setOAuthAccessToken(accessToken);

		StatusListener listener = new StatusListener() {

			private boolean isInRange(GeoLocation gl, double[][] bbox) {
				double[] sw = bbox[0];
				double[] ne = bbox[1];
				double latitude = gl.getLatitude();
				double longitude = gl.getLongitude();
				if ((latitude >= sw[1] && latitude <= ne[1]) && (longitude >= sw[0] && longitude <= ne[0]))
					return true;
				return false;
			}

			public void onStatus(Status status) {
				if (status.getURLEntities().length != 0) {
					GeoLocation gl = status.getGeoLocation();
					if (gl == null || isInRange(gl, bbox))
						System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
				}
			}

			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};

		twitterStream.addListener(listener);
		FilterQuery query = new FilterQuery();
		query.locations(bbox);
		twitterStream.filter(query);

	}
}