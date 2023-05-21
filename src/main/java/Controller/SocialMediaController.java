// controller receives input from user, sends it to service layer

package Controller;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {

        // Instantiate Javalin object
        Javalin app = Javalin.create();

        // ~~ ENDPOINTS ~~
        app.post("/register", this::registrationHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::newMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesFromAccountHandler);

        // Return Javalin app
        return app;
    }


    // ~~ HANDLERS ~~

    // 1. Registration
    // The Javalin Context object manages information about both the HTTP request and response.
    private void registrationHandler(Context context) throws JsonProcessingException {

        // The Jackson ObjectMapper class translates between JSON and Java.
        ObjectMapper mapper = new ObjectMapper();
        // Here, we're using it to create an Account object from a JSON String.
        Account account = mapper.readValue(context.body(), Account.class);

        // I'm using the object created from ObjectMapper as a parameter in the AccountService.addAccount method, which will ultimately result in an attempt to persist the new account to the database assuming some validation checks are passed. If it works, a new Account object will be returned, but because I don't know whether or not it will work, I'm using the Optional class, which allows for the possiblility that nothing will be returned.
        Optional<Account> newAccount = AccountService.addAccount(account);

        if (newAccount.isEmpty()) {  // If the Optional<Account> object is empty, 
                                     // it means that some test was failed in the Service or DAO layer and the new Account was never created. Return status code 400 (client error).
            context.status(400);
        } else {  // On the other hand, if an Optional<Account> object was returned, 
                  // return status code 200 OK (default) and the new account as a JSON String.
            context.json(mapper.writeValueAsString(newAccount.get()));
        }
    }

    // 2. Login
    private void loginHandler(Context context) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        Optional<Account> loggedInAcc = AccountService.login(account);

        if (loggedInAcc.isEmpty()) { // Unsuccessful logins return status code 401 (Unauthorized).
            context.status(401);

        } else {
            context.json(mapper.writeValueAsString(loggedInAcc.get()));
        }

    }

    // 3. New message
    private void newMessageHandler(Context context) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);

        Optional<Message> newMessage = MessageService.newMessage(message);

        if (newMessage.isEmpty()) {
            context.status(400);

        } else {
            context.json(mapper.writeValueAsString(newMessage.get()));
        }
    }

    // 4. Return all messages
    private void getAllMessagesHandler(Context context) throws JsonProcessingException {

        List<Message> messages = MessageService.getAllMessages();

        ObjectMapper mapper = new ObjectMapper();

        context.json(mapper.writeValueAsString(messages));
        
    }
    
    // 5. Get message by ID
    private void getMessageByIdHandler(Context context) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        String[] urlSplit = context.url().split("/");
        int messageIndex = Integer.parseInt(urlSplit[urlSplit.length - 1]);

        Optional<Message> message = MessageService.getMessageById(messageIndex);

        if (message.isPresent()) {
            context.json(mapper.writeValueAsString(message.get()));
        }
    }

    // 6. Delete message by ID
    private void deleteMessageByIdHandler(Context context) throws JsonProcessingException{

        // Instantiate Mapper
        ObjectMapper mapper = new ObjectMapper();

        // Split URL on slashes
        String[] urlSplit = context.url().split("/");
        // Find index
        int messageToDeleteIndex = Integer.parseInt(urlSplit[urlSplit.length - 1]);

        // Pass to Service layer
        Optional<Message> deletedMessage = MessageService.deleteMessage(messageToDeleteIndex);


        if (deletedMessage.isPresent()) {
            context.json(mapper.writeValueAsString(deletedMessage.get()));
        }

    }

    // 7. Update message by ID
    private void updateMessageByIdHandler(Context context) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        String[] urlSplit = context.url().split("/");
        int messageIdToUpdate = Integer.parseInt(urlSplit[urlSplit.length - 1]);

        // New Message object fron JSON. 
        // Note: This worked even though I'm only passing in an ID and message_text, and there is no such constructor in the Message object. Jackson uses the default constructor and fills in default values for missing parameters.
        Message newMessage = mapper.readValue(context.body(), Message.class);

        Optional<Message> updatedMessage = MessageService.updateMessage(messageIdToUpdate, newMessage.getMessage_text());

        if (updatedMessage.isPresent()) {
            context.json(mapper.writeValueAsString(updatedMessage.get()));
        } else {
            context.status(400);
        }

    }
    
    // 8. Get user messages
    private void getMessagesFromAccountHandler(Context context) throws JsonProcessingException {

        String[] urlSplit = context.url().split("/");
        int userIndex = Integer.parseInt(urlSplit[urlSplit.length - 2]); // Because the endpoint is ..../{account_id}/messages

        List<Message> messages = MessageService.getMessagesByUserId(userIndex);

        ObjectMapper mapper = new ObjectMapper();
        context.json(mapper.writeValueAsString(messages));
    }

}