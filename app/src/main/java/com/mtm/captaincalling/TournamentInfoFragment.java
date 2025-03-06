package com.mtm.captaincalling;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class TournamentInfoFragment extends Fragment {
    String name, date, state, district, teams, address, startingDate, endDate;

    private  String tournamentStartingDate = "", tournamentEndDate = "";
    TextView editTournamentName, editTournamentEndDate, editTournamentStartingDate, editTournamentState, editTournamentDistrict, editTournamentAddress, editTournamentTeams, editTournamentDetails;
    ImageView editTournamentBanner, buttonSelectStartingDate, buttonSelectEndDate;
    Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tournament_info, container, false);

        Paper.init(getActivity().getApplicationContext());

        editTournamentName = view.findViewById(R.id.edit_tournament_name);
        editTournamentStartingDate = view.findViewById(R.id.edit_tournament_starting_date);
        editTournamentEndDate = view.findViewById(R.id.edit_tournament_end_date);
        editTournamentState = view.findViewById(R.id.edit_tournament_state);
        editTournamentDistrict = view.findViewById(R.id.edit_tournament_district);
        editTournamentAddress = view.findViewById(R.id.edit_tournament_address);
        editTournamentTeams = view.findViewById(R.id.edit_tournament_teams);
        editTournamentBanner = view.findViewById(R.id.edit_tournament_banner);
        editTournamentDetails = view.findViewById(R.id.edit_tournament_details);
        submitButton = view.findViewById(R.id.update_tournament_details);

        buttonSelectStartingDate = view.findViewById(R.id.buttonSelectStartingDate);
        buttonSelectEndDate = view.findViewById(R.id.buttonSelectEndDate);

        if (getActivity().getIntent() != null) {
            editTournamentName.setText(getActivity().getIntent().getStringExtra("tournament_name"));
            editTournamentStartingDate.setText(getActivity().getIntent().getStringExtra("tournament_starting_date"));
            editTournamentEndDate.setText(getActivity().getIntent().getStringExtra("tournament_end_date"));
            editTournamentState.setText(getActivity().getIntent().getStringExtra("tournament_state"));
            editTournamentDistrict.setText(getActivity().getIntent().getStringExtra("tournament_district"));
            editTournamentAddress.setText(getActivity().getIntent().getStringExtra("tournament_address"));
            editTournamentTeams.setText(getActivity().getIntent().getStringExtra("tournament_teams"));
            Glide.with(getActivity().getApplicationContext()).load(getActivity().getIntent().getStringExtra("tournament_picture")).into(editTournamentBanner);
        }

        buttonSelectStartingDate.setOnClickListener(new View.OnClickListener() {
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
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                                if (isStartingDate) {
                                    // Update the starting date text view
                                    tournamentStartingDate = selectedDate;
                                    editTournamentStartingDate.setText(tournamentStartingDate);
                                } else {
                                    // Check if the selected ending date is after the starting date
                                    if (!TextUtils.isEmpty(tournamentStartingDate) && isDateAfterStartingDate(year, monthOfYear, dayOfMonth)) {
                                        // Update the ending date text view
                                        tournamentEndDate = selectedDate;
                                        editTournamentEndDate.setText(tournamentEndDate);
                                    } else {
                                        // Display an error message
                                        Toast.makeText(getActivity(), "Starting date must be set first", Toast.LENGTH_SHORT).show();
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

        buttonSelectEndDate.setOnClickListener(new View.OnClickListener() {
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
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                                if (isStartingDate) {
                                    // Update the starting date text view
                                    tournamentStartingDate = selectedDate;
                                    editTournamentStartingDate.setText(tournamentStartingDate);
                                } else {
                                    // Check if the selected ending date is after the starting date
                                    if (!TextUtils.isEmpty(tournamentStartingDate) && isDateAfterStartingDate(year, monthOfYear, dayOfMonth)) {
                                        // Update the ending date text view
                                        tournamentEndDate = selectedDate;
                                        editTournamentEndDate.setText(tournamentEndDate);
                                    } else {
                                        // Display an error message
                                        Toast.makeText(getActivity(), "Starting date must be set first", Toast.LENGTH_SHORT).show();
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

        editTournamentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTournamentDetails.getText().equals("Edit")) {
                    editTournamentDetails.setText("Cancel");
                    editTournamentName.setEnabled(true);
                    editTournamentState.setEnabled(true);
                    editTournamentDistrict.setEnabled(true);
                    editTournamentAddress.setEnabled(true);
                    buttonSelectStartingDate.setEnabled(true);
                    editTournamentStartingDate.setEnabled(false);
                    buttonSelectEndDate.setEnabled(true);
                    editTournamentEndDate.setEnabled(false);
                    editTournamentTeams.setEnabled(true);
                } else {
                    editTournamentDetails.setText("Edit");
                    editTournamentName.setEnabled(false);
                    editTournamentState.setEnabled(false);
                    editTournamentDistrict.setEnabled(false);
                    editTournamentAddress.setEnabled(false);
                    editTournamentStartingDate.setEnabled(false);
                    editTournamentEndDate.setEnabled(false);
                    buttonSelectStartingDate.setEnabled(false);
                    buttonSelectEndDate.setEnabled(false);
                    editTournamentTeams.setEnabled(false);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnection()) {
                    updateTournament();
                } else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    private boolean checkBlankSpaces() {
        name = editTournamentName.getText().toString();
        state = editTournamentState.getText().toString();
        district = editTournamentDistrict.getText().toString();
        address = editTournamentAddress.getText().toString();
        teams = editTournamentTeams.getText().toString();
        startingDate = editTournamentStartingDate.getText().toString();
        endDate = editTournamentEndDate.getText().toString();

        if (TextUtils.isEmpty(name)) {
            editTournamentName.setError("Required...");
            editTournamentName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(startingDate)) {
            editTournamentStartingDate.setError("Required...");
            editTournamentStartingDate.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(endDate)) {
            editTournamentEndDate.setError("Required...");
            editTournamentEndDate.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(state)) {
            editTournamentState.setError("Required...");
            editTournamentState.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(district)) {
            editTournamentDistrict.setError("Required...");
            editTournamentDistrict.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(address)) {
            editTournamentAddress.setError("Required...");
            editTournamentAddress.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(teams)) {
            editTournamentTeams.setError("Required...");
            editTournamentTeams.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void updateTournament() {
        if (checkBlankSpaces()) {
            DatabaseReference updateTournamentRef;
            updateTournamentRef = FirebaseDatabase.getInstance().getReference().child("tournaments");

            final HashMap<String, Object> updateTournamentHashmap = new HashMap<>();

            updateTournamentHashmap.put("TournamentName", name);
            updateTournamentHashmap.put("TournamentState", state);
            updateTournamentHashmap.put("TournamentDistrict", district);
            updateTournamentHashmap.put("TournamentAddress", address);
            updateTournamentHashmap.put("TournamentTeams", teams);
            updateTournamentHashmap.put("TournamentStartingDate", startingDate);
            updateTournamentHashmap.put("TournamentEndDate", endDate);

            String updateTournamentKey = Paper.book().read("TournamentKey");
            if (updateTournamentKey == null) {
                Toast.makeText(getActivity(), "Failed to update tournament: Key not found", Toast.LENGTH_SHORT).show();
                return;
            }

            updateTournamentRef.child(updateTournamentKey).updateChildren(updateTournamentHashmap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Tournament Successfully Updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
