package com.example.admin_beerbar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Item_Wise_Ledger_Other_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date;
    SearchView searchView;
    Button btn_report;
    String SubCodeStr,db;
    ProgressBar pgb;
    PreparedStatement ps1;
    ResultSet rs;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "WINESHOP";
    //String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    int m_opbl=0;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="",m_purdate,m_item_code;
    TransparentProgressDialog pd;
    DecimalFormat df2;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    int m_compcode,m_TAB_CODE;
    double m_clbal,cbal;

    String IMEINumber,con_ipaddress,portnumber,str_amount_1;
    public Item_Wise_Ledger_Other_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.item_wise_ledger_other, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences ss = getActivity().getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

        pgb=(ProgressBar)view.findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) view.findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getActivity(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
       // report_search("");
        report_search("%");
        //------------------------------------------------------------------------------------------
        searchView=(SearchView)view.findViewById(R.id.report_searchView);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 0) {

                    SubCodeStr = newText;
                    SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();

                    //subcodestr = subcodestr.replaceAll("\\s+", "% ").toLowerCase();
                    Log.d("sucess", SubCodeStr);

                    //new FetchSearchResult().execute();
                    report_search(SubCodeStr);
                    //report_search();
                } else if (TextUtils.isEmpty(newText)) {
                    // lin_grid_visible.setVisibility(View.INVISIBLE);
                    // menu_card_arryList.clear();
                    // menu_search("");
                } else {
                    report_search("%");
                    //report_search( SubCodeStr);
                }
                return false;
            }
        });

        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        edt_frm_date=(TextView) view.findViewById(R.id.edt_frm_date);
        edt_to_date=(TextView) view.findViewById(R.id.edt_to_date);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        formattedDate = df.format(c);
        edt_to_date.setText(formattedDate);
        edt_frm_date.setText(formattedDate);
        Date  d = Calendar.getInstance().getTime();

        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Temp_frm_date=out.format(d);

        Date dd = Calendar.getInstance().getTime();
        SimpleDateFormat ot = new SimpleDateFormat("MM/dd/yyyy");
        Temp_to_date=ot.format(dd);

        edt_frm_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edt_frm_date.setText(""+str_day + "/" + str_month + "/" + year);
                                Temp_frm_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                //edt_frm_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        edt_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edt_to_date.setText(""+str_day + "/" + str_month + "/" + year);
                                Temp_to_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                //edt_to_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);

                try {
                    //String sDate1="12/02/2020";
                    String sDate1=edt_frm_date.getText().toString();
                    Date date=new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
                    datePickerDialog.getDatePicker().setMinDate(date.getTime());
                }catch (Exception e)
                {

                }

                //datePickerDialog.getDatePicker().setMinDate(d.getTime());
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

       // insert_data(Temp_frm_date,Temp_to_date);
    //    report_search();
    }

    public void report_search(String SubCodeStr) {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                PreparedStatement ps = con.prepareStatement("SELECT ITEM_CODE,liqr_desc as Type,brnd_desc AS BRAND,size_desc AS SIZE,seq_no FROM ITEMMAST,SIZEMAST,BRNDMAST,liqrmast WHERE ITEMMAST.LIQR_CODE=liqrmast.LIQR_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND ITEMMAST.SIZE_CODE=SIZEMAST.SIZE_CODE AND ITEM_CODE IN (SELECT ITEM_CODE FROM ONLNSTOK) AND brnd_desc LIKE '"+SubCodeStr+"%' order by type,brand,seq_no");

                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("item_code", rs.getString("item_code"));
                    map.put("Type", rs.getString("Type"));
                    map.put("Brand", rs.getString("Brand"));
                   // map.put("Size", rs.getString("Size"));

                    menu_card_arryList.add(map);
                }
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_other, parent, false);
            atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new atnds_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {


            // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.list_d1.setText(attendance_list.get(position).get("Type"));
            holder.list_d2.setText(attendance_list.get(position).get("Brand"));
           // holder.list_d3.setText(attendance_list.get(position).get("Size"));
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_item_code=attendance_list.get(position).get("item_code");
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            try {

                                con = CONN(con_ipaddress,portnumber,db);
                                if (con == null) {
                                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                                } else {
                                    String q="select PURCHASE_STOCK_AS_PER from profile";

                                    PreparedStatement ps = con.prepareStatement(q);
                                    ResultSet rs = ps.executeQuery();
                                    while(rs.next())
                                    {
                                        m_purdate=rs.getString("PURCHASE_STOCK_AS_PER");
                                        Log.d("m_purdate",m_purdate);
                                    }
                                }

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
                            }
                           // df2 = new DecimalFormat("#.##");
                           // str_amount_1=attendance_list.get(position).get("amount_1");
                            report_data(Temp_frm_date,Temp_to_date);
                            Intent i=new Intent(getActivity(),Item_Wise_Ledger_Report.class);
                            i.putExtra("from_date", edt_frm_date.getText().toString());
                            i.putExtra("to_date", edt_to_date.getText().toString());
                            i.putExtra("forname", attendance_list.get(position).get("Brand")+", "+attendance_list.get(position).get("Size"));
                            i.putExtra("m_opbl", m_opbl);
                           // i.putExtra("m_clbal", df2.format(m_clbal));
                            //i.putExtra("forname", attendance_list.get(position).get("gl_desc"));

                            startActivity(i);
                            pd.dismiss();
                        }
                    }, 3000);


                }
            });

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3, list_d4, list_item_type;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                ////this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                //this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);

            }
        }
    }

  public void report_data(String frm_date,String to_date)
    {
        //===========ALL==============================
        try
        {
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {


                ps1 = con.prepareStatement("select isnull(sum(op_qty),0) as qty from opitstok where comp_code="+m_compcode+" and item_code='"+m_item_code+"' union select isnull(sum(op_qty),0) as qty from opitstok_b where comp_code="+m_compcode+" and item_code='"+m_item_code+"' union select isnull(sum(bottle_qty)+sum(free_qty),0) as qty from puritem where "+m_purdate+" < '" +Temp_frm_date+"' and stock_method=1 and comp_code="+m_compcode+" and item_code='"+m_item_code+"' union select isnull(sum(bottle_qty),0) as qty from chalanitem where doc_dt < '" +Temp_frm_date+"' and stock_method=1 and comp_code="+m_compcode+" and item_code='"+m_item_code+"' union select -isnull(sum(qty+breakage_qty),0) as qty from countersaleitem where doc_dt < '" +Temp_frm_date+"' and stock_method=1 and comp_code="+m_compcode+" and item_code='"+m_item_code+"'  union select -isnull(sum(qty+breakage_qty),0) as qty from provisionalsaleitem where doc_dt < '" +Temp_frm_date+"' and stock_method=1 and comp_code="+m_compcode+" and item_code='"+m_item_code+"' union  select isnull(sum(qty),0) as qty from countersalereturnitem where doc_dt < '" +Temp_frm_date+"' and stock_method=1 and comp_code="+m_compcode+" and item_code='"+m_item_code+"'");
                rs = ps1.executeQuery();
                m_opbl=0;
                while(rs.next())
                {
                    m_opbl=m_opbl+rs.getInt("qty");
                }

                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code= "+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,amount_1,gl_desc,TAB_CODE,doc_no) select doc_dt,isnull(sum(qty+breakage_qty),0),'COUNTER SALE',"+m_TAB_CODE+",1 from countersaleitem where doc_dt between '" +Temp_frm_date+"' and '" +Temp_to_date+"' and comp_code=" +m_compcode + " and item_code = '"+m_item_code+"' group by doc_dt");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,amount_1,gl_desc,TAB_CODE,doc_no) select doc_dt,isnull(sum(qty+breakage_qty),0),'COUNTER SALE',"+m_TAB_CODE+",1 from provisionalsaleitem where doc_dt between '" +Temp_frm_date+"' and '" +Temp_to_date+"' and comp_code=" +m_compcode + " and item_code = '"+m_item_code+"' group by doc_dt");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,amount_1,gl_desc,TAB_CODE,doc_no) select doc_dt,-isnull(sum(qty),0),'COUNTER SALE',"+m_TAB_CODE+",1 from countersalereturnitem  where doc_dt between '" +Temp_frm_date+"' and '" +Temp_to_date+"' and comp_code=" +m_compcode + " and item_code = '"+m_item_code+"' group by doc_dt");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,amount_1,gl_desc,TAB_CODE,crdr_cd) select doc_dt,sum(amount_1),'COUNTER SALE',"+m_TAB_CODE+",'D' from tabreportparameters where TAB_CODE ="+m_TAB_CODE+" group by doc_dt");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code= "+m_TAB_CODE+" and doc_no=1");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,amount_1,gl_desc,TAB_CODE) select doc_dt,isnull((bottle_qty)+(free_qty),0),isnull((select 'Bill No.' + ltrim(invoice_no) from purchase where doc_no=puritem.doc_no and doc_dt=puritem.doc_dt and comp_code=puritem.comp_code)+'/','')+isnull((select gl_desc from glmast where ac_head_id in(select ac_head_id from purchase where doc_no=puritem.doc_no and doc_dt=puritem.doc_dt and comp_code=puritem.comp_code)),''),"+m_TAB_CODE+" from puritem where doc_dt between '" +Temp_frm_date+"' and '" +Temp_to_date+"' and comp_code=" +m_compcode + " and item_code = '"+m_item_code+"' ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,amount_1,gl_desc,TAB_CODE) select doc_dt,isnull((bottle_qty),0),'CHALLAN',"+m_TAB_CODE+" from chalanitem  where doc_dt between '" +Temp_frm_date+"' and '" +Temp_to_date+"' and comp_code=" +m_compcode + " and item_code = '"+m_item_code+"' ");
                ps1.executeUpdate();

            }
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
        }
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
