package com.example.uasshabri.dashboard.fragment.dailyreport;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.uasshabri.R;
import com.example.uasshabri.dashboard.fragment.model.Dailyreport;
import com.example.uasshabri.dashboard.fragment.model.Machine;
import com.example.uasshabri.dashboard.fragment.model.Material;
import com.example.uasshabri.dashboard.fragment.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddDailyreportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddDailyreportFragment extends Fragment implements View.OnClickListener {
    private EditText edtId, edtDate, edtCreatedBy, edtPlanQty, edtMinQty, edtTodayQty;
    private Button btnSave, btnCancel;
    private Spinner spnMaterial, spnMachine;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private List<String> machineList;
    private List<String> materialList;

    private String materialId, machineId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddDailyreportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddDailyreportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddDailyreportFragment newInstance(String param1, String param2) {
        AddDailyreportFragment fragment = new AddDailyreportFragment();
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
        return inflater.inflate(R.layout.fragment_add_dailyreport, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave = view.findViewById(R.id.btnAddDailyreport);
        btnCancel = view.findViewById(R.id.btnCancel);
        edtId = view.findViewById(R.id.addDailyreportId);
        edtDate = view.findViewById(R.id.addDailyreportDate);
        edtCreatedBy = view.findViewById(R.id.addDailyreportCreatedby);
        edtPlanQty = view.findViewById(R.id.addDailyreportPlanQty);
        edtMinQty = view.findViewById(R.id.addDailyreportMinQty);
        edtTodayQty = view.findViewById(R.id.addDailyreportTodayQty);
        spnMaterial = view.findViewById(R.id.addDailyreportMaterial);
        spnMachine = view.findViewById(R.id.addDailyreportMachine);
        progressBar = view.findViewById(R.id.progressBarDailyreport);

        initDateToday();
        initCreatedBy();
        initSpinnerMaterial();
        initSpinnerMachine();

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        edtDate.setKeyListener(null);
        edtCreatedBy.setKeyListener(null);
        edtTodayQty.setKeyListener(null);
    }

    private void initCreatedBy() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);

            documentReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    User user = documentSnapshot.toObject(User.class);
                                    edtCreatedBy.setText(user.getName());
                                } else {
                                    Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                                    edtCreatedBy.setText("-");
                                }
                            } else {
                                Toast.makeText(getActivity(), "User not found, " + task.getException(), Toast.LENGTH_SHORT).show();
                                edtCreatedBy.setText("-");
                            }
                        }
                    });
        }
    }

    private void generateUploadQr(String documentId) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(documentId, BarcodeFormat.QR_CODE, 500, 500);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference().child("qrs/" + documentId + ".jpg");
            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "QR upload failed, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (WriterException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initDateToday() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        edtDate.setText(makeDateString(day, month, year));
    }

    private String makeDateString(int day, int month, int year) {
        return day + "/" + month + "/" + year;
    }

    private void initSpinnerMachine() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        machineList = new ArrayList<>();
        ArrayAdapter<String> adapterMachine = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, machineList);
        adapterMachine.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnMachine.setAdapter(adapterMachine);
        spnMachine.setOnItemSelectedListener(new MachineSpinner());

        firebaseFirestore.collection("machines")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            machineList.clear();

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Machine machine = documentSnapshot.toObject(Machine.class);
                                machineList.add(machine.getId());
                            }
                            adapterMachine.notifyDataSetChanged();
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

    class MachineSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            machineId = machineList.get(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void initSpinnerMaterial() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        materialList = new ArrayList<>();
        ArrayAdapter<String> adapterMaterial = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, materialList);
        adapterMaterial.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnMaterial.setAdapter(adapterMaterial);
        spnMaterial.setOnItemSelectedListener(new MaterialSpinner());

        firebaseFirestore.collection("materials")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            materialList.clear();

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Material material = documentSnapshot.toObject(Material.class);
                                materialList.add(material.getId());
                            }
                            adapterMaterial.notifyDataSetChanged();
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

    class MaterialSpinner implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            materialId = materialList.get(i);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddDailyreport:
                progressBar.setVisibility(View.VISIBLE);
                String id = edtId.getText().toString().trim();
                String date = edtDate.getText().toString().trim();
                String create_by = edtCreatedBy.getText().toString().trim();
                int plan_qty = stringToInt(edtPlanQty.getText().toString().trim());
                int min_qty = stringToInt(edtMinQty.getText().toString().trim());
                int today_qty = 0;

                if (checkRule(id, plan_qty, min_qty)) {
                    firebaseFirestore = FirebaseFirestore.getInstance();

                    Dailyreport dailyreport = new Dailyreport(id, date, create_by, plan_qty, min_qty, today_qty, this.materialId, this.machineId);

                    firebaseFirestore.collection("daily-reports")
                            .add(dailyreport)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    generateUploadQr(documentReference.getId());
                                    Toast.makeText(getActivity(), "Data daily report added", Toast.LENGTH_SHORT).show();
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

    protected boolean checkRule(String id, int plan_qty, int min_qty) {
        if (TextUtils.isEmpty(id)) {
            edtId.setError("ID is required");
            edtId.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (plan_qty == 0) {
            edtPlanQty.setError("Plan qty must be 1 or higher");
            edtPlanQty.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (min_qty == 0) {
            edtMinQty.setError("Min qty must be 1 or higher");
            edtMinQty.requestFocus();
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