package edu.northeastern.group40.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import edu.northeastern.group40.R;

public class SearchActivity extends AppCompatActivity {

    Button showStartDatePickerButton;
    TextView selectedStartDateTextView;
    Button showEndDatePickerButton;
    TextView selectedEndDateTextView;
    Calendar startCalendar, endCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Initialize views and calendars
        showStartDatePickerButton = findViewById(R.id.show_start_date_picker_button);
        selectedStartDateTextView = findViewById(R.id.selected_start_date_text_view);
        startCalendar = Calendar.getInstance();

        showEndDatePickerButton = findViewById(R.id.show_end_date_picker_button);
        selectedEndDateTextView = findViewById(R.id.selected_end_date_text_view);
        endCalendar = Calendar.getInstance();

        // Set listeners for date pickers
        showStartDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startCalendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, monthOfYear);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateSelectedStartDate();
                    }
                });
            }
        });

        showEndDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endCalendar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, monthOfYear);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (endCalendar.before(startCalendar)) {
                            Toast.makeText(SearchActivity.this, "End date cannot be earlier than start date", Toast.LENGTH_SHORT).show();
                            endCalendar.setTimeInMillis(startCalendar.getTimeInMillis());
                            updateSelectedEndDate();
                        } else {
                            updateSelectedEndDate();
                        }
                    }
                });
            }
        });
    }

    private void showDatePickerDialog(Calendar calendar, DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateSelectedStartDate() {
        selectedStartDateTextView.setText("Start date: " + String.format("%02d/%02d/%d",
                startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH),
                startCalendar.get(Calendar.YEAR)));
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateSelectedEndDate() {
        selectedEndDateTextView.setText("End date: " + String.format("%02d/%02d/%d",
                endCalendar.get(Calendar.MONTH) + 1, endCalendar.get(Calendar.DAY_OF_MONTH),
                endCalendar.get(Calendar.YEAR)));
    }
}