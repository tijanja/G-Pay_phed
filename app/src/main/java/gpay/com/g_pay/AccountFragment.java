package gpay.com.g_pay;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by adetunji on 6/8/17.
 */

public class AccountFragment extends Fragment
{
    private FrameLayout payBtn;
    private String email,authToken;
    private TextView accountName,accountNumber,accountPhone;
    private AccountMeter accountMeter;
    private static CoordinatorLayout mainCoordinator;

    public static AccountFragment newInstance(AccountMeter accountMeter, String email__, String auth, CoordinatorLayout coord)
    {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putSerializable("email",email__);
        args.putSerializable("auth",auth);
        args.putSerializable("accountDetail",accountMeter);

        mainCoordinator = coord;

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

            email = getArguments().getString("email");
            authToken = getArguments().getString("auth");
            accountMeter = (AccountMeter) getArguments().getSerializable("accountDetail");

            View view = inflater.inflate(R.layout.account_fragment,container,false);
            accountName = (TextView)view.findViewById(R.id.accountName);
            accountPhone = (TextView)view.findViewById(R.id.accountPhone);
            accountNumber = (TextView)view.findViewById(R.id.accountNumber);
            payBtn = (FrameLayout) view.findViewById(R.id.payBtn);

            accountName.setText(accountMeter.getName());
            accountPhone.setText(accountMeter.getAddress());
            accountNumber.setText(accountMeter.getAccount());

            payBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    int[] screenPos = new int[2];
                    view.getLocationOnScreen(screenPos);


                    final Intent intent = new Intent(getActivity(), GpayPaymentActivity.class);
                    intent.putExtra("email",email);
                    intent.putExtra("authToken",authToken);
                    intent.putExtra("account_details",accountMeter);
                    /*intent.putExtra("pointX",screenPos[0]);
                    intent.putExtra("pointY",screenPos[1]);
                    intent.putExtra("scaleX",view.getWidth());
                    intent.putExtra("scaleY",view.getHeight());*/



                    mainCoordinator.animate().setDuration(250).scaleX(0.9f).scaleY(.9f).withStartAction(new Runnable() {
                        @Override
                        public void run()
                        {
                            getActivity().startActivityForResult(intent,1);
                            getActivity().overridePendingTransition(0,0);
                        }
                    }).start();
                }
            });


        return view;
    }

}
