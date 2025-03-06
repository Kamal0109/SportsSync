package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class JoinTournamentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference teamRef;
    private FirebaseRecyclerAdapter<MyTeams, MyTeamViewHolder> adapter;
    private ProgressBar progressBar;
    private TextView textView;
    private HashMap<String, Object> addRequestHashmap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_my_team);
        Paper.init(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        recyclerView = findViewById(R.id.recycler_myteam);
        progressBar = findViewById(R.id.progress_myteam);
        textView = findViewById(R.id.xxccccsdsfsfdwwf);

        ImageView backBtn = findViewById(R.id.back_myteams);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String phone = Paper.book().read("Phone");
        teamRef = FirebaseDatabase.getInstance().getReference("AllProfiles")
                .child(phone)
                .child("MyTeams");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        loadData();
    }

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 0.92) {
            configuration.fontScale = 0.92f;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    private void loadData() {
        FirebaseRecyclerOptions<MyTeams> options =
                new FirebaseRecyclerOptions.Builder<MyTeams>()
                        .setQuery(teamRef, MyTeams.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<MyTeams, MyTeamViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyTeamViewHolder holder, int position, @NonNull MyTeams model) {
                textView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                holder.itemView.setVisibility(View.INVISIBLE);

                String entryId = model.getEntryId();
                if (entryId == null) {
                    Log.e("JoinTournamentActivity", "Entry ID is null for position: " + position);
                    Log.d("JoinTournamentActivity", "Model data: " + model.toString());
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    return;
                }

                Log.d("JoinTournamentActivity", "Entry ID: " + entryId);
                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot allTeamSnapshot = snapshot.child("AllTeam");
                        DataSnapshot teamDataSnapshot = allTeamSnapshot.child(entryId);

                        if (teamDataSnapshot.exists()) {
                            AllTeam teamData = teamDataSnapshot.getValue(AllTeam.class);
                            if (teamData != null) {
                                Glide.with(getApplicationContext())
                                        .load(teamData.getPicture())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                Log.e("Glide", "Error loading image: " + e.getMessage(), e);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                textView.setVisibility(View.VISIBLE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                holder.itemView.setVisibility(View.VISIBLE);
                                                return false;
                                            }
                                        })
                                        .into(holder.circleImageView);

                                String phone = Paper.book().read("Phone");
                                if (teamData.getCaptainPhone().equals(phone)) {
                                    holder.sport.setText(teamData.getSport() + " (created by you)");
                                } else {
                                    holder.sport.setText(teamData.getSport());
                                }

                                holder.title.setText(ProperCase.properCase(teamData.getTeamName()));

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String tournamentId = Paper.book().read("TournamentKey");
                                        DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference().child("tournaments").child(tournamentId).child("Requests").child("Received").push();

                                        String TeamPlayId = playersRef.getKey();

                                        Paper.book().write("TeamPlayId", TeamPlayId);


                                        HashMap<String, Object> addRequestHashmap = new HashMap<>();
                                        addRequestHashmap.put("TeamPlayId", TeamPlayId);
                                        addRequestHashmap.put("JoinTournamentTeamName", teamData.getTeamName());
                                        addRequestHashmap.put("JoinTournamentTeamCaptainName", teamData.getCaptain());
                                        addRequestHashmap.put("JoinStatus", "Pending");

                                        playersRef.setValue(addRequestHashmap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(view.getContext(), "Player added to tournament requests", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(view.getContext(), "Failed to add player to requests", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });

                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        holder.itemView.setVisibility(View.VISIBLE);
                                    }
                                }, 1000);
                            } else {
                                Log.e("JoinTournamentActivity", "Team data is null for entryId: " + entryId);
                            }
                        } else {
                            Log.e("JoinTournamentActivity", "Team data snapshot does not exist for entryId: " + entryId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Database error: " + error.getMessage(), error.toException());
                    }
                });
            }

            @NonNull
            @Override
            public MyTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_team_model, parent, false);
                return new MyTeamViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
