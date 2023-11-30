package com.example.infits;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.infits.customDialog.StepCounterViewModel;
import com.example.infits.customDialog.StepTrackerService;
import com.tenclouds.gaugeseekbar.GaugeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StepTrackerFragment extends Fragment  {
    private static final String MY_PREFERENCE_NAME = "WORKERONE";
    Float goalPercent2;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
    Handler handler = new Handler();
    private StepCounterViewModel viewModel;
    Thread mythread;
    Button setgoal;
    ImageButton imgback;
    WorkRequest workRequest;
    TextView steps_label, goal_step_count, distance, calories, speed, Distance_unit;
    ImageView reminder;
    private static final int PERMISSION_REQUEST_BODY_SENSORS = 1;
    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1000;
    private static final int PERMISSION_REQUEST_ALL_SENSORS = 100;
    //
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private TextView stepCountTextView;
    private int GoalSteps;
    private int stepPercent1;
    private  StringRequest stringRequest;
    private int currentsteps;
  //  private GoalReachedReceiver goalReachedReceiver;
    //
    private int steps = 0;
    private int previousStepCount = 0;
    public final String NOTIFICATION_CHANNEL_ID = "MyNotificationChannel";
    private SharedPreferences notificationsharedPreferences;

    private static final int SENSOR_PERMISSION_REQUEST = 1;
    SharedPreferences stepPrefs;

    GaugeSeekBar progressBar;
    int stepCount;

    static float goalVal;
    // private int currentsteps;
    SharedPreferences currentPreferences;

    float goalPercent = 0;

    UpdateStepCard updateStepCard;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public StepTrackerFragment() {

    }




    public static StepTrackerFragment newInstance(String param1, String param2) {
        StepTrackerFragment fragment = new StepTrackerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (getArguments() != null && getArguments().getBoolean("notification") /* coming from notification */) {
                    startActivity(new Intent(getActivity(), DashBoardMain.class));
                    requireActivity().finish();
                } else {
                    Navigation.findNavController(requireActivity(), R.id.imgback).navigate(R.id.action_stepTrackerFragment_to_dashBoardFragment);
                    FragmentManager manager = requireActivity().getSupportFragmentManager();
                    manager.popBackStack();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // resetSteps();

        loadData();
//        sensorManager = (SensorManager) requireContext().getSystemService(requireContext().SENSOR_SERVICE);
//        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        viewModel = new ViewModelProvider(requireActivity()).get(StepCounterViewModel.class);

    }
    private BroadcastReceiver stepCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("step-count-update".equals(intent.getAction())) {
                stepCount = intent.getIntExtra("stepCount", 0);
                distance.setText(String.format(String.format("%.3f",(float)stepCount*0.0005)));
                // Update UI with the new step count
                stepCountTextView.setText(String.valueOf(stepCount));
                calories.setText(String.valueOf((float) 0.05*stepCount));
                float stepPercent = GoalSteps == 0 ? 0 : (int) ((stepCount * 100) / GoalSteps);
                double stepPercent1 = Math.min(100, Math.max(0, stepPercent));
                progressBar.setProgress((float) stepPercent1 / 100);
            }
        }
    };
    private BroadcastReceiver averageSpeedAndDistanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("averageSpeed-and-distance-update".equals(intent.getAction())) {
              String  averageSpeed = intent.getStringExtra("averageSpeed");
               // float speedKmH = averageSpeed * 3.6f;
              float  distanceVal = intent.getFloatExtra("distance", 0f);
                float distanceKm = distanceVal / 1000.0f;
                // Update UI with the new step count
              speed.setText(String.format(averageSpeed.substring(0, 1)));

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_tracker, container, false);
        stepCountTextView = view.findViewById(R.id.steps_label);
        setgoal = view.findViewById(R.id.setgoal);
        imgback = view.findViewById(R.id.imgback);
        goal_step_count = view.findViewById(R.id.goal_step_count);
        RecyclerView pastActivity = view.findViewById(R.id.past_activity);
        progressBar = view.findViewById(R.id.progressBar);
        speed = view.findViewById(R.id.speed);
        distance = view.findViewById(R.id.distance);
        calories = view.findViewById(R.id.calories);
        reminder = view.findViewById(R.id.reminder);
        Distance_unit = view.findViewById(R.id.distance_unit);

        getPastActivity(pastActivity);


        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("GOALVALUE", MODE_PRIVATE);
        GoalSteps = sharedPreferences1.getInt("goalValue", 0);
        goal_step_count.setText(String.valueOf(sharedPreferences1.getInt("goalValue", 0)));
        //goalReachedReceiver = new GoalReachedReceiver();
        IntentFilter filter = new IntentFilter("GOAL_REACHED");
       // requireActivity().registerReceiver(goalReachedReceiver, filter);
        resetSteps();
        notificationsharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        setgoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchTrackerInfos.flag_steps = 0;
                final Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.setgoaldialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                EditText goal = dialog.findViewById(R.id.goal);
                Button save = dialog.findViewById(R.id.save_btn_steps);
                dialog.show();
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int goalValue = Integer.parseInt(goal.getText().toString());
                        resetSteps();
                        goal_step_count.setText(String.valueOf(goalValue));
                        GoalSteps = goalValue;
                        previousStepCount = steps;
                        stepCountTextView.setText("0");
                        progressBar.setProgress(0);
                        saveData();
                        SharedPreferences sharedPreferences4 = requireContext().getSharedPreferences("PREFDATA",MODE_PRIVATE);
                        SharedPreferences.Editor editor4 = sharedPreferences4.edit();
                        editor4.putBoolean("PREF",false);
                        editor4.apply();

                        Intent intent = new Intent(requireContext(), StepTrackerService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            requireContext().startForegroundService(intent);
                        }
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GOALVALUE", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("goalValue", goalValue);
                        editor.apply();

                        saveGoal(sharedPreferences4.getInt("goalValue",0));
                        //notification
                        SharedPreferences.Editor editor2 = notificationsharedPreferences.edit();
                        editor2.putBoolean("notification_sent", false);
                        editor2.apply();
                      //  goalupdate();
                        dialog.dismiss();
                    }
                });


            }
        });
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArguments() != null && getArguments().getBoolean("notification") /* coming from notification */) {
                    startActivity(new Intent(getActivity(), DashBoardMain.class));
                    requireActivity().finish();
                } else {
                    Navigation.findNavController(v).navigate(R.id.action_stepTrackerFragment_to_dashBoardFragment);
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.popBackStack();
                }
            }
        });

        return view;
    }
    private void getPastActivity(RecyclerView pastActivity) {
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> datas = new ArrayList<>();
        //String url = "https://infits.in/androidApi/pastActivity.php";
       String url= "http://192.168.27.94/phpProjects/pastActivity.php";
        stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                Log.d("dattaaaa:", response.toString());
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("steps");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String data = object.getString("steps");
                    String date = object.getString("date");
                    dates.add(date);
                    datas.add(data);
                    System.out.println(datas.get(i));
                    System.out.println(dates.get(i));
                }
                AdapterForPastActivity ad = new AdapterForPastActivity(getContext(), dates, datas, Color.parseColor("#FF9872"));
                pastActivity.setLayoutManager(new LinearLayoutManager(getContext()));
                pastActivity.setAdapter(ad);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            Log.d("Error", error.toString());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("clientID", DataFromDatabase.clientuserID);
                return data;
            }
        };

        Volley.newRequestQueue(getActivity()).add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void saveGoal(int goalValue) {

    }

    @Override
    public void onStart() {
        super.onStart();

    }
    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(stepCountReceiver);
        super.onStop();
    }

    @SuppressLint("Range")
    @Override
    public void onResume() {
        super.onResume();

        currentPreferences = getActivity().getSharedPreferences("CURRENTSTEP", MODE_PRIVATE);
        int currentStepsVal = currentPreferences.getInt("key2", 0);
        stepCountTextView.setText(String.valueOf(currentStepsVal));
                    float stepPercent = GoalSteps == 0 ? 0 : (int) ((currentStepsVal * 100) / GoalSteps);
            double stepPercent1 = Math.min(100, Math.max(0, stepPercent));
                    progressBar.setProgress((float) stepPercent1 / 100);

        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(stepCountReceiver, new IntentFilter("step-count-update"));
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(averageSpeedAndDistanceReceiver, new IntentFilter("averageSpeed-and-distance-update"));
        if (stepCounterSensor == null) {
            Toast.makeText(requireContext(), "Step counter sensor not available", Toast.LENGTH_SHORT).show();
        }
//        if (currentsteps>=GoalSteps && GoalSteps>0)
//        {
//            sensorManager.unregisterListener(this);
////            stepCountTextView.setText(String.valueOf(currentsteps));
////            int stepPercent = GoalSteps == 0 ? 0 : (int) ((currentsteps * 100) / GoalSteps);
////            progressBar.setProgress(stepPercent);
//            Toast.makeText(requireContext(), "You have reached goal", Toast.LENGTH_SHORT).show();
//        }
//        else {

        int stepPercent2 = GoalSteps == 0 ? 0 : (int) ((currentStepsVal * 100) / GoalSteps);
//        if (stepPercent1>100)
//        {
//            sensorManager.unregisterListener(this);
//        }
//         if(stepPercent2>=100){
//            // int stepPercent = GoalSteps == 0 ? 0 : (int) ((currentsteps * 100) / GoalSteps);
//          //  int stepPercent1 = GoalSteps == 0 ? 0 : (int) ((GoalSteps * 100) / GoalSteps);
//          //  progressBar.setProgress(stepPercent1/100);
//          //   sensorManager.unregisterListener(this);
//            Toast.makeText(requireContext(), "You have reached goal", Toast.LENGTH_SHORT).show();
//        }
//
//        float stepPercent = GoalSteps == 0 ? 0 : (int) ((currentsteps * 100) / GoalSteps);
//        double stepPercent1 = Math.min(100, Math.max(0, stepPercent));
        if (stepPercent >= 100) {
            // stepCountTextView.setText(String.valueOf(currentsteps));
            // int stepPercent = GoalSteps == 0 ? 0 : (int) ((currentsteps * 100) / GoalSteps);
            //  int stepPercent1 = GoalSteps == 0 ? 0 : (int) ((GoalSteps * 100) / GoalSteps);
            // progressBar.setProgress(stepPercent/100);
            Toast.makeText(requireContext(), "You have reached goal", Toast.LENGTH_SHORT).show();
        }

        startTracking();


        //}
    }

    @Override
    public void onPause() {
        super.onPause();
//        sensorManager.unregisterListener(this);
        // getActivity().unregisterReceiver(goalReachedReceiver);
    }


