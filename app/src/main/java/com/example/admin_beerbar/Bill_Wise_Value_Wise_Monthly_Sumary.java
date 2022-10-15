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
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.ArrayList;
import java.util.HashMap;

public class Bill_Wise_Value_Wise_Monthly_Sumary extends AppCompatActivity {
    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,from_date,to_date;
    TextView txt_total;
    Connection con;
    Toolbar toolbar;
    String IMEINumber,db,Query_frm_date,Query_to_date;
    int m_TAB_CODE;
    NumberFormat nf ;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;

    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> ps_menu_card_arryList;
    private RecyclerView.LayoutManager ps_layoutManager_pe;
    ps_atnds_recyclerAdapter ps_attendance_recyclerAdapter;
    private RecyclerView ps_recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    TextView txt_food_amt,txt_bar_amt,txt_cgst_amt,txt_sgst_amt,txt_amt,txt_dis,txt_srvc_chg,txt_liq_vat_tx,txt_net_total,txt_bill_no;
    Double food_amt=0.00,bar_amt=0.00,cgst_amt=0.00,sgst_amt=0.00,amt=0.00,dis=0.00,srvc_chg=0.00,liq_vat_tx=0.00,net_total=0.00,bill_no=0.00;
    ProgressBar pgb;
    PreparedStatement ps1;
    int m_compcode;
    int m_srno = 0;
    Double m_clbal = 0.00;
    ResultSet rs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_wise_value_wise_daily_monthly_rpt);
        nf = new DecimalFormat(".00");
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
        SharedPreferences sss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = sss.getInt("COMP_CODE", 0);
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
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Month Wise Sale Summary");
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
        //-------------payment summary-------------------------------
        //---------------------Recyclerview 1-----------------------------------------
        ps_menu_card_arryList = new ArrayList<HashMap<String, String>>();
        ps_recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_ps);
        ps_layoutManager_pe = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        ps_recycler_medal_offline_rpt_list.setLayoutManager(ps_layoutManager_pe);
        ps_attendance_recyclerAdapter = new ps_atnds_recyclerAdapter(getApplicationContext(), ps_menu_card_arryList);
        ps_recycler_medal_offline_rpt_list.setAdapter(ps_attendance_recyclerAdapter);
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
               // PreparedStatement ps = con.prepareStatement("select convert(varchar(10),doc_dt,103)as docdt,ltrim(str(amount_5,12,2)) as foodamt,ltrim(str(amount_6,12,2)) as baramt,ltrim(str(amount,12,2)) as tot,case when dis_amount>0 then ltrim(str(dis_amount,12,2)) else '' end as disamt, ltrim(str(tot_amount,12,2)) as nettot,case when amount_1>0 then ltrim(str(amount_1,12,2)) else '' end as cgstamt,case when amount_2>0 then ltrim(str(amount_2,12,2)) else '' end as sgstamt,gl_desc,ltrim(str(amount_3,12,2)) as srvcamt,ltrim(str(amount_4,12,2)) as vatamt from tabreportparameters where tab_code="+m_TAB_CODE+" order by doc_dt");
                PreparedStatement ps = con.prepareStatement("select upper(replace(right(convert(varchar(11),doc_dt,106),8),' ','-'))as docdt,ltrim(str(sum(amount_5),12,2)) as foodamt,ltrim(str(sum(amount_6),12,2)) as baramt,ltrim(str(sum(amount),12,2)) as tot,case when sum(dis_amount)>0 then ltrim(str(sum(dis_amount),12,2)) else '' end as disamt, ltrim(str(sum(tot_amount),12,2)) as nettot,case when sum(amount_1)>0 then ltrim(str(sum(amount_1),12,2)) else '' end as cgstamt,case when sum(amount_2)>0 then ltrim(str(sum(amount_2),12,2)) else '' end as sgstamt,ltrim(str(sum(amount_3),12,2)) as srvcamt,ltrim(str(sum(amount_4),12,2)) as vatamt from tabreportparameters where tab_code="+m_TAB_CODE+" group by upper(replace(right(convert(varchar(11),doc_dt,106),8),' ','-')),month(doc_dt),year(doc_dt) order by year(doc_dt),month(doc_dt)");
                ResultSet rs = ps.executeQuery();
                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    {
                        map.put("docdt", rs.getString("docdt"));
                        map.put("foodamt", rs.getString("foodamt"));
                        map.put("baramt", rs.getString("baramt"));
                        map.put("tot", rs.getString("tot"));
                        map.put("disamt", rs.getString("disamt"));
                        map.put("nettot", rs.getString("nettot"));
                        map.put("cgstamt", rs.getString("cgstamt"));
                        map.put("sgstamt", rs.getString("sgstamt"));
                      //  map.put("gl_desc", rs.getString("gl_desc"));
                        map.put("srvcamt", rs.getString("srvcamt"));
                        map.put("vatamt", rs.getString("vatamt"));
                        menu_card_arryList.add(map);
                    }
                    if(!rs.getString("foodamt").equals(""))
                    {
                        food_amt=food_amt+Double.parseDouble(rs.getString("foodamt"));
                        txt_food_amt.setText(""+nf.format(food_amt));
                    }
                    if(!rs.getString("baramt").equals(""))
                    {
                        bar_amt=bar_amt+Double.parseDouble(rs.getString("baramt"));
                        txt_bar_amt.setText(""+nf.format(bar_amt));
                    }
                    if(!rs.getString("cgstamt").equals(""))
                    {
                        cgst_amt=cgst_amt+Double.parseDouble(rs.getString("cgstamt"));
                        txt_cgst_amt.setText(""+nf.format(cgst_amt));
                    }
                    if(!rs.getString("sgstamt").equals(""))
                    {
                        sgst_amt=sgst_amt+Double.parseDouble(rs.getString("sgstamt"));
                        txt_sgst_amt.setText(""+nf.format(sgst_amt));
                    }
                    if(!rs.getString("tot").equals(""))
                    {
                        amt=amt+Double.parseDouble(rs.getString("tot"));
                        txt_amt.setText(""+nf.format(amt));
                    }
                    if(!rs.getString("disamt").equals(""))
                    {
                        dis=dis+Double.parseDouble(rs.getString("disamt"));
                        txt_dis.setText(""+nf.format(dis));
                    }
                    if(!rs.getString("vatamt").equals(""))
                    {
                        liq_vat_tx=liq_vat_tx+Double.parseDouble(rs.getString("vatamt"));
                        txt_liq_vat_tx.setText(""+nf.format(liq_vat_tx));
                    }
                    if(!rs.getString("nettot").equals(""))
                    {
                        net_total=net_total+Double.parseDouble(rs.getString("nettot"));
                        txt_net_total.setText(""+nf.format(net_total));
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

        insert_summary_payment(Query_frm_date,Query_to_date);
        //---------Payment summary-------------------------------------------
        try {
            pbbar.setVisibility(View.VISIBLE);
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            }
            else
            {
                PreparedStatement ps = con.prepareStatement("SELECT GL_DESC,CASE WHEN AMOUNT <> 0 THEN LTRIM(STR(AMOUNT,12,2)) ELSE '' END AS AMOUNT,CASE WHEN AMOUNT_1 <> 0 THEN LTRIM(STR(AMOUNT_1,12,2)) ELSE '' END AS AMOUNT_1 FROM TABREPORTPARAMETERS where tab_code="+m_TAB_CODE+" order by doc_no");
               // PreparedStatement ps = con.prepareStatement("SELECT GL_DESC, AMOUNT,AMOUNT_1  FROM TABREPORTPARAMETERS order by doc_no");
                ResultSet rs = ps.executeQuery();
                ps_menu_card_arryList.clear();
                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    {
                        map.put("gl_desc", rs.getString("gl_desc"));
                        map.put("AMOUNT", rs.getString("AMOUNT"));
                        map.put("AMOUNT_1", rs.getString("AMOUNT_1"));

                        ps_menu_card_arryList.add(map);
                    }
                }
            }
            pbbar.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + ps_menu_card_arryList.toString());

            if (ps_attendance_recyclerAdapter != null) {
                ps_attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + ps_attendance_recyclerAdapter.toString());
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_bill_wise_value_wise_daily_monthly_sumary_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            //-------------------------
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
                //this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
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
    public class ps_atnds_recyclerAdapter extends RecyclerView.Adapter<ps_atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public ps_atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public ps_atnds_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_list_, parent, false);
            ps_atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new ps_atnds_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ps_atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("gl_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("AMOUNT"));
            holder.list_d3.setText(attendance_list.get(position).get("AMOUNT_1"));
        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2,list_d3;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);


            }
        }
    }
    public void insert_summary_payment(String Temp_frm_date,String Temp_to_date) {
        try {
            pbbar.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = connectionClass.CONN(con_ipaddress, portnumber,db);
            if (con == null) {
                Toast.makeText(getApplicationContext(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            } else {
                m_srno=0;
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                try {
                    ps1 = con.prepareStatement("select isnull(sum(paid_amount-by_cash_pmt),0)as amount_1 from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + " and pmt_mode = 2");
                    ResultSet  rs = ps1.executeQuery();
                    while (rs.next()) {
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('ONLINE PAYMENT SALES BREAKUP'," + rs.getString("amount_1") + "," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();
                    }
                    m_srno = m_srno + 1;
                    // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                    // ps1.executeUpdate();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error..407" + e, Toast.LENGTH_SHORT).show();
                    Log.d("eeee",""+e);
                }


                try {
                    ps1 = con.prepareStatement("select (select crdt_desc from crdtmast where crdt_code=sales.crdt_code) as crdt_desc,isnull(sum(paid_amount-by_cash_pmt),0)as amount from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between   '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + "  and pmt_mode = 2 group by crdt_code order by crdt_desc");
                    ResultSet   rs = ps1.executeQuery();
                    while (rs.next()) {
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('" + rs.getString("crdt_desc") + "'," + rs.getString("amount") + "," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();
                    }
                    m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                    ps1.executeUpdate();

                    //   ''''''''''New Addition On 25/07/2021 ''''''''''''''
                    ps1 = con.prepareStatement("select isnull(sum(online_payment-paid_amount),0)as amt from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+"  and pmt_mode = 2 and online_payment > paid_amount");
                    ResultSet   rs1 = ps1.executeQuery();
                    int i=0;
                    double amount = 0.00;
                    while (rs1.next()) {
                        i++;
                        amount = Double.parseDouble(rs1.getString("amt"));
                        // if (amount > 0) {
                    }
                    if(i>0&&amount>0){
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('ONLINE PAYMENT SALE-EXTRA'," + rs1.getString("amt") + "," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();

                        m_srno = m_srno + 1;
                        //  ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        // ps1.executeUpdate();
                        ///-----------
                        ps1 = con.prepareStatement("select (select crdt_desc from crdtmast where crdt_code=sales.crdt_code) as crdt_desc,isnull(sum(online_payment-paid_amount),0)as amount from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode = 2  and online_payment > paid_amount group by crdt_code order by crdt_desc");
                        ResultSet  rs2 = ps1.executeQuery();
                        while (rs2.next())
                        {   m_srno = m_srno + 1;
                            ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('" +rs2.getString("crdt_desc")+"'," +rs2.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                            ps1.executeUpdate();
                        }
                        m_srno = m_srno + 1;
                        // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        //  ps1.executeUpdate();
                    }


                }catch (Exception e){
                    Log.d("eeee","451"+e);
                    Toast.makeText(getApplicationContext(), "Error..451" + e, Toast.LENGTH_SHORT).show();
                }

                //----''''''''''New Addition On 25/07/2021 ''''''''''''''
                m_clbal=0.00;
                try {
                    ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyrcp.ac_head_id) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where crdt_code <> 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id order by gl_desc");
                    ResultSet   rs = ps1.executeQuery();
                    int i=0;
                    while (rs.next()) {
                        i++;
                    }

                    if (i > 0) {
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('RECEIPTS (REGULAR CUSTOMERS-ONLINE PAYMENT)',0," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        //ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        // ps1.executeUpdate();
                        ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyrcp.ac_head_id) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where crdt_code <> 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id order by gl_desc");
                        ResultSet   rs1 = ps1.executeQuery();
                        while (rs1.next())
                        { m_srno = m_srno + 1;
                            ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('"+rs1.getString("gl_desc")+"',"+rs1.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                            ps1.executeUpdate();
                            m_clbal = m_clbal +Double.parseDouble(rs1.getString("amount"));
                        }
                        m_srno = m_srno + 1;
                        // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        //  ps1.executeUpdate();
                        //  m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('TOTAL =>'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+")");
                        ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        ps1.executeUpdate();
                        // }
                    }


                }catch (Exception e){
                    Log.d("eeee","488"+e);
                    Toast.makeText(getApplicationContext(), "Error..488" + e, Toast.LENGTH_SHORT).show();
                }
                //----------------------------------------------------
                m_clbal=0.00;
                try {
                    ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select PENDING_BILL_AC_HEAD_ID from profile) group by narr order by gl_desc ");
                    ResultSet   rs = ps1.executeQuery();
                    int i=0;
                    while (rs.next()) {
                        i++;
                    }
                    if(i>0)
                    {
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,tab_code,doc_no) values('RECEIPTS (OTHER CUSTOMERS)'," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();

                        m_srno = m_srno + 1;
                        // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        // ps1.executeUpdate();
                        ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select PENDING_BILL_AC_HEAD_ID from profile) group by narr order by gl_desc ");
                        ResultSet   rs1 = ps1.executeQuery();
                        while (rs1.next())
                        { m_srno = m_srno + 1;
                            ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('"+rs1.getString("gl_desc")+"',"+rs1.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                            ps1.executeUpdate();
                            m_clbal = m_clbal +Double.parseDouble(rs1.getString("amount"));
                        }
                        m_srno = m_srno + 1;
                        //  ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        //  ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('TOTAL =>'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+")");
                        ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        ps1.executeUpdate();
                    }



                }catch (Exception e){
                    Log.d("eeee","527"+e);
                    Toast.makeText(getApplicationContext(), "Error..526" + e, Toast.LENGTH_SHORT).show();
                }
                //==================================================

                m_clbal=0.00;
                try {
                    ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id not in(select PENDING_BILL_AC_HEAD_ID from profile) and ac_head_id not in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by narr order by narr");
                    ResultSet   rs = ps1.executeQuery();
                    int i=0;
                    while (rs.next()) {
                        i++;
                    }
                    if (i > 0) {
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('OTHER RECEIPTS',0," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        //  ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        //  ps1.executeUpdate();
                        ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select PENDING_BILL_AC_HEAD_ID from profile) group by narr order by gl_desc ");
                        ResultSet   rs1 = ps1.executeQuery();
                        while (rs1.next())
                        { m_srno = m_srno + 1;
                            ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('"+rs1.getString("gl_desc")+"',"+rs1.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                            ps1.executeUpdate();
                            m_clbal = m_clbal +Double.parseDouble(rs1.getString("amount"));
                        }
                        m_srno = m_srno + 1;
                        //  ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        //  ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('TOTAL =>'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+")");
                        ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        ps1.executeUpdate();
                    }


                }catch (Exception e){
                    Log.d("eeee","562"+e);
                    Toast.makeText(getApplicationContext(), "Error..562" + e, Toast.LENGTH_SHORT).show();
                }

                //================================================================================================
                m_clbal=0.00;
                try {
                    ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and ac_head_id = 0 group by narr order by narr");
                    ResultSet   rs = ps1.executeQuery();
                    // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                    // ps1.executeUpdate();
                    int i=0;
                    while (rs.next()) {
                        i++;
                    }
                    if(i>0) {
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('OTHER RECEIPTS',0," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();
                        ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and ac_head_id = 0 group by narr order by narr");
                        ResultSet   rs1 = ps1.executeQuery();
                        while (rs1.next()) {
                            m_srno = m_srno + 1;
                            ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('" + rs1.getString("gl_desc") + "'," + rs1.getString("amount") + "," + m_TAB_CODE + "," + m_srno + ")");
                            ps1.executeUpdate();
                            m_clbal = m_clbal + Double.parseDouble(rs1.getString("amount"));
                        }
                        m_srno = m_srno + 1;
                        //  ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        //  ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('TOTAL =>'," + m_clbal + "," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        // ps1.executeUpdate();
                        //}
                    }

                }catch (Exception e)  {
                    Log.d("eeee","599"+e);
                    Toast.makeText(getApplicationContext(), "Error..599" + e, Toast.LENGTH_SHORT).show();
                }

                //___________________________________________________________________________
                //''''''''Addition On 18/4/2019=============
                try {
                    ps1 = con.prepareStatement("select isnull(sum(amount),0)as amt from dailyrcp where crdt_code <> 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + "");
                    ResultSet  rs = ps1.executeQuery();
                    int i=0;
                    double amount = 0.00;
                    while (rs.next()) {
                        i++;
                        amount = Double.parseDouble(rs.getString("amt"));
                        // if (amount > 0) {
                    }
                    if(i>0&&amount>0)
                    {
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('ONLINE PAYMENT RECEIPTS BREAKUP'," + amount + "," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        //  ps1.executeUpdate();

                        ps1 = con.prepareStatement("select (select crdt_desc from crdtmast where crdt_code=dailyrcp.crdt_code) as crdt_desc,isnull(sum(amount),0)as amount from dailyrcp where crdt_code <> 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" group by crdt_code order by crdt_desc");
                        ResultSet  rs1 = ps1.executeQuery();
                        while (rs1.next())
                        { m_srno = m_srno + 1;
                            ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('"+rs1.getString("crdt_desc")+"',"+rs1.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                            ps1.executeUpdate();
                        }
                        m_srno = m_srno + 1;
                        // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        // ps1.executeUpdate();
                    }

                }catch (Exception e){
                    Log.d("eeee","634"+e);
                    Toast.makeText(getApplicationContext(), "Error..634" + e, Toast.LENGTH_SHORT).show();
                }
                //********************************************************

                //''''''''Addition On 18/4/2019
                m_clbal=0.00;
                try {
                    ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyrcp.ac_head_id) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where crdt_code = 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id order by gl_desc");
                    ResultSet  rs = ps1.executeQuery();
                    int i=0;
                    double amount = 0.00;
                    while (rs.next()) {
                        i++;
                        amount = Double.parseDouble(rs.getString("amount"));
                    }
                    if(i>0&&amount>0)
                    {
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('RECEIPTS (REGULAR CUSTOMERS-CASH)',0," + m_TAB_CODE + "," + m_srno + ")");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyrcp.ac_head_id) as gl_desc,isnull(sum(amount),0) as amount from dailyrcp where crdt_code = 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '" + Temp_frm_date + "' and '" + Temp_to_date + "' and comp_code = " + m_compcode + " and ac_head_id <> 0 and ac_head_id in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id order by gl_desc");
                        ResultSet  rs1 = ps1.executeQuery();
                        while (rs1.next()) {
                            m_srno = m_srno + 1;
                            //  ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                            // ps1.executeUpdate();
                            ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('" + rs1.getString("gl_desc") + "'," + rs1.getString("amount") + "," + m_TAB_CODE + "," + m_srno + ")");
                            ps1.executeUpdate();
                            m_clbal = m_clbal + Double.parseDouble(rs1.getString("amount"));
                        }
                        m_srno = m_srno + 1;
                        // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        //  ps1.executeUpdate();
                        //   m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('TOTAL =>'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+")");
                        ps1.executeUpdate();
                        m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                        ps1.executeUpdate();
                    }

                    //********************************************************
                }catch (Exception e){
                    Log.d("eeee","675"+e);
                    Toast.makeText(getApplicationContext(), "Error..675" + e, Toast.LENGTH_SHORT).show();
                }
                //############################################
                try{
                    // ps1 = con.prepareStatement("select 'CASH PAYMENT FROM SALE' as gl_desc,(select ISNULL(sum(paid_amount),0)as amount from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode=1),0)+ISNULL((select sum(by_cash_pmt) from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode=2),0),12,2)) as amount");
                    //  ps1 = con.prepareStatement("select 'CASH PAYMENT FROM SALE' as gl_desc,LTRIM(STR(ISNULL((select sum(paid_amount) from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode=1),0)+ISNULL((select sum(by_cash_pmt) from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode=2),0),12,2)) as amount ");
                    ps1 = con.prepareStatement("select 'CASH PAYMENT FROM SALE' as gl_desc,ISNULL((select sum(paid_amount) from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode=1),0)+ISNULL((select sum(by_cash_pmt) from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode=2),0) as amount");
                    ResultSet rs = ps1.executeQuery();
                    while (rs.next())
                    { m_srno = m_srno + 1;
                        ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('"+rs.getString("gl_desc")+"',"+rs.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                        ps1.executeUpdate();
                        m_clbal = m_clbal +Double.parseDouble(rs.getString("amount"));
                    }
                    m_srno = m_srno + 1;
                    // ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                    // ps1.executeUpdate();
                    m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('TOTAL CASH COLLECTION'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                    m_srno = m_srno + 1;
                    //  ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                    // ps1.executeUpdate();

                }catch (Exception e){
                    Log.d("eeee","697"+e);
                    Toast.makeText(getApplicationContext(), "Error..697" + e, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error..746" + e, Toast.LENGTH_SHORT).show();
        }
    }
}