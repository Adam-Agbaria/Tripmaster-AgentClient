package dev.adamag.travelagentfront.Activity.AgentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dev.adamag.travelagentfront.Activity.Adapter.PackagesAdapter;
import dev.adamag.travelagentfront.model.BoundaryObject;
import dev.adamag.travelagentfront.model.User;
import dev.adamag.travelagentfront.network.ObjectService;
import dev.adamag.travelagentfront.network.RetrofitClient;
import dev.adamag.tripmasterfront.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayAgentPackages extends MenuBarActivity {

    private RecyclerView recyclerView;
    private PackagesAdapter packagesAdapter;
    private TextView noPackagesTextView;
    private MaterialButton addPackageButton;
    private String userIdBoundaryJson;
    private User.UserIdBoundary userIdBoundary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_agent_packages);

        recyclerView = findViewById(R.id.recycler_view);
        noPackagesTextView = findViewById(R.id.no_packages_text);
        addPackageButton = findViewById(R.id.add_package_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent incomingIntent = getIntent();
        userIdBoundaryJson = incomingIntent.getStringExtra("userIdBoundary");
        if (userIdBoundaryJson != null) {
            userIdBoundary = new Gson().fromJson(userIdBoundaryJson, User.UserIdBoundary.class);
            Log.d("UserIdBoundary", "userIdBoundary: " + userIdBoundary);
        } else {
            Log.e("UserIdBoundary", "userIdBoundaryJson is null");
        }

        fetchVacationPackages();

        addPackageButton.setOnClickListener(v -> {
            Intent addPackageIntent = new Intent(DisplayAgentPackages.this, AddPackageActivity.class);
            addPackageIntent.putExtra("userIdBoundary", userIdBoundaryJson);
            startActivity(addPackageIntent);
        });
    }

    private void fetchVacationPackages() {
        if (userIdBoundary == null) {
            Toast.makeText(this, "User information is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ObjectService objectService = RetrofitClient.getTripMasterClient().create(ObjectService.class);

        Call<List<BoundaryObject>> call = objectService.searchObjectsByType(
                "VacationPackage",
                userIdBoundary.getSuperapp(),
                userIdBoundary.getEmail(),
                100,
                0
        );

        call.enqueue(new Callback<List<BoundaryObject>>() {
            @Override
            public void onResponse(Call<List<BoundaryObject>> call, Response<List<BoundaryObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BoundaryObject> packages = response.body();

                    // Filter packages to only include those created by the logged-in user
                    List<BoundaryObject> userPackages = new ArrayList<>();
                    for (BoundaryObject packageObj : packages) {
                        if (packageObj.getCreatedBy().getUserId().getEmail().equals(userIdBoundary.getEmail()) && packageObj.isActive()) {
                            userPackages.add(packageObj);
                        }
                    }

                    if (userPackages.isEmpty()) {
                        noPackagesTextView.setVisibility(View.VISIBLE);
                        addPackageButton.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noPackagesTextView.setVisibility(View.GONE);
                        addPackageButton.setVisibility(View.GONE);
                        packagesAdapter = new PackagesAdapter(userPackages, DisplayAgentPackages.this); // Pass context to adapter
                        recyclerView.setAdapter(packagesAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(DisplayAgentPackages.this, "Failed to fetch packages", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<BoundaryObject>> call, Throwable t) {
                Toast.makeText(DisplayAgentPackages.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
