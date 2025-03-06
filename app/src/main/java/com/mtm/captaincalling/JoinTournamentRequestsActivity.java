package com.mtm.captaincalling;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class JoinTournamentRequestsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView backButton;
    FirebaseRecyclerAdapter<JoinTournamentRequests, JoinTournamentRequestsViewHolder> adapter;
    DatabaseReference joinRequestsRef;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_tournament_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Paper.init(this);

        backButton = findViewById(R.id.back_tournament_request);
        recyclerView = findViewById(R.id.join_request_recycler_view);
        floatingActionButton = findViewById(R.id.send_request);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JoinTournamentRequestsActivity.this, SendTournamentRequestsActivity.class));
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        String tournamentKey = Paper.book().read("TournamentKey");
        String teamPlayId = Paper.book().read("TeamPlayId");

        if (tournamentKey == null) {
            // Handle the case where the tournament key is null, e.g., show an error message.
            Log.e("JoinTournamentRequests", "Tournament key is null");
            return; // Exit the method to avoid further execution
        }

        Log.e("JoinTournamentRequests", "Tournament key is not null");

        joinRequestsRef = FirebaseDatabase.getInstance().getReference()
                .child("tournaments")
                .child(tournamentKey)
                .child("Requests")
                .child("Received");

        loadRequests();
    }

    public void loadRequests(){
        FirebaseRecyclerOptions<JoinTournamentRequests> options = new FirebaseRecyclerOptions.Builder<JoinTournamentRequests>()
                .setQuery(joinRequestsRef, JoinTournamentRequests.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<JoinTournamentRequests, JoinTournamentRequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JoinTournamentRequestsViewHolder joinTournamentRequestsViewHolder, int i, @NonNull JoinTournamentRequests joinTournamentRequests) {
                String teamPlayId = getRef(i).getKey();
                assert teamPlayId != null;


                DatabaseReference joinRequestRef2 = FirebaseDatabase.getInstance().getReference()
                        .child("tournaments")
                        .child(Paper.book().read("TournamentKey"));



                joinRequestRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Requests").child("Received").child(teamPlayId).exists()) {
                            DataSnapshot receivedSnapshot = dataSnapshot.child("Requests").child("Received").child(teamPlayId);

                            String teamName = receivedSnapshot.child("JoinTournamentTeamName").getValue(String.class);
                            String teamCaptainName = receivedSnapshot.child("JoinTournamentTeamCaptainName").getValue(String.class);
                            String status = receivedSnapshot.child("JoinStatus").getValue(String.class);

                            // Log the entire DataSnapshot for debugging
                            Log.d("onDataChange", "receivedSnapshot: " + receivedSnapshot.toString());

                            if (teamName == null || teamCaptainName == null) {
                                Log.e("onDataChange", "teamName or teamCaptainName is null");
                                Toast.makeText(JoinTournamentRequestsActivity.this, "Error: Team information is incomplete.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Log.d("Team Name", teamName);
                            Log.d("Team Captain", teamCaptainName);
                            assert status != null;
                            Log.d("Status", status);

                            joinTournamentRequestsViewHolder.tournamentTeamName.setText(teamName);
                            joinTournamentRequestsViewHolder.tournamentTeamCaptainName.setText(teamCaptainName);

                            if(status.equals("Accepted")){
                               // joinTournamentRequestsViewHolder.statusDecisionLayout.setVisibility(View.VISIBLE);
                                joinTournamentRequestsViewHolder.requestStatus.setVisibility(View.VISIBLE);
                                joinTournamentRequestsViewHolder.requestStatus.setText("Accepted");
                                joinTournamentRequestsViewHolder.requestStatus.setTextColor(Color.parseColor("#00BF00"));
                                joinTournamentRequestsViewHolder.acceptRequest.setVisibility(View.INVISIBLE);
                                joinTournamentRequestsViewHolder.declineRequest.setVisibility(View.INVISIBLE);
                            }
                            else if(status.equals("Rejected")){
                               // joinTournamentRequestsViewHolder.statusDecisionLayout.setVisibility(View.VISIBLE);
                                joinTournamentRequestsViewHolder.requestStatus.setVisibility(View.VISIBLE);
                                joinTournamentRequestsViewHolder.requestStatus.setText("Rejected");
                                joinTournamentRequestsViewHolder.requestStatus.setTextColor(Color.parseColor("#FF0000"));
                                joinTournamentRequestsViewHolder.acceptRequest.setVisibility(View.INVISIBLE);
                                joinTournamentRequestsViewHolder.declineRequest.setVisibility(View.INVISIBLE);
                            }
                            else{

                                joinTournamentRequestsViewHolder.requestStatus.setVisibility(View.GONE);
                            }

                            joinTournamentRequestsViewHolder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final String tournamentKey = Paper.book().read("TournamentKey");
                                    final DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference()
                                            .child("tournaments")
                                            .child(tournamentKey)
                                            .child("Requests")
                                            .child("Received")
                                            .child(teamPlayId);

                                    tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            tournamentRef.updateChildren(new HashMap<>()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(JoinTournamentRequestsActivity.this, "Request Accepted, " + teamName + " is now a part of the Tournament", Toast.LENGTH_SHORT).show();

                                                    DatabaseReference updateTournamentTeamsRef = FirebaseDatabase.getInstance().getReference()
                                                            .child("tournaments")
                                                            .child(tournamentKey)
                                                            .child("Requests")
                                                            .child("Received")
                                                            .child(teamPlayId);

                                                    final HashMap<String, Object> updateTeamRequests = new HashMap<>();
                                                    updateTeamRequests.put("JoinTournamentTeamName", teamName);
                                                    updateTeamRequests.put("JoinTournamentTeamCaptainName", teamCaptainName);
                                                    updateTeamRequests.put("JoinStatus", "Accepted");

                                                    updateTournamentTeamsRef.setValue(updateTeamRequests)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    // Add team to "Teams" node
                                                                    DatabaseReference viewTeamsRef = FirebaseDatabase.getInstance().getReference()
                                                                            .child("tournaments")
                                                                            .child(tournamentKey)
                                                                            .child("Teams");

                                                                    HashMap<String, Object> teamDetails = new HashMap<>();
                                                                    teamDetails.put("ParticipatingTeamName", teamName);
                                                                    teamDetails.put("ParticipatingTeamCaptainName", teamCaptainName);

                                                                    viewTeamsRef.child(teamPlayId).setValue(teamDetails)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    // Update the UI to reflect the accepted status
                                                                                    joinTournamentRequestsViewHolder.statusDecisionLayout.setVisibility(View.GONE);
                                                                                    joinTournamentRequestsViewHolder.requestStatus.setVisibility(View.VISIBLE);
                                                                                    joinTournamentRequestsViewHolder.requestStatus.setText("Accepted");
                                                                                    joinTournamentRequestsViewHolder.requestStatus.setTextColor(Color.parseColor("#00BF00"));
                                                                                    joinTournamentRequestsViewHolder.acceptRequest.setVisibility(View.INVISIBLE);
                                                                                    joinTournamentRequestsViewHolder.declineRequest.setVisibility(View.INVISIBLE);
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    // Handle failure if needed
                                                                                    Toast.makeText(JoinTournamentRequestsActivity.this, "Failed to add team to tournament: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Handle failure if needed
                                                                    Toast.makeText(JoinTournamentRequestsActivity.this, "Failed to update request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle cancellation if needed
                                            Toast.makeText(JoinTournamentRequestsActivity.this, "Request cancelled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });


                            joinTournamentRequestsViewHolder.declineRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference().child("tournaments")
                                            .child(Paper.book().read("TournamentKey"))
                                            .child("Requests")
                                            .child("Received")
                                            .child(teamPlayId);

                                    tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            tournamentRef.updateChildren(new HashMap<>()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(JoinTournamentRequestsActivity.this, "Request Declined, " + teamName + " will not be a part of the Tournament", Toast.LENGTH_SHORT).show();


                                                    DatabaseReference updateTournamentTeamsRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(Paper.book().read("TournamentKey")).child("Requests").child("Received").child(teamPlayId);

                                                    final HashMap<String, Object> updateTeamRequests = new HashMap<>();

                                                    updateTeamRequests.put("JoinTournamentTeamName", teamName);
                                                    updateTeamRequests.put("JoinTournamentTeamCaptainName", teamCaptainName);
                                                    updateTeamRequests.put("JoinStatus", "Rejected");

                                                    updateTournamentTeamsRef.setValue(updateTeamRequests)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    // Handle success if needed
                                                                    joinTournamentRequestsViewHolder.statusDecisionLayout.setVisibility(View.VISIBLE);
                                                                    joinTournamentRequestsViewHolder.requestStatus.setVisibility(View.VISIBLE);
                                                                    joinTournamentRequestsViewHolder.requestStatus.setText("Rejected");
                                                                    joinTournamentRequestsViewHolder.requestStatus.setTextColor(Color.parseColor("#FF0000"));
                                                                    joinTournamentRequestsViewHolder.acceptRequest.setVisibility(View.INVISIBLE);
                                                                    joinTournamentRequestsViewHolder.declineRequest.setVisibility(View.INVISIBLE);

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Handle failure if needed
                                                                }
                                                            });
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle cancellation if needed
                                        }
                                    });
                                }
                            });
                        } else {
                            Log.e("onDataChange", "DataSnapshot does not exist for the given team");
                            Toast.makeText(JoinTournamentRequestsActivity.this, "Error: Request does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("onCancelled", "DatabaseError: " + databaseError.getMessage());
                    }
                });

            }

            @NonNull
            @Override
            public JoinTournamentRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.join_tournament_request_model, viewGroup, false);
                return new JoinTournamentRequestsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
