package com.example.fashionshopmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.model.UserAddress;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<UserAddress> addressList = new ArrayList<>();
    private OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onEdit(UserAddress address);
        void onDelete(UserAddress address);
        void onSetDefault(UserAddress address);
    }

    public AddressAdapter(OnAddressClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<UserAddress> addresses) {
        this.addressList = addresses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        UserAddress address = addressList.get(position);

        holder.tvReceiverName.setText(address.getReceiverName());
        holder.tvReceiverPhone.setText(address.getReceiverPhone());
        holder.tvFullAddress.setText(buildFullAddress(address));

        boolean isDefault = Boolean.TRUE.equals(address.getDefaultAddress());

        holder.tvDefault.setVisibility(isDefault ? View.VISIBLE : View.GONE);
        holder.btnSetDefault.setVisibility(isDefault ? View.GONE : View.VISIBLE);

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(address));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(address));
        holder.btnSetDefault.setOnClickListener(v -> listener.onSetDefault(address));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    private String buildFullAddress(UserAddress address) {
        StringBuilder builder = new StringBuilder();

        if (address.getAddressDetail() != null) {
            builder.append(address.getAddressDetail());
        }

        if (address.getWard() != null && !address.getWard().isEmpty()) {
            builder.append(", ").append(address.getWard());
        }

        if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
            builder.append(", ").append(address.getDistrict());
        }

        if (address.getProvince() != null && !address.getProvince().isEmpty()) {
            builder.append(", ").append(address.getProvince());
        }

        return builder.toString();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverName, tvReceiverPhone, tvFullAddress, tvDefault;
        Button btnEdit, btnDelete, btnSetDefault;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvReceiverPhone = itemView.findViewById(R.id.tvReceiverPhone);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            tvDefault = itemView.findViewById(R.id.tvDefault);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
        }
    }
}