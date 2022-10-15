package com.example.admin_beerbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
public class Date_Brand_Wise_Sale_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date;
    int m_TAB_CODE;
    String con_ipaddress ,portnumber, str_month="",str_day="",str_sp_type;
    ProgressBar pbbar;
    ProgressDialog progressDoalog;
    TransparentProgressDialog pd;
    Button btn_proceed,btn_report,btn_check_all,btn_uncheck_all;
    String IMEINumber;
    int m_compcode;
    Date date;
    Spinner sp_type;
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
   //  String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    ProgressBar progressBar;
    PreparedStatement ps1;
    String Temp_frm_date,Temp_to_date,formattedDate,db;
    CheckBox chk_adj_sdk,chk_brand_name,chk_size;
    String str_chk_adj_sdk,str_chk_brand_name,str_chk_size;
    Date d;
    int m_from_code;
    TextView txt_chk_adj_sdk;
    RadioGroup radiowiseGroup;
    String radio_val="";
    int m_index_position=0;
    String m_rptyn = "N";
    String m_oldcat = "";
    String q="";
    int n_grd_tot = 0,n_qtytot = 0,m_srno = 0,m_qtytot=0,m_grd_tot=0,n_srno=0;
    RadioButton rb,radio_brand_detils,radio_size_summary,radio_trade_wise;
    String str_radio_brand_detils,str_radio_size_summary,str_radio_trade_wise;
    String check_id="",M_liqr_code="";
    LinearLayout lin_hide;
    public Date_Brand_Wise_Sale_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_brand_wise_sale, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
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
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        pbbar = (ProgressBar) view.findViewById(R.id.pgb);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar_cyclic);

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
                btn_check_all.setVisibility(View.INVISIBLE);
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                lin_hide.setVisibility(View.INVISIBLE);
                chk_adj_sdk.setEnabled(true);
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
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                btn_check_all.setVisibility(View.INVISIBLE);
                lin_hide.setVisibility(View.INVISIBLE);
                chk_adj_sdk.setEnabled(true);
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

        //---------------------Recyclerview 2-----------------------------------------
        bill_arryList = new ArrayList<HashMap<String, String>>();
        recycler_bill_list = (RecyclerView) view.findViewById(R.id.recycler_bill_list);
        layoutManager_bill = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_bill_list.setLayoutManager(layoutManager_bill);
        bill_recyclerAdapter = new tbill_recyclerAdapter(getActivity(), bill_arryList);
        recycler_bill_list.setAdapter(bill_recyclerAdapter);

        // txt_tbl_bill = (TextView) findViewById(R.id.txt_tbl_bill);
        //------------------------------------------------------------------------------------------
        //---------------------Recyclerview swap-----------------------------------------
        swap_arryList = new ArrayList<HashMap<String, String>>();
        recycler_swap_list = (RecyclerView)view. findViewById(R.id.recycler_swap_list);
        layoutManager_swap = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_swap_list.setLayoutManager(layoutManager_swap);
        swap_recyclerAdapter=new tswap_recyclerAdapter(getActivity(),swap_arryList);
        recycler_swap_list.setAdapter(swap_recyclerAdapter);

        lin_hide=(LinearLayout) view.findViewById(R.id.lin_hide);
        radio_brand_detils=(RadioButton) view.findViewById(R.id.radio_brand_detils);
        radio_brand_detils.setChecked(true);
        radio_size_summary=(RadioButton) view.findViewById(R.id.radio_size_summary);
        radio_trade_wise=(RadioButton) view.findViewById(R.id.radio_trade_wise);
        radiowiseGroup=(RadioGroup) view.findViewById(R.id.radiowiseGroup);
        chk_adj_sdk=(CheckBox)view.findViewById(R.id.chk_adj_sdk);
        chk_brand_name=(CheckBox)view.findViewById(R.id.chk_brand_name);
        chk_brand_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chk_brand_name.setChecked(true);
                chk_size.setChecked(false);
            }
        });
        chk_size=(CheckBox)view.findViewById(R.id.chk_size);
        chk_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chk_brand_name.setChecked(false);
                chk_size.setChecked(true);
            }
        });
        chk_size.setChecked(true);
        txt_chk_adj_sdk=(TextView) view.findViewById(R.id.txt_chk_adj_sdk);

        hide_adj_sdk();
        sp_type=(Spinner)view.findViewById(R.id.sp_type);
        List<String> list = new ArrayList<String>();
        list.add("Retail");
        list.add("Whole Sale");
        list.add("Retail + Whole Sale");
        if(m_from_code>0) {
            list.add("Retail + Whole Sale + Adjust");
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        sp_type.setAdapter(dataAdapter);
        sp_type.setSelection(0);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                btn_check_all.setVisibility(View.INVISIBLE);
                lin_hide.setVisibility(View.INVISIBLE);
                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();

                m_index_position=position;
                Log.d("m_index_position",""+m_index_position);
                str_sp_type = adapterView.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

     //-------------------------------------------------

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
                            if (rb.getText().toString().equals("Brand Detils")) {
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                               // radio_val="Group_stock";
                                radio_val="";
                                q="select distinct liqr_code,DOC_TYPE from tabreportparameters where TAB_CODE="+m_TAB_CODE+"  and item_code <> '' order by liqr_code";
                            } else if (rb.getText().toString().equals("Traderwise")) {
                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="trade";
                                q = "select ac_head_id as liqr_code,case when ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end as doc_type from glmast where ac_head_id in(select ac_head_id from tabreportparameters where TAB_CODE="+m_TAB_CODE+" and item_code <> '') order by gl_desc";
                            } else {

                                bill_arryList.clear();
                                bill_recyclerAdapter.notifyDataSetChanged();
                                swap_arryList.clear();
                                swap_recyclerAdapter.notifyDataSetChanged();
                                radio_val="";
                                q="select distinct liqr_code,DOC_TYPE from tabreportparameters where TAB_CODE="+m_TAB_CODE+" and item_code <> '' order by liqr_code";
                            }
                        }catch (Exception e)
                        {
                            q="select distinct liqr_code,DOC_TYPE from tabreportparameters where TAB_CODE="+m_TAB_CODE+" and item_code <> ''  order by liqr_code";
                        }

                        insert_data(Temp_frm_date, Temp_to_date);
                        load_data();

                        pd.dismiss();
                        // progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 2000);

            }
        });

        //-----------------------------------------------
        btn_proceed=(Button)view.findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // progressBar.setVisibility(View.VISIBLE);

                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        q="";
                        radio_brand_detils.setChecked(true);
                        radio_size_summary.setChecked(false);
                        radio_trade_wise.setChecked(false);
                        btn_check_all.setVisibility(View.VISIBLE);
                        lin_hide.setVisibility(View.VISIBLE);
                        chk_adj_sdk.setEnabled(false);
                        if (chk_adj_sdk.isChecked()) {

                            str_chk_adj_sdk = "1";
                        } else {
                            str_chk_adj_sdk = "0";
                        }
                        if (chk_brand_name.isChecked()) {
                            str_chk_brand_name = "1";
                        } else {
                            str_chk_brand_name = "0";
                        }
                        if (chk_size.isChecked()) {
                            str_chk_size = "1";
                        } else {
                            str_chk_size = "0";
                        }

                        if (radio_brand_detils.isChecked() == true) {
                            str_radio_brand_detils="1";
                        } else {
                            str_radio_brand_detils="0";
                        }
                        if (radio_size_summary.isChecked() == true) {
                            str_radio_size_summary="1";
                        } else {
                            str_radio_size_summary="0";
                        }
                        if (radio_trade_wise.isChecked() == true) {
                            str_radio_trade_wise="1";
                        } else {
                            str_radio_trade_wise="0";
                        }

                        insert_data(Temp_frm_date, Temp_to_date);
                        load_data();
                        // btn_uncheck_all.setVisibility(View.VISIBLE);
                        btn_check_all.setVisibility(View.VISIBLE);
                       // progressBar.setVisibility(View.INVISIBLE);
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
                        pd.dismiss();
                    }
                }, 3000);

    }
});

        //------------------------------------------------------------------------------------------
        btn_uncheck_all=(Button)view.findViewById(R.id.btn_uncheck_all);
        btn_check_all=(Button)view.findViewById(R.id.btn_check_all);
        btn_check_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_uncheck_all.setVisibility(View.VISIBLE);
                btn_check_all.setVisibility(View.INVISIBLE);

                    bill_arryList.clear();
                    bill_recyclerAdapter.notifyDataSetChanged();
                    swap_arryList.clear();
                    swap_recyclerAdapter.notifyDataSetChanged();
                    try {
                        con = CONN(con_ipaddress, portnumber,db);
                        if(isNullOrEmpty(q))
                        {
                            q="select distinct liqr_code,DOC_TYPE from tabreportparameters where TAB_CODE="+m_TAB_CODE+" order by liqr_code";
                        }
                        PreparedStatement ps = con.prepareStatement(q);
                            ResultSet rs = ps.executeQuery();

                            while (rs.next()) {
                                smap = new HashMap<String, String>();
                                smap.put("liqr_code", rs.getString(1));
                                smap.put("DOC_TYPE", rs.getString(2));
                                //-------------------------------------------
                                swap_arryList.add(smap);

                            }

                    }
                    catch (NullPointerException e)
                    {
                        Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();
                    }
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
                     con = CONN(con_ipaddress, portnumber,db);
                    if(isNullOrEmpty(q))
                    {
                        q="select distinct liqr_code,DOC_TYPE from tabreportparameters where TAB_CODE="+m_TAB_CODE+" order by liqr_code";
                    }
                        PreparedStatement ps = con.prepareStatement(q);
                        ResultSet rs = ps.executeQuery();

                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {
                            map = new HashMap<String, String>();
                            map.put("liqr_code", rs.getString(1));
                            map.put("DOC_TYPE", rs.getString(2));
                            //-------------------------------------------
                            bill_arryList.add(map);

                        }
                }
                catch (NullPointerException e) {
                    Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){ Toast.makeText(getActivity(), "Error"+e, Toast.LENGTH_SHORT).show();}
            }
        });
        //-----------------------------------------------------------------------------------
        btn_report=(Button)view.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // progressBar.setVisibility(View.VISIBLE);
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if(swap_arryList.size()==0)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
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
                        }
                        else {
                            if (chk_adj_sdk.isChecked()) {
                                str_chk_adj_sdk = "1";
                            } else {
                                str_chk_adj_sdk = "0";
                            }
                            if (chk_brand_name.isChecked()) {
                                str_chk_brand_name = "1";
                            } else {
                                str_chk_brand_name = "0";
                            }
                            if (chk_size.isChecked()) {
                                str_chk_size = "1";
                            } else {
                                str_chk_size = "0";
                            }

                            if (radio_brand_detils.isChecked() == true) {
                                str_radio_brand_detils = "1";
                            } else {
                                str_radio_brand_detils = "0";
                            }
                            if (radio_size_summary.isChecked() == true) {
                                str_radio_size_summary = "1";
                            } else {
                                str_radio_size_summary = "0";
                            }
                            if (radio_trade_wise.isChecked() == true) {
                                str_radio_trade_wise = "1";
                            } else {
                                str_radio_trade_wise = "0";
                            }

                        //=================Liqr_Code======================
                        M_liqr_code="";
                        for (HashMap<String, String> map1 :swap_arryList )
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

                        // If OptDetails.Value = True Then Reportdetails
                       if(str_radio_brand_detils.equals("1"))
                       {
                           Reportdetails();
                       }
                       // If OptTrader.Value = True Then ReportTrader
                        if(str_radio_trade_wise.equals("1"))
                        {
                            ReportTrader();
                        }
                       // If OptSize.Value = True Then SizeSummary
                        if(str_radio_size_summary.equals("1"))
                        {
                            SizeSummary();
                        }

                        }
                        pd.dismiss();
                      //  progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 1000);

            }
        });

    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.trim().isEmpty())
            return false;
        return true;
    }

    public void load_data() {

        progressDoalog = new ProgressDialog(getActivity());
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        try {
            progressDoalog.dismiss();
            pbbar.setVisibility(View.VISIBLE);
            bill_arryList.clear();
            con = CONN(con_ipaddress,portnumber,db);

            if(isNullOrEmpty(q))
            {
                q="select distinct liqr_code,DOC_TYPE from tabreportparameters where TAB_CODE="+m_TAB_CODE+" and item_code <> '' order by liqr_code";
            }
            PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("liqr_code", rs.getString(1));
                    map.put("DOC_TYPE", rs.getString(2));

                    //-------------------------------------------
                    bill_arryList.add(map);
                }


            pbbar.setVisibility(View.GONE);
            Log.d("bill_arryList_Data", "" + bill_arryList.toString());
            if (bill_recyclerAdapter != null) {
                bill_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + bill_recyclerAdapter.toString());
            }

        }
        catch (NullPointerException e)
        {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(getActivity(), "628" + e, Toast.LENGTH_SHORT).show();
        }

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
            holder.name.setText(attendance_list.get(position).get("DOC_TYPE"));
           // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    smap= new HashMap<String, String>();
                    smap.put("liqr_code",attendance_list.get(position).get("liqr_code"));
                    smap.put("DOC_TYPE",attendance_list.get(position).get("DOC_TYPE"));

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

            holder.name.setText(attendance_list.get(position).get("DOC_TYPE"));
           // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    map= new HashMap<String, String>();
                    map.put("liqr_code",attendance_list.get(position).get("liqr_code"));
                    map.put("DOC_TYPE",attendance_list.get(position).get("DOC_TYPE"));

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
            TextView name, qty,  lsize,time;
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
    public void insert_data(String frm_date,String to_date)
    {
        //===========ALL==============================
        try
        {
            con = CONN(con_ipaddress,portnumber,db);
            ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();
            if(str_chk_adj_sdk.equals("0")) {
                if (m_index_position == 0) {

                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,sum(qty)," + m_TAB_CODE + ",sum(item_value) from countersaleitem where stock_adj_yn=0 and WHOLESALE_DOC_NO = 0 and doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,sum(qty)," + m_TAB_CODE + ",sum(item_value) from provisionalsaleitem where WHOLESALE_DOC_NO = 0 and doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,-sum(qty)," + m_TAB_CODE + ",-sum(item_value) from countersalereturnitem where stock_adj_yn=0 and doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                } else if (m_index_position == 1) {
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,sum(qty)," + m_TAB_CODE + ",sum(item_value) from countersaleitem where stock_adj_yn=0 and WHOLESALE_DOC_NO <> 0 and doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,sum(qty)," + m_TAB_CODE + ",sum(item_value) from provisionalsaleitem where WHOLESALE_DOC_NO <> 0 and doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();

                } else if (m_index_position == 2) {
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,sum(qty)," + m_TAB_CODE + ",sum(item_value) from countersaleitem where stock_adj_yn=0  and doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,sum(qty)," + m_TAB_CODE + ",sum(item_value) from provisionalsaleitem where  doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,-sum(qty)," + m_TAB_CODE + ",-sum(item_value) from countersalereturnitem where stock_adj_yn=0 and doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                } else {
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,sum(qty)," + m_TAB_CODE + ",sum(item_value) from countersaleitem where  doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,sum(qty)," + m_TAB_CODE + ",sum(item_value) from provisionalsaleitem where  doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,-sum(qty)," + m_TAB_CODE + ",-sum(item_value) from countersalereturnitem where  doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                    ps1.executeUpdate();
                }
            }
            else
            {
                ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,sum(qty),"+ m_TAB_CODE +",sum(item_value) from countersaleitem where stock_adj_yn=1 and doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount) select item_code,-sum(qty),"+m_TAB_CODE +",-sum(item_value) from countersalereturnitem where stock_adj_yn=1 and doc_dt between '" + frm_date + "' and '" + to_date + "'  and comp_code=" + m_compcode + " group by item_code");
                ps1.executeUpdate();
            }
            ps1 = con.prepareStatement("insert into tabreportparameters (item_code,tot_sale,TAB_CODE,amount,doc_no) select item_code,sum(tot_sale),"+m_TAB_CODE +",sum(amount),1 from tabreportparameters where TAB_CODE = "+m_TAB_CODE+" group by item_code ");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("delete from tabreportparameters where (doc_no = 0 or tot_sale = 0) AND TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set AMOUNT_1 = round(amount/tot_sale,2) where TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set brnd_code = (select brnd_code from itemmast where item_code = tabreportparameters.item_code) where TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set ac_head_id = (select trader_ac_head_id from brndmast where brnd_code = tabreportparameters.brnd_code) where TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set liqr_code = (select mainliqrhead_code from brndmast where brnd_code = tabreportparameters.brnd_code) where TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();

            ps1 = con.prepareStatement("update tabreportparameters set DOC_TYPE =  'IMFL',liqr_code=1 where TAB_CODE ="+m_TAB_CODE+" and liqr_code in(1,2)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set DOC_TYPE =  'WINE' where TAB_CODE ="+m_TAB_CODE+" and liqr_code in(3,4)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set DOC_TYPE =  'STRONG BEER' where TAB_CODE ="+m_TAB_CODE+" and liqr_code in(5)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set DOC_TYPE =  'MILD BEER' where TAB_CODE ="+m_TAB_CODE+" and liqr_code in(6)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set DOC_TYPE =  'COLD DRINKS & OTHERS' where TAB_CODE ="+m_TAB_CODE+" and liqr_code in(7)");
            ps1.executeUpdate();


            ps1 = con.prepareStatement("update tabreportparameters set liqr_code=17, DOC_TYPE =  'COUNTRY' where TAB_CODE ="+m_TAB_CODE+" and item_code in(select item_code from itemmast where liqr_code = 17)");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set AMOUNT_2 = (select club_size_code from itemmast where item_code=tabreportparameters.item_code) where TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();

               // Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

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
    public void hide_adj_sdk()
    {
        try {

            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
                String q="SELECT FORM_CODE FROM USERRIGHTS WHERE FORM_CODE=241";

                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();
                int cnt=0;
                while(rs.next())
                {cnt++;
                    m_from_code=rs.getInt("FORM_CODE");
                    Log.d("m_from_code",""+m_from_code);
                }
                if(cnt>0)
                {
                    chk_adj_sdk.setVisibility(View.VISIBLE);
                    txt_chk_adj_sdk.setVisibility(View.VISIBLE);
                }
                else
                {
                    chk_adj_sdk.setVisibility(View.INVISIBLE);
                    txt_chk_adj_sdk.setVisibility(View.INVISIBLE);
                }

            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
        }
    }
    public void Reportdetails()
    {
        try {

        con = CONN(con_ipaddress,portnumber,db);
        if (con == null) {
            Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

        } else {

            ps1 = con.prepareStatement("delete from tabreportparameters where item_code='' and tab_code = "+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters(doc_type,tab_code,liqr_code) select distinct doc_type,"+m_TAB_CODE+",liqr_code from tabreportparameters where tot_sale <> 0 and tab_code="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters(doc_type,tab_code,liqr_code) select distinct 'GROUP TOTAL',"+m_TAB_CODE+",liqr_code from tabreportparameters where tot_sale <> 0 and tab_code="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set amount = (select sum(amount) from tabreportparameters a where a.liqr_code=tabreportparameters.liqr_code and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set tot_sale = (select sum(tot_sale) from tabreportparameters a where a.liqr_code=tabreportparameters.liqr_code and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
            ps1.executeUpdate();

            Intent i = new Intent(getActivity(), Date_Brand_Wise_Sale_Report.class);
            i.putExtra("checklist", M_liqr_code);
            if(str_chk_adj_sdk.equals("1"))
            {
                i.putExtra("category", "Stock Adjustment");
            }
            else {
                i.putExtra("category", str_sp_type);
            }
            i.putExtra("query_type", "brand");
            i.putExtra("str_chk_brand_name", str_chk_brand_name);
            i.putExtra("str_chk_size", str_chk_size);
            i.putExtra("to_date", edt_to_date.getText().toString());
            i.putExtra("frm_date", edt_frm_date.getText().toString());

            startActivity(i);

        }

    } catch (Exception e) { }
    }
    public void ReportTrader()
    {
        try {

            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

            } else {
               // String q="select (select brnd_desc from brndmast where brnd_code in(select brnd_code from itemmast where item_code=tabreportparameters.item_code))as brnd_desc,tot_sale,(select size_desc from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as size_desc,(select seq_no from sizemast where size_code in(select size_code from itemmast where item_code=tabreportparameters.item_code))as seq_no,str(basic_amt,12,2)as basic_amt,str(amount,12,2)as item_value from tabreportparameters where TAB_CODE="+m_TAB_CODE+" and ac_head_id = \" & Val(Flxtype.TextMatrix(i, 0)) & \"  order by brnd_desc,seq_no";

                ps1 = con.prepareStatement("delete from tabreportparameters where item_code='' and tab_code = "+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_desc = (select case when tabreportparameters.ac_head_id=0 then '*TRADERS NOT ASSIGN TO BRAND' else left(ltrim(gl_desc),50) end from glmast where ac_head_id=tabreportparameters.ac_head_id and tab_code="+m_TAB_CODE+") where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,tab_code,ac_head_id) select distinct gl_desc,"+m_TAB_CODE+",ac_head_id from tabreportparameters where tot_sale <> 0 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_type,gl_desc,tab_code,ac_head_id) select distinct 'GROUP TOTAL',gl_desc,"+m_TAB_CODE+",ac_head_id from tabreportparameters where tot_sale <> 0 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = (select sum(amount) from tabreportparameters a where a.gl_desc=tabreportparameters.gl_desc and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = (select sum(tot_sale) from tabreportparameters a where a.gl_desc=tabreportparameters.gl_desc and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
                ps1.executeUpdate();

                Intent i = new Intent(getActivity(), Date_Brand_Wise_Sale_Report.class);
                i.putExtra("checklist", M_liqr_code);
                if(str_chk_adj_sdk.equals("1"))
                {
                    i.putExtra("category", "Stock Adjustment");
                }
                else {
                    i.putExtra("category", str_sp_type);
                }
                i.putExtra("query_type", "trade");
                i.putExtra("str_chk_brand_name", str_chk_brand_name);
                i.putExtra("str_chk_size", str_chk_size);
                i.putExtra("to_date", edt_to_date.getText().toString());
                i.putExtra("frm_date", edt_frm_date.getText().toString());

                startActivity(i);
            }

        } catch (Exception e) { }
    }
    public void SizeSummary()
    {
        try {

        con = CONN(con_ipaddress,portnumber,db);
        if (con == null) {
            Toast.makeText(getActivity(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();

        } else {
            ps1 = con.prepareStatement("delete from tabreportparameters where item_code='' and tab_code = "+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters(doc_type,tab_code,liqr_code) select distinct doc_type,"+m_TAB_CODE+",liqr_code from tabreportparameters where tot_sale <> 0 and tab_code="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters(doc_type,tab_code,liqr_code) select distinct 'GROUP TOTAL',"+m_TAB_CODE+",liqr_code from tabreportparameters where tot_sale <> 0 and tab_code="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set amount = (select sum(amount) from tabreportparameters a where a.liqr_code=tabreportparameters.liqr_code and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set tot_sale = (select sum(tot_sale) from tabreportparameters a where a.liqr_code=tabreportparameters.liqr_code and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
            ps1.executeUpdate();

            Intent i = new Intent(getActivity(), Date_Rrand_Wise_Sale_Summary_Report.class);
            i.putExtra("to_date", edt_to_date.getText().toString());
            i.putExtra("frm_date", edt_frm_date.getText().toString());
            i.putExtra("category", str_sp_type);

            startActivity(i);



        }

    } catch (Exception e) { }
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
