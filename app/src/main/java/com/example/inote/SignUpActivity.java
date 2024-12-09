package com.example.inote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    // Request code for adding a note
    private static final int REQUEST_CODE_ADD_NOTE = 1;

    // SharedPreferences file name & Key for notes list in SharedPreferences
    private static final String NOTES_PREFS = "notes_prefs", NOTES_KEY = "notes_list";
    TextInputLayout fullname, email, password, confirmpassword;
    TextInputEditText editeTextFullname, editetextEmail, editeTextPassword, edittextConfirmPassword;
    String myfullname, myemail, mypassword, myconfirmpassword, notesJson;
    FirebaseAuth auth;
    FirebaseFirestore db;
    private Button buttonSignup;
    private TextView SignUp, TextLogin;
    private LinearLayout linearLayout;
    private static final String KEY = "1Hbfh667adfDEJ78";
    private static final String KEYP = "5Fqvc445qfcQSD76";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //call views by ids
        SignUp = findViewById(R.id.signup);

        linearLayout = findViewById(R.id.linearlayout);
        buttonSignup = findViewById(R.id.button_signup);
        TextLogin = findViewById(R.id.textview_login);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);
        editeTextFullname = findViewById(R.id.editeTextFullname);
        editetextEmail = findViewById(R.id.editetextEmail);
        editeTextPassword = findViewById(R.id.editeTextPassword);
        edittextConfirmPassword = findViewById(R.id.edittextConfirmPassword);

        //call firebase auth
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            notesJson = getIntent().getStringExtra("notesJson");
        }

        // Load animations
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        SignUp.setAnimation(animation);
        SignUp.setVisibility(View.VISIBLE);
        linearLayout.setAnimation(animation);
        linearLayout.setVisibility(View.VISIBLE);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSignup.setClickable(false);

                myfullname = fullname.getEditText().getText().toString().trim();
                myemail = email.getEditText().getText().toString().trim();
                mypassword = password.getEditText().getText().toString().trim();
                myconfirmpassword = confirmpassword.getEditText().getText().toString().trim();

                if (!CheckConnection.checkConnection(SignUpActivity.this)) {
                    Toast.makeText(SignUpActivity.this, "Verify your internet connection", Toast.LENGTH_SHORT).show();
                    buttonSignup.setClickable(true);
                } else {
                    if (!valideInput(myfullname, myemail, mypassword, myconfirmpassword)) {
                        auth.createUserWithEmailAndPassword(myemail, mypassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            String uid = task.getResult().getUser().getUid();
                                            String email = task.getResult().getUser().getEmail();

                                            try { // Create a User object
                                            User user = new User
                                                        (
                                                                myfullname,
                                                               myemail,
                                                               mypassword,
                                                                uid,
                                                                "",
                                                                GenerateTimeStamp.generateTimestamp()
                                                        );


                                            // Store user data in Firestore
                                            db.collection("Users").add(user)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                // Get the document reference and ID
                                                                DocumentReference documentReference = task.getResult();
                                                                String id = documentReference.getId();

                                                                // Update the document with the ID
                                                                documentReference.update("id", id)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    if (notesJson != null) {

                                                                                        Gson gson = new Gson();
                                                                                        Type type = new TypeToken<List<Note>>() {
                                                                                        }.getType();
                                                                                        List<Note> noteList = gson.fromJson(notesJson, type);
                                                                                        if (noteList != null && !noteList.isEmpty()) {
                                                                                            // Save each note to Firestore
                                                                                            for (Note note : noteList) {
                                                                                                db.collection("Users").document(id).collection("Notes").add(note)
                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    Toast.makeText(SignUpActivity.this, "Note added successfully!", Toast.LENGTH_SHORT).show();
                                                                                                                    // Get the generated document ID
                                                                                                                    DocumentReference documentReference = task.getResult();
                                                                                                                    String noteId = documentReference.getId();


                                                                                                                    // Now update the 'id' field in the newly created document
                                                                                                                    documentReference.update(
                                                                                                                            "id", noteId,
                                                                                                                            "userEmail",email
                                                                                                                            )

                                                                                                                            .addOnSuccessListener(aVoid -> {
                                                                                                                                // Successfully updated the 'id' field
                                                                                                                                // In another activity where you want to clear the shared preferences
                                                                                                                                SharedPreferences sharedPreferences = getSharedPreferences(NOTES_PREFS, MODE_PRIVATE);
                                                                                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                                                                                editor.remove(NOTES_KEY);
                                                                                                                                editor.apply();

                                                                                                                                Toast.makeText(SignUpActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                                                                                                                            })
                                                                                                                            .addOnFailureListener(e -> {
                                                                                                                                // Handle failure in updating the ID
                                                                                                                                Toast.makeText(SignUpActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                                            });


                                                                                                                } else {
                                                                                                                    Toast.makeText(SignUpActivity.this, "Error adding note: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    Toast.makeText(SignUpActivity.this, "Sign up Successful", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                                                                                    intent.putExtra("id", id);
                                                                                    intent.putExtra("fullname", myfullname);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                } else {
                                                                                    Toast.makeText(SignUpActivity.this, "Error updating document ID: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                    buttonSignup.setClickable(true);
                                                                                }
                                                                            }
                                                                        });
                                                            } else {
                                                                Toast.makeText(SignUpActivity.this, "Error adding user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                buttonSignup.setClickable(true);
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            buttonSignup.setClickable(true);
                                                        }
                                                    });

                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }

                                        } else {
                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            buttonSignup.setClickable(true);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        buttonSignup.setClickable(true);
                                    }
                                });
                    }
                }
            }
        });


        TextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }


    private boolean valideInput(String myfullname, String myemail, String mypassword, String myconfirmpassword) {
        myfullname = fullname.getEditText().getText().toString().trim();
        myemail = email.getEditText().getText().toString().trim();
        mypassword = password.getEditText().getText().toString().trim();
        myconfirmpassword = confirmpassword.getEditText().getText().toString().trim();

        ///verify if full name is empty or not
        if (myfullname.isEmpty()) {
            fullname.setError("Please enter your full name");
            fullname.requestFocus();
            buttonSignup.setClickable(true);
            return true;
        } else {
            fullname.setError(null);
            fullname.setErrorEnabled(false);
            buttonSignup.setClickable(false);
        }

        ///verify if email is empty or not
        if (myemail.isEmpty()) {
            email.setError("Please enter your email");
            email.requestFocus();
            buttonSignup.setClickable(true);
            return true;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            buttonSignup.setClickable(false);
        }

        //verify if email correct
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(myemail).matches()) {
            email.setError("Enter a valid email");
            email.requestFocus();
            buttonSignup.setClickable(true);
            return true;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            buttonSignup.setClickable(false);
        }

        ///verify if password is empty or not
        if (mypassword.isEmpty()) {
            password.setError("Please enter your password");
            password.requestFocus();
            buttonSignup.setClickable(true);
            return true;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            buttonSignup.setClickable(false);
        }

        ///verify if password is strong
        if (!StronPassword.isStrongPassword(mypassword)) {
            password.setError("Password should contain numbers, uppercase and lowercase letters \\n(e.g., \"AAaa1234\")");
            password.requestFocus();
            buttonSignup.setClickable(true);
            return true;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            buttonSignup.setClickable(false);

        }

        if (mypassword.length() != 12) {
            password.setError("Enter a valid password");
            password.requestFocus();
            buttonSignup.setClickable(true);
            return true;

        }

        ///verify if password is empty or not
        if (myconfirmpassword.isEmpty()) {
            confirmpassword.setError("Please confirm your password");
            confirmpassword.requestFocus();
            buttonSignup.setClickable(true);
            return true;
        } else {
            confirmpassword.setError(null);
            confirmpassword.setErrorEnabled(false);
            buttonSignup.setClickable(false);
        }

        //if confirm password = password
        if (!myconfirmpassword.equals(mypassword)) {
            confirmpassword.setError("Wrong password");
            buttonSignup.setClickable(true);
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2 = new Intent(this, MainActivity.class);
        startActivity(intent2);
        finish();

    }

}