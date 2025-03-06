package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProPlayerInformationActivity extends AppCompatActivity {

    FloatingActionButton nextPage;
    TextView showProRequests;
    ImageView backButton;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_player_information);

        backButton = findViewById(R.id.back);
        nextPage = findViewById(R.id.next_page);
        showProRequests = findViewById(R.id.showProRequestsText);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProPlayerInformationActivity.this, ProPlayerUpgradeInfoActivity.class));
            }
        });

        showProRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Admin").child("Password");

                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            password = dataSnapshot.getValue(String.class);
                            // Do something with the password
                            //                     Toast.makeText(ProPlayerInformationActivity.this, "Password: " + password, Toast.LENGTH_SHORT).show();
                            showPasswordDialog();
                        } else {
                            Toast.makeText(ProPlayerInformationActivity.this, "Password not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors.
                        Toast.makeText(ProPlayerInformationActivity.this, "Failed to read password: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProPlayerInformationActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_password, null);
        builder.setView(dialogView);

        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        Button submitButton = dialogView.findViewById(R.id.submitButton);

        AlertDialog dialog = builder.create();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if entered password matches the correct password
                String enteredPassword = passwordEditText.getText().toString();
                if (enteredPassword.equals(password)) {
                    // Password is correct, dismiss the dialog and navigate to ManageTournamentActivity
                    dialog.dismiss();
                    Intent intent = new Intent(ProPlayerInformationActivity.this, ProPlayerRequestsActivity.class);
                    startActivity(intent);
                } else {
                    // Incorrect password, show error message
                    Toast.makeText(ProPlayerInformationActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}
