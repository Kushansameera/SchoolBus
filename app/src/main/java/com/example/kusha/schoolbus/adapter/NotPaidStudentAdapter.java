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

public class NotPaidStudentAdapter extends RecyclerView.Adapter<NotPaidStudentAdapter.NotPaidStudentViewHolder>  {
    List<PaymentList> payments;
    Context context;
    LayoutInflater layoutInflater;

    public NotPaidStudentAdapter(Context context, List<PaymentList> payment) {
        this.payments = payment;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public NotPaidStudentAdapter.NotPaidStudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.payment_not_paid_card, parent, false);
        return new NotPaidStudentAdapter.NotPaidStudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotPaidStudentAdapter.NotPaidStudentViewHolder holder, int position) {
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

    public class NotPaidStudentViewHolder extends RecyclerView.ViewHolder {

        TextView studentName;
        TextView studentId;

        public NotPaidStudentViewHolder(final View itemView) {
            super(itemView);

            studentId = (TextView) itemView.findViewById(R.id.txtNotPayStuId);
            studentName = (TextView) itemView.findViewById(R.id.txtNotPayStuName);

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
