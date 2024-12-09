package com.example.inote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class WriteNoteForUsersActivity extends AppCompatActivity {

    FirebaseFirestore db ;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    boolean isUnderline, isItalic, isBold,isClickedColor;
    TextInputLayout noteContent, noteTitle;
    TextInputEditText contentEditText, titleEditText;
    String currentBackgroundColor="#fbfcfc" ,kind, notetitle, notecontent,ProfileId, noteId;
    TextView textKind, charCounterTop;
    private static final String KEY = "1Hbfh667adfDEJ78";
    private static final String KEYP = "5Fqvc445qfcQSD76";

    boolean dialogueShown = false, isPressed = false, editIsPressed;
    private ImageView saveButton, heartButton, backgroundButton;
    String  editNoteTitle, editNoteContent;
    int size = 16;
    int currentTextColor = Color.parseColor("#2E3A59");



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_write_note_for_users);



        //call views by Id
        noteTitle = findViewById(R.id.noteTitle);
        noteContent = findViewById(R.id.noteContent);
        saveButton = findViewById(R.id.saveButton);
        heartButton = findViewById(R.id.heartButton);
        backgroundButton = findViewById(R.id.backgroundButton);
        contentEditText = findViewById(R.id.contentEditText);
        titleEditText = findViewById(R.id.titleEditText);
        charCounterTop = findViewById(R.id.charCounterTop);
       textKind = findViewById(R.id.textKind);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        Intent intent = getIntent();

        if (intent != null) {
            kind = intent.getStringExtra("noteKind");
            ProfileId = intent.getStringExtra("ProfileId");
            noteId = intent.getStringExtra("noteId");
            editIsPressed = intent.getBooleanExtra("isPressed", false);// Default to false if not found

            // when edit, if the note was favorite, the heart pressed show, else not show
            if (editIsPressed) {
                heartButton.setImageResource(R.drawable.heart_pressed);
                isPressed = true;
            } else {
                heartButton.setImageResource(R.drawable.heart);
                isPressed = false;
            }


            editNoteTitle = intent.getStringExtra("noteTitle");
            editNoteContent = intent.getStringExtra("noteContent");
            if (kind.equals("Personal")) {
                textKind.setTextColor(Color.parseColor("#A569BD"));
            } else if (kind.equals("Important")) {
                textKind.setTextColor(Color.parseColor("#58D68D"));
            } else if (kind.equals("Work")) {
                textKind.setTextColor(Color.parseColor("#F39C12"));
            } else if (kind.equals("Education")) {
                textKind.setTextColor(Color.parseColor("#8a6f8f"));
            }



            if (intent.getStringExtra("backgroundColorString")!=null){
                currentBackgroundColor = intent.getStringExtra("backgroundColorString");


            if (currentBackgroundColor.equals("#fbfcfc")) {
                backgroundButton.setImageResource(R.drawable.note_background); // Update background button image
                //When edit the note, get the background saved and set it to the contentEditText
                int color = Color.parseColor(currentBackgroundColor);
                noteContent.setBoxBackgroundColor(color);
            }

            else  if (currentBackgroundColor.equals("#ffd0c5")) {
                backgroundButton.setImageResource(R.drawable.pink_note_background);
                //When edit the note, get the background saved and set it to the contentEditText
                int color = Color.parseColor(currentBackgroundColor);
                noteContent.setBoxBackgroundColor(color);
// Update background button image
            }
            else  if (currentBackgroundColor.equals("#ffcf48")) {
                backgroundButton.setImageResource(R.drawable.yellow_note_background);
                //When edit the note, get the background saved and set it to the contentEditText
                int color = Color.parseColor(currentBackgroundColor);
                noteContent.setBoxBackgroundColor(color);
// Update background button image
            }

            else  if (currentBackgroundColor.equals("#5eb1bf")) {
                backgroundButton.setImageResource(R.drawable.cyan_note_background);
                //When edit the note, get the background saved and set it to the contentEditText
                int color = Color.parseColor(currentBackgroundColor);
                noteContent.setBoxBackgroundColor(color);
// Update background button image
            }
            else  if (currentBackgroundColor.equals("#ef7b45")) {
                backgroundButton.setImageResource(R.drawable.orange_note_background);
                //When edit the note, get the background saved and set it to the contentEditText
                int color = Color.parseColor(currentBackgroundColor);
                noteContent.setBoxBackgroundColor(color);
// Update background button image
            }
            else  if (currentBackgroundColor.equals("#0aff99")) {
                backgroundButton.setImageResource(R.drawable.green_note_background);
                //When edit the note, get the background saved and set it to the contentEditText
                int color = Color.parseColor(currentBackgroundColor);
                noteContent.setBoxBackgroundColor(color);
// Update background button image
            }
            }

            //When edit the note, get the title saved and set it to the titleEditText
            if (intent.getStringExtra("noteTitle") != null) {
                titleEditText.setText(editNoteTitle);
            }
            //When edit the note, get the content saved and set it to the contentEditText
            if (intent.getStringExtra("noteContent") != null) {
                Spannable spannableContent = (Spannable) Html.fromHtml(editNoteContent, Html.FROM_HTML_MODE_LEGACY);
                contentEditText.setText(spannableContent);
            }

        }


        textKind.setText("#"+kind);
        // set the text color depend the kind

        if (kind.equals("Personal")) {
            textKind.setTextColor(Color.parseColor("#A569BD"));
        } else if (kind.equals("Important")) {
            textKind.setTextColor(Color.parseColor("#58D68D"));
        } else if (kind.equals("Work")) {
            textKind.setTextColor(Color.parseColor("#F39C12"));
        } else if (kind.equals("Education")) {
            textKind.setTextColor(Color.parseColor("#8a6f8f"));
        }

        // change the background of the noteContent
        backgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Inflate the dialog layout
                LayoutInflater inflater = LayoutInflater.from(WriteNoteForUsersActivity.this);
                //relate the dialog with the layout dialog_color
                View dialogView = inflater.inflate(R.layout.dialog_color, null);


                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(WriteNoteForUsersActivity.this);
                // set the title
                builder.setTitle("Choose note background color")
                        //set the view
                        .setView(dialogView);
                // create the dialog
                AlertDialog dialog = builder.create();

                //call each relative with id
                RelativeLayout defaultColor = dialogView.findViewById(R.id.defaultColorLayout);
                RelativeLayout lightPink = dialogView.findViewById(R.id.lightPinkLayout);
                RelativeLayout brightYellow = dialogView.findViewById(R.id.brightYellowLayout);
                RelativeLayout softCyan = dialogView.findViewById(R.id.softCyanLayout);
                RelativeLayout vibrantOrange = dialogView.findViewById(R.id.vibrantOrangeLayout);
                RelativeLayout springGreen = dialogView.findViewById(R.id.springGreenLayout);


                //when click on RelativeLayout defaultColor
                defaultColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBackgroundColor = "#fbfcfc"; // Set the current background color
                        int color = Color.parseColor(currentBackgroundColor);
                        noteContent.setBoxBackgroundColor(color);
                        backgroundButton.setImageResource(R.drawable.note_background); // Update background button image
                        dialog.dismiss();  // Dismiss the dialog
                    }
                });

                lightPink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBackgroundColor = "#ffd0c5";
                        int color = Color.parseColor(currentBackgroundColor);
                        noteContent.setBoxBackgroundColor(color);
                        backgroundButton.setImageResource(R.drawable.pink_note_background);
                        dialog.dismiss();
                    }
                });

                brightYellow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBackgroundColor = "#ffcf48";
                        int color = Color.parseColor(currentBackgroundColor);
                        noteContent.setBoxBackgroundColor(color);
                        backgroundButton.setImageResource(R.drawable.yellow_note_background);
                        dialog.dismiss();
                    }
                });

                softCyan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBackgroundColor = "#5eb1bf";
                        int color = Color.parseColor(currentBackgroundColor);
                        noteContent.setBoxBackgroundColor(color);
                        backgroundButton.setImageResource(R.drawable.cyan_note_background);
                        dialog.dismiss();
                    }
                });

                vibrantOrange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBackgroundColor = "#ef7b45";
                        int color = Color.parseColor(currentBackgroundColor);
                        noteContent.setBoxBackgroundColor(color);
                        backgroundButton.setImageResource(R.drawable.orange_note_background);
                        dialog.dismiss();
                    }
                });

                springGreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBackgroundColor = "#0aff99";
                        int color = Color.parseColor(currentBackgroundColor);
                        noteContent.setBoxBackgroundColor(color);
                        backgroundButton.setImageResource(R.drawable.green_note_background);
                        dialog.dismiss();
                    }
                });

                // Show the dialog

                dialog.show();


            }
        });
