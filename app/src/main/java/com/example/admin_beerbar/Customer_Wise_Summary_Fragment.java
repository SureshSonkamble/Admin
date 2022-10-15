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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.admin_beerbar.Class.TransparentProgressDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Customer_Wise_Summary_Fragment extends Fragment {
    TextView edt_as_on_date;
    Button btn_report;
    int mYear, mMonth, mDay;
    //--------------------------
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    //String db = "WINESHOP";
    // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String formattedDate,Temp_date;
    TransparentProgressDialog pd;
    int m_TAB_CODE;
    int m_compcode;
    String con_ipaddress,portnumber,IMEINumber,str_month="",str_day="",m_purdate,db;
    DatePickerDialog  datePickerDialog;
    PreparedStatement ps1;
    double m_clbal,m_grntot,m_openingcash,o_cashopbal,m_cashopbal,m_retailcash,m_stockadjamt,m_computernetcash,m_shortextracash,m_withdrawalcash,m_nextdayopcash,m_cardamount;
    int m_closingcashyn;
    int m_entryseqno;

    String TEMPSTOCK_LIQR;
    String TEMPSTOCK_OTHR;
    public Customer_Wise_Summary_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.daily_cash_expence, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        SharedPreferences ss = getActivity().getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getActivity().getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");

        SharedPreferences sp1 = getActivity().getSharedPreferences("IPADDR", MODE_PRIVATE);
        con_ipaddress = sp1.getString("ipaddress", "");
        portnumber = sp1.getString("portnumber", "");
        db = sp1.getString("db", "");
        m_TAB_CODE = sp1.getInt("TAB_CODE", 0);
        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        formattedDate = df.format(c);
        System.out.println("Today Date => " + formattedDate);

        edt_as_on_date=(TextView) view.findViewById(R.id.edt_as_on_date);
        edt_as_on_date.setText(formattedDate);

        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Temp_date=out.format(d);
        btn_report=(Button) view.findViewById(R.id.btn_report);

        //img_as_on_date=(ImageView)view.findViewById(R.id.img_as_on_date);
        edt_as_on_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth)
                            {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edt_as_on_date.setText(""+str_day + "/" + str_month + "/" + year);

                                Temp_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;

                            }
                        }, mYear, mMonth, mDay);
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
                        m_purdate = "doc_dt";
                        insert_data(Temp_date);
                        Intent i=new Intent(getActivity(),Customer_Wise_Summary_Report.class);
                        i.putExtra("date",edt_as_on_date.getText().toString());
                        startActivity(i);
                        pd.dismiss();
                    }
                }, 1000);

            }
        });
    }

    public void insert_data(String date)
    {
        //===========ALL==============================
        try {
            con = CONN(con_ipaddress, portnumber,db);

           //===============Stock Updation liqr=====================
            ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+"");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,amount,tab_code)select ac_head_id,opening_bal,"+m_TAB_CODE+" from custwiseopeningbal where comp_code="+m_compcode+" and ac_head_id in(select ac_head_id from glmast where group_code in (select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code))");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,amount,tab_code)select ac_head_id,isnull(sum(bal_amount),0),"+m_TAB_CODE+" from sales where doc_dt <= '"+date+"' and comp_code="+m_compcode+" and ac_head_id <>0 and ac_head_id in(select ac_head_id from glmast where group_code in (select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,amount,tab_code)select ac_head_id,isnull(sum(amount),0),"+m_TAB_CODE+" from dailyexp where doc_dt <= '"+date+"' and comp_code="+m_compcode+" and ac_head_id <>0 and ac_head_id in(select ac_head_id from glmast where group_code in (select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id");
            ps1.executeUpdate();
            ps1 = con.prepareStatement("insert into tabreportparameters (ac_head_id,amount,tab_code)select ac_head_id,-1*isnull(sum(amount),0),"+m_TAB_CODE+" from dailyrcp where doc_dt <= '"+date+"' and comp_code="+m_compcode+" and ac_head_id <>0 and ac_head_id in(select ac_head_id from glmast where group_code in (select fatree.group_code from fagroupparameters ,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id");
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
