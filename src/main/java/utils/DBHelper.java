package utils;

import java.sql.*;

public class DBHelper {
    private static Connection connection;
    private static final String DATABASE = ReadProperty.getValue("mysql.db");

    public static void saveTweet(String username, String mentionId, String mediaTweetId, String mediaUrl, String thumbnail, String mediaTweetUser, String mediaTweetText, String isSensitive) {
        String sql = "INSERT INTO tweet_records(username, mention_id, media_tweet_id, media_url, thumbnail, media_tweet_user, media_tweet_text, is_sensitive) VALUES(?,?,?,?,?,?,?,?)";
        try {
            connection = DriverManager.getConnection(DATABASE);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, mentionId);
            ps.setString(3, mediaTweetId);
            ps.setString(4, mediaUrl);
            ps.setString(5, thumbnail);
            ps.setString(6,mediaTweetUser);
            ps.setString(7,mediaTweetText);
            ps.setString(8,isSensitive);
            ps.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getMention(String mentionId) {
        StringBuilder mention = new StringBuilder();
        try {
            connection = DriverManager.getConnection(DATABASE);
            PreparedStatement ps = connection.prepareStatement("SELECT mention_id FROM tweet_records WHERE mention_id = ?");

            ps.setString(1, mentionId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mention.append(rs.getString("mention_id"));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mention.toString();
    }

}
