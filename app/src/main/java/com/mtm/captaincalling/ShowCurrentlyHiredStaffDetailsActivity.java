package com.mtm.captaincalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class ShowCurrentlyHiredStaffDetailsActivity extends AppCompatActivity {

    ImageView back;

    CircleImageView loadPicture;

    TextView nameText, feesText, noOfMatchesText, stateText, districtText, startingDateText, endDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_currently_hired_staff_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.Muddy_Appbar));
            getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.Muddy_Appbar));
        }

        back = findViewById(R.id.back);

        nameText = findViewById(R.id.name_text);
        feesText = findViewById(R.id.fees_text);
        noOfMatchesText = findViewById(R.id.no_of_matches_text);
        stateText = findViewById(R.id.state_text);
        districtText = findViewById(R.id.district_text);
        startingDateText = findViewById(R.id.starting_date_text);
        endDateText = findViewById(R.id.end_date_text);

        loadPicture = findViewById(R.id.add_pic);

        String hirerPhoneNo = Paper.book().read("IntentHirerPhoneNoDetail");
        String no_of_matches = Paper.book().read("IntentNoOfMatchesDetail");
        String startingDate = Paper.book().read("IntentStartingDateDetail");
        String endDate = Paper.book().read("IntentEndDateDetail");
        String totalFees = Paper.book().read("IntentPriceDetail");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("AllProfiles").child(hirerPhoneNo);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Name").getValue(String.class);
                String picture = snapshot.child("Picture").getValue(String.class);
                String state = snapshot.child("State").getValue(String.class);
                String district = snapshot.child("District").getValue(String.class);

                if (TextUtils.isEmpty(picture)) {
                    Glide.with(ShowCurrentlyHiredStaffDetailsActivity.this).load(R.drawable.user).into(loadPicture);
                }
                else {
                    Glide.with(ShowCurrentlyHiredStaffDetailsActivity.this).load(picture).into(loadPicture);
                }

                nameText.setText(name);
                nameText.setEnabled(false);

                feesText.setText(totalFees);
                feesText.setEnabled(false);

                noOfMatchesText.setText(no_of_matches);
                noOfMatchesText.setEnabled(false);

                startingDateText.setText(startingDate);
                startingDateText.setEnabled(false);

                endDateText.setText(endDate);
                endDateText.setEnabled(false);

                stateText.setText(state);
                stateText.setEnabled(false);

                districtText.setText(district);
                districtText.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = dateFormat.parse(startingDate);
            Date endDateObj = dateFormat.parse(endDate);
            Date today = new Date();

            // Calculate the end date 1 days ago
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            Date oneDaysAgo = calendar.getTime();

            if(endDateObj.before(oneDaysAgo)) {
                ref.child(Paper.book().read("Phone")).child("CurrentlyHiredStaff").child(hirerPhoneNo).removeValue();
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
}