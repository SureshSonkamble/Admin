package com.example.admin_beerbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Item_Wise_Ledger_Liquor_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,from_date,to_date;
    TextView txt_total;
    Double total=0.0;
    Double ttl=0.0;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String IMEINumber,db;
    int m_TAB_CODE;

    BigDecimal b_sealed,b_opbl,b_clbalance,b_clbal,b_purtot,b_saletot=BigDecimal.ZERO,b_size,m_clbalance_ml,b_loosed_ml;
    Double m_clbal =0.00;
    Double m_clbalance =0.00;
    Double m_opbl=0.00;
    Double opbl=0.0000;
    Double m_size;
    Double m_purtot=0.00;
    Double m_saletot =0.00;
    TextView txt_sale_qty_total,txt_pur_qty_total;
    String forname,opbal,size;
    int m_sealed=0;
    NumberFormat nf1,nf2;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_wise_ledger_liquor_report);
        nf1 =new DecimalFormat(".0000");

        txt_pur_qty_total=(TextView)findViewById(R.id.txt_pur_qty_total);
        txt_sale_qty_total=(TextView)findViewById(R.id.txt_sale_qty_total);
        Bundle bd = getIntent().getExtras();
        try {
            from_date = bd.getString("from_date");
            to_date = bd.getString("to_date");
            forname = bd.getString("forname");
            opbal =  bd.getString("m_opbl");
            size =  bd.getString("m_size");
            size=size.replace("ML", " ");
            m_opbl=Double.parseDouble(opbal);
            m_size=Double.parseDouble(size);
            m_clbalance_ml=new BigDecimal(opbal, MathContext.DECIMAL64);
            m_clbalance=m_opbl;
            //  m_sealed=Integer.parseInt(String.valueOf(m_clbalance/m_size));
            m_sealed= (int) (m_clbalance/m_size);
            //m_clbalance = Math.round((m_clbalance) -(m_sealed*m_size))/10000.0;
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
            BigDecimal res;
            int newScale = 4;

           // m_clbalance = ((m_clbalance) -(m_sealed*m_size))/10000.0000;

            BigDecimal a = new BigDecimal(((m_clbalance -(m_sealed*m_size))/10000.0000));
            res=a.setScale(4,RoundingMode.UP);
            b_sealed =new BigDecimal(m_sealed, MathContext.DECIMAL64);
            b_size =new BigDecimal(m_size, MathContext.DECIMAL64);
            //opbl=res;
           // opbl=new BigDecimal(res.toString()).movePointLeft(0).doubleValue();
            b_opbl = b_sealed.add(res);
            //m_opbl=b_opbl.doubleValue();
            Log.d("abc",""+b_opbl);


        } catch (Exception e) {
            Toast.makeText(connectionClass, ""+e, Toast.LENGTH_SHORT).show();
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
        TextView toolbar_to_date = (TextView) toolbar.findViewById(R.id.toolbar_to_date);//title
        TextView toolbar_frm_date = (TextView) toolbar.findViewById(R.id.toolbar_frm_date);//title
        TextView toolbar_for_name = (TextView) toolbar.findViewById(R.id.toolbar_for_name);//title
        TextView toolbar_op_bal = (TextView) toolbar.findViewById(R.id.toolbar_op_bal);//title

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Item Wise Ledger-Liquor");
       // toolbar_title.setText("Item Wise Ledger");
        toolbar_op_bal.setTextColor(0xFFFFFFFF);
        toolbar_op_bal.setText(" "+  b_opbl);
        toolbar_for_name.setTextColor(0xFFFFFFFF);
        toolbar_for_name.setText(forname+ " "+"Size "+m_size);
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_to_date.setText(to_date);
        toolbar_to_date.setTextColor(0xFFFFFFFF);
        toolbar_frm_date.setText(from_date);
        toolbar_frm_date.setTextColor(0xFFFFFFFF);

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
                //PreparedStatement ps = con.prepareStatement("select (convert(varchar(10),doc_dt,103))docdt,amount,crdr_cd,doc_type from tabreportparameters where TAB_CODE = "+m_TAB_CODE+" order by doc_dt,doc_no,crdr_cd");
                PreparedStatement ps = con.prepareStatement("select convert(varchar(10),doc_dt,103)as docdt,gl_desc,ltrim(str(gl_opbal,12,4)) as gl_opbal,ac_head_id from tabreportparameters where tab_code ="+ m_TAB_CODE + " order by doc_dt,crdr_cd,ac_head_id ");
                ResultSet rs = ps.executeQuery();
                b_clbalance=b_opbl;
               // m_clbalance_ml=b_opbl;
                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("docdt", rs.getString("docdt"));
                    map.put("gl_desc", rs.getString("gl_desc"));
                    if(!rs.getString("gl_desc").equals("COUNTER SALE")&& !rs.getString("gl_desc").equals("TRANSFER ISS")&& !rs.getString("gl_desc").equals("LIQ.RECIPE"))
                    {

                        m_clbalance_ml=m_clbalance_ml.add(b_size.multiply(new BigDecimal(rs.getString("gl_opbal"))));
                        // b_clbalance=b_clbalance+(m_size*Double.parseDouble(rs.getString("gl_opbal")));
                        b_clbal=m_clbalance_ml.divide(b_size, MathContext.DECIMAL128);
                        b_clbal=b_clbal.setScale(4,RoundingMode.FLOOR);
                        b_clbal=new BigDecimal(b_clbal.intValue(), MathContext.DECIMAL64);
                        b_loosed_ml=m_clbalance_ml.subtract(b_clbal.multiply(b_size));
                        b_clbal=b_clbal.add(b_loosed_ml.divide(new BigDecimal(10000)));
                        m_purtot=m_purtot+Double.parseDouble(rs.getString("gl_opbal"));
                        int IntValue = (int) Double.parseDouble(rs.getString("gl_opbal"));
                        map.put("m_purqty",""+IntValue);

                    }
                    else
                    {    if(rs.getString("ac_head_id").equals("1.0")) {
                        m_clbalance_ml=m_clbalance_ml.subtract(b_size.multiply(new BigDecimal(rs.getString("gl_opbal"))));
                        b_clbal=m_clbalance_ml.divide(b_size, MathContext.DECIMAL128);
                        b_clbal=b_clbal.setScale(4,RoundingMode.FLOOR);
                        b_clbal=new BigDecimal(b_clbal.intValue(), MathContext.DECIMAL64);
                        b_loosed_ml=m_clbalance_ml.subtract(b_clbal.multiply(b_size));
                        b_clbal=b_clbal.add(b_loosed_ml.divide(new BigDecimal(10000)));
                        b_saletot = b_saletot.add(new BigDecimal(rs.getString("gl_opbal")).multiply(b_size));
                        //b_saletot = b_saletot.add(new BigDecimal(rs.getString("gl_opbal")));
                        map.put("m_saleqty", rs.getString("gl_opbal"));
                    }
                    else {
                        m_clbalance_ml=m_clbalance_ml.subtract(new BigDecimal(rs.getString("gl_opbal")));
                       // m_clbalance = m_clbalance -  Double.parseDouble(rs.getString("gl_opbal"));
                       // m_clbal = m_clbalance / m_size;
                        b_clbal=m_clbalance_ml.divide(b_size, MathContext.DECIMAL128);
                        b_clbal=b_clbal.setScale(4,RoundingMode.FLOOR);
                        b_clbal=new BigDecimal(b_clbal.intValue(), MathContext.DECIMAL64);
                        b_loosed_ml=m_clbalance_ml.subtract(b_clbal.multiply(b_size));
                        b_clbal=b_clbal.add(b_loosed_ml.divide(new BigDecimal(10000)));
                        //b_saletot = b_saletot.add(b_loosed_ml);
                        b_saletot = b_saletot.add(new BigDecimal(rs.getString("gl_opbal")) );
                        map.put("m_saleqty", ""+new BigDecimal(rs.getString("gl_opbal")).divide(new BigDecimal(10000)) );
                       // map.put("m_saleqty",""+b_saletot);
                    }
                    }
                        map.put("m_clbalance",""+nf1.format(b_clbal));

                    menu_card_arryList.add(map);
                }
                txt_pur_qty_total.setText(""+m_purtot);
                b_clbal=b_saletot.divide(b_size, MathContext.DECIMAL128);
                b_clbal=new BigDecimal(b_clbal.intValue(), MathContext.DECIMAL64);
                b_loosed_ml=b_saletot.subtract(b_clbal.multiply(b_size));
                b_clbal=b_clbal.add(b_loosed_ml.divide(new BigDecimal(10000)));
                txt_sale_qty_total.setText(""+b_clbal);

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ledger_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("docdt"));
            holder.list_d2.setText(attendance_list.get(position).get("gl_desc"));

            holder.list_d3.setText(attendance_list.get(position).get("m_purqty"));
            holder.list_d4.setText(attendance_list.get(position).get("m_saleqty"));
            holder.list_d5.setText(attendance_list.get(position).get("m_clbalance"));

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3,list_d4,list_d5;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);

            }
        }
    }

}
