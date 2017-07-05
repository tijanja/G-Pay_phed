package gpay.com.g_pay;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by adetunji on 5/17/17.
 */

public class TransactionListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Object> transList;
    private int screenHight;

    public TransactionListAdapter(List<Object> items,int hight)
    {
        screenHight = hight;
        transList = items;
    }

    public class TransactionHolder extends RecyclerView.ViewHolder
    {
        TextView tan,amount,date,unit,meter;

        public TransactionHolder(View itemView)
        {
            super(itemView);
            amount = (TextView) itemView.findViewById(R.id.amount);
            date = (TextView) itemView.findViewById(R.id.date);
            unit = (TextView) itemView.findViewById(R.id.unit);
            meter = (TextView) itemView.findViewById(R.id.meter);
        }
    }

    public class FormHolder extends RecyclerView.ViewHolder
    {
        Spinner spinner;
        EditText account,phone;
        Button payBtn;

        public FormHolder(View itemView)
        {
            super(itemView);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;

        switch (viewType)
        {
            case 1:

                View view1 = inflater.inflate(R.layout.inline_pay_form,parent,false);
                holder = new FormHolder(view1);

                break;


            case 2:

                View view = inflater.inflate(R.layout.transaction_view,parent,false);

                view.setTranslationY(screenHight);
                view.animate().setDuration(800).translationY(1).setInterpolator(new DecelerateInterpolator(3.f)).start();
                holder = new TransactionHolder(view);

                break;
        }



        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        switch (holder.getItemViewType())
        {
            case 1:

                break;

            case 2:

                TransactionHolder transactionHolder = (TransactionHolder) holder;
                Transaction transaction = (Transaction) transList.get(position);
                NumberFormat format = NumberFormat.getNumberInstance();
                String amount = format.format(transaction.getAmount());
                transactionHolder.amount.setText("\u20A6"+amount);
                transactionHolder.date.setText(transaction.getDate());
                transactionHolder.unit.setText(transaction.getUnit()+" unit(s)");
                transactionHolder.meter.setText(transaction.getAccount());


                break;
        }


    }


    @Override
    public int getItemCount()
    {
        return transList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(transList.get(position) instanceof Transaction)
        {
            return 2;
        }

        if (transList.get(position) instanceof PayForm)
        {
            return 1;
        }
        return -1;
    }

    public void animateItem(View v)
    {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(v.getContext(), R.anim.recycler_item_anim);
        v.setAnimation(animAnticipateOvershoot);
    }
}
