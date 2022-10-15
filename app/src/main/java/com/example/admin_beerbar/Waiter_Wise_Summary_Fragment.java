package com.example.admin_beerbar;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.admin_beerbar.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Waiter_Wise_Summary_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date,txt_total;
    ProgressBar pgb;
    PreparedStatement ps1;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="",db,m_loct_code;
    TransparentProgressDialog pd;
    double m_total=0.00;
    int m_compcode,m_TAB_CODE;
    Button btn_report;

    String IMEINumber,con_ipaddress,portnumber;

    public Waiter_Wise_Summary_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.section_wise_summary, container, false);
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
        btn_report=(Button)view.findViewById(R.id.btn_report);
        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        txt_total=(TextView) view.findViewById(R.id.txt_total);
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

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run()
                    {
                           insert_data(Temp_frm_date,Temp_to_date);
                            Intent i=new Intent(getActivity(), Waiter_Wise_Summary_Report.class);
                            i.putExtra("frm_date",edt_frm_date.getText().toString());
                            i.putExtra("to_date",edt_to_date.getText().toString());
                            i.putExtra("Query_frm_date",Temp_frm_date);
                            i.putExtra("Query_to_date",Temp_to_date);
                            startActivity(i);

                        pd.dismiss();
                    }
                }, 1000);
            }
        });

    }
    public void insert_data(String Temp_frm_date,String Temp_to_date) {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(item_code,doc_no,doc_dt,amount_1,ac_head_id,tab_code) select item_code,doc_no,doc_dt,item_value,item_type,"+m_TAB_CODE+" from saleitem where doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code="+m_compcode+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set brnd_code = (select watr_code from sales where doc_no=tabreportparameters.doc_no and doc_dt=tabreportparameters.doc_dt) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set ac_head_id = 1 where ac_head_id = 3 and tab_code="+m_TAB_CODE+" and item_code in(select menuitem_code from menucarditemmast where menu_code in(select menu_code from menumast where bargroup_yn=1))");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_2 = amount_1 where ac_head_id <> 3 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_1 = 0 where ac_head_id <> 3 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(brnd_code,amount_1,amount_2,liqr_code,tab_code) select brnd_code,sum(amount_1),sum(amount_2),1,"+m_TAB_CODE+" from tabreportparameters where tab_code="+m_TAB_CODE+" group by brnd_code");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where liqr_code = 0 and tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount_1 + amount_2 where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_3 = isnull((select sum(dis_amount) from sales where watr_code = tabreportparameters.brnd_code and doc_dt  between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code="+m_compcode+" group by watr_code),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_4 = isnull((select sum(net_amount) from sales where watr_code = tabreportparameters.brnd_code and doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code="+m_compcode+" group by watr_code),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_5 = isnull((select sum(FOOD_SERVICE_TAX_AMT) from sales where watr_code = tabreportparameters.brnd_code and doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code="+m_compcode+" group by watr_code),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount_6 = isnull((select sum(cgst_amt) from sales where watr_code = tabreportparameters.brnd_code and doc_dt between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code="+m_compcode+" group by watr_code),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_sale = isnull((select sum(sgst_amt) from sales where watr_code = tabreportparameters.brnd_code and doc_dt  between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code="+m_compcode+" group by watr_code),0) where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set amount = amount + amount_5 + amount_6 + tot_sale where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();


            }
            pgb.setVisibility(View.GONE);


        } catch (Exception e) {
            Toast.makeText(getActivity(), "246" + e, Toast.LENGTH_SHORT).show();
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
