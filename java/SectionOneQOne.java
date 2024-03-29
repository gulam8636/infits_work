package com.example.infits;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infits.customDialog.SectionPref;

import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SectionOneQOne#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectionOneQOne extends Fragment {

    ImageButton imgBack;
    Button nextbtn;
    TextView backbtn, emailtv;
    EditText eTextEmail;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    int position = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SectionOneQOne() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SectionOneQOne.
     */
    // TODO: Rename and change types and number of parameters
    public static SectionOneQOne newInstance(String param1, String param2) {
        SectionOneQOne fragment = new SectionOneQOne();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_section_one_q_one, container, false);

        imgBack = view.findViewById(R.id.imgback);
        nextbtn = view.findViewById(R.id.nextbtn);
        backbtn = view.findViewById(R.id.backbtn);
        eTextEmail = view.findViewById(R.id.eTextEmail);
        emailtv = view.findViewById(R.id.textView80);

        TextView gotomain = view.findViewById(R.id.gotomainsection);
        gotomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_sectionOneQOne_to_consultationFragment);
            }
        });
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("STEP1Q1", Context.MODE_PRIVATE);
        String storedvalue = sharedPreferences.getString("useremail", "");
        if(!storedvalue.isEmpty()) {
            eTextEmail.setText(storedvalue);
            DataSectionOne.email = storedvalue;
        }


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  String user_email = eTextEmail.getText().toString();
                //Toast.makeText(getContext(),user_email, Toast.LENGTH_SHORT).show();
                String user_email = eTextEmail.getText().toString();
                DataSectionOne.s1q1 = emailtv.getText().toString();
                if(user_email.equals("")|| user_email.equals(" ")) {
                    Toast.makeText(getContext(), "Add your email", Toast.LENGTH_SHORT).show();
                }
               else if(!isValidEmail(eTextEmail.getText().toString().trim())){
                    Toast.makeText(requireContext(), "InValid Email Address.", Toast.LENGTH_SHORT).show();
                }
                else {
                    DataSectionOne.email = user_email;
                    ConsultationFragment.psection1 += 1;
                    SectionPref.saveform("useremail",eTextEmail.getText().toString(),0,0,1,"STEP1Q1",requireContext());
//                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("STEP1Q1", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("useremail", eTextEmail.getText().toString());
//                    editor.apply();
//
//                    String sharedemail = sharedPreferences.getString("useremail", "");
//                    // Example in an Activity
//                    Toast.makeText(requireContext(),sharedemail , Toast.LENGTH_SHORT).show();
//
//                    if (!(sharedemail.isEmpty()) && ConsultationFragment.psection1==1){
//                        SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("SEC1PROG", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
//                    editor1.putInt("progress", ConsultationFragment.psection1);
//                    editor1.apply();
//                    }

                    Navigation.findNavController(v).navigate(R.id.action_sectionOneQOne_to_sectionOneQTwo);
                 }
            }
        });

          // emailpref = requireContext().getSharedPreferences("SEC1Q1", Context.MODE_PRIVATE);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConsultationFragment.psection1>0)
                    ConsultationFragment.psection1-=1;
                requireActivity().onBackPressed();
            }
        });

        imgBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}