//
//    @SuppressLint("Range")
//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
//            steps = (int) sensorEvent.values[0];
//            currentsteps = steps - previousStepCount;
//            currentPreferences = requireContext().getSharedPreferences("CURRENTSTEP", MODE_PRIVATE);
//            SharedPreferences.Editor editor = currentPreferences.edit();
//            editor.putInt("key2", currentsteps);
//            editor.apply();
//            float stepPercent = GoalSteps == 0 ? 0 : (int) ((currentsteps * 100) / GoalSteps);
//            double stepPercent1 = Math.min(100, Math.max(0, stepPercent));
//
//
//            //  if(stepPercent<=100 &&stepPercent>=0){
////            if (stepPercent1 == 0) {
////                stepPercent1 = 0.1; // Set a minimum value to prevent 0% display
////            }
//
//
//            if (stepPercent>=100) {
////                Intent goalReachedIntent = new Intent("GOAL_REACHED");
////                requireActivity().sendBroadcast(goalReachedIntent);
//                setAlarm();
//            }
//            viewModel.setStepCount(currentsteps);
//           // stepCountTextView.setText(String.valueOf(currentsteps));
//            // int stepPercent = GoalSteps == 0 ? 0 : (int) ((currentsteps * 100) / GoalSteps);
//            // stepPercent = stepPercent/100;
//            progressBar.setProgress((float) stepPercent1 / 100);
//
//        }
//    }

