package cis350.upenn.edu.remindmelater;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by AJNandi on 2/8/17.
 */

public class User {

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private List<Reminder> reminders;
    static DatabaseReference mDatabase;



    public User() {

    }

    public User(String firstname, String lastname, String username, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.reminders = new LinkedList<>();
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public String getUsername() {
        return username;
    }
    /**
     * Static method to sign up a user from other activities and add them to the database
     * @param activity the current activity trying to sign in, pass in using "this"
     * @param mAuth the authentication from firebase
     */
    public static void createNewUser(final Activity activity, FirebaseAuth mAuth, final String firstname, final String lastname, final String username, final String email, String password) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e){
                                Toast.makeText(activity.getApplicationContext(), "That email is already being used! Return to the" +
                                        "login screen or use a different email.", Toast.LENGTH_SHORT).show();
                                // catch (FirebaseAuthWeakPasswordException e)
                                // catch (FirebaseAuthInvalidCredentialsException e)
                            } catch (Exception e) {
                                Toast.makeText(activity.getApplicationContext(), R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            //Add user to database
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            User user = new User(firstname, lastname, username, email);
                            mDatabase.child("users").child(currentUser.getUid()).setValue(user);
                            Toast.makeText(activity.getApplicationContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();


                            //@Christina: uncomment and change to whatever class the home screen is called
                            //Intent i = new Intent(this, HomeActivity.class)
                            //startActivity(i)
                        }


                    }
                });
    }


    /**
     * Static method to sign in user from other activities
     * @param activity the current activity trying to sign in, pass in using "this"
     * @param mAuth the authentication from firebase
     * @param email user's email
     * @param password user's password
     */
    public static void signInUser(final Activity activity, FirebaseAuth mAuth, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            System.out.println("signInWithEmail:failed" + task.getException());
                            Toast.makeText(activity.getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
