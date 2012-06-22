package models;

import javax.validation.*;

import play.data.validation.Constraints.*;

public class Client {
    
    @Required
    @MinLength(4)
    public String login;
    
    @Required
    @MinLength(6)
    public String password;

    public Client() {}
    
    public Client(String login, String password) {
        this.login = login;
        this.password = password;
    }
    
}
