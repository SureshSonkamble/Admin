package com.example.admin_beerbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.admin_beerbar.Class.ConnectionClass;
import com.example.admin_beerbar.Class.SessionManager;
import com.example.admin_beerbar.Class.TransparentProgressDialog;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class IPAdderss_Activity extends AppCompatActivity {

    TextView txt_msg,txt_pwd;
    String session="0";
    Toolbar toolbar;
    String str_pwd,str_wine_shop,str_ip_addr,str_port_number,str_new_pwd,str_cnfrm_pwd,str_old_pwd,str_mob;
    Button btn_ip_addr,btn_cancel,btn_restore_db;
    Spinner spinner_wine_shop;
    SessionManager sessionManager;
    SharedPreferences sp;
    float apk_code;
    String str_CLOUD_SERVER_BACKUP_DATABASE,str_CLOUD_SEVER_BACKUP_FILE,str_TIME,str_Date;
    String  user_name, user,password,expiry_alert,expiry_yn;
    String IMEINumber;
    ConnectionClass connection;
    int m_TAB_CODE;
    ProgressDialog progressDoalog;

    int val=0;
    String flag="0";
    TransparentProgressDialog pd;
    EditText edt_mob,edt_ip,edt_old_pwd,edt_new_pwd,edt_cnfrm_pwd,edt_pwd;
    Button btn_save;
    AlertDialog dialog;
    PreparedStatement ps1;
    boolean reachable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipaddress_);
        connection = new ConnectionClass();
        sessionManager = new SessionManager(this);
        //------------------------User Session------------------------------------------
        Bundle b = getIntent().getExtras();
        try {
            user = b.getString("email");

            // Toast.makeText(getApplicationContext(), "Welcome-" + user, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }

        if (user == null) {
            finish();
            //Toast.makeText(getApplicationContext(),"Invalid User...",Toast.LENGTH_LONG).show();
        } else {
            sp = this.getSharedPreferences("PI", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("email", user);
            editor.commit();
        }
        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        apk_code = sp.getFloat("apk_code", 0);
        user_name = sp.getString("user_name", "");
        password = sp.getString("password", "");
        IMEINumber = sp.getString("mac_id", "");


        // Toast.makeText(getApplicationContext(), "Emp Code :" + emp_code + "\n" + "Emp Name :" + emp_name+ACCESS_FULL_YN, Toast.LENGTH_LONG).show();
        pd = new TransparentProgressDialog(IPAdderss_Activity.this, R.drawable.hourglass);
        spinner_wine_shop=(Spinner)findViewById(R.id.spinner_wine_shop);

        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Ip Address");
        /*ImageView toolbar_reset=(ImageView)toolbar.findViewById(R.id.img_reset);//arrow
        toolbar_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(IPAdderss_Activity.this);
                alertDialog.setTitle("Do You Really Want To Relogin?");
                alertDialog.setMessage("App will Forced stop");
                alertDialog.setIcon(R.drawable.fail);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAppData();
                    }
                });
                alertDialog.show();

            }
        });*/
        //toolbar_title.setTextColor(0xFFFFFFFF);*/
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
       // pd.show();
       // Toast.makeText(getApplicationContext(), "Loading Data Please Wait..", Toast.LENGTH_LONG).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                try {
                    new load_wine_shop().execute();

                } catch (Exception e) {
                }
              //  pd.dismiss();
            }
        }, 2000);
        SharedPreferences  spp = getSharedPreferences("IP", MODE_PRIVATE);
        session = spp.getString("session", "");
      /*  if(session.equals("1"))
        {
            Intent i=new Intent(getApplicationContext(),Home.class);
            startActivity(i);
            finish();
        }*/
        //----------------------------------------------
      //  btn_restore_db=(Button) findViewById(R.id.btn_restore_db);
        btn_ip_addr=(Button) findViewById(R.id.btn_ip_addr);
        btn_ip_addr=(Button) findViewById(R.id.btn_ip_addr);
        txt_pwd=(TextView)findViewById(R.id.txt_pwd);
        txt_pwd.setPaintFlags(txt_pwd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chng_pwf_popup_form();
            }
        });
        txt_msg=(TextView)findViewById(R.id.txt_msg);
        txt_msg.setPaintFlags(txt_msg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btn_ip_addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if(!str_CLOUD_SERVER_BACKUP_DATABASE.equals("BEERBAR")&& !str_CLOUD_SEVER_BACKUP_FILE.equals("")){
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("IPADDR", MODE_PRIVATE); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("ipaddress", str_ip_addr);
                            editor.putString("portnumber", str_port_number);
                            editor.putInt("TAB_CODE", m_TAB_CODE);
                            editor.putString("str_wine_shop", str_wine_shop);
                            editor.putString("db", str_CLOUD_SERVER_BACKUP_DATABASE);
                            editor.commit();
                            SharedPreferences pf = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                            SharedPreferences.Editor edtr = pf.edit();
                            edtr.putString("session", "1");
                            edtr.commit();
                          //  Intent i = new Intent(getApplicationContext(), Home.class);
                           // startActivity(i);
                          //  finish();
                            //---check expiry-----------------------------

                            try {
                                Connection con = connection.CONN();
                                if (con == null) {
                                    Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                                } else {

                                    PreparedStatement pss = con.prepareStatement("UPDATE APKCONNECTION SET EXPIRY_YN='Y' WHERE EXPIRY_DATE+1 < GETDATE() AND APK_GROUP_CODE="+apk_code+" AND CLOUD_SERVER_BACKUP_DATABASE ='"+str_CLOUD_SERVER_BACKUP_DATABASE+"'");
                                    pss.executeUpdate();


                                    //String q="SELECT LTRIM(RTRIM(COMP_NAME)) AS SHOP_NAME,LTRIM(RTRIM(CONNECTION_STRING)) AS IP FROM APKCONNECTION WHERE APK_GROUP_CODE IN (SELECT APK_GROUP_CODE FROM APKGROUPMAST WHERE APK_TYPE = 1 AND MAC_ID LIKE '"+'%'+IMEINumber+'%'+"')";
                                    // String q="select install_date,expiry_date,expiry_days from profile";
                                    String q="SELECT CASE WHEN EXPIRY_MSG_DAYS >= DATEDIFF(D,GETDATE(),EXPIRY_DATE) THEN 'This Application Will Expire ' + case when DATEDIFF(D,GETDATE(),EXPIRY_DATE) <> 0 then 'After ' + ltrim(str(DATEDIFF(D,GETDATE(),EXPIRY_DATE))) + case when DATEDIFF(D,GETDATE(),EXPIRY_DATE) > 1 then + ' Days' else ' Day' end else 'Today' end else '' end as expiry_alert,expiry_yn FROM APKCONNECTION WHERE APK_GROUP_CODE="+apk_code+"  AND CLOUD_SERVER_BACKUP_DATABASE ='"+str_CLOUD_SERVER_BACKUP_DATABASE+"'";
                                    PreparedStatement ps = con.prepareStatement(q);
                                    ResultSet rs = ps.executeQuery();
                                    while(rs.next())
                                    {
                                        expiry_alert=rs.getString("expiry_alert");
                                        expiry_yn=rs.getString("expiry_yn");

                                    }
                                    if(expiry_yn.equals("Y"))
                                    {
                                        alert();
                                    }
                                    else if(!expiry_alert.equals(""))
                                    {
                                        soft_alert();
                                    }
                                    else if(expiry_alert.equals("")&& expiry_yn.equals("")){
                                        Intent i = new Intent(getApplicationContext(), Home.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else {
                                        Intent i = new Intent(getApplicationContext(), Home.class);
                                        startActivity(i);
                                        finish();
                                    }


                                }  //z = "Success";


                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "248-Exception"+e, Toast.LENGTH_SHORT).show();
                            }
                            //--------------------------------------------

                        }
                        else {
                            ping();
                        }

                        pd.dismiss();
                    }
                }, 2000);

            }
        });


    }


    public class load_wine_shop extends AsyncTask<String, String, String> {

        List<Map<String, String>> hw_data = new ArrayList<Map<String, String>>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection con = connection.CONN();
                if (con == null) {
                    Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {

                   // String q="SELECT LTRIM(RTRIM(COMP_NAME)) AS SHOP_NAME,LTRIM(RTRIM(CONNECTION_STRING)) AS IP,(SELECT TOP 1 TAB_CODE FROM TABUSERMAST WHERE MAC_ID = '"+IMEINumber+"') AS TAB_CODE,PORT_NO FROM APKCONNECTION WHERE APK_GROUP_CODE IN (SELECT APK_GROUP_CODE FROM APKGROUPMAST WHERE APK_TYPE = 2 AND MAC_ID LIKE '"+'%'+IMEINumber+'%'+"')";
                   String q="SELECT LTRIM(RTRIM(COMP_NAME)) AS SHOP_NAME,LTRIM(RTRIM(CONNECTION_STRING)) AS IP,(SELECT TOP 1 TAB_CODE FROM TABUSERMAST WHERE MAC_ID = '"+IMEINumber+"') AS TAB_CODE,PORT_NO,CASE WHEN LEN(LTRIM(RTRIM(CLOUD_SERVER_BACKUP_DATABASE))) = 0 THEN 'BEERBAR' ELSE CLOUD_SERVER_BACKUP_DATABASE END AS CLOUD_SERVER_BACKUP_DATABASE,CLOUD_SERVER_BACKUP_PATH + CLOUD_SERVER_BACKUP_RARFILE AS CLOUD_SEVER_BACKUP_FILE,'Last Backup : ' + CLOUD_SERVER_BACKUP_RARFILE as lastfile, '  Last Time : ' +  CLOUD_SERVER_BACKUP_TIME  as lasttime FROM APKCONNECTION WHERE APK_GROUP_CODE IN (SELECT APK_GROUP_CODE FROM APKGROUPMAST WHERE APK_TYPE = 2 AND MAC_ID LIKE '"+'%'+IMEINumber+'%'+"')";
                    PreparedStatement ps = con.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();
                    //ArrayList data1 = new ArrayList();
                    hw_data.clear();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("A", rs.getString(1));
                        data.put("B", rs.getString(2));
                        data.put("C", rs.getString(4));
                        data.put("D", rs.getString(5));//CLOUD_SERVER_BACKUP_DATABASE
                        data.put("E", rs.getString(6));//CLOUD_SEVER_BACKUP_FILE
                        data.put("F", rs.getString(7));//LAST_FILE
                        data.put("G", rs.getString(8));//LAST_TIME
                        m_TAB_CODE=rs.getInt(3);
                        hw_data.add(data);
                    }
                }  //z = "Success";

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(hw_data.size()==0)
            {
                warn();
            }
            String[] from = {"A", "B","C","D","E","F","G"};
           // String[] from = {"A", "B","C"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getApplicationContext(), hw_data, R.layout.spin, from, views);
            spinner_wine_shop.setAdapter(spnr_data);
           // spinner_wine_shop.setSelection(20);
            spinner_wine_shop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    str_wine_shop = (String) obj.get("A");
                    str_ip_addr = (String) obj.get("B");
                    str_CLOUD_SERVER_BACKUP_DATABASE = (String) obj.get("D");
                    str_CLOUD_SEVER_BACKUP_FILE = (String) obj.get("E");
                    str_Date = (String) obj.get("F");
                    str_TIME = (String) obj.get("G");
                    if(str_ip_addr.equals("PRANALISERVER"))
                    {
                        ip_popup_form();
                    }
                    else
                    {
                        str_ip_addr = (String) obj.get("B");
                    }
                    str_port_number = (String) obj.get("C");
                  /*  if(!str_CLOUD_SERVER_BACKUP_DATABASE.equals("BEERBAR")&& !str_CLOUD_SEVER_BACKUP_FILE.equals("")){
                        btn_restore_db.setVisibility(View.VISIBLE);
                    }*/
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            super.onPostExecute(s);
        }
    }
    public  void warn()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(IPAdderss_Activity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Warning");
        builder.setIcon(R.drawable.warn);
        builder.setMessage("Your Device is Not Registered");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finish();
            }
        });
        builder.show();
       txt_msg.setText("Your Device is Not Registered");
       btn_ip_addr.setVisibility(View.INVISIBLE);
    }

    public void ip_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.ip_popup_form, null);

        edt_ip = (EditText) alertLayout.findViewById(R.id.edt_ip);

        btn_save = (Button) alertLayout.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_ip_addr=edt_ip.getText().toString();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("IPADDR", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ipaddress", str_ip_addr);
                editor.putString("portnumber", str_port_number);
                editor.putInt("TAB_CODE", m_TAB_CODE);
                editor.putString("str_wine_shop", str_wine_shop);
                editor.putString("db", str_CLOUD_SERVER_BACKUP_DATABASE);
                editor.commit();
                SharedPreferences pf = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor edtr = pf.edit();
                edtr.putString("session", "1");
                edtr.commit();
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
                finish();
                dialog.dismiss();
            }
        });

        btn_cancel = (Button) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(IPAdderss_Activity.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }
    public void chng_pwf_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.chng_pwd_popup_form, null);

        edt_mob = (EditText) alertLayout.findViewById(R.id.edt_mob);
        edt_mob.setText(IMEINumber);
        edt_old_pwd = (EditText) alertLayout.findViewById(R.id.edt_old_pwd);
        // edt_old_pwd.setText(password);
        edt_new_pwd = (EditText) alertLayout.findViewById(R.id.edt_new_pwd);
        edt_cnfrm_pwd = (EditText) alertLayout.findViewById(R.id.edt_cnfrm_pwd);

        btn_save = (Button) alertLayout.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        str_mob=edt_mob.getText().toString();
                        str_old_pwd=edt_old_pwd.getText().toString();
                        str_new_pwd=edt_new_pwd.getText().toString();
                        str_cnfrm_pwd=edt_cnfrm_pwd.getText().toString();
                       // validate_pwd(str_old_pwd);
                        if(str_old_pwd.equals(password)) {
                            if (!str_cnfrm_pwd.equals(str_new_pwd)) {
                                pd.dismiss();
                                Toast.makeText(IPAdderss_Activity.this, "Invalid Confirm Password..", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    int r=0;
                                    Connection con = connection.CONN();
                                    if (con == null) {
                                        // z = "Error in connection with SQL server";
                                    } else {
                                        ps1 = con.prepareStatement("Update APKLOGIN set PASS_WORD='" + str_cnfrm_pwd + "'  where MOBILE_NO='" + str_mob + "' and PASS_WORD='" + str_old_pwd + "' and APK_GROUP_CODE="+apk_code+"");
                                        r= ps1.executeUpdate();

                                        if(r>0)
                                        {
                                            pd.dismiss();
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(IPAdderss_Activity.this, R.style.AppCompatAlertDialogStyle);
                                            builder.setTitle("Success");
                                            builder.setIcon(R.drawable.warn);
                                            builder.setMessage("Password Changed Successfully");
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    deleteAppData();
                                                }
                                            });
                                            builder.show();
                                        }else
                                        { pd.dismiss();
                                            Toast.makeText(IPAdderss_Activity.this, "Error Wile Password Changed..", Toast.LENGTH_SHORT).show();
                                        }
                                        con.close();
                                    }

                                } catch (Exception e) {
                                }
                            }
                        }
                        else {
                            pd.dismiss();
                            Toast.makeText(IPAdderss_Activity.this, "Invalid Old Password..", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 2000);
                dialog.dismiss();
            }
        });

        btn_cancel = (Button) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(IPAdderss_Activity.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }

    void validate_pwd(String pwd)
    {
        try {
            flag="0";
            int r=0;
            Connection con = connection.CONN();
            if (con == null) {
                // z = "Error in connection with SQL server";
            } else {

                //String query = "select EMP_CODE, EMP_NAME,MAC_ID,ACCESS_FULL_YN from emplmast where MAC_ID in (select MAC_ID from emplmast where MOBILE_NO='"+lemail+"' and PASS_WORD='"+lpass+"' AND MAC_ID='"+IMEINumber+"')";
                String query = "select  MOBILE_NO ,PASS_WORD  from APKLOGIN where MOBILE_NO='"+IMEINumber+"' and PASS_WORD='"+pwd+"'";

                Statement stmt = con.createStatement();
                ResultSet rs= stmt.executeQuery(query);
                while (rs.next()) {
                    r=1;
                }
                if(r>0)
                {
                    flag="1";
                }
                else
                {
                    Toast.makeText(IPAdderss_Activity.this, "Invalid Password", Toast.LENGTH_SHORT).show();

                }

                con.close();

            }

        } catch (Exception e) {
        }
    }
    private void deleteAppData() {
        try {
            // clearing app data
            String packageName = getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear "+packageName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void ping()
    {
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    try{
                        InetAddress address = InetAddress.getByName(str_ip_addr);
                        // InetAddress address = InetAddress.getByName("192.168.29.1511");
                        reachable = address.isReachable(1000);
                        // txt.setText("Is host reachable? \n"+reachable);
                        System.out.println("Is host reachable? " + reachable);
                        Log.d("ssss",""+reachable);
                        if(reachable)
                        {
                            new Thread()
                            {
                                public void run()
                                {
                                    IPAdderss_Activity.this.runOnUiThread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            SharedPreferences pref = getApplicationContext().getSharedPreferences("IPADDR", MODE_PRIVATE); // 0 - for private mode
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("ipaddress", str_ip_addr);
                                            editor.putString("portnumber", str_port_number);
                                            editor.putInt("TAB_CODE", m_TAB_CODE);
                                            editor.putString("str_wine_shop", str_wine_shop);
                                            editor.putString("db", str_CLOUD_SERVER_BACKUP_DATABASE);
                                            editor.commit();
                                            SharedPreferences pf = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                                            SharedPreferences.Editor edtr = pf.edit();
                                            edtr.putString("session", "1");
                                            edtr.commit();
                                            Intent i = new Intent(getApplicationContext(), Home.class);
                                            startActivity(i);
                                            finish();

                                        }
                                    });
                                }
                            }.start();

                        }
                        else
                        {
                            new Thread()
                            {
                                public void run()
                                {
                                    IPAdderss_Activity.this.runOnUiThread(new Runnable()
                                    {
                                        public void run()
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(IPAdderss_Activity.this, R.style.AppCompatAlertDialogStyle);
                                            builder.setTitle("Connection Error..!");
                                            builder.setIcon(R.drawable.warn);
                                            builder.setMessage("Please Check Your Ip Address Connection First Then Try Again..");
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //sessionManager.logoutUser();
                                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putString("session","0");
                                                    editor.putString("flag","1");
                                                    editor.commit();
                                                   /* Intent i=new Intent(getApplicationContext(),Login.class);
                                                    startActivity(i);
                                                    finish();*/
                                                    dialog.dismiss();

                                                }
                                            });
                                            // builder.setNegativeButton("Cancel", null);
                                            builder.show();
                                        }
                                    });
                                }
                            }.start();

                        }


                    } catch (Exception e){
                        e.printStackTrace();
                        new Thread()
                        {
                            public void run()
                            {
                                IPAdderss_Activity.this.runOnUiThread(new Runnable()
                                {
                                    public void run()
                                    {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(IPAdderss_Activity.this, R.style.AppCompatAlertDialogStyle);
                                        builder.setTitle("Connection Error..!");
                                        builder.setIcon(R.drawable.warn);
                                        builder.setMessage("Please Check Your Ip Address Connection First Then Try Again..");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //sessionManager.logoutUser();
                                                SharedPreferences pref = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                                                SharedPreferences.Editor editor = pref.edit();
                                                editor.putString("session","0");
                                                editor.putString("flag","1");
                                                editor.commit();
                                                Intent i=new Intent(getApplicationContext(),Login.class);
                                                startActivity(i);

                                                finish();

                                            }
                                        });
                                        // builder.setNegativeButton("Cancel", null);
                                        builder.show();
                                    }
                                });
                            }
                        }.start();

                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    new Thread()
                    {
                        public void run()
                        {
                            IPAdderss_Activity.this.runOnUiThread(new Runnable()
                            {
                                public void run()
                                {
                                    //Do your UI operations like dialog opening or Toast here
                                    AlertDialog.Builder builder = new AlertDialog.Builder(IPAdderss_Activity.this, R.style.AppCompatAlertDialogStyle);
                                    builder.setTitle("Connection Error..!");
                                    builder.setIcon(R.drawable.warn);
                                    builder.setMessage("Please Check Your Ip Address Connection First Then Try Again..");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //sessionManager.logoutUser();
                                            SharedPreferences pref = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("session","0");
                                            editor.putString("flag","1");
                                            editor.commit();
                                            Intent i=new Intent(getApplicationContext(),Login.class);
                                            startActivity(i);

                                            finish();

                                        }
                                    });
                                    // builder.setNegativeButton("Cancel", null);
                                    builder.show();
                                }
                            });
                        }
                    }.start();
                }
            }
        }).start();
    }
    void alert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(IPAdderss_Activity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Expired");
        builder.setIcon(R.drawable.warning);
        builder.setMessage("This Application Has Been Expired"+"\n"+"Please Contact Pranali Infotech, Nashik\n"+"0253-4062999, Mob.: 9373062271");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
        builder.setCancelable(false);
    }

    void soft_alert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(IPAdderss_Activity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Expiry Alert");
        builder.setIcon(R.drawable.warning);
        builder.setMessage(expiry_alert+"\n"+"Please Contact Pranali Infotech,Nashik\n"+"0253-4062999, Mob.: 9373062271");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
                finish();
            }
        });

        builder.show();
        builder.setCancelable(false);
    }
}