//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }


    private void startTracking() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION)
                == PackageManager.PERMISSION_GRANTED) {
            steps = 0;
            //sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, SENSOR_PERMISSION_REQUEST);
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SAVEDATA", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("key1", previousStepCount);
        editor.apply();

    }

    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SAVEDATA", MODE_PRIVATE);
        int savenumber = sharedPreferences.getInt("key1", 0);
        previousStepCount = savenumber;
    }

    private void resetSteps() {
        stepCountTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                previousStepCount = steps;
                stepCountTextView.setText("0");
                progressBar.setProgress(0);
                saveData();
//                currentPreferences = getActivity().getSharedPreferences("CURRENTSTEP", MODE_PRIVATE);
//                SharedPreferences.Editor editor1 = currentPreferences.edit();
//                editor1.putInt("key2", 0);
//                editor1.apply();
                return true;
            }
        });
    }

//    private class GoalReachedReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if ("GOAL_REACHED".equals(intent.getAction())) {
////                SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//                boolean notificationSent = notificationsharedPreferences.getBoolean("notification_sent", false);
//                // Goal reached, show a notification
//                if (!notificationSent) {
//                    // Create and send the notification
//                    // ...
//                    showNotification("Goal Reached", "Congratulations, you've reached your step goal!");
//                    // Mark that the notification has been sent to avoid showing it again
//                    SharedPreferences.Editor editor = notificationsharedPreferences.edit();
//                    editor.putBoolean("notification_sent", true);
//                    editor.apply();
//                }
//
//            }
//        }
//    }
    private void showNotification(String title, String content) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Notification Channel";
            String description = "Description for my notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm_png)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Create an intent to open the app when the notification is tapped
        Intent intent = new Intent(requireContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }
    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, YourNotificationReceiver.class); // Replace with your BroadcastReceiver
        Intent goalReachedIntent = new Intent("GOAL_REACHED");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, goalReachedIntent, PendingIntent.FLAG_IMMUTABLE);

        // requireActivity().sendBroadcast(goalReachedIntent);
        // Set the alarm to trigger immediately
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }

}
