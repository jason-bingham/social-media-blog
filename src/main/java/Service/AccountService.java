// Service layer receives data from the controller, performs business logic and passes it on to DAO

package Service;

import java.util.Optional;

import DAO.AccountDAO;
import Model.Account;

// Called from SocialMediaController
public class AccountService {
  
  public static Optional<Account> addAccount(Account account) {

    if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
      // return empty Optional if username empty
      return Optional.empty();

    } else if (account.getPassword().length() < 4) {
      // return empty Optional if password less than 4 characters long
      return Optional.empty();

    } else {
      return AccountDAO.addAccount(account);
    }
  }

  public static Optional<Account> login(Account account) {

    // I'm passing the argument directly to the DAO this time, without performing any business logic, because the DAO is the easiest place to check if the usernmae and password match an existing account.
    return AccountDAO.login(account);
  }
}
