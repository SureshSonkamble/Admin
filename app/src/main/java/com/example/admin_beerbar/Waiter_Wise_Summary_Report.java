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
import java.util.ArrayList;
import java.util.HashMap;

public class Waiter_Wise_Summary_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,from_date,to_date;
    TextView txt_total;
    Double total=0.0;
    Double ttl=0.0;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String IMEINumber,db,invoice_no,supplier_name,amount,querydt,doc_no,Query_frm_date,Query_to_date;
    int m_TAB_CODE;
    String mclbal,forname,Query_date;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    TextView txt_amt_1,txt_amt_2,txt_amt_3,txt_amt_4,txt_amt_5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiter_wise_summary_report);
        txt_amt_1=(TextView)findViewById(R.id.txt_amt_1);
        txt_amt_2=(TextView)findViewById(R.id.txt_amt_2);
        txt_amt_3=(TextView)findViewById(R.id.txt_amt_3);
        txt_amt_4=(TextView)findViewById(R.id.txt_amt_4);
        txt_amt_5=(TextView)findViewById(R.id.txt_amt_5);
        Bundle bd = getIntent().getExtras();
        try {
            from_date = bd.getString("frm_date");
            to_date = bd.getString("to_date");
            Query_frm_date = bd.getString("Query_frm_date");
            Query_to_date = bd.getString("Query_to_date");



        } catch (Exception e) {
        }

        pbbar = (ProgressBar) findViewById(R.id.pgb);
        SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        str_compdesc = ss.getString("COMP_DESC", "");

        SharedPreferences sp =getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Waiter Wise Summary Report");
        // toolbar_title.setText("Purchase Register Value Wise");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(from_date);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(to_date);
        toolbar_to_date.setTextColor(0xFFFFFFFF);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        SharedPreferences sp1 = getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

       /* Date d = Calendar.getInstance().getTime();
        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Query_date=out.format(docdt);*/
        // Query_date=  new SimpleDateFormat("MM/dd/yyyy").format(docdt);

        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_bill_list);
        layoutManager_pe = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getApplicationContext(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        txt_total = (TextView) findViewById(R.id.txt_total);

        connectionClass = new Config();
        try {
            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else
            {
                PreparedStatement ps = con.prepareStatement("select watr_desc,str(sum(amount_1),12,2) as food_amt,str(sum(amount_2),12,2) as bar_amt, str(sum(amount),12,2) as amount,str(sum(amount_3),12,2) as discount,str(sum(amount_4),12,2) as net_amount,str(sum(amount_5),12,2) as stax_amt,str(sum(amount_6),12,2) as cgst_amt,str(sum(tot_sale),12,2) as sgst_amt,0 as seqno from tabreportparameters,watrmast where  watrmast.watr_code = tabreportparameters.brnd_code and tab_code="+m_TAB_CODE+" group by brnd_code,watr_desc union select 'Grand Total',str(sum(amount_1),12,2) as food_amt,str(sum(amount_2),12,2) as bar_amt,str(sum(amount),12,2) as amount,str(sum(amount_3),12,2) as discount,str(sum(amount_4),12,2) as net_amount,str(sum(amount_5),12,2) as stax_amt,str(sum(amount_6),12,2) as cgst_amt,str(sum(tot_sale),12,2) as sgst_amt,1 as seqno from tabreportparameters where tab_code="+m_TAB_CODE+" order by watr_desc,seqno ");
                ResultSet rs = ps.executeQuery();

                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    if(!rs.getString("watr_desc").equals("Grand Total")) {
                        map.put("watr_desc", rs.getString("watr_desc"));
                        map.put("food_amt", rs.getString("food_amt"));
                        map.put("bar_amt", rs.getString("bar_amt"));
                        map.put("amount", rs.getString("amount"));
                        map.put("discount", rs.getString("discount"));
                        map.put("stax_amt", rs.getString("stax_amt"));
                        map.put("net_amount", rs.getString("net_amount"));
                        map.put("cgst_amt", rs.getString("cgst_amt"));
                        map.put("sgst_amt", rs.getString("sgst_amt"));
                        map.put("seqno", rs.getString("seqno"));
                    }
                    else
                    {
                        txt_amt_1.setText(rs.getString("food_amt"));
                        txt_amt_2.setText(rs.getString("bar_amt"));
                        txt_amt_3.setText(rs.getString("amount"));
                        txt_amt_4.setText(rs.getString("discount"));
                        txt_amt_5.setText(rs.getString("net_amount"));
                    }

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
             Toast.makeText(getApplicationContext(), "Error..168" + e, Toast.LENGTH_SHORT).show();
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
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_wise_summary_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            //-------------------------
            holder.list_d1.setText(attendance_list.get(position).get("watr_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("food_amt"));
            holder.list_d3.setText(attendance_list.get(position).get("bar_amt"));
            holder.list_d4.setText(attendance_list.get(position).get("cgst_amt"));
            holder.list_d5.setText(attendance_list.get(position).get("sgst_amt"));
            holder.list_d6.setText(attendance_list.get(position).get("amount"));
            holder.list_d7.setText(attendance_list.get(position).get("discount"));
            holder.list_d8.setText(attendance_list.get(position).get("stax_amt"));
            holder.list_d9.setText(attendance_list.get(position).get("net_amount"));
           // holder.list_d10.setText(attendance_list.get(position).get("stax_amt"));
           /* holder.list_d5.setTextColor(Color.BLACK);
            holder.list_d6.setTextColor(Color.BLACK);
            if(!attendance_list.get(position).get("seqno").equals("0"))
            {
                holder.list_d5.setTextColor(Color.BLUE);
                holder.list_d6.setTextColor(Color.RED);
            }*/


        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3,list_d4,list_d5,list_d6,list_d7,list_d8,list_d9,list_d10;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);
                this.list_d6 = (TextView) itemView.findViewById(R.id.list_d6);
                this.list_d7 = (TextView) itemView.findViewById(R.id.list_d7);
                this.list_d8 = (TextView) itemView.findViewById(R.id.list_d8);
                this.list_d9 = (TextView) itemView.findViewById(R.id.list_d9);
               // this.list_d10 = (TextView) itemView.findViewById(R.id.list_d10);

            }
        }
    }

}
