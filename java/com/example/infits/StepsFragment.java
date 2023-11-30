package com.example.infits;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.archit.calendardaterangepicker.customviews.CalendarDateRangeManager;
import com.archit.calendardaterangepicker.customviews.CustomDateView;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.archit.calendardaterangepicker.models.CalendarStyleAttributes;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.savvi.rangedatepicker.CalendarPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class StepsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StepsFragment() {
        // Required empty public constructor
    }

    RadioGroup grp;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepsFragment newInstance(String param1, String param2) {
        StepsFragment fragment = new StepsFragment();
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
                requireActivity().finish();
                startActivity(new Intent(getActivity(),DashBoardMain.class));
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_steps, container, false);

        LineChart lineChart = view.findViewById(R.id.graph);
        ArrayList<Entry> NoOfEmp = new ArrayList<>();

        final LineDataSet[] dataSet = {new LineDataSet(NoOfEmp, "Number Of Employees")};
        dataSet[0].setDrawHorizontalHighlightIndicator(false);
        dataSet[0].setDrawVerticalHighlightIndicator(false);
        ArrayList<ILineDataSet> year = new ArrayList<>();
        year.add(dataSet[0]);

        dataSet[0].setColor(Color.parseColor("#FFA381"));
        dataSet[0].setCircleColor(Color.parseColor("#FFA381"));
        Typeface tf = ResourcesCompat.getFont(getContext(), R.font.nats_regular);
        LineData data = new LineData(dataSet[0]);

        XAxis xl = lineChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart.getAxisRight().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setDrawAxisLine(true);
        yAxisRight.setDrawGridLines(true);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(true);

        data.setValueTypeface(tf);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                e.getData();
                CustomMarkerView customMarkerView = new CustomMarkerView(getContext(), R.layout.mark);
                lineChart.setMarker(customMarkerView);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        dataSet[0].setDrawValues(false);

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        lineChart.setData(data);

        RadioButton week_radioButton = view.findViewById(R.id.week_btn_steps);
        RadioButton month_radioButton = view.findViewById(R.id.month_btn_steps);
        RadioButton year_radioButton = view.findViewById(R.id.year_btn_steps);
        RadioButton custom_radioButton = view.findViewById(R.id.customdates_btn_steps);
        week_radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoOfEmp.removeAll(NoOfEmp);
                //String url = String.format("%sstepsGraph.php", DataFromDatabase.ipConfig);
                String url = "https://infits.in/androidApi/stepsGraph.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                    System.out.println(DataFromDatabase.clientuserID);
                    System.out.println(response);
                    List<String> allNames = new ArrayList<>();
                    JSONObject jsonResponse = null;
                    ArrayList<String> mons = new ArrayList<>();
                    Log.d("stepsGraph", response);
                    try {
                        jsonResponse = new JSONObject(response);
                        JSONArray cast = jsonResponse.getJSONArray("steps");
                        for (int i = 0; i < cast.length(); i++) {
                            JSONObject actor = cast.getJSONObject(i);
                            String name = actor.getString("steps");
                            if(name==null){
                                name="0";
                            }
                            String date = actor.getString("date");
                            System.out.println(name + "   " + date);
                            allNames.add(name);
                            mons.add(date);
                            NoOfEmp.add(new Entry(i, Float.parseFloat(name)));
                            System.out.println("Points " + NoOfEmp.get(i));
                            dataSet[0] = (LineDataSet) lineChart.getData().getDataSetByIndex(0);

                            dataSet[0].setValues(NoOfEmp);


                            dataSet[0].notifyDataSetChanged();
                        }
                        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(mons));

                        lineChart.getData().notifyDataChanged();
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Log.d("Data", error.toString().trim());
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> dataVol = new HashMap<>();

                        dataVol.put("clientuserID", DataFromDatabase.clientuserID);
                        return dataVol;
                    }
                };
                Volley.newRequestQueue(getActivity()).add(stringRequest);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            }
        });

        week_radioButton.performClick();

        month_radioButton.setOnClickListener(v -> {
            NoOfEmp.removeAll(NoOfEmp);
            //String url = String.format("%sstepsMonthGraph.php", DataFromDatabase.ipConfig);
            String url = "https://infits.in/androidApi/stepsMonthGraph.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                System.out.println(DataFromDatabase.clientuserID);
                System.out.println(response);
                List<String> allNames = new ArrayList<>();
                JSONObject jsonResponse = null;
                ArrayList<String> mons = new ArrayList<>();
                Log.d("stepsMonthGraph", response);
                try {
                    jsonResponse = new JSONObject(response);
                    JSONArray cast = jsonResponse.getJSONArray("steps");
                    for (int i = 0; i < cast.length(); i++) {
                        Log.d("data",cast.toString());
                        JSONObject actor = cast.getJSONObject(i);
                        String name = actor.getString("steps");
                        String date = actor.getString("date");
                        System.out.println(name + "   " + date);
                        allNames.add(name);
                        mons.add(date);
                        NoOfEmp.add(new Entry(i, Float.parseFloat(name)));
                        System.out.println("Points " + NoOfEmp.get(i));
                        dataSet[0] = (LineDataSet) lineChart.getData().getDataSetByIndex(0);

                        dataSet[0].setValues(NoOfEmp);


                        dataSet[0].notifyDataSetChanged();
                    }
                    lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(mons));

                    lineChart.getData().notifyDataChanged();
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.d("Data", error.toString().trim());
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> data = new HashMap<>();

                    data.put("clientuserID", DataFromDatabase.clientuserID);

                    return data;
                }
            };
            Volley.newRequestQueue(getActivity()).add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        });
        year_radioButton.setOnClickListener(v -> {
            NoOfEmp.removeAll(NoOfEmp);
            //String url = String.format("%sstepsYearGraph.php", DataFromDatabase.ipConfig);
            String url = "https://infits.in/androidApi/stepsYearGraph.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                List<String> allNames = new ArrayList<>();
                JSONObject jsonResponse = null;
                ArrayList<String> mons = new ArrayList<>();
                try {
                    jsonResponse = new JSONObject(response);
                    JSONArray cast = jsonResponse.getJSONArray("steps");
                    for (int i = 0; i < cast.length(); i++) {
                        JSONObject actor = cast.getJSONObject(i);
                        String name = actor.getString("av");
                        String month = actor.getString("month");
                        System.out.println(name);
                        allNames.add(name);
                        mons.add(month);
                        NoOfEmp.add(new Entry(i, Float.parseFloat(name)));
                        System.out.println("Points " + NoOfEmp.get(i));
                        dataSet[0] = (LineDataSet) lineChart.getData().getDataSetByIndex(0);

                        dataSet[0].setValues(NoOfEmp);
                        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(mons));

                        dataSet[0].notifyDataSetChanged();
                        lineChart.getData().notifyDataChanged();
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.d("Data", error.toString().trim());
            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> data = new HashMap<>();

                    data.put("clientID", DataFromDatabase.client_id);

                    return data;
                }
            };
            Volley.newRequestQueue(getActivity()).add(stringRequest);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        });
        custom_radioButton.setOnClickListener(v -> {
            NoOfEmp.removeAll(NoOfEmp);
            final Dialog dialog = new Dialog(getActivity());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.calender_dialog);
            final Calendar nextYear = Calendar.getInstance();
            nextYear.add(Calendar.YEAR, 10);

            final Calendar lastYear = Calendar.getInstance();
            lastYear.add(Calendar.YEAR, -10);

            CalendarPickerView calendarPickerView = dialog.findViewById(R.id.cal_for_graph);
            calendarPickerView.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MMMM", Locale.getDefault())).inMode(CalendarPickerView.SelectionMode.RANGE).withSelectedDate(new Date());

            Button done = dialog.findViewById(R.id.done);
            Button cancel = dialog.findViewById(R.id.cancel);

            done.setBackgroundColor(Color.parseColor("#FFA381"));

            done.setOnClickListener(vi -> {
                List<Date> dates = calendarPickerView.getSelectedDates();
                SimpleDateFormat sf = new SimpleDateFormat("MMM dd,yyyy");
                String from = sf.format(dates.get(0));
                String to = sf.format(dates.get(dates.size() - 1));
                //String url = String.format("%scustom.php", DataFromDatabase.ipConfig);
                String url = "https://infits.in/androidApi/customStep.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                    System.out.println(DataFromDatabase.clientuserID);
                    System.out.println(response);
                    List<String> allNames = new ArrayList<>();
                    JSONObject jsonResponse = null;
                    ArrayList<String> mons = new ArrayList<>();
                    try {
                        jsonResponse = new JSONObject(response);
                        JSONArray cast = jsonResponse.getJSONArray("steps");
                        for (int i = 0; i < cast.length(); i++) {
                            JSONObject actor = cast.getJSONObject(i);
                            String name = actor.getString("steps");
                            String date = actor.getString("date");
                            System.out.println(name + "   " + date);
                            allNames.add(name);
                            mons.add(date);
                            NoOfEmp.add(new Entry(i, Float.parseFloat(name)));
                            System.out.println("Points " + NoOfEmp.get(i));
                            dataSet[0] = (LineDataSet) lineChart.getData().getDataSetByIndex(0);

                            dataSet[0].setValues(NoOfEmp);

                            dataSet[0].notifyDataSetChanged();
                        }
                        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(mons));

                        lineChart.getData().notifyDataChanged();
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Log.d("Data", error.toString().trim());
                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> dataVol = new HashMap<>();
                        dataVol.put("clientID", getClientId());
                        dataVol.put("from", from);
                        dataVol.put("to", to);
                        return dataVol;
                    }
                };
                Volley.newRequestQueue(getActivity()).add(stringRequest);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                dialog.dismiss();
            });

            cancel.setOnClickListener(view1 -> {
                dialog.dismiss();
            });
            dialog.show();
        });
        return view;
    }
    private String getClientId(){
        SharedPreferences loginDetails = requireContext().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
        return loginDetails.getString("client_id","fes");

    }
}