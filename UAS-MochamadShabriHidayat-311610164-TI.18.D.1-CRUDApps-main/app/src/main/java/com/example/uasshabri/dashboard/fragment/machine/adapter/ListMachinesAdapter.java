package com.example.uasshabri.dashboard.fragment.machine.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uasshabri.R;
import com.example.uasshabri.dashboard.fragment.model.Machine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ListMachinesAdapter extends RecyclerView.Adapter<ListMachinesAdapter.ListViewHolder> {
    private List<Machine> machineList;
    private Context context;
    private List<String > machineDocument;

    public ListMachinesAdapter(Context context, List<Machine> machines, List<String> machineDocument) {
        this.context = context;
        this.machineList = machines;
        this.machineDocument = machineDocument;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_machine, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMachinesAdapter.ListViewHolder holder, int position) {
        holder.tvMachineId.setText(machineList.get(position).getName());
        holder.tvMachineDocument.setText(machineDocument.get(position));
    }

    @Override
    public int getItemCount() {
        return machineList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageButton btnDelete, btnEdit;
        private LinearLayout linearLayout;
        private TextView tvMachineId, tvMachineDocument;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEdit = itemView.findViewById(R.id.editMachine);
            btnDelete = itemView.findViewById(R.id.deleteMachine);
            tvMachineId = itemView.findViewById(R.id.machineIdRow);
            tvMachineDocument = itemView.findViewById(R.id.machineDocumentRow);
            linearLayout = itemView.findViewById(R.id.linearClickMachine);

            btnDelete.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
            linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.linearClickMachine:
                    Bundle bundleView = new Bundle();
                    bundleView.putString("uDocument", tvMachineDocument.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.action_machineFragment_to_viewMachineFragment, bundleView);
                    break;
                case R.id.editMachine:
                    Bundle bundleEdit = new Bundle();
                    bundleEdit.putString("uDocument", tvMachineDocument.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.action_machineFragment_to_updateMachineFragment, bundleEdit);
                    break;
                case R.id.deleteMachine:
                    showAlert(context, tvMachineDocument.getText().toString(), view);
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

                            Navigation.findNavController(view).navigate(R.id.machineFragment);
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
            DocumentReference documentReference = firebaseFirestore.collection("machines").document(document);

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
    }
}
