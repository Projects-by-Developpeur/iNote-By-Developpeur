package com.example.inote;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String ProfileId, uid, id, fullname;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser curretuser;
    boolean doubleBackToExitPressedOnce = false;
    Button all, personal, important, work, education;
    ImageView addButton, menuButton, heartButton;
    int numberFav = 0;
    private DrawerLayout drawerLayout;
    private TextView hi, iNote, emptyNote;
    private LinearLayout notesContainer;
    private String currentFilterKind = "All"; // Default filter

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);  // 0 is the index if you only have one header
        LinearLayout linearLayout = headerView.findViewById(R.id.linearContent);
        ImageView guestphoto = headerView.findViewById(R.id.guestphoto);
    
        linearLayout.setVisibility(View.GONE);
        TextView header_user_type = headerView.findViewById(R.id.userType);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        curretuser = auth.getCurrentUser();
        assert curretuser != null;
        uid = curretuser.getUid();

        //call views by id
        hi = findViewById(R.id.hi);
        iNote = findViewById(R.id.iNote);
        all = findViewById(R.id.buttonAll);
        personal = findViewById(R.id.buttonPersonal);
        important = findViewById(R.id.buttonImportant);
        work = findViewById(R.id.buttonWork);
        education = findViewById(R.id.buttonEducation);
        addButton = findViewById(R.id.addButton);
        menuButton = findViewById(R.id.menuButton);
        heartButton = findViewById(R.id.heartButton);
        emptyNote = findViewById(R.id.emptyNote);
        drawerLayout = findViewById(R.id.drawer_layout);
        notesContainer = findViewById(R.id.notesContainer);

        if (notesContainer.getChildCount() > 0) {
            emptyNote.setVisibility(View.GONE);
        } else {
            emptyNote.setVisibility(View.INVISIBLE);
        }

        // Set onClickListeners for filter buttons
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterNotes("All");
                iNote.setText("All Notes");

            }
        });

        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterNotes("Personal");
                iNote.setText("Personal Notes");
            }
        });

        important.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterNotes("Important");
                iNote.setText("Important Notes");
            }
        });

        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterNotes("Work");
                iNote.setText("Wotrk Notes");
            }
        });

        education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterNotes("Education");
                iNote.setText("Education Notes");
            }
        });
        //heart button
        // When clicking on the heart button, show only the favorite notes
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterFavorite();
            }
        });


        //click on menu button
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

        // add new note
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFilterDialog();
            }
        });

        if (!curretuser.isEmailVerified()) {
            curretuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Please verify your email", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        //updateUri


        //call database
        db.collection("Users").whereEqualTo("email", curretuser.getEmail()).addSnapshotListener((querySnapshot, e) -> {
            if (e != null) return;
            for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                fullname = dc.getDocument().getString("fullname");
                hi.setText("Hi,\n" + fullname);
                header_user_type.setText("Welcome, " + fullname + " \uD83D\uDC99");
                ProfileId = dc.getDocument().getString("id");
                setAvatarImage(fullname, guestphoto);
                loadNotes();
            }

        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_add_note) {
            // Handle add note action
            showFilterDialog();

        } else if (id == R.id.nav_filter_all) {

            filterNotes("All");
            iNote.setText("All Notes");

        } else if (id == R.id.nav_filter_personal) {
            filterNotes("Personal");
            iNote.setText("Personal Notes");
        } else if (id == R.id.nav_filter_education) {
            filterNotes("Education");
            iNote.setText("Education Notes");
        } else if (id == R.id.nav_filter_work) {
            filterNotes("Work");
            iNote.setText("Wotrk Notes");
        } else if (id == R.id.nav_filter_important) {
            filterNotes("Important");
            iNote.setText("Important Notes");
        } else if (id == R.id.nav_filter_favorite) {
            filterFavorite();
        } else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(ProfileActivity.this, ContactActivity.class);
            intent.putExtra("contact", "contactP");
            startActivity(intent);

            finish();
            // Handle contact us action
        } else if (id == R.id.nav_about_us) {
            Intent intent = new Intent(ProfileActivity.this, AboutUsActivity.class);
            intent.putExtra("about", "aboutP");
            startActivity(intent);
            finish();
            // Handle about us action
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "Check out this app!";
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "iNote");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Share via"));

            // Handle share the app action
        } else if (id == R.id.nav_log_out) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Are you sure you want to log out?");

            builder.setIcon(R.drawable.logout);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    if (mAuth.getCurrentUser() == null) {
                        // User is not signed in, update UI accordingly
                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog ad = builder.create();
            ad.show();

        }

        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
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

                        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
                        int selectedId = radioGroup.getCheckedRadioButtonId();

                        if (selectedId == -1) {
                            // If no RadioButton is selected, show a message to the user.
                            Toast.makeText(ProfileActivity.this, "You have to choose your note kind", Toast.LENGTH_SHORT).show();
                        } else {

                            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                            String selectedText = selectedRadioButton.getText().toString();
                            Intent intent = new Intent(ProfileActivity.this, WriteNoteForUsersActivity.class);
                            intent.putExtra("noteKind", selectedText);
                            intent.putExtra("ProfileId", ProfileId);
                            startActivity(intent);
                            finish();

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

    private void loadNotes() {
        db.collection("Users").document(ProfileId)
                .collection("Notes")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    notesContainer.removeAllViews(); // Clear existing views
                    for (DocumentSnapshot document : task.getResult()) {
                        // Inflate the card layout
                        LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
                        View noteCard = inflater.inflate(R.layout.card, null);

                        CardView cardView = noteCard.findViewById(R.id.note1);
                        TextView noteTitle = noteCard.findViewById(R.id.noteTitle);
                        TextView noteContent = noteCard.findViewById(R.id.noteContent);
                        TextView noteDate = noteCard.findViewById(R.id.noteDate);
                        Button noteKind = noteCard.findViewById(R.id.noteKind);
                        ImageView noteMenu = noteCard.findViewById(R.id.noteMenu);
                        ImageView noteLike = noteCard.findViewById(R.id.noteLike);

                        cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ProfileActivity.this, WriteNoteForUsersActivity.class);
                                intent.putExtra("ProfileId", ProfileId);
                                intent.putExtra("noteId", document.getId());
                                intent.putExtra("noteTitle", document.getString("title"));
                                intent.putExtra("noteContent", document.getString("content"));
                                intent.putExtra("isPressed", document.getBoolean("pressed"));
                                intent.putExtra("backgroundColorString", document.getString("backgroundColorString"));
                                intent.putExtra("noteKind", document.getString("kind"));
                                startActivity(intent);
                            }
                        });
                        // Set data to views
                        String title = document.getString("title");
                        String htmlContent = document.getString("content");
                        String date = document.getString("date");
                        String kind = document.getString("kind");
                        boolean isPressed = Boolean.TRUE.equals(document.getBoolean("pressed"));
                        String backgroundColor = document.getString("backgroundColorString");

                        noteTitle.setText(title);
                        if (htmlContent != null && !htmlContent.isEmpty()) {
                            Spannable spannableContent = (Spannable) Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY);
                            noteContent.setText(spannableContent);
                        } else {
                            noteContent.setText("No content");
                        }
                        noteDate.setText(date);
                        noteKind.setText(kind);

                        // Set up an OnClickListener for the noteMenu to show a popup menu when clicked
                        noteMenu.setOnClickListener(v -> showPopupMenu(v, document));

                        // Display or hide the like icon based on whether the note is pressed
                        noteLike.setVisibility(isPressed ? View.VISIBLE : View.INVISIBLE);

                        if (backgroundColor != null) {
                            int color = Color.parseColor(backgroundColor);
                            noteContent.setBackgroundColor(color);
                            cardView.setCardBackgroundColor(color);
                        } else {
                            // Handle the case when the background color is null (e.g., set a default color)
                            int color = Color.parseColor("#fbfcfc"); // White background as default
                            noteContent.setBackgroundColor(color);
                            cardView.setCardBackgroundColor(color);
                        }


                        // Set button background color based on the kind
                        int kindColor = getColorForKind(kind);
                        noteKind.setBackgroundTintList(ColorStateList.valueOf(kindColor));
                        // Set up an OnClickListener for the noteKind button to show a dialog for changing the note's kind
                        noteKind.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showChangeKindDialog(document);
                            }
                        });
                        // Add the card to the LinearLayout
                        notesContainer.addView(noteCard);
                    }
                    updateButtonCounts();
                    updateEmptyNoteVisibility("All"); // Default filter is "All"
                } else {
                    Toast.makeText(ProfileActivity.this, "Error getting notes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filterNotes(String type) {
        // Clear existing notes
        notesContainer.removeAllViews();

        // Query Firestore based on the selected type
        Query query = type.equals("All") ? db.collection("Users").document(ProfileId).collection("Notes").orderBy("date", Query.Direction.DESCENDING) : db.collection("Users").document(ProfileId).collection("Notes").whereEqualTo("kind", type);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
                    View noteCard = inflater.inflate(R.layout.card, null);

                    CardView cardView = noteCard.findViewById(R.id.note1);
                    TextView noteTitle = noteCard.findViewById(R.id.noteTitle);
                    TextView noteContent = noteCard.findViewById(R.id.noteContent);
                    TextView noteDate = noteCard.findViewById(R.id.noteDate);
                    Button noteKind = noteCard.findViewById(R.id.noteKind);
                    ImageView noteMenu = noteCard.findViewById(R.id.noteMenu);
                    ImageView noteLike = noteCard.findViewById(R.id.noteLike);

                    // Set data to views
                    String title = document.getString("title");
                    String htmlContent = document.getString("content");
                    String date = document.getString("date");
                    String kind = document.getString("kind");
                    boolean isPressed = Boolean.TRUE.equals(document.getBoolean("pressed"));
                    String backgroundColor = document.getString("backgroundColorString");
                    if (backgroundColor != null) {
                        int color = Color.parseColor(backgroundColor);
                        noteContent.setBackgroundColor(color);
                        cardView.setCardBackgroundColor(color);
                    } else {
                        // Handle the case when the background color is null (e.g., set a default color)
                        int color = Color.parseColor("#fbfcfc"); // White background as default
                        noteContent.setBackgroundColor(color);
                        cardView.setCardBackgroundColor(color);
                    }

                    noteTitle.setText(title);
                    if (htmlContent != null && !htmlContent.isEmpty()) {
                        Spannable spannableContent = (Spannable) Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY);
                        noteContent.setText(spannableContent);
                    } else {
                        noteContent.setText("No content");
                    }
                    noteDate.setText(date);
                    noteKind.setText(kind);

                    // Display or hide the like icon based on whether the note is pressed
                    noteLike.setVisibility(isPressed ? View.VISIBLE : View.INVISIBLE);


                    // Set button background color based on the kind
                    int kindColor = getColorForKind(kind);
                    noteKind.setBackgroundTintList(ColorStateList.valueOf(kindColor));

                    // Set up an OnClickListener for the noteMenu to show a popup menu when clicked
                    noteMenu.setOnClickListener(v -> showPopupMenu(v, document));

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ProfileActivity.this, WriteNoteForUsersActivity.class);

                            intent.putExtra("ProfileId", ProfileId);
                            intent.putExtra("noteId", document.getId());
                            intent.putExtra("noteTitle", document.getString("title"));
                            intent.putExtra("noteContent", document.getString("content"));
                            intent.putExtra("isPressed", document.getBoolean("pressed"));
                            intent.putExtra("backgroundColorString", document.getString("backgroundColorString"));
                            intent.putExtra("noteKind", document.getString("kind"));
                            startActivity(intent);
                        }
                    });

                    // Set up an OnClickListener for the noteKind button to show a dialog for changing the note's kind
                    noteKind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showChangeKindDialog(document);
                        }
                    });

                    // Add the card to the container
                    notesContainer.addView(noteCard);
                }
                updateButtonCounts();
                updateEmptyNoteVisibility(type);
            } else {
                // Handle errors
                Toast.makeText(this, "Error fetching notes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getColorForKind(String kind) {
        switch (kind) {
            case "Important":
                return Color.parseColor("#58D68D");
            case "Personal":
                return Color.parseColor("#A569BD");
            case "Education":
                return Color.parseColor("#8a6f8f");
            case "Work":
                return Color.parseColor("#F39C12");
            default:
                return Color.GRAY;
        }
    }


    private void updateEmptyNoteVisibility(String filterKind) {
        final int[] pendingCounts = {5}; // Total counts to be performed

        // Initialize the `emptyNote` text and visibility
        emptyNote.setVisibility(View.GONE);

        CountCompleteListener listener = new CountCompleteListener() {
            @Override
            public void onCountComplete(int count, String kind) {
                pendingCounts[0]--;

                // Update the visibility of `emptyNote`
                if (pendingCounts[0] == 0) {
                    // No notes available
                    if (filterKind.equals("All") && count == 0) {
                        emptyNote.setText("Click on (+) to add notes");
                        emptyNote.setVisibility(View.VISIBLE);
                    }
                    // Check specific filter
                    else if (filterKind.equals("Personal") && count == 0) {
                        emptyNote.setText("No Personal notes");
                        emptyNote.setVisibility(View.VISIBLE);
                    } else if (filterKind.equals("Important") && count == 0) {
                        emptyNote.setText("No Important notes");
                        emptyNote.setVisibility(View.VISIBLE);
                    } else if (filterKind.equals("Work") && count == 0) {
                        emptyNote.setText("No Work notes");
                        emptyNote.setVisibility(View.VISIBLE);
                    } else if (filterKind.equals("Education") && count == 0) {
                        emptyNote.setText("No Education notes");
                        emptyNote.setVisibility(View.VISIBLE);
                    } else {
                        emptyNote.setVisibility(View.GONE);
                    }
                }
            }
        };

        // Fetch counts for each kind
        countNotesByKind("All", listener);
        countNotesByKind("Personal", listener);
        countNotesByKind("Important", listener);
        countNotesByKind("Work", listener);
        countNotesByKind("Education", listener);
    }

    private void countNotesByKind(String kind, CountCompleteListener listener) {
        Query query = kind.equals("All") ? db.collection("Users").document(ProfileId).collection("Notes") : db.collection("Users").document(ProfileId).collection("Notes").whereEqualTo("kind", kind);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = task.getResult().size(); // Directly get the count of documents
                listener.onCountComplete(count, kind);
            } else {
                Toast.makeText(this, "Error counting notes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Example of setting the filter kind
    public void onFilterButtonClick(String filterKind) {
        currentFilterKind = filterKind;
        filterNotes(filterKind);
    }

    private void updateButtonCounts() {
        final int[] pendingCounts = {5}; // Total number of counts to be performed
        final int[] noteCounts = new int[5]; // Array to hold counts for All, Personal, Important, Work, Education

        CountCompleteListener listener = new CountCompleteListener() {
            @Override
            public void onCountComplete(int count, String kind) {
                int index = getIndexForKind(kind);
                noteCounts[index] = count; // Update the count for the specific kind
                pendingCounts[0]--;

                // Update the button text based on kind
                switch (kind) {
                    case "Personal":
                        personal.setText("#Personal (" + count + ")");
                        break;
                    case "Important":
                        important.setText("#Important (" + count + ")");
                        break;
                    case "Work":
                        work.setText("#Work (" + count + ")");
                        break;
                    case "Education":
                        education.setText("#Education (" + count + ")");
                        break;
                    case "All":
                        all.setText("#All (" + count + ")");
                        break;
                }

                // Update emptyNote visibility only when all counts are fetched
                if (pendingCounts[0] == 0) {
                    // Assuming you store the current filter type in a member variable
                    updateEmptyNoteVisibility(noteCounts, currentFilterKind);
                }
            }
        };

        // Fetch counts for each category
        countNotesByKind("All", listener);
        countNotesByKind("Personal", listener);
        countNotesByKind("Important", listener);
        countNotesByKind("Work", listener);
        countNotesByKind("Education", listener);
    }

    private int getIndexForKind(String kind) {
        switch (kind) {
            case "Personal":
                return 1;
            case "Important":
                return 2;
            case "Work":
                return 3;
            case "Education":
                return 4;
            case "All":
                return 0;
            default:
                return -1;
        }
    }

    private void updateEmptyNoteVisibility(int[] noteCounts, String filterKind) {
        int allCount = noteCounts[0];
        int personalCount = noteCounts[1];
        int importantCount = noteCounts[2];
        int workCount = noteCounts[3];
        int educationCount = noteCounts[4];

        // Ensure this method is being called on the main thread
        runOnUiThread(() -> {
            // Update visibility based on the current filter
            if ("All".equals(filterKind)) {
                if (allCount == 0) {
                    emptyNote.setText("Click on (+) to add notes");
                    emptyNote.setVisibility(View.VISIBLE);
                } else {
                    emptyNote.setVisibility(View.GONE);
                }
            } else if ("Personal".equals(filterKind)) {
                if (personalCount == 0) {
                    emptyNote.setText("No Personal notes");
                    emptyNote.setVisibility(View.VISIBLE);
                } else {
                    emptyNote.setVisibility(View.GONE);
                }
            } else if ("Important".equals(filterKind)) {
                if (importantCount == 0) {
                    emptyNote.setText("No Important notes");
                    emptyNote.setVisibility(View.VISIBLE);
                } else {
                    emptyNote.setVisibility(View.GONE);
                }
            } else if ("Work".equals(filterKind)) {
                if (workCount == 0) {
                    emptyNote.setText("No Work notes");
                    emptyNote.setVisibility(View.VISIBLE);
                } else {
                    emptyNote.setVisibility(View.GONE);
                }
            } else if ("Education".equals(filterKind)) {
                if (educationCount == 0) {
                    emptyNote.setText("No Education notes");
                    emptyNote.setVisibility(View.VISIBLE);
                } else {
                    emptyNote.setVisibility(View.GONE);
                }
            } else {
                emptyNote.setVisibility(View.GONE);
            }
        });
    }

    // Method to show PopupMenu for note options
    public void showPopupMenu(View view, DocumentSnapshot document) {

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.note_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.edit) {
                    editNote(document);
                    return true;
                } else if (item.getItemId() == R.id.delete) {
                    deleteNote(document);
                    return true;
                } else {
                    return false;
                }

            }
        });

        popupMenu.show();
    }

    private void editNote(DocumentSnapshot document) {
        Intent intent = new Intent(this, WriteNoteForUsersActivity.class);

        intent.putExtra("ProfileId", ProfileId);
        intent.putExtra("noteId", document.getId());
        intent.putExtra("noteTitle", document.getString("title"));
        intent.putExtra("noteContent", document.getString("content"));
        intent.putExtra("isPressed", document.getBoolean("pressed"));
        intent.putExtra("backgroundColorString", document.getString("backgroundColorString"));
        intent.putExtra("noteKind", document.getString("kind"));
        startActivity(intent);
    }

    private void deleteNote(DocumentSnapshot document) {
        // Handle deletion of the note here
        String noteId = document.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note");
        builder.setMessage("Are you sure you want to delete this note?");

        // Set icon for the dialog
        builder.setIcon(android.R.drawable.ic_menu_delete);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Remove the note from the list
                db.collection("Users").document(ProfileId).collection("Notes").document(noteId).delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    loadNotes(); // Reload notes after deletion
                }).addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error deleting note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

    private void showChangeKindDialog(DocumentSnapshot document) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_filter, null);
        String noteId = document.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Note Kind").setView(dialogView).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    Toast.makeText(ProfileActivity.this, "You have to choose a note kind", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
                    String selectedText = selectedRadioButton.getText().toString();
                    // Update the "kind" field of the note in Firestore with the selected kind
                    db.collection("Users").document(ProfileId).collection("Notes").document(noteId).update("kind", selectedText)  // Update with the selected text, not ID
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ProfileActivity.this, "Note kind updated", Toast.LENGTH_SHORT).show();
                                filterNotes("All"); // Refresh the UI to show the updated note
                            }).addOnFailureListener(e -> {
                                Toast.makeText(ProfileActivity.this, "Error updating kind: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
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

    private void filterFavorite() {
        // Clear existing notes
        notesContainer.removeAllViews();

        // Perform a query to get favorite notes from Firestore
        db.collection("Users").document(ProfileId).collection("Notes").whereEqualTo("pressed", true) // Filter for favorite notes
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            numberFav = 0; // Initialize favorite count

                            // Iterate through the query result
                            for (DocumentSnapshot document : task.getResult()) {
                                // Inflate and add note card to the container
                                Note note = document.toObject(Note.class); // Assuming Note class has appropriate fields and methods
                                // Inflate the card layout
                                LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
                                View noteCard = inflater.inflate(R.layout.card, null);

                                CardView cardView = noteCard.findViewById(R.id.note1);
                                TextView noteTitle = noteCard.findViewById(R.id.noteTitle);
                                TextView noteContent = noteCard.findViewById(R.id.noteContent);
                                TextView noteDate = noteCard.findViewById(R.id.noteDate);
                                Button noteKind = noteCard.findViewById(R.id.noteKind);
                                ImageView noteMenu = noteCard.findViewById(R.id.noteMenu);
                                ImageView noteLike = noteCard.findViewById(R.id.noteLike);

                                // Set data to views
                                String title = note.getTitle();
                                String htmlContent = note.getContent();
                                String date = note.getDate();
                                String kind = note.getKind();
                                boolean isPressed = Boolean.TRUE.equals(note.isPressed());
                                String backgroundColor = document.getString("backgroundColorString");

                                noteTitle.setText(title);
                                if (htmlContent != null && !htmlContent.isEmpty()) {
                                    Spannable spannableContent = (Spannable) Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY);
                                    noteContent.setText(spannableContent);
                                } else {
                                    noteContent.setText("No content");
                                }
                                noteDate.setText(date);
                                noteKind.setText(kind);

                                // Set up an OnClickListener for the noteMenu to show a popup menu when clicked
                                // noteMenu.setOnClickListener(v -> showPopupMenu(v, note));

                                // Display or hide the like icon based on whether the note is pressed
                                noteLike.setVisibility(isPressed ? View.VISIBLE : View.INVISIBLE);
                                int color = Color.parseColor(backgroundColor);
                                noteContent.setBackgroundColor(color);
                                cardView.setCardBackgroundColor(color);

                                // Set button background color based on the kind
                                int kindColor = getColorForKind(kind);
                                noteKind.setBackgroundTintList(ColorStateList.valueOf(kindColor));

                                // Add the card to the LinearLayout
                                notesContainer.addView(noteCard);
                                numberFav++;
                            }

                            // Update the favorite notes count text
                            iNote.setText("Favorite Notes (" + numberFav + ")");

                            // Update visibility and text for empty notes message
                            if (numberFav == 0) {
                                emptyNote.setText("No Favorite Notes");
                                emptyNote.setVisibility(View.VISIBLE);
                            } else {
                                emptyNote.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(ProfileActivity.this, "Error fetching favorite notes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setAvatarImage(String userName, ImageView avatarImageView) {
        // Extract the first letter
        char firstLetter = userName.toUpperCase().charAt(0);

        // Create a high-resolution bitmap (larger than actual size, e.g., 300x300)
        int size = 300;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Choose a background color
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // Anti-aliasing for smooth edges
        paint.setColor(Color.parseColor("#2E3A59")); // Background color

        // Draw a larger circle (for high-resolution)
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);

        // Set up paint for drawing the letter
        paint.setColor(Color.parseColor("#fbfcfc")); // Text color
        paint.setTextSize(size / 2); // Proportional text size (half the size of the bitmap)
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true); // Enable smooth text rendering

        // Use font metrics to center the text vertically
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float x = size / 2; // Center horizontally
        float y = (size / 2) - ((fontMetrics.ascent + fontMetrics.descent) / 2); // Center vertically

        // Draw the first letter on the circle
        canvas.drawText(String.valueOf(firstLetter), x, y, paint);

        // Scale the bitmap down to fit the ImageView (e.g., 100x100)
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

        // Set the scaled bitmap as the ImageView's drawable
        avatarImageView.setImageBitmap(scaledBitmap);
    }

    interface CountCompleteListener {
        void onCountComplete(int count, String kind);
    }
    //to quit the app
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed(); // This will exit the app
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click again to quit the app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false; // Reset the flag after 2 seconds
            }
        }, 2000); // 2 seconds delay
    }

}