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

public class Bill_Wise_Value_Wise_Date_Sumary extends AppCompatActivity {
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,from_date,to_date;
    TextView txt_total;
    Connection con;
    Toolbar toolbar;
    String IMEINumber,db,Query_frm_date,Query_to_date;
    int m_TAB_CODE;

    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    TextView txt_food_amt,txt_bar_amt,txt_cgst_amt,txt_sgst_amt,txt_amt,txt_dis,txt_srvc_chg,txt_liq_vat_tx,txt_net_total,txt_bill_no;
    Double food_amt=0.00,bar_amt=0.00,cgst_amt=0.00,sgst_amt=0.00,amt=0.00,dis=0.00,srvc_chg=0.00,liq_vat_tx=0.00,net_total=0.00,bill_no=0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_wise_value_wise_date_rpt);
        txt_food_amt=(TextView)findViewById(R.id.txt_food_amt);
        txt_bar_amt=(TextView)findViewById(R.id.txt_bar_amt);
        txt_cgst_amt=(TextView)findViewById(R.id.txt_cgst_amt);
        txt_sgst_amt=(TextView)findViewById(R.id.txt_sgst_amt);
        txt_amt=(TextView)findViewById(R.id.txt_amt);
        txt_dis=(TextView)findViewById(R.id.txt_dis);
        txt_srvc_chg=(TextView)findViewById(R.id.txt_srvc_chg);
        txt_liq_vat_tx=(TextView)findViewById(R.id.txt_liq_vat_tx);
        txt_net_total=(TextView)findViewById(R.id.txt_net_total);
       // txt_bill_no=(TextView)findViewById(R.id.txt_bill_no);
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
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Date Wise Bill Wise Details");
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
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getApplicationContext(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        connectionClass = new Config();

        try {
            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else
            {      food_amt=0.00;
                   bar_amt=0.00;
                   cgst_amt=0.00;
                   sgst_amt=0.00;
                   amt=0.00;
                   dis=0.00;
                   liq_vat_tx=0.00;
                   net_total=0.00;
                   bill_no=0.00;
              //  PreparedStatement ps = con.prepareStatement("select convert(varchar(10),doc_dt,103)as docdt,ltrim(str(amount_5,12,2)) as foodamt,ltrim(str(amount_6,12,2)) as baramt,ltrim(str(amount,12,2)) as tot,case when dis_amount>0 then ltrim(str(dis_amount,12,2)) else '' end as disamt, ltrim(str(tot_amount,12,2)) as nettot,case when amount_1>0 then ltrim(str(amount_1,12,2)) else '' end as cgstamt,case when amount_2>0 then ltrim(str(amount_2,12,2)) else '' end as sgstamt,gl_desc,ltrim(str(amount_3,12,2)) as srvcamt,ltrim(str(amount_4,12,2)) as vatamt from tabreportparameters where tab_code="+m_TAB_CODE+" order by doc_dt");
                PreparedStatement ps = con.prepareStatement(" select doc_no,convert(varchar(10),doc_dt,103)as docdt,ltrim(str(amount_5,12,2)) as foodamt,ltrim(str(amount_6,12,2)) as baramt,ltrim(str(amount,12,2)) as tot,case when dis_amount>0 then ltrim(str(dis_amount,12,2)) else '' end as disamt, ltrim(str(tot_amount,12,2)) as nettot,case when amount_1>0 then ltrim(str(amount_1,12,2)) else '' end as cgstamt,case when amount_2>0 then ltrim(str(amount_2,12,2)) else '' end as sgstamt,ac_head_id as pmt_mode,(select tbno_desc from tbnomast where tbno_code=tabreportparameters.item_code) as tbno_desc,ltrim(str(tot_sale,12,2)) as srvcamt,ltrim(str(amount_4,12,2)) as vatamt from tabreportparameters where tab_code="+m_TAB_CODE+" order by doc_dt,doc_no");
                ResultSet rs = ps.executeQuery();
                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    {
                        //map.put("docdt", rs.getString("docdt"));
                        map.put("docdt", rs.getString("docdt"));
                        map.put("foodamt", rs.getString("foodamt"));
                        map.put("baramt", rs.getString("baramt"));
                        map.put("tot", rs.getString("tot"));
                        map.put("disamt", rs.getString("disamt"));
                        map.put("nettot", rs.getString("nettot"));
                        map.put("cgstamt", rs.getString("cgstamt"));
                        map.put("sgstamt", rs.getString("sgstamt"));
                        map.put("tbno_desc", rs.getString("tbno_desc"));
                        map.put("srvcamt", rs.getString("srvcamt"));
                        map.put("vatamt", rs.getString("vatamt"));
                        menu_card_arryList.add(map);
                    }
                    if(!rs.getString("foodamt").equals(""))
                    {
                        food_amt=food_amt+Double.parseDouble(rs.getString("foodamt"));
                        txt_food_amt.setText(""+food_amt);
                    }
                    if(!rs.getString("baramt").equals(""))
                    {
                        bar_amt=bar_amt+Double.parseDouble(rs.getString("baramt"));
                        txt_bar_amt.setText(""+bar_amt);
                    }
                    if(!rs.getString("cgstamt").equals(""))
                    {
                        cgst_amt=cgst_amt+Double.parseDouble(rs.getString("cgstamt"));
                        txt_cgst_amt.setText(""+cgst_amt);
                    }
                    if(!rs.getString("sgstamt").equals(""))
                    {
                        sgst_amt=sgst_amt+Double.parseDouble(rs.getString("sgstamt"));
                        txt_sgst_amt.setText(""+sgst_amt);
                    }
                    if(!rs.getString("tot").equals(""))
                    {
                        amt=amt+Double.parseDouble(rs.getString("tot"));
                        txt_amt.setText(""+amt);
                    }
                    if(!rs.getString("disamt").equals(""))
                    {
                        dis=dis+Double.parseDouble(rs.getString("disamt"));
                        txt_dis.setText(""+dis);
                    }
                    if(!rs.getString("vatamt").equals(""))
                    {
                        liq_vat_tx=liq_vat_tx+Double.parseDouble(rs.getString("vatamt"));
                        txt_liq_vat_tx.setText(""+liq_vat_tx);
                    }
                    if(!rs.getString("nettot").equals(""))
                    {
                        net_total=net_total+Double.parseDouble(rs.getString("nettot"));
                        txt_net_total.setText(""+net_total);
                    }
                   /* if(!rs.getString("gl_desc").equals(""))
                    {
                        bill_no=bill_no+Double.parseDouble(rs.getString("gl_desc"));
                        txt_bill_no.setText(""+bill_no);
                    }*/


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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_bill_wise_value_wise_monthly_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            //-------------------------
            holder.list_d1.setText(attendance_list.get(position).get("tbno_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("docdt"));
            holder.list_d3.setText(attendance_list.get(position).get("foodamt"));
            holder.list_d4.setText(attendance_list.get(position).get("baramt"));
            holder.list_d5.setText(attendance_list.get(position).get("cgstamt"));
            holder.list_d6.setText(attendance_list.get(position).get("sgstamt"));
            holder.list_d7.setText(attendance_list.get(position).get("tot"));
            holder.list_d8.setText(attendance_list.get(position).get("disamt"));
            holder.list_d9.setText(attendance_list.get(position).get("srvcamt"));
            holder.list_d10.setText(attendance_list.get(position).get("vatamt"));
            holder.list_d11.setText(attendance_list.get(position).get("nettot"));
           // holder.list_d12.setText(attendance_list.get(position).get("gl_desc"));
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
            TextView list_d1, list_d2, list_d3,list_d4,list_d5,list_d6,list_d7,list_d8,list_d9,list_d10,list_d11,list_d12;
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
                this.list_d10 = (TextView) itemView.findViewById(R.id.list_d10);
                this.list_d11 = (TextView) itemView.findViewById(R.id.list_d11);
               // this.list_d12 = (TextView) itemView.findViewById(R.id.list_d12);

            }
        }
    }
}