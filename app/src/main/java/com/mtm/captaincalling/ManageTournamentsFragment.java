package com.mtm.captaincalling;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.paperdb.Paper;

public class ManageTournamentsFragment extends Fragment {
    private RecyclerView tournamentRecyclerView;
    private DatabaseReference tournamentRef;
    private FirebaseRecyclerAdapter<ManageTournaments, ManageTournamentViewHolder> tournamentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manage_tournaments, container, false);

        tournamentRecyclerView = rootView.findViewById(R.id.manage_tournament_recycler_view);
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
        FirebaseRecyclerOptions<ManageTournaments> options =
                new FirebaseRecyclerOptions.Builder<ManageTournaments>()
                        .setQuery(tournamentRef, ManageTournaments.class)
                        .build();

        tournamentAdapter = new FirebaseRecyclerAdapter<ManageTournaments, ManageTournamentViewHolder>(options) {

            @NonNull
            @Override
            public ManageTournamentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.manage_tournament_model, viewGroup,false);
                return new ManageTournamentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ManageTournamentViewHolder manageTournamentViewHolder, int i, @NonNull ManageTournaments manageTournaments) {
                String tournamentId = getRef(i).getKey();
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
                            String password = snapshot.child("TournamentPassword").getValue(String.class);

                            manageTournamentViewHolder.manageTournamentName.setText(name != null ? name.toUpperCase() : null);
                            Glide.with(getActivity()).load(picture).into(manageTournamentViewHolder.manageTournamentBanner);

                            manageTournamentViewHolder.tournamentStartingDate.setText(startingDate);
                            manageTournamentViewHolder.tournamentEndDate.setText(endDate);

                            // Determine tournament status (upcoming, ongoing, or finished)
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date startDate = dateFormat.parse(startingDate);
                                Date endDateObj = dateFormat.parse(endDate);
                                Date today = new Date();

                                if (today.before(startDate)) {
                                    manageTournamentViewHolder.tournamentStatus.setText("Upcoming");
                                    manageTournamentViewHolder.tournamentStatus.setTextColor(Color.parseColor("green"));
     //                               TranslateAnimation bounceAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 20.0f);
       //                             bounceAnimation.setDuration(500); // Duration of each bounce
         //                           bounceAnimation.setInterpolator(new BounceInterpolator()); // Use bounce interpolator for a natural bouncing effect
           //                         bounceAnimation.setRepeatMode(Animation.REVERSE); // Reverse the animation when it reaches the end
             //                       bounceAnimation.setRepeatCount(Animation.INFINITE); // Repeat the animation infinitely
               //                     manageTournamentViewHolder.tournamentStatus.startAnimation(bounceAnimation);

                                    manageTournamentViewHolder.endTournamentText.setVisibility(View.GONE);
                                    manageTournamentViewHolder.endTournamentText.setOnClickListener(null); // Remove the click listener


                                } else if (today.after(endDateObj)) {
                                    manageTournamentViewHolder.tournamentStatus.setText("Finished");
                                    manageTournamentViewHolder.tournamentStatus.setTextColor(Color.parseColor("red"));
                                    //                          AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
                                    //                        fadeOutAnimation.setDuration(500); // Duration of the fade-out
                                    //                      fadeOutAnimation.setStartOffset(0); // Delay before the fade-out starts
                                    //                    fadeOutAnimation.setFillAfter(true); // Keep the text invisible after the animation ends
                                    //                  holder.tournamentStatus.startAnimation(fadeOutAnimation);

                                    manageTournamentViewHolder.endTournamentText.setText("Tournament has ended. It will automatically get deleted after 7 days of completion.");
                                    manageTournamentViewHolder.endTournamentText.setTextColor(Color.DKGRAY);

                                    manageTournamentViewHolder.endTournamentText.setVisibility(View.VISIBLE);
                                    manageTournamentViewHolder.endTournamentText.setOnClickListener(null); // Remove the click listener

                                    // Calculate the end date 7 days ago
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                                    Date sevenDaysAgo = calendar.getTime();

                                    // Check if the tournament is finished and its end date was 7 or more days ago
                                    if (endDateObj.before(sevenDaysAgo)) {
                                        // Automatically delete the tournament
                                        tournamentRef2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                    //                                Toast.makeText(getContext(), "Tournament " + name + " has been automatically deleted.", Toast.LENGTH_SHORT).show();
                                                } else {
                     //                               Toast.makeText(getContext(), "Failed to automatically delete tournament " + name, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        return; // Exit the method as the tournament has been deleted
                                    }


                                } else {
                                    manageTournamentViewHolder.tournamentStatus.setText("Ongoing");
                                    manageTournamentViewHolder.tournamentStatus.setTextColor(Color.parseColor("#0A79DF"));
                                    AlphaAnimation blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
                                    blinkAnimation.setDuration(500); // Duration of the blink
                                    blinkAnimation.setStartOffset(20); // Delay before the blink starts
                                    blinkAnimation.setRepeatMode(Animation.REVERSE); // Reverse the animation when it reaches the end
                                    blinkAnimation.setRepeatCount(Animation.INFINITE); // Repeat the animation infinitely
                                    manageTournamentViewHolder.tournamentStatus.startAnimation(blinkAnimation);

                                    manageTournamentViewHolder.endTournamentText.setVisibility(View.VISIBLE);

                                    manageTournamentViewHolder.endTournamentText.setText("End Tournament");
                                    manageTournamentViewHolder.endTournamentText.setTextColor(Color.RED);
                                    manageTournamentViewHolder.endTournamentText.setTextSize(14);

                                    manageTournamentViewHolder.endTournamentText.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("End Tournament");
                                            builder.setMessage("Do you want to end this tournament?");

                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Delete the team here
                                                    // For example:
                                                    // DatabaseReference teamRef = tournamentTeamRef.child(teamKey);
                                                    // teamRef.removeValue();

                                                    showPasswordDialog_end_tour(name, password, tournamentRef2);


                                                }

                                            });

                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Dismiss the dialog
                                                    dialog.dismiss();
                                                }
                                            });
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    });

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            manageTournamentViewHolder.manageTournamentButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Paper.book().write("TournamentKey", tournamentId);
                                    Paper.book().write("TournamentStatus", manageTournamentViewHolder.tournamentStatus.getText().toString());

                                    Intent intent = new Intent(getActivity(), ManageTournamentActivity.class);
                                    intent.putExtra("tournament_name",name);
                                    intent.putExtra("tournament_starting_date",startingDate);
                                    intent.putExtra("tournament_end_date",endDate);
                                    intent.putExtra("tournament_state",state);
                                    intent.putExtra("tournament_district",district);
                                    intent.putExtra("tournament_address",address);
                                    intent.putExtra("tournament_teams",teams);
                                    intent.putExtra("tournament_pass",password);
                                    intent.putExtra("tournament_picture", picture);
            //                        showPasswordDialog(name, password);
                                    // startActivity(intent);


                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                                Intent intent = new Intent(getActivity(), ManageTournamentActivity.class);
                                                intent.putExtra("tournament_name", name);
                                                intent.putExtra("tournament_name",name);
                                                intent.putExtra("tournament_starting_date",startingDate);
                                                intent.putExtra("tournament_end_date",endDate);
                                                intent.putExtra("tournament_state",state);
                                                intent.putExtra("tournament_district",district);
                                                intent.putExtra("tournament_address",address);
                                                intent.putExtra("tournament_teams",teams);
                                                intent.putExtra("tournament_pass",password);
                                                intent.putExtra("tournament_picture", picture);
                                                startActivity(intent);
                                            } else {
                                                // Incorrect password, show error message
                                                Toast.makeText(getActivity(), "Incorrect password", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });

                                    dialog.show();

                                }
                            });

                            manageTournamentViewHolder.manageTournamentCard.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Delete Tournament");
                                    builder.setMessage("Do you want to delete this tournament?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Delete the team here
                                            // For example:
                                            // DatabaseReference teamRef = tournamentTeamRef.child(teamKey);
                                            // teamRef.removeValue();

                                            showPasswordDialog2(name, password, tournamentRef2, name);



                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Dismiss the dialog
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                    return true; // Return true to indicate that the long click was consumed
                                }
                            });

                            Log.d("Tournament Name","Name "+name);
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

    private void showPasswordDialog_end_tour(String name, String correctPassword, DatabaseReference tournamentRef2) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                if (enteredPassword.equals(correctPassword)) {
                    dialog.dismiss();

                    // Update tournament end date to current date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String currentDate = dateFormat.format(new Date());

                    tournamentRef2.child("TournamentEndDate").setValue(currentDate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Tournament "+ name + " has ended", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Tournament could not be ended, retry again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    // Incorrect password, show error message
                    Toast.makeText(getActivity(), "Incorrect password", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }


    private void showPasswordDialog(String tournamentName, String correctPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                if (enteredPassword.equals(correctPassword)) {
                    // Password is correct, dismiss the dialog and navigate to ManageTournamentActivity
                    dialog.dismiss();
                    Intent manageIntent = new Intent(getActivity(), ManageTournamentActivity.class);
                    manageIntent.putExtra("tournament_name", tournamentName);
                    startActivity(manageIntent);
                } else {
                    // Incorrect password, show error message
                    Toast.makeText(getActivity(), "Incorrect password", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }


    private void showPasswordDialog2(String tournamentName, String correctPassword, DatabaseReference tournamentRef2, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                if (enteredPassword.equals(correctPassword)) {
                    // Password is correct, dismiss the dialog and navigate to ManageTournamentActivity
                    dialog.dismiss();
                    //           Intent manageIntent = new Intent(getActivity(), ManageTournamentActivity.class);
                    //             manageIntent.putExtra("tournament_name", tournamentName);
                    //               startActivity(manageIntent);

                    DatabaseReference teamRef = tournamentRef2; // Use the DatabaseReference from ValueEventListener
                    teamRef.removeValue();

                    Toast.makeText(getContext(), "Tournament " + name + " has been deleted!", Toast.LENGTH_SHORT).show();

                } else {
                    // Incorrect password, show error message
                    Toast.makeText(getActivity(), "Incorrect password", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        tournamentAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        tournamentAdapter.stopListening();
    }

}