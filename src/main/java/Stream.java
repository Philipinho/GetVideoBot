import io.github.redouane59.twitter.IAPIEventListener;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.stream.StreamRules;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import io.github.redouane59.twitter.dto.tweet.TweetParameters;
import io.github.redouane59.twitter.dto.tweet.TweetType;
import io.github.redouane59.twitter.dto.tweet.TweetV2;
import utils.DBHelper;
import utils.ReadProperty;
import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stream {

    private static final TwitterClient twitter = Utils.twitterClient();

    public static void main(String[] args) {

        String botUsername = ReadProperty.getValue("twitter.username");

        int retryCount = 0;
        int maxRetries = 5;

        while (retryCount < maxRetries) {
            try {
                List<StreamRules.StreamRule> rules = twitter.retrieveFilteredStreamRules();

                if (rules == null) {
                    twitter.addFilteredStreamRule(botUsername, "");
                } else {
                    for (StreamRules.StreamRule rule : rules) {
                        if (!Objects.equals(rule.getValue(), botUsername)) {
                            twitter.deleteFilteredStreamRuleId(rule.getId());
                            System.out.println(twitter.addFilteredStreamRule(botUsername, ""));
                        }
                    }
                }

                System.out.println("Stream connected to: " + botUsername);

                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception: " + e.getMessage());

                retryCount++;
                if (retryCount < maxRetries) {
                    try {
                        // Wait for 1 minute before retrying
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                } else {
                    System.out.println("Max retries reached. Exiting...");
                }
            }
        }
        //                //{"title":"ConnectionException","detail":"Your subscription change is currently being provisioned, please try again in a minute.","connection_issue":"ProvisioningSubscription","type":"https://api.twitter.com/2/problems/streaming-connection"}

        twitter.startFilteredStream(new IAPIEventListener() {
            @Override
            public void onStreamError(int i, String s) {
                System.out.println("Stream error: " + s);
            }

            @Override
            public void onTweetStreamed(Tweet tweet) {
                try {
                    String tweetReferencedId = "";
                    String mentionTweetId = tweet.getId();

                    if (tweet.getTweetType().equals(TweetType.QUOTED)) {
                        tweetReferencedId = tweet.getInReplyToStatusId();

                    } else if (tweet.getInReplyToStatusId() != null) {
                        tweetReferencedId = tweet.getInReplyToStatusId();
                    }

                    if (tweetReferencedId != null) {


                        Tweet mediaTweet = twitter.getTweet(tweetReferencedId);

                        if (mediaTweet.getMedia() != null && !mediaTweet.getMedia().isEmpty() && mediaTweet.getMedia().get(0).getType().equals("video")) {

                            TweetV2.MediaEntityV2 mediaEntity = (TweetV2.MediaEntityV2) mediaTweet.getMedia().get(0);
                            String thumbnail = mediaEntity.getPreviewImageUrl();
                            String mediaTweetUser = mediaTweet.getUser().getName();
                            String mediaTweetText = mediaTweet.getText();
                            String isSensitive = ""; // temporary
                            String videoUrl = ""; // don't really need it

                            boolean shouldReply = true;

                            // fix reply bug - a case were the bot replies where it is not intentionally called
                            // on its own video post or retweeted video posts

                            if (mediaTweetUser.equalsIgnoreCase(botUsername.split("@")[1]) // get username without @ symbol
                                    || mediaTweetUser.equalsIgnoreCase("crazyvideoclips")) {
                                // get number of times the bot appears in a tweet
                                if (getMentionCount(tweet.getText(), botUsername.toLowerCase()) == 1) {
                                    shouldReply = false;
                                }
                            }

                            if (shouldReply) {
                                DBHelper.saveTweet(tweet.getUser().getName(), tweet.getId(), mediaTweet.getId(), videoUrl, thumbnail, mediaTweetUser, mediaTweetText, isSensitive);

                                String userToReply = tweet.getUser().getName();
                                String responseText = tweetMessage(userToReply);

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

                    }
                } catch (Exception e) {
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

    public static int getMentionCount(String tweetText, String username) {
        String regex_url = "(?<=^|(?<=[^a-zA-Z0-9-_\\.]))@([A-Za-z]+[A-Za-z0-9_]+)";
        int count = 0;

        Pattern pattern = Pattern.compile(regex_url);
        Matcher matcher = pattern.matcher(tweetText.toLowerCase());

        while (matcher.find()) {
            if (matcher.group(0).equalsIgnoreCase(username)) {
                count += 1;
            }
        }

        return count;
    }

    private static String getUserPage(String username) {
        String userPage = ReadProperty.getValue("website.url") + "/" + username;
        String notice = "\n\nAd: Follow @CrazyVideoClips for crazy and viral videos.";
        return userPage + notice;
    }

    private static String tweetMessage(String username) {
        String[] messages = {"Yes! video, it's here: ", "Alright, i got this: ", "Video? Here we go: ", "Yes! video is ready: ", "Yes, Video! At your service: "};
        int rand = (int) (messages.length * Math.random());

        return messages[rand] + getUserPage(username);
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
