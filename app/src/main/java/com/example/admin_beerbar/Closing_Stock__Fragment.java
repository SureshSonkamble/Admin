package com.example.admin_beerbar;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin_beerbar.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class Closing_Stock__Fragment extends Fragment {

    //---------------------------------------------------------
    int mYear, mMonth, mDay;
    String con_ipaddress ,portnumber;
    ProgressBar pbbar;
    ProgressDialog progressDoalog;
    TransparentProgressDialog pd;
    Button btn_proceed,btn_report,btn_check_all,btn_uncheck_all;
    //================Recyclerview 2======================
    ArrayList<HashMap<String, String>> bill_arryList;
    private RecyclerView.LayoutManager layoutManager_bill;
    tbill_recyclerAdapter bill_recyclerAdapter;
    private RecyclerView recycler_bill_list;

    //================Recyclerview swap======================
    ArrayList<HashMap<String, String>> swap_arryList;
    private RecyclerView.LayoutManager layoutManager_swap;
    tswap_recyclerAdapter swap_recyclerAdapter;
    private RecyclerView recycler_swap_list;
    HashMap<String, String> map;
    HashMap<String, String> smap;
    //--------------------------
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    //String db = "WINESHOP";
    //String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String IMEINumber = "";
    private ProgressBar progressBar;
    //---------------------------------------------------------
    CheckBox chk_stk_all_loc,chk_all;
    String str_stk_all_loc,str_all,str_summary="0";
    //EditText edt_data;
    TextView edt_date;
    Spinner sp_counter_godown,sp_cost_evaluation;
    String formattedDate, str_month="",str_day,systemDate,db;
    RadioGroup radioGroup,radiowiseGroup;
    String m_purdate;
    String m_loct_desc,m_loct_code,Temp_date,Query_date,M_ratetype_code,M_ratetype_desc;
    LinearLayout lin_sp_hide;
    String str_cost_evaluation,str_price_type_title;
    RadioButton rb,radio_bottle,radio_cases,radio_group_wise_stock,radio_trade_wise_stock,radio_summary_wise;
    String q="";
    String radio_val="";
    int m_TAB_CODE;
    int m_compcode;
    int txt_per=0;
    PreparedStatement ps1;
    LinearLayout lin_hide;
    String check_id="",M_liqr_code="";
    public Closing_Stock__Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_closing_stock_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        pbbar = (ProgressBar) view.findViewById(R.id.pgb);
        lin_sp_hide = (LinearLayout) view.findViewById(R.id.lin_sp_hide);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar_cyclic);

        //---------------------Recyclerview 2-----------------------------------------
        bill_arryList = new ArrayList<HashMap<String, String>>();
        recycler_bill_list = (RecyclerView) view.findViewById(R.id.recycler_bill_list);
        layoutManager_bill = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_bill_list.setLayoutManager(layoutManager_bill);
        bill_recyclerAdapter = new tbill_recyclerAdapter(getActivity(), bill_arryList);
        recycler_bill_list.setAdapter(bill_recyclerAdapter);
        //---------------------Recyclerview swap-----------------------------------------
        swap_arryList = new ArrayList<HashMap<String, String>>();
        recycler_swap_list = (RecyclerView)view. findViewById(R.id.recycler_swap_list);
        layoutManager_swap = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_swap_list.setLayoutManager(layoutManager_swap);
        swap_recyclerAdapter=new tswap_recyclerAdapter(getActivity(),swap_arryList);
        recycler_swap_list.setAdapter(swap_recyclerAdapter);
        //-------------------------------------------------
        chk_stk_all_loc=(CheckBox)view.findViewById(R.id.chk_stk_all_loc);
        chk_stk_all_loc.setChecked(true);
        chk_stk_all_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_stk_all_loc.isChecked())
                {
                    lin_sp_hide.setVisibility(View.GONE);
                }
                else
                {    str_stk_all_loc="0";
                    lin_sp_hide.setVisibility(View.VISIBLE);
                }
            }
        });
        lin_hide=(LinearLayout) view.findViewById(R.id.lin_hide);
        chk_all=(CheckBox)view.findViewById(R.id.chk_all);
        chk_all.setChecked(true);

        radio_group_wise_stock=(RadioButton) view.findViewById(R.id.radio_group_wise);
        radio_summary_wise=(RadioButton) view.findViewById(R.id.radio_summary_wise);
        radio_trade_wise_stock=(RadioButton) view.findViewById(R.id.radio_trade_wise);
        radio_bottle=(RadioButton) view.findViewById(R.id.radio_bottle);
        radio_cases=(RadioButton)view.findViewById(R.id.radio_cases);
        radio_bottle.setChecked(true);
        radio_group_wise_stock.setChecked(true);
        radioGroup=(RadioGroup)view.findViewById(R.id.radioGroup);
        radiowiseGroup=(RadioGroup)view.findViewById(R.id.radiowiseGroup);
        radiowiseGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                btn_check_all.setVisibility(View.VISIBLE);
                  rb=(RadioButton)view.findViewById(checkedId);
               // progressBar.setVisibility(View.VISIBLE);
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        try {
                            if (rb.getText().toString().equals("Group Wise Stock")) {
                                bill_arryList.clear();
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="Group_stock";
                                q = "select distinct(select liqr_code from itemmast where item_code=tabreportparameters.item_code)as liqr_code,(select liqr_desc from liqrmast where liqr_code in(select liqr_code from itemmast where item_code=tabreportparameters.item_code))as liqr_desc from tabreportparameters where TAB_CODE="+m_TAB_CODE+" order by liqr_desc";
                            } else if (rb.getText().toString().equals("Trader Wise Stock")) {
                                bill_arryList.clear();
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="Trader_stock";
                                q = "select ac_head_id,case when ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end from glmast where ac_head_id in(select ac_head_id from tabreportparameters where TAB_CODE="+m_TAB_CODE+") order by gl_desc";
                            }
                            else if (rb.getText().toString().equals("Summary")) {
                                bill_arryList.clear();
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="Summary";
                                q = "select distinct(select liqr_code from itemmast where item_code=tabreportparameters.item_code)as liqr_code,(select liqr_desc from liqrmast where liqr_code in(select liqr_code from itemmast where item_code=tabreportparameters.item_code))as liqr_desc from tabreportparameters where TAB_CODE="+m_TAB_CODE+" order by liqr_desc";
                            }else {
                                bill_arryList.clear();
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="";
                                q = "select distinct(select liqr_code from itemmast where item_code=tabreportparameters.item_code)as liqr_code,(select liqr_desc from liqrmast where liqr_code in(select liqr_code from itemmast where item_code=tabreportparameters.item_code))as liqr_desc from tabreportparameters where TAB_CODE = "+m_TAB_CODE+" order by liqr_desc";
                            }
                        }catch (Exception e)
                        {
                            q = "select distinct(select liqr_code from itemmast where item_code=tabreportparameters.item_code)as liqr_code,(select liqr_desc from liqrmast where liqr_code in(select liqr_code from itemmast where item_code=tabreportparameters.item_code))as liqr_desc from tabreportparameters where TAB_CODE = "+m_TAB_CODE+" order by liqr_desc";
                        }

                        insert_data(Temp_date);
                        load_data();

                        pd.dismiss();
                       // progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 2000);


            }
        });

        Date cc = Calendar.getInstance().getTime();
        SimpleDateFormat dff = new SimpleDateFormat("dd/MM/yyyy");
        systemDate = dff.format(cc);

        //date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        formattedDate = df.format(c);
        System.out.println("Today Date => " + formattedDate);

        edt_date=(TextView)view.findViewById(R.id.edt_date);
        edt_date.setText(formattedDate);

        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Temp_date=out.format(d);
        Query_date=Temp_date;

        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chk_all.setEnabled(true);
                btn_check_all.setVisibility(View.INVISIBLE);
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                lin_hide.setVisibility(View.INVISIBLE);
                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();
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
                                edt_date.setText(""+str_day + "/" + str_month + "/" + year);

                                Temp_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                Query_date=Temp_date;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        btn_proceed=(Button)view.findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               pd.show();
                // progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        chk_all.setEnabled(false);
                        q="";
                        radio_group_wise_stock.setChecked(true);
                        radio_summary_wise.setChecked(false);
                        radio_trade_wise_stock.setChecked(false);
                        // Actions to do after 10 seconds
                        lin_hide.setVisibility(View.VISIBLE);
                        bill_arryList.clear();
                        bill_arryList.clear();
                        bill_recyclerAdapter.notifyDataSetChanged();
                        swap_arryList.clear();
                        swap_recyclerAdapter.notifyDataSetChanged();
                        //-------------------Checkbox-----------------------------------------------
                        String msg="";
                        if(chk_stk_all_loc.isChecked()) {
                            msg = msg + "stok_for_all_location ";
                            str_stk_all_loc = "1";
                        }
                        else
                        {
                            str_stk_all_loc = "0";
                        }
                        if(chk_all.isChecked())
                            msg = msg + "all ";
                        str_all="1";


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

                        insert_data(Temp_date);
                        load_data();
                        if(bill_arryList.size()==0)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                            builder.setTitle("Alert");
                            builder.setIcon(R.drawable.warn);
                            builder.setMessage("Record Not Found");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            builder.show();
                        }

                        btn_check_all.setVisibility(View.VISIBLE);
                       pd.dismiss();
                        // progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 2000);

                //Toast.makeText(getActivity(), "loct_code: "+m_loct_code, Toast.LENGTH_SHORT).show();

            }
        });

        btn_report=(Button)view.findViewById(R.id.btn_report);
        //----------Stream--------------------------------------------------------
        sp_counter_godown=(Spinner)view.findViewById(R.id.sp_counter_godown);
        new load_spinner_data().execute();

        //----------Stream--------------------------------------------------------
        sp_cost_evaluation=(Spinner)view.findViewById(R.id.sp_cost_evaluation);
        new load_spinner_Rate_type().execute();

        btn_uncheck_all=(Button)view.findViewById(R.id.btn_uncheck_all);
        btn_check_all=(Button)view.findViewById(R.id.btn_check_all);
        btn_check_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //m_fullswapyn=1;
                btn_uncheck_all.setVisibility(View.VISIBLE);
                btn_check_all.setVisibility(View.INVISIBLE);
                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();
                try {
                    String qry;
                    con = CONN(con_ipaddress, portnumber,db);
                    if (con == null) {
                        Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                    } else {
                        if(radio_val.equals("Trader_stock"))
                        {
                            qry="select ac_head_id,case when ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end from glmast where ac_head_id in(select ac_head_id from tabreportparameters where TAB_CODE="+m_TAB_CODE+") order by gl_desc";
                        }
                        else
                        {
                            qry="select distinct(select case when liqr_code = 17 then 999 else liqr_code end from itemmast where item_code=tabreportparameters.item_code)as liqr_code,(select liqr_desc from liqrmast where liqr_code in(select liqr_code from itemmast where item_code=tabreportparameters.item_code))as liqr_desc from tabreportparameters where TAB_CODE = "+m_TAB_CODE+" and item_code <> '' order by liqr_desc";

                        }
                        PreparedStatement ps = con.prepareStatement(qry);
                        ResultSet rs = ps.executeQuery();

                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {
                            smap = new HashMap<String, String>();
                            smap.put("liqr_code", rs.getString(1));
                            smap.put("liqr_desc", rs.getString(2));
                            //-------------------------------------------
                            swap_arryList.add(smap);
                        }

                    }
                }catch (Exception e){ Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();}

            }
        });
        //-----------------------------------------------------------------------------------
        btn_uncheck_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                btn_check_all.setVisibility(View.VISIBLE);

                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();
                try {
                    String qry;
                    con = CONN(con_ipaddress, portnumber,db);
                    if (con == null) {
                        Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                    } else {
                        if(radio_val.equals("Trader_stock"))
                        {
                            qry="select ac_head_id,case when ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end from glmast where ac_head_id in(select ac_head_id from tabreportparameters where TAB_CODE="+m_TAB_CODE+") order by gl_desc";
                        }
                        else
                        {
                            qry="select distinct(select case when liqr_code = 17 then 999 else liqr_code end from itemmast where item_code=tabreportparameters.item_code)as liqr_code,(select liqr_desc from liqrmast where liqr_code in(select liqr_code from itemmast where item_code=tabreportparameters.item_code))as liqr_desc from tabreportparameters where TAB_CODE = "+m_TAB_CODE+" and item_code <> '' order by liqr_desc";

                        }
                        PreparedStatement ps = con.prepareStatement(qry);
                        ResultSet rs = ps.executeQuery();

                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {
                            map = new HashMap<String, String>();
                            map.put("liqr_code", rs.getString(1));
                            map.put("liqr_desc", rs.getString(2));
                            //-------------------------------------------
                            bill_arryList.add(map);

                        }

                    }
                }catch (Exception e){ Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();}

            }
        });
        //-----------------------------------------------------------------------------------
        btn_report=(Button)view.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                try
                {
                    con = CONN(con_ipaddress,portnumber,db);
                    if (con == null) {
                        Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                    } else {
                        ps1 = con.prepareStatement("delete from tabreportparameters where item_code='' and tab_code = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal=gl_opbal where TAB_CODE="+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        if(txt_per>=0)
                        {
                            ps1 = con.prepareStatement("update tabreportparameters set gl_clbal=gl_clbal+round(gl_clbal* "+txt_per+"/100,0) where TAB_CODE="+m_TAB_CODE+"");
                            ps1.executeUpdate();
                        }
                        else {
                            ps1 = con.prepareStatement("update tabreportparameters set gl_clbal=gl_clbal-round(gl_clbal* "+txt_per+"/100,0) where TAB_CODE="+m_TAB_CODE+"");
                            ps1.executeUpdate();
                        }

                        if(formattedDate.equals(systemDate)||!str_cost_evaluation.equals("MRP"))
                        {
                            ps1 = con.prepareStatement("update tabreportparameters set amount = (select "+str_cost_evaluation+" from itemmast where item_code=tabreportparameters.item_code) where TAB_CODE="+m_TAB_CODE+"");
                            ps1.executeUpdate();
                            if(str_cost_evaluation.equals("PURCHASE_PRICE"))
                            {
                                ps1 = con.prepareStatement("update tabreportparameters set amount = isnull((select top 1 round(basic_amt/bottle_qty,2) from puritem where bottle_qty > 0 and basic_amt > 0 and puritem.item_code = tabreportparameters.item_code and "+m_purdate +"<='"+Query_date+"' order by doc_dt desc),amount) where tab_code = "+ m_TAB_CODE +"");
                                ps1.executeUpdate();
                            }
                        }
                        else{
                            ps1 = con.prepareStatement("update tabreportparameters set amount = (select mrp from countersaleitem a where a.doc_no in(select max(doc_no) from countersaleitem where item_code=a.item_code and doc_dt in(select max(doc_dt) from countersaleitem where item_code=a.item_code and doc_dt <='"+Query_date+"')) and doc_dt in(select max(doc_dt) from countersaleitem where item_code=a.item_code and doc_dt <='"+Query_date+"') and a.item_code=tabreportparameters.item_code union select mrp from itemmast where item_code=tabreportparameters.item_code and item_code not in(select item_code from countersaleitem where doc_dt <='"+Query_date+"')) where TAB_CODE="+m_TAB_CODE+"");
                            ps1 = con.prepareStatement("update tabreportparameters set amount = (select mrp from countersaleitem a where a.doc_no in(select max(doc_no) from countersaleitem where item_code=a.item_code and doc_dt in(select max(doc_dt) from countersaleitem where item_code=a.item_code and doc_dt <='"+Query_date+"')) and doc_dt in(select max(doc_dt) from countersaleitem where item_code=a.item_code and doc_dt <='"+Query_date+"') and a.item_code=tabreportparameters.item_code union select mrp from itemmast where item_code=tabreportparameters.item_code and item_code not in(select item_code from countersaleitem where doc_dt <='"+Query_date+"')) where TAB_CODE="+m_TAB_CODE+"");
                            ps1.executeUpdate();
                        }
                        //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e)
                {
                    Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
                }

                //progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        if (radio_summary_wise.isChecked() == true) {
                            Intent i = new Intent(getActivity(), Check_Summary_Report.class);
                            i.putExtra("cdate", edt_date.getText().toString());
                            startActivity(i);
                        }
                       else {
                            if (swap_arryList.size() == 0) {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                                builder.setTitle("Alert");
                                builder.setIcon(R.drawable.warn);
                                builder.setMessage("Please Click At Least One Checkbox");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                builder.show();
                            } else {
                                //-------------------Checkbox-------------------
                                String msg = "";
                                if (chk_stk_all_loc.isChecked()) {
                                    msg = msg + "stok_for_all_location ";
                                    str_stk_all_loc = "1";
                                } else {
                                    str_stk_all_loc = "0";
                                }
                                if (chk_all.isChecked())
                                    msg = msg + "all ";
                                str_all = "1";

                                //=================Liqr_Code======================
                                M_liqr_code = "";
                                for (HashMap<String, String> map1 : swap_arryList)
                                    for (Map.Entry<String, String> mapEntry : map1.entrySet()) {
                                        if (mapEntry.getKey().equals("liqr_code")) {
                                            check_id = mapEntry.getValue();
                                            if (M_liqr_code.length() == 0) {
                                                M_liqr_code = check_id;
                                            } else {
                                                M_liqr_code = M_liqr_code + ',' + check_id;
                                            }
                                        }
                                    }
                                //=================================================
                                {
                                    if (radio_group_wise_stock.isChecked() == true) {
                                        Report_Group_Wise_Stock();
                                    } else {
                                        Report_Trader_Wise_Stock();
                                    }

                                }
                            }
                        }
                     pd.dismiss();
                        //   progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 2000);

            }
        });
    }

    public void load_data() {
        progressDoalog = new ProgressDialog(getActivity());
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        try {

            pbbar.setVisibility(View.VISIBLE);
            bill_arryList.clear();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {

                if(isNullOrEmpty(q))
                {
                    q = "select distinct(select case when liqr_code = 17 then 999 else liqr_code end from itemmast where item_code=tabreportparameters.item_code)as liqr_code,(select liqr_desc from liqrmast where liqr_code in(select liqr_code from itemmast where item_code=tabreportparameters.item_code))as liqr_desc from tabreportparameters where TAB_CODE = "+m_TAB_CODE+" and item_code <> '' order by liqr_desc";
                }
                Log.d("final query_____",q);
                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("liqr_code", rs.getString(1));
                    map.put("liqr_desc", rs.getString(2));

                    //-------------------------------------------
                    bill_arryList.add(map);
                }
            }
            pbbar.setVisibility(View.GONE);
            progressDoalog.dismiss();
            Log.d("bill_arryList_Data", "" + bill_arryList.toString());
            if (bill_recyclerAdapter != null) {
                bill_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + bill_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "308" + e, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
    }

    public class tbill_recyclerAdapter extends RecyclerView.Adapter<tbill_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public tbill_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swap_table, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {
            // holder.contact_list_id.setText(attendance_list.get(position).get("A"));
            holder.name.setText(attendance_list.get(position).get("liqr_desc"));
            // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    smap= new HashMap<String, String>();
                    smap.put("liqr_code",attendance_list.get(position).get("liqr_code"));
                    smap.put("liqr_desc",attendance_list.get(position).get("liqr_desc"));

                    swap_arryList.add(smap);
                    swap_recyclerAdapter.notifyDataSetChanged();

                    // attendance_list.remove(position);
                    bill_arryList.remove(position);
                    bill_recyclerAdapter.notifyDataSetChanged();


                }
            });

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView action;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                // this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.action = (ImageView) itemView.findViewById(R.id.imp_swap);

            }
        }
    }

    public class tswap_recyclerAdapter extends RecyclerView.Adapter<tswap_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public tswap_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swap_table_list, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            holder.name.setText(attendance_list.get(position).get("liqr_desc"));
            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    map= new HashMap<String, String>();
                    map.put("liqr_code",attendance_list.get(position).get("liqr_code"));
                    map.put("liqr_desc",attendance_list.get(position).get("liqr_desc"));

                    bill_arryList.add(map);
                    bill_recyclerAdapter.notifyDataSetChanged();

                    //attendance_list.remove(position);
                    swap_arryList.remove(position);
                    swap_recyclerAdapter.notifyDataSetChanged();

                }

            });

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            ImageView action;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.name = (TextView) itemView.findViewById(R.id.list_d1);
                this.action = (ImageView) itemView.findViewById(R.id.imp_swap);

            }
        }
    }

    public void insert_data(String date)
    {
        //===========ALL==============================
        try
        {
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {

                ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters (item_code,TAB_CODE,liqr_code) select item_code,TAB_CODE = "+m_TAB_CODE+",case when liqr_code = 17 then 999 else liqr_code end from itemmast where item_code > '0'");
                ps1.executeUpdate();
                //=======================CheckBox Condition===========================
                if(str_stk_all_loc.equals("1"))
                {
                    if(str_all.equals("1"))
                    {
                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = isnull((select sum(op_balance+op_balance_b) from onlnstok where item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(qty+breakage_qty) from countersaleitem where doc_dt <='"+date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(qty+breakage_qty) from provisionalsaleitem where doc_dt <='"+date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(qty) from countersalereturnitem where doc_dt <='"+date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(bottle_qty+free_qty) from puritem where "+m_purdate+" <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(bottle_qty) from chalanitem where doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(quantity) from purchasereturnitem where "+m_purdate+" <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("delete from tabreportparameters where gl_clbal =0 and TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();
                    }
                    else
                     {

                    ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = isnull((select sum(op_balance) from onlnstok where item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                    ps1.executeUpdate();

                    ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(qty+breakage_qty) from countersaleitem where sale_type = 0 and doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                    ps1.executeUpdate();

                    ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(qty+breakage_qty) from provisionalsaleitem where sale_type = 0 and doc_dt <='" +date+"' and  item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                    ps1.executeUpdate();

                    ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(qty) from countersalereturnitem where sale_type=0 and doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                    ps1.executeUpdate();

                    ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(bottle_qty+free_qty) from puritem where "+m_purdate+" <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                    ps1.executeUpdate();

                    ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(quantity) from purchasereturnitem where "+m_purdate+" <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                    ps1.executeUpdate();

                    ps1 = con.prepareStatement("delete from tabreportparameters where gl_clbal =0 and TAB_CODE = "+m_TAB_CODE+"");
                    ps1.executeUpdate();

                    ps1 = con.prepareStatement("delete from tabreportparameters where item_code in(select item_code from itemmast where brnd_code in(select brnd_code from brndmast where mainliqrhead_code = 7)) and TAB_CODE = "+m_TAB_CODE+"");
                    ps1.executeUpdate();
                }
            }
                else
            {
                    if(str_all.equals("1"))
                    {
                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = isnull((select sum(op_balance+op_balance_b) from onlnstok where loct_code = "+m_loct_code+" and  item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(qty+breakage_qty) from countersaleitem where loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and  item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(qty+breakage_qty) from provisionalsaleitem where loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(bottle_qty) from transfernote where from_loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(qty) from countersalereturnitem where loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and  item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(bottle_qty+free_qty) from puritem where loct_code = "+m_loct_code+" and "+m_purdate+" <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(bottle_qty) from chalanitem where loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(bottle_qty) from transfernote where to_loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(quantity) from purchasereturnitem where "+m_purdate+" <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("delete from tabreportparameters where gl_clbal =0 and TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();
                    }
                    else
                    {
                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = isnull((select sum(op_balance) from onlnstok where loct_code = "+m_loct_code+" and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(qty+breakage_qty) from countersaleitem where sale_type=0 and loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(qty+breakage_qty) from provisionalsaleitem where sale_type=0 and loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(bottle_qty) from transfernote where from_loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and  item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(qty) from countersalereturnitem where sale_type=0 and loct_code = "+m_loct_code+" and doc_dt <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(bottle_qty+free_qty) from puritem where loct_code = "+m_loct_code+" and "+m_purdate+" <='"+date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal + isnull((select sum(bottle_qty) from transfernote where to_loct_code = "+m_loct_code+" and doc_dt <='"+date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_clbal - isnull((select sum(quantity) from purchasereturnitem where "+m_purdate+" <='" +date+"' and item_code=tabreportparameters.item_code),0) where TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("delete from tabreportparameters where gl_clbal =0 and TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();

                        ps1 = con.prepareStatement("delete from tabreportparameters where item_code in(select item_code from itemmast where brnd_code in(select brnd_code from brndmast where mainliqrhead_code = 7)) and TAB_CODE = "+m_TAB_CODE+"");
                        ps1.executeUpdate();
                    }

                }

                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal=gl_clbal where TAB_CODE = "+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update tabreportparameters set ac_head_id = (select trader_ac_head_id from itemmast,brndmast where itemmast.item_code = tabreportparameters.item_code and itemmast.brnd_code = brndmast.brnd_code) where TAB_CODE = "+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update tabreportparameters set brnd_code = (select mainliqrhead_code from itemmast,brndmast where item_code = tabreportparameters.item_code and itemmast.brnd_code = brndmast.brnd_code) where TAB_CODE = "+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update tabreportparameters set brnd_code = 2 where liqr_code = 57 and TAB_CODE = "+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update tabreportparameters set MAIN_LIQR_DESC = (select mainliqrhead_desc from mainliquorheadmast where mainliqrhead_code = tabreportparameters.brnd_code) where TAB_CODE = "+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update tabreportparameters set MAIN_LIQR_DESC = 'COUNTRY', brnd_code = 999 where liqr_code=999 and TAB_CODE = "+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update tabreportparameters set tot_amount = (select convert(money,(LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,(gl_clbal/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))) + '.' + LTRIM(STR(gl_clbal-((SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE))* CONVERT(INT,(CONVERT(FLOAT,(gl_clbal/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))))))) where tab_code="+m_TAB_CODE+" and gl_clbal > 0");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("update tabreportparameters set qty_in_cases = (select LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,(gl_clbal/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))) + '.' + LTRIM(STR(gl_clbal-((SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE))* CONVERT(INT,(CONVERT(FLOAT,(gl_clbal/(SELECT CASE_QTY FROM SIZEMAST WHERE SIZE_CODE IN(SELECT SIZE_CODE FROM ITEMMAST WHERE ITEM_CODE=tabreportparameters.ITEM_CODE)))))))))) where tab_code="+m_TAB_CODE+" and gl_clbal > 0");
                ps1.executeUpdate();

                ps1 = con.prepareStatement(" update tabreportparameters set gl_desc = (select case when tabreportparameters.ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end from glmast where ac_head_id=tabreportparameters.ac_head_id and tab_code="+m_TAB_CODE+") where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();

                // Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

            }
            progressDoalog.dismiss();

        }catch(Exception e)
        {
            Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
        }
    }

    //==========================================
    public void Report_Group_Wise_Stock()
    {
        try {

            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                ps1 = con.prepareStatement("update tabreportparameters set doc_type = isnull((select liqr_desc from liqrmast where liqr_code=tabreportparameters.liqr_code),'') where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set doc_type = 'COUNTRY' where liqr_code = 999 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement(" insert into tabreportparameters(doc_type,tab_code,liqr_code) select distinct doc_type,"+m_TAB_CODE+",liqr_code from tabreportparameters where gl_clbal <> 0 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement(" insert into tabreportparameters(doc_type,tab_code,liqr_code) select distinct 'GROUP TOTAL',"+m_TAB_CODE+",liqr_code from tabreportparameters where gl_clbal <> 0 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement(" update tabreportparameters set amount_1 = isnull((select sum(gl_clbal*amount) from tabreportparameters a where a.liqr_code=tabreportparameters.liqr_code and a.tab_code=tabreportparameters.tab_code),0) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("  update tabreportparameters set gl_clbal = isnull((select sum(gl_clbal) from tabreportparameters a where a.liqr_code=tabreportparameters.liqr_code and a.tab_code=tabreportparameters.tab_code),0) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("  update tabreportparameters set tot_amount = isnull((select sum(tot_amount) from tabreportparameters a where a.liqr_code=tabreportparameters.liqr_code and a.tab_code=tabreportparameters.tab_code),0) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
                ps1.executeUpdate();

                Intent i = new Intent(getActivity(), Closing_Stock_Report.class);
                i.putExtra("checklist", M_liqr_code);
                i.putExtra("str_price_type_title", str_price_type_title);
                i.putExtra("m_pricetype", str_cost_evaluation);
                i.putExtra("cdate", edt_date.getText().toString());
                i.putExtra("Query_date", Query_date);
                i.putExtra("m_purdate", m_purdate);
                i.putExtra("stock_wise_radioButton", "Group");
                   try {
                      if (radio_bottle.isChecked() == true) {
                          i.putExtra("stock_in_radioButton", "Bottles");
                          } else {
                          i.putExtra("stock_in_radioButton", "Cases");
                                 }
                         } catch (Exception e) {  }
                          startActivity(i);

            }

        } catch (Exception e) { }
    }
    public void Report_Trader_Wise_Stock()
    {
        try {

            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,tab_code,ac_head_id) select distinct gl_desc,"+m_TAB_CODE+",ac_head_id from tabreportparameters where gl_clbal <> 0 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_type,gl_desc,tab_code,ac_head_id) select distinct 'GROUP TOTAL',gl_desc,"+m_TAB_CODE+",ac_head_id from tabreportparameters where gl_clbal <> 0 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement(" update tabreportparameters set amount_1 = (select sum(gl_clbal*amount) from tabreportparameters a where a.gl_desc=tabreportparameters.gl_desc and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement(" update tabreportparameters set gl_clbal = (select sum(gl_clbal) from tabreportparameters a where a.gl_desc=tabreportparameters.gl_desc and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement(" update tabreportparameters set tot_amount = (select sum(tot_amount) from tabreportparameters a where a.gl_desc=tabreportparameters.gl_desc and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
                ps1.executeUpdate();
                Intent i = new Intent(getActivity(), Closing_Stock_Report.class);
                i.putExtra("checklist", M_liqr_code);
                i.putExtra("str_price_type_title", str_price_type_title);
                i.putExtra("m_pricetype", str_cost_evaluation);
                i.putExtra("cdate", edt_date.getText().toString());
                i.putExtra("Query_date", Query_date);
                i.putExtra("chksummary", str_summary);
                i.putExtra("m_purdate", m_purdate);
                i.putExtra("stock_wise_radioButton", "Trade");
                try {
                    if (radio_bottle.isChecked() == true) {
                        i.putExtra("stock_in_radioButton", "Bottles");
                    } else {
                        i.putExtra("stock_in_radioButton", "Cases");
                    }

                } catch (Exception e) {
                }
                startActivity(i);

            }

        } catch (Exception e) { }
    }
    //=====================================
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
                    String query = "select loct_code,loct_desc from loctmast where loct_code in(select loct_code from onlnstok)";
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
            sp_counter_godown.setAdapter(spnr_data);
            sp_counter_godown.setSelection(0);
            sp_counter_godown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    m_loct_desc = (String) obj.get("A");
                    m_loct_code = (String) obj.get("B");
                  //  Toast.makeText(getActivity(), "loct_code: "+m_loct_code, Toast.LENGTH_SHORT).show();
                    Log.d("m_loct_code",m_loct_code);
                    bill_arryList.clear();
                    bill_recyclerAdapter.notifyDataSetChanged();
                    swap_arryList.clear();
                    swap_recyclerAdapter.notifyDataSetChanged();
                    btn_uncheck_all.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
        }
    }

    public class load_spinner_Rate_type extends AsyncTask<String, String, String> {
        List<Map<String, String>> sp_data = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                con = CONN(con_ipaddress,portnumber,db);
                if (con == null) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

                } else {
                    //String query="select size_code,size_desc from sizemast";
                    String query = "SELECT 'MRP' AS CODE , 'M.R.P' AS DISC,0 AS SEQ UNION SELECT 'PURCHASE_PRICE' AS CODE , 'PURCHASE PRICE' AS DISC,1 AS SEQ UNION SELECT 'SALE_PRICE' AS CODE , 'COUNTER SALE PRICE' AS DISC ,2 AS SEQ UNION SELECT 'MINIMUM_PRICE' AS CODE , 'WHOLE SALE PRICE' AS DISC, 3 AS SEQ UNION SELECT 'CASHMEMO_PRICE' AS CODE , 'CASH MEMO PRICE' AS DISC, 4 AS SEQ  ORDER BY SEQ ";
                    PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery();

                    //ArrayList data1 = new ArrayList();
                    while (rs.next()) {
                        Map<String, String> data = new HashMap<String, String>();
                        data.put("B", rs.getString(1));
                        data.put("A", rs.getString(2));

                        sp_data.add(data);

                    }

                }  //z = "Success";


            } catch (Exception e) {
           //     Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            // progressDoalog.dismiss();
            String[] from = {"A", "B"};
            int[] views = {R.id.list_d1};

            final SimpleAdapter spnr_data = new SimpleAdapter(getActivity(), sp_data, R.layout.spin, from, views);
            sp_cost_evaluation.setAdapter(spnr_data);
            sp_cost_evaluation.setSelection(0);
            sp_cost_evaluation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) spnr_data.getItem(i);
                    M_ratetype_desc= (String) obj.get("A");
                    M_ratetype_code = (String) obj.get("B");
                    str_cost_evaluation=M_ratetype_code;
                    str_price_type_title=M_ratetype_desc;
                    //Toast.makeText(getActivity(), "loct_code: "+M_ratetype_code, Toast.LENGTH_SHORT).show();
                    Log.d("m_loct_code",M_ratetype_code);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            super.onPostExecute(s);
        }
    }

    public Connection CONN(String ip,String port,String db) {

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