package com.mtm.captaincalling;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class SendTournamentRequestsActivity extends AppCompatActivity {
    ImageView backButton;
    RecyclerView recyclerView;
    DatabaseReference sendTournamentRef, sendInvitationRef, addToTournamentRef;
    FirebaseRecyclerAdapter<SendTournamentRequests, SendTournamentRequestsViewHolder> adapter;

    HashMap<String, Object> addRequestHashmap = new HashMap<>();
    HashMap<String, Object> addRequestHashmap2 = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_tournament_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.back_send_tournament_request);
        recyclerView = findViewById(R.id.send_tournament_recycler_view);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        sendTournamentRef = FirebaseDatabase.getInstance().getReference().child("AllTeam");

        loadTeams();
    }

    private void loadTeams() {
        FirebaseRecyclerOptions<SendTournamentRequests> options = new FirebaseRecyclerOptions.Builder<SendTournamentRequests>()
                .setQuery(sendTournamentRef, SendTournamentRequests.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<SendTournamentRequests, SendTournamentRequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SendTournamentRequestsViewHolder sendTournamentRequestsViewHolder, int i, @NonNull SendTournamentRequests sendTournamentRequests) {
                String teamKey = getRef(i).getKey();
                assert teamKey != null;

                DatabaseReference sendTournamentRef2 = FirebaseDatabase.getInstance().getReference();

                sendTournamentRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("AllTeam").child(teamKey).exists()) {

                            String teamName = dataSnapshot.child("AllTeam").child(teamKey).child("TeamName").getValue(String.class);
                            String teamCaptainName = dataSnapshot.child("AllTeam").child(teamKey).child("Captain").getValue(String.class);
                            String teamSport = dataSnapshot.child("AllTeam").child(teamKey).child("Sport").getValue(String.class);
                            String teamState = dataSnapshot.child("AllTeam").child(teamKey).child("State").getValue(String.class);
                            String teamDistrict = dataSnapshot.child("AllTeam").child(teamKey).child("District").getValue(String.class);
                            String teamAddress = dataSnapshot.child("AllTeam").child(teamKey).child("Address").getValue(String.class);
                            String teamLevel = dataSnapshot.child("AllTeam").child(teamKey).child("Level").getValue(String.class);
                            String teamPicture = dataSnapshot.child("AllTeam").child(teamKey).child("Picture").getValue(String.class);

                            sendTournamentRequestsViewHolder.sendTournamentTeamName.setText(teamName);
                            sendTournamentRequestsViewHolder.sendTournamentTeamCaptianName.setText(teamCaptainName);
                            sendTournamentRequestsViewHolder.sendTournamentSport.setText(teamSport);
                            sendTournamentRequestsViewHolder.sendTournamentState.setText(teamState);
                            sendTournamentRequestsViewHolder.sendTournamentDistrict.setText(teamDistrict);
                            sendTournamentRequestsViewHolder.sendTournamentAddress.setText(teamAddress);
                            sendTournamentRequestsViewHolder.sendTournamentLevel.setText(teamLevel);
                            Glide.with(getApplicationContext()).load(teamPicture).into(sendTournamentRequestsViewHolder.teamPic);

                            sendTournamentRequestsViewHolder.sendTournamentButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(Paper.book().read("TournamentKey"));

                                    tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int no_of_teams = (int) dataSnapshot.child("Teams").getChildrenCount();
                                            String teams = dataSnapshot.child("TournamentTeams").getValue(String.class);

                                            if (no_of_teams < Integer.parseInt(teams)) {

                                                String tournamentKey = Paper.book().read("TournamentKey");
                                                Log.d("tour", tournamentKey);

                                                if (tournamentKey != null && !tournamentKey.isEmpty()) {
                                                    sendInvitationRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentKey).child("Requests").child("Sent");
                                                    addToTournamentRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentKey).child("Teams");
                                                } else {
                                                    Toast.makeText(SendTournamentRequestsActivity.this, "Tournament key is null or empty", Toast.LENGTH_SHORT).show();
                                                }

                                                addRequestHashmap.put("TeamName", teamName);
                                                addRequestHashmap.put("TeamCaptainName", teamCaptainName);
                                                addRequestHashmap.put("TeamDistrict", teamDistrict);
                                                addRequestHashmap.put("TeamAddress", teamAddress);
                                                addRequestHashmap.put("TeamSport", teamSport);

                                                addRequestHashmap2.put("ParticipatingTeamName", teamName);
                                                addRequestHashmap2.put("ParticipatingTeamCaptainName", teamCaptainName);

                                                if (sendInvitationRef != null) {
                                                    sendInvitationRef.child(teamName).setValue(addRequestHashmap)
                                                            .addOnCompleteListener(task -> {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(SendTournamentRequestsActivity.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(SendTournamentRequestsActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                                                                    Log.e("JoinTournamentActivity", "Failed to send request", task.getException());
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(SendTournamentRequestsActivity.this, "Database reference is null", Toast.LENGTH_SHORT).show();
                                                    Log.e("JoinTournamentActivity", "Database reference is null");
                                                }

                                                if (addToTournamentRef != null) {
                                                    assert teamName != null;
                                                    addToTournamentRef.child(teamName).setValue(addRequestHashmap2)
                                                            .addOnCompleteListener(task -> {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(SendTournamentRequestsActivity.this, "Team joined Tournaments", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(SendTournamentRequestsActivity.this, "Some error", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(SendTournamentRequestsActivity.this, "Database 2 reference is null", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else {
                                                Toast.makeText(SendTournamentRequestsActivity.this, "Tournament has reached team limits of "+ no_of_teams+ " teams", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public SendTournamentRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(SendTournamentRequestsActivity.this).inflate(R.layout.send_request_tournament_model, viewGroup, false);
                return new SendTournamentRequestsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
