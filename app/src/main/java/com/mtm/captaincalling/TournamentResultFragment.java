package com.mtm.captaincalling;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mtm.captaincalling.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;

public class TournamentResultFragment extends Fragment {

    private EditText Team1NameText, Team2NameText, MatchResultsText, TournamentResults;
    private Button uploadButton, uploadButtonFinished;
    private FirebaseFirestore db;

    private TextView textViewUpcoming;

    private RelativeLayout screenOngoing, screenFinished;

    public TournamentResultFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tournament_result, container, false);
        uploadButton = view.findViewById(R.id.upload_button);
        uploadButtonFinished = view.findViewById(R.id.upload_button_2);

        textViewUpcoming = view.findViewById(R.id.textViewUpcoming);
        screenOngoing = view.findViewById(R.id.screen_ongoing);
        screenFinished = view.findViewById(R.id.screen_finished);

        Team1NameText = view.findViewById(R.id.input_team_1_name);
        Team2NameText = view.findViewById(R.id.input_team_2_name);
        MatchResultsText = view.findViewById(R.id.input_match_result);

        TournamentResults = view.findViewById(R.id.tournament_result);

        // Assuming tournamentId is obtained or set somewhere in your app
        String tournamentId = Paper.book().read("TournamentKey");
        String tournamentStatus = Paper.book().read("TournamentStatus");

 //       Toast.makeText(getContext(), tournamentStatus, Toast.LENGTH_SHORT).show();

        if(Objects.equals(tournamentStatus, "Upcoming")) {
            screenFinished.setVisibility(View.GONE);
            screenOngoing.setVisibility(View.GONE);
            textViewUpcoming.setVisibility(View.VISIBLE);
            textViewUpcoming.setText("This is an Upcoming Tournament. Results will be updated as tournament starts");
        }
        else {
            if (Objects.equals(tournamentStatus, "Ongoing")) {
                screenOngoing.setVisibility(View.VISIBLE);
                screenFinished.setVisibility(View.GONE);
                textViewUpcoming.setVisibility(View.GONE);
            }

            else {
                screenFinished.setVisibility(View.VISIBLE);
                screenOngoing.setVisibility(View.GONE);
                textViewUpcoming.setVisibility(View.GONE);
            }
        }

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(haveNetworkConnection()) {
                    uploadMatchResultToFirebase(tournamentId);
                }
                else {
                    Toast.makeText(getActivity(), "Internet Connection is not available. Try again later.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadButtonFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()) {
                    uploadResultToFirebase(tournamentId); // Pass the tournamentId
                }
                else {
                    Toast.makeText(getActivity(), "Internet Connection is not available. Try again later.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void uploadMatchResultToFirebase(String tournamentId) {
        String Team1Name = Team1NameText.getText().toString().trim();
        String Team2Name = Team2NameText.getText().toString().trim();
        String MatchResult = MatchResultsText.getText().toString().trim();

        if (TextUtils.isEmpty(Team1Name)) {
            Team1NameText.setError("Required..");
            Team1NameText.requestFocus();
        } else if (TextUtils.isEmpty(Team2Name)) {
            Team2NameText.setError("Required..");
            Team2NameText.requestFocus();
        } else if (TextUtils.isEmpty(MatchResult)) {
            MatchResultsText.setError("Required..");
            MatchResultsText.requestFocus();
        } else {
            final DatabaseReference dbRef;
            dbRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentId).child("MatchResults");

            final Map<String, Object> MatchResultHashMap = new HashMap<>();

            MatchResultHashMap.put("Team1Name", Team1Name);
            MatchResultHashMap.put("Team2Name", Team2Name);
            MatchResultHashMap.put("MatchResult", MatchResult);

            // Get the child count of MatchKey and increment by 1 to get the match number
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String MatchNumber = String.valueOf(dataSnapshot.getChildrenCount() + 1);
                    MatchResultHashMap.put("MatchNumber", MatchNumber);

                    String MatchKey = dbRef.push().getKey();
                    assert MatchKey != null;

                    dbRef.child(MatchKey).setValue(MatchResultHashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Result Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Result Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error retrieving match count", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadResultToFirebase(String tournamentId) {
        String result = TournamentResults.getText().toString().trim();

        if (TextUtils.isEmpty(result)) {
            TournamentResults.setError("Required..");
            TournamentResults.requestFocus();
        } else {
            // Get a reference to the specific tournament node in the database
            DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference()
                    .child("tournaments")
                    .child(tournamentId)
                    .child("Result");


            // Create a map to hold the result data
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("result", result);

            // Set the result data to the tournament node in the database
            tournamentRef.setValue(resultData)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        Toast.makeText(getContext(), "Result Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(getContext(), "Result Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}