package com.example.kusha.schoolbus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kusha.schoolbus.R;
import com.example.kusha.schoolbus.models.PaymentList;

import java.util.List;

/**
 * Created by kusha on 12/13/2016.
 */

public class PaidStudentAdapter extends RecyclerView.Adapter<PaidStudentAdapter.PaidStudentViewHolder>  {

    List<PaymentList> payments;
    Context context;
    LayoutInflater layoutInflater;

    public PaidStudentAdapter(Context context, List<PaymentList> payment) {
        this.payments = payment;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public PaidStudentAdapter.PaidStudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.payment_paid_card, parent, false);
        return new PaidStudentAdapter.PaidStudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PaidStudentAdapter.PaidStudentViewHolder holder, int position) {
        PaymentList paymentList = payments.get(position);
        holder.studentName.setText(paymentList.getStuName());
        holder.studentId.setText(paymentList.getStuID());
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public PaymentList getItem(int position){
        return payments.get(position);
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class PaidStudentViewHolder extends RecyclerView.ViewHolder {

        TextView studentName;
        TextView studentId;

        public PaidStudentViewHolder(final View itemView) {
            super(itemView);

            studentName = (TextView) itemView.findViewById(R.id.txtPayStuId);
            studentId = (TextView) itemView.findViewById(R.id.txtPayStuName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });

        }

    }
}
