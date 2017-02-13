package cis350.upenn.edu.remindmelater;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by AJNandi on 2/8/17.
 */

public class User {

    public String username;
    public String email;




    public User() {

    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }



}
