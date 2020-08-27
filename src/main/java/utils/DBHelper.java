package utils;

import java.sql.*;

public class DBHelper {
    private static Connection connection;
    private static final String DATABASE = ReadProperty.getValue("mysql.db");

    public static void saveTweet(String username, String mentionId, String mediaTweetId, String mediaUrl, String mediaTweetUser, String mediaTweetText) {
        String sql = "INSERT INTO tweet_records(username, mention_id, media_tweet_id, media_url, media_tweet_user, media_tweet_text) VALUES(?,?,?,?,?,?)";
        try {
            connection = DriverManager.getConnection(DATABASE);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, mentionId);
            ps.setString(3, mediaTweetId);
            ps.setString(4, mediaUrl);
            ps.setString(5,mediaTweetUser);
            ps.setString(6,mediaTweetText);
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
            PreparedStatement ps = connection.prepareStatement("SELECT mention_id FROM tweet_records WHERE mention_id =" + mentionId + ";");

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

    public static String lastSearchId() {
        StringBuilder sinceId = new StringBuilder();
        try {
            connection = DriverManager.getConnection(DATABASE);
            PreparedStatement ps = connection.prepareStatement("SELECT mention_id FROM tweet_records ORDER BY Id DESC LIMIT 1;");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                sinceId.append(rs.getString("mention_id"));
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sinceId.toString();
    }
}
