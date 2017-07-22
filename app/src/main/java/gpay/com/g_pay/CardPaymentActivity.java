package gpay.com.g_pay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CardPaymentActivity extends AppCompatActivity implements OnDataReady
{

    private Spinner meterType2;
    private String mType=null,authToken,imei,sName,sAccount,sPhone,sAddress;
    private FrameLayout verify_for_save_btn;
    private TextView btnText,meterAccount,phone;
    private ProgressBar progressBar;
    private int measuredWidth,sCustomerid;
    private int btnCheck = 0,pointX,pointY,scaleX,scaleY;
    private SQLiteDatabase sqLiteDatabase;
    private AccountMeter accountMeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        Bundle bundle = getIntent().getExtras();
        authToken = bundle.getString("authToken");
        imei = bundle.getString("imei");





        verify_for_save_btn = (FrameLayout) findViewById(R.id.verify_for_save_btn);

        int[] screenLocation = new int[2];
        verify_for_save_btn.getLocationOnScreen(screenLocation);

        /*int vRight = pointX - screenLocation[0];
        int vTop = pointY - screenLocation[0];

        float vScaleX = (float) scaleX/verify_for_save_btn.getWidth();
        float vScaleY = (float) scaleY/verify_for_save_btn.getHeight();


        verify_for_save_btn.setPivotX(0);
        verify_for_save_btn.setPivotY(0);

        verify_for_save_btn.setTranslationY(vTop);
        verify_for_save_btn.setTranslationX(vRight);

        verify_for_save_btn.setScaleY(vScaleY);
        verify_for_save_btn.setScaleX(vScaleX);*/



        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        btnText = (TextView) findViewById(R.id.btnText);
        phone = (TextView) findViewById(R.id.benefPhone);
        meterAccount = (TextView) findViewById(R.id.benefMeterAccount);

        meterType2 = (Spinner) findViewById(R.id.meterType2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        meterType2.setAdapter(adapter);

        meterType2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 1:
                        mType = "postpaid";
                        break;

                    case 2:
                        mType = "prepaid";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void verify_acc_for_save(View view)
    {
        switch (btnCheck)
        {
            case 0:
                if(mType!=null && !meterAccount.getText().toString().isEmpty() && !phone.getText().toString().isEmpty())
                {

                    measuredWidth = verify_for_save_btn.getMeasuredWidth();
                    ValueAnimator anim = ValueAnimator.ofInt(measuredWidth,verify_for_save_btn.getMeasuredHeight());
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                    {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int val = (Integer) animation.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = verify_for_save_btn.getLayoutParams();
                            layoutParams.width = val;
                            verify_for_save_btn.requestLayout();

                        }

                    });
                    anim.setDuration(250);
                    anim.start();

                    btnText.animate().setDuration(250).alpha(0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            super.onAnimationEnd(animation);
                            progressBar.setVisibility(View.VISIBLE);

                            Map<String, Object> params = new HashMap<>();
                            params.put("dir","client-api/gpay");
                            params.put("authToken",authToken);
                            params.put("imei",imei);
                            params.put("mobileNumber", phone.getText().toString());
                            params.put("accountNumber", meterAccount.getText().toString());
                            params.put("accountType", mType);
                            params.put("disco","phedc");
                            params.put("method", Request.Method.POST);

                            new Connection(CardPaymentActivity.this,params,CardPaymentActivity.this);
                        }
                    });
                }
                else if(mType==null)
                {
                    Snackbar.make(phone,"Please select Account/Meter type",Snackbar.LENGTH_LONG).show();
                }
                else if(meterAccount.getText().toString().isEmpty())
                {
                    Snackbar.make(phone,"Please enter Account/Meter number",Snackbar.LENGTH_LONG).show();
                }
                else if(phone.getText().toString().isEmpty())
                {
                    Snackbar.make(phone,"Please enter your phone number",Snackbar.LENGTH_LONG).show();
                }

                break;

            case 1:
                btnCheck = 0;
                if(sCustomerid!=0 && sName!=null && sAccount!=null && sAddress!=null && sPhone!=null && mType!=null)
                {
                    if( saveToDatabase(sCustomerid,sName,sAccount,sAddress,sPhone,mType))
                    {
                        Intent intent = new Intent();
                        intent.putExtra("customerid",sCustomerid);
                        intent.putExtra("name",sName);
                        intent.putExtra("account",sAccount);
                        intent.putExtra("address",sAddress);
                        intent.putExtra("phone",sPhone);
                        intent.putExtra("mType",mType);

                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }
                    else
                    {
                        Snackbar.make(progressBar,"Error while saving data",Snackbar.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Snackbar.make(progressBar,"Please check, some fields are empty",Snackbar.LENGTH_LONG).show();
                }

                break;
        }

    }


    @Override
    public void dataReady(JSONObject jsonObject, Object object)
    {
        Log.e("eeeee",jsonObject.toString());
        LinearLayout accountDetailsView = (LinearLayout) findViewById(R.id.accountDetailsView);
        LinearLayout accountDetailsFormView = (LinearLayout) findViewById(R.id.accountDetailsFormView);

        TextView name = (TextView) findViewById(R.id.name);
        TextView phone__ = (TextView) findViewById(R.id.phone);
        TextView account_meter = (TextView) findViewById(R.id.account_meter);
        TextView address = (TextView) findViewById(R.id.address);


        try
        {
            JSONObject accountJson = jsonObject.getJSONObject("yourResponse");
            address.setText(accountJson.getString("address"));
            sCustomerid = accountJson.getInt("id");
            sAddress = accountJson.getString("address");
            sName = accountJson.getString("name");
            sPhone = phone.getText().toString();
            sAccount = meterAccount.getText().toString();

            account_meter.setText(meterAccount.getText().toString());
            phone__.setText(phone.getText().toString());
            name.setText(sName);

            btnText.setText("Save Account");
            accountDetailsFormView.setVisibility(View.INVISIBLE);
            accountDetailsView.setVisibility(View.VISIBLE);

            accountDetailsView.invalidate();

            scaleBackBtn();
            btnCheck=1;

        }
        catch (JSONException e)
        {
            scaleBackBtn();
           Log.e("json",e.getMessage());
        }
    }

    @Override
    public void onConnectionError(JSONObject error)
    {

        scaleBackBtn();
        try
        {
            Snackbar.make(progressBar,error.getString("description"),Snackbar.LENGTH_LONG).show();
        }
        catch (JSONException e) {
            FirebaseCrash.log(e.getMessage());

        }
        Log.e("",error.toString());
    }

    @Override
    public void onNoConnection(String message) {
        scaleBackBtn();
        Snackbar.make(progressBar,message,Snackbar.LENGTH_LONG).show();
    }


    public boolean saveToDatabase(int customerid,String name,String account,String address,String phone,String type)
    {
        Database database = new Database(this);

        if(database.insertValue(customerid,name,account,address,phone,type))
        {

            Snackbar.make(progressBar,"Account saved",Snackbar.LENGTH_LONG).show();
            return true;
        }
        else
        {
            Snackbar.make(progressBar,"Error while saving data",Snackbar.LENGTH_LONG).show();
            return false;
        }

    }

    @Override
    public void onBackPressed()
    {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void scaleBackBtn()
    {
        ValueAnimator anim = ValueAnimator.ofInt(verify_for_save_btn.getMeasuredHeight(),measuredWidth);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = verify_for_save_btn.getLayoutParams();
                layoutParams.width = val;
                verify_for_save_btn.requestLayout();
            }
        });

        anim.setDuration(250);
        anim.start();
        progressBar.setVisibility(View.GONE);

        btnText.animate().setDuration(250).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                verify_for_save_btn.setEnabled(true);

            }
        }).start();
    }
}