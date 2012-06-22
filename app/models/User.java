package models;

import javax.validation.*;

import play.data.validation.Constraints.*;

public class User {
    
    @Required
    @MinLength(4)
    public String username;
    
    @Required
    @Email
    public String email;
    
    @Required
    @MinLength(6)
    public String password;

    @Valid
    public Profile profile;
    
    public User() {}
    
    public User(String username, String email, String password, Profile profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }
    
    public static class Profile {
        
        public String country;
        
        public String lastName;

        public String firstName;
        
        public String firm;
        
        public Profile() {}
        
        public Profile(String country, String lastName, String firstName,  String firm) {
            this.country = country;
            this.lastName = lastName;
            this.firstName = firstName;
            this.firm = firm;
        }
        
    }
    
}
