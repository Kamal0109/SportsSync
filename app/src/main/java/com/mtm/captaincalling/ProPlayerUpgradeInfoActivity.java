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
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class ProPlayerUpgradeInfoActivity extends AppCompatActivity {

    private static final int VIDEO_PICKER_REQUEST_CODE = 1;
    private Uri uri;
    private ImageView back, addAchievements;
    private Button submitDetails, seeRequests;

    private ProgressDialog loadingBar;

    private Spinner spinnerSport;
    private TextView perMatchFeesText, discountText, sportText, roleText, videoLinkText1, videoLinkText2, videoLinkText3;
    private String selectedSport, imageUrl, isPro;
    private FirebaseStorage storage;
    private StorageReference storageReference;

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
        adjustFontScale(getResources().getConfiguration());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_pro_player_upgrade_info);
        Paper.init(ProPlayerUpgradeInfoActivity.this);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        back = findViewById(R.id.back);
        submitDetails = findViewById(R.id.button_submit_details);
        addAchievements = findViewById(R.id.add_achievements);
        spinnerSport = findViewById(R.id.edit_sport_spinner);
        sportText = findViewById(R.id.edit_sport);
        roleText = findViewById(R.id.role_text);

        videoLinkText1 = findViewById(R.id.video_link_1);
        videoLinkText2 = findViewById(R.id.video_link_2);
        videoLinkText3 = findViewById(R.id.video_link_3);

        perMatchFeesText = findViewById(R.id.per_match_fees_text);
        discountText = findViewById(R.id.discount_text);

        seeRequests = findViewById(R.id.see_requests);

        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("AllProRequestProfiles").child(Paper.book().read("Phone")).child("ProRequestDetails");

        profileRef.child("IsPro").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if "IsPro" exists and its value is "1"
                if (dataSnapshot.exists() && Objects.equals(dataSnapshot.getValue(String.class), "1")) {
                    seeRequests.setVisibility(View.VISIBLE);
                    isPro = "1";
                } else {
                    seeRequests.setVisibility(View.GONE);
                    isPro = "0";
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
                startActivity(new Intent(ProPlayerUpgradeInfoActivity.this, SeeProHiringRequestsActivity.class));
            }
        });


            String[] sportArray = new String[]{"Cricket", "Football", "Kabaddi", "Volleyball", "Basketball"};
        final List<String> sportList = new ArrayList<>(Arrays.asList(sportArray));

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
        spinnerSport.setAdapter(spinnerArrayAdapter);

        spinnerSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSport = (String) parent.getItemAtPosition(position);
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


        loadDetails();

        addAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProPlayerUpgradeInfoActivity.this);
            }
        });

        submitDetails.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(roleText.getText().toString())) {
                    roleText.setError("required");
                    roleText.requestFocus();
                }
                else if (TextUtils.isEmpty(perMatchFeesText.getText().toString())) {
                    perMatchFeesText.setError("required");
                    perMatchFeesText.requestFocus();
                } else if (TextUtils.isEmpty(discountText.getText().toString())) {
                    discountText.setError("required");
                    discountText.requestFocus();
                } else {
                    loadingBar.show();

                    if (uri != null) {
                        uploadAchievementsToFirebase(uri);
                    } else {
                        submitDetailsToDatabase();
                    }
                }
            }

            private void uploadAchievementsToFirebase(Uri imageUri) {
                if (imageUri != null) {
                    StorageReference imageRef = storageReference.child("achievements/" + System.currentTimeMillis() + ".jpg");
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
                                            Toast.makeText(ProPlayerUpgradeInfoActivity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    loadingBar.dismiss();
                                    Toast.makeText(ProPlayerUpgradeInfoActivity.this, "Achievements upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(ProPlayerUpgradeInfoActivity.this, "Image URI is null", Toast.LENGTH_SHORT).show();
                }
            }

            private void submitDetailsToDatabase() {
                DatabaseReference proRef = FirebaseDatabase.getInstance().getReference()
                        .child("AllProRequestProfiles").child(Paper.book().read("Phone"))
                        .child("ProRequestDetails");

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

                            proRequestDetailsHashMap.put("IsPro", isPro);
                            proRequestDetailsHashMap.put("ProPerMatchFees", perMatchFeesText.getText().toString());
                            proRequestDetailsHashMap.put("ProDiscount", discountText.getText().toString());
                            proRequestDetailsHashMap.put("ProPlayerSport", selectedSport);
                            proRequestDetailsHashMap.put("ProPlayerRole", roleText.getText().toString());
                            proRequestDetailsHashMap.put("Achievements", imageUrl != null ? imageUrl : "");
                            proRequestDetailsHashMap.put("SkillVideoLink1", videoLinkText1.getText().toString());
                            proRequestDetailsHashMap.put("SkillVideoLink2", videoLinkText2.getText().toString());
                            proRequestDetailsHashMap.put("SkillVideoLink3", videoLinkText3.getText().toString());

                            proRef.setValue(proRequestDetailsHashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ProPlayerUpgradeInfoActivity.this, "Details submitted for Review", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();

                                            startActivity(new Intent(ProPlayerUpgradeInfoActivity.this, MainActivity.class));

                                            //     finish(); // Finish activity or perform oth er actions
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loadingBar.dismiss();
                                            Toast.makeText(ProPlayerUpgradeInfoActivity.this, "Failed to Submit Details", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }


        });
    }

    private void loadDetails() {
        DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference().child("AllProRequestProfiles").child(Paper.book().read("Phone")).child("ProRequestDetails");

        infoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String achievementsUrl = snapshot.child("Achievements").getValue(String.class);
                    String proPlayerSport = snapshot.child("ProPlayerSport").getValue(String.class);
                    String proPlayerRole = snapshot.child("ProPlayerRole").getValue(String.class);
                    String skillVideoLink1 = snapshot.child("SkillVideoLink1").getValue(String.class);
                    String skillVideoLink2 = snapshot.child("SkillVideoLink2").getValue(String.class);
                    String skillVideoLink3 = snapshot.child("SkillVideoLink3").getValue(String.class);

                    String perMatchFees = snapshot.child("ProPerMatchFees").getValue(String.class);
                    String discount = snapshot.child("ProDiscount").getValue(String.class);

                    // Load image using Glide
                    if (!TextUtils.isEmpty(achievementsUrl)) {
                        imageUrl = achievementsUrl; // Set imageUrl
                        Glide.with(ProPlayerUpgradeInfoActivity.this)
                                .load(achievementsUrl)
                                .into(addAchievements);
                    } else {
                        Glide.with(ProPlayerUpgradeInfoActivity.this)
                                .load(R.drawable.add_pic)
                                .into(addAchievements);
                    }

                    sportText.setText(proPlayerSport);

                    perMatchFeesText.setText(perMatchFees);
                    discountText.setText(discount);

                    // Set the selected sport in the spinner

                        int spinnerPosition = ((ArrayAdapter<String>) spinnerSport.getAdapter()).getPosition(proPlayerSport);
                        spinnerSport.setSelection(spinnerPosition);

                    roleText.setText(proPlayerRole);

                    videoLinkText1.setText(skillVideoLink1);
                    videoLinkText2.setText(skillVideoLink2);
                    videoLinkText3.setText(skillVideoLink3);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProPlayerUpgradeInfoActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
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
                addAchievements.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Cropping error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
