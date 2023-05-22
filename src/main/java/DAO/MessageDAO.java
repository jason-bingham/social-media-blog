package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Model.Message;
import Util.ConnectionUtil;

/* Message Model:
 * public int message_id;
 * public int posted_by;
 * public String message_text;
 * public long time_posted_epoch;
 */

/* Message Table:
 * message_id int primary key auto_increment,
 * posted_by int,
 * message_text varchar(255),
 * time_posted_epoch bigint,
 * foreign key (posted_by) references  account(account_id)
 */

public class MessageDAO {

  public static Optional<Message> newMessage(Message message) {

    try {

      Connection connection = ConnectionUtil.getConnection();

      String sql = "insert into message values (default, ?, ?, ?)";

      PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      // Gotta remember to add the Statement.GET_GENERATED_KEYS parameter when I need to use the getGeneratedKeys() method below.

      statement.setInt(1, message.getPosted_by());
      statement.setString(2, message.getMessage_text());
      statement.setLong(3, message.getTime_posted_epoch());

      statement.executeUpdate();

      ResultSet rs = statement.getGeneratedKeys();

      if (rs.next()) {
        int message_id = rs.getInt(1);
        Message newMessage = new Message(message_id, message.getPosted_by(), message.getMessage_text(),
            message.getTime_posted_epoch());
        return Optional.of(newMessage);
      }

      // connection.close();

    } catch (SQLException e) {

      e.printStackTrace();

      return Optional.empty();

    }

    return Optional.empty();

  }

  public static List<Message> getAllMessages() {

    List<Message> messages = new ArrayList<>();

    try {

      Connection connection = ConnectionUtil.getConnection();

      String sql = "select * from message";

      Statement statement = connection.createStatement();
      // Remember that connection.prepareStatement(sql) is only for prepared statements. For regular statements it's .createStatement() with no parameters passed.
      ResultSet rs = statement.executeQuery(sql);

      while (rs.next()) {
        int message_id = rs.getInt("message_id");
        int posted_by = rs.getInt("posted_by");
        String message_text = rs.getString("message_text");
        long time_posted_epoch = rs.getLong("time_posted_epoch");
        Message nextMessage = new Message(message_id, posted_by, message_text, time_posted_epoch);
        messages.add(nextMessage);

      }

      // connection.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return messages;
  }

  public static Optional<Message> getMessageById(int id) {

    try {

      Connection connection = ConnectionUtil.getConnection();

      String sql = "select * from message where message_id = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, id);

      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int message_id = rs.getInt("message_id");
        int posted_by = rs.getInt("posted_by");
        String message_text = rs.getString("message_text");
        long time_posted_epoch = rs.getLong("time_posted_epoch");
        Message returnMessage = new Message(message_id, posted_by, message_text, time_posted_epoch);
        return Optional.of(returnMessage);
      }

      // connection.close();

    } catch (SQLException e) {

      e.printStackTrace();

      return Optional.empty();
    }
    return Optional.empty();
  }

  public static Optional<Message> deleteMessage(int id) {

    try {

      Connection connection = ConnectionUtil.getConnection();

      String sql = "select * from message where message_id = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, id);

      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int message_id = id;
        int posted_by = rs.getInt("posted_by");
        String message_text = rs.getString("message_text");
        long time_posted_epoch = rs.getLong("time_posted_epoch");
        Message returnMessage = new Message(message_id, posted_by, message_text, time_posted_epoch);
        return Optional.of(returnMessage);
      }

      // connection.close();

    } catch (SQLException e) {

      e.printStackTrace();

      return Optional.empty();
    }

    return Optional.empty();

  }

  public static Optional<Message> updateMessage(int id, String message) {

    try {

      Connection connection = ConnectionUtil.getConnection();

      String sql = "update message set message_text = ? where message_id = ?";
      PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, message);
      statement.setInt(2, id);

      statement.executeUpdate();

      // ResultSet rs = statement.getGeneratedKeys();
      // The above ResultSet didn't work, I guess because getGeneratedKeys() only returned message_id and message_text. So I just added another sql query to get all the parameters after updating:

      String sql2 = "select * from message where message_id = ?";
      PreparedStatement statement2 = connection.prepareStatement(sql2);
      statement2.setInt(1, id);
      ResultSet rs = statement2.executeQuery();

      while (rs.next()) {
        int message_id = id;
        int posted_by = rs.getInt("posted_by");
        String message_text = message;
        long time_posted_epoch = rs.getLong("time_posted_epoch");
        Message returnMessage = new Message(message_id, posted_by, message_text, time_posted_epoch);
        return Optional.of(returnMessage);
      }

      // connection.close();

    } catch (SQLException e) {

      e.printStackTrace();

      return Optional.empty();

    }

    return Optional.empty();

  }
  
  public static List<Message> getMessagesByUserId(int id) {

    List<Message> messages = new ArrayList<>();

    try {

      Connection connection = ConnectionUtil.getConnection();

      String sql = "select * from message where posted_by = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, id);
      // Remember that connection.prepareStatement(sql) is only for prepared
      // statements. For regular statements it's .createStatement() with no parameters
      // passed.
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        int message_id = rs.getInt("message_id");
        int posted_by = rs.getInt("posted_by");
        String message_text = rs.getString("message_text");
        long time_posted_epoch = rs.getLong("time_posted_epoch");
        Message nextMessage = new Message(message_id, posted_by, message_text, time_posted_epoch);
        messages.add(nextMessage);
      }

      // connection.close();

    } catch (SQLException e) {
      e.printStackTrace();
    } 
    
    return messages;

  }
  
}
