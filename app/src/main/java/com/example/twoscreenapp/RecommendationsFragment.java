package com.example.twoscreenapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecommendationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public View mView;
    private ImageButton imagebtn1;
    private ImageButton imagebtn2;
    private ImageButton imagebtn3;
    private ImageButton imagebtn4;
    private ImageButton imagebtn5;
    private boolean first_reco = true;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecommendationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecommendationsFragment newInstance(String param1, String param2) {
        RecommendationsFragment fragment = new RecommendationsFragment();
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
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_recommendations, container, false);
        helperLoadImg(mView);
        return mView;
    }



    public void helperLoadImg(View v){
        if(RecommendationPage.pub_result != null) {
            if (first_reco) {
                imagebtn1 = v.findViewById(R.id.Model1);
                imagebtn2 = v.findViewById(R.id.Model2);
                imagebtn3 = v.findViewById(R.id.Model3);
                imagebtn4 = v.findViewById(R.id.Model4);
                imagebtn5 = v.findViewById(R.id.Model5);
                for (int i = 0; i < RecommendationPage.pub_result.length; i++) {
                    imagebtn1.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[0], "drawable", getActivity().getPackageName()));
                    imagebtn2.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[1], "drawable", getActivity().getPackageName()));
                    imagebtn3.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[2], "drawable", getActivity().getPackageName()));
                    imagebtn4.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[3], "drawable", getActivity().getPackageName()));
                    imagebtn5.setImageResource(getResources().getIdentifier(RecommendationPage.pub_result[4], "drawable", getActivity().getPackageName()));
                    Log.d("insideTestReco", RecommendationPage.pub_result[i]);
                }
                Log.d("loadImg", "load9ing..");

                first_reco = false;
            }
        }
        else{
            Log.d("pub_result", "Is length 0");
            return;
        }
    }
}