package com.example.inote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class DialogShowLoginSignUp {

    public static void showLoginSignUpDialog(Activity activity, String title, String message){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);

        // Step 3: Set dialog title and message
        dialogBuilder
                .setTitle(title)
                .setMessage(message)

                // Step 4: Set positive button for logging in
                .setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle click on Log In button
                        Intent loginIntent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(loginIntent);
                    }
                })

                // Step 5: Set negative button for signing up
                .setNegativeButton("Sign Up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle click on Sign Up button
                        Intent signUpIntent = new Intent(activity, SignUpActivity.class);
                        activity.startActivity(signUpIntent);
                    }
                })

                // Step 6: Set neutral button for canceling the dialog
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Dismiss the dialog when Cancel is clicked
                        dialogInterface.dismiss();
                    }
                });

        // Step 7: Create the AlertDialog object
        AlertDialog dialog = dialogBuilder.create();

        // Step 8: Show the dialog
        dialog.show();
    }
}
