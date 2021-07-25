package com.example.uasshabri.dashboard.fragment.dailyreport;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.uasshabri.R;
import com.example.uasshabri.dashboard.fragment.dailyreport.adapter.ListDailyreportsAdapter;
import com.example.uasshabri.dashboard.fragment.model.Dailyreport;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyreportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyreportFragment extends Fragment implements View.OnClickListener {
    private Button btnAdd;
    private RecyclerView recyclerView;
    private ListDailyreportsAdapter dailyreportsAdapter;
    private List<Dailyreport> dailyreportList;
    private ArrayList<String> dailyreportDocument;
    private FirebaseFirestore firebaseFirestore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DailyreportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyreportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyreportFragment newInstance(String param1, String param2) {
        DailyreportFragment fragment = new DailyreportFragment();
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
        return inflater.inflate(R.layout.fragment_dailyreport, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAdd = view.findViewById(R.id.addDailyreport);
        recyclerView = view.findViewById(R.id.recycleviewDailyreport);

        btnAdd.setOnClickListener(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getDailyreportsData();
    }

    private void getDailyreportsData() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        dailyreportList = new ArrayList<>();
        dailyreportDocument = new ArrayList<>();
        dailyreportsAdapter = new ListDailyreportsAdapter(getActivity(), dailyreportList, dailyreportDocument);
        recyclerView.setAdapter(dailyreportsAdapter);

        firebaseFirestore.collection("daily-reports")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dailyreportList.clear();

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Dailyreport dailyreport = documentSnapshot.toObject(Dailyreport.class);
                                dailyreportList.add(dailyreport);
                                dailyreportDocument.add(documentSnapshot.getId());
                            }
                            dailyreportsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong, " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong, " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addDailyreport:
                Navigation.findNavController(view).navigate(R.id.action_dailyreportFragment_to_addDailyreportFragment);
                break;
        }
    }
}