package com.example.admin_beerbar;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date;
    int m_TAB_CODE;
    String con_ipaddress ,portnumber, str_month="",str_day="";
    ProgressBar pbbar;
    ProgressDialog progressDoalog;
    TransparentProgressDialog pd;
    Button btn_proceed,btn_report,btn_check_all,btn_uncheck_all;
    String IMEINumber,db;
    int m_compcode;
    Date date;
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
    // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    ProgressBar progressBar;
    PreparedStatement ps1;
    String Temp_frm_date,Temp_to_date,formattedDate;
    Date d;
    RadioGroup radioGroup,radiowiseGroup;
    RadioButton radio_bottle,radio_cases;
    LinearLayout lin_radio_hide;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home2, container, false);
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
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        pbbar = (ProgressBar) view.findViewById(R.id.pgb);
        lin_radio_hide = (LinearLayout) view.findViewById(R.id.lin_radio_hide);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar_cyclic);
        radio_bottle=(RadioButton) view.findViewById(R.id.radio_bottle);
        radio_cases=(RadioButton)view.findViewById(R.id.radio_cases);
        radio_bottle.setChecked(true);

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
                lin_radio_hide.setVisibility(View.INVISIBLE);

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
                btn_check_all.setVisibility(View.INVISIBLE);
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                lin_radio_hide.setVisibility(View.INVISIBLE);

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

        btn_proceed=(Button)view.findViewById(R.id.btn_proceed);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_check_all.setVisibility(View.VISIBLE);
                lin_radio_hide.setVisibility(View.VISIBLE);
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        insert_data(Temp_frm_date, Temp_to_date);
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
                        // btn_uncheck_all.setVisibility(View.VISIBLE);
                        btn_check_all.setVisibility(View.VISIBLE);
                       // progressBar.setVisibility(View.INVISIBLE);
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
                            PreparedStatement ps = con.prepareStatement("select liqr_code,liqr_desc from liqrmast where liqr_code in(select distinct liqr_code from tabreportparameters where item_code <> '' and TAB_CODE="+m_TAB_CODE+") order by liqr_desc");
                            ResultSet rs = ps.executeQuery();

                            while (rs.next()) {
                                smap = new HashMap<String, String>();
                                smap.put("liqr_code", rs.getString("liqr_code"));
                                smap.put("liqr_desc", rs.getString("liqr_desc"));
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
                        PreparedStatement ps = con.prepareStatement("select liqr_code,liqr_desc from liqrmast where liqr_code in(select distinct liqr_code from tabreportparameters where item_code <> '' and TAB_CODE="+m_TAB_CODE+") order by liqr_desc");
                        ResultSet rs = ps.executeQuery();

                        //ArrayList data1 = new ArrayList();
                        while (rs.next()) {
                            map = new HashMap<String, String>();
                            map.put("liqr_code", rs.getString("liqr_code"));
                            map.put("liqr_desc", rs.getString("liqr_desc"));
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
                        }
                        else {
                            String check_id = "", M_liqr_code = "";

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
                            Intent i = new Intent(getActivity(), Report.class);
                            i.putExtra("checklist", M_liqr_code);
                            i.putExtra("from_date", edt_frm_date.getText().toString());
                            i.putExtra("to_date", edt_to_date.getText().toString());
                            try {
                                if (radio_bottle.isChecked() == true) {
                                    i.putExtra("stock_in_radioButton", "Bottles");
                                } else {
                                    i.putExtra("stock_in_radioButton", "Cases");
                                }
                            } catch (Exception e) {  }
                            startActivity(i);
                        }
                        pd.dismiss();
                      //  progressBar.setVisibility(View.INVISIBLE);
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
            progressDoalog.dismiss();
            pbbar.setVisibility(View.VISIBLE);
            bill_arryList.clear();
            con = CONN(con_ipaddress,portnumber,db);

                String q="select liqr_code,liqr_desc from liqrmast where liqr_code in(select distinct liqr_code from tabreportparameters where item_code <> '' and TAB_CODE="+m_TAB_CODE+") order by liqr_desc";
                Log.d("final query_____",q);
                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("liqr_code", rs.getString("liqr_code"));
                    map.put("liqr_desc", rs.getString("liqr_desc"));

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
            Toast.makeText(getActivity(), "308" + e, Toast.LENGTH_SHORT).show();
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
           // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

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
            ps1 = con.prepareStatement("insert into tabreportparameters(item_code,TAB_CODE) select item_code,"+m_TAB_CODE+" from itemmast where item_code in(select distinct item_code from countersaleitem where comp_code="+m_compcode+" and doc_dt between '"+frm_date+"' and '"+to_date+"' union select distinct item_code from provisionalsaleitem where comp_code="+m_compcode+" and doc_dt between '"+frm_date+"' and '"+to_date+"' union select distinct item_code from countersalereturnitem where comp_code="+m_compcode+" and doc_dt between '"+frm_date+"' and '"+to_date+"')");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set tot_sale = isnull((select sum(qty) from countersaleitem where countersaleitem.item_code=tabreportparameters.item_code and comp_code="+m_compcode+" and doc_dt between '"+frm_date+"' and '"+to_date+"'),0) where TAB_CODE="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale+isnull((select sum(qty) from provisionalsaleitem where provisionalsaleitem.item_code=tabreportparameters.item_code and comp_code="+m_compcode+" and doc_dt between '"+frm_date+"' and '"+to_date+"'),0) where TAB_CODE="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale-isnull((select sum(qty) from countersalereturnitem where countersalereturnitem.item_code=tabreportparameters.item_code and comp_code="+m_compcode+" and doc_dt between '"+frm_date+"' and '"+to_date+"'),0) where TAB_CODE="+m_TAB_CODE+"");
            ps1.executeUpdate();

            ps1 = con.prepareStatement("update tabreportparameters set liqr_code = (select liqrmast.liqr_code from liqrmast,itemmast where liqrmast.liqr_code=itemmast.liqr_code and tabreportparameters.item_code = itemmast.item_code) where TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set doc_type = (select liqr_desc from liqrmast where tabreportparameters.liqr_code = liqrmast.liqr_code) where TAB_CODE ="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters(doc_type,tab_code,liqr_code) select distinct doc_type,"+m_TAB_CODE+",liqr_code from tabreportparameters where tot_sale <> 0 and tab_code="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement(" insert into tabreportparameters(doc_type,tab_code,liqr_code) select distinct 'GROUP TOTAL',"+m_TAB_CODE+",liqr_code from tabreportparameters where tot_sale <> 0 and tab_code="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set gl_desc = isnull((select LTRIM(STR(CONVERT(INT,(CONVERT(FLOAT,(tot_sale/(CASE_QTY))))))) + '.' + LTRIM(STR(tot_sale-((CASE_QTY)* CONVERT(INT,(CONVERT(FLOAT,(tot_sale/(CASE_QTY)))))))) from itemmast,brndmast,sizemast where tabreportparameters.item_code=itemmast.item_code and itemmast.brnd_code=brndmast.brnd_code and itemmast.size_code=sizemast.size_code),'') where tab_code="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set tot_sale = (select sum(tot_sale) from tabreportparameters a where a.liqr_code=tabreportparameters.liqr_code and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = (select sum(convert(money,gl_desc)) from tabreportparameters a where a.liqr_code=tabreportparameters.liqr_code and a.tab_code=tabreportparameters.tab_code) where tab_code="+m_TAB_CODE+" and item_code = '' and doc_type='GROUP TOTAL'");
            ps1.executeUpdate();


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
