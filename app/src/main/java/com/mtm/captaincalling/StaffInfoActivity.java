package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;

public class StaffInfoActivity extends AppCompatActivity {

    ImageView back, addStaffExperience;

    private Uri uri;

    Spinner spinner;
    private ProgressDialog loadingBar;

    String isStaff;

    private Button submitDetails, seeRequests;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    Boolean isCrickedSelected = false, isFootballSelected = false, isKabaddiSelected = false, isVolleyballSelected = false, isBasketBallSelected = false;

    String selectedStaffRole = "", imageUrl;
    private TextView perMatchFeesText, discountText, cricketText, footballText, kabaddiText, volleyballText, basketballText, videoLinkText;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_staff_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        back = findViewById(R.id.back);
        spinner = findViewById(R.id.edit_staff_spinner);

        addStaffExperience = findViewById(R.id.add_staff_experience);

        submitDetails = findViewById(R.id.button_submit_details);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        cricketText = findViewById(R.id.cricket_text);
        footballText = findViewById(R.id.football_text);
        kabaddiText = findViewById(R.id.kabaddi_text);
        volleyballText = findViewById(R.id.volleyball_text);
        basketballText = findViewById(R.id.basketball_text);

        perMatchFeesText = findViewById(R.id.per_match_fees_text);
        discountText = findViewById(R.id.discount_text);

        videoLinkText = findViewById(R.id.video_link);

        seeRequests = findViewById(R.id.see_requests);

        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("AllStaffRequestProfiles").child(Paper.book().read("Phone")).child("StaffRequestDetails");

