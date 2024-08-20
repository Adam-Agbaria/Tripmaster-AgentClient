package dev.adamag.travelagentfront.Activity.AgentActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dev.adamag.travelagentfront.model.BoundaryObject;
import dev.adamag.travelagentfront.model.User;
import dev.adamag.travelagentfront.model.VacationPackage;
import dev.adamag.travelagentfront.network.ObjectServiceImpl;
import dev.adamag.tripmasterfront.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPackageActivity extends MenuBarActivity {

    private EditText packageNameEditText;
    private MaterialAutoCompleteTextView destinationEditText;
    private EditText priceEditText;
    private EditText hotelNameEditText;
    private EditText starsEditText;
    private Spinner isConnectionFlightSpinner;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private MaterialButton publishButton;
    private User.UserIdBoundary userIdBoundary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);

        packageNameEditText = findViewById(R.id.package_name);
        destinationEditText = findViewById(R.id.destination);
        priceEditText = findViewById(R.id.price);
        hotelNameEditText = findViewById(R.id.hotel_name);
        starsEditText = findViewById(R.id.stars);
        isConnectionFlightSpinner = findViewById(R.id.is_connection_flight);
        startDateEditText = findViewById(R.id.start_date);
        endDateEditText = findViewById(R.id.end_date);
        publishButton = findViewById(R.id.publish_button);

        String userIdBoundaryJson = getIntent().getStringExtra("userIdBoundary");
        if (userIdBoundaryJson != null) {
            userIdBoundary = new Gson().fromJson(userIdBoundaryJson, User.UserIdBoundary.class);
        } else {
            Log.e("AddPackageActivity", "userIdBoundaryJson is null");
        }

        // Set up the destination AutoCompleteTextView
        ArrayAdapter<CharSequence> destinationAdapter = ArrayAdapter.createFromResource(this,
                R.array.destinations_array, android.R.layout.simple_dropdown_item_1line);
        destinationEditText.setAdapter(destinationAdapter);

        // Set up the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.connection_flight_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isConnectionFlightSpinner.setAdapter(adapter);

        startDateEditText.setOnClickListener(v -> showDatePickerDialog(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePickerDialog(endDateEditText));

        publishButton.setOnClickListener(v -> handlePublish());

        // Setup bottom navigation bar
        setupBottomNavigationBar();
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddPackageActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    editText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void handlePublish() {
        String packageName = packageNameEditText.getText().toString();
        String destination = destinationEditText.getText().toString();
        String priceStr = priceEditText.getText().toString();
        String hotelName = hotelNameEditText.getText().toString();
        String starsStr = starsEditText.getText().toString();
        String connectionFlightStr = isConnectionFlightSpinner.getSelectedItem().toString();
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();

        if (packageName.isEmpty() || destination.isEmpty() || priceStr.isEmpty() || hotelName.isEmpty() || starsStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int stars;
        try {
            stars = Integer.parseInt(starsStr);
            if (stars < 0 || stars > 5) {
                Toast.makeText(this, "Stars must be between 0 and 5", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Stars must be a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isConnectionFlight = connectionFlightStr.equalsIgnoreCase("Yes");

        // Validate dates
        if (!isValidDateRange(startDate, endDate)) {
            Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
            return;
        }

        VacationPackage vacationPackage = new VacationPackage(
                "1", // Placeholder ID, you might want to generate a unique ID
                packageName,
                destination,
                hotelName,
                stars,
                isConnectionFlight,
                price,
                startDate,
                endDate
        );

        BoundaryObject boundaryObject = vacationPackage.toBoundaryObject();
        boundaryObject.getCreatedBy().getUserId().setEmail(userIdBoundary.getEmail());
        boundaryObject.getCreatedBy().getUserId().setSuperapp(userIdBoundary.getSuperapp());

        ObjectServiceImpl serviceUtil = new ObjectServiceImpl();
        serviceUtil.createObject(boundaryObject, new Callback<BoundaryObject>() {
            @Override
            public void onResponse(Call<BoundaryObject> call, Response<BoundaryObject> response) {
                if (response.isSuccessful()) {
                    Log.d("PackageCreated", "Object created: " + response.body());
                    Toast.makeText(AddPackageActivity.this, "Package published successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("PackageCreateError", "Create object failed: " + response.toString());
                    Toast.makeText(AddPackageActivity.this, "Failed to publish package", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BoundaryObject> call, Throwable t) {
                Log.e("PackageCreateError", "Create object error: " + t.getMessage());
                Toast.makeText(AddPackageActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidDateRange(String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            return !end.before(start);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
