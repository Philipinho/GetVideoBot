import utils.DBHelper;

public class Run {
    private static int sleepTime = 240000; // 4 minutes in milliseconds

    public static void StartApplication(){

    while (true) {
    for (String mentionID : Bot.findMentions().keySet()){
        String tweetWithMedia = mentionID;
        String mentionTweet = Bot.findMentions().get(mentionID);

        try {

            String user = Bot.twitter.showStatus(Long.parseLong(mentionTweet)).getUser().getScreenName();

            if (DBHelper.getMention(mentionTweet).equals(mentionTweet)) {
                System.out.println("Record already exist.");
            }
            else {

                Bot.replyTweet(Bot.tweetMessage(user, mentionID), mentionTweet);

				DBHelper.saveTweet(user, mentionTweet, tweetWithMedia, Bot.fetchVideo(tweetWithMedia));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    try {
        System.out.println("Application is Sleeping for 4 minutes.");
        Thread.sleep(sleepTime);
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }

		}
    }
}