// Set hint programmatically
        contentEditText.setHint("Content");
        titleEditText.setHint("Title");

// Remove hint when user starts typing
        contentEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!contentEditText.getText().toString().isEmpty()) {
                    contentEditText.setHint(null); // Remove hint

                } else {
                    contentEditText.setHint("Content"); // Restore hint if text is empty
                }
                return false;
            }
        });

        titleEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (!titleEditText.getText().toString().isEmpty()) {
                    titleEditText.setHint(null); // Remove hint

                } else {
                    titleEditText.setHint("Title"); // Restore hint if text is empty
                }
                return false;
            }
        });

        // when write, the counter ++
        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCounterTop.setText(length + "/5000");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // click on favorite button
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPressed = !isPressed;
                // if it is click, dis-click it, or not
                heartButton.setImageResource(isPressed ? R.drawable.heart_pressed : R.drawable.heart);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.saveButton).setClickable(false);
                String notetitle = noteTitle.getEditText().getText().toString();
                String notecontent = noteContent.getEditText().getText().toString();
                String noteId = getIntent().getStringExtra("noteId"); // Get the note ID from the intent

                if (!validaInput(notetitle, notecontent)) {
                    findViewById(R.id.saveButton).setClickable(false);
                    // Get the Spannable text from the EditText
                    Editable content = contentEditText.getText();

                    // Convert the Spannable text to an HTML string
                    String htmlContent = Html.toHtml(content, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);

                    Note note = null;
                    try {
                        note = new Note(
                                notetitle,
                                htmlContent,
                                GenerateTimeStamp.generateTimestamp(),
                                kind,
                                "",
                                Encrypt.encrypt(currentUser.getEmail(), GenerateKey.generateKey(KEY)),
                                currentBackgroundColor,
                                isPressed
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    if (noteId != null && !noteId.isEmpty()) {
                        findViewById(R.id.saveButton).setClickable(false);
                        // Update existing note
                        db.collection("Users").document(ProfileId)
                                .collection("Notes").document(noteId)
                                .set(note) // Use .set() to update the existing note
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(WriteNoteForUsersActivity.this, "Note updated", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(WriteNoteForUsersActivity.this, ProfileActivity.class);
                                            intent.putExtra("ProfileId", ProfileId);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(WriteNoteForUsersActivity.this, "Error updating note: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WriteNoteForUsersActivity.this, "Error updating note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        findViewById(R.id.saveButton).setClickable(false);
                        // Add new note
                        db.collection("Users").document(ProfileId)
                                .collection("Notes")
                                .add(note)  // Using .add() to auto-generate a unique note ID
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {

                                            DocumentReference documentReference = task.getResult();
                                            String newNoteId = documentReference.getId();

                                            // Now update the 'id' field in the newly created document
                                            documentReference.update("id", newNoteId)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(WriteNoteForUsersActivity.this, "Note added", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(WriteNoteForUsersActivity.this, ProfileActivity.class);
                                                        intent.putExtra("ProfileId", ProfileId);
                                                        startActivity(intent);
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(WriteNoteForUsersActivity.this, "Error updating note ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(WriteNoteForUsersActivity.this, "Error adding note: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WriteNoteForUsersActivity.this, "Error saving note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });




        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                charCounterTop.setText(length + "/5000");


            }

            @Override
            public void afterTextChanged(Editable s) {
                // Determine where new text is added
                // Update formatting after text change

            }
        });



    }



    private boolean validaInput(String title, String content) {

        // store edit text on string
        title = noteTitle.getEditText().getText().toString();
        content = noteContent.getEditText().getText().toString();

        // verify if title empty or no
        if (title.isEmpty()) {
            noteTitle.setError("Please write the title");
            noteTitle.requestFocus();
            findViewById(R.id.saveButton).setClickable(true);
            return true;

        } else {
            noteTitle.setError(null);
            noteTitle.setErrorEnabled(false);
            findViewById(R.id.saveButton).setClickable(true);
        }
        if (title.length()>100) {
            noteTitle.setError("you have reached the limit");
            noteTitle.requestFocus();
            findViewById(R.id.saveButton).setClickable(true);
            return true;

        } else {
            noteTitle.setError(null);
            noteTitle.setErrorEnabled(false);
            findViewById(R.id.saveButton).setClickable(true);
        }

        // Check if content length exceeds 5000 characters
        if (content.length() > 5000) {
            // Show a dialog to inform the user
            CharacterLimitDialog.showCharacterLimitDialog(this);
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2 = new Intent(this, ProfileActivity.class);
        startActivity(intent2);
        finish();

    }
}