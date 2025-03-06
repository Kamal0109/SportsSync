package com.mtm.captaincalling;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HireStaffRequestDetailsActivity extends AppCompatActivity {

    ImageView back;

    private ImageButton selectStartingDate,  selectEndDate;

    CircleImageView playerPic;

    EditText no_of_matches, address, totalFees, info, additional_info;

    TextView proName, startingDateText, endDateText;

    Button saveHireDetails;

    private String tournamentStartingDate, tournamentEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_staff_request_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        back = findViewById(R.id.back_create_tournament);
        playerPic = findViewById(R.id.add_pic);

        no_of_matches = findViewById(R.id.no_of_matches);
        address = findViewById(R.id.tournament_address);
        totalFees = findViewById(R.id.total_fees);
        info = findViewById(R.id.tournament_info);
        additional_info = findViewById(R.id.additional_info);


        proName = findViewById(R.id.pro_name);

        selectStartingDate = findViewById(R.id.buttonSelectStartingDate);
        startingDateText = findViewById(R.id.textViewSelectedStartingDate);
        selectEndDate = findViewById(R.id.buttonSelectEndDate);
        endDateText = findViewById(R.id.textViewSelectedEndDate);

        saveHireDetails = findViewById(R.id.saveHireDetails);

        totalFees.setEnabled(false);

        int fees = Integer.parseInt(Paper.book().read("ProStaffFees").toString());
        float dis = (Float.parseFloat(Paper.book().read("ProStaffDiscount").toString())) / 100;

        // Set up TextWatcher for no_of_matches
        no_of_matches.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(no_of_matches.getText().toString())) {
                    int numberOfMatches = Integer.parseInt(no_of_matches.getText().toString());

                    int totalFee;

                    if(numberOfMatches == 1) {
                        totalFee = fees;
                    }
                    else {
                        totalFee = (int) (numberOfMatches * fees * (1 - dis));
                    }

                    totalFees.setText(String.valueOf(totalFee));
                } else {
                    totalFees.setText("");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String picture = Paper.book().read("ProProfilePic").toString();

        if (!TextUtils.isEmpty(picture)) {
            Glide.with(getApplication()).load(picture).into(playerPic);
        } else {
            Glide.with(getApplication()).load(R.drawable.user).into(playerPic);
        }

        proName.setText(Paper.book().read("ProStaffName").toString());



        selectStartingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(true);
            }

            private void showDatePicker(final boolean isStartingDate) {
                final Calendar c = Calendar.getInstance();

                // Get the current date
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HireStaffRequestDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                                if (isStartingDate) {
                                    // Update the starting date text view
                                    tournamentStartingDate = selectedDate;
                                    startingDateText.setText(tournamentStartingDate);
                                } else {
                                    // Check if the selected ending date is after the starting date
                                    if (isDateAfterStartingDate(year, monthOfYear, dayOfMonth)) {
                                        // Update the ending date text view
                                        tournamentEndDate = selectedDate;
                                        endDateText.setText(tournamentEndDate);
                                    } else {
                                        // Display an error message
                                        Toast.makeText(HireStaffRequestDetailsActivity.this, "Ending date must be after starting date", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            private boolean isDateAfterStartingDate(int year, int month, int day) {
                                Calendar startingDateCalendar = Calendar.getInstance();
                                Calendar selectedDateCalendar = Calendar.getInstance();

                                // Set the starting date calendar
                                String[] startingDateParts = tournamentStartingDate.split("/");
                                startingDateCalendar.set(Integer.parseInt(startingDateParts[2]), Integer.parseInt(startingDateParts[1]) - 1, Integer.parseInt(startingDateParts[0]));

                                // Set the selected date calendar
                                selectedDateCalendar.set(year, month, day);

                                // Check if the selected date is after the starting date
                                return selectedDateCalendar.after(startingDateCalendar);
                            }
                        },
                        year, month, day);

                // Show the date picker dialog
                datePickerDialog.show();
            }


        });

        selectEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(false);
            }

            private void showDatePicker(final boolean isStartingDate) {
                final Calendar c = Calendar.getInstance();

                // Get the current date
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        HireStaffRequestDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                                if (isStartingDate) {
                                    // Update the starting date text view
                                    tournamentStartingDate = selectedDate;
                                    startingDateText.setText(tournamentStartingDate);
                                } else {
                                    // Check if the selected ending date is after the starting date
                                    if (isDateAfterStartingDate(year, monthOfYear, dayOfMonth)) {
                                        // Update the ending date text view
                                        tournamentEndDate = selectedDate;
                                        endDateText.setText(tournamentEndDate);
                                    } else {
                                        // Display an error message
                                        Toast.makeText(HireStaffRequestDetailsActivity.this, "Ending date must be after starting date", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            private boolean isDateAfterStartingDate(int year, int month, int day) {
                                Calendar startingDateCalendar = Calendar.getInstance();
                                Calendar selectedDateCalendar = Calendar.getInstance();

                                // Set the starting date calendar
                                String[] startingDateParts = tournamentStartingDate.split("/");
                                startingDateCalendar.set(Integer.parseInt(startingDateParts[2]), Integer.parseInt(startingDateParts[1]) - 1, Integer.parseInt(startingDateParts[0]));

                                // Set the selected date calendar
                                selectedDateCalendar.set(year, month, day);

                                // Check if the selected date is after or equal to the starting date
                                return !selectedDateCalendar.before(startingDateCalendar);
                            }

                        },
                        year, month, day);

                // Show the date picker dialog
                datePickerDialog.show();
            }
        });

        saveHireDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(no_of_matches.getText().toString())) {
                    no_of_matches.setError("Required..");
                    no_of_matches.requestFocus();
                } else if (TextUtils.isEmpty(address.getText().toString())) {
                    address.setError("Required..");
                    address.requestFocus();
                } else if (TextUtils.isEmpty(totalFees.getText().toString())) {
                    totalFees.setError("Required..");
                    totalFees.requestFocus();
                } else if (TextUtils.isEmpty(info.getText().toString())) {
                    info.setError("Required..");
                    info.requestFocus();
                } else if(TextUtils.isEmpty(tournamentStartingDate)){
                    startingDateText.setText("required date...");
                    startingDateText.setTextColor(0xff0000);
                } else if(TextUtils.isEmpty(tournamentEndDate)){
                    endDateText.setText("required date...");
                    endDateText.setTextColor(0xff0000);
                } else {
                    saveHireDetails();
                }
            }
        });
    }

    private void saveHireDetails() {

        String proStaffPhone = Paper.book().read("ProStaffPhone");


        final HashMap<String, Object> HashMap = new HashMap<>();

        HashMap.put("HirerPhoneNo", Paper.book().read("Phone").toString());
        HashMap.put("No_Of_Matches", no_of_matches.getText().toString());
        HashMap.put("TourAddress", address.getText().toString());
        HashMap.put("TotalFees", totalFees.getText().toString());
        HashMap.put("TourInfo", info.getText().toString());
        HashMap.put("StartingDate", startingDateText.getText().toString());
        HashMap.put("EndDate", endDateText.getText().toString());
        HashMap.put("AdditionalInfo", additional_info.getText().toString());

        HashMap.put("isRequestAccepted", "0");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("AllStaffRequestProfiles").child(proStaffPhone).child("StaffRequestDetails");

        ref.child("HireProRequests").child(Paper.book().read("Phone").toString()).setValue(HashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(HireStaffRequestDetailsActivity.this, "Request Submitted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HireStaffRequestDetailsActivity.this, "Request Submission Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}