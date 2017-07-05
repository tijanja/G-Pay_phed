package gpay.com.g_pay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import android.view.View.OnKeyListener;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GpayPaymentActivity extends AppCompatActivity implements OnDataReady
{

    private LinearLayout paymentCard,cardPaymentView,paymentForm,otpView,otpViewSub,successView,successViewPrepaid,transNotSuccessful;
    private Spinner meterType,meterType2;
    private FrameLayout mainPayFrame,benefDetailsView;
    private CardView beneficiaryForm,cardView;
    private Toolbar toolbar;
    private FrameLayout saveBeneficiaryBtn;
    private EditText benefMeterAccount,benefPhone,cardCVV,cardYear,cardMonth,cardNumber,cardOTP,benefAmount;
    private String authToken,email,mType=null,payRef=null;
    private TextView benefName,benefAddress,benefOutstanding,__benefPhone,successAccountMeter,amountPaidPrepaid,successMeter,tokenString,transType,accountName,unit,prepaidTransDate;
    private boolean accountSaveMode = false;
    private ImageView checkbox;
    private TextView balanceDetails,amountPaid,successAccountType,prevOut,curOut;
    private ProgressBar progressbar;
   // private final String API_KEY = "20356a01-f053-45bd-a2ec-3fadd71d56c5";
    //private final String MERCHANT_ID = "KM2GFSDXVEOEUPEGVFTCQ";

    private final String API_KEY = "8e056aa1-312c-4c06-8ba6-ab97d4f2298d";
    private final String MERCHANT_ID = "RKFVRAFGTKS2IY96ZHORGW";

    private String EncKey;
    private String AuthToken;
    private String transactionRef;
    private  String otpID,name,phone,amount,accountMeter,transDate,tokenUnit;
    private ProgressDialog dialog;
    private AccountMeter customerAccount;
    private int customerId;
    private String current = "";
    private String cleanString;
    String a;
    int keyDel;


   // private EditText

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpay_payment);

        Bundle bundle = getIntent().getExtras();
        authToken = bundle.getString("authToken");
        email = bundle.getString("email");
        customerAccount = (AccountMeter) bundle.getSerializable("account_details");
        customerId = customerAccount.getCustomerid();

        /*customerAccount = (AccountMeter) bundle.getSerializable("account_details");
        pointX = bundle.getInt("pointX");
        pointY = bundle.getInt("pointY");

        scaleX = bundle.getInt("scaleX");
        scaleY = bundle.getInt("scaleY");*/
        saveBeneficiaryBtn = (FrameLayout) findViewById(R.id.verify_to_pay);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Pay Bill");
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        cardView = (CardView) findViewById(R.id.paymentCard);
       // meterType = (Spinner) findViewById(R.id.meterType);

        amountPaidPrepaid = (TextView) findViewById(R.id.amountPaidPrepaid);
        successMeter = (TextView) findViewById(R.id.successMeter);
        tokenString = (TextView) findViewById(R.id.tokenString);
        transType = (TextView) findViewById(R.id.transType);
        accountName = (TextView) findViewById(R.id.accountName);

        prepaidTransDate = (TextView) findViewById(R.id.prepaidTransDate);
        unit = (TextView) findViewById(R.id.unit);

        cardPaymentView = (LinearLayout) findViewById(R.id.cardPaymentView);
        beneficiaryForm = (CardView) findViewById(R.id.beneficiaryForm);
        //paymentForm = (LinearLayout) findViewById(R.id.paymentForm);
        benefMeterAccount = (EditText) findViewById(R.id.benefMeterAccount);
        benefPhone = (EditText) findViewById(R.id.benefPhone);

        mainPayFrame = (FrameLayout) findViewById(R.id.mainPayFrame);
        meterType2 = (Spinner) findViewById(R.id.meterType2);

        successViewPrepaid = (LinearLayout) findViewById(R.id.successViewPrepaid);



        benefName = (TextView) findViewById(R.id.benefName);
        benefAddress = (TextView) findViewById(R.id.benefAddress);
        benefOutstanding = (TextView) findViewById(R.id.benefOutstanding);
        __benefPhone = (TextView) findViewById(R.id.__benefPhone);

        benefDetailsView = (FrameLayout) findViewById(R.id.benefDetailsView);
        otpViewSub = (LinearLayout) findViewById(R.id.otpViewSub);
        successView = (LinearLayout) findViewById(R.id.successView);
        transNotSuccessful = (LinearLayout) findViewById(R.id.transNotSuccessful);

        checkbox = (ImageView) findViewById(R.id.checkbox);
        balanceDetails = (TextView) findViewById(R.id.balanceDetails);

        benefAmount = (EditText) findViewById(R.id.benefAmount);
        benefAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(!s.toString().equals(current)) {
                    benefAmount.removeTextChangedListener(this);

                    cleanString = s.toString().replaceAll("[$,.]", "");

                    try {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                        current = formatted.replace(NumberFormat.getCurrencyInstance().getCurrency().getSymbol(Locale.getDefault()), "");
                        benefAmount.setText(current);
                        benefAmount.setSelection(current.length());


                    } catch (Exception e) {
                        //notify.show(e.getMessage());
                    }
                    benefAmount.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        cardCVV = (EditText) findViewById(R.id.cardCVV);
        cardYear = (EditText) findViewById(R.id.cardYear);
        cardMonth = (EditText) findViewById(R.id.cardMonth);
        cardNumber = (EditText) findViewById(R.id.cardNumber);


        cardNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                boolean flag = true;
                String eachBlock[] = cardNumber.getText().toString().split("-");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 4) {
                        flag = false;
                    }
                }
                if (flag) {

                    cardNumber.setOnKeyListener(new OnKeyListener() {

                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    if (keyDel == 0) {

                        if (((cardNumber.getText().length() + 1) % 5) == 0) {

                            if (cardNumber.getText().toString().split("-").length <= 3) {
                                cardNumber.setText(cardNumber.getText() + "-");
                                cardNumber.setSelection(cardNumber.getText().length());
                            }
                        }
                        a = cardNumber.getText().toString();
                    } else {
                        a = cardNumber.getText().toString();
                        keyDel = 0;
                    }

                } else {
                    cardNumber.setText(a);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        otpView = (LinearLayout) findViewById(R.id.otpView);
        cardOTP = (EditText) findViewById(R.id.cardOTP);

        amountPaid = (TextView) findViewById(R.id.amountPaid);
        successAccountType = (TextView) findViewById(R.id.successAccountType);

        curOut = (TextView) findViewById(R.id.curOut);
        prevOut = (TextView) findViewById(R.id.prevOut);
        successAccountMeter = (TextView) findViewById(R.id.successAccountMeter);

        benefName.setText(customerAccount.getName());
        benefAddress.setText(customerAccount.getAddress());
        __benefPhone.setText(customerAccount.getPhone());

        getTransactionRef();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //meterType.setAdapter(adapter);
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

        ViewTreeObserver observer = cardView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw()
            {
                cardView.getViewTreeObserver().removeOnPreDrawListener(this);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                cardView.setPivotY(0);
                cardView.setTranslationY(height);
                //cardView.setAlpha(0);

                cardView.animate().setDuration(500).translationY(1).start();


                /*int[] screenLocation = new int[2];
                saveBeneficiaryBtn.getLocationOnScreen(screenLocation);

                int vRight = pointX - screenLocation[0];
                int vTop = pointY - screenLocation[1];

                float vScaleX = (float) scaleX/saveBeneficiaryBtn.getWidth();
                float vScaleY = (float) scaleY/saveBeneficiaryBtn.getHeight();


                saveBeneficiaryBtn.setPivotX(0);
                saveBeneficiaryBtn.setPivotY(0);

                saveBeneficiaryBtn.setTranslationY(vTop);
                saveBeneficiaryBtn.setTranslationX(vRight);

                saveBeneficiaryBtn.setScaleY(vScaleY);
                saveBeneficiaryBtn.setScaleX(vScaleX);*/



                return true;
            }
        });
    }


    public void saveBeneficiary(View view)
    {
          if(mType!=null && !TextUtils.isEmpty(benefMeterAccount.getText().toString()) && !TextUtils.isEmpty(benefPhone.getText().toString()))
            {
                dialog.setTitle("Verifying customer\'s details");
                dialog.setMessage("Please wait...");
                dialog.show();

                accountMeter = benefMeterAccount.getText().toString();
                phone = benefPhone.getText().toString();

                Map<String,String> params = new HashMap<>();
                params.put("dir","gpay");
                params.put("authToken",authToken);
                //params.put("imei",imei);
                params.put("mobileNumber", benefPhone.getText().toString());
                params.put("accountNumber", benefMeterAccount.getText().toString());
                params.put("accountType", mType);
                //params.put("mobileNumber", "07011389064");
                //params.put("accountNumber", "862114154201");
                //params.put("accountType", "postpaid");
                params.put("disco","phedc");

                new ConnectionTest(this,params,this);
            }
            else if(mType==null)
            {
                Snackbar.make(cardView,"Please select Meter/Account type",Snackbar.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(benefMeterAccount.getText().toString()))
            {
                if(mType.equals("prepaid"))
                {
                    Snackbar.make(cardView,"Please enter Meter number",Snackbar.LENGTH_SHORT).show();
                }
                else if(mType.equals("postpaid"))
                {
                    Snackbar.make(cardView,"Please enter Account number",Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    Snackbar.make(cardView,"Please enter Account/Meter",Snackbar.LENGTH_SHORT).show();
                }

            }
            else if(TextUtils.isEmpty(benefPhone.getText().toString()))
            {
                Snackbar.make(cardView,"Please select Meter/Account type",Snackbar.LENGTH_SHORT).show();
            }


    }

    @Override
    public void dataReady(JSONObject jsonObject, Object object)
    {
        try
        {
            dialog.dismiss();
            //Log.e("Gpay",jsonObject.toString());
            JSONObject yourObj = jsonObject.getJSONObject("yourResponse");
            switch (mType)
            {
                case "postpaid":

                    benefName.setText(yourObj.getString("name"));
                    benefAddress.setText(yourObj.getString("address"));
                    benefOutstanding.setText(yourObj.getString("outstanding"));
                    __benefPhone.setText(benefPhone.getText().toString());
                    payRef = yourObj.getString("payRef");
                    break;

                case "prepaid":
                    benefName.setText(yourObj.getString("name"));
                    balanceDetails.setText("Mini/Total Payable");
                    benefAddress.setText(yourObj.getString("address"));
                    __benefPhone.setText(benefPhone.getText().toString());
                    benefOutstanding.setText(yourObj.getString("minimumPayable")+" / "+yourObj.getString("totalPayable"));
                    payRef = yourObj.getString("payRef");
                    break;
            }



            slideLayout();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionError(JSONObject error)
    {
        try
        {
            Snackbar.make(cardView,error.getString("description"),Snackbar.LENGTH_LONG).show();
            dialog.dismiss();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onNoConnection(String message)
    {
        Snackbar.make(cardView,message,Snackbar.LENGTH_LONG).show();
        dialog.dismiss();
    }

    public void slideLayout()
    {
        if(mainPayFrame.isShown())
        {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            //mainPayFrame.animate().setDuration(500).scaleY(0).start();
            benefDetailsView.setTranslationX((float)width);
            benefDetailsView.setVisibility(View.VISIBLE);
            beneficiaryForm.animate().setDuration(1500).translationX((float)-width).start();
            benefDetailsView.animate().setDuration(800).translationX(1).withEndAction(new Runnable() {
                @Override
                public void run() {
                    toolbar.setTitle("Create Beneficiary");
                }
            }).start();
        }
    }

    public void toggleCheckbox(View v)
    {
        if(accountSaveMode)
        {
            checkbox.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_benef_save_disabled));
            checkbox.invalidate();
            accountSaveMode = false;
        }
        else
        {
            checkbox.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_benef_save_enabled));
            checkbox.invalidate();
            accountSaveMode = true;

        }
    }


    public void paymantView(View v)
    {


        if(!TextUtils.isEmpty(benefAmount.getText().toString()))
        {
            dialog.setTitle("Payment");
            dialog.setMessage("Connecting to gateway");
            dialog.show();
            amount = benefAmount.getText().toString();
            name = benefName.getText().toString();

            Map<String,String> params = new HashMap<>();
            params.put( "MerchantId",MERCHANT_ID);
            params.put("ApiKey",API_KEY);
            params.put("PaymentDescription", "PHED Bill");
            params.put("dir","sdk/init");
            params.put("TransId",payRef);
            params.put("CustomerEmail",email);
            params.put("CustomerName",benefName.getText().toString());
            params.put("amount",amount+"");

            new PaymentConnection(this, params, new OnDataReady() {
                @Override
                public void dataReady(JSONObject jsonObject, Object object)
                {
                    dialog.dismiss();
                    if(benefDetailsView.isShown())
                    {
                        benefDetailsView.setVisibility(View.INVISIBLE);
                        cardPaymentView.setVisibility(View.VISIBLE);
                        cardPaymentView.invalidate();
                        toolbar.setTitle("Enter card details");

                        try
                        {
                            EncKey = jsonObject.getString("EncKey");
                            AuthToken = jsonObject.getString("AuthToken");
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        Log.e("response==",jsonObject.toString());
                    }
                }

                @Override
                public void onConnectionError(JSONObject error)
                {
                    dialog.dismiss();
                    Snackbar.make(cardView,"Error please try again later",Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onNoConnection(String message)
                {
                    dialog.dismiss();
                    Snackbar.make(cardView,message,Snackbar.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            Snackbar.make(cardView,"Please enter amount",Snackbar.LENGTH_LONG).show();
        }


    }

    public void payWithCard(View v)
    {
        if(!TextUtils.isEmpty(cardNumber.getText().toString()) && !TextUtils.isEmpty(cardMonth.getText().toString()) && !TextUtils.isEmpty(cardYear.getText().toString()) && !TextUtils.isEmpty(cardCVV.getText().toString()))
        {
            dialog.setTitle("Payment");
            dialog.setMessage("Please wait...");
            dialog.show();

            try
            {
                String encCardNum = EncryptionHelper.encrypt(cardNumber.getText().toString().replace("-",""),EncKey);
                String encCardM = EncryptionHelper.encrypt(cardMonth.getText().toString(),EncKey);
                String encCardY = EncryptionHelper.encrypt(cardYear.getText().toString(),EncKey);
                String encCVV = EncryptionHelper.encrypt(cardCVV.getText().toString(),EncKey);

                Map<String,String> params = new HashMap<>();
                params.put("dir","sdk/card/charge");
                params.put("MerchantId",MERCHANT_ID);
                params.put("AuthToken",AuthToken);
                params.put("EncCARD",encCardNum);
                params.put("EncEXPMONTH",encCardM);
                params.put("EncEXPYEAR",encCardY);
                params.put("EncCVV",encCVV);
                params.put("EncPIN",encCVV);
                params.put("MaskedPan",maskedNumber(cardNumber.getText().toString()));




                //5060990580000217499
                new PaymentConnection(this, params, new OnDataReady()
                {
                    @Override
                    public void dataReady(JSONObject jsonObject, Object object)
                    {
                        Log.e("new card",jsonObject.toString());

                        try
                        {
                            switch(jsonObject.getString("StatusCode"))
                            {
                                case "00":

                                    transactionRef = jsonObject.getString("TransactionRef");


                                         verifyCardPayment();



                                    break;

                                case "A11":

                                    dialog.dismiss();
                                    benefDetailsView.setVisibility(View.VISIBLE);
                                    cardPaymentView.setVisibility(View.INVISIBLE);
                                    Snackbar.make(cardView,jsonObject.getString("StatusDesc"),3000).show();
                                    break;
                            }
                        }
                        catch (JSONException e)
                        {
                            Log.e("error-tunji66666",e.getMessage());
                        }

                    }

                    @Override
                    public void onConnectionError(JSONObject error)
                    {
                        dialog.dismiss();
                        benefDetailsView.setVisibility(View.VISIBLE);
                        cardPaymentView.setVisibility(View.INVISIBLE);

                        Snackbar.make(cardView,"Error please try again",Snackbar.LENGTH_LONG).show();

                    }

                    @Override
                    public void onNoConnection(String message)
                    {
                        dialog.dismiss();
                        Snackbar.make(cardView,message,Snackbar.LENGTH_LONG).dismiss();
                    }
                });
            }
            catch (Exception e)
            {

                Log.e("exception",e.getMessage());
            }
        }
    }

    private String maskedNumber(String cardNumber){
        String starts = null;
        String leadNumber = cardNumber.substring(0,6);
        String lastFourLetter = cardNumber.substring(cardNumber.length() - 4);
        for(int i=0;i<(cardNumber.length()-10);i++)
        {
            if(starts==null)
            {
                starts ="*";
            }
            else
            {
                starts +="*";
            }

        }
        String result = leadNumber + starts + lastFourLetter;

        return result;
    }

    public void confirmOTPPayment(View v) throws Exception {

        if(!TextUtils.isEmpty(cardOTP.getText().toString()))
        {
            dialog.setTitle("Confirming OTP");
            dialog.setMessage("Please wait...");
            dialog.show();

            Map<String,String> params = new HashMap<>();
            params.put("dir","sdk/card/validate");
            params.put("MerchantId",MERCHANT_ID);
            params.put("AuthToken",AuthToken);
            params.put("TransactionRef",transactionRef);
            params.put("EncOTP",EncryptionHelper.encrypt(cardOTP.getText().toString(),EncKey));
            params.put("EncOTPID",EncryptionHelper.encrypt(otpID,EncKey));

            new PaymentConnection(this, params, new OnDataReady()
            {
                @Override
                public void dataReady(JSONObject jsonObject, Object object)
                {
                    try
                    {
                        switch (jsonObject.getString("StatusCode"))
                        {
                            case "00":
                                dialog.setTitle("Connecting to PHED");
                                dialog.setMessage("Logging payment, please wait...");

                            Map<String,Object> payParams = new HashMap<>();
                            payParams.put("dir","gpay");
                            payParams.put("authToken",authToken);
                            //payParams.put("imei",imei);
                            payParams.put("channel","4");
                            payParams.put("mode","1");
                            payParams.put("disco","phedc");
                            payParams.put("accountType",mType);
                            payParams.put("payerName",name);
                            payParams.put("amount",amount);
                            payParams.put("accountNumber",accountMeter);
                            payParams.put("payRef",payRef);
                            payParams.put("mobileNumber",phone);
                            payParams.put("method",Request.Method.POST);

                            new Connection(GpayPaymentActivity.this, payParams, new OnDataReady() {
                                @Override
                                public void dataReady(JSONObject jsonObject, Object object)
                                {
                                    try
                                    {
                                        Log.e("eeee",jsonObject.toString());

                                        switch (jsonObject.getString("message"))
                                        {
                                            case "00":
                                                dialog.dismiss();
                                                progressbar.setVisibility(View.GONE);
                                                otpViewSub.setVisibility(View.GONE);
                                                successView.setVisibility(View.VISIBLE);
                                                successView.invalidate();
                                                toolbar.setTitle("SuccessFul");

                                                JSONObject yourJsonObj = jsonObject.getJSONObject("yourResponse");
                                                double payAmount = Double.parseDouble(yourJsonObj.getString("payment_amount"))*100;

                                                amountPaid.setText("\u20A6"+payAmount+"");
                                                successAccountMeter.setText(yourJsonObj.getString("account_no"));

                                                successAccountType.setText(jsonObject.getString(""));

                                                prevOut.setText("Pre  "+"\u20A6"+yourJsonObj.getString("outstanding_prev"));
                                                curOut.setText("Cur  "+"\u20A6"+yourJsonObj.getString("outstanding"));

                                                transDate = yourJsonObj.getString("payment_time");


                                                break;

                                            default:
                                                dialog.dismiss();
                                                Snackbar.make(cardView,jsonObject.getString("message"),Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onConnectionError(JSONObject error) {

                                    dialog.dismiss();
                                    try
                                    {
                                        Log.e("card error",error.toString());
                                        Snackbar.make(cardView,error.getString("description"),Snackbar.LENGTH_LONG).show();
                                    }
                                    catch (JSONException e) {

                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onNoConnection(String message)
                                {
                                    dialog.dismiss();
                                    Snackbar.make(cardView,message,Snackbar.LENGTH_LONG).show();
                                }
                            });

                                break;
                        }

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    Log.e("otp===",jsonObject.toString());
                }

                @Override
                public void onConnectionError(JSONObject error)
                {
                    try
                    {
                        dialog.dismiss();
                        Snackbar.make(cardView,error.getString("Message"),Snackbar.LENGTH_LONG).show();
                        /*Button btn = new Button(GpayPaymentActivity.this);
                        btn.setText("Cancel");
                        ViewGroup.LayoutParams buttonLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.addContentView(btn,buttonLayout);*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNoConnection(String message)
                {
                    dialog.dismiss();
                    Snackbar.make(cardView,message,Snackbar.LENGTH_LONG).show();
                }
            });

        }
        else
        {

        }
    }

    @Override
    public void onBackPressed()
    {
        //closePaymentActivity();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public String standardAmount(String amount)
    {
        String[] sArray = amount.split("\\.");
        int i = Integer.parseInt(sArray[0])*100;
        return i+"";
    }

    public void closePaymentActivity()
    {
        Intent intent = new Intent();
        intent.putExtra("amount",amount);
        intent.putExtra("meter",customerAccount.getAccount());
        intent.putExtra("date",transDate);
        intent.putExtra("type",customerAccount.getType());
        intent.putExtra("customerid",customerId);

        setResult(Activity.RESULT_OK,intent);
        finish();
        //overridePendingTransition(0,0);
    }

    public void closePayWindow(View view)
    {
        closePaymentActivity();
    }

    public void getTransactionRef()
    {
        dialog.setTitle("Please wait");
        dialog.setMessage("Connecting to PHED");
        dialog.show();
        Map<String, Object> params = new HashMap<>();
        params.put("dir","client-api/gpay");
        params.put("authToken",authToken);
        //params.put("imei",imei);
        params.put("mobileNumber", customerAccount.getPhone());
        params.put("accountNumber", customerAccount.getAccount());
        params.put("accountType", customerAccount.getType());
        params.put("disco","phedc");
        params.put("method",Request.Method.POST);

        new Connection(this, params, new OnDataReady()
        {
            @Override
            public void dataReady(JSONObject jsonObject, Object object)
            {
                dialog.dismiss();
                try
                {
                    JSONObject accountJson = jsonObject.getJSONObject("yourResponse");
                    payRef = accountJson.getString("payRef");
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                Log.e("tranRef",jsonObject.toString());
            }

            @Override
            public void onConnectionError(JSONObject error)
            {
                payRef=null;
                dialog.dismiss();
                switch (mType)
                {
                    case "prepaid":
                        Snackbar.make(cardView,"Error fetching token, please contact PHED",3000).show();
                        break;

                    case "postpaid":
                        Snackbar.make(cardView,"Error completing transaction, please contact PHED",3000).show();
                        break;
                }

            }

            @Override
            public void onNoConnection(String message)
            {
                dialog.dismiss();
                Intent i = new Intent();
                i.putExtra("message",message);
                setResult(Activity.RESULT_CANCELED,i);
                //Log.e("=========",customerAccount.getPhone()+" ---"+customerAccount.getAccount()+"-----"+customerAccount.getType());
                finish();
            }
        });

    }

    public void payOnPHED()
    {
        dialog.setTitle("Connecting to PHED");
        dialog.setMessage("Logging payment, please wait...");


        Map<String,Object> payParams = new HashMap<>();
        payParams.put("dir","client-api/gpay");
        payParams.put("authToken",authToken);
        //payParams.put("imei",imei);
        payParams.put("channel","4");
        payParams.put("mode","1");
        payParams.put("disco","phedc");
        payParams.put("accountType",customerAccount.getType());
        payParams.put("payerName",customerAccount.getName());
        payParams.put("amount",standardAmount(amount));
        payParams.put("accountNumber",customerAccount.getAccount());
        payParams.put("payRef",payRef);
        payParams.put("mobileNumber",customerAccount.getPhone());
        payParams.put("method", Request.Method.POST);


        Log.e("amount",(int)Double.parseDouble(amount)+"");

        new Connection(this, payParams, new OnDataReady()
        {
            @Override
            public void dataReady(JSONObject jsonObject, Object object)
            {
                try
                {
                    JSONObject responseObj = jsonObject.getJSONObject("yourResponse");
                        Log.e("token----",jsonObject.toString());
                    switch (jsonObject.getString("message"))
                    {
                        case "00":
                            dialog.dismiss();

                            switch (customerAccount.getType())
                            {
                                case "prepaid":

                                    //double amt = Double.parseDouble(responseObj.getString("payment_amount"));
                                    //NumberFormat format = NumberFormat.getNumberInstance();
                                    //String amount = format.format(amt*100);

                                    amountPaidPrepaid.setText("\u20A6"+responseObj.getString("payment_amount"));
                                    accountName.setText(responseObj.getString("name"));
                                    transType.setText(customerAccount.getType());
                                    tokenString.setText(responseObj.getString("meter_token"));
                                    successMeter.setText(responseObj.getString("meterno"));
                                    String payDate = responseObj.getString("payment_time");
                                    prepaidTransDate.setText(payDate.substring(0,payDate.lastIndexOf(" ",20)));
                                    unit.setText(responseObj.getString("token_unit"));


                                    successViewPrepaid.setVisibility(View.VISIBLE);
                                    cardPaymentView.setVisibility(View.INVISIBLE);
                                    successViewPrepaid.invalidate();

                                    break;

                                case "postpaid":

                                    successView.setVisibility(View.VISIBLE);
                                    cardPaymentView.setVisibility(View.INVISIBLE);
                                    successView.invalidate();

                                    JSONObject yourJsonObj = jsonObject.getJSONObject("yourResponse");
                                    //double payAmount = Double.parseDouble(yourJsonObj.getString("payment_amount"))*100;

                                    amountPaid.setText("\u20A6"+yourJsonObj.getString("payment_amount")+"");
                                    successAccountMeter.setText(yourJsonObj.getString("account_no"));

                                    successAccountType.setText(jsonObject.getString(""));

                                    prevOut.setText("Pre  "+"\u20A6"+yourJsonObj.getString("outstanding_prev"));
                                    curOut.setText("Cur  "+"\u20A6"+yourJsonObj.getString("outstanding"));

                                    transDate = yourJsonObj.getString("payment_time");

                                    break;
                            }

                            break;

                        default:
                            dialog.dismiss();
                            transNotSuccessful.setVisibility(View.VISIBLE);
                            cardPaymentView.setVisibility(View.INVISIBLE);
                            transNotSuccessful.invalidate();

                            Snackbar snack = Snackbar.make(cardView,jsonObject.getString("message"),Snackbar.LENGTH_LONG);
                            snack.setAction("Close", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    closePaymentActivity();
                                }
                            }).show();
                    }
                }
                catch (JSONException e) {
                    Log.e("pay-log",e.getMessage());
                }
            }

            @Override
            public void onConnectionError(JSONObject error) {

                dialog.dismiss();
                try
                {
                    Log.e("card error",error.toString());
                    Snackbar.make(cardView,error.getString("description"),Snackbar.LENGTH_LONG).show();
                }
                catch (JSONException e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void onNoConnection(String message)
            {
                dialog.dismiss();
                Snackbar.make(cardView,message,Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void verifyCardPayment()
    {
        boolean check=false;
        Map<String,String> param = new HashMap<>();
        param.put("dir","merchant/verify");
        param.put("merchantId",MERCHANT_ID);
        param.put("apiKey",API_KEY);
        param.put("transactionRef",transactionRef);

        new PaymentConnection(this, param, new OnDataReady()
        {
            @Override
            public void dataReady(JSONObject jsonObject, Object object)
            {
                String apiKey;
                Log.e("checkError",jsonObject.toString());
                try
                {
                    switch (jsonObject.getString("StatusCode"))
                    {
                        case "00":

                            if(API_KEY.contentEquals(jsonObject.getString("ApiKey")))
                            {

                                payOnPHED();
                            }
                            else
                            {
                                dialog.dismiss();
                                benefDetailsView.setVisibility(View.VISIBLE);
                                cardPaymentView.setVisibility(View.INVISIBLE);
                                benefDetailsView.invalidate();

                                final Snackbar snackbar = Snackbar.make(cardView,"Incomplete transaction",Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("Close", new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        snackbar.dismiss();
                                    }
                                }).show();
                            }

                            break;

                        default:

                            dialog.dismiss();
                            benefDetailsView.setVisibility(View.VISIBLE);
                            cardPaymentView.setVisibility(View.INVISIBLE);
                            benefDetailsView.invalidate();

                            final Snackbar snackbar = Snackbar.make(cardView,"Incomplete transaction",Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Close", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    snackbar.dismiss();
                                }
                            }).show();

                    }


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnectionError(JSONObject error)
            {
                dialog.dismiss();
            }

            @Override
            public void onNoConnection(String message)
            {
                dialog.dismiss();
                Snackbar.make(cardView,message,Snackbar.LENGTH_LONG).show();
            }
        });

    }

}
