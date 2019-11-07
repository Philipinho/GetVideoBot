import twitter4j.*;

import java.util.*;

public class Bot {
    public static Twitter twitter = TwitterFactory.getSingleton();

    public static void replyTweet(String text, String id) {
        try {
            Status status = twitter.updateStatus(
                    new StatusUpdate(text)
                            .inReplyToStatusId(Long.parseLong(id)));

            System.out.println("Replied to: " + status.getInReplyToScreenName());

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public static String fetchMedia(String statusId) {
        String mediaUrl = "";
        try{
            Status status = twitter.showStatus(Long.parseLong(statusId));

            for (MediaEntity media : status.getMediaEntities()) {
                MediaEntity.Variant[] variants = media.getVideoVariants();

                List<Integer> bitrate = new ArrayList<>();

                for (int i =0; i < variants.length;i++) {
                    if (variants[i].getContentType().equals("video/mp4")) {
                        if (variants[i].getBitrate() == maxVariant(bitrate, variants[i].getBitrate())) {
                            mediaUrl = variants[i].getUrl();
                        }
                    }
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return mediaUrl;
    }

    public static int maxVariant(List<Integer> bitrate, int variant){
        bitrate.add(variant);
        Collections.sort(bitrate);
        return bitrate.get(bitrate.size()-1);
    }

    public static Map<String, String> findMentions() {

        Map<String,String> statusId = new HashMap<>();
        Paging paging = new Paging();
        paging.count(30);

        try {
            paging.setSinceId(twitter.getUserTimeline().get(0).getId());
  
            List<Status> mentionList = twitter.getMentionsTimeline(paging);
            
            String tweetReferenced = "-1";
            
            for (Status tweet : mentionList) {
                
                if (tweet.getQuotedStatus() != null){
                    tweetReferenced = String.valueOf(tweet.getQuotedStatusId());
                } else {
                    tweetReferenced = String.valueOf(tweet.getInReplyToStatusId());
                }
                
                ResponseList<Status> statuses = twitter.lookup(Long.parseLong(tweetReferenced));

                for (Status mediaFilter : statuses) {
                    MediaEntity[] mediaEntities = mediaFilter.getMediaEntities();

                    for (int i = 0; i < mediaEntities.length; i++) {
                        MediaEntity.Variant[] variants = mediaEntities[i].getVideoVariants();

                        for (int j = 0; j < variants.length; j++) {
                            if ((!tweetReferenced.equals("-1")) && variants[j].getContentType().equals("video/mp4")) {
                                String tweetId = String.valueOf(tweet.getId());
                                statusId.put(tweetReferenced, tweetId);
                            }
                        }
                    }
                }
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }

        return statusId;
    }

    public static String tweetMessage(String username, String tweetId){
        String userMention = "@" + username;
        String[] messages = {"Yes! video, it's here: ", "Alright, i got this: ", "Video? Here we go: ", "Yes! video is ready: ", "Yes, Video! At your service: "};
        int rand = (int)(messages.length * Math.random());

        return userMention + " " + messages[rand] + fetchMedia(tweetId);
    }

}
