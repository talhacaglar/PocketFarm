package com.tarlamcebimde.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tarlamcebimde.app.R;
import com.tarlamcebimde.app.model.User;
import com.tarlamcebimde.app.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment {
    private RecyclerView rv;
    private TextView tvEmpty;
    private UserRepository repo;
    private AdminUserAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inf.inflate(R.layout.fragment_admin_list, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        rv = v.findViewById(R.id.rv_admin_list);
        tvEmpty = v.findViewById(R.id.tv_empty);
        repo = new UserRepository();
        adapter = new AdminUserAdapter(new ArrayList<>(), user -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Kullanıcı Sil")
                    .setMessage(user.getFullName() + " silinsin mi?")
                    .setPositiveButton("Evet", (d, w) -> repo.deleteUser(user.getUserId())
                            .addOnSuccessListener(x -> Toast.makeText(requireContext(), "Silindi", Toast.LENGTH_SHORT).show()))
                    .setNegativeButton("Hayır", null).show();
        });
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);
        repo.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            if (users == null || users.isEmpty()) { tvEmpty.setVisibility(View.VISIBLE); rv.setVisibility(View.GONE); }
            else { tvEmpty.setVisibility(View.GONE); rv.setVisibility(View.VISIBLE); adapter.updateUsers(users); }
        });
    }

    // İç adapter sınıfı
    static class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.VH> {
        private List<User> users;
        private final OnDeleteListener listener;
        interface OnDeleteListener { void onDelete(User user); }

        AdminUserAdapter(List<User> users, OnDeleteListener listener) { this.users = users; this.listener = listener; }
        void updateUsers(List<User> users) { this.users = users; notifyDataSetChanged(); }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_admin_user, p, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) { h.bind(users.get(pos), listener); }
        @Override public int getItemCount() { return users.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvRole;
            View btnDelete;
            VH(View v) { super(v); tvName = v.findViewById(R.id.tv_name); tvEmail = v.findViewById(R.id.tv_email); tvRole = v.findViewById(R.id.tv_role); btnDelete = v.findViewById(R.id.btn_delete); }
            void bind(User u, OnDeleteListener l) {
                tvName.setText(u.getFullName()); tvEmail.setText(u.getEmail());
                String role = u.isSeller() ? "Satıcı" : u.isBuyer() ? "Alıcı" : "Yönetici";
                tvRole.setText(role);
                btnDelete.setOnClickListener(v -> l.onDelete(u));
            }
        }
    }
}
