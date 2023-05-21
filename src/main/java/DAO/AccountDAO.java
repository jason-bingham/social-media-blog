// DAO receives data from service layer and performs SQL operations. Returns data from database.

package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import Model.Account;
import Util.ConnectionUtil;

/**
 * AccountDAO
 */
public class AccountDAO {

  public static Optional<Account> addAccount(Account account) {

    // try with resources automatically closes connection at the end of the block
    try (Connection connection = ConnectionUtil.getConnection()) {

      // SQL logic
      String sql = "insert into account values (default, ?, ?)";

      PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

      statement.setString(1, account.getUsername());
      statement.setString(2, account.getPassword());

      statement.executeUpdate();

      ResultSet rs = statement.getGeneratedKeys();

      if (rs.next()) {
        int account_id = rs.getInt(1);
        Account newAcc = new Account(account_id, account.getUsername(), account.getPassword());
        return Optional.of(newAcc);
      }

    // SQL exception will be thrown if the username is already taken because it's defined as unique in SocialMedia.sql
    } catch (SQLException e) {
      return Optional.empty();
    }

    // If the try block is unsuccessful for some other reason, also return an empty Optional.
    return Optional.empty();

  }

  public static Optional<Account> login(Account account) {

    try (Connection connection = ConnectionUtil.getConnection()) {

      String sql = "select * from account where username = ? and password = ?";

      PreparedStatement statement = connection.prepareStatement(sql);

      statement.setString(1, account.getUsername());
      statement.setString(2, account.getPassword());

      // This part is a little different from the first DAO method because queries require a different execute method and I don't have to worry about returning generated keys because queries automatically return a result set.
      ResultSet rs = statement.executeQuery();

      if (rs.next()) {
        int account_id = (int) rs.getLong(1);
        Account loggedInAcc = new Account(account_id, account.getUsername(), account.getPassword());
        return Optional.of(loggedInAcc);
      }

    } catch (SQLException e) {

      return Optional.empty();

    }

    return Optional.empty();

  }
}