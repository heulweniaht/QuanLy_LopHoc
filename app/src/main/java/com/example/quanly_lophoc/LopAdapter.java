package com.example.quanly_lophoc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LopAdapter extends RecyclerView.Adapter<LopAdapter.LopViewHolder>{
    private Context context;
    private List<Lop> mList;
    private IClickListener listener;

    public interface IClickListener{
        void onClickUpdate(Lop lop);
        void onLongClickDelete(Lop lop);
    }

    public LopAdapter(Context context, List<Lop> mList ,IClickListener listener ){
        this.context = context;
        this.mList = mList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public LopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lop,parent,false);
        return new LopViewHolder(view);
    }
    public void onBindViewHolder(@NonNull LopViewHolder holder, int position) {
        Lop lop = mList.get(position);
        if(lop == null){
            return;
        }
        holder.tvMaLop.setText(lop.getMaLop());
        holder.tvTenLop.setText(lop.getTenLop());
        holder.tvNienKhoa.setText(lop.getNienKhoa());
        holder.tvTenNganh.setText("Ngành: "+ (lop.getTenNganh()!=null ? lop.getTenNganh() : ""));

        holder.layoutItem.setOnClickListener(v -> listener.onClickUpdate(lop));

        holder.layoutItem.setOnLongClickListener(v -> {
            listener.onLongClickDelete(lop);
            return true;
        });
    }

    @Override
    public int getItemCount(){
        return mList != null ? mList.size() : 0;
    }
    public class LopViewHolder extends RecyclerView.ViewHolder{
        TextView tvMaLop,tvTenLop,tvNienKhoa,tvTenNganh;
        CardView layoutItem;
        public LopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaLop = itemView.findViewById(R.id.tvMaLop);
            tvTenLop = itemView.findViewById(R.id.tvTenLop);
            tvNienKhoa = itemView.findViewById(R.id.tvNienKhoa);
            tvTenNganh = itemView.findViewById(R.id.tvTenNganh);
            layoutItem = itemView.findViewById(R.id.item_layout);
        }
    }
}
