package part2;

import twitter4j.*;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class TwitterDownloader {

    private static final String APIKEY = "Hu68KYtlont0pmU08Gl64VnQz";
    private static final String APISECRETKEY 	= "TzITg1hwIWN8xoReo7LAnwgoO7OcUTR17lvXQNMmvrkLr4QtHo";
    private static final String TOPIC = "nba";
    // default is 15
    private static final int MAX_PER_QUERY = 100;
    private int MAX_QUERIES = 10;

    /**
     * it's used to get the OAuth2Token aka bearer token for api authentication.
     * @return OAuth2Token
     */
    public OAuth2Token getToken() {
        OAuth2Token token = null;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(APIKEY).setOAuthConsumerSecret(APISECRETKEY);

        try {
            token = new TwitterFactory(cb.build()).getInstance().getOAuth2Token();
        }catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    /**
     * it's used to get a application-only Twitter object
     * @return Twitter Object
     */
    public Twitter getTwitter(){
        OAuth2Token token = getToken();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuth2TokenType(token.getTokenType());
        cb.setOAuth2AccessToken(token.getAccessToken());
        cb.setApplicationOnlyAuthEnabled(true);
        cb.setOAuthConsumerKey(APIKEY);
        cb.setOAuthConsumerSecret(APISECRETKEY);

        return new TwitterFactory(cb.build()).getInstance();
    }

    public void getTweets(int MAX_QUERIES, String fromDate, String toDate){
        Twitter twitter = getTwitter();
        int countTweets = 0;
        long maxID = -1;
        try {
            //check how far will we reach the search limit
            Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
            RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
            System.out.printf("Calls usage : %d / %d, Limit resets in %d seconds\n",
                    searchTweetsRateLimit.getRemaining(),
                    searchTweetsRateLimit.getLimit(),
                    searchTweetsRateLimit.getSecondsUntilReset());
            System.out.printf("Search tweets from %s to %s\n", fromDate, toDate);
            for(int i = 0; i < MAX_QUERIES; i++){
                if(searchTweetsRateLimit.getRemaining() == 0){
                    System.out.printf("Sleep for %d seconds until limit resets\n", searchTweetsRateLimit.getSecondsUntilReset());
                    Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset() + 5) * 1000);
                }

                Query q= new Query(TOPIC);
                q.setCount(MAX_PER_QUERY);
                q.resultType(Query.RECENT);
                q.since(fromDate);
                q.until(toDate);
                if(maxID != -1) q.setMaxId(maxID - 1);

                QueryResult result = twitter.search(q);
                countTweets += result.getTweets().size();
                System.out.printf("Tweets searched are %d\n", countTweets);

                if(result.getTweets().size() == 0) break;

                PrintWriter out1 = null;
                try {
                    String home = System.getProperty("user.home");
                    File dir = new File(home + "/tweets");
                    dir.mkdir();
                    out1 = new PrintWriter(new FileWriter(home + "/tweets/" + fromDate + "-" +
                            toDate + ".txt",i != 0));

                    for (Status status : result.getTweets()) {
                        if (maxID == -1 || status.getId() < maxID) {
                            maxID = status.getId();
                        }

                        out1.write(status.getText());
                        //out1.write("\n");
                    }

                    out1.close();
                } catch (Exception ex) {
                    System.out.println("error: " + ex.toString());
                }

                searchTweetsRateLimit = result.getRateLimitStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * to get one-day intervals from current time
     *
     * @param numbers get how many one-day intervals
     * @return String Array represents one-day intervals, String[i][0] is fromDate, String[i][1] is toDate
     */
    public static String[][] getDates(int numbers){
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        String[][] dates = new String[numbers][2];
        for(int i = 0; i < numbers; i++){
            cal.setTime(date);
            dates[i][1] = simpleDateFormat.format(cal.getTime());
            cal.add(Calendar.DATE, -1);
            dates[i][0] = simpleDateFormat.format(cal.getTime());
            date = cal.getTime();
        }
        return dates;
    }


    public static void main(String[] args){
        TwitterDownloader td = new TwitterDownloader();
        String[][] dates = getDates(Integer.parseInt(args[0]));
        for(int i = 0; i < dates.length; i++) {
            td.getTweets(Integer.parseInt(args[1]), dates[i][0], dates[i][1]);
        }
    }
}
