package com.example.uasshabri.dashboard.fragment.machine;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.uasshabri.R;
import com.example.uasshabri.dashboard.fragment.model.Machine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddMachineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMachineFragment extends Fragment implements View.OnClickListener {
    private EditText inptMachineId, inptMachineName;
    private Button btnSave, btnCancel, btnDatePicker;
    private DatePickerDialog datePickerDialog;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddMachineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddMachineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMachineFragment newInstance(String param1, String param2) {
        AddMachineFragment fragment = new AddMachineFragment();
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
        return inflater.inflate(R.layout.fragment_add_machine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave = view.findViewById(R.id.btnAddMachine);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnDatePicker = view.findViewById(R.id.datePicker);
        inptMachineId = view.findViewById(R.id.machineId);
        inptMachineName = view.findViewById(R.id.machineName);
        progressBar = view.findViewById(R.id.progressBarMachine);

        initDatePicker();

        btnDatePicker.setText(getTodaysDate());

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddMachine:
                progressBar.setVisibility(View.VISIBLE);
                String id = inptMachineId.getText().toString().trim();
                String name = inptMachineName.getText().toString().trim();
                String date = btnDatePicker.getText().toString().trim();

                if (checkRule(id, name)) {
                    firebaseFirestore = FirebaseFirestore.getInstance();

                    Machine machine = new Machine(id, name, date);

                    firebaseFirestore.collection("machines")
                            .add(machine)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getActivity(), "Data machine added", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    getActivity().onBackPressed();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Data failed to added, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
                break;
            case R.id.btnCancel:
                getActivity().onBackPressed();
                break;
            case R.id.datePicker:
                datePickerDialog.show();
                break;
        }
    }

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                btnDatePicker.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = R.style.Theme_MaterialComponents_Light_Dialog_Alert;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return day + "/" + month + "/" + year;
    }

    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }

    protected boolean checkRule(String id, String name) {
        if (TextUtils.isEmpty(id)) {
            inptMachineId.setError("ID is required");
            inptMachineId.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(name)) {
            inptMachineName.setError("Name is required");
            inptMachineName.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        return true;
    }
}