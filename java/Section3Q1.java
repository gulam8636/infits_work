package com.example.infits;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.infits.customDialog.SectionPref;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Section3Q1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Section3Q1 extends Fragment {

    ImageButton imgBack;
    Button nextbtn;
    TextView backbtn, gastv;
    RadioButton yes,some,occ,no,afterFood,beforeFood;
    String gastritis="";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Section3Q1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Section3Q1.
     */
    // TODO: Rename and change types and number of parameters
    public static Section3Q1 newInstance(String param1, String param2) {
        Section3Q1 fragment = new Section3Q1();
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
        View view = inflater.inflate(R.layout.fragment_section3_q1, container, false);

        //yes,some,occ,no,afterFood,beforeFood

        imgBack = view.findViewById(R.id.imgback);
        nextbtn = view.findViewById(R.id.nextbtn);
        backbtn = view.findViewById(R.id.backbtn);
        yes = view.findViewById(R.id.yes);
        some = view.findViewById(R.id.some);
        no = view.findViewById(R.id.no);
        occ = view.findViewById(R.id.occ);
        afterFood = view.findViewById(R.id.afterFood);
        beforeFood = view.findViewById(R.id.beforeFood);
        gastv = view.findViewById(R.id.textView77);


        TextView gotomain = view.findViewById(R.id.gotomainsection);
        gotomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_section3Q1_to_consultationFragment);

            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("STEP3Q1", Context.MODE_PRIVATE);
        String storedvalue = sharedPreferences.getString("gastritis", "");
        if(!storedvalue.isEmpty()) {
            switch (storedvalue) {
                case "Yes":
                   Yes();
                    break;
                case "Sometimes":
                    Some();
                    break;
                case "No":
                    No();
                    break;
                case "Occasionally":
                    Occ();
                    break;
                case "After having food":
                    AfterFood();
                    break;
                case "Before having food":
                    BeforeFood();
                    break;
                default:

            }
            DataSectionThree.gastritis = storedvalue;
        }

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Yes();
            }
        });

        some.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Some();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                No();
            }
        });

        occ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Occ();
            }
        });

        afterFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AfterFood();
            }
        });

        beforeFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeforeFood();
            }
        });


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getContext(),employment, Toast.LENGTH_SHORT).show();

                DataSectionThree.gastritis = gastritis;
                DataSectionThree.s3q1 = gastv.getText().toString();
                if (gastritis.equals(""))
                    Toast.makeText(getContext(), "Select atleast one of the given options", Toast.LENGTH_SHORT).show();
                else {
                    ConsultationFragment.psection3 += 1;
//                    SharedPreferences sharedPreferences2 = requireContext().getSharedPreferences("SEC3PROG", Context.MODE_PRIVATE);
//                    int preval =       sharedPreferences2.getInt("progress3",0);
                    SectionPref.saveformsection3("gastritis",gastritis,0,0,1,"STEP3Q1",requireContext());
//
                    Navigation.findNavController(v).navigate(R.id.action_section3Q1_to_section3Q2);
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConsultationFragment.psection3>0)
                    ConsultationFragment.psection3-=1;
                requireActivity().onBackPressed();
            }
        });

        imgBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void Yes() {
        yes.setBackgroundResource(R.drawable.radiobtn_on);
        some.setBackgroundResource(R.drawable.radiobtn_off);
        no.setBackgroundResource(R.drawable.radiobtn_off);
        occ.setBackgroundResource(R.drawable.radiobtn_off);
        afterFood.setBackgroundResource(R.drawable.radiobtn_off);
        beforeFood.setBackgroundResource(R.drawable.radiobtn_off);

        yes.setTextColor(Color.WHITE);
        some.setTextColor(Color.BLACK);
        no.setTextColor(Color.BLACK);
        occ.setTextColor(Color.BLACK);
        afterFood.setTextColor(Color.BLACK);
        beforeFood.setTextColor(Color.BLACK);

        gastritis="Yes";
    }

    private void Some() {
        some.setBackgroundResource(R.drawable.radiobtn_on);
        yes.setBackgroundResource(R.drawable.radiobtn_off);
        no.setBackgroundResource(R.drawable.radiobtn_off);
        occ.setBackgroundResource(R.drawable.radiobtn_off);
        afterFood.setBackgroundResource(R.drawable.radiobtn_off);
        beforeFood.setBackgroundResource(R.drawable.radiobtn_off);

        some.setTextColor(Color.WHITE);
        yes.setTextColor(Color.BLACK);
        no.setTextColor(Color.BLACK);
        occ.setTextColor(Color.BLACK);
        afterFood.setTextColor(Color.BLACK);
        beforeFood.setTextColor(Color.BLACK);

        gastritis="Sometimes";
    }

    private void No() {
        no.setBackgroundResource(R.drawable.radiobtn_on);
        some.setBackgroundResource(R.drawable.radiobtn_off);
        yes.setBackgroundResource(R.drawable.radiobtn_off);
        occ.setBackgroundResource(R.drawable.radiobtn_off);
        afterFood.setBackgroundResource(R.drawable.radiobtn_off);
        beforeFood.setBackgroundResource(R.drawable.radiobtn_off);

        no.setTextColor(Color.WHITE);
        some.setTextColor(Color.BLACK);
        yes.setTextColor(Color.BLACK);
        occ.setTextColor(Color.BLACK);
        afterFood.setTextColor(Color.BLACK);
        beforeFood.setTextColor(Color.BLACK);

        gastritis="No";
    }

    private void Occ() {
        occ.setBackgroundResource(R.drawable.radiobtn_on);
        some.setBackgroundResource(R.drawable.radiobtn_off);
        no.setBackgroundResource(R.drawable.radiobtn_off);
        yes.setBackgroundResource(R.drawable.radiobtn_off);
        afterFood.setBackgroundResource(R.drawable.radiobtn_off);
        beforeFood.setBackgroundResource(R.drawable.radiobtn_off);

        occ.setTextColor(Color.WHITE);
        some.setTextColor(Color.BLACK);
        no.setTextColor(Color.BLACK);
        yes.setTextColor(Color.BLACK);
        afterFood.setTextColor(Color.BLACK);
        beforeFood.setTextColor(Color.BLACK);

        gastritis="Occasionally";
    }

    private void AfterFood() {
        afterFood.setBackgroundResource(R.drawable.radiobtn_on);
        some.setBackgroundResource(R.drawable.radiobtn_off);
        no.setBackgroundResource(R.drawable.radiobtn_off);
        occ.setBackgroundResource(R.drawable.radiobtn_off);
        yes.setBackgroundResource(R.drawable.radiobtn_off);
        beforeFood.setBackgroundResource(R.drawable.radiobtn_off);

        afterFood.setTextColor(Color.WHITE);
        some.setTextColor(Color.BLACK);
        no.setTextColor(Color.BLACK);
        occ.setTextColor(Color.BLACK);
        yes.setTextColor(Color.BLACK);
        beforeFood.setTextColor(Color.BLACK);

        gastritis="After having food";
    }

    private void BeforeFood() {
        beforeFood.setBackgroundResource(R.drawable.radiobtn_on);
        some.setBackgroundResource(R.drawable.radiobtn_off);
        no.setBackgroundResource(R.drawable.radiobtn_off);
        occ.setBackgroundResource(R.drawable.radiobtn_off);
        afterFood.setBackgroundResource(R.drawable.radiobtn_off);
        yes.setBackgroundResource(R.drawable.radiobtn_off);

        beforeFood.setTextColor(Color.WHITE);
        some.setTextColor(Color.BLACK);
        no.setTextColor(Color.BLACK);
        occ.setTextColor(Color.BLACK);
        afterFood.setTextColor(Color.BLACK);
        yes.setTextColor(Color.BLACK);

        gastritis="Before having food";
    }
}