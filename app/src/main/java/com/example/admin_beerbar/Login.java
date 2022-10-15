package com.example.admin_beerbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.admin_beerbar.Class.ConnectionClass;
import com.example.admin_beerbar.Class.SessionManager;
import com.example.admin_beerbar.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Login extends AppCompatActivity {
    ProgressDialog progressDoalog;
    SessionManager sessionManager;
    SharedPreferences sp_pi_login,sp_edit;
    SharedPreferences.Editor editor_sp_pi_login;
    ConnectionClass connectionClass;
    EditText edit_login_email,edit_login_pass;
    TextView btn_login;
    Toolbar toolbar;
    String user_name,password,mac_id,flag="0",mob,pass;
    float apk_code;
    TransparentProgressDialog pd;
    ProgressBar pbbar;
    String m_androidId,device_id,group_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pd = new TransparentProgressDialog(Login.this, R.drawable.hourglass);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Login");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        sessionManager = new SessionManager(this);
        sp_pi_login = getSharedPreferences("PI", MODE_PRIVATE);
        editor_sp_pi_login = sp_pi_login.edit();

      /*  if (sessionManager.isLoggedIn()) {

            String user=sp_pi_login.getString("email",null);
            Intent i=new Intent(getApplicationContext(),IPAdderss_Activity.class);
            i.putExtra("email",user );
            startActivity(i);
            finish();

        }*/
        edit_login_email = (EditText) findViewById(R.id.edt_uname);
        edit_login_pass = (EditText) findViewById(R.id.edt_pass);
        connectionClass = new ConnectionClass();
        m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("m_androidId",m_androidId);


        btn_login = (TextView) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i=new Intent(getApplicationContext(),Home.class);
                // startActivity(i);
                if(edit_login_email.getText().toString().length()==0) {
                    edit_login_email.setError("Input User name missing");
                    return;
                }
                else if (edit_login_pass.getText().toString().length()==0){
                    edit_login_pass.setError("Input Password missing");
                    return;
                }
                else {

                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {


                    //============Update Device ID===================
                       try {
            Connection con = connectionClass.CONN();
            // conn=CONN(con_ipaddress, portnumber);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
            } else {

                String query = "select APK_GROUP_CODE,DEVICE_ID,MOBILE_NO,PASS_WORD from APKLOGIN where MOBILE_NO='"+edit_login_email.getText().toString()+"' and PASS_WORD='"+edit_login_pass.getText().toString()+"'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next())
                {
                    device_id=rs.getString("DEVICE_ID");
                    group_code=rs.getString("APK_GROUP_CODE");
                   // new login().execute();
                }
                if(device_id.equals(""))
                {
                    PreparedStatement ps1 = con.prepareStatement("UPDATE APKLOGIN SET DEVICE_ID='" + m_androidId + "' WHERE MOBILE_NO='"+edit_login_email.getText().toString()+"' AND APK_GROUP_CODE='"+group_code+"'");
                    ps1.executeUpdate();
                    new login().execute();
                }
                else
                {
                    new login().execute();
                }
            }
        } catch (Exception e) {
                           Toast.makeText(getApplicationContext(), "Invalid Credentials.", Toast.LENGTH_SHORT).show();
                     //  Toast.makeText(getApplicationContext(), "136-Login\n"+e, Toast.LENGTH_SHORT).show();
                     }
                    //--------------------------------------------------*/
                   // new login().execute();

                            pd.dismiss();
                        }
                    }, 3000);
                }

            }
        });

        SharedPreferences  spp = getSharedPreferences("IP", MODE_PRIVATE);
        flag = spp.getString("flag", "");
        mob = spp.getString("mob", "");
        pass = spp.getString("pass", "");
        if(flag.equals("1"))
        {
            edit_login_email.setText(mob);
            edit_login_pass.setText(pass);
            //sessionManager.logoutUser();
        }
    }

    public class login extends AsyncTask<String, String, String>
    {
        String z = "";
        Boolean isSuccess = false;
        String lemail = edit_login_email.getText().toString();
        String lpass = edit_login_pass.getText().toString();
        @Override
        protected void onPreExecute() {

            progressDoalog = new ProgressDialog(Login.this);
            progressDoalog.setMessage("Login....");
            progressDoalog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                Connection con = connectionClass.CONN();
                // conn=CONN(con_ipaddress, portnumber);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {

                 //  String query = "select APK_GROUP_CODE, USER_NAME,MOBILE_NO,PASS_WORD from APKLOGIN where MOBILE_NO='"+lemail+"' and PASS_WORD='"+lpass+"' and DEVICE_ID='"+m_androidId+"'";
                   String query = "select APK_TYPE, APKLOGIN.APK_GROUP_CODE, USER_NAME,MOBILE_NO,PASS_WORD from APKLOGIN,APKGROUPMAST where APKGROUPMAST.APK_GROUP_CODE=APKLOGIN.APK_GROUP_CODE and MOBILE_NO='"+lemail+"' and PASS_WORD='"+lpass+"' and DEVICE_ID='"+m_androidId+"' and APK_TYPE =2";
                // String query = "select APK_GROUP_CODE, USER_NAME,MOBILE_NO,PASS_WORD from APKLOGIN where MOBILE_NO='"+lemail+"' and PASS_WORD='"+lpass+"'";

                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next())
                    {
                        apk_code=rs.getFloat("APK_GROUP_CODE");
                        user_name=rs.getString("USER_NAME");
                        mac_id=rs.getString("MOBILE_NO");
                        password=rs.getString("PASS_WORD");
                            z = "Login successful";
                            isSuccess = true;

                        con.close();
                    }
                    else
                    {
                        z = "Invalid Credentials!";
                        isSuccess = false;
                    }
                }

            }catch(Exception e)
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDoalog.dismiss();

            if(isSuccess==true)
            {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("USER_DATA", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putFloat("apk_code",apk_code);
                editor.putString("user_name",user_name);
                editor.putString("mac_id",mac_id);
                editor.putString("password",password);
                editor.commit();
                sessionManager.createLoginSession(edit_login_email.getText().toString(), edit_login_pass.getText().toString());
                Toast.makeText(getApplicationContext(), "Login Successfull.", Toast.LENGTH_SHORT).show();
                editor_sp_pi_login.putString("email", lemail);
                editor_sp_pi_login.commit();

                Intent i = new Intent(Login.this, IPAdderss_Activity.class);
                i.putExtra("email", lemail);
                startActivity(i);
                finish();

            }
            else
            {
                alert();
               // Toast.makeText(getApplicationContext(), "Invalid Credentials.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

        void alert()
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Warning");
            builder.setIcon(R.drawable.alert);
            builder.setMessage("Your Device Is Not Registered.\n Please Registere Your Device  & Try Again..!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }
}

