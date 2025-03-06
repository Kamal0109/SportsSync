package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class ViewMatchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ImageView BackButton;

    private ProgressBar progress;

    private TextView NoMatchYetText;

    private FirebaseRecyclerAdapter<ViewMatchResults, ViewMatchResultsViewHolder> adapter;

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 0.92) {
            configuration.fontScale = (float) 0.92;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_match_results);
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Paper.init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        recyclerView = findViewById(R.id.recycler_match_results);
        BackButton = findViewById(R.id.back_button);

        progress = findViewById(R.id.progress);

        NoMatchYetText = findViewById(R.id.no_matches_yet);

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadData();
    }

    private void loadData() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("tournaments").child(Paper.book().read("TournamentKey")).child("MatchResults");

        FirebaseRecyclerOptions<ViewMatchResults> options =
                new FirebaseRecyclerOptions.Builder<ViewMatchResults>()
                        .setQuery(dbRef, ViewMatchResults.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ViewMatchResults, ViewMatchResultsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewMatchResultsViewHolder holder, int i, @NonNull ViewMatchResults model) {

                progress.setVisibility(View.VISIBLE);

                String MatchId = getRef(i).getKey();
                assert MatchId != null;

                DatabaseReference ref = dbRef.child(MatchId);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && snapshot.getChildrenCount() > 0) {
                            String matchNoText = snapshot.child("MatchNumber").getValue(String.class);
                            String matchResult = snapshot.child("MatchResult").getValue(String.class);
                            String team1Name = snapshot.child("Team1Name").getValue(String.class);
                            String team2Name = snapshot.child("Team2Name").getValue(String.class);

                            NoMatchYetText.setVisibility(View.GONE);

                            holder.MatchResultText.setText(matchResult);
                            holder.Team1NameText.setText(team1Name);
                            holder.Team2NameText.setText(team2Name);
                            holder.MatchNoText.setText("Match " + matchNoText);
                        }
                        else {
                            NoMatchYetText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                progress.setVisibility(View.GONE);

            }

            @NonNull
            @Override
            public ViewMatchResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_result_model, parent,false);
                return  new ViewMatchResultsViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}