        profileRef.child("IsStaff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if "IsPro" exists and its value is "1"
                if (dataSnapshot.exists() && Objects.equals(dataSnapshot.getValue(String.class), "1")) {
                    seeRequests.setVisibility(View.VISIBLE);
                    isStaff = "1";
                } else {
                    seeRequests.setVisibility(View.GONE);
                    isStaff = "0";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                //        Log.e("FirebaseError", "Error reading IsPro", databaseError.toException());
            }
        });

        seeRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StaffInfoActivity.this, SeeStaffHiringRequestsActivity.class));
            }
        });

        loadDetails();

        String[] staffArray = new String[]{"Umpire", "Ground Staff", "Commentator"};
        final List<String> sportList = new ArrayList<>(Arrays.asList(staffArray));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.support_simple_spinner_dropdown_item, sportList) {
            @Override
            public boolean isEnabled(int position) {
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                tv.setTextColor(Color.BLACK);

                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStaffRole = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cricketText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCrickedSelected) {
                    cricketText.setTextColor(Color.WHITE);
                    cricketText.setBackgroundResource(R.drawable.pro_bg);
                }
                else {
                    cricketText.setTextColor(Color.BLACK);
                    cricketText.setBackgroundResource(R.drawable.pro_white);
                }
                isCrickedSelected = !isCrickedSelected;
            }
        });

        footballText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFootballSelected) {
                    footballText.setTextColor(Color.WHITE);
                    footballText.setBackgroundResource(R.drawable.pro_bg);
                }
                else {
                    footballText.setTextColor(Color.BLACK);
                    footballText.setBackgroundResource(R.drawable.pro_white);
                }
                isFootballSelected = !isFootballSelected;
            }
        });

        kabaddiText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isKabaddiSelected) {
                    kabaddiText.setTextColor(Color.WHITE);
                    kabaddiText.setBackgroundResource(R.drawable.pro_bg);
                }
                else {
                    kabaddiText.setTextColor(Color.BLACK);
                    kabaddiText.setBackgroundResource(R.drawable.pro_white);
                }
                isKabaddiSelected = !isKabaddiSelected;
            }
        });

        volleyballText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVolleyballSelected) {
                    volleyballText.setTextColor(Color.WHITE);
                    volleyballText.setBackgroundResource(R.drawable.pro_bg);
                }
                else {
                    volleyballText.setTextColor(Color.BLACK);
                    volleyballText.setBackgroundResource(R.drawable.pro_white);
                }
                isVolleyballSelected = !isVolleyballSelected;
            }
        });

        basketballText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBasketBallSelected) {
                    basketballText.setTextColor(Color.WHITE);
                    basketballText.setBackgroundResource(R.drawable.pro_bg);
                }
                else {
                    basketballText.setTextColor(Color.BLACK);
                    basketballText.setBackgroundResource(R.drawable.pro_white);
                }
                isBasketBallSelected = !isBasketBallSelected;
            }
        });

        addStaffExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(StaffInfoActivity.this);
            }
        });

        submitDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCrickedSelected && !isFootballSelected && !isKabaddiSelected && !isVolleyballSelected && !isBasketBallSelected) {
                    Toast.makeText(StaffInfoActivity.this, "Select at least one Sport", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(perMatchFeesText.getText().toString())) {
                    perMatchFeesText.setError("required");
                    perMatchFeesText.requestFocus();
                } else if (TextUtils.isEmpty(discountText.getText().toString())) {
                    discountText.setError("required");
                    discountText.requestFocus();
                }
                else {
                    loadingBar.show();

                    if (uri != null) {
                        uploadAchievementsToFirebase(uri);
                    } else {
                        submitDetailsToDatabase();
                    }
                }
            }
        });
    }

    private void uploadAchievementsToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference imageRef = storageReference.child("staff_experience/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri imageDownloadUri) {
                                    // Once image is uploaded, get its download URL
                                    imageUrl = imageDownloadUri.toString();
                                    // Submit details to the database after image URL is retrieved
                                    submitDetailsToDatabase();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingBar.dismiss();
                                    Toast.makeText(StaffInfoActivity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            loadingBar.dismiss();
                            Toast.makeText(StaffInfoActivity.this, "Staff Experience upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            loadingBar.dismiss();
            Toast.makeText(StaffInfoActivity.this, "Image URI is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitDetailsToDatabase() {
        DatabaseReference proRef = FirebaseDatabase.getInstance().getReference()
                .child("AllStaffRequestProfiles").child(Paper.book().read("Phone"))
                .child("StaffRequestDetails");

        DatabaseReference profileInfoRef = FirebaseDatabase.getInstance().getReference().child("AllProfiles").child(Paper.book().read("Phone"));

        profileInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String phone = snapshot.child("Phone").getValue(String.class);
                    String address = snapshot.child("Address").getValue(String.class);
                    String district = snapshot.child("District").getValue(String.class);
                    String state = snapshot.child("State").getValue(String.class);

                    String name = snapshot.child("Name").getValue(String.class);
                    String picture = snapshot.child("Picture").getValue(String.class);


                    final Map<String, Object> proRequestDetailsHashMap = new HashMap<>();

                    proRequestDetailsHashMap.put("Pro_phone", phone);
                    proRequestDetailsHashMap.put("Pro_address", address);
                    proRequestDetailsHashMap.put("Pro_district", district);
                    proRequestDetailsHashMap.put("Pro_state", state);
                    proRequestDetailsHashMap.put("Pro_name", name);
                    proRequestDetailsHashMap.put("Pro_picture", picture);

                    proRequestDetailsHashMap.put("IsStaff", isStaff);

                    proRequestDetailsHashMap.put("StaffPerMatchFees", perMatchFeesText.getText().toString());
                    proRequestDetailsHashMap.put("StaffDiscount", discountText.getText().toString());

                    proRequestDetailsHashMap.put("StaffVideoLink", videoLinkText.getText().toString());

                    proRequestDetailsHashMap.put("IsCricketSelected", isCrickedSelected);
                    proRequestDetailsHashMap.put("IsFootballSelected", isFootballSelected);
                    proRequestDetailsHashMap.put("IsFootballSelected", isFootballSelected);
                    proRequestDetailsHashMap.put("IsKabaddiSelected", isKabaddiSelected);
                    proRequestDetailsHashMap.put("IsVolleyballSelected", isVolleyballSelected);
                    proRequestDetailsHashMap.put("IsBasketBallSelected", isBasketBallSelected);

                    proRequestDetailsHashMap.put("StaffSelected", selectedStaffRole);

                    proRequestDetailsHashMap.put("StaffExperience", imageUrl != null ? imageUrl : "");


                    proRef.setValue(proRequestDetailsHashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(StaffInfoActivity.this, "Details submitted for Review", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

         //                           startActivity(new Intent(StaffInfoActivity.this, MainActivity.class));

                                    //     finish(); // Finish activity or perform oth er actions
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingBar.dismiss();
                                    Toast.makeText(StaffInfoActivity.this, "Failed to Submit Details", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadDetails() {
        DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference().child("AllStaffRequestProfiles").child(Paper.book().read("Phone")).child("StaffRequestDetails");

        infoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String staffExperienceUrl = snapshot.child("StaffExperience").getValue(String.class);

                    String perMatchFees = snapshot.child("StaffPerMatchFees").getValue(String.class);
                    String discount = snapshot.child("StaffDiscount").getValue(String.class);

                    String videoLink = snapshot.child("StaffVideoLink").getValue(String.class);

                    Boolean IsCricketSelected = snapshot.child("IsCricketSelected").getValue(Boolean.class);
                    Boolean IsFootballSelected = snapshot.child("IsFootballSelected").getValue(Boolean.class);
                    Boolean IsKabaddiSelected = snapshot.child("IsKabaddiSelected").getValue(Boolean.class);
                    Boolean IsVolleyballSelected = snapshot.child("IsVolleyballSelected").getValue(Boolean.class);
                    Boolean IsBasketBallSelected = snapshot.child("IsBasketBallSelected").getValue(Boolean.class);

                    String selectedStaff = snapshot.child("StaffSelected").getValue(String.class);

                    // Load image using Glide
                    if (!TextUtils.isEmpty(staffExperienceUrl)) {
                        imageUrl = staffExperienceUrl; // Set imageUrl
                        Glide.with(StaffInfoActivity.this)
                                .load(staffExperienceUrl)
                                .into(addStaffExperience);
                    } else {
                        Glide.with(StaffInfoActivity.this)
                                .load(R.drawable.add_pic)
                                .into(addStaffExperience);
                    }

                    perMatchFeesText.setText(perMatchFees);
                    discountText.setText(discount);
                    videoLinkText.setText(videoLink);

                    // Set the selected sport in the spinner

                    int spinnerPosition = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(selectedStaff);
                    spinner.setSelection(spinnerPosition);



                    isCrickedSelected = IsCricketSelected;

                    if(Boolean.TRUE.equals(isCrickedSelected)) {
                        cricketText.setTextColor(Color.WHITE);
                        cricketText.setBackgroundResource(R.drawable.pro_bg);
                    }

                    isFootballSelected = IsFootballSelected;

                    if (isFootballSelected) {
                        footballText.setTextColor(Color.WHITE);
                        footballText.setBackgroundResource(R.drawable.pro_bg);
                    }

                    isKabaddiSelected = IsKabaddiSelected;

                    if (isKabaddiSelected) {
                        kabaddiText.setTextColor(Color.WHITE);
                        kabaddiText.setBackgroundResource(R.drawable.pro_bg);
                    }

                    isVolleyballSelected = IsVolleyballSelected;

                    if (isVolleyballSelected) {
                        volleyballText.setTextColor(Color.WHITE);
                        volleyballText.setBackgroundResource(R.drawable.pro_bg);
                    }

                    isBasketBallSelected = IsBasketBallSelected;

                    if (isBasketBallSelected) {
                        basketballText.setTextColor(Color.WHITE);
                        basketballText.setBackgroundResource(R.drawable.pro_bg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StaffInfoActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                imageUrl = uri.toString(); // Set imageUrl to the new URI
                addStaffExperience.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Cropping error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}