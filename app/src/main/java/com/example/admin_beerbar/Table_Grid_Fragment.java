package com.example.admin_beerbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin_beerbar.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
public class Table_Grid_Fragment extends Fragment {
    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "BEERBAR";
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
    String search_word="",db;
    int m_TAB_CODE;
    String con_ipaddress ,portnumber;
    SearchView place_searchView;
    String tab_user_code,tab_user_name;
    String TBNO_CODE,TBNO_DESC,SERVICE_TAX_PER,CGST_PER,SGST_PER,TABL_VALUE;
    int m_WATRCODE =0;
    int m_BRANDCLUBYN =0;
    Double Total_Table_Bill=0.0;
    Double Last_total=0.0;
    int m_swap;
    DecimalFormat d;
    ScheduledExecutorService scheduler;
    int flag=0;
    String file,val2;

    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    public Table_Grid_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        d = new DecimalFormat("0.00");
        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        file = sp1.getString("file", "");

        //================================================
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) view.findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new GridLayoutManager(getActivity(), 3);
       // layoutManager_pe = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getActivity(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
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
                     // Toast.makeText(getActivity(), "It works", Toast.LENGTH_SHORT).show();
                      if (Total_Table_Bill!=Last_total){
                          //  new load_table().execute();
                          search_word="";
                          sales_data();
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
              //  new load_all_table().execute();
                sales_data();
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
                {   search_word="";
                    search_word=newText;
                    //new FetchSearchResult().execute();
                 //  new table_search().execute();
                    sales_data();
                }
                else  if (TextUtils.isEmpty(newText)){
                    //new Attendance_list().execute();
                 //   new load_all_table().execute();
                    search_word="";
                    sales_data();
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

       // new load_table().execute();
        sales_data();

    }

    public void sales_data() {

        try {
            pd.show();
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                PreparedStatement p = con.prepareStatement("delete from COUNTERSALEITEM where qty=0");
                p.executeUpdate();
                PreparedStatement ps = con.prepareStatement("SELECT TBNO_CODE,TBNO_DESC,CASE WHEN SERVICE_TAX_YN=0 THEN 0 ELSE (SELECT FOOD_SERVICE_TAX_PER FROM PROFILE) END AS FOOD_SRVICE_TAX,CGST_PER,SGST_PER,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM WHERE COUNTERSALEITEM.TBNO_CODE = TBNOMAST.TBNO_CODE),'') AS TABL_VALUE,(SELECT COUNT(*) FROM COUNTERSALEITEM WHERE PRINT_YN=0 AND COUNTERSALEITEM.TBNO_CODE = TBNOMAST.TBNO_CODE) AS BILL_NO,ISNULL((SELECT LTRIM(STR(SUM(ITEM_VALUE),12,2)) FROM COUNTERSALEITEM A),'') AS RUNNING_TOTAL FROM TBNOMAST,SECTIONMAST WHERE TBNOMAST.SECTION_CODE=SECTIONMAST.SECTION_CODE AND TBNO_DESC LIKE '%"+search_word+"%' AND TBNO_CODE IN(SELECT TBNO_CODE FROM COUNTERSALEITEM) ORDER BY left(tbno_desc,patINDEX('%[0-9]%',TBNO_DESC)-1),convert(float,right(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end,len(case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end) - patINDEX('%[0-9]%',case when right(tbno_desc,1) not in('A','B','C','D') then tbno_desc else left(tbno_desc,len(tbno_desc)-1) end)+1)),right(tbno_desc,1)");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                Total_Table_Bill=0.0;
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("TBNO_CODE", rs.getString(1));
                    map.put("TBNO_DESC", rs.getString(2));
                    map.put("SERVICE_TAX_PER", rs.getString(3));
                    map.put("CGST_PER", rs.getString(4));
                    map.put("SGST_PER", rs.getString(5));
                    map.put("TABL_VALUE", rs.getString("TABL_VALUE"));
                    map.put("BILL_NO", rs.getString("BILL_NO"));
                    if(!rs.getString("RUNNING_TOTAL").equals("")) {
                        //Total_Table_Bill = Total_Table_Bill + Double.parseDouble(rs.getString("TABL_VALUE"));
                        //Last_total = Last_total + Double.parseDouble(rs.getString("TABL_VALUE"));
                        Total_Table_Bill = Double.parseDouble(rs.getString("RUNNING_TOTAL"));
                        Last_total = Double.parseDouble(rs.getString("RUNNING_TOTAL"));
                    }
                    menu_card_arryList.add(map);

                }
                txt_total_table_bill.setText(""+d.format(Total_Table_Bill));
            }
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public atnds_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_list_test, parent, false);
            atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new atnds_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {

            // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.txt_d3.setText(attendance_list.get(position).get("TBNO_DESC"));
            holder.txt_d4.setText(attendance_list.get(position).get("TABL_VALUE"));

            double v=Double.parseDouble(attendance_list.get(position).get("BILL_NO"));
            if(v==0)
            {
               holder.lin.setBackgroundColor(Color.rgb(255, 198, 179));
            }
            else {
                holder.lin.setBackgroundColor(Color.rgb(255, 255, 204));

            }

            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run()
                        {
                            SharedPreferences pref = getActivity().getSharedPreferences("HOME_DATA", MODE_PRIVATE); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("TBNO_CODE",attendance_list.get(position).get("TBNO_CODE"));
                            editor.putString("TBNO_DESC",attendance_list.get(position).get("TBNO_DESC"));
                            editor.putString("SERVICE_TAX_PER",attendance_list.get(position).get("SERVICE_TAX_PER"));
                            editor.putString("CGST_PER",attendance_list.get(position).get("CGST_PER"));
                            editor.putString("SGST_PER",attendance_list.get(position).get("SGST_PER"));
                            editor.commit();

                            Intent i=new Intent(getContext(),Table_Report_Activity.class);
                            i.putExtra("doc_dt",doc_dt);
                            i.putExtra("doc_dt_display",doc_dt_display);
                            startActivity(i);
                            pd.dismiss();
                        }
                    }, 1000);

                }
            });
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView txt_d3, txt_d4;
            LinearLayout lin;
            ImageView img_tbl;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.txt_d3 = (TextView) itemView.findViewById(R.id.txt_d3);
                this.txt_d4 = (TextView) itemView.findViewById(R.id.txt_d4);
                this.img_tbl = (ImageView) itemView.findViewById(R.id.img_tbl);

            }
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
     //   new load_all_table().execute();
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
