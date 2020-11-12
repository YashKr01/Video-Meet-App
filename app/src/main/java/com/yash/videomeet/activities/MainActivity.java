package com.yash.videomeet.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.yash.videomeet.R;
import com.yash.videomeet.adapters.UsersAdapter;
import com.yash.videomeet.listeners.UsersListener;
import com.yash.videomeet.models.User;
import com.yash.videomeet.utilities.Constants;
import com.yash.videomeet.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UsersListener {

    private PreferenceManager preferenceManager;
    private List<User> users;
    private UsersAdapter usersAdapter;
    private TextView textErrorMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageConference;

    private int REQUEST_CODE_BATTERY_OPTIMISATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplicationContext());

        imageConference = findViewById(R.id.imageConference);

        // SIGN OUT USER
        findViewById(R.id.textSignOut).setOnClickListener(view ->
                signOut());

        // SET NAME AS TITLE
        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)));

        // GET TOKEN OF USER
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        sendFCMTokenToDatabase(task.getResult().getToken());
                    }
                });

        // RECYCLER VIEW
        RecyclerView userRecyclerView = findViewById(R.id.usersRecyclerView);
        textErrorMessage = findViewById(R.id.textErrorMessage);

        // LIST OF USERS
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users, this);
        userRecyclerView.setAdapter(usersAdapter);

        // SWIPE REFRESH LAYOUT
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getUsers);

        // GET LIST OF USERS
        getUsers();
        checkForBatteryOptimisation();

    }

    private void getUsers() {
        swipeRefreshLayout.setRefreshing(true);

        // FIRE STORE QUERY
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    // SWIPE REFRESH RUNNING
                    swipeRefreshLayout.setRefreshing(false);

                    // GET UID OF CURRENT USER
                    String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {

                        users.clear();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (myUserId.equals(documentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                            user.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                            user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                            user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            users.add(user);

                        }
                        if (users.size() > 0) {
                            usersAdapter.notifyDataSetChanged();
                        } else {
                            textErrorMessage.setText(String.format("%s", "No Users Available"));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }

                    } else {
                        textErrorMessage.setText(String.format("%s", "No Users Available"));
                        textErrorMessage.setVisibility(View.VISIBLE);
                    }

                });

    }

    private void sendFCMTokenToDatabase(String token) {

        // FIRE STORE
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));

        // UPDATE TOKEN VALUE
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this,
                                "Unable to send Token : " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    // SIGN OUT USER
    private void signOut() {

        Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show();

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // FIRE STORE QUERY
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID));

        // DELETE TOKEN FROM USER DATA
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());

        documentReference.update(updates)
                .addOnSuccessListener(aVoid -> {

                    // CLEAR TOKEN FROM SHARED PREFERENCES
                    preferenceManager.clearPreferences();
                    // GO BACK TO SIGN IN ACTIVITY
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Unable to Sign Out",
                                Toast.LENGTH_SHORT).show());

    }


    @Override
    public void initiateVideoMeeting(User user) {

        // USER IS LOGGED OUT CURRENTLY
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(this, user.firstName + " "
                    + user.lastName + " is not available for meeting", Toast.LENGTH_LONG).show();
        } else {
            // START VIDEO MEETING
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "video");
            startActivity(intent);

        }
    }

    @Override
    public void initiateAudioMeeting(User user) {

        // USER IS CURRENTLY LOGGED OUT
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(this, user.firstName + " "
                    + user.lastName + " is not available for meeting", Toast.LENGTH_LONG).show();
        } else {
            // START AUDIO MEETING
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }
    }

    @Override
    public void onMultipleUserAction(Boolean isMultipleUsersSelected) {
        if (isMultipleUsersSelected) {
            imageConference.setVisibility(View.VISIBLE);
            imageConference.setOnClickListener(view -> {

                Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                intent.putExtra("selectedUsers", new Gson().toJson(usersAdapter.getSelectedUsers()));
                intent.putExtra("type", "video");
                intent.putExtra("isMultiple", true);
                startActivity(intent);

            });
        } else {
            imageConference.setVisibility(View.GONE);
        }
    }

    private void checkForBatteryOptimisation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (!powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Warning");
                builder.setMessage("Battery optimization is enabled. It can interrupt running background services.");

                builder.setPositiveButton("Disable", (dialogInterface, i) -> {

                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMISATION);
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i)
                        -> dialogInterface.dismiss());

                builder.create().show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BATTERY_OPTIMISATION) {
            checkForBatteryOptimisation();
        }
    }
}