package com.example.admin_beerbar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Online_Scanning_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date,txt_total;
    ProgressBar pgb;
    PreparedStatement ps1;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    //String db = "WINESHOP";
   // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String chkdate,displaydate,db,str_month="",str_day="",m_item_code;
    TransparentProgressDialog pd;
    double m_total=0.00;
    double r_total=0.00;
    double total=0.00;
    double last_total=0.00;
    int m_compcode,m_TAB_CODE;
    Button btn_green,btn_red;
    TextView edt_total_sale,edt_date,edt_net_sale,edt_total_sale_return;
    String IMEINumber,con_ipaddress,portnumber;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;

    //================Recyclerview 2======================
    ArrayList<HashMap<String, String>> menu_card_arryList_sale;
    private RecyclerView.LayoutManager layoutManager_pe_sale;
    sale_atnds_recyclerAdapter attendance_recyclerAdapter_sale;
    private RecyclerView recycler_medal_offline_rpt_list_sale;
    ScheduledExecutorService scheduler;
    public Online_Scanning_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.online_scanning, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edt_total_sale_return=(TextView)view.findViewById(R.id.edt_total_sale_return);
        edt_total_sale=(TextView)view.findViewById(R.id.edt_total_sale);
        btn_green=(Button) view.findViewById(R.id.btn_green);
        btn_red=(Button) view.findViewById(R.id.btn_red);
        edt_date=(TextView)view.findViewById(R.id.edt_date);
        edt_net_sale=(TextView)view.findViewById(R.id.edt_net_sale);
        SharedPreferences ss = getActivity().getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) view.findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getActivity(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);


        //---------------------Recyclerview 2-----------------------------------------
        menu_card_arryList_sale = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list_sale = (RecyclerView) view.findViewById(R.id.recycler_return_list);
        layoutManager_pe_sale = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list_sale.setLayoutManager(layoutManager_pe_sale);
        attendance_recyclerAdapter_sale = new sale_atnds_recyclerAdapter(getActivity(), menu_card_arryList_sale);
        recycler_medal_offline_rpt_list_sale.setAdapter(attendance_recyclerAdapter_sale);

        pgb=(ProgressBar)view.findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);


        try {

            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                PreparedStatement ps = con.prepareStatement("select isnull(convert(varchar(10),max(doc_dt),101),convert(varchar(10),getdate(),101)) as chkdate, isnull(convert(varchar(10),max(doc_dt),103),convert(varchar(10),getdate(),103)) as displaydate from countersaleitem where doc_dt = getdate()");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    chkdate= rs.getString("chkdate");
                    displaydate= rs.getString("displaydate");
                    Log.d("dddd",chkdate);
                    Log.d("dddd",displaydate);
                    edt_date.setText(displaydate);
                }
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
        btn_red.setVisibility(View.VISIBLE);
        btn_green.setVisibility(View.INVISIBLE);
        sales_data();
        retun_sales_data();
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

                                PreparedStatement ps = con.prepareStatement("select isnull(sum(item_value-discount_amount),0)-isnull((select sum(item_value) from countersalereturnitem where doc_dt = '"+ chkdate+"'),0) as item_value from countersaleitem where stock_adj_yn=0 and doc_dt = '"+ chkdate+"' and rate_type=0 and tran_type=0 and bill_no=0");
                                ResultSet rs = ps.executeQuery();
                                while (rs.next()) {
                                    last_total= rs.getDouble("item_value");
                                }
                            }

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
                        }

                      //  Toast.makeText(getActivity(), "It works", Toast.LENGTH_SHORT).show();
                       // Log.d("Number", "111");
                        if (last_total!=total) {
                          btn_red.setVisibility(View.INVISIBLE);
                          btn_green.setVisibility(View.VISIBLE);
                            sales_data();
                            retun_sales_data();
                            btn_red.setVisibility(View.VISIBLE);
                            btn_green.setVisibility(View.INVISIBLE);

                        }

                    }
                });
            }
        }, 10, 10, TimeUnit.SECONDS);

    }

    public void sales_data() {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                PreparedStatement ps = con.prepareStatement("select brnd_desc,size_desc,ltrim(str(qty)) as qty,ltrim(str(rate,12,2)) as rate,ltrim(str(item_value,12,2)) as item_value,LTrim(Right(convert(Char(20),countersaleitem.tran_date, 22), 11)) as time_desc,ltrim(str(discount_amount,12,2)) as discount_amount from countersaleitem,itemmast,brndmast,sizemast where countersaleitem.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code and stock_adj_yn=0 and doc_dt = '"+chkdate+"'  and comp_code=1 and rate_type=0 and tran_type=0 and bill_no=0 order by doc_srno");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                m_total=0;
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("brnd_desc", rs.getString("brnd_desc"));
                    map.put("size_desc", rs.getString("size_desc"));
                    map.put("qty", rs.getString("qty"));
                    map.put("item_value", rs.getString("item_value"));
                    map.put("time_desc", rs.getString("time_desc"));
                    map.put("discount_amount", rs.getString("discount_amount"));
                    map.put("rate", rs.getString("rate"));

                     m_total=m_total+rs.getDouble("item_value")-rs.getDouble("discount_amount");

                    menu_card_arryList.add(map);
                    recycler_medal_offline_rpt_list.scrollToPosition(menu_card_arryList.size() - 1);
                }
                NumberFormat nf1 =new DecimalFormat(".00");
                edt_total_sale.setText(""+nf1.format(m_total));
            }
            pgb.setVisibility(View.GONE);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_scanning_list_, parent, false);
            atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new atnds_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {

            // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.list_d1.setText(attendance_list.get(position).get("brnd_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("size_desc"));
            holder.list_d3.setText(attendance_list.get(position).get("qty"));
            holder.list_d4.setText(attendance_list.get(position).get("item_value"));
            holder.list_d5.setText(attendance_list.get(position).get("time_desc"));
            holder.list_d6.setText(attendance_list.get(position).get("rate"));

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3, list_d4, list_d5,list_d6;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);
                this.list_d6 = (TextView) itemView.findViewById(R.id.list_d6);

            }
        }
    }

    //===============================Return===============================
    public void retun_sales_data() {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                PreparedStatement ps = con.prepareStatement("select brnd_desc,size_desc,ltrim(str(qty)) as qty,ltrim(str(rate,12,2)) as rate,ltrim(str(item_value,12,2)) as item_value,LTrim(Right(convert(Char(20),countersalereturnitem.tran_date, 22), 11)) as time_desc from countersalereturnitem,itemmast,brndmast,sizemast where countersalereturnitem.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code and doc_dt = '"+chkdate+"'  and comp_code=1 order by doc_srno");

                ResultSet rs = ps.executeQuery();
                menu_card_arryList_sale.clear();
                r_total=0;
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("brnd_desc", rs.getString("brnd_desc"));
                    map.put("size_desc", rs.getString("size_desc"));
                    map.put("qty", rs.getString("qty"));
                    map.put("item_value", rs.getString("item_value"));
                    map.put("time_desc", rs.getString("time_desc"));
                    map.put("rate", rs.getString("rate"));

                    r_total=r_total+rs.getDouble("item_value");
                    menu_card_arryList_sale.add(map);
                    recycler_medal_offline_rpt_list_sale.scrollToPosition(menu_card_arryList_sale.size() - 1);
                }
                NumberFormat nf1 =new DecimalFormat(".00");
                edt_total_sale_return.setText(""+nf1.format(r_total));
                total=m_total-r_total;
                edt_net_sale.setText(""+nf1.format(total));
                last_total=total;
            }
            pgb.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + menu_card_arryList_sale.toString());

            if (attendance_recyclerAdapter_sale != null) {
                attendance_recyclerAdapter_sale.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter_sale.toString());
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }

    public class sale_atnds_recyclerAdapter extends RecyclerView.Adapter<sale_atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public sale_atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public sale_atnds_recyclerAdapter.Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_scanning_list_, parent, false);
            sale_atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new sale_atnds_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final sale_atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {

            // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.list_d1.setText(attendance_list.get(position).get("brnd_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("size_desc"));
            holder.list_d3.setText(attendance_list.get(position).get("qty"));
            holder.list_d4.setText(attendance_list.get(position).get("item_value"));
            holder.list_d5.setText(attendance_list.get(position).get("time_desc"));
            holder.list_d6.setText(attendance_list.get(position).get("rate"));


        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3, list_d4, list_d5,list_d6;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);
                this.list_d6 = (TextView) itemView.findViewById(R.id.list_d6);


            }
        }
    }
    //-------------------------------------------------------------------
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
