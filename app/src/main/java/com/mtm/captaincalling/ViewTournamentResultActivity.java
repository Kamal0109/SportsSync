package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.paperdb.Paper;

public class ViewTournamentResultActivity extends AppCompatActivity {

    private TextView resultTextView, viewMatchResultsText;

    private View underlineView;

    private ImageView backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tournament_result);

        resultTextView = findViewById(R.id.result_text_view);
        viewMatchResultsText = findViewById(R.id.viewMatchResultsText);

        underlineView = findViewById(R.id.viewUnderLine);

        backButton = findViewById(R.id.back_cteam);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Retrieve tournament key from Paper
        String tournamentKey = Paper.book().read("TournamentKey");
        String tournamentStatus = Paper.book().read("TournamentStatus");

        // Get a reference to the specific tournament result node in Firebase
        DatabaseReference tournamentResultRef = FirebaseDatabase.getInstance().getReference()
                .child("tournaments")
                .child(tournamentKey)
                .child("Result")
                .child("result");

        // Read the result data from Firebase
        tournamentResultRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Check if the result data exists
//                if (dataSnapshot.exists()) {
//                    String result = dataSnapshot.getValue(String.class);
//                    // Display the result in the TextView
//                    resultTextView.setText("Result: " + result);
//                } else {
//                    // Handle case where no result data is available
//                    resultTextView.setText("Result not available");
//                }

                if(Objects.equals(tournamentStatus, "Upcoming"))
                {
                    underlineView.setVisibility(View.GONE);
                    viewMatchResultsText.setVisibility(View.GONE);
                    resultTextView.setText("Tournament Result will be updated after Tournament ends");
                    resultTextView.setTextColor(Color.parseColor("red"));
                } else if (Objects.equals(tournamentStatus, "Ongoing")) {
                    underlineView.setVisibility(View.VISIBLE);
                    viewMatchResultsText.setVisibility(View.VISIBLE);
                    resultTextView.setText("Tournament Result will be updated after Tournament ends");
                    resultTextView.setTextColor(Color.parseColor("purple"));
                }
                else {
                    underlineView.setVisibility(View.VISIBLE);
                    viewMatchResultsText.setVisibility(View.VISIBLE);
                    resultTextView.setText(dataSnapshot.getValue(String.class));

                    if(dataSnapshot.exists()) {
                        resultTextView.setText(dataSnapshot.getValue(String.class));
                    }
                    else {
                        resultTextView.setText("Result not available yet");
                    }

                    resultTextView.setTextColor(Color.parseColor("purple"));
                }

            viewMatchResultsText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewTournamentResultActivity.this, ViewMatchResultsActivity.class);
                    startActivity(intent);
                }
            });

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(ViewTournamentResultActivity.this, "Error retrieving result data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}