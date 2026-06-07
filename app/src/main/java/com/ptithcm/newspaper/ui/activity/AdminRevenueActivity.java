package com.ptithcm.newspaper.ui.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import com.ptithcm.newspaper.R;
import com.ptithcm.newspaper.data.remote.BackendApiClient;
import com.ptithcm.newspaper.data.remote.UserApi;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRevenueActivity extends AppCompatActivity {
    private TextView tvTotalRevenue, tvOverallRevenue, tvSelectedDate, tvDailyTitle;
    private ImageView btnPickDate, btnClearFilter;
    private RecyclerView recyclerTransactions;
    private TransactionAdapter adapter;
    private List<UserApi.Transaction> transactionList = new ArrayList<>();
    
    private String selectedDate = null; // YYYY-MM-DD format

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_revenue);

        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvOverallRevenue = findViewById(R.id.tvOverallRevenue);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvDailyTitle = findViewById(R.id.tvDailyTitle);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnClearFilter = findViewById(R.id.btnClearFilter);
        
        recyclerTransactions = findViewById(R.id.recyclerTransactions);
        recyclerTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter();
        recyclerTransactions.setAdapter(adapter);

        btnPickDate.setOnClickListener(v -> showDatePicker());
        
        btnClearFilter.setOnClickListener(v -> {
            selectedDate = null;
            tvSelectedDate.setText("Lịch sử giao dịch");
            tvDailyTitle.setText("THEO NGÀY");
            btnClearFilter.setVisibility(View.GONE);
            loadRevenue();
        });

        loadRevenue();
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // month is 0-indexed
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdfAPI = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            
            selectedDate = sdfAPI.format(calendar.getTime());
            tvSelectedDate.setText("Ngày: " + sdfDisplay.format(calendar.getTime()));
            tvDailyTitle.setText(sdfDisplay.format(calendar.getTime()));
            btnClearFilter.setVisibility(View.VISIBLE);
            
            loadRevenue();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void loadRevenue() {
        UserApi userApi = BackendApiClient.getClient().create(UserApi.class);
        userApi.getRevenue(selectedDate).enqueue(new Callback<UserApi.RevenueResponse>() {
            @Override
            public void onResponse(Call<UserApi.RevenueResponse> call, Response<UserApi.RevenueResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserApi.RevenueResponse res = response.body();
                    
                    NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                    tvOverallRevenue.setText(formatter.format(res.overall_revenue) + " đ");
                    tvTotalRevenue.setText(formatter.format(res.total_revenue) + " đ");

                    transactionList.clear();
                    if (res.transactions != null && !res.transactions.isEmpty()) {
                        transactionList.addAll(res.transactions);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminRevenueActivity.this, "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserApi.RevenueResponse> call, Throwable t) {
                Toast.makeText(AdminRevenueActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UserApi.Transaction t = transactionList.get(position);
            holder.tvTxUser.setText(t.username);
            holder.tvTxDesc.setText(t.description);
            holder.tvTxDate.setText(t.created_at);
            
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            holder.tvTxAmount.setText("+" + formatter.format(t.amount) + " đ");
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTxUser, tvTxDesc, tvTxDate, tvTxAmount;
            ViewHolder(View itemView) {
                super(itemView);
                tvTxUser = itemView.findViewById(R.id.tvTxUser);
                tvTxDesc = itemView.findViewById(R.id.tvTxDesc);
                tvTxDate = itemView.findViewById(R.id.tvTxDate);
                tvTxAmount = itemView.findViewById(R.id.tvTxAmount);
            }
        }
    }
}
