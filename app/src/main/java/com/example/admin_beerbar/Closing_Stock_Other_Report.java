package com.example.admin_beerbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Closing_Stock_Other_Report extends AppCompatActivity {
   String check_id="";
    String db,cdate,Query_date,str_txt_per,chksummary,stock_wise_radioButton,stock_in_radioButton;
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,loc,str_price_type_title;
    TextView txt_total,txt_sale_total;
    Double total=0.0;
    Double sale_total=0.0;
    String qry="",IMEINumber,systemDate;
    Connection con;
    PreparedStatement ps1;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    Toolbar toolbar;
    int m_TAB_CODE;
    int srno=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing_stock_other_report);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
       // lin_summary_hide=(LinearLayout)findViewById(R.id.lin_summary_hide);
       // lin_heading_hide=(LinearLayout)findViewById(R.id.lin_heading_hide);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        systemDate = df.format(c);

        Bundle bb = getIntent().getExtras();
        try {
            cdate=bb.getString("date");
            Query_date=bb.getString("Query_date");
            loc=bb.getString("loc");

        } catch (Exception e) { }

        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_location = (TextView) toolbar.findViewById(R.id.toolbar_location);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText(""+str_compdesc+""+"\n"+"Closing Stock Other");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(cdate);
        toolbar_to_date.setTextColor(0xFFFFFFFF);
        toolbar_location.setText(loc);
        toolbar_location.setTextColor(0xFFFFFFFF);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        pbbar = (ProgressBar)findViewById(R.id.pgb);
        SharedPreferences sp1 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getApplicationContext(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        txt_total=(TextView)findViewById(R.id.txt_total);
        txt_sale_total=(TextView)findViewById(R.id.txt_sale_total);

                         connectionClass = new Config();
                         try {
                             pbbar.setVisibility(View.VISIBLE);
                             con = connectionClass.CONN(con_ipaddress, portnumber,db);
                             if (con == null) {
                                 Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
                             }
                             else {
                                 qry = "select ac_head_id,menuitem_desc,STR(SUM(gl_opbal),12,2) AS closing,STR(ROUND(AMOUNT_1,0),12,2) as basic_rate, STR(SUM(ROUND(AMOUNT_1,0)*gl_opbal),12,2) as amount,0 AS SEQNO,menu_desc AS m_desc from tabreportparameters,menumast,menucarditemmast WHERE tabreportparameters.ac_head_id=menumast.menu_code and tabreportparameters.item_code=menucarditemmast.menuitem_code AND TAB_CODE="+m_TAB_CODE+" GROUP BY ac_head_id,menu_desc,menuitem_desc,AMOUNT_1  having SUM(gl_opbal) > 0 UNION select ac_head_id,'Group Total ('+menu_desc+')',STR(SUM(gl_opbal),12,2) AS closing,'', STR(SUM(ROUND(AMOUNT_1,0)*gl_opbal),12,2) as amount,1 AS SEQNO,menu_desc from tabreportparameters,menumast,menucarditemmast WHERE tabreportparameters.ac_head_id=menumast.menu_code and tabreportparameters.item_code=menucarditemmast.menuitem_code AND TAB_CODE="+m_TAB_CODE+" GROUP BY ac_head_id,menu_desc order by m_desc,seqno,menuitem_desc,AC_HEAD_ID";
                                 Log.d("qry", qry);
                                 PreparedStatement ps = con.prepareStatement(qry);
                                 ResultSet rs = ps.executeQuery();
                                 total=0.0;
                                 sale_total=0.0;
                                 srno=0;
                                 HashMap<String, String> map2 = new HashMap<String, String>();
                                 while (rs.next()) {
                                     HashMap<String, String> map = new HashMap<String, String>();

                                     map.put("menuitem_desc", rs.getString("menuitem_desc"));
                                     map.put("closing", rs.getString("closing"));
                                     map.put("basic_rate", rs.getString("basic_rate"));
                                     map.put("amount", rs.getString("amount"));
                                     map.put("seqno", rs.getString("seqno"));

                                     if (rs.getString("seqno").equals("1")) {

                                         total = total + Double.parseDouble(rs.getString("amount"));
                                         sale_total = sale_total + Double.parseDouble(rs.getString("closing"));
                                     }
                                     menu_card_arryList.add(map);

                                 }
                                 NumberFormat n = new DecimalFormat(".00");
                                 txt_total.setText("" + n.format(total));
                                 txt_sale_total.setText("" + n.format(sale_total));
                             }
                             pbbar.setVisibility(View.GONE);
                             Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

                             if (attendance_recyclerAdapter != null) {
                                 attendance_recyclerAdapter.notifyDataSetChanged();
                                 System.out.println("Adapter " + attendance_recyclerAdapter.toString());
                             }

                         } catch (Exception e) {
                             Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
                         }
                       //--------------------------------------------
                    }

                    //Toast.makeText(getApplicationContext(), "check_id: " + check_id, Toast.LENGTH_SHORT).show();


    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
    }

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.closing_stock_other_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

           // holder.list_d1.setText(attendance_list.get(position).get("srno"));
            holder.list_d2.setText(attendance_list.get(position).get("menuitem_desc"));
            holder.list_d3.setText(attendance_list.get(position).get("closing"));
            holder.list_d4.setText(attendance_list.get(position).get("basic_rate"));
            holder.list_d5.setText(attendance_list.get(position).get("amount"));
            if(attendance_list.get(position).get("seqno").equals("1"))
            {
                holder.list_d2.setTextColor(Color.BLUE);
                holder.list_d3.setTextColor(Color.RED);
                holder.list_d5.setTextColor(Color.RED);
            }
            else
            {
                holder.list_d2.setTextColor(Color.BLACK);
                holder.list_d3.setTextColor(Color.BLACK);
                holder.list_d5.setTextColor(Color.BLACK);
            }

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3, list_d4, list_d5;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
               // this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);
            }
        }
    }


}
