package com.mtm.captaincalling;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;

public class HireProPlayerActivity extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference paymentRef;
    private FirebaseRecyclerAdapter<AllProfile, AllProfileViewHolder> adapter;

    private ProgressBar progressBar;
    private SearchView searchView;
    private String mQueryString;

    RelativeLayout relative;

    private ProgressDialog loadingBar;
    public String queryFire = "Name";

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 0.92) {
            configuration.fontScale = (float) 0.92;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            //          WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            //        wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            //      getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_hire_pro_player, container, false);
        Paper.init(getContext());

        recyclerView = rootView.findViewById(R.id.recycler_add_player);
        progressBar = rootView.findViewById(R.id.dsdjsbhjsvdhsvdshd);

        loadingBar = new ProgressDialog(getContext());
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

//        searchView.setOnClickListener(view -> searchView.onActionViewExpanded());
//
//        searchView.setOnCloseListener(() -> {
//            progressBar.setVisibility(View.INVISIBLE);
//            return false;
//        });

        loadData();

        return rootView;
    }

    private void loadData() {
        paymentRef = FirebaseDatabase.getInstance().getReference()
                .child("AllProRequestProfiles");

        FirebaseRecyclerOptions<AllProfile> options =
                new FirebaseRecyclerOptions.Builder<AllProfile>()
                        .setQuery(paymentRef, AllProfile.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<AllProfile, AllProfileViewHolder>(options) {

            @Override

            protected void onBindViewHolder(@NonNull AllProfileViewHolder holder, int position, @NonNull AllProfile model) {
                // Set default visibility to GONE
                holder.itemView.setVisibility(View.GONE);

                String phoneId = getRef(position).getKey();
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("AllProRequestProfiles").child(phoneId);

                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("ProRequestDetails").exists() &&
                                (Objects.equals(snapshot.child("ProRequestDetails").child("IsPro").getValue(String.class), "1"))) {

                            // Fetch the data
                            String name = snapshot.child("ProRequestDetails/Pro_name").getValue(String.class);
                            String proPlayerSport = snapshot.child("ProRequestDetails").child("ProPlayerSport").getValue(String.class);
                            String proPlayerRole = snapshot.child("ProRequestDetails").child("ProPlayerRole").getValue(String.class);
                            String picture = snapshot.child("ProRequestDetails/Pro_picture").getValue(String.class);
                            String state = snapshot.child("ProRequestDetails/Pro_state").getValue(String.class);
                            String address = snapshot.child("ProRequestDetails/Pro_address").getValue(String.class);
                            String district = snapshot.child("ProRequestDetails/Pro_district").getValue(String.class);
                            String phone = snapshot.child("ProRequestDetails/Pro_phone").getValue(String.class); // Assuming phone number is stored here


                            String proPerMatchFees = snapshot.child("ProRequestDetails").child("ProPerMatchFees").getValue(String.class);
                            String proDiscount = snapshot.child("ProRequestDetails").child("ProDiscount").getValue(String.class);


                            String achievements = snapshot.child("ProRequestDetails").child("Achievements").getValue(String.class);
                            String skillVideoLink1 = snapshot.child("ProRequestDetails").child("SkillVideoLink1").getValue(String.class);
                            String skillVideoLink2 = snapshot.child("ProRequestDetails").child("SkillVideoLink2").getValue(String.class);
                            String skillVideoLink3 = snapshot.child("ProRequestDetails").child("SkillVideoLink3").getValue(String.class);

                            // Set the data to the views

                            holder.name.setText(name);
                            holder.level.setText(proPlayerSport);
                            holder.phone.setText(phone); // Set the phone number to the hidden TextView


                            holder.priceText.setText(proPerMatchFees + " ( - " + proDiscount + "% )");

                            holder.relativeLayout.setOnClickListener(null);

                            holder.roleText.setText("Role: " + proPlayerRole);

                            holder.call.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + phone));
                                startActivity(intent);
                            });


                            if (TextUtils.isEmpty(achievements)) {
                                holder.viewAchievements.setVisibility(View.GONE);
                            } else {
                                holder.viewAchievements.setVisibility(View.VISIBLE);
                                holder.viewAchievements.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String url = achievements;
                                        if (URLUtil.isValidUrl(url)) {
                                            try {
                                                CustomTabsIntent.Builder builder1 = new CustomTabsIntent.Builder();
                                                CustomTabsIntent customTabsIntent1 = builder1.build();
                                                customTabsIntent1.launchUrl(getContext(), Uri.parse(url));
                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(getContext(), "No application can handle this request. Please install a web browser.", Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }


                            if (TextUtils.isEmpty(skillVideoLink1)) {
                                holder.viewSkillVideoLink1.setVisibility(View.GONE);
                            } else {
                                holder.viewSkillVideoLink1.setVisibility(View.VISIBLE);
                                holder.viewSkillVideoLink1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String url = skillVideoLink1;
                                        if (URLUtil.isValidUrl(url)) {
                                            try {
                                                CustomTabsIntent.Builder builder1 = new CustomTabsIntent.Builder();
                                                CustomTabsIntent customTabsIntent1 = builder1.build();
                                                customTabsIntent1.launchUrl(getContext(), Uri.parse(url));
                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(getContext(), "No application can handle this request. Please install a web browser.", Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            if (TextUtils.isEmpty(skillVideoLink2)) {
                                holder.viewSkillVideoLink2.setVisibility(View.GONE);
                            } else {
                                holder.viewSkillVideoLink2.setVisibility(View.VISIBLE);
                                holder.viewSkillVideoLink2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String url = skillVideoLink2;
                                        if (URLUtil.isValidUrl(url)) {
                                            try {
                                                CustomTabsIntent.Builder builder1 = new CustomTabsIntent.Builder();
                                                CustomTabsIntent customTabsIntent1 = builder1.build();
                                                customTabsIntent1.launchUrl(getContext(), Uri.parse(url));
                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(getContext(), "No application can handle this request. Please install a web browser.", Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            if (TextUtils.isEmpty(skillVideoLink3)) {
                                holder.viewSkillVideoLink3.setVisibility(View.GONE);
                            } else {
                                holder.viewSkillVideoLink3.setVisibility(View.VISIBLE);
                                holder.viewSkillVideoLink3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String url = skillVideoLink3;
                                        if (URLUtil.isValidUrl(url)) {
                                            try {
                                                CustomTabsIntent.Builder builder1 = new CustomTabsIntent.Builder();
                                                CustomTabsIntent customTabsIntent1 = builder1.build();
                                                customTabsIntent1.launchUrl(getContext(), Uri.parse(url));
                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(getContext(), "No application can handle this request. Please install a web browser.", Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }


                            if (isAdded()) {
                                if (!TextUtils.isEmpty(picture)) {
                                    Glide.with(requireContext()).load(picture).into(holder.circleImageView);
                                } else {
                                    Glide.with(requireContext()).load(R.drawable.user).into(holder.circleImageView);
                                }
                            }


                            // Make the item visible
                            holder.itemView.setVisibility(View.VISIBLE);
                            //                         holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                            holder.accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    // Update the "IsPro" value to "1"
//                                    ref2.child("ProRequestDetails").child("IsPro").setValue("1")
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    // Handle success
//                                                    Toast.makeText(getContext(), "Pro Player request accepted", Toast.LENGTH_SHORT).show();
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//
//                                                public void onFailure(@NonNull Exception e) {
//                                                    // Handle failure
//                                                    Toast.makeText(getContext(), "Failed to accept Pro Player request", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });

                                    if(Objects.equals(phone, Paper.book().read("Phone"))) {
                                        Toast.makeText(getContext(), "You cannot hire yourself", Toast.LENGTH_SHORT).show();
                                    }

                                    else {
           //                             Toast.makeText(getContext(), phone + "   " + Paper.book().read("Phone"), Toast.LENGTH_SHORT).show();

                                        Paper.book().write("ProProfilePic", picture);
                                        Paper.book().write("ProStaffName", name);
                                        Paper.book().write("ProStaffPhone", phoneId);
                                        Paper.book().write("ProStaffFees", proPerMatchFees);
                                        Paper.book().write("ProStaffDiscount", proDiscount);


                                        startActivity(new Intent(getContext(), HireRequestDetailsActivity.class));
                                    }
                                }
                            });


//

//                            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    Paper.book().read("Pro_name_detail", name);
//                                    Paper.book().read("ProPlayerRole_detail", proPlayerRole);
//                                    Paper.book().read("ProPlayerSport_detail", proPlayerSport);
//                                    Paper.book().read("Pro_picture_detail", picture);
//                                    Paper.book().read("Pro_state_detail", state);
//                                    Paper.book().read("Pro_address_detail", address);
//                                    Paper.book().read("Pro_district_detail", district);
//
//                                    Paper.book().read("Pro_skillVideoLink1_detail", skillVideoLink1);
//                                    Paper.book().read("Pro_skillVideoLink2_detail", skillVideoLink2);
//                                    Paper.book().read("Pro_skillVideoLink3_detail", skillVideoLink3);
//
//                                    Paper.book().read("Pro_achievements_detail", achievements);
//
//                                    startActivity(new Intent(ProPlayerRequestsActivity.this, AcceptRejectProActivity.class));
//                                }
//                            });



                        } else {
                            holder.itemView.setVisibility(View.GONE);
                            //                       holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });

                progressBar.setVisibility(View.INVISIBLE);
            }

            @NonNull
            @Override
            public AllProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hire_pro_player_model, parent, false);
                return new AllProfileViewHolder(view);
            }


//            @Override
//            public void onDataChanged() {
//                super.onDataChanged();
//                progressBar.setVisibility(View.INVISIBLE);
//                if (getItemCount() == 0) {
//                    Toast.makeText(ProPlayerRequestsActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(@NonNull DatabaseError error) {
//                super.onError(error);
//                Toast.makeText(ProPlayerRequestsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
