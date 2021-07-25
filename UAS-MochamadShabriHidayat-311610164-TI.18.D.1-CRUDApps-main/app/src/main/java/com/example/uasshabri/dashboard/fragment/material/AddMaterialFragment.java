package com.example.uasshabri.dashboard.fragment.material;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.uasshabri.R;
import com.example.uasshabri.dashboard.fragment.model.Material;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddMaterialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMaterialFragment extends Fragment implements View.OnClickListener {
    private Button btnSave, btnCancel;
    private EditText edtId, edtName, edtWeight, edtQty;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddMaterialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddMaterialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddMaterialFragment newInstance(String param1, String param2) {
        AddMaterialFragment fragment = new AddMaterialFragment();
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
        return inflater.inflate(R.layout.fragment_add_material, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave = view.findViewById(R.id.btnAddMaterial);
        btnCancel = view.findViewById(R.id.btnCancel);
        edtId = view.findViewById(R.id.addMaterialId);
        edtName = view.findViewById(R.id.addMaterialName);
        edtWeight = view.findViewById(R.id.addMaterialWeight);
        edtQty = view.findViewById(R.id.addMaterialQty);
        progressBar = view.findViewById(R.id.progressBarMaterial);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddMaterial:
                progressBar.setVisibility(View.VISIBLE);
                String id = edtId.getText().toString().trim();
                String name = edtName.getText().toString().trim();
                int weight = stringToInt(edtWeight.getText().toString().trim());
                int qty = stringToInt(edtQty.getText().toString().trim());

                if (checkRule(id, name)) {
                    firebaseFirestore = FirebaseFirestore.getInstance();

                    Material material = new Material(id, name, weight, qty);

                    firebaseFirestore.collection("materials")
                            .add(material)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getActivity(), "Data material added", Toast.LENGTH_SHORT).show();
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
        }
    }

    protected boolean checkRule(String id, String name) {
        if (TextUtils.isEmpty(id)) {
            edtId.setError("ID is required");
            edtId.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(name)) {
            edtName.setError("Name is required");
            edtName.requestFocus();
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