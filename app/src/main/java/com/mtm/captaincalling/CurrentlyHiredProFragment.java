package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class CurrentlyHiredProFragment extends Fragment {

    private RecyclerView recyclerView;

    private DatabaseReference paymentRef;

    private FirebaseRecyclerAdapter<AllProfile, AllProfileViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_currently_hired_pro_fragment, container, false);


        recyclerView = rootView.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        loadData();

        return rootView;
    }

    private void loadData() {
        paymentRef = FirebaseDatabase.getInstance().getReference()
                .child("AllProfiles").child(Paper.book().read("Phone")).child("CurrentlyHiredPro");

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

                DatabaseReference ref = paymentRef.child(phoneId);

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            // Fetch the data

                            String additionalInfo = snapshot.child("AdditionalInfo").getValue(String.class);

                            String startingDate = snapshot.child("StartingDate").getValue(String.class);
                            String endDate = snapshot.child("EndDate").getValue(String.class);

                            String hirerPhoneNo = snapshot.child("HirerPhoneNo").getValue(String.class);
                            String no_of_matches = snapshot.child("No_Of_Matches").getValue(String.class);

                            String totalFees = snapshot.child("TotalFees").getValue(String.class);
                            String address = snapshot.child("TourAddress").getValue(String.class); // Assuming phone number is stored here

                            String info = snapshot.child("TourInfo").getValue(String.class); // Assuming phone number is stored here


//                            DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child("AllProfiles").child(hirerPhoneNo);
//
//                            ref3.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot innerSnapshot) {
//                                    String innerName = innerSnapshot.child("Name").getValue(String.class);
//                                    String innerPicture = innerSnapshot.child("Picture").getValue(String.class);
//                                    String innerState = innerSnapshot.child("State").getValue(String.class);
//                                    String innerAddress = innerSnapshot.child("Address").getValue(String.class);
//                                    String innerDistrict = innerSnapshot.child("District").getValue(String.class);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//
//                            });

                            // Set the data to the views

                            holder.priceText.setText(startingDate + " - " + endDate);

                            holder.level.setText(totalFees);
                            holder.level.setTextColor(Color.BLUE);


                            holder.phone.setText(hirerPhoneNo); // Set the phone number to the hidden TextView

                            //       holder.address_text.setText(address);
                            //     holder.info_text.setText(info);

//                            holder.additional_info_text.setText(additionalInfo);
//
//                            if(additionalInfo.isEmpty()) {
//                                holder.additional_info_layout.setVisibility(View.GONE);
//                            }
//                            else {
//                                holder.additional_info_layout.setVisibility(View.VISIBLE);
//                            }

                            //                     holder.priceText.setText(proPerMatchFees + " ( - " + proDiscount + "% )");

                            holder.full_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Paper.book().write("IntentHirerPhoneNoDetail", hirerPhoneNo);
                                    Paper.book().write("IntentNoOfMatchesDetail", no_of_matches);
                                    Paper.book().write("IntentStartingDateDetail", startingDate);
                                    Paper.book().write("IntentEndDateDetail", endDate);
                                    Paper.book().write("IntentPriceDetail", totalFees);

                                    startActivity(new Intent(getContext(), ShowCurrentlyHiredDetailsActivity.class));
                                }
                            });

                            holder.roleText.setText(no_of_matches);

                            holder.call.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + hirerPhoneNo));
                                startActivity(intent);
                            });


                            // Make the item visible
                            holder.itemView.setVisibility(View.VISIBLE);
                            //                         holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currently_hired_model, parent, false);
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