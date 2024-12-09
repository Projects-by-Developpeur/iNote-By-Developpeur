package com.example.inote;

import android.app.Activity;
import android.app.AlertDialog;

public class CharacterLimitDialog {

    static public void showCharacterLimitDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Character Limit Exceeded")
                .setMessage("The text cannot exceed 5000 characters.")
                .setPositiveButton("OK", null)
                .show();
    }
}
