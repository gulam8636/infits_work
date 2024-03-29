package com.example.infits;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
 * Use the {@link SectionOneQFour#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectionOneQFour extends Fragment {

    ImageButton imgBack;
    Button nextbtn;
    TextView backbtn, hometv;
    EditText eTextEmail;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SectionOneQFour() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SectionOneQFour.
     */
    // TODO: Rename and change types and number of parameters
    public static SectionOneQFour newInstance(String param1, String param2) {
        SectionOneQFour fragment = new SectionOneQFour();
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
        View view = inflater.inflate(R.layout.fragment_section_one_q_four, container, false);

        imgBack = view.findViewById(R.id.imgback);
        nextbtn = view.findViewById(R.id.nextbtn);
        backbtn = view.findViewById(R.id.backbtn);
        eTextEmail = view.findViewById(R.id.eTextEmail);

        hometv = view.findViewById(R.id.textView80);

        TextView gotomain = view.findViewById(R.id.gotomainsection);
        gotomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_sectionOneQFour_to_consultationFragment);

            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("STEP1Q4", Context.MODE_PRIVATE);
        String storedvalue = sharedPreferences.getString("hometown", "");
        if(!storedvalue.isEmpty()) {
            eTextEmail.setText(storedvalue);
            DataSectionOne.hometown = storedvalue;
        }

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_hometown = eTextEmail.getText().toString();
                //Toast.makeText(getContext(),user_email, Toast.LENGTH_SHORT).show();

                DataSectionOne.hometown = user_hometown;
                DataSectionOne.s1q4 = hometv.getText().toString();
                if(user_hometown.equals("")|| user_hometown.equals(" "))
                    Toast.makeText(getContext(),"Add your hometown",Toast.LENGTH_SHORT).show();
                else{
                    ConsultationFragment.psection1+=1;
                    SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("SEC1PROG", Context.MODE_PRIVATE);
                    int preval =       sharedPreferences2.getInt("progress",0);
                    SectionPref.saveform("hometown",eTextEmail.getText().toString(),3,preval,4,"STEP1Q4",requireContext());
//                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("STEP1Q4", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("hometown", eTextEmail.getText().toString());
//                    editor.apply();
//                    String sharedage = sharedPreferences.getString("hometown", "");
//
//                    if (!(sharedage.isEmpty()) && preval==3){
//                        SharedPreferences sharedPreferences1 = requireContext().getSharedPreferences("SEC1PROG", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
//                        editor1.putInt("progress", 4);
//                        editor1.apply();
//                    }
                Navigation.findNavController(v).navigate(R.id.action_sectionOneQFour_to_sectionOneQFive);
            }}
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConsultationFragment.psection1>0)
                    ConsultationFragment.psection1-=1;
                requireActivity().onBackPressed();
            }
        });

        imgBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }
}