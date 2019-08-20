import twitter4j.TwitterException;
import utils.DBHelper;
import utils.ReadProperty;

import java.time.LocalDateTime;
import java.util.Map;

public class Run {

    public static void StartApplication(){

        while (true) {

            String mediaTweet = "";
            String mentionTweet = "";

            for (Map.Entry<String, String> tweetSet : Bot.findMentions().entrySet()){
                mediaTweet = tweetSet.getKey();
                mentionTweet = tweetSet.getValue();

                try {

                    String user = Bot.twitter.showStatus(Long.parseLong(mentionTweet)).getUser().getScreenName();
                    String mediaTweetUser = Bot.twitter.showUser(Long.parseLong(mediaTweet)).getScreenName();
                    String mediaTweetText = Bot.twitter.showStatus(Long.parseLong(mediaTweet)).getText();

                    if (DBHelper.getMention(mentionTweet).equals(mentionTweet)) {
                        System.out.println("Record already exist.");
                    }
                    else {

                        DBHelper.saveTweet(user, mentionTweet, mediaTweet, Bot.fetchMedia(mediaTweet), mediaTweetUser, mediaTweetText);

                        Bot.replyTweet(Bot.tweetMessage(user, mediaTweet), mentionTweet);
                    }

                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }

            try {
                System.out.println("Bot is sleeping.");
                Thread.sleep(Long.parseLong(ReadProperty.getValue("bot.sleeptime")));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }
}