package com.example.inote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class WriteNoteActivity extends AppCompatActivity {

    // Array of color options for note background
    private final int[] colorOptions = {
            Color.parseColor("#fbfcfc"), // Default
            Color.parseColor("#ffd0c5"), // Light Pink
            Color.parseColor("#ffcf48"), // Bright Yellow
            Color.parseColor("#5eb1bf"), // Soft Cyan
            Color.parseColor("#ef7b45"), // Vibrant Orange
            Color.parseColor("#0aff99")  // Spring Green
    };


    TextInputLayout noteContent, noteTitle;
    TextInputEditText contentEditText, titleEditText;
    String currentBackgroundColor="#fbfcfc", kind, editNoteTitle, editNoteContent;
    TextView textKind, charCounterTop;
    boolean dialogueShown = false, isPressed = false, editIsPressed;
    private ImageView saveButton, heartButton, backgroundButton;
    private int  index;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_note);

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

        //receiving data sent from GuestActivity using  getIntent() (Edit)
        Intent intent = getIntent();

        if (intent != null) {

            index = intent.getIntExtra("index", -1); // Default to -1 if not found
            editIsPressed = intent.getBooleanExtra("isPressed", false);// Default to false if not found
            kind = intent.getStringExtra("noteKind");

            editNoteTitle = intent.getStringExtra("noteTitle");
            editNoteContent = intent.getStringExtra("noteContent");
            // set the text to kind
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
            //When edit the note, get the background saved and set it to the contentEditText

            int color = Color.parseColor(currentBackgroundColor);
            contentEditText.setBackgroundColor(color);


            //When edit the note, get the title saved and set it to the titleEditText
            if (intent.getStringExtra("noteTitle") != null) {
                titleEditText.setText(editNoteTitle);
            }
            //When edit the note, get the content saved and set it to the contentEditText
            if (intent.getStringExtra("noteContent") != null) {
                contentEditText.setText(editNoteContent);
            }
            //When edit the note, get the number of the list saved
            if (intent.getStringExtra("index") != null) {
                index = intent.getIntExtra("index", -1);
            }


        }
        // change the background of the noteContent
        backgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Inflate the dialog layout
                LayoutInflater inflater = LayoutInflater.from(WriteNoteActivity.this);
                //relate the dialog with the layout dialog_color
                View dialogView = inflater.inflate(R.layout.dialog_color, null);


                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(WriteNoteActivity.this);
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

        //if no background select, set the default
        int color = Color.parseColor(currentBackgroundColor);
        noteContent.setBoxBackgroundColor(color);


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


// When click on seek bar show dialog



        // when click on save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        // when edit, if the note was favorite, the heart pressed show, else not show
        if (editIsPressed) {
            heartButton.setImageResource(R.drawable.heart_pressed);
            isPressed = true;
        } else {
            heartButton.setImageResource(R.drawable.heart);
            isPressed = false;
        }

        // click on favorite button
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPressed = !isPressed;
                // if it is click, dis-click it, or not
                heartButton.setImageResource(isPressed ? R.drawable.heart_pressed : R.drawable.heart);
            }
        });


    }

    private void saveNote() {
        findViewById(R.id.saveButton).setClickable(false);
        // store edit text on string
        String title = noteTitle.getEditText().getText().toString();
        String content = noteContent.getEditText().getText().toString().trim();

        // verify if title empty or no
        if (title.isEmpty()) {
            noteTitle.setError("Please write the title");
            noteTitle.requestFocus();
            findViewById(R.id.saveButton).setClickable(true);
            return;

        } else {
            noteTitle.setError(null);
            noteTitle.setErrorEnabled(false);
            findViewById(R.id.saveButton).setClickable(true);
        }

        if (title.length()>100) {
            noteTitle.setError("you have reached the limit");
            noteTitle.requestFocus();
            findViewById(R.id.saveButton).setClickable(true);
            return ;

        } else {
            noteTitle.setError(null);
            noteTitle.setErrorEnabled(false);
            findViewById(R.id.saveButton).setClickable(true);
        }
        // Check if content length exceeds 5000 characters
        if (content.length() > 5000) {
            // Show a dialog to inform the user
            CharacterLimitDialog.showCharacterLimitDialog(this);
            findViewById(R.id.saveButton).setClickable(true);
            return; // Exit the method without saving
        }

        // Create an Intent to pass the data back to GuessActivity
        // if -1 so it is new note
        if (index == -1) {

            Note newNote = new Note(
                    title,
                    content,
                    GenerateTimeStamp.generateTimestamp(),
                    kind,
                   "",
                    "",
                    currentBackgroundColor,
                    isPressed);
            Intent resultIntent = new Intent();
            //send data to GuestActivity
            resultIntent.putExtra("noteTitle", newNote.getTitle());
            resultIntent.putExtra("noteContent", newNote.getContent());
            resultIntent.putExtra("noteKind", newNote.getKind());
            resultIntent.putExtra("noteDate", newNote.getDate());
            resultIntent.putExtra("index", -1);
            resultIntent.putExtra("isPressed", isPressed);
            resultIntent.putExtra("backgroundColor", currentBackgroundColor);
            setResult(RESULT_OK, resultIntent);
        }

        // not new note
        // Update existing note
        else {

            // if content is empty set ""
            if (content.isEmpty()) {
                content = "";
            }
            Intent resultIntent = new Intent();
            //send data to GuestActivity
            resultIntent.putExtra("noteTitle", title);
            resultIntent.putExtra("noteContent", content);
            resultIntent.putExtra("noteKind", kind);
            resultIntent.putExtra("noteDate", GenerateTimeStamp.generateTimestamp());
            resultIntent.putExtra("index", index);
            resultIntent.putExtra("isPressed", isPressed);
            resultIntent.putExtra("backgroundColor", currentBackgroundColor);
            setResult(RESULT_OK, resultIntent);
        }


        finish();
    }


}