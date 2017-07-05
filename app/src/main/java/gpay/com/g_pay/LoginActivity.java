package gpay.com.g_pay;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rajioladayo on 5/12/17.
 */

public class LoginActivity extends AppCompatActivity{

    private final int REQUEST_READ_PHONE_STATE = 300;
    EditText username,password,sEmail,sPassword,sRePassword;
    TextView forgot_password,action_text,loginText,registerText;
    LinearLayout signup,signup_layout,login_layout;
    FrameLayout login,registerUserBtn;
    View reveal;
    int measuredWidth;
    boolean isLogin = true;
    private ProgressBar login_progress,registerProgress;
    private String IMEI,token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().setTitle("Login");

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE))
            {

            }
            else
            {*/
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            //}

        }

        try
        {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = "356766076068242";//telephonyManager.getDeviceId();
        }
        catch (Exception e)
        {
            Log.e("error",e.getMessage());
        }

        initializeControls();
    }

    private void initializeControls(){

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        registerProgress = (ProgressBar) findViewById(R.id.registerProgress);
        registerText = (TextView) findViewById(R.id.registerText);

        sEmail = (EditText) findViewById(R.id.email);
        sEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                Log.e("hint","tring to verify");
                if(!hasFocus && !TextUtils.isEmpty(sEmail.getText().toString()))
                {

                    Map<String,Object> map = new HashMap<>();
                    map.put("dir","admin/email-verify/");
                    map.put("email",sEmail.getText().toString());
                    map.put("method",Request.Method.POST);

                    new Connection(LoginActivity.this, map, new OnDataReady() {
                        @Override
                        public void dataReady(JSONObject jsonObject, Object object)
                        {
                            try
                            {
                                JSONObject resObject = jsonObject.getJSONObject("yourResponse");

                                if(resObject.getBoolean("valid"))
                                {
                                    sEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, R.drawable.ic_check_circle_black_24dp, 0);
                                    Log.e("-------",jsonObject.toString());
                                }
                                else
                                {
                                    sEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, R.drawable.ic_cancel_black_24dp, 0);
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
                            Log.e("net error1",error.toString());
                        }

                        @Override
                        public void onNoConnection(String message)
                        {
                            Log.e("net error",message);
                        }
                    });
                }
                else
                {
                    sEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, 0, 0);
                }

            }
        });
        sPassword = (EditText) findViewById(R.id.password_signup);
        sRePassword = (EditText) findViewById(R.id.re_password_signup);

        registerUserBtn = (FrameLayout) findViewById(R.id.registerUser);



        action_text = (TextView) findViewById(R.id.action_text);

        forgot_password = (TextView) findViewById(R.id.forgot_password);
        login_progress = (ProgressBar) findViewById(R.id.loginProgress);
        loginText = (TextView) findViewById(R.id.loginText);

        reveal = findViewById(R.id.reveal);


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        signup_layout = (LinearLayout) findViewById(R.id.signup_layout);
        login_layout = (LinearLayout) findViewById(R.id.login_layout);

        signup = (LinearLayout) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // animate(isLogin);
            }
        });

        login = (FrameLayout) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
                {
                    login.setEnabled(false);
                    measuredWidth = login.getMeasuredWidth();
                    ValueAnimator anim = ValueAnimator.ofInt(measuredWidth,login.getMeasuredHeight());
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation)
                        {
                            int val = (Integer) animation.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = login.getLayoutParams();
                            layoutParams.width = val;
                            login.requestLayout();


                        }
                    });

                    anim.setDuration(250);
                    anim.start();

                    loginText.animate().setDuration(250).alpha(0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            super.onAnimationEnd(animation);
                            login_progress.setVisibility(View.VISIBLE);


                            login_progress.setVisibility(View.VISIBLE);
                            Map<String,Object> params = new HashMap<>();
                            params.put("dir","client-api/auth/");
                            params.put("userNameheaderValue",username.getText().toString());
                            params.put("passwordheaderValue",password.getText().toString());
                            params.put("method",Request.Method.GET);


                            new Auth(LoginActivity.this, params, new OnDataReady() {

                                @Override
                                public void dataReady(JSONObject jsonObject, Object object)
                                {
                                    try
                                    {
                                        if (Integer.parseInt(jsonObject.getString("code"))==200)
                                        {
                                            JSONObject loginObj = jsonObject.getJSONObject("yourResponse");
                                            revealButton();
                                            login_progress.setVisibility(View.GONE);
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            intent.putExtra("token",loginObj.getString("token"));
                                            intent.putExtra("email",username.getText().toString());
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            loginBtnScaleOutAnim("error in connection");
                                        }
                                    }
                                    catch (JSONException e)
                                    {
                                        loginBtnScaleOutAnim(e.getMessage());
                                    }

                                }

                                @Override
                                public void onConnectionError(JSONObject error)
                                {
                                    loginBtnScaleOutAnim("Connection error!!");

                                }

                                @Override
                                public void onNoConnection(String message)
                                {
                                    loginBtnScaleOutAnim(message);
                                }
                            });

                        }
                    }).start();



                }
                else if (username.getText().toString().isEmpty())
                {
                    Snackbar.make(username,"Please enter User name",Snackbar.LENGTH_LONG).show();
                }
                else if (password.getText().toString().isEmpty())
                {
                    Snackbar.make(password,"Please enter Password",Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private boolean validateControls(Context context){

       /* if(TextUtils.isEmpty(username.getText().toString())){
            Utility.alert(context,"Please provide your username");
            username.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(password.getText().toString())){
            Utility.alert(context,"Please provide your password");
            password.requestFocus();
            return false;
        }*/
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                {
                    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    if(IMEI==null)
                    {
                        IMEI = telephonyManager.getDeviceId();
                    }

                }
                break;

            default:
                break;
        }
    }

    private void revealButton() {
        //login.setElevation(0f);
        reveal.setVisibility(View.VISIBLE);


        int cx = reveal.getWidth();
        int cy = reveal.getHeight();

        int startX = cx / 2;
        int startY = (int) (cy / 2 + login.getY());

        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(reveal, startX, startY, login.getMeasuredHeight(), finalRadius);
        }

        anim.setDuration(350);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
        });
        anim.start();
    }


    public void signUpUser(View v)
    {
       if(!TextUtils.isEmpty(sEmail.getText().toString())&& !TextUtils.isEmpty(sPassword.getText().toString()))
       {
           if(sPassword.getText().toString().contentEquals(sRePassword.getText().toString()))
           {
               registerUserBtn.setEnabled(false);
               measuredWidth = registerUserBtn.getMeasuredWidth();
               ValueAnimator anim = ValueAnimator.ofInt(measuredWidth,registerUserBtn.getMeasuredHeight());
               anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                   @Override
                   public void onAnimationUpdate(ValueAnimator animation)
                   {
                       int val = (Integer) animation.getAnimatedValue();
                       ViewGroup.LayoutParams layoutParams = registerUserBtn.getLayoutParams();
                       layoutParams.width = val;
                       registerUserBtn.requestLayout();


                   }
               });

               anim.setDuration(250);
               anim.start();

               registerText.animate().setDuration(250).alpha(0f).setListener(new AnimatorListenerAdapter() {
                   @Override
                   public void onAnimationEnd(Animator animation) {
                       super.onAnimationEnd(animation);

                       registerProgress.setVisibility(View.VISIBLE);
                       Map<String,Object> params = new HashMap<>();
                       params.put("dir","auth");
                       params.put("userNameheaderValue","client");
                       params.put("passwordheaderValue", "Ieh6fooMaize");
                       params.put("method",Request.Method.POST);

                       new Auth(LoginActivity.this, params, new OnDataReady() {
                           @Override
                           public void dataReady(JSONObject jsonObject, Object object)
                           {
                               try
                               {
                                   token = jsonObject.getString("token");
                                   Map<String,Object> params = new HashMap<>();
                                   params.put("dir","admin/clients/");
                                   params.put("password",sPassword.getText().toString());
                                   params.put("email",sEmail.getText().toString());
                                   params.put("authToken",token);
                                   params.put("method", Request.Method.PUT);

                                   new Connection(LoginActivity.this, params, new OnDataReady() {
                                       @Override
                                       public void dataReady(JSONObject jsonObject, Object object)
                                       {
                                           try
                                           {
                                               JSONObject resObj = jsonObject.getJSONObject("yourResponse");
                                               if(resObj.getString("email").contentEquals(sEmail.getText().toString()))
                                               {
                                                   username.setText(resObj.getString("email"));
                                                   username.invalidate();

                                                   registerBtnScaleOutAnim("User setup successful");
                                                   animateToShowLoginView();
                                               }
                                               else
                                               {
                                                   registerBtnScaleOutAnim("Sorry unable to register user");

                                               }

                                           }
                                           catch (JSONException e)
                                           {
                                               registerBtnScaleOutAnim(e.getMessage());
                                           }
                                       }

                                       @Override
                                       public void onConnectionError(JSONObject error)
                                       {

                                           registerBtnScaleOutAnim("Connection Error, Please try again");
                                       }

                                       @Override
                                       public void onNoConnection(String message)
                                       {

                                           registerBtnScaleOutAnim(message);
                                       }

                                   });

                               }
                               catch (JSONException e)
                               {

                                   registerBtnScaleOutAnim(e.getMessage());
                               }

                           }

                           @Override
                           public void onConnectionError(JSONObject error)
                           {

                               registerBtnScaleOutAnim("Connection error");
                           }

                           @Override
                           public void onNoConnection(String message)
                           {

                               registerBtnScaleOutAnim(message);
                           }
                       });
                   }
               }).start();


           }
           else
           {
               Snackbar.make(v,"Passwords not the same",Snackbar.LENGTH_LONG).show();
           }
       }
       else if(TextUtils.isEmpty(sEmail.getText().toString()))
       {
           Snackbar.make(v,"Please enter your email",Snackbar.LENGTH_LONG).show();
       }
       else if (TextUtils.isEmpty(sPassword.getText().toString()))
       {
           Snackbar.make(v,"Enter Password",Snackbar.LENGTH_LONG).show();
       }

    }

    public void getRegistarView(View view)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        signup_layout.setPivotX(0);
        signup_layout.setTranslationX(-(float) width);
        signup_layout.setVisibility(View.VISIBLE);


        login_layout.animate().setDuration(350).translationX(-(float) width).start();
        signup_layout.animate().translationX(1).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run()
            {
                login_layout.setVisibility(View.GONE);
            }
        });


    }

    public void getLoginView(View v)
    {
        animateToShowLoginView();
    }

    public void animateToShowLoginView()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        login_layout.setVisibility(View.VISIBLE);

        login_layout.animate().setDuration(500).translationX(1).start();
        signup_layout.animate().translationX(-(float) width).setDuration(350).withEndAction(new Runnable() {
            @Override
            public void run()
            {
                signup_layout.setVisibility(View.GONE);
            }
        });


    }

    public void loginBtnScaleOutAnim(String message)
    {
        Snackbar.make(login_progress,message,Snackbar.LENGTH_LONG).show();

        ValueAnimator anim = ValueAnimator.ofInt(login.getMeasuredWidth(),measuredWidth);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = login.getLayoutParams();
                layoutParams.width = val;
                login.requestLayout();
            }
        });

        anim.setDuration(250);
        anim.start();
        login_progress.setVisibility(View.GONE);

        loginText.animate().setDuration(250).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                login.setEnabled(true);

            }
        }).start();
    }

    public void registerBtnScaleOutAnim(String message)
    {
        registerUserBtn.setEnabled(true);
        Snackbar.make(registerProgress,message,Snackbar.LENGTH_LONG).show();

        ValueAnimator anim = ValueAnimator.ofInt(registerUserBtn.getMeasuredWidth(),measuredWidth);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = registerUserBtn.getLayoutParams();
                layoutParams.width = val;
                registerUserBtn.requestLayout();
            }
        });

        anim.setDuration(250);
        anim.start();
        registerProgress.setVisibility(View.GONE);

        registerText.animate().setDuration(250).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                login.setEnabled(true);

            }
        }).start();
    }
}
