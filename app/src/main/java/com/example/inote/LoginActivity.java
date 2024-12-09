package com.example.inote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    // Request code for adding a note
    private static final int REQUEST_CODE_ADD_NOTE = 1;

    // SharedPreferences file name & Key for notes list in SharedPreferences
    private static final String NOTES_PREFS = "notes_prefs", NOTES_KEY = "notes_list";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_EMAIL = "saved_email";
    private static final String KEY_PASSWORD = "saved_password";
    TextInputLayout emailLayout, passwordlayout;
    TextInputEditText editetextEmail, editeTextPassword;
    String myemail, mypassword, notesJson;
    FirebaseAuth auth;
    FirebaseFirestore db;
    CheckBox checkBox;
    private Button buttonLogin;
    private TextView Login, TextSignUp;
    private LinearLayout linearLayout;
    // SharedPreferences to store user credentials
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonLogin = findViewById(R.id.button_login);
        TextSignUp = findViewById(R.id.signup);
        Login = findViewById(R.id.login);

        linearLayout = findViewById(R.id.linearlayout);
        emailLayout = findViewById(R.id.email);
        passwordlayout = findViewById(R.id.password);
        editetextEmail = findViewById(R.id.editetextEmail);
        editeTextPassword = findViewById(R.id.editeTextPassword);
        checkBox = findViewById(R.id.remember_me);


        Intent intent = getIntent();
        if (intent != null) {
            notesJson = getIntent().getStringExtra("notesJson");
        }

        // Load animations
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);


        Login.setAnimation(animation);
        Login.setVisibility(View.VISIBLE);
        linearLayout.setAnimation(animation);
        linearLayout.setVisibility(View.VISIBLE);
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load saved credentials if they exist
        loadSavedCredentials();


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonLogin.setClickable(false);
                myemail = emailLayout.getEditText().getText().toString().trim();
                mypassword = passwordlayout.getEditText().getText().toString().trim();

                if (!CheckConnection.checkConnection(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "Verify your internet connection", Toast.LENGTH_SHORT).show();
                    buttonLogin.setClickable(true);
                    return;
                }

                // Handle the remember me functionality
                boolean isRemembered = checkBox.isChecked();
                if (isRemembered) {
                    // Save user credentials securely using SharedPreferences
                    saveCredentials(myemail, mypassword);

                } else {
                    // Clear saved credentials if checkbox is unchecked
                    clearSavedCredentials();
                }

                if (valideInput(myemail, mypassword)) {
                    auth.signInWithEmailAndPassword(myemail, mypassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                if (user == null) {
                                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                    buttonLogin.setClickable(true);
                                    return;
                                }

                                // Fetch user ID from Firestore
                                db.collection("Users").whereEqualTo("email", user.getEmail()) // Ensure this field matches your Firestore field
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                                    String userId = document.getString("id");

                                                    if (notesJson != null) {
                                                        Gson gson = new Gson();
                                                        Type type = new TypeToken<List<Note>>() {
                                                        }.getType();
                                                        List<Note> noteList = gson.fromJson(notesJson, type);

                                                        if (noteList != null && !noteList.isEmpty()) {
                                                            // Save each note to Firestore
                                                            for (Note note : noteList) {
                                                                db.collection("Users").document(userId).collection("Notes").add(note).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                        if (task.isSuccessful()) {
                                                                            DocumentReference documentReference = task.getResult();
                                                                            String noteId = documentReference.getId();

                                                                            // Update the 'id' field in the newly created document
                                                                            documentReference.update("id", noteId, "userEmail", user.getEmail()).addOnSuccessListener(aVoid -> {
                                                                                Toast.makeText(LoginActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                                                                                // In another activity where you want to clear the shared preferences
                                                                                SharedPreferences sharedPreferences = getSharedPreferences(NOTES_PREFS, MODE_PRIVATE);
                                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                                editor.remove(NOTES_KEY);
                                                                                editor.apply();

                                                                            }).addOnFailureListener(e -> {
                                                                                Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            });
                                                                        } else {
                                                                            Toast.makeText(LoginActivity.this, "Error adding note: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }


                                                        }
                                                    }

                                                    // Navigate to ProfileActivity
                                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                                    intent.putExtra("userId", userId);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "User not found in Firestore", Toast.LENGTH_SHORT).show();
                                                    buttonLogin.setClickable(true);
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                buttonLogin.setClickable(true);
                            }
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        buttonLogin.setClickable(true);
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                    buttonLogin.setClickable(true);
                }
            }
        });


        findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the custom dialog view
                LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
                View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);

                // Find the EditText in the custom layout
                TextInputLayout emailInput = dialogView.findViewById(R.id.email);

                // Build the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);  // Custom style for dialog
                builder.setView(dialogView);

                builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailInput.getEditText().getText().toString().trim();

                        if (email.isEmpty()) {
                            emailInput.setError("Please enter your email");
                            emailInput.requestFocus();
                        } else {
                            emailInput.setError(null);
                            emailInput.setErrorEnabled(false);


                            // Check if email exists in Firestore
                            db.collection("Users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    // Email exists, show password reset dialog
                                    showPasswordResetDialog(email);
                                } else {
                                    // Email does not exist
                                    Toast.makeText(LoginActivity.this, "Email does not exist in the system.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
            }
        });
        TextSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }

    private boolean valideInput(String myemail, String mypassword) {
        // Verify if email is empty
        if (myemail.isEmpty()) {
            emailLayout.setError("Please enter your email");
            emailLayout.requestFocus();
            return false; // Invalid input
        } else {
            emailLayout.setError(null);
            emailLayout.setErrorEnabled(false);
        }

        // Verify if email is valid
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(myemail).matches()) {
            emailLayout.setError("Please enter a valid email");
            emailLayout.requestFocus();
            return false; // Invalid input
        } else {
            emailLayout.setError(null);
            emailLayout.setErrorEnabled(false);
        }

        // Verify if password is empty
        if (mypassword.isEmpty()) {
            passwordlayout.setError("Please enter your password");
            passwordlayout.requestFocus();
            return false; // Invalid input
        } else {
            passwordlayout.setError(null);
            passwordlayout.setErrorEnabled(false);
        }

        // Verify if password length is correct
        if (mypassword.length() != 12) { // Adjust password length requirement as needed
            passwordlayout.setError("Password must be at 12 characters");
            passwordlayout.requestFocus();
            return false; // Invalid input
        }

        return true; // Valid input
    }

    private void showPasswordResetDialog(String email) {
        // Inflate the password reset dialog view
        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View passwordDialogView = inflater.inflate(R.layout.dialog_reset_password, null);

        // Get references to the password input fields
        TextInputLayout newPasswordInput = passwordDialogView.findViewById(R.id.password);
        TextInputLayout confirmPasswordInput = passwordDialogView.findViewById(R.id.confirmpassword);

        // Create and set up the AlertDialog for password reset
        AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        passwordDialogBuilder.setView(passwordDialogView);

        // Set up the "Update Password" button
        passwordDialogBuilder.setPositiveButton("Update Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the inputted new password and confirmation password
                String newPassword = newPasswordInput.getEditText().getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getEditText().getText().toString().trim();

                boolean isValid = true; // Flag to track if all validations pass

                // Password validation
                if (newPassword.isEmpty()) {
                    newPasswordInput.setError("Please enter your password");
                    newPasswordInput.requestFocus();
                    isValid = false;
                } else if (!StronPassword.isStrongPassword(newPassword)) {
                    newPasswordInput.setError("Password should contain numbers, uppercase and lowercase letters (e.g., \"AAaa1234\")");
                    newPasswordInput.requestFocus();
                    isValid = false;
                } else if (newPassword.length() != 12) {
                    newPasswordInput.setError("Password must be exactly 12 characters long");
                    newPasswordInput.requestFocus();
                    isValid = false;
                } else {
                    newPasswordInput.setError(null);
                    newPasswordInput.setErrorEnabled(false);
                }

                if (confirmPassword.isEmpty()) {
                    confirmPasswordInput.setError("Please confirm your password");
                    confirmPasswordInput.requestFocus();
                    isValid = false;
                } else if (!confirmPassword.equals(newPassword)) {
                    confirmPasswordInput.setError("Passwords do not match");
                    confirmPasswordInput.requestFocus();
                    isValid = false;
                } else {
                    confirmPasswordInput.setError(null);
                    confirmPasswordInput.setErrorEnabled(false);
                }

                // If all validations pass, proceed with password update
                // If all validations pass, proceed with password update
                if (isValid) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    // Query Firestore to find the user by email
                    db.collection("Users")
                            .whereEqualTo("email", email) // Assuming "email" is the field storing user emails
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    // User found, get the user ID (document ID)
                                    DocumentSnapshot userDocument = task.getResult().getDocuments().get(0);
                                    String userId = userDocument.getId();
                                    String oldPassword = userDocument.getString("password"); // Retrieve old password

                                    // Sign in with the old password
                                    assert oldPassword != null;

                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, oldPassword)
                                            .addOnCompleteListener(signInTask -> {
                                                if (signInTask.isSuccessful()) {
                                                    FirebaseUser user = signInTask.getResult().getUser();
                                                    if (user != null) { // Check if the user is not null
                                                        // Update the password in Firebase Authentication
                                                        user.updatePassword(newPassword)
                                                                .addOnCompleteListener(updateTask -> {
                                                                    if (updateTask.isSuccessful()) {
                                                                        Toast.makeText(LoginActivity.this, "Password updated  successfully!", Toast.LENGTH_SHORT).show();

                                                                        // Update the password in Firestore
                                                                        db.collection("Users")
                                                                                .document(userId) // Use the unique user ID to find the correct document
                                                                                .update("password", newPassword) // Update Firestore password field
                                                                                .addOnSuccessListener(aVoid -> {
                                                                                    checkBox.setChecked(false); // Uncheck the "Remember Me" checkbox
                                                                                    editetextEmail.setText(""); // Clear the email field
                                                                                    editeTextPassword.setText(""); // Clear the password field
                                                                                    clearSavedCredentials();
                                                                                    Toast.makeText(LoginActivity.this, "Password updated  successfully!", Toast.LENGTH_SHORT).show();
                                                                                })
                                                                                .addOnFailureListener(e -> {
                                                                                    Toast.makeText(LoginActivity.this, "Error updating password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                });
                                                                    } else {
                                                                        Toast.makeText(LoginActivity.this, "Error updating password : " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "Error: User not found during password update.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Error signing in for password update: " + signInTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(LoginActivity.this, "Error: Email does not exist in the system.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });

        // Set up the "Cancel" button
        passwordDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the password reset dialog
        passwordDialogBuilder.create().show();
    }


    // Method to load saved credentials from SharedPreferences
    private void loadSavedCredentials() {
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, null);
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, null);

        if (savedEmail != null) {
            editetextEmail.setText(savedEmail);
            checkBox.setChecked(true); // Check the box if email exists
        }

        if (savedPassword != null) {
            editeTextPassword.setText(savedPassword);
        }
    }

    // Method to save credentials in SharedPreferences
    private void saveCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply(); // Save changes
    }

    // Method to clear saved credentials from SharedPreferences
    private void clearSavedCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.apply(); // Save changes
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2 = new Intent(this, MainActivity.class);
        startActivity(intent2);
        finish();

    }
}