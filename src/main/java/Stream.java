import io.github.redouane59.twitter.IAPIEventListener;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.stream.StreamRules;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import io.github.redouane59.twitter.dto.tweet.TweetParameters;
import io.github.redouane59.twitter.dto.tweet.TweetType;
import utils.ReadProperty;
import utils.Utils;

import java.util.List;
import java.util.Objects;

public class Stream {

    private static final TwitterClient twitter = Utils.twitterClient();

    public static void main(String[] args) {

        String botUsername = ReadProperty.getValue("twitter.username");

        try {
            List<StreamRules.StreamRule> rules = twitter.retrieveFilteredStreamRules();

            if (rules == null) {
                twitter.addFilteredStreamRule(botUsername, "");
            } else {

                for (StreamRules.StreamRule rule : rules) {
                    if (!Objects.equals(rule.getValue(), botUsername)) {
                        twitter.deleteFilteredStreamRuleId(rule.getId());
                        twitter.addFilteredStreamRule(botUsername, "");
                    }
                }
            }

            System.out.println("Stream connected to: " + botUsername);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getMessage());
        }

        twitter.startFilteredStream(new IAPIEventListener() {
            @Override
            public void onStreamError(int i, String s) {
                System.out.println("Stream error: " + s);
                //{"title":"ConnectionException","detail":"Your subscription change is currently being provisioned, please try again in a minute.","connection_issue":"ProvisioningSubscription","type":"https://api.twitter.com/2/problems/streaming-connection"}
            }

            @Override
            public void onTweetStreamed(Tweet tweet) {
                try {
                    String tweetReferencedId = "";

                    if (tweet.getTweetType().equals(TweetType.QUOTED)) {
                        tweetReferencedId = tweet.getInReplyToStatusId();

                    } else if (tweet.getInReplyToStatusId() != null) {
                        tweetReferencedId = tweet.getInReplyToStatusId();
                    }

                    if (tweetReferencedId != null) {


                        Tweet mediaTweet = twitter.getTweet(tweetReferencedId);

                        if (mediaTweet.getMedia() != null && !mediaTweet.getMedia().isEmpty() && mediaTweet.getMedia().get(0).getType().equals("video")) {

                            String userToReply = tweet.getUser().getName();
                            String responseText = tweetMessage(tweetReferencedId);

                            try {
                                replyTweet(responseText, tweet.getId(), userToReply);
                            } catch (Exception e) {

                                if (e.getMessage().contains("User is over daily status update limit")) {
                                    System.out.println("limit reached");

                                } else {
                                    System.out.println(e.getMessage());
                                }

                            }

                        }
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onUnknownDataStreamed(String s) {

            }

            @Override
            public void onStreamEnded(Exception e) {

            }
        });

    }

    private static String getLink(String tweetId) {
        return ReadProperty.getValue("website.url") + "/video/" + tweetId;
    }


    private static String tweetMessage(String username) {
        String[] messages = {"Yes! video, it's here: ", "Alright, i got this: ", "Video? Here we go: ", "Yes! video is ready: ", "Yes, Video! At your service: "};
        int rand = (int) (messages.length * Math.random());

        return messages[rand] + getLink(username);
    }

    private static void replyTweet(String text, String inReplyToTweetId, String whoMentionedMe) {

        TweetParameters tweetParams = TweetParameters.builder().text(text).reply(TweetParameters.Reply.builder().inReplyToTweetId(inReplyToTweetId).build()).build();

        try {
            Tweet tweet = twitter.postTweet(tweetParams);
            System.out.println("Replied to: " + whoMentionedMe + " - Reply Id: " + tweet.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
