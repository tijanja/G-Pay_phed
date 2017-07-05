package gpay.com.g_pay;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class TransactionDetailsActivity extends AppCompatActivity {

    private int height,width,left,top,mLeft,mTop,cLeft,cTop;
    private float mScaleX,mScaleY,cScaleX,cScaleY;
    private TextView genReciept,amount,transDate,transUnit,meter,genToken,custType,phone,prev,outstanding,custName,custAddress,cardAmount,cardMeter,cardUnit,cardDate;
    private String sAmount;
    ColorDrawable mBackground;
    private CoordinatorLayout coordinatorLayout;
    ImageView nikeSign,newNike;
    private Toolbar toolbar;
    AppBarLayout toolbarLayout;
    private Transaction transaction;


    float sWidth;

    private LinearLayout detailsBox,topBoard,detailsLowerBox;

    private CardView transView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        height = bundle.getInt("transHeight");
        width = bundle.getInt("transWidth");
        left = bundle.getInt("transLeft");
        top = bundle.getInt("transTop");
        sAmount = bundle.getString("amountTrans");
        transaction = (Transaction) bundle.getSerializable("transObject");


        //nikeSign = (ImageView) findViewById(R.id.nike);
        //newNike = (ImageView) findViewById(R.id.newNike);

        detailsBox = (LinearLayout) findViewById(R.id.detailBox);
        topBoard = (LinearLayout) findViewById(R.id.topBoard);
        toolbarLayout = (AppBarLayout) findViewById(R.id.toolbarLayout);
        detailsLowerBox = (LinearLayout) findViewById(R.id.detailsLowerBox);
        transDate = (TextView) findViewById(R.id.date);
        transUnit = (TextView) findViewById(R.id.unit);
        meter = (TextView) findViewById(R.id.meter);
        genToken = (TextView) findViewById(R.id.genToken);
        cardAmount = (TextView) findViewById(R.id.cardAmount);
        cardMeter = (TextView) findViewById(R.id.cardMeter);
        cardUnit = (TextView) findViewById(R.id.cardUnit);
        cardDate = (TextView) findViewById(R.id.cardDate);

        phone = (TextView) findViewById(R.id.phone);
        custType = (TextView) findViewById(R.id.custType);
        custAddress = (TextView) findViewById(R.id.address);
        custName = (TextView) findViewById(R.id.name);

        genReciept = (TextView) findViewById(R.id.genReciept);

        toolbarLayout.setTranslationY(-120f);

        sWidth = (float) detailsBox.getWidth();
        amount = (TextView) findViewById(R.id.amount);

        NumberFormat format = NumberFormat.getNumberInstance();
        String samount = format.format(transaction.getAmount());
        amount.setText("\u20A6"+samount);
        cardAmount.setText("\u20A6"+samount);

        transDate.setText(transaction.getDate());
        transUnit.setText(transaction.getUnit()+" unit(s)");
        cardDate.setText(transaction.getDate());
        cardUnit.setText(transaction.getUnit()+" unit(s)");

        meter.setText(transaction.getAccount());
        cardMeter.setText(transaction.getAccount());
        genToken.setText(transaction.getToken());

        phone.setText(transaction.getPhone());
        custType.setText(transaction.getCustCategory());
        custAddress.setText(transaction.getAddress());
        custName.setText(transaction.getCustomerName());

        genReciept.setText(transaction.geteReceipt());

        transView = (CardView) findViewById(R.id.cardView);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coord_bg);
        mBackground = new ColorDrawable(Color.BLACK);
        coordinatorLayout.setBackground(mBackground);

        ViewTreeObserver observer = detailsBox.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                detailsBox.getViewTreeObserver().removeOnPreDrawListener(this);
                int[] screenLocation = new int[2];

                detailsBox.getLocationOnScreen(screenLocation);
                mLeft = left - screenLocation[0];
                mTop = top - screenLocation[1];

                mScaleX = (float) width/detailsBox.getWidth();
                mScaleY = (float) height/detailsBox.getHeight();

                int[] cardviewLocation = new int[2];
                transView.getLocationOnScreen(cardviewLocation);

                cLeft = left - cardviewLocation[0];
                cTop = top - cardviewLocation[1];

                cScaleX = (float) width/transView.getWidth();
                cScaleY = (float) height/transView.getHeight();


                runAnimation();

                return true;
            }
        });




    }


    public void runAnimation()
    {
        final long duration = (long) 500;
        detailsBox.setPivotX(0);
        detailsBox.setPivotY(0);

        detailsBox.setTranslationX(mLeft);
        detailsBox.setTranslationY(mTop);

        detailsBox.setScaleX(mScaleX);
        detailsBox.setScaleY(mScaleY);

        transView.setPivotY(0);
        transView.setPivotX(0);

        transView.setTranslationY(cTop);
        transView.setTranslationX(cLeft);

        transView.setScaleX(cScaleX);
        transView.setScaleY(cScaleY);

        topBoard.setScaleY(0);

        detailsBox.animate().setDuration(duration).scaleX(1).translationX(1).translationY(1).setInterpolator(new AccelerateDecelerateInterpolator())
                .scaleY(1).withEndAction(new Runnable() {
                    @Override
                    public void run()
                    {



                        toolbarLayout.animate().setDuration(300).translationY(1);
                        topBoard.animate().setDuration(300).scaleY(1).setInterpolator(new AnticipateOvershootInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run()
                            {
                                detailsLowerBox.setVisibility(View.VISIBLE);
                            }
                        }).start();

                        int[] nikeLocation = new int[2];
                        //nikeSign.getLocationOnScreen(nikeLocation);
                        //int height = nikeSign.getHeight();
                        //int width = nikeSign.getWidth();

                        int[] newNikeLocation = new int[2];
                        //newNike.getLocationOnScreen(newNikeLocation);

                        //newNike.setPivotY(0);
                        //newNike.setPivotX(0);

                       // newNike.setTranslationY(nikeLocation[1]-newNikeLocation[1]);
                       // newNike.setTranslationX(nikeLocation[0]-newNikeLocation[0]);

                        //newNike.setVisibility(View.VISIBLE);


                        //transView.animate().setDuration(500).alpha(0).start();
                        //newNike.animate().setDuration(800).scaleX(1).scaleY(1).start();

                    }
                }).start();

        transView.animate().setDuration(1000).translationY(2.1f).alpha(0).start();

        ObjectAnimator animBg = ObjectAnimator.ofInt(mBackground,"alpha",0,200);
        animBg.setDuration(500);
        animBg.start();

    }

    public void runOnExit()
    {
        int[] screenLocation = new int[2];
        detailsBox.getLocationOnScreen(screenLocation);
        mLeft = left - screenLocation[0];
        mTop = top - screenLocation[1];


        mScaleX = (float) width/detailsBox.getWidth();
        mScaleY = (float) height/detailsBox.getHeight();
        toolbarLayout.animate().setDuration(300).translationY(-120f);
        topBoard.animate().setDuration(300).scaleY(0).start();
        detailsBox.animate().setDuration(600).scaleY(mScaleY).translationY(mTop).translationX(mLeft).scaleX(mScaleX).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    detailsBox.setElevation(0);
                }

            }
        }).start();

        detailsLowerBox.animate().setDuration(300).alpha(0).start();
        transView.animate().setDuration(1000).alpha(1).translationY(cTop).setInterpolator(new DecelerateInterpolator(1.5f)).withEndAction(new Runnable() {
            @Override
            public void run()
            {
                finish();
                overridePendingTransition(0,0);
            }
        }).start();

        ObjectAnimator animBg = ObjectAnimator.ofInt(mBackground,"alpha",0);
        animBg.setDuration(500);
        animBg.start();

    }

    @Override
    public void onBackPressed()
    {
        runOnExit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu_scrolling; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                runOnExit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
