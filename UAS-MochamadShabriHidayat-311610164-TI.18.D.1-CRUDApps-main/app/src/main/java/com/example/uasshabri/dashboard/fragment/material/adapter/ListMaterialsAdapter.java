package com.example.uasshabri.dashboard.fragment.material.adapter;

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
import com.example.uasshabri.dashboard.fragment.model.Material;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ListMaterialsAdapter extends RecyclerView.Adapter<ListMaterialsAdapter.ListViewHolder> {
    private List<Material> materialList;
    private Context context;
    private List<String > materialDocument;

    public ListMaterialsAdapter(Context context, List<Material> materials, List<String> materialDocument) {
        this.context = context;
        this.materialList = materials;
        this.materialDocument = materialDocument;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_material, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMaterialsAdapter.ListViewHolder holder, int position) {
        holder.tvMaterialId.setText(materialList.get(position).getName());
        holder.tvMaterialDocument.setText(materialDocument.get(position));
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageButton btnDelete, btnEdit;
        private LinearLayout linearLayout;
        private TextView tvMaterialId, tvMaterialDocument;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEdit = itemView.findViewById(R.id.editMaterial);
            btnDelete = itemView.findViewById(R.id.deleteMaterial);
            tvMaterialId = itemView.findViewById(R.id.materialIdRow);
            tvMaterialDocument = itemView.findViewById(R.id.materialDocumentRow);
            linearLayout = itemView.findViewById(R.id.linearClickMaterial);

            btnDelete.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
            linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.linearClickMaterial:
                    Bundle bundleView = new Bundle();
                    bundleView.putString("uDocument", tvMaterialDocument.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.action_materialFragment_to_viewMaterialFragment, bundleView);
                    break;
                case R.id.editMaterial:
                    Bundle bundleEdit = new Bundle();
                    bundleEdit.putString("uDocument", tvMaterialDocument.getText().toString());
                    Navigation.findNavController(view).navigate(R.id.action_materialFragment_to_updateMaterialFragment, bundleEdit);
                    break;
                case R.id.deleteMaterial:
                    showAlert(context, tvMaterialDocument.getText().toString(), view);
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

                            Navigation.findNavController(view).navigate(R.id.materialFragment);
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
            DocumentReference documentReference = firebaseFirestore.collection("materials").document(document);

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
