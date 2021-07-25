package com.example.uasshabri.dashboard.fragment.dailyreport.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uasshabri.R;
import com.example.uasshabri.dashboard.fragment.model.Dailyreport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ListDailyreportsAdapter extends RecyclerView.Adapter<ListDailyreportsAdapter.ListViewHolder> {
    private List<Dailyreport> dailyreportList;
    private Context context;
    private List<String> dailyreportDocument;

    public ListDailyreportsAdapter(Context context, List<Dailyreport> dailyreportList, List<String> dailyreportDocument) {
        this.context = context;
        this.dailyreportList = dailyreportList;
        this.dailyreportDocument = dailyreportDocument;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_dailyreport, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListDailyreportsAdapter.ListViewHolder holder, int position) {
        holder.tvDailyreportId.setText(dailyreportList.get(position).getId());
        holder.tvDailyreportDate.setText(dailyreportList.get(position).getDate());
        holder.tvDailyreportDocument.setText(dailyreportDocument.get(position));
    }

    @Override
    public int getItemCount() {
        return dailyreportList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageButton btnQr;
        private RelativeLayout relativeLayout;
        private TextView tvDailyreportId, tvDailyreportDocument, tvDailyreportDate;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            btnQr = itemView.findViewById(R.id.qrDailyreport);
            tvDailyreportId = itemView.findViewById(R.id.dailyreportIdRow);
            tvDailyreportDate = itemView.findViewById(R.id.dailyreportDateRow);
            tvDailyreportDocument = itemView.findViewById(R.id.dailyreportDocumentRow);
            relativeLayout = itemView.findViewById(R.id.relativeClickDailyreport);

            btnQr.setOnClickListener(this);
            relativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.relativeClickDailyreport:
                    Bundle bundleView = new Bundle();
                    bundleView.putString("uDocument", tvDailyreportDocument.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.action_dailyreportFragment_to_viewDailyreportFragment, bundleView);
                    break;
                case R.id.qrDailyreport:
                    Bundle bundleQr = new Bundle();
                    bundleQr.putString("uDocument", tvDailyreportDocument.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.action_dailyreportFragment_to_qrDailyreportFragment, bundleQr);
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
                            Toast.makeText(context, "Data deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Something went wrong, " + e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, "Something went wrong when trying to delete QR, " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
