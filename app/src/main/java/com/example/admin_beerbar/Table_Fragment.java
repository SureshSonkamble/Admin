package com.example.admin_beerbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.admin_beerbar.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Table_Fragment extends Fragment {
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    //String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
   // ConnectionClass connectionClass;
    GridView lstpro;
    AlertDialog dialog;
    ArrayList<HashMap<String, String>> contact_arryList;
    TransparentProgressDialog pd;
    TextView txt_total_table_bill;
    String doc_dt, doc_dt_display;
    String search_word,value;
    int m_TAB_CODE;
    String con_ipaddress ,portnumber;
    SearchView place_searchView;
    String tab_user_code,tab_user_name,db;
    String TBNO_CODE,TBNO_DESC,SERVICE_TAX_PER,CGST_PER,SGST_PER,TABL_VALUE;
    int m_WATRCODE =0;
    int m_BRANDCLUBYN =0;
    Double Total_Table_Bill=0.0;
    Double Last_total=0.0;
    int m_swap;
    DecimalFormat d;
    ScheduledExecutorService scheduler;
    int flag=0;
    String val1,val2;
    public Table_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        d = new DecimalFormat("0.00");
        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");

        //============REFRESH====================================

        //==========Date=========================
        try {
            con = CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
            } else {

                PreparedStatement ps = con.prepareStatement("select isnull(convert(varchar(10),max(doc_dt),101),convert(varchar(10),getdate(),101)) from countersaleitem");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    doc_dt = rs.getString(1);
                }
                PreparedStatement p = con.prepareStatement("select isnull(convert(varchar(10),max(doc_dt),103),convert(varchar(10),getdate(),103)) from countersaleitem");
                ResultSet r = p.executeQuery();
                while (r.next()) {
                    doc_dt_display = r.getString(1);
                }
            }
        }catch (Exception e)
        {
        }
        //======================================
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            con = CONN(con_ipaddress,portnumber,db);
                            if (con == null) {
                                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

                            } else {

                                PreparedStatement ps = con.prepareStatement("select isnull(sum(item_value),0) as item_value from countersaleitem" );
                                ResultSet rs = ps.executeQuery();
                                while (rs.next()) {
                                    Last_total= rs.getDouble("item_value");
                                }
                            }

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
                        }
                      Toast.makeText(getActivity(), "It works", Toast.LENGTH_SHORT).show();
                      if (Total_Table_Bill!=Last_total){
                            new load_table().execute();
                        }

                    }
                });
            }
        }, 10, 10, TimeUnit.SECONDS);
        //================================================
        SharedPreferences sp = getActivity().getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        tab_user_name = sp.getString("tab_user_name", "");
        tab_user_code = sp.getString("tab_user_code", "");
        tab_user_code="1";

        txt_total_table_bill=(TextView)view.findViewById(R.id.txt_total_table_bill);
        //--------- search ------------
        place_searchView=(SearchView)view.findViewById(R.id.place_searchView);
        place_searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        place_searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                // new Attendance_list().execute();
                new load_all_table().execute();
                return false;
            }
        });

        place_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >0)
                {   search_word=newText;
                    //new FetchSearchResult().execute();
                   new table_search().execute();
                }
                else  if (TextUtils.isEmpty(newText)){
                    //new Attendance_list().execute();
                    new load_all_table().execute();
                }
                else
                { }
                return false;
            }
        });
        //********************
        pd = new TransparentProgressDialog(getActivity(), R.drawable.busy);

        con = CONN(con_ipaddress,portnumber,db);
        contact_arryList = new ArrayList<HashMap<String, String>>();
        lstpro = (GridView) view.findViewById(R.id.lv);

        new load_table().execute();
    }
    public class load_table extends AsyncTask<String,String,String>
    {
        List<Map<String, String>> prolist  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            //String query = "SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"' ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1)";
            String query = "SELECT TBNO_CODE,TBNO_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE COUNTERSALEITEM.TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE,(SELECT MAX(BILL_NO) FROM COUNTERSALEITEM WHERE COUNTERSALEITEM.TBNO_CODE = TBNOMAST.TBNO_CODE) AS BILL_NO FROM TBNOMAST,SECTIONMAST WHERE TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND TBNO_CODE IN(SELECT TBNO_CODE FROM COUNTERSALEITEM) ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1)";
            Total_Table_Bill=0.0;
            int m_exit_yn=0;
            while (true) {
                try {
                    con = CONN(con_ipaddress,portnumber,db);
                    if (con == null) {
                        Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                    } else {
                        // String query = "SELECT TBNO_CODE,TBNO_DESC FROM TBNOMAST";
                        // String query = "select TBNO_DESC from tabrights,TBNOMAST where tabrights.TBNO_CODE = TBNOMAST.TBNO_CODE AND TABUSER_CODE='"+tab_user_code+"'";
                        PreparedStatement ps = con.prepareStatement(query);
                        ResultSet rs = ps.executeQuery();
                        m_exit_yn=1;
                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {

                            Map<String, String> datanum = new HashMap<String, String>();
                            TBNO_CODE= rs.getString(1);
                            TBNO_DESC= rs.getString(2);
                            SERVICE_TAX_PER= rs.getString(3);
                            CGST_PER= rs.getString(4);
                            SGST_PER= rs.getString(5);
                            TABL_VALUE = rs.getString("TABL_VALUE");
                            Total_Table_Bill=Total_Table_Bill+Double.parseDouble(rs.getString("TABL_VALUE"));
                            Last_total=Last_total+Double.parseDouble(rs.getString("TABL_VALUE"));
                            datanum.put("TBNO_CODE", TBNO_CODE);
                            datanum.put("TBNO_DESC", TBNO_DESC);
                            datanum.put("SERVICE_TAX_PER", SERVICE_TAX_PER);
                            datanum.put("CGST_PER", CGST_PER);
                            datanum.put("SGST_PER", SGST_PER);
                            datanum.put("TABL_VALUE", TABL_VALUE);
                            datanum.put("BILL_NO", rs.getString("BILL_NO"));

                            prolist.add(datanum);

                        }
                        txt_total_table_bill.setText(""+d.format(Total_Table_Bill));
                        //m_swap=prolist.size();

                    }

                } catch (Exception e) {


                }
                if (m_exit_yn==1){
                    break;
                }
            }
                    return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            String[] from = { "TBNO_DESC","TABL_VALUE"};
            int[] views = {R.id.txt_d3,R.id.txt_d4};
            SimpleAdapter ADA = null;
           /* if(flag==1) {
                ADA = new SimpleAdapter(getActivity(), prolist, R.layout.black_table_list_test, from, views);
            }
            else {
                 ADA = new SimpleAdapter(getActivity(), prolist, R.layout.table_list_test, from, views);
            }*/


           //=========================================
          /*  for (Map<String, String> distro : prolist) {

                System.out.println(distro);
                Toast.makeText(getActivity(), ""+distro, Toast.LENGTH_SHORT).show();
            }*/
            Map<String, String> myMap ;
            for (int j=0; j<prolist.size(); j++)
            {
                myMap = prolist.get(j);

                 val1= myMap.get("TBNO_DESC");
                 val2 = myMap.get("TABL_VALUE");
                Log.d("aa",val1);
                Log.d("aa",val2);
                Log.d("aa",""+j);

                double v=Double.parseDouble(myMap.get("BILL_NO"));
                if(v>0)
                {
                    lstpro.setBackgroundColor(Color.RED);

                  //  ADA = new SimpleAdapter(getActivity(), prolist, R.layout.black_table_list_test, from, views);
                }
                else
                {
                    lstpro.setBackgroundColor(Color.BLUE);
                  //  ADA = new SimpleAdapter(getActivity(), prolist, R.layout.table_list_test, from, views);
                }
            }

            //=========================================
            ADA = new SimpleAdapter(getActivity(), prolist, R.layout.table_list_test, from, views);
            lstpro.setAdapter(ADA);
            final SimpleAdapter finalADA = ADA;
            lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    pd.show();
                   // String tbno_code=ADA.getItem(position);
                    HashMap<String,Object> obj=(HashMap<String,Object>) finalADA.getItem(position);
                    TBNO_CODE=(String)obj.get("TBNO_CODE");
                    TBNO_DESC=(String)obj.get("TBNO_DESC");
                    SERVICE_TAX_PER=(String)obj.get("SERVICE_TAX_PER");
                    CGST_PER=(String)obj.get("CGST_PER");
                    SGST_PER=(String)obj.get("SGST_PER");
                    TABL_VALUE=(String)obj.get("TABL_VALUE");

                    SharedPreferences pref = getActivity().getSharedPreferences("HOME_DATA", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("TBNO_CODE",TBNO_CODE);
                    editor.putString("TBNO_DESC",TBNO_DESC);
                    editor.putString("SERVICE_TAX_PER",SERVICE_TAX_PER);
                    editor.putString("CGST_PER",CGST_PER);
                    editor.putString("SGST_PER",SGST_PER);

                    editor.commit();

                    Intent i=new Intent(getContext(),Table_Report_Activity.class);
                    i.putExtra("doc_dt",doc_dt);
                    i.putExtra("doc_dt_display",doc_dt_display);
                    startActivity(i);
                    pd.dismiss();
                }
            });

            super.onPostExecute(s);
        }
    }

    public class load_all_table extends AsyncTask<String,String,String>
    {
        List<Map<String, String>> prolist  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                con = CONN(con_ipaddress,portnumber,db);
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                   // String query = "select TBNO_DESC from tabrights,TBNOMAST where tabrights.TBNO_CODE = TBNOMAST.TBNO_CODE AND TABUSER_CODE='"+tab_user_code+"'";
                    String query = "SELECT TBNO_CODE,TBNO_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE COUNTERSALEITEM.TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TBNOMAST,SECTIONMAST WHERE TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND TBNO_CODE IN(SELECT TBNO_CODE FROM COUNTERSALEITEM) ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1) ";
                    // String query = "SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"'";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    prolist.clear();
                    Total_Table_Bill=0.0;
                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {

                        Map<String, String> datanum = new HashMap<String, String>();
                        TBNO_CODE= rs.getString(1);
                        TBNO_DESC= rs.getString(2);
                        SERVICE_TAX_PER= rs.getString(3);
                        CGST_PER= rs.getString(4);
                        SGST_PER= rs.getString(5);
                        TABL_VALUE= rs.getString("TABL_VALUE");
                        Total_Table_Bill=Total_Table_Bill+Double.parseDouble(rs.getString("TABL_VALUE"));
                        datanum.put("TBNO_CODE", TBNO_CODE);
                        datanum.put("TBNO_DESC", TBNO_DESC);
                        datanum.put("SERVICE_TAX_PER", SERVICE_TAX_PER);
                        datanum.put("CGST_PER", CGST_PER);
                        datanum.put("SGST_PER", SGST_PER);
                        datanum.put("TABL_VALUE", TABL_VALUE);

                        prolist.add(datanum);

                    }
                    txt_total_table_bill.setText(""+d.format(Total_Table_Bill));

                }

            }catch (Exception e)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            String[] from = {"TBNO_DESC","TABL_VALUE"};
            int[] views = {R.id.txt_d3,R.id.txt_d4};
            final SimpleAdapter ADA = new SimpleAdapter(getActivity(), prolist, R.layout.table_list_test, from, views);
            lstpro.setAdapter(ADA);

            lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pd.show();
                    HashMap<String,Object> obj=(HashMap<String,Object>)ADA.getItem(position);
                    TBNO_CODE=(String)obj.get("TBNO_CODE");
                    TBNO_DESC=(String)obj.get("TBNO_DESC");
                    SERVICE_TAX_PER=(String)obj.get("SERVICE_TAX_PER");
                    CGST_PER=(String)obj.get("CGST_PER");
                    SGST_PER=(String)obj.get("SGST_PER");
                    TABL_VALUE=(String)obj.get("TABL_VALUE");

                    SharedPreferences pref = getActivity().getSharedPreferences("HOME_DATA", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("TBNO_CODE",TBNO_CODE);
                    editor.putString("TBNO_DESC",TBNO_DESC);
                    editor.putString("SERVICE_TAX_PER",SERVICE_TAX_PER);
                    editor.putString("CGST_PER",CGST_PER);
                    editor.putString("SGST_PER",SGST_PER);
                    editor.commit();

                    Intent i=new Intent(getContext(),Table_Report_Activity.class);
                    i.putExtra("doc_dt",doc_dt);
                    i.putExtra("doc_dt_display",doc_dt_display);
                    startActivity(i);
                    pd.show();
                }
            });

            super.onPostExecute(s);
        }
    }

    public class table_search extends AsyncTask<String,String,String>
    {
        List<Map<String, String>> prolist  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                con = CONN(con_ipaddress,portnumber,db);
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    //String query = "SELECT TABRIGHTS.TBNO_CODE,TBNO_DESC,RATETYPE_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TABRIGHTS,TBNOMAST,SECTIONMAST,RATETYPEMAST WHERE TABRIGHTS.TBNO_CODE=TBNOMAST.TBNO_CODE AND TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND SECTIONMAST.RATETYPE_CODE=RATETYPEMAST.RATETYPE_CODE AND TABUSER_CODE='"+tab_user_code+"' AND TBNO_DESC LIKE '"+search_word+"%' ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1)";
                    String query = "SELECT TBNO_CODE,TBNO_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE COUNTERSALEITEM.TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE FROM TBNOMAST,SECTIONMAST WHERE TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND TBNO_DESC LIKE '"+search_word+"%' AND TBNO_CODE IN(SELECT TBNO_CODE FROM COUNTERSALEITEM) ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1) ";

                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();
                    Total_Table_Bill=0.0;
                    prolist.clear();
                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {

                        Map<String, String> datanum = new HashMap<String, String>();
                        TBNO_CODE= rs.getString(1);
                        TBNO_DESC= rs.getString(2);
                        SERVICE_TAX_PER= rs.getString(3);
                        CGST_PER= rs.getString(4);
                        SGST_PER= rs.getString(5);
                        TABL_VALUE= rs.getString("TABL_VALUE");
                        Total_Table_Bill=Total_Table_Bill+Double.parseDouble(rs.getString("TABL_VALUE"));
                        datanum.put("TBNO_CODE", TBNO_CODE);
                        datanum.put("TBNO_DESC", TBNO_DESC);
                        datanum.put("SERVICE_TAX_PER", SERVICE_TAX_PER);
                        datanum.put("CGST_PER", CGST_PER);
                        datanum.put("SGST_PER", SGST_PER);
                        datanum.put("TABL_VALUE", TABL_VALUE);

                        prolist.add(datanum);

                    }
                    txt_total_table_bill.setText(""+d.format(Total_Table_Bill));

                }

            }catch (Exception e)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }

            String[] from = {"TBNO_DESC","TABL_VALUE"};
            int[] views = {R.id.txt_d3,R.id.txt_d4};
            final SimpleAdapter ADA = new SimpleAdapter(getActivity(), prolist, R.layout.table_list_test, from, views);
            lstpro.setAdapter(ADA);

            lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pd.show();
                    HashMap<String,Object> obj=(HashMap<String,Object>)ADA.getItem(position);
                    TBNO_CODE=(String)obj.get("TBNO_CODE");
                    TBNO_DESC=(String)obj.get("TBNO_DESC");
                    SERVICE_TAX_PER=(String)obj.get("SERVICE_TAX_PER");
                    CGST_PER=(String)obj.get("CGST_PER");
                    SGST_PER=(String)obj.get("SGST_PER");

                    SharedPreferences pref = getActivity().getSharedPreferences("HOME_DATA", MODE_PRIVATE); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("TBNO_CODE",TBNO_CODE);
                    editor.putString("TBNO_DESC",TBNO_DESC);
                    editor.putString("SERVICE_TAX_PER",SERVICE_TAX_PER);
                    editor.putString("CGST_PER",CGST_PER);
                    editor.putString("SGST_PER",SGST_PER);
                    editor.commit();
                    Intent i=new Intent(getContext(),Table_Report_Activity.class);
                    i.putExtra("doc_dt",doc_dt);
                    i.putExtra("doc_dt_display",doc_dt_display);
                    startActivity(i);
                    pd.show();

                }
            });

            super.onPostExecute(s);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {

            con = CONN(con_ipaddress,portnumber,db);

            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                //String query="select size_code,size_desc from sizemast";

                String query = "select WATR_CODE,BRAND_CLUB_YN from tabusermast WHERE TABUSER_code='"+tab_user_code+"'";
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    m_WATRCODE=rs.getInt("WATR_CODE");
                    m_BRANDCLUBYN=rs.getInt("BRAND_CLUB_YN");
                }

            }  //z = "Success";


        } catch (Exception e) {

        }
        new load_all_table().execute();
        //Table_Fragment rSum = new Table_Fragment();
       // getActivity().getSupportFragmentManager().beginTransaction().remove(rSum).commit();
    }

    public Connection CONN(String ip, String port,String db) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll().build();

        StrictMode.setThreadPolicy(policy);

        con = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";;
            con = DriverManager.getConnection(ConnURL);

        } catch (SQLException se) {

            Log.e("ERRO", se.getMessage());

        } catch (ClassNotFoundException e) {

            Log.e("ERRO", e.getMessage());

        } catch (Exception e) {

            Log.e("ERRO", e.getMessage());

        }

        return con;

    }
}
