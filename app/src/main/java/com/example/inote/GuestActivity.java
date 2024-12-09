package com.example.inote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GuestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Request code for adding a note
    private static final int REQUEST_CODE_ADD_NOTE = 1;

    // SharedPreferences file name & Key for notes list in SharedPreferences
    private static final String NOTES_PREFS = "notes_prefs", NOTES_KEY = "notes_list";

    // List to store notes
    public List<Note> noteList = new ArrayList<>();

    // Index of the note being edited & // Number of favorite notes
    int index = -1, numberFav = 0;


    HorizontalScrollView horizontalScrollView;
    Button all, personal, important, work, education;
    ImageView addButton, menuButton, heartButton;
    private DrawerLayout drawerLayout;
    private TextView hi, iNote, emptyNote;
    private LinearLayout notesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        ////
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        MenuItem navItem = menu.findItem(R.id.nav_log_out);  // Replace with the ID of the item you want to hide
        navItem.setVisible(false);

        View headerView = navigationView.getHeaderView(0);  // 0 is the index if you only have one header
        TextView header_tv_login = headerView.findViewById(R.id.tv_login);
        TextView header_tv_sigup = headerView.findViewById(R.id.tv_signup);


        header_tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(NOTES_PREFS, Context.MODE_PRIVATE);
                String notesJson = sharedPreferences.getString(NOTES_KEY, null);

                Intent intentlogin = new Intent(GuestActivity.this, LoginActivity.class);
                intentlogin.putExtra("notesJson", notesJson);
                startActivity(intentlogin);
                finish();

            }
        });
        header_tv_sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(NOTES_PREFS, Context.MODE_PRIVATE);
                String notesJson = sharedPreferences.getString(NOTES_KEY, null);

                Intent intentsignup = new Intent(GuestActivity.this, SignUpActivity.class);
                intentsignup.putExtra("notesJson", notesJson);
                startActivity(intentsignup);
                finish();


            }
        });

        // find views by id
        notesContainer = findViewById(R.id.notesContainer);
        hi = findViewById(R.id.hi);
        iNote = findViewById(R.id.iNote);
        horizontalScrollView = findViewById(R.id.horizontalscroll);
        all = findViewById(R.id.buttonAll);
        personal = findViewById(R.id.buttonPersonal);
        important = findViewById(R.id.buttonImportant);
        work = findViewById(R.id.buttonWork);
        education = findViewById(R.id.buttonEducation);
        addButton = findViewById(R.id.addButton);
        menuButton = findViewById(R.id.menuButton);
        heartButton = findViewById(R.id.heartButton);
        emptyNote = findViewById(R.id.emptyNote);

        // Load animations
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);

        hi.setAnimation(animation);
        hi.setVisibility(View.VISIBLE);
        addButton.setAnimation(animation);
        addButton.setVisibility(View.VISIBLE);
        menuButton.setAnimation(animation);
        menuButton.setVisibility(View.VISIBLE);
        heartButton.setAnimation(animation);
        heartButton.setVisibility(View.VISIBLE);
        iNote.setAnimation(animation);
        iNote.setVisibility(View.VISIBLE);
        horizontalScrollView.setAnimation(animation);
        horizontalScrollView.setVisibility(View.VISIBLE);
        emptyNote.setAnimation(animation);
        emptyNote.setVisibility(View.VISIBLE);
        notesContainer.setAnimation(animation);
        notesContainer.setVisibility(View.VISIBLE);

        drawerLayout = findViewById(R.id.drawer_layout);


        ImageView menuButton = findViewById(R.id.menuButton);


        ///menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });


        // Set onClickListeners for filter buttons
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFilteredNotes("");
                iNote.setText("All Notes");
            }
        });

        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFilteredNotes("Personal");
                iNote.setText("Personal Notes");
            }
        });

        important.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFilteredNotes("Important");
                iNote.setText("Important Notes");
            }
        });

        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFilteredNotes("Work");
                iNote.setText("Wotrk Notes");
            }
        });

        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFilteredNotes("Education");
                iNote.setText("Education Notes");
            }
        });

        // load the saved notes
        loadNotes();

        // add new note
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if note are less than 10
                if (noteList.size() <= 9) {
                    showFilterDialog();
                } else {
                    DialogShowLoginSignUp.showLoginSignUpDialog(GuestActivity.this, "You have reached the limit", "Sign up or log in to continue");
                }

            }
        });

        // when click on heart button, show just the favorite notes
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesContainer.removeAllViews();
                // Check for empty notes and update emptyNote visibility and text
                numberFav = 0;
                for (Note note : noteList) {

                    if (note.isPressed()) {
                        addNoteCard(note);
                        numberFav = numberFav + 1;
                    }
                }
                iNote.setText("Favorite Notes (" + numberFav + ")");
                if (numberFav == 0) {
                    emptyNote.setText("No Favorite Notes");
                    emptyNote.setVisibility(View.VISIBLE);
                } else {
                    emptyNote.setVisibility(View.INVISIBLE);
                }
            }
        });


        displayFilteredNotes("");

    }

    private void showFilterDialog() {
        //  Create a LayoutInflater object from the current context (usually an Activity or Fragment).
        //  LayoutInflater is used to create (inflate) View objects from XML layout files.
        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate the XML layout file 'dialog_filter.xml' into a View object.
        // The 'null' parameter means that this View will not be attached to any parent ViewGroup immediately.
        View dialogView = inflater.inflate(R.layout.dialog_filter, null);

        // Create an AlertDialog.Builder object to start building the alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("This note is ...") // Set the title of the dialog.
                .setView(dialogView) // Set the custom View for the dialog (previously inflated layout).
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // This code is executed when the "Next" button is clicked.

                        // Find the RadioGroup within the dialogView by its ID.
                        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

                        // Get the ID of the selected RadioButton within the RadioGroup.
                        int selectedId = radioGroup.getCheckedRadioButtonId();

                        if (selectedId == -1) {
                            // If no RadioButton is selected, show a message to the user.
                            Toast.makeText(GuestActivity.this, "You have to choose your note kind", Toast.LENGTH_SHORT).show();
                        } else {

                            // If a RadioButton is selected, find the selected RadioButton.
                            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);

                            // Get the text of the selected RadioButton.
                            String selectedText = selectedRadioButton.getText().toString();

                            // Create an Intent to start the WriteNoteActivity.
                            Intent intent = new Intent(GuestActivity.this, WriteNoteActivity.class);

                            // Pass the selected note kind as an extra with the Intent.
                            intent.putExtra("noteKind", selectedText);

                            // Start the WriteNoteActivity and wait for a result.
                            // startActivityForResult is used to start another activity and expect a result back from it
                            // If you add finish() immediately after startActivityForResult, the current activity (GuestActivity)
                            // will be closed right after starting the new activity (WriteNoteActivity). This means that GuestActivity
                            // will not be around to receive the result when WriteNoteActivity finishes.
                            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // This code is executed when the "Cancel" button is clicked.

                        // Dismiss the dialog without doing anything.
                        dialog.dismiss();
                    }
                });

        // Create the AlertDialog object from the builder and show the dialog on the screen.
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK && data != null) {
            String noteTitle = data.getStringExtra("noteTitle");
            String noteContent = data.getStringExtra("noteContent");
            String noteKind = data.getStringExtra("noteKind");
            String noteDate = data.getStringExtra("noteDate");
            int noteIndex = data.getIntExtra("index", -1);
            boolean isPressed = data.getBooleanExtra("isPressed", false);
            String currentBackgroundColor = data.getStringExtra("backgroundColor");

            if (noteIndex == -1) {
                Note newNote = new Note(noteTitle, noteContent, noteDate, noteKind, "", "", currentBackgroundColor, isPressed);
                noteList.add(newNote);
                saveNotes();
                displayFilteredNotes("");
            } else {

                Note editedNote = noteList.get(noteIndex);
                editedNote.setTitle(noteTitle);
                editedNote.setContent(noteContent);
                editedNote.setKind(noteKind);
                editedNote.setDate(noteDate);
                editedNote.setPressed(isPressed);
                editedNote.setBackgroundColorString(currentBackgroundColor);

                // Update UI or save changes as needed
                saveNotes();
                displayFilteredNotes(""); // Refresh UI
            }


        }
    }

    @SuppressLint("SetTextI18n")
    private void addNoteCard(Note note) {
        // Inflate the layout for the note card from the XML resource
        View noteCard = getLayoutInflater().inflate(R.layout.card, null);

        CardView cardView = noteCard.findViewById(R.id.note1);
        TextView noteTitle = noteCard.findViewById(R.id.noteTitle);
        TextView noteContent = noteCard.findViewById(R.id.noteContent);
        TextView noteDate = noteCard.findViewById(R.id.noteDate);
        Button noteKind = noteCard.findViewById(R.id.noteKind);
        ImageView noteMenu = noteCard.findViewById(R.id.noteMenu);
        ImageView noteLike = noteCard.findViewById(R.id.noteLike);

        for (int i = 0; i < noteList.size(); i++) {
            if (noteList.get(i).equals(note)) {
                index = i; // Found the index of the note
                break; // Exit the loop once found
            }
        }

        // Set up an OnClickListener for the noteMenu to show a popup menu when clicked
        noteMenu.setOnClickListener(v -> showPopupMenu(v, note));


        noteTitle.setText(note.getTitle());

        if (note.getContent().equals("")) {
            noteContent.setText("No content");
        } else {
            noteContent.setText(note.getContent());
        }

        noteDate.setText(note.getDate());
        noteKind.setText(note.getKind());

        // Set up an OnClickListener for the noteKind button to show a dialog for changing the note's kind
        noteKind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeKindDialog(note);
            }
        });

        // Display or hide the like icon based on whether the note is pressed
        if (note.isPressed()) {
            noteLike.setVisibility(View.VISIBLE);
        } else {
            noteLike.setVisibility(View.INVISIBLE);
        }

        if (note.getBackgroundColorString() != null) {
            int color = Color.parseColor(note.getBackgroundColorString());
            noteContent.setBackgroundColor(color);
            cardView.setCardBackgroundColor(color);
        } else {
            // Handle the case when the background color is null (e.g., set a default color)
            int defaultColor = Color.parseColor("#fbfcfc");
            noteContent.setBackgroundColor(defaultColor);
            cardView.setCardBackgroundColor(defaultColor);
        }


        // Set button background color based on the kind
        switch (note.getKind()) {
            case "Important":
                noteKind.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#58D68D")));
                break;
            case "Personal":
                noteKind.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A569BD")));
                break;
            case "Education":
                noteKind.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8a6f8f")));
                break;
            case "Work":
                noteKind.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F39C12")));
                break;
            default:
                noteKind.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                break;

        }
        notesContainer.addView(noteCard);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestActivity.this, WriteNoteActivity.class);
                intent.putExtra("index", index);
                intent.putExtra("noteTitle", note.getTitle());
                intent.putExtra("noteContent", note.getContent());
                intent.putExtra("noteKind", note.getKind());
                intent.putExtra("noteDate", note.getDate());
                intent.putExtra("isPressed", note.isPressed());
                intent.putExtra("backgroundColorString", note.getBackgroundColorString());
                startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
            }
        });
    }

    // Save notes to SharedPreferences
    private void saveNotes() {
        //Shared Preferences  is a mechanism for storing small amounts of primitive data
        // Obtain a SharedPreferences instance for storing note data.
        //  `NOTES_PREFS` is the name of the SharedPreferences file.
        SharedPreferences sharedPreferences = getSharedPreferences(NOTES_PREFS, Context.MODE_PRIVATE);

        // Create an editor to make changes to the SharedPreferences file.
        SharedPreferences.Editor editor = sharedPreferences.edit();


        Gson gson = new Gson();

        // Convert the noteList to a JSON string.
        // `noteList` is the list of Note objects that need to be saved.
        String json = gson.toJson(noteList);

        // Save the JSON string to SharedPreferences using the key `NOTES_KEY`.
        // This stores the serialized noteList in SharedPreferences.
        editor.putString(NOTES_KEY, json);

        // Apply the changes to the SharedPreferences file.
        editor.apply();
    }

    // Load notes from SharedPreferences
    private void loadNotes() {
        SharedPreferences sharedPreferences = getSharedPreferences(NOTES_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(NOTES_KEY, null);
        Type type = new TypeToken<ArrayList<Note>>() {
        }.getType();

        noteList = gson.fromJson(json, type);
        if (noteList == null) {
            noteList = new ArrayList<>();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save notes when the activity is stopped (e.g., when the user quits the app)
        saveNotes();
    }

    @SuppressLint("SetTextI18n")
    private void displayFilteredNotes(String filterKind) {
        // Clear all views currently in the notesContainer (e.g., previously displayed notes)
        notesContainer.removeAllViews();

        // Variable to track whether any notes were displayed
        boolean anyNotesDisplayed = false;

        // Check for empty notes and update emptyNote visibility and text
        if (noteList.isEmpty()) {
            emptyNote.setText("Click on (+) to add notes");
            emptyNote.setVisibility(View.VISIBLE);
        } else if (countNotesByKind("Personal") == 0 && filterKind.equals("Personal")) {
            emptyNote.setText("No Personal notes");
            emptyNote.setVisibility(View.VISIBLE);
        } else if (countNotesByKind("Important") == 0 && filterKind.equals("Important")) {
            emptyNote.setText("No Important notes");
            emptyNote.setVisibility(View.VISIBLE);
        } else if (countNotesByKind("Work") == 0 && filterKind.equals("Work")) {
            emptyNote.setText("No Work notes");
            emptyNote.setVisibility(View.VISIBLE);
        } else if (countNotesByKind("Education") == 0 && filterKind.equals("Education")) {
            emptyNote.setText("No Education notes");
            emptyNote.setVisibility(View.VISIBLE);
        } else {
            emptyNote.setVisibility(View.GONE);
        }

// Iterate through the noteList and add note cards for notes that match the filter criteria

        for (Note note : noteList) {
            if (filterKind.isEmpty() || note.getKind().equals(filterKind)) {
                addNoteCard(note);
                anyNotesDisplayed = true;
            }
        }

        if (anyNotesDisplayed) {
            emptyNote.setVisibility(View.GONE);
        } else {
            emptyNote.setVisibility(View.VISIBLE);
        }


        // Update button texts with note counts for each filter
        updateButtonCounts();
    }

    // Update button texts with note counts for each filter
    private void updateButtonCounts() {
        int allCount = noteList.size();
        int personalCount = countNotesByKind("Personal");
        int importantCount = countNotesByKind("Important");
        int workCount = countNotesByKind("Work");
        int educationCount = countNotesByKind("Education");

        all.setText("#All (" + allCount + ")");
        personal.setText("#Personal (" + personalCount + ")");
        important.setText("#Important (" + importantCount + ")");
        work.setText("#Work (" + workCount + ")");
        education.setText("#Education (" + educationCount + ")");
    }

    // Count notes by kind
    private int countNotesByKind(String kind) {
        int count = 0;
        for (Note note : noteList) {
            if (note.getKind().equals(kind)) {
                count++;
            }
        }
        return count;
    }

    // Method to show PopupMenu for note options
    public void showPopupMenu(View view, Note note) {

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.note_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.edit) {
                    editNote(note);
                    return true;
                } else if (item.getItemId() == R.id.delete) {
                    deleteNote(note);
                    return true;
                } else {
                    return false;
                }

            }
        });

        popupMenu.show();
    }

    // Method to edit a note
    private void editNote(Note note) {

        for (int i = 0; i < noteList.size(); i++) {
            if (noteList.get(i).equals(note)) {
                index = i; // Found the index of the note
                break; // Exit the loop once found
            }
        }
        if (index != -1) {
            Intent intent = new Intent(GuestActivity.this, WriteNoteActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("noteTitle", note.getTitle());
            intent.putExtra("noteContent", note.getContent());
            intent.putExtra("noteKind", note.getKind());
            intent.putExtra("noteDate", note.getDate());
            intent.putExtra("isPressed", note.isPressed());
            intent.putExtra("backgroundColorString", note.getBackgroundColorString());
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

        } else {
            Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show();
        }


    }

    // Method to delete a note
    private void deleteNote(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note");
        builder.setMessage("Are you sure you want to delete this note?");

        // Set icon for the dialog
        builder.setIcon(android.R.drawable.ic_menu_delete);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Remove the note from the list
                noteList.remove(note);
                saveNotes(); // Make sure to save changes to SharedPreferences or database
                displayFilteredNotes(""); // Update UI after deletion
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User cancelled the dialog, do nothing
            }
        });

        builder.show();
    }

    private void showChangeKindDialog(Note note) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_filter, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Note Kind").setView(dialogView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    Toast.makeText(GuestActivity.this, "You have to choose a note kind", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                    String selectedText = selectedRadioButton.getText().toString();
                    note.setKind(selectedText);
                    saveNotes();
                    displayFilteredNotes(""); // Refresh the UI to show the updated note
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_add_note) {
            // Handle add note action
            if (noteList.size() <= 9) {
                showFilterDialog();
            } else {
                DialogShowLoginSignUp.showLoginSignUpDialog(GuestActivity.this, "You have reached the limit", "Sign up or log in to continue");
            }
        } else if (id == R.id.nav_filter_all) {
            // Handle filter by all action
            displayFilteredNotes("");
            iNote.setText("All Notes");

        } else if (id == R.id.nav_filter_personal) {
            // Handle filter by personal action
            displayFilteredNotes("Personal");
            iNote.setText("Personal Notes");

        } else if (id == R.id.nav_filter_education) {
            // Handle filter by education action
            displayFilteredNotes("Education");
            iNote.setText("Education Notes");


        } else if (id == R.id.nav_filter_work) {
            // Handle filter by work action
            displayFilteredNotes("Work");
            iNote.setText("Work Notes");

        } else if (id == R.id.nav_filter_important) {
            // Handle filter by important action
            displayFilteredNotes("Important");
            iNote.setText("Important Notes");


        } else if (id == R.id.nav_filter_favorite) {
            // Handle filter by favorite action
            notesContainer.removeAllViews();
            // Check for empty notes and update emptyNote visibility and text
            numberFav = 0;
            for (Note note : noteList) {

                if (note.isPressed()) {
                    addNoteCard(note);
                    numberFav = numberFav + 1;
                }
            }
            iNote.setText("Favorite Notes (" + numberFav + ")");
            if (numberFav == 0) {
                emptyNote.setText("No Favorite Notes");
                emptyNote.setVisibility(View.VISIBLE);
            } else {
                emptyNote.setVisibility(View.INVISIBLE);
            }

        } else if (id == R.id.nav_contact_us) {
            // Handle contact us action
            Intent intent = new Intent(GuestActivity.this, ContactActivity.class);
            intent.putExtra("contact", "contactG");
            startActivity(intent);

            finish();
        } else if (id == R.id.nav_about_us) {
            // Handle about us action
            Intent intent = new Intent(GuestActivity.this, AboutUsActivity.class);
            intent.putExtra("about", "aboutG");
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_share) {
            // Handle share the app action
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "Check out this app!";
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "iNote");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } else if (id == R.id.nav_log_out) {

        }

        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent2 = new Intent(this, MainActivity.class);
        startActivity(intent2);
        finish();

    }
}