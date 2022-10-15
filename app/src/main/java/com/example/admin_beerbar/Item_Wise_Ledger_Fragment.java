package com.example.admin_beerbar;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Item_Wise_Ledger_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    ImageView img_frm_date,img_to_date;
    TextView edt_frm_date,edt_to_date;
    DatePickerDialog datePickerDialog;
    SearchView searchView;
    Button btn_proceed;
    String SubCodeStr;
    ProgressBar pgb;
    PreparedStatement ps1;
    ResultSet rs;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "WINESHOP";
    // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;

    String m_loct_desc,m_loct_code,m_opbl;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="",db,m_item_code;
    TransparentProgressDialog pd;
    DecimalFormat df2;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    int m_compcode,m_TAB_CODE;
    double m_clbal,cbal;
    Spinner sp_stock_loc;
    String IMEINumber,con_ipaddress,portnumber,str_amount_1;
    ProgressDialog progressDoalog;
    public Item_Wise_Ledger_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.item_wise_ledger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_proceed=(Button)view.findViewById(R.id.btn_proceed);
        SharedPreferences ss = getActivity().getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);
        sp_stock_loc=(Spinner)view.findViewById(R.id.sp_stock_loc);
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

       /* //------------------------------------------------------------------------------------------
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
        });*/

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
                menu_card_arryList.clear();
                attendance_recyclerAdapter.notifyDataSetChanged();
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
                menu_card_arryList.clear();
                attendance_recyclerAdapter.notifyDataSetChanged();
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
        //report_search("%");
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_card_arryList.clear();
                attendance_recyclerAdapter.notifyDataSetChanged();
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        pre_insert_data(Temp_frm_date,Temp_to_date);
                        report_search("%");
                        pd.dismiss();
                    }
                }, 3000);
                //-----------
            }
        });
        new load_spinner_data().execute();
    }

    public void report_search(String SubCodeStr) {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                //PreparedStatement ps = con.prepareStatement("SELECT ITEM_CODE,liqr_desc as Type,brnd_desc AS BRAND,size_desc AS SIZE,seq_no FROM ITEMMAST,SIZEMAST,BRNDMAST,liqrmast WHERE ITEMMAST.LIQR_CODE=liqrmast.LIQR_CODE AND ITEMMAST.BRND_CODE=BRNDMAST.BRND_CODE AND ITEMMAST.SIZE_CODE=SIZEMAST.SIZE_CODE AND ITEM_CODE IN (SELECT ITEM_CODE FROM ONLNSTOK) AND brnd_desc LIKE '"+SubCodeStr+"%' order by type,brand,seq_no");
                PreparedStatement ps = con.prepareStatement("select distinct item_code,(select menu_desc from menumast where menu_code in(select menu_code from menucarditemmast where menuitem_code=tabreportparameters.item_code)) as Type,(select menuitem_desc from menucarditemmast where menuitem_code=tabreportparameters.item_code)as Brand,'',str(gl_opbal,12,2) as gl_opbal from tabreportparameters where tab_code="+m_TAB_CODE+" and (gl_opbal+amount_1+tot_sale) <> 0 order by type,brand");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("item_code", rs.getString("item_code"));
                    map.put("Type", rs.getString("Type"));
                    map.put("Brand", rs.getString("Brand"));
                    map.put("gl_opbal", rs.getString("gl_opbal"));

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_, parent, false);
            atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new atnds_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {


            // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.list_d1.setText(attendance_list.get(position).get("Type"));
            holder.list_d2.setText(attendance_list.get(position).get("Brand"));
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            m_item_code=attendance_list.get(position).get("item_code");
                            m_opbl=attendance_list.get(position).get("gl_opbal");
                           // df2 = new DecimalFormat("#.##");
                           // str_amount_1=attendance_list.get(position).get("amount_1");
                            post_insert_data(Temp_frm_date,Temp_to_date);
                            Intent i=new Intent(getActivity(),Item_Wise_Ledger_Report.class);
                            i.putExtra("from_date", edt_frm_date.getText().toString());
                            i.putExtra("to_date", edt_to_date.getText().toString());
                            i.putExtra("forname", attendance_list.get(position).get("Brand"));
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
               // this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                //this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);

            }
        }
    }
    public void pre_insert_data(String frm_date,String to_date)
    {
        //===========ALL==============================
        try
        {
            con = CONN(con_ipaddress,portnumber,db);
            ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "");
            ps1.executeUpdate();
            if(m_loct_code.equals("0.0")) {

                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,tab_code) select distinct item_code,"+ m_TAB_CODE +" from opitstok where item_type=2 and comp_code="+ m_compcode +" union select distinct item_code,"+ m_TAB_CODE +" from puritem where pur_type=2 and doc_dt <= '" + to_date + "' and comp_code="+ m_compcode +" and stock_method=1 union select distinct item_code,"+ m_TAB_CODE +" from countersaleitem where doc_dt <= '" + to_date + "' and comp_code=" + m_compcode + " and item_type=3 and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) union select distinct item_code,"+ m_TAB_CODE +" from countersaleitem where doc_dt <= '" + to_date + "' and comp_code=" + m_compcode + " and item_type=3 and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = isnull((select sum(op_qty) from opitstok where item_type=2 and comp_code=" + m_compcode + " and item_code=tabreportparameters.item_code),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal + isnull((select sum(bottle_qty)+sum(free_qty) from puritem where doc_dt < '" + frm_date + "' and pur_type=2 and stock_method=1 and comp_code="+ m_compcode +"),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(qty) from countersaleitem where item_type=3 and doc_dt < '" + frm_date + "' and maintain_stock=1 and comp_code=" + m_compcode + " and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1)),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(qty) from saleitem where item_type=3 and doc_dt < '" + frm_date + "' and maintain_stock=1 and comp_code=" + m_compcode + " and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1)),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = isnull((select sum(bottle_qty)+sum(free_qty) from puritem where pur_type=2 and doc_dt between '" + frm_date + "' and '" + to_date + "' and stock_method=1 and comp_code=" + m_compcode + "),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = isnull((select sum(qty) from countersaleitem where item_type=3 and doc_dt between '" + frm_date + "' and '" + to_date + "' and maintain_stock=1 and comp_code=" + m_compcode + "  and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1)),0) where tab_code=" + m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale+isnull((select sum(qty) from saleitem where item_type=3 and doc_dt between '" + frm_date + "' and '" + to_date + "' and maintain_stock=1 and comp_code="+ m_compcode +"  and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1)),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_opbal+amount_1-tot_sale where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();

            }
            else {

                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,tab_code) select distinct item_code,"+ m_TAB_CODE +" from opitstok where item_type=2 and comp_code="+m_compcode+" and loct_code = "+m_loct_code+" union select distinct item_code,"+ m_TAB_CODE +" from puritem where pur_type=2 and doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and stock_method=1 and loct_code = "+m_loct_code+" union select distinct item_code,"+ m_TAB_CODE +" from countersaleitem where doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and item_type=3 and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+" union select distinct item_code,"+ m_TAB_CODE +" from countersaleitem where doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and item_type=3 and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+" union select distinct item_code,"+ m_TAB_CODE +" from transfernote where doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and item_type=2 and to_loct_code = "+m_loct_code+" union select distinct item_code,"+m_TAB_CODE+" from transfernote where doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and item_type=2 and from_loct_code = "+m_loct_code+" ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = isnull((select sum(op_qty) from opitstok where item_type=2 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal + isnull((select sum(bottle_qty)+sum(free_qty) from puritem where doc_dt < '"+frm_date+"' and pur_type=2 and stock_method=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal + isnull((select sum(bottle_qty) from transfernote where doc_dt < '"+frm_date+"' and item_type=2 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and to_loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(qty) from countersaleitem where item_type=3 and doc_dt < '"+frm_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+"),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(qty) from saleitem where item_type=3 and doc_dt < '"+frm_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+"),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(bottle_qty) from transfernote where item_type=2 and doc_dt < '"+frm_date+"' and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and from_loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = isnull((select sum(bottle_qty)+sum(free_qty) from puritem where pur_type=2 and doc_dt between '"+frm_date+"' and '"+to_date+"' and stock_method=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1+isnull((select sum(bottle_qty) from transfernote where item_type=2 and doc_dt between '"+frm_date+"' and '"+to_date+"' and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and to_loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = isnull((select sum(qty) from countersaleitem where item_type=3 and doc_dt between '"+frm_date+"' and '"+to_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale+isnull((select sum(qty) from saleitem where item_type=3 and doc_dt between '"+frm_date+"' and '"+to_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale+isnull((select sum(bottle_qty) from transfernote where item_type=2 and doc_dt between '"+frm_date+"' and '"+to_date+"' and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and from_loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_opbal+amount_1-tot_sale where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();


            }

           /* ps1 = con.prepareStatement("update tabreportparameters set amount = isnull((select sum(item_value) from countersaleitem where item_type=3 and doc_dt between '"+frm_date+"' and '"+to_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set amount = amount+isnull((select sum(item_value) from saleitem where item_type=3 and doc_dt between '"+frm_date+"' and '"+to_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
            ps1.executeUpdate();*/
        }
        catch(NullPointerException e)
        {
            Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
        }
    }
    public void post_insert_data(String frm_date,String to_date)
    {
        //===========ALL==============================
        try
        {
            con = CONN(con_ipaddress,portnumber,db);
            ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "");
            ps1.executeUpdate();
            if(m_loct_code.equals("0.0")) {

                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,gl_opbal,gl_desc,tab_code,doc_no) select doc_dt,isnull((bottle_qty)+(free_qty),0),isnull((select 'Bill No.' + ltrim(invoice_no) from purchase where doc_no=puritem.doc_no and doc_dt=puritem.doc_dt and comp_code=puritem.comp_code)+'/','')+isnull((select gl_desc from glmast where ac_head_id in(select ac_head_id from purchase where doc_no=puritem.doc_no and doc_dt=puritem.doc_dt and comp_code=puritem.comp_code)),''),"+m_TAB_CODE+",1 from puritem where doc_dt between '"+frm_date+"' and '"+to_date+"' and comp_code="+m_compcode+" and stock_method=1 and pur_type=2  and item_code="+m_item_code+" ");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,gl_opbal,gl_desc,tab_code,doc_no) select doc_dt,isnull(sum(qty),0),'COUNTER SALE',"+m_TAB_CODE+",2 from countersaleitem where item_type=3 and doc_dt between '" + frm_date + "' and '" + to_date + "' and comp_code="+ m_compcode +" and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and item_code="+m_item_code+" group by doc_dt");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,gl_opbal,gl_desc,tab_code,doc_no) select doc_dt,isnull(sum(qty),0),'COUNTER SALE',"+m_TAB_CODE+",2 from saleitem where item_type=3 and doc_dt between '" + frm_date + "' and '" + to_date + "' and comp_code="+ m_compcode +" and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and item_code="+m_item_code+" group by doc_dt");
                ps1.executeUpdate();

            }
            else {

                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,gl_opbal,gl_desc,tab_code,doc_no) select doc_dt,isnull((bottle_qty)+(free_qty),0),isnull((select 'Bill No.' + ltrim(invoice_no) from purchase where doc_no=puritem.doc_no and doc_dt=puritem.doc_dt and comp_code=puritem.comp_code)+'/','')+isnull((select gl_desc from glmast where ac_head_id in(select ac_head_id from purchase where doc_no=puritem.doc_no and doc_dt=puritem.doc_dt and comp_code=puritem.comp_code)),''),"+m_TAB_CODE+",1 from puritem where doc_dt between '" + frm_date + "' and '" + to_date + "' and comp_code="+m_compcode+" and stock_method=1 and pur_type=2  and item_code="+m_item_code+" and loct_code="+m_loct_code+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,gl_opbal,gl_desc,tab_code,doc_no) select doc_dt,isnull(sum(qty),0),'TRANSFER RCV',"+m_TAB_CODE+",1 from transfernote where item_type=2 and doc_dt between '" + frm_date + "' and '" + to_date + "' and comp_code="+ m_compcode +" and item_code="+m_item_code+" and to_loct_code="+m_loct_code+" group by doc_dt");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,gl_opbal,gl_desc,tab_code,doc_no) select doc_dt,isnull(sum(qty),0),'COUNTER SALE',"+m_TAB_CODE+",2 from countersaleitem where item_type=3 and doc_dt between '" + frm_date + "' and '" + to_date + "' and comp_code="+ m_compcode +" and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and item_code="+m_item_code+" and loct_code="+m_loct_code+" group by doc_dt");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,gl_opbal,gl_desc,tab_code,doc_no) select doc_dt,isnull(sum(qty),0),'COUNTER SALE',"+m_TAB_CODE+",2 from saleitem where item_type=3 and doc_dt between '" + frm_date + "' and '" + to_date + "' and comp_code="+ m_compcode +" and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and item_code="+m_item_code+" and loct_code="+m_loct_code+" group by doc_dt");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (doc_dt,gl_opbal,gl_desc,tab_code,doc_no) select doc_dt,isnull(sum(qty),0),'TRANSFER ISS',"+m_TAB_CODE+",3 from transfernote where item_type=2 and doc_dt between '" + frm_date + "' and '" + to_date + "' and comp_code="+ m_compcode +" and item_code="+m_item_code+" and from_loct_code="+m_loct_code+" group by doc_dt");
                ps1.executeUpdate();

            }

           /* ps1 = con.prepareStatement("update tabreportparameters set amount = isnull((select sum(item_value) from countersaleitem where item_type=3 and doc_dt between '"+frm_date+"' and '"+to_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set amount = amount+isnull((select sum(item_value) from saleitem where item_type=3 and doc_dt between '"+frm_date+"' and '"+to_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
            ps1.executeUpdate();*/
        }
        catch(NullPointerException e)
        {
            Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
        }
    }
    public class load_spinner_data extends AsyncTask<String, String, String> {
        List<Map<String, String>> sp_data = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            progressDoalog = new ProgressDialog(getActivity());
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                con = CONN(con_ipaddress,portnumber,db);
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    String query = "select LOCT_CODE,LOCT_DESC from LOCTmast where LOCT_CODE >0 and loct_code in(select loct_code from onlnstok where item_type =2) union select 0 as loct_code,'TOTAL LOCATIONS' as loct_desc order by LOCT_DESC ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));
                        sp_data.add(data);
                    }
                }

            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDoalog.dismiss();
            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getActivity(), sp_data, R.layout.spin, from, views);
            sp_stock_loc.setAdapter(spnr_data);
            sp_stock_loc.setSelection(0);
            sp_stock_loc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    m_loct_desc = (String) obj.get("A");
                    m_loct_code = (String) obj.get("B");
                    //  Toast.makeText(getActivity(), "loct_code: "+m_loct_code, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
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
