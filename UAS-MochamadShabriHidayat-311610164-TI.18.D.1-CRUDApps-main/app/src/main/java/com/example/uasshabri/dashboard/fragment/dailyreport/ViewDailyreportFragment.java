package com.example.uasshabri.dashboard.fragment.dailyreport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewDailyreportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewDailyreportFragment extends Fragment implements View.OnClickListener {
    private EditText edtId, edtDate, edtCreatedBy, edtPlanQty, edtMinQty, edtTodayQty, edtMaterial, edtMachine;
    private Button btnEdit, btnDelete;
    private FirebaseFirestore firebaseFirestore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewDailyreportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewDailyreportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewDailyreportFragment newInstance(String param1, String param2) {
        ViewDailyreportFragment fragment = new ViewDailyreportFragment();
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
        return inflater.inflate(R.layout.fragment_view_dailyreport, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtId = view.findViewById(R.id.viewDailyreportId);
        edtDate = view.findViewById(R.id.viewDailyreportDate);
        edtCreatedBy = view.findViewById(R.id.viewDailyreportCreatedby);
        edtPlanQty = view.findViewById(R.id.viewDailyreportPlanQty);
        edtMinQty = view.findViewById(R.id.viewDailyreportMinQty);
        edtTodayQty = view.findViewById(R.id.viewDailyreportTodayQty);
        edtMaterial = view.findViewById(R.id.viewDailyreportMaterial);
        edtMachine = view.findViewById(R.id.viewDailyreportMachine);
        btnEdit = view.findViewById(R.id.btnEditDailyreport);
        btnDelete = view.findViewById(R.id.btnDeleteDailyreport);

        edtId.setKeyListener(null);
        edtDate.setKeyListener(null);
        edtCreatedBy.setKeyListener(null);
        edtPlanQty.setKeyListener(null);
        edtMinQty.setKeyListener(null);
        edtTodayQty.setKeyListener(null);
        edtMaterial.setKeyListener(null);
        edtMachine.setKeyListener(null);
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

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
        String dailyreportDocument = getArguments().getString("uDocument");
        switch (view.getId()) {
            case R.id.btnEditDailyreport:
                Bundle bundleQr = new Bundle();
                bundleQr.putString("uDocument", dailyreportDocument);
                Navigation.findNavController(view).navigate(R.id.action_viewDailyreportFragment_to_updateDailyreportFragment, bundleQr);
                break;
            case R.id.btnDeleteDailyreport:
                showAlert(getActivity(), dailyreportDocument, view);
                break;
        }
    }

    public void showAlert(Context context, String document, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure want to delete this data?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteDataFirestore(document);
                        deleteDataQrStorage(document);

                        Navigation.findNavController(view).navigate(R.id.dailyreportFragment);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        builder.show();
    }

    public void deleteDataFirestore(String document) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("daily-reports").document(document);

        documentReference
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Data deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong, " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteDataQrStorage(String document) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("qrs/" + document + ".jpg");

        storageReference.delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong when trying to delete QR, " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}