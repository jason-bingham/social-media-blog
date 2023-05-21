package Service;

import java.util.List;
import java.util.Optional;

import DAO.MessageDAO;
import Model.Message;

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

public class MessageService {
  
  public static Optional<Message> newMessage(Message message) {

    if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()
        || message.getMessage_text().length() >= 255) {
      // return empty Optional if message empty or too long
      return Optional.empty();

    } else {
      return MessageDAO.newMessage(message);
    }
  }
  
  public static List<Message> getAllMessages() {
    // No business logic to perform. Pass directly to DAO
    return MessageDAO.getAllMessages();
  }

  public static Optional<Message> getMessageById(int id) {
    // No business logic to perform. Pass directly to DAO
    return MessageDAO.getMessageById(id);
  }

  public static Optional<Message> deleteMessage(int id) {
    // No business logic to perform. Pass directly to DAO
    return MessageDAO.deleteMessage(id);
  }

  public static Optional<Message> updateMessage(int id, String updatedMessage) {

    // Null and length checks
    if (updatedMessage == null || updatedMessage.trim() == "" || updatedMessage.length() >= 255) {
      return Optional.empty();
    } else {
      return MessageDAO.updateMessage(id, updatedMessage);
    }
  }

  public static List<Message> getMessagesByUserId(int id) {
    // No business logic to perform. Pass directly to DAO
    return MessageDAO.getMessagesByUserId(id);
  }

}
