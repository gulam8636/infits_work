package com.example.infits;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.infits.R;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class running_frag1 extends Fragment implements SensorEventListener {

    private RotateAnimation rotateAnimation;
    private boolean isRotationStarted = false;

    SensorManager sensorManager;
    Sensor stepSensor;
    int pre_step=0,current=0,flag_steps=0,current_steps;
    float distance, calories;

    Button btn_pause, btn_start;
    ImageView btn_stop;
    ImageView imageView80;
    ImageView imageView79;
    ImageView imageView76;
    TextView running_txt, cont_running_txt, distance_disp, calorie_disp, time_disp;

    public static final String preference = "running_values";
    SharedPreferences sharedpreferences;
    Context c;

    String calories_save = "", distance_save = " ", time_disp_save = " ", goal = "8", goal_save = "", time = "2";

    public running_frag1() {

    }

    public static running_frag1 newInstance(String param1, String param2) {
        running_frag1 fragment = new running_frag1();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_running_frag1, container, false);
        //distance_show=view.findViewById(R.id.textView70);
        btn_pause = view.findViewById(R.id.imageView86);
        btn_start = view.findViewById(R.id.imageView105);
        btn_stop = view.findViewById(R.id.imageView89);
        imageView80 = view.findViewById(R.id.imageView80);
        imageView76 = view.findViewById(R.id.imageView76);
        imageView79 = view.findViewById(R.id.imageView79);
        running_txt = view.findViewById(R.id.textView63);
        cont_running_txt = view.findViewById(R.id.textView89);
        distance_disp = view.findViewById(R.id.textView70);
        calorie_disp = view.findViewById(R.id.textView72);
        time_disp = view.findViewById(R.id.textView73);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        register();

        // Start the rotation animation when the fragment is created
        startRotationAnimation();

        //Activity Paused
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start.setVisibility(View.VISIBLE);
                btn_pause.setVisibility(View.GONE);
                running_txt.setVisibility(View.GONE);
                cont_running_txt.setVisibility(View.VISIBLE);
                imageView76.clearAnimation();
                imageView79.clearAnimation();
                imageView80.clearAnimation();
                isRotationStarted = false;
                flag_steps = 0;
                register();
            }
        });

        // stop activity
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send data to the server when "Stop" button is pressed
                sendDataToServer();

                // Navigate to the next fragment after sending the data
                Navigation.findNavController(v).navigate(R.id.action_running_frag1_to_activitySecondFragment);
            }
        });

        //Activity Start/Resume
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start.setVisibility(View.GONE);
                btn_pause.setVisibility(View.VISIBLE);
                running_txt.setVisibility(View.VISIBLE);
                cont_running_txt.setVisibility(View.GONE);

                if (!isRotationStarted) {
                    // Start the rotation animations
                    startRotationAnimation();

                    isRotationStarted = true;
                }
                flag_steps = 0;
                stop();
            }
        });

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (flag_steps == 0) {
            pre_step = (int) event.values[0] - 1;
            flag_steps = 1;
        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            current = (int) event.values[0];
            current_steps = current - pre_step;
            distance = (float) 0.002 * current_steps;
            calories = (float) 0.06 * current_steps;

            distance_disp.setText(String.format("%.2f", distance));
            calorie_disp.setText(String.format("%.2f", calories));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void register() {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this, stepSensor);
    }

    public void startRotationAnimation() {
        // Create a RotateAnimation for imageView79 (anti-clockwise)
        RotateAnimation rotateAnimation79 = new RotateAnimation(
                0, -360, // Starting and ending angles of rotation (0 to -360 degrees for anti-clockwise)
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point for the X coordinate (center)
                Animation.RELATIVE_TO_SELF, 0.5f // Pivot point for the Y coordinate (center)
        );

        // Set the animation properties
        rotateAnimation79.setInterpolator(new LinearInterpolator()); // LinearInterpolator for smooth rotation
        rotateAnimation79.setDuration(4000); // Duration of the rotation animation in milliseconds
        rotateAnimation79.setRepeatCount(Animation.INFINITE); // Infinite repeat count for continuous rotation
        rotateAnimation79.setFillAfter(true); // Set to true to keep the final rotation state after the animation ends

        // Create a RotateAnimation for imageView80 and imageView76 (clockwise)
        rotateAnimation = new RotateAnimation(
                0, 360, // Starting and ending angles of rotation (0 to 360 degrees for clockwise)
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point for the X coordinate (center)
                Animation.RELATIVE_TO_SELF, 0.5f // Pivot point for the Y coordinate (center)
        );

        // Set the animation properties
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(4000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setFillAfter(true);

        // Apply the animations to the corresponding ImageViews
        imageView80.startAnimation(rotateAnimation);
        imageView76.startAnimation(rotateAnimation);
        imageView79.startAnimation(rotateAnimation79);
    }

    private void sendDataToServer() {
        String url = "https://infits.in/androidApi/runningTracker.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            if (response.equals("updated")) {
                Toast.makeText(getActivity(), "Data updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to update data", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(requireContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Data to be sent in the request body
                Map<String, String> data = new HashMap<>();
                data.put("client_id", DataFromDatabase.client_id);
                data.put("clientuserID", DataFromDatabase.clientuserID);
                data.put("distance", String.valueOf(distance));
                data.put("calories", String.valueOf(calories));
                data.put("runtime", String.valueOf(time));
                data.put("goal", goal);
                data.put("duration", "0");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                data.put("date", dtf.format(now));
                data.put("dateandtime", DTF.format(now));
                return data;
            }
        };

        // Set a retry policy in case of timeout or connection errors
        int socketTimeout = 30000; // 30 seconds
        request.setRetryPolicy(new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(request);
        Toast.makeText(getActivity(), "Updating data...", Toast.LENGTH_SHORT).show();
    }
}
