package com.example.uasapriyandi.dashboard.fragment.material;

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

import com.example.uasapriyandi.R;
import com.example.uasapriyandi.dashboard.fragment.model.Material;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateMaterialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateMaterialFragment extends Fragment implements View.OnClickListener {
    private EditText edtId, edtName, edtWeight, edtQty;
    private Button btnEdit, btnCancel;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpdateMaterialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateMaterialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateMaterialFragment newInstance(String param1, String param2) {
        UpdateMaterialFragment fragment = new UpdateMaterialFragment();
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
        return inflater.inflate(R.layout.fragment_update_material, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnEdit = view.findViewById(R.id.btnEditMaterial);
        btnCancel = view.findViewById(R.id.btnCancelEdit);
        edtId = view.findViewById(R.id.editMaterialId);
        edtName = view.findViewById(R.id.editMaterialName);
        edtWeight = view.findViewById(R.id.editMaterialWeight);
        edtQty = view.findViewById(R.id.editMaterialQty);
        progressBar = view.findViewById(R.id.progressBarMaterial);

        initFirestoreData();

        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void initFirestoreData() {
        String materialDocument = getArguments().getString("uDocument");
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("materials").document(materialDocument);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Material material = documentSnapshot.toObject(Material.class);
                        edtId.setText(material.getId());
                        edtName.setText(material.getName());
                        edtWeight.setText("" + material.getWeight());
                        edtQty.setText("" + material.getQty());
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
            case R.id.btnEditMaterial:
                progressBar.setVisibility(View.VISIBLE);
                String id = edtId.getText().toString().trim();
                String name = edtName.getText().toString().trim();
                int weight = stringToInt(edtWeight.getText().toString().trim());
                int qty = stringToInt(edtQty.getText().toString().trim());

                if (checkRule(id, name)) {
                    String materialDocument = getArguments().getString("uDocument");

                    firebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = firebaseFirestore.collection("materials").document(materialDocument);

                    Material material = new Material(id, name, weight, qty);

                    documentReference
                            .update("id", material.getId(), "name", material.getName(), "weight", material.getWeight(), "qty", material.getQty())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Data Material edited", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    getActivity().onBackPressed();
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
                break;
            case R.id.btnCancelEdit:
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