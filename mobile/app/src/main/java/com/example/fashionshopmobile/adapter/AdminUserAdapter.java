package com.example.fashionshopmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.model.AdminUser;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    public interface UserActionListener {
        void onView(AdminUser user);
        void onEdit(AdminUser user);
        void onToggleStatus(AdminUser user);
    }

    private final List<AdminUser> users = new ArrayList<>();
    private final Long currentAdminId;
    private final UserActionListener listener;

    public AdminUserAdapter(Long currentAdminId, UserActionListener listener) {
        this.currentAdminId = currentAdminId;
        this.listener = listener;
    }

    public void setUsers(List<AdminUser> newUsers) {
        users.clear();
        if (newUsers != null) {
            users.addAll(newUsers);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AdminUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtUserTitle;
        private final TextView txtEmail;
        private final TextView txtRoleStatus;
        private final Button btnView;
        private final Button btnEdit;
        private final Button btnLock;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserTitle = itemView.findViewById(R.id.txtUserTitle);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtRoleStatus = itemView.findViewById(R.id.txtRoleStatus);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnLock = itemView.findViewById(R.id.btnLock);
        }

        void bind(AdminUser user) {
            String name = safe(user.getFullName());
            if (name.isEmpty()) {
                name = "Chưa có tên";
            }

            txtUserTitle.setText("#" + user.getId() + " - " + name);
            txtEmail.setText(safe(user.getEmail()));
            txtRoleStatus.setText(getRoleLabel(user.getRole()) + " | " + getStatusLabel(user.getStatus()));

            boolean locked = "LOCKED".equalsIgnoreCase(user.getStatus());
            btnLock.setText(locked ? "Mở khóa" : "Khóa");

            boolean isCurrentAdmin = currentAdminId != null && currentAdminId.equals(user.getId());
            btnLock.setVisibility(isCurrentAdmin ? View.GONE : View.VISIBLE);

            btnView.setOnClickListener(v -> listener.onView(user));
            btnEdit.setOnClickListener(v -> listener.onEdit(user));
            btnLock.setOnClickListener(v -> listener.onToggleStatus(user));
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String getRoleLabel(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "Quản trị viên";
        }
        return "Khách hàng";
    }

    private String getStatusLabel(String status) {
        if ("LOCKED".equalsIgnoreCase(status)) {
            return "Đã khóa";
        }
        return "Đang hoạt động";
    }
}
