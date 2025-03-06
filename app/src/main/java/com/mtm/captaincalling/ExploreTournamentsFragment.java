package com.mtm.captaincalling;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.paperdb.Paper;

public class ExploreTournamentsFragment extends Fragment {
    private RecyclerView tournamentRecyclerView;
    private DatabaseReference tournamentRef;
    private FirebaseRecyclerAdapter<ExploreTournaments, ExploreTournamentsViewHolder> tournamentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore_tournaments, container, false);

        tournamentRecyclerView = rootView.findViewById(R.id.explore_tournament_recycler_view);
        tournamentRef = FirebaseDatabase.getInstance().getReference().child("tournaments");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        tournamentRecyclerView.setLayoutManager(linearLayoutManager);
        tournamentRecyclerView.setHasFixedSize(true);

        loadTournaments();

        return rootView;
    }

    private void loadTournaments() {
        FirebaseRecyclerOptions<ExploreTournaments> options =
                new FirebaseRecyclerOptions.Builder<ExploreTournaments>()
                        .setQuery(tournamentRef, ExploreTournaments.class)
                        .build();

        tournamentAdapter = new FirebaseRecyclerAdapter<ExploreTournaments, ExploreTournamentsViewHolder>(options) {

            @NonNull
            @Override
            public ExploreTournamentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_tournament_model, parent, false);
                return new ExploreTournamentsViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull ExploreTournamentsViewHolder holder, int position, @NonNull ExploreTournaments model) {
                String tournamentId = getRef(position).getKey();
                assert tournamentId != null;

                DatabaseReference tournamentRef2 = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentId);

                tournamentRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = snapshot.child("TournamentName").getValue(String.class);
                            String startingDate = snapshot.child("TournamentStartingDate").getValue(String.class);
                            String endDate = snapshot.child("TournamentEndDate").getValue(String.class);
                            String picture = snapshot.child("TournamentBanner").getValue(String.class);
                            String state = snapshot.child("TournamentState").getValue(String.class);
                            String district = snapshot.child("TournamentDistrict").getValue(String.class);
                            String organiser = snapshot.child("OrganiserName").getValue(String.class);
                            String teams = snapshot.child("TournamentTeams").getValue(String.class);
                            String address = snapshot.child("TournamentAddress").getValue(String.class);
                            String info = snapshot.child("TournamentInfo").getValue(String.class);

                            int no_of_teams = (int) snapshot.child("Teams").getChildrenCount();

                            Glide.with(getActivity()).load(picture).into(holder.tournamentBanner);
                            holder.tournamentName.setText(name);
                            holder.tournamentStartingDate.setText(startingDate);
                            holder.tournamentEndDate.setText(endDate);
                            holder.tournamentState.setText(state);
                            holder.tournamentDistrict.setText(district);
                            holder.tournamentOrganiser.setText(organiser);
                            holder.tournamentTeams.setText(teams);
                            holder.tournamentAddress.setText(address);
                            holder.tournamentInfo.setText(info);

                            // Determine tournament status (upcoming, ongoing, or finished)
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date startDate = dateFormat.parse(startingDate);
                                Date endDateObj = dateFormat.parse(endDate);
                                Date today = new Date();

                                if (today.before(startDate)) {
                                    holder.tournamentStatus.setText("Upcoming");
                                    holder.tournamentStatus.setTextColor(Color.parseColor("green"));
   //                                 ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
     //                               scaleAnimation.setDuration(500); // Duration of the scale animation
       //                             scaleAnimation.setStartOffset(0); // No delay before the scale animation starts
         //                           scaleAnimation.setRepeatMode(Animation.REVERSE); // Reverse the animation when it reaches the end
           //                         scaleAnimation.setRepeatCount(Animation.INFINITE); // Repeat the animation once
             //                       holder.tournamentStatus.startAnimation(scaleAnimation);

                                } else if (today.after(endDateObj)) {
                                    holder.tournamentStatus.setText("Finished");
                                    holder.tournamentStatus.setTextColor(Color.parseColor("red"));
          //                          AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
            //                        fadeOutAnimation.setDuration(500); // Duration of the fade-out
              //                      fadeOutAnimation.setStartOffset(0); // Delay before the fade-out starts
                //                    fadeOutAnimation.setFillAfter(true); // Keep the text invisible after the animation ends
                  //                  holder.tournamentStatus.startAnimation(fadeOutAnimation);

                                } else {
                                    holder.tournamentStatus.setText("Ongoing");
                                    holder.tournamentStatus.setTextColor(Color.parseColor("#0A79DF"));
                                    AlphaAnimation blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
                                    blinkAnimation.setDuration(500); // Duration of the blink
                                    blinkAnimation.setStartOffset(20); // Delay before the blink starts
                                    blinkAnimation.setRepeatMode(Animation.REVERSE); // Reverse the animation when it reaches the end
                                    blinkAnimation.setRepeatCount(Animation.INFINITE); // Repeat the animation infinitely
                                    holder.tournamentStatus.startAnimation(blinkAnimation);

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            holder.tournamentCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(holder.tournamentLayout.getVisibility() == View.VISIBLE){
                                        holder.tournamentLayout.setVisibility(View.GONE);
                                    } else{
                                        holder.tournamentLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                            holder.viewTournamentResult.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Paper.book().write("TournamentKey", tournamentId);
                                    Paper.book().write("TournamentStatus", holder.tournamentStatus.getText().toString());

                                    Intent intent = new Intent(getActivity(), ViewTournamentResultActivity.class);
                                    startActivity(intent);
                                }
                            });

                            holder.joinTournament.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Paper.book().write("TournamentKey", tournamentId);

                                    if(no_of_teams < Integer.parseInt(teams)) {
                                        Intent intent = new Intent(getActivity(), JoinTournamentActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "Tournament " + name + " already has " + no_of_teams + " teams", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                            holder.viewTeams.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Paper.book().write("TournamentKey", tournamentId);
                                    startActivity(new Intent(getActivity(), ViewTournamentTeamsActivity.class));
                                }
                            });
                        } else {
                            Log.e("TAG", "Tournament does not exist for tournamentId: " + tournamentId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        tournamentRecyclerView.setAdapter(tournamentAdapter);
        tournamentAdapter.startListening();
    }
}
