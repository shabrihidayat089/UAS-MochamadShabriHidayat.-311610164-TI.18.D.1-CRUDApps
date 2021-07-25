package com.example.uasshabri.dashboard.fragment.dailyreport;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.uasshabri.R;
import com.example.uasshabri.dashboard.fragment.model.Dailyreport;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateDailyreportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateDailyreportFragment extends Fragment implements View.OnClickListener {
    private EditText edtId, edtDate, edtCreatedBy, edtPlanQty, edtMinQty, edtTodayQty, edtMaterial, edtMachine;
    private ProgressBar progressBar;
    private Button btnUpdate, btnCancel;
    private FirebaseFirestore firebaseFirestore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpdateDailyreportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateDailyreportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateDailyreportFragment newInstance(String param1, String param2) {
        UpdateDailyreportFragment fragment = new UpdateDailyreportFragment();
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
        return inflater.inflate(R.layout.fragment_update_dailyreport, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtId = view.findViewById(R.id.editDailyreportId);
        edtDate = view.findViewById(R.id.editDailyreportDate);
        edtCreatedBy = view.findViewById(R.id.editDailyreportCreatedby);
        edtPlanQty = view.findViewById(R.id.editDailyreportPlanQty);
        edtMinQty = view.findViewById(R.id.editDailyreportMinQty);
        edtTodayQty = view.findViewById(R.id.editDailyreportTodayQty);
        edtMaterial = view.findViewById(R.id.editDailyreportMaterial);
        edtMachine = view.findViewById(R.id.editDailyreportMachine);
        btnUpdate = view.findViewById(R.id.btnEditDailyreport);
        btnCancel = view.findViewById(R.id.btnCancel);
        progressBar = view.findViewById(R.id.progressBarDailyreport);

        edtDate.setKeyListener(null);
        edtCreatedBy.setKeyListener(null);
        edtPlanQty.setKeyListener(null);
        edtMinQty.setKeyListener(null);
        edtMaterial.setKeyListener(null);
        edtMachine.setKeyListener(null);

        btnUpdate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initGetDataDailyreport();
    }

    private void initGetDataDailyreport() {
        String dailyreportDocument = getArguments().getString("uDocument");
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("daily-reports").document(dailyreportDocument);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Dailyreport dailyreport = documentSnapshot.toObject(Dailyreport.class);
                        edtId.setText(dailyreport.getId());
                        edtDate.setText(dailyreport.getDate());
                        edtCreatedBy.setText(dailyreport.getCreate_by());
                        edtPlanQty.setText("" + dailyreport.getPlan_qty());
                        edtMinQty.setText("" + dailyreport.getMin_qty());
                        edtTodayQty.setText("" + dailyreport.getToday_qty());
                        edtMaterial.setText(dailyreport.getId_material());
                        edtMachine.setText(dailyreport.getId_machine());
                    } else {
                        Toast.makeText(getActivity(), "Document not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong, " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Something went wrong, " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEditDailyreport:
                saveDataDailyreport(view);
                break;
            case R.id.btnCancel:
                getActivity().onBackPressed();
                break;
        }
    }

    private void saveDataDailyreport(View view) {
        progressBar.setVisibility(View.VISIBLE);
        String dailyreportDocument = getArguments().getString("uDocument");
        String id = edtId.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String create_by = edtCreatedBy.getText().toString().trim();
        int plan_qty = stringToInt(edtPlanQty.getText().toString().trim());
        int min_qty = stringToInt(edtMinQty.getText().toString().trim());
        int today_qty = stringToInt(edtTodayQty.getText().toString().trim());
        String material = edtMaterial.getText().toString().trim();
        String machine = edtMachine.getText().toString().trim();

        if (checkRule(id, today_qty)) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection("daily-reports").document(dailyreportDocument);

            Dailyreport dailyreport = new Dailyreport(id, date, create_by, plan_qty, min_qty, today_qty, material, machine);
            documentReference
                    .update("id", dailyreport.getId(), "today_qty", dailyreport.getToday_qty())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(), "Data daily report edited", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            Navigation.findNavController(view).navigate(R.id.dailyreportFragment);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Data failed to edited, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }

    }

    protected boolean checkRule(String id, int today_qty) {
        if (TextUtils.isEmpty(id)) {
            edtId.setError("ID is required");
            edtId.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (today_qty == 0) {
            edtTodayQty.setError("Today qty must be 1 or higher");
            edtTodayQty.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        return true;
    }

    private int stringToInt(String str) {
        int result = 0;
        if (!TextUtils.isEmpty(str)) {
            result = Integer.parseInt(str);
        }

        return result;
    }
}