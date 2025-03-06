package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Objects;

import io.paperdb.Paper;

public class SeeStaffHiringRequestsActivity extends AppCompatActivity {

    ImageView back;

    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<AllProfile, AllProfileViewHolder> adapter;

    private DatabaseReference paymentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_staff_hiring_requests);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        back = findViewById(R.id.back);

        recyclerView = findViewById(R.id.recycler_pro_hiring_requests);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SeeStaffHiringRequestsActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadData();

    }

    private void loadData() {
        paymentRef = FirebaseDatabase.getInstance().getReference()
                .child("AllStaffRequestProfiles").child(Paper.book().read("Phone")).child("StaffRequestDetails").child("HireProRequests");

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
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("AllStaffRequestProfiles").child(Paper.book().read("Phone")).child("StaffRequestDetails").child("HireProRequests").child(phoneId);

                ref2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() &&
                                (Objects.equals(snapshot.child("isRequestAccepted").getValue(String.class), "0"))) {

                            // Fetch the data

                            String additionalInfo = snapshot.child("AdditionalInfo").getValue(String.class);

                            String startingDate = snapshot.child("StartingDate").getValue(String.class);
                            String endDate = snapshot.child("EndDate").getValue(String.class);

                            String hirerPhoneNo = snapshot.child("HirerPhoneNo").getValue(String.class);
                            String no_of_matches = snapshot.child("No_Of_Matches").getValue(String.class);

                            String totalFees = snapshot.child("TotalFees").getValue(String.class);
                            String address = snapshot.child("TourAddress").getValue(String.class); // Assuming phone number is stored here

                            String info = snapshot.child("TourInfo").getValue(String.class); // Assuming phone number is stored here

                            // Set the data to the views

                            holder.name.setText(startingDate + " - " + endDate);

                            holder.level.setText(totalFees);
                            holder.level.setTextColor(Color.BLUE);

                            holder.phone.setText(hirerPhoneNo); // Set the phone number to the hidden TextView

                            holder.address_text.setText(address);
                            holder.info_text.setText(info);

                            holder.additional_info_text.setText(additionalInfo);

                            if(additionalInfo.isEmpty()) {
                                holder.additional_info_layout.setVisibility(View.GONE);
                            }
                            else {
                                holder.additional_info_layout.setVisibility(View.VISIBLE);
                            }

       //                     holder.priceText.setText(proPerMatchFees + " ( - " + proDiscount + "% )");

                            holder.relativeLayout.setOnClickListener(null);

                            holder.roleText.setText(no_of_matches);

                            holder.call.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + hirerPhoneNo));
                                startActivity(intent);
                            });


                            // Make the item visible
                            holder.itemView.setVisibility(View.VISIBLE);
                            //                         holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                            holder.accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference()
                                            .child("AllStaffRequestProfiles")
                                            .child(Paper.book().read("Phone"))
                                            .child("StaffRequestDetails")
                                            .child("HireProRequests")
                                            .child(phoneId);

                                    DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference()
                                            .child("AllProfiles")
                                            .child(Paper.book().read("Phone").toString());

                                    final HashMap<String, Object> HashMap = new HashMap<>();

                                    HashMap.put("HirerPhoneNo", hirerPhoneNo);
                                    HashMap.put("No_Of_Matches", no_of_matches);
                                    HashMap.put("TourAddress", address);
                                    HashMap.put("TotalFees", totalFees);
                                    HashMap.put("TourInfo", info);
                                    HashMap.put("StartingDate", startingDate);
                                    HashMap.put("EndDate", endDate);
                                    HashMap.put("AdditionalInfo", additionalInfo);

                                    ref2.child("isRequestAccepted").setValue("1")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SeeStaffHiringRequestsActivity.this, "Request Accepted", Toast.LENGTH_SHORT).show();

                                                    // Write to ref3 only after ref2 update is successful
                                                    ref3.child("CurrentlyHiredStaff").child(hirerPhoneNo).setValue(HashMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(SeeStaffHiringRequestsActivity.this, "Added to Currently Hired", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.e("FirebaseError", "Error adding to CurrentlyHired", e);
                                                                    Toast.makeText(SeeStaffHiringRequestsActivity.this, "Failed to add to Currently Hired: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SeeStaffHiringRequestsActivity.this, "Request Accept failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });


                            holder.reject.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    // Update the "IsPro" value to "1"
//                                    ref2.child("StaffRequestDetails").child("IsStaff").setValue("-1")
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    // Handle success
//                                                    Toast.makeText(getContext(), "Staff request Rejected", Toast.LENGTH_SHORT).show();
//                                                }
//                                            })
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//
//                                                public void onFailure(@NonNull Exception e) {
//                                                    // Handle failure
//                                                    Toast.makeText(getContext(), "Failed to reject Staff request", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });

                                    ref2.child("isRequestAccepted").setValue("-1")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(SeeStaffHiringRequestsActivity.this, "Request Declined", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SeeStaffHiringRequestsActivity.this, "Request Decline failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });


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

                //             progressBar.setVisibility(View.INVISIBLE);
            }

            @NonNull
            @Override
            public AllProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.see_hiring_requests_model, parent, false);
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