package gpay.com.g_pay;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnDataReady
{
    private String token,email;
    private Map<String, Object> params;
    private RecyclerView recyclerView;
    private List<Object> tranList;
    private TransactionListAdapter transactionListAdapter;
    private ProgressBar progressBar;
    private String authToken,imei;
    private Button payBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager mViewPager;
    private List<Object> sectionHashMap;
    private SQLiteDatabase sqLiteDatabase;
    private  AccountPagerAdapter accountPagerAdapter;
    private Database database;
    private int PAGE=1;
    private CoordinatorLayout mainCoordinator;
    private ImageView navRight,navLeft;
    private AccountMeter aMeter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_phedsource);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //getWindow().setBackgroundDrawableResource(R.drawable.bg_image);

        sectionHashMap = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_archive);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorButtonYellow,R.color.successGreen,R.color.colorAccent);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        tranList = new ArrayList<>();
        //tranList.add(new PayForm());

        transactionListAdapter = new TransactionListAdapter(tranList,height);


        navRight = (ImageView) findViewById(R.id.navRight);
        navLeft = (ImageView) findViewById(R.id.navLeft);


        authToken = getIntent().getStringExtra("token");
        email = getIntent().getStringExtra("email");
        database = new Database(this);
        Cursor cursor = database.getDbAccounts();

         if(cursor.getCount()==0)
         {
             Intent intent = new Intent(this,CardPaymentActivity.class);
             intent.putExtra("imei",imei);
             intent.putExtra("authToken",authToken);
             startActivityForResult(intent,2);

         }
         else
         {

             while(cursor.moveToNext())
             {
                 if(!TextUtils.isEmpty(cursor.getString(2)))
                 {
                     AccountMeter accountMeter = new AccountMeter();
                     accountMeter.setCustomerid(cursor.getInt(1));
                     accountMeter.setAccount(cursor.getString(3));
                     accountMeter.setAddress(cursor.getString(4));
                     accountMeter.setName(cursor.getString(2));
                     accountMeter.setPhone(cursor.getString(5));
                     accountMeter.setType(cursor.getString(6));

                     sectionHashMap.add(accountMeter);
                 }

             }

             aMeter = (AccountMeter)sectionHashMap.get(0);
             getTransactions(aMeter.getCustomerid(),PAGE);

             cursor.close();

             swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                 @Override
                 public void onRefresh()
                 {

                     getTransactions(aMeter.getCustomerid(),PAGE);

                     PAGE +=1;
                 }
             });


         }


        //progressBar = (ProgressBar) findViewById(R.id.toolbarProgress);

        mainCoordinator = (CoordinatorLayout) findViewById(R.id.mainCoordinator);
        accountPagerAdapter = new AccountPagerAdapter(getSupportFragmentManager(),sectionHashMap,email,authToken,mainCoordinator);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(0,20,0,20);

        mViewPager.setAdapter(accountPagerAdapter);
        //mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(10);
        mViewPager.setPageMargin(20);


        recyclerView = (RecyclerView) findViewById(R.id.trans_list);
        recyclerView.setAdapter(transactionListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnItemTouchListener(new OnStatuteClickListener(this, recyclerView, new OnClickStatute() {
            @Override
            public void onClick(View view, int position)
            {
                Transaction transaction = (Transaction) tranList.get(position);
                TextView amountTv = (TextView) view.findViewById(R.id.amount);
                //Toast.makeText(MainActivity.this,amountTv.getText().toString(),Toast.LENGTH_LONG).show();
                int height = view.getHeight();
                int width = view.getWidth();

                int[] screenLocation = new int[2];
                view.getLocationOnScreen(screenLocation);

                Intent transactionDetails = new Intent(MainActivity.this,TransactionDetailsActivity.class);
                transactionDetails.putExtra("transHeight",height);
                transactionDetails.putExtra("transWidth",width);
                transactionDetails.putExtra("transLeft",screenLocation[0]);
                transactionDetails.putExtra("transTop",screenLocation[1]);
                transactionDetails.putExtra("amountTrans",amountTv.getText().toString());
                transactionDetails.putExtra("transObject",transaction);

                startActivity(transactionDetails);
                overridePendingTransition(0,0);
            }

            @Override
            public void onLongClick(View view, int position)
            {

            }
        }));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                int totalPage = sectionHashMap.size();
                tranList.clear();
                transactionListAdapter.notifyDataSetChanged();

                if(position==0)
                {
                    navLeft.setVisibility(View.GONE);
                    navRight.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(position==totalPage-1)
                    {
                        navRight.setVisibility(View.GONE);
                        navLeft.setVisibility(View.VISIBLE);
                    }
                    else if(position>0 && position!=totalPage-1)
                    {
                        navRight.setVisibility(View.VISIBLE);
                        navLeft.setVisibility(View.VISIBLE);
                    }



                }

                aMeter = (AccountMeter)sectionHashMap.get(position);
                PAGE = 1;
                getTransactions(aMeter.getCustomerid(),PAGE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(sectionHashMap.size()>1)
        {
            navRight.setVisibility(View.VISIBLE);
        }

        navRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            }
        });


       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, GpayPaymentActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addAcct)
        {
            final Intent intent = new Intent(this,CardPaymentActivity.class);
            intent.putExtra("imei",imei);
            intent.putExtra("authToken",authToken);

            mainCoordinator.animate().setDuration(250).scaleX(0.9f).scaleY(.9f).withStartAction(new Runnable() {
                @Override
                public void run()
                {
                    MainActivity.this.startActivityForResult(intent,2);
                    MainActivity.this.overridePendingTransition(0,0);
                }
            }).start();

        }

        if(id == R.id.logout)
        {
            finish();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void dataReady(JSONObject jsonObject,Object object)
    {
        Log.e("trans",jsonObject.toString());
            try
            {
                if(jsonObject.getInt("code")==200)
                {
                    JSONObject response = jsonObject.getJSONObject("yourResponse");
                    JSONArray transJsonArray = response.getJSONArray("transactions");

                    /*if(!tranList.isEmpty())
                    {

                    }*/

                    for(int i=0;i<transJsonArray.length();i++)
                    {
                        JSONObject transObj = transJsonArray.getJSONObject(i);

                        Transaction transaction = new Transaction();
                        transaction.setCustomerName(transObj.getString("name"));
                        transaction.setAmount(Double.parseDouble(transObj.getString("amount").replace(",","")));
                        String dateString = transObj.getString("date");
                        transaction.setDate( dateString.substring(0,dateString.lastIndexOf(" ",20)));
                        transaction.setUnit(transObj.getString("unit"));
                        transaction.setAccount(transObj.getString("meter"));
                        transaction.setStatus(transObj.getString("status"));
                        transaction.setAddress(transObj.getString("address"));
                        transaction.setCustCategory(transObj.getString("custCategory"));
                        transaction.setDiscoRef(transObj.getString("discoRef"));
                        transaction.seteReceipt(transObj.getString("eReceipt"));
                        transaction.setCustomerName(transObj.getString("name"));
                        transaction.setOutstanding(transObj.getString("outstanding"));
                        transaction.setPreviousOutstanding(transObj.getString("previousOutstanding"));
                        transaction.setPhone(transObj.getString("phone"));
                        transaction.setToken(transObj.getString("token"));
                        transaction.setAccount(transObj.getString("account"));



                        tranList.add(transaction);
                        transactionListAdapter.notifyDataSetChanged();

                    }


                    transactionListAdapter.notifyDataSetChanged();
                    if(swipeRefreshLayout.isRefreshing())
                    {
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(swipeRefreshLayout,"Pulled "+transJsonArray.length()+" transaction(s)",Snackbar.LENGTH_SHORT).show();
                    }
                    recyclerView.invalidate();

                }
                else
                {
                    Toast.makeText(this,jsonObject.getInt("code")+"",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
            }

    }

    @Override
    public void onConnectionError(JSONObject error)
    {
        Snackbar.make(swipeRefreshLayout,error.toString(),Snackbar.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNoConnection(String message)
    {
        Snackbar.make(payBtn,message,Snackbar.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mainCoordinator.animate().setDuration(250).scaleX(1f).scaleY(1f).start();
        if(resultCode == Activity.RESULT_OK && requestCode==1)
        {
            if(data!=null)
            {
                AccountMeter account = new AccountMeter();
                account.setAccount(data.getStringExtra("account"));
                account.setName(data.getStringExtra("name"));
                account.setPhone(data.getStringExtra("phone"));
                account.setAddress(data.getStringExtra("address"));
                account.setType(data.getStringExtra("type"));

                sectionHashMap.add(account);

                accountPagerAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(true);
                getTransactions(data.getIntExtra("customerid",0),1);
            }

        }

        if(resultCode == Activity.RESULT_OK &&  requestCode==2)
        {
            if(data!=null)
            {
                if(!tranList.isEmpty())
                {
                    tranList.clear();
                    transactionListAdapter.notifyDataSetChanged();
                }

                AccountMeter account = new AccountMeter();
                account.setAccount(data.getStringExtra("account"));
                account.setName(data.getStringExtra("name"));
                account.setPhone(data.getStringExtra("phone"));
                account.setAddress(data.getStringExtra("address"));
                account.setType(data.getStringExtra("mType"));
                account.setCustomerid(data.getIntExtra("customerid",0));

                sectionHashMap.add(account);
                accountPagerAdapter.notifyDataSetChanged();

                mViewPager.setCurrentItem(sectionHashMap.size()-1,true);
                getTransactions(account.getCustomerid(),1);
            }

        }

        if(resultCode==Activity.RESULT_CANCELED)
        {
            if(data!=null)
            {
                Snackbar.make(swipeRefreshLayout,data.getStringExtra("message"),Snackbar.LENGTH_LONG).show();
            }

        }

    }

    public boolean database()
    {
        boolean check = false;
        final String  DATABASE_NAME = "PHED_Database";

        sqLiteDatabase = openOrCreateDatabase(DATABASE_NAME,SQLiteDatabase.OPEN_READWRITE,null);

        if(sqLiteDatabase.isOpen())
        {
            Cursor resultCheck = sqLiteDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = 'phed_app_user';",null);

            try
            {
                if(resultCheck!=null)
                {
                    if(resultCheck.getCount()>0) {
                        resultCheck.close();
                        Cursor resultSet = sqLiteDatabase.rawQuery("select * from phed_app_user",null);

                        if(resultSet.getCount()>0)
                        {
                            check=true;
                        }

                        resultSet.close();
                        sqLiteDatabase.close();
                    }
                    else
                    {
                        Log.e("database-error","table not created");
                        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS phed_app_user(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR,account VARCHAR,phone VARCHAR,address VARCHAR);");
                        sqLiteDatabase.close();
                    }

                }
                else
                {
                    Log.e("database-error","generating false");
                }
            }
            catch (Exception e)
            {
                Log.e("error",e.getMessage());
            }



        }
        else
        {
            Log.e("database-error","database not open");
        }



        return check;

    }


    public void getTransactions(int customerid,int page)
    {
        swipeRefreshLayout.setRefreshing(true);
        params = new HashMap<>();
        params.put("dir","client-api/transactions?page="+page+"&per-page=10&customer_id="+customerid);
        params.put("imei",imei);
        params.put("authToken",authToken);
        params.put("method", Request.Method.GET);
        Log.e("customerId",customerid+"");
        new Connection(this,params,this);
    }

}
