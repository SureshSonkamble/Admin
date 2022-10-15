package com.example.admin_beerbar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.admin_beerbar.Class.ConnectionClass;
import com.example.admin_beerbar.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serial_Number_Activity extends AppCompatActivity {
     EditText edt_ip_addr,edt_port_number;
     TextView txt_msg;
    String session="0";
    Toolbar toolbar;
    String str_wine_shop,str_ip_addr,str_port_number;
    Button btn_ip_addr;
    Spinner spinner_wine_shop;
    String con_ipaddress ,portnumber;
    String IMEINumber,m_ip_address,m_wine_shop;
    Config connectionClass;
    Connection con;
    ConnectionClass connection;
    int m_TAB_CODE;
    ProgressDialog progressDoalog;
    String m_local_mac_id="D8:32:E3:3C:F1:C2,40:A1:8:1A:60:3C,A4:4B:D5:6E:40:30";
    int val=0;
    TransparentProgressDialog pd;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipaddress_);
        connection = new ConnectionClass();
        pd = new TransparentProgressDialog(Serial_Number_Activity.this, R.drawable.hourglass);
        spinner_wine_shop=(Spinner)findViewById(R.id.spinner_wine_shop);
       /* try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            IMEINumber ="";
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                IMEINumber=res1.toString().toUpperCase();
                Log.d("IMEINumber",IMEINumber);
                SharedPreferences pref = getApplicationContext().getSharedPreferences("IMEINumber", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("IMEINumber",IMEINumber);
                editor.commit();
            }

        } catch (Exception ex) {
            //handle exception
        }*/
         //==============Serial Number================
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    IMEINumber=Build.getSerial();
                    Log.d("IMEINumber",IMEINumber);
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("IMEINumber", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("IMEINumber",IMEINumber);
                    editor.commit();
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        IMEINumber=Build.getSerial();
        Log.d("IMEINumber",IMEINumber);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("IMEINumber", MODE_PRIVATE); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("IMEINumber",IMEINumber);
        editor.commit();
        //==============================================

        Toast.makeText(getApplicationContext(), "Loading Data Please Wait..", Toast.LENGTH_LONG).show();
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
        SharedPreferences  sp = getSharedPreferences("IP", MODE_PRIVATE);
        session = sp.getString("session", "");
        if(session.equals("1"))
        {
            Intent i=new Intent(getApplicationContext(),Home.class);
            startActivity(i);
            finish();
        }
        //----------------------------------------------
        btn_ip_addr=(Button) findViewById(R.id.btn_ip_addr);

        txt_msg=(TextView)findViewById(R.id.txt_msg);
        txt_msg.setPaintFlags(txt_msg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btn_ip_addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                                SharedPreferences pref = getApplicationContext().getSharedPreferences("IPADDR", MODE_PRIVATE); // 0 - for private mode
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("ipaddress", str_ip_addr);
                                editor.putString("portnumber", str_port_number);
                                editor.putInt("TAB_CODE", m_TAB_CODE);
                                editor.putString("str_wine_shop", str_wine_shop);
                                editor.commit();
                                SharedPreferences pf = getApplicationContext().getSharedPreferences("IP", MODE_PRIVATE); // 0 - for private mode
                                SharedPreferences.Editor edtr = pf.edit();
                                edtr.putString("session", "1");
                                edtr.commit();
                                Intent i = new Intent(getApplicationContext(), Home.class);
                                startActivity(i);
                                finish();

                    }
                }, 2000);

            }
        });

        //--------------------------------------------------

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
                    //String q="SELECT LTRIM(RTRIM(COMP_NAME)) AS SHOP_NAME,LTRIM(RTRIM(CONNECTION_STRING)) AS IP FROM APKCONNECTION WHERE APK_GROUP_CODE IN (SELECT APK_GROUP_CODE FROM APKGROUPMAST WHERE APK_TYPE = 1 AND MAC_ID LIKE '"+'%'+IMEINumber+'%'+"')";
                    String q="SELECT LTRIM(RTRIM(COMP_NAME)) AS SHOP_NAME,LTRIM(RTRIM(CONNECTION_STRING)) AS IP,(SELECT TOP 1 TAB_CODE FROM TABUSERMAST WHERE MAC_ID = '"+IMEINumber+"') AS TAB_CODE,PORT_NO FROM APKCONNECTION WHERE APK_GROUP_CODE IN (SELECT APK_GROUP_CODE FROM APKGROUPMAST WHERE APK_TYPE = 1 AND MAC_ID LIKE '"+'%'+IMEINumber+'%'+"')";
                    PreparedStatement ps = con.prepareStatement(q);
                    ResultSet rs = ps.executeQuery();
                    //ArrayList data1 = new ArrayList();
                    hw_data.clear();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("A", rs.getString(1));
                        data.put("B", rs.getString(2));
                        data.put("C", rs.getString(4));
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
            String[] from = {"A", "B","C"};
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
                    str_port_number = (String) obj.get("C");
                    //   String text = sp_food_test.getSelectedItem().toString();

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(Serial_Number_Activity.this, R.style.AppCompatAlertDialogStyle);
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

}
