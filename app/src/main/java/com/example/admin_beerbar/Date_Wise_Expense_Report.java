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
import java.util.ArrayList;
import java.util.HashMap;

public class Date_Wise_Expense_Report extends AppCompatActivity {

    Config connectionClass;
    ProgressBar pbbar;
    String con_ipaddress ,portnumber,str_compdesc,from_date,to_date;
    TextView txt_total;
    Double total=0.0;
    Double ttl=0.0;
    String qry="";
    Connection con;
    Toolbar toolbar;
    String IMEINumber,db,invoice_no,supplier_name,amount,querydt,doc_no,dfrom_date,dto_date;
    int m_TAB_CODE;
    String mclbal,forname,Query_date;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    HashMap<String, String> map;
    int m_compcode;
    int srno=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_wise_reciepts_report);
       // txt_total=(TextView)findViewById(R.id.txt_total);
        Bundle bd = getIntent().getExtras();
        try {
            dfrom_date = bd.getString("dfrm_date");
            dto_date = bd.getString("dto_date");
            to_date = bd.getString("to_date");
            from_date = bd.getString("frm_date");

        } catch (Exception e) {
        }

        SharedPreferences s = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = s.getInt("COMP_CODE", 0);
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
        toolbar_frm_date.setText(dfrom_date);
        toolbar_to_date.setText(dto_date);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("" + str_compdesc + "" + "\n" + "Date Wise Expenses");
        // toolbar_title.setText("Purchase Register Value Wise");

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

                PreparedStatement ps = con.prepareStatement("select convert(varchar(10),doc_dt,103)docdt,narr,gl_desc,str(amount,12,2) as amount,doc_dt,0 as seq  from dailyexp,glmast where  doc_dt BETWEEN '"+from_date+"' and '"+to_date+"'  and dailyexp.ac_head_id=glmast.ac_head_id union select '','','Date Wise Total',str(sum(amount),12,2) as amount,doc_dt,1 as seq from dailyexp where doc_dt BETWEEN '"+from_date+"' and '"+to_date+"'  group by doc_dt order by doc_dt,seq");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                srno=0;
                total=0.0;
                while (rs.next())
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    if (!rs.getString("gl_desc").equals("Date Wise Total")){
                        ++srno;
                        map.put("srno", ""+srno);
                        total = total + (Double.parseDouble(rs.getString("amount")));
                    }
                    map.put("docdt", rs.getString("docdt"));
                    map.put("narr", rs.getString("narr"));
                    map.put("gl_desc", rs.getString("gl_desc"));
                    map.put("amount", rs.getString("amount"));
                    menu_card_arryList.add(map);


                }
                NumberFormat nf1 =new DecimalFormat(".00");
                txt_total.setText(""+nf1.format(total));

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_wise_reciepts_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("srno"));
            holder.list_d2.setText(attendance_list.get(position).get("docdt"));
            holder.list_d3.setText(attendance_list.get(position).get("narr"));
            holder.list_d4.setText(attendance_list.get(position).get("gl_desc"));
            holder.list_d5.setText(attendance_list.get(position).get("amount"));
            holder.list_d4.setTextColor(Color.BLACK);
            if(attendance_list.get(position).get("gl_desc").equals("Date Wise Total"))
            {
                holder.list_d4.setTextColor(Color.BLUE);
            }
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
