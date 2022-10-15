package com.example.admin_beerbar;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Date_Tranfer_Note_Other_Date_Wise_Report extends AppCompatActivity {
   String check_id="";
    String Query_to_date,cdate,Query_frm_date,to_date,frm_date,db,stock_in_radioButton;
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,loc,loc_code;
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
    int m_compcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_transfer_note_other_date_wise_report);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");
        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
       // lin_summary_hide=(LinearLayout)findViewById(R.id.lin_summary_hide);
       // lin_heading_hide=(LinearLayout)findViewById(R.id.lin_heading_hide);
        SharedPreferences s = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = s.getInt("COMP_CODE", 0);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        systemDate = df.format(c);

        Bundle bb = getIntent().getExtras();
        try {
            frm_date=bb.getString("frm_date");
            to_date=bb.getString("to_date");
            Query_frm_date=bb.getString("Query_frm_date");
            Query_to_date=bb.getString("Query_to_date");
            loc=bb.getString("loc");
            loc_code=bb.getString("loc_code");

        } catch (Exception e) { }

        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_location = (TextView) toolbar.findViewById(R.id.toolbar_location);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText(""+str_compdesc+""+"\n"+"Date Wise Transfer Notes Details- Other");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(to_date);
        toolbar_to_date.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(frm_date);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);
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

                                 qry = "select convert(varchar(10),doc_dt,103) as docdt,menuitem_desc,str(sum(bottle_qty),12,0) as bottle_qty,doc_dt,0 as seqno from TRANSFERNOTE,menucarditemmast where TRANSFERNOTE.item_code=menucarditemmast.menuitem_code and doc_dt between '"+Query_frm_date+"' and '"+Query_to_date+"' and comp_code="+m_compcode+" and item_type=2 group by doc_dt,menuitem_desc union select '','','',doc_dt,1 as seqno from TRANSFERNOTE where doc_dt between '"+Query_frm_date+"' and '"+Query_to_date+"' and comp_code="+m_compcode+" and item_type=2 group by doc_dt order by doc_dt,seqno,menuitem_desc";
                                 Log.d("qry", qry);
                                 PreparedStatement ps = con.prepareStatement(qry);
                                 ResultSet rs = ps.executeQuery();

                                 HashMap<String, String> map2 = new HashMap<String, String>();
                                 while (rs.next()) {
                                     HashMap<String, String> map = new HashMap<String, String>();
                                   //  map.put("docdt", rs.getString("docdt"));
                                     map.put("menuitem_desc", rs.getString("menuitem_desc"));
                                     map.put("docdt", rs.getString("docdt"));
                                     map.put("bottle_qty", rs.getString("bottle_qty"));
                                     menu_card_arryList.add(map);
                                 }
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


    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_transfer_note_other_date_wise_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            //holder.list_d1.setText(attendance_list.get(position).get("srno"));
            holder.list_d2.setText(attendance_list.get(position).get("docdt"));
            holder.list_d3.setText(attendance_list.get(position).get("menuitem_desc"));
            holder.list_d4.setText(attendance_list.get(position).get("bottle_qty"));
           // holder.list_d5.setText(attendance_list.get(position).get("bottle_qty"));

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
              //  this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);
            }
        }
    }


}
