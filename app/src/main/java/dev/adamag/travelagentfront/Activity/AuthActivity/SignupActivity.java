package dev.adamag.travelagentfront.Activity.AuthActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import dev.adamag.travelagentfront.Activity.AgentActivity.DisplayAgentPackages;
import dev.adamag.travelagentfront.model.BoundaryCommand;
import dev.adamag.travelagentfront.model.BoundaryObject;
import dev.adamag.travelagentfront.model.Password;
import dev.adamag.travelagentfront.model.User;
import dev.adamag.travelagentfront.model.UserInput;
import dev.adamag.travelagentfront.model.UserRole;
import dev.adamag.travelagentfront.network.CommandServiceImpl;
import dev.adamag.travelagentfront.network.ObjectService;
import dev.adamag.travelagentfront.network.RetrofitClient;
import dev.adamag.travelagentfront.network.UserService;
import dev.adamag.tripmasterfront.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, passwordConfirmEditText;
    private MaterialButton signupButton;
    private ProgressBar progressBar;
    private Gson gson = new Gson();


    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameEditText = findViewById(R.id.SGP_userName_EDT);
        emailEditText = findViewById(R.id.SGP_email_EDT);
        passwordEditText = findViewById(R.id.SGP_password_EDT);
        passwordConfirmEditText = findViewById(R.id.SGP_passwordCon_EDT);
        signupButton = findViewById(R.id.SGP_button_BTN);
        progressBar = findViewById(R.id.progressBar);



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String passwordConfirm = passwordConfirmEditText.getText().toString();
                String role = UserRole.SUPERAPP_USER.toString();

                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else if (!isEmailValid(email)) {
                    Toast.makeText(SignupActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                } else {
                    String passwordError = getPasswordError(password);
                    if (passwordError != null) {
                        Toast.makeText(SignupActivity.this, passwordError, Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        register(username, email, password, role);
                    }
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String getPasswordError(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        Pattern lowerCasePattern = Pattern.compile("[a-z]");
        Pattern digitPattern = Pattern.compile("[0-9]");
        Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");

        if (!upperCasePattern.matcher(password).find()) {
            return "Password must contain at least one uppercase letter";
        }
        if (!lowerCasePattern.matcher(password).find()) {
            return "Password must contain at least one lowercase letter";
        }
        if (!digitPattern.matcher(password).find()) {
            return "Password must contain at least one digit";
        }
        if (!specialCharPattern.matcher(password).find()) {
            return "Password must contain at least one special character";
        }
        return null;
    }

    private void logUserDetails(User user) {
        Log.d(TAG, "User Details:");
        Log.d(TAG, "Email: " + user.getUserId().getEmail());
        Log.d(TAG, "Username: " + user.getUsername());
        Log.d(TAG, "Role: " + user.getRole().name());
    }

    private void register(String username, String email, String password, String role) {
        UserService userService = RetrofitClient.getTripMasterClient().create(UserService.class);
        ObjectService objectService = RetrofitClient.getTripMasterClient().create(ObjectService.class);

        UserInput newUser = new UserInput();
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setAvatar("B");
        newUser.setRole(UserRole.SUPERAPP_USER);




        Call<User> userCall = userService.createUser(newUser);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User createdUser = response.body();
                    logUserDetails(createdUser);

                    // After creating the user, create the password object
                    Password passwordObject = new Password(password);
                    BoundaryObject passwordBoundaryObject = passwordObject.toBoundaryObject();

                    // Set the createdBy boundary with the created user's ID
                    BoundaryObject.CreatedByBoundary createdByBoundary = new BoundaryObject.CreatedByBoundary();
                    BoundaryObject.CreatedByBoundary.UserIdBoundary userId = new BoundaryObject.CreatedByBoundary.UserIdBoundary(createdUser.getUserId().getSuperapp(), createdUser.getUserId().getEmail());
                    createdByBoundary.setUserId(userId);
                    passwordBoundaryObject.setCreatedBy(createdByBoundary);

                    // Delay the password creation by 4 second
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Call<BoundaryObject> passwordCall = objectService.createObject(passwordBoundaryObject);
                            passwordCall.enqueue(new Callback<BoundaryObject>() {
                                @Override
                                public void onResponse(Call<BoundaryObject> call, Response<BoundaryObject> response) {
                                    progressBar.setVisibility(View.GONE);
                                    if (response.isSuccessful()) {
//                                        saveRegisterCommand(createdUser);
                                        Toast.makeText(SignupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        Intent intent;
                                        intent = new Intent(SignupActivity.this, DisplayAgentPackages.class);

                                        String userIdBoundaryJson = gson.toJson(createdUser.getUserId());
                                        intent.putExtra("userIdBoundary", userIdBoundaryJson);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Password creation failed", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<BoundaryObject> call, Throwable t) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignupActivity.this, "Network error during password creation", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }, 4000); // 4-second delay
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, "User registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignupActivity.this, "Network error during user registration", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
