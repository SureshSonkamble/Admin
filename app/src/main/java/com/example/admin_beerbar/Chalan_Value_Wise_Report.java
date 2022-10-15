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
import java.util.ArrayList;
import java.util.HashMap;

public class Chalan_Value_Wise_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,from_date,to_date;
    TextView txt_total;
    Double total=0.0;
    Double ttl=0.0;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String IMEINumber,docdt,db,supplier_name,amount,querydt,doc_no;
    int m_TAB_CODE;
    String mclbal,forname,Query_date;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chalan_value_wise_report);
       // txt_total=(TextView)findViewById(R.id.txt_total);
        Bundle bd = getIntent().getExtras();
        try {
            from_date = bd.getString("frm_date");
            to_date = bd.getString("to_date");
            docdt = bd.getString("docdt");
            supplier_name = bd.getString("supplier_name");
            amount = bd.getString("amount");
            doc_no = bd.getString("doc_no");
            querydt = bd.getString("querydt");
            Log.d("ssssdocdt",docdt);
            Log.d("ssssquerydt",querydt);
            Log.d("doc_no",doc_no);

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
        TextView toolbar_docdt = (TextView) toolbar.findViewById(R.id.toolbar_docdt);//title
        TextView toolbar_ino = (TextView) toolbar.findViewById(R.id.toolbar_ino);//title
        TextView toolbar_sname = (TextView) toolbar.findViewById(R.id.toolbar_sname);//title
        TextView toolbar_amt = (TextView) toolbar.findViewById(R.id.toolbar_amt);//title

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Challan Register Value Wise");
        // toolbar_title.setText("Purchase Register Value Wise");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_docdt.setText(docdt);
        toolbar_docdt.setTextColor(0xFFFFFFFF);
        toolbar_ino.setText(doc_no);
        toolbar_ino.setTextColor(0xFFFFFFFF);
        toolbar_sname.setText(supplier_name);
        toolbar_sname.setTextColor(0xFFFFFFFF);
        toolbar_amt.setText(amount);
        toolbar_amt.setTextColor(0xFFFFFFFF);

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

                PreparedStatement ps = con.prepareStatement("SELECT BRND_DESC,SIZE_DESC,LTRIM(RTRIM(STR(QUANTITY,12,2))) AS CASE_QTY,LTRIM(RTRIM(STR(BOTTLE_QTY,12,0))) AS BTL_QTY,ltrim(str(basic_rate,12,2)) as basic_rate,ltrim(str(basic_amt,12,2)) as basic_amt,doc_srno,0 as seqno FROM CHALANITEM,BRNDMAST,SIZEMAST,ITEMMAST WHERE ITEMMAST.ITEM_CODE=CHALANITEM.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND ITEMMAST.SIZE_CODE=SIZEMAST.SIZE_CODE AND DOC_NO="+doc_no+" AND DOC_DT='"+querydt+"' union SELECT '','','','','ITEM TOTAL=>',ltrim(str(sum(basic_amt),12,2)) as basic_amt,0,1 as seqno  FROM CHALANITEM WHERE DOC_NO="+doc_no+" AND DOC_DT='"+querydt+"' union SELECT '','','','','ADD AMOUNT=>',ltrim(str(add_amount,12,2)) as basic_amt,0,2 as seqno FROM CHALANITEM WHERE DOC_NO="+doc_no+" AND DOC_DT='"+querydt+"' and doc_srno=1 union SELECT '','','','','NET TOTAL=>',ltrim(str(sum(basic_amt)+(SELECT add_amount FROM CHALANITEM A WHERE A.DOC_NO=CHALANITEM.DOC_NO AND A.DOC_DT=CHALANITEM.DOC_DT and A.doc_srno=1),12,2)) as basic_amt,0,3 as seqno FROM CHALANITEM WHERE DOC_NO="+doc_no+" AND DOC_DT='"+querydt+"' GROUP BY DOC_NO,DOC_DT ORDER BY SEQNO,DOC_SRNO");
               // PreparedStatement ps = con.prepareStatement("SELECT BRND_DESC,SIZE_DESC,LTRIM(RTRIM(STR(QUANTITY,12,2))) AS CASE_QTY,LTRIM(RTRIM(STR(BOTTLE_QTY,12,0))) AS BTL_QTY FROM CHALANITEM,BRNDMAST,SIZEMAST,ITEMMAST WHERE ITEMMAST.ITEM_CODE=CHALANITEM.ITEM_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND ITEMMAST.SIZE_CODE=SIZEMAST.SIZE_CODE AND DOC_NO="+doc_no+" AND DOC_DT='"+querydt+"' ORDER BY DOC_SRNO");
                ResultSet rs = ps.executeQuery();

                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("BRND_DESC", rs.getString("BRND_DESC"));
                    map.put("SIZE_DESC", rs.getString("SIZE_DESC"));
                    map.put("CASE_QTY", rs.getString("CASE_QTY"));
                    map.put("BTL_QTY", rs.getString("BTL_QTY"));
                    map.put("basic_rate", rs.getString("basic_rate"));
                    map.put("basic_amt", rs.getString("basic_amt"));
                    map.put("seqno", rs.getString("seqno"));


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
            // Toast.makeText(getApplicationContext(), "Error.." + e, Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chalan_reg_value_wise_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("BRND_DESC"));
            holder.list_d2.setText(attendance_list.get(position).get("SIZE_DESC"));
            holder.list_d3.setText(attendance_list.get(position).get("CASE_QTY"));
            holder.list_d4.setText(attendance_list.get(position).get("BTL_QTY"));
            holder.list_d5.setText(attendance_list.get(position).get("basic_rate"));
            holder.list_d6.setText(attendance_list.get(position).get("basic_amt"));
            holder.list_d5.setTextColor(Color.BLACK);
            holder.list_d6.setTextColor(Color.BLACK);
            if(!attendance_list.get(position).get("seqno").equals("0"))
            {
                holder.list_d5.setTextColor(Color.BLUE);
                holder.list_d6.setTextColor(Color.RED);
            }


        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3,list_d4,list_d5,list_d6;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);
                this.list_d6 = (TextView) itemView.findViewById(R.id.list_d6);

            }
        }
    }

}
