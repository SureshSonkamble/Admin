package com.example.admin_beerbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
public class Item_Wise_Stock_Other_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date;
    int m_TAB_CODE;
    String con_ipaddress ,portnumber, str_month="",str_day="",m_loct_desc,m_loct_code,db;
    ProgressBar pbbar;
    ProgressDialog progressDoalog;
    TransparentProgressDialog pd;
    Button btn_proceed,btn_report,btn_check_all,btn_uncheck_all;
    String IMEINumber;
    int m_compcode;
    Date date;
    Spinner sp_stock_loc;
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
    String Temp_frm_date,Temp_to_date,formattedDate;
    Date d;
    RadioGroup radioGroup,radiowiseGroup;
    String check_id = "", M_liqr_code = "";
    LinearLayout lin_radio_hide;
    public Item_Wise_Stock_Other_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.item_wise_stock_other, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp_stock_loc=(Spinner)view.findViewById(R.id.sp_stock_loc);
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
                btn_uncheck_all.setVisibility(View.INVISIBLE);
                lin_radio_hide.setVisibility(View.VISIBLE);
                bill_arryList.clear();
                bill_recyclerAdapter.notifyDataSetChanged();
                swap_arryList.clear();
                swap_recyclerAdapter.notifyDataSetChanged();
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        pre_insert_data(Temp_frm_date, Temp_to_date);
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
                            PreparedStatement ps = con.prepareStatement("select distinct liqr_code as menu_code,(select menu_desc from menumast where menu_code=tabreportparameters.liqr_code) as menu_desc,'' from tabreportparameters where tab_code="+m_TAB_CODE+"");
                            ResultSet rs = ps.executeQuery();

                            while (rs.next()) {
                                smap = new HashMap<String, String>();
                                smap.put("menu_code", rs.getString("menu_code"));
                                smap.put("menu_desc", rs.getString("menu_desc"));
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
                    PreparedStatement ps = con.prepareStatement("select distinct liqr_code as menu_code,(select menu_desc from menumast where menu_code=tabreportparameters.liqr_code) as menu_desc,'' from tabreportparameters where tab_code="+m_TAB_CODE+"");
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        smap = new HashMap<String, String>();
                        smap.put("menu_code", rs.getString("menu_code"));
                        smap.put("menu_desc", rs.getString("menu_desc"));
                        //-------------------------------------------
                        bill_arryList.add(smap);

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
                            check_id = "";
                            M_liqr_code = "";

                            for (HashMap<String, String> map1 : swap_arryList)
                                for (Map.Entry<String, String> mapEntry : map1.entrySet()) {
                                    if (mapEntry.getKey().equals("menu_code")) {

                                        check_id = mapEntry.getValue();
                                        if (M_liqr_code.length() == 0) {
                                            M_liqr_code = check_id;
                                        } else {
                                            M_liqr_code = M_liqr_code + ',' + check_id;
                                        }
                                    }
                                }

                            Intent i = new Intent(getActivity(), Item_Wise_Stock_Other_Report.class);
                            i.putExtra("checklist", M_liqr_code);
                            i.putExtra("loc", m_loct_desc);
                            i.putExtra("from_date", edt_frm_date.getText().toString());
                            i.putExtra("to_date", edt_to_date.getText().toString());

                            startActivity(i);
                        }
                        pd.dismiss();
                      //  progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 2000);

            }
        });
        new load_spinner_data().execute();
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

                String q="select distinct liqr_code as menu_code,(select menu_desc from menumast where menu_code=tabreportparameters.liqr_code) as menu_desc,'' from tabreportparameters where tab_code="+m_TAB_CODE+"";
                Log.d("final query_____",q);
                PreparedStatement ps = con.prepareStatement(q);
                ResultSet rs = ps.executeQuery();

                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    map= new HashMap<String, String>();
                    map.put("menu_code", rs.getString("menu_code"));
                    map.put("menu_desc", rs.getString("menu_desc"));

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
            holder.name.setText(attendance_list.get(position).get("menu_desc"));
           // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    smap= new HashMap<String, String>();
                    smap.put("menu_code",attendance_list.get(position).get("menu_code"));
                    smap.put("menu_desc",attendance_list.get(position).get("menu_desc"));

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

            holder.name.setText(attendance_list.get(position).get("menu_desc"));
           // holder.lsize.setText(attendance_list.get(position).get("LSIZE"));

            holder.action.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    map= new HashMap<String, String>();
                    map.put("menu_code",attendance_list.get(position).get("menu_code"));
                    map.put("menu_desc",attendance_list.get(position).get("menu_desc"));

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

    public void pre_insert_data(String frm_date,String to_date)
    {
        //===========ALL==============================
        try
        {
            con = CONN(con_ipaddress,portnumber,db);
            ps1 = con.prepareStatement("delete from tabreportparameters where TAB_CODE =" + m_TAB_CODE + "");
            ps1.executeUpdate();
            if(m_loct_code.equals("0.0")) {

                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,tab_code) select distinct item_code," + m_TAB_CODE + " from opitstok where comp_code=" + m_compcode + " and item_type=2 union select distinct item_code," + m_TAB_CODE + " from puritem where doc_dt <= '" + to_date + "' and comp_code=" + m_compcode + " and stock_method=1 and pur_type=2 union select distinct item_code," + m_TAB_CODE + " from countersaleitem where doc_dt <= '" + to_date + "' and comp_code=" + m_compcode + " and item_type=3 and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) union select distinct item_code," + m_TAB_CODE + " from saleitem where doc_dt <= '" + to_date + "' and comp_code=" + m_compcode + " and item_type=3 and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1)");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = isnull((select sum(op_qty) from opitstok where comp_code=" + m_compcode + " and item_code=tabreportparameters.item_code and item_type=2),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal + isnull((select sum(bottle_qty)+sum(free_qty) from puritem where doc_dt < '" + frm_date + "' and stock_method=1 and comp_code=" + m_compcode + " and item_code=tabreportparameters.item_code and pur_type=2),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(qty) from countersaleitem where item_type=3 and doc_dt < '" + frm_date + "' and maintain_stock=1 and comp_code=" + m_compcode + " and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1)),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(qty) from saleitem where item_type=3 and doc_dt < '" + frm_date + "' and maintain_stock=1 and comp_code=" + m_compcode + " and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1)),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = isnull((select sum(bottle_qty)+sum(free_qty) from puritem where doc_dt between '" + frm_date + "' and '" + to_date + "' and stock_method=1 and pur_type=2 and comp_code=" + m_compcode + " and item_code=tabreportparameters.item_code),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = isnull((select sum(qty) from countersaleitem where item_type=3 and doc_dt between '" + frm_date + "' and '" + to_date + "' and maintain_stock=1 and comp_code=" + m_compcode + " and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and item_code=tabreportparameters.item_code),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale+isnull((select sum(qty) from saleitem where item_type=3 and doc_dt between '" + frm_date + "' and '" + to_date + "' and maintain_stock=1 and comp_code=" + m_compcode + " and item_code in (select menuitem_code from menucarditemmast where maintain_stock=1) and item_code=tabreportparameters.item_code),0) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_opbal+amount_1-tot_sale where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+ m_TAB_CODE +" and (gl_opbal+tot_sale+tot_sale) = 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set liqr_code = (select menu_code from menucarditemmast where menuitem_code=tabreportparameters.item_code) where tab_code=" + m_TAB_CODE + "");
                ps1.executeUpdate();
            }
            else {
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,tab_code) select distinct item_code,"+ m_TAB_CODE +" from opitstok where comp_code="+m_compcode+" and item_type=2 and loct_code = "+m_loct_code+" union select distinct item_code,"+ m_TAB_CODE +" from puritem where doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and stock_method=1 and pur_type=2 and loct_code = "+m_loct_code+" union select distinct item_code,"+ m_TAB_CODE +" from countersaleitem where doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and item_type=3 and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+" union select distinct item_code,"+ m_TAB_CODE +" from saleitem where doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and item_type=3 and maintain_stock=1 and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+" union select distinct item_code,"+ m_TAB_CODE +" from transfernote where doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and item_type=2 and from_loct_code = "+m_loct_code+" union select distinct item_code,"+m_TAB_CODE+" from transfernote where doc_dt <= '"+to_date+"' and comp_code="+m_compcode+" and item_type=2 and to_loct_code ="+m_loct_code+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = isnull((select sum(op_qty) from opitstok where comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and item_type=2 and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal + isnull((select sum(bottle_qty)+sum(free_qty) from puritem where doc_dt < '"+frm_date+"' and stock_method=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and pur_type=2 and loct_code ="+m_loct_code+"),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal + isnull((select sum(bottle_qty) from transfernote where doc_dt < '"+frm_date+"' and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and item_type=2 and to_loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(qty) from countersaleitem where item_type=3 and doc_dt < '"+frm_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+"),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(qty) from saleitem where item_type=3 and doc_dt < '"+frm_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and loct_code = "+m_loct_code+"),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_opbal = gl_opbal - isnull((select sum(bottle_qty) from transfernote where item_type=2 and doc_dt < '"+frm_date+"' and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and from_loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = isnull((select sum(bottle_qty)+sum(free_qty) from puritem where doc_dt between '"+frm_date+"' and '"+to_date+"' and stock_method=1 and pur_type=2 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = amount_1+isnull((select sum(bottle_qty) from transfernote where doc_dt between '"+frm_date+"' and '"+to_date+"' and item_type=2 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and to_loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = isnull((select sum(qty) from countersaleitem where item_type=3 and doc_dt between '"+frm_date+"' and '"+to_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale+isnull((select sum(qty) from saleitem where item_type=3 and doc_dt between '"+frm_date+"' and '"+to_date+"' and maintain_stock=1 and comp_code="+m_compcode+" and item_code in(select menuitem_code from menucarditemmast where maintain_stock=1) and item_code=tabreportparameters.item_code and loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = tot_sale+isnull((select sum(bottle_qty) from transfernote where doc_dt between '"+frm_date+"' and '"+to_date+"' and item_type=2 and comp_code="+m_compcode+" and item_code=tabreportparameters.item_code and from_loct_code = "+m_loct_code+"),0) where tab_code="+ m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set gl_clbal = gl_opbal+amount_1-tot_sale where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+" and (gl_opbal+amount_1+tot_sale) = 0 ");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set liqr_code = (select menu_code from menucarditemmast where menuitem_code=tabreportparameters.item_code) where tab_code="+m_TAB_CODE+"");
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
                    String query = "select LOCT_CODE,LOCT_DESC from LOCTmast where LOCT_CODE >0 and loct_code in(select loct_code from onlnstok where item_type =2) union select 0 as loct_code,'TOTAL LOCATIONS' as loct_desc order by LOCT_DESC";
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
