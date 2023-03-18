package utils;

import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.signature.TwitterCredentials;

public class Utils {

    public static TwitterClient twitterClient() {

        return new TwitterClient(TwitterCredentials.builder()
                .accessToken(ReadProperty.getValue("tw.accessToken"))
                .accessTokenSecret(ReadProperty.getValue("tw.accessTokenSecret"))
                .apiKey(ReadProperty.getValue("tw.apiKey"))
                .apiSecretKey(ReadProperty.getValue("tw.apiSecretKey"))
                .build());
    }
}
