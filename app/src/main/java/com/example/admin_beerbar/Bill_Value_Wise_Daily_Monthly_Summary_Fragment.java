package com.example.admin_beerbar;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
public class Bill_Value_Wise_Daily_Monthly_Summary_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date,txt_total;
    ProgressBar pgb;
    PreparedStatement ps1;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="",m_loct_desc,m_loct_code;
    TransparentProgressDialog pd;
    double m_total=0.00;
    int m_compcode,m_TAB_CODE;
    Button btn_report;
    CheckBox chk_food_bar_group;
    int m_srno = 0;
    int m_clbal = 0;
    String IMEINumber,con_ipaddress,portnumber,db;
    RadioButton radio_summary_daily,radio_date_wise,radio_summary_monthly;
    ProgressDialog progressDoalog;
    public Bill_Value_Wise_Daily_Monthly_Summary_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bill_wise_valu_wise_daily_monthly_sumary, container, false);
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
        chk_food_bar_group=(CheckBox)view.findViewById(R.id.chk_food_bar_group);
        chk_food_bar_group.setChecked(true);
        radio_summary_monthly=(RadioButton) view.findViewById(R.id.radio_summary_monthly);
       // radio_date_wise=(RadioButton) view.findViewById(R.id.radio_date_wise);
        radio_summary_daily=(RadioButton)view.findViewById(R.id.radio_summary_daily);
        radio_summary_daily.setChecked(true);

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

                        if (radio_summary_daily.isChecked() == true) {
                            insert_summary_daily_data(Temp_frm_date,Temp_to_date);
                            Intent i=new Intent(getActivity(), Bill_Wise_Value_Wise_Daily_Sumary.class);
                            i.putExtra("frm_date",edt_frm_date.getText().toString());
                            i.putExtra("to_date",edt_to_date.getText().toString());
                            i.putExtra("Query_frm_date",Temp_frm_date);
                            i.putExtra("Query_to_date",Temp_to_date);
                            startActivity(i);
                        }
                        else  if (radio_summary_monthly.isChecked() == true) {
                            insert_summary_monthly_data(Temp_frm_date,Temp_to_date);
                            Intent i=new Intent(getActivity(), Bill_Wise_Value_Wise_Monthly_Sumary.class);
                            i.putExtra("frm_date",edt_frm_date.getText().toString());
                            i.putExtra("to_date",edt_to_date.getText().toString());
                            i.putExtra("Query_frm_date",Temp_frm_date);
                            i.putExtra("Query_to_date",Temp_to_date);
                            startActivity(i);
                        }
                       /* else if (radio_date_wise.isChecked() == true) {
                            insert_summary_date_data(Temp_frm_date,Temp_to_date);
                            Intent i=new Intent(getActivity(), Bill_Wise_Value_Wise_Date_Sumary.class);
                            i.putExtra("frm_date",edt_frm_date.getText().toString());
                            i.putExtra("to_date",edt_to_date.getText().toString());
                            i.putExtra("Query_frm_date",Temp_frm_date);
                            i.putExtra("Query_to_date",Temp_to_date);
                            startActivity(i);
                        }*/
                        pd.dismiss();
                    }
                }, 1000);
            }
        });

    }
    public void insert_summary_daily_data(String Temp_frm_date,String Temp_to_date) {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("UPDATE sales SET AMOUNT = ISNULL((SELECT SUM(QTY*RATE) FROM saleitem WHERE saleitem.DOC_NO=sales.DOC_NO AND saleitem.DOC_DT=sales.DOC_DT),AMOUNT) + CGST_AMT + SGST_AMT where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE sales SET NET_AMOUNT = round(AMOUNT + FOOD_SERVICE_TAX_AMT - DIS_AMOUNT + LIQ_VAT_AMT,0) where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE sales SET PAID_AMOUNT = NET_AMOUNT WHERE cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+" and BAL_AMOUNT = 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE sales SET BAL_AMOUNT = NET_AMOUNT-PAID_AMOUNT where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("insert into tabreportparameters(doc_dt,amount,dis_amount,tab_code,amount_1,amount_2,gl_desc,amount_3,amount_4,tot_amount) select doc_dt,isnull(sum(amount),0)as Amount,isnull(sum(dis_amount),0)as discount,"+m_TAB_CODE+",isnull(sum(cgst_amt),0)as cgstamt,isnull(sum(sgst_amt),0)as sgstamt,case when  'm_TABLEBILLINGTYPE'  = 'Y' then (SELECT LTRIM(STR(MIN(DOC_NO))) + ' TO ' + LTRIM(STR(MAX(DOC_NO))) FROM sales A WHERE A.DOC_DT=sales.DOC_DT) else (SELECT LTRIM(STR(count(*))) FROM sales A WHERE A.DOC_DT=sales.DOC_DT) end AS NARR,isnull(sum(food_service_tax_amt),0)as food_service_tax_amt,isnull(sum(liq_vat_amt),0)as liq_vat_amt,isnull(sum(net_amount),0)as net_Amount from sales where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+" group by doc_dt");
                ps1.executeUpdate();
                if(chk_food_bar_group.isChecked())
                {
                    ps1 = con.prepareStatement("update tabreportparameters set amount_5 = amount_5+isnull((select sum(item_value) from saleitem where item_type=3 and saleitem.doc_dt=tabreportparameters.doc_dt and saleitem.item_code in(select ltrim(str(menuitem_code)) from menucarditemmast,menumast where menucarditemmast.menu_code = menumast.menu_code and bargroup_yn = 0)),0) where tab_code="+m_TAB_CODE+"");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement(" update tabreportparameters set amount_6 = isnull((select sum(item_value) from saleitem where item_type<>3 and saleitem.doc_dt=tabreportparameters.doc_dt),0) + isnull((select sum(item_value) from saleitem where item_type=3 and saleitem.doc_dt=tabreportparameters.doc_dt and item_code in(select ltrim(str(menuitem_code)) from menucarditemmast,menumast where menucarditemmast.menu_code = menumast.menu_code and bargroup_yn = 1)),0) where tab_code="+m_TAB_CODE+"");
                    ps1.executeUpdate();
                }
                else
                {
                    ps1 = con.prepareStatement("update tabreportparameters set amount_5 = isnull((select sum(item_value) from saleitem where item_type=3 and saleitem.doc_dt=tabreportparameters.doc_dt),0) where tab_code= "+m_TAB_CODE+"");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement(" update tabreportparameters set amount_6 = isnull((select sum(item_value) from saleitem where item_type<>3 and saleitem.doc_dt=tabreportparameters.doc_dt),0) where tab_code= "+m_TAB_CODE+"");
                    ps1.executeUpdate();
                }



            }
            pgb.setVisibility(View.GONE);


        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }
    public void insert_summary_monthly_data(String Temp_frm_date,String Temp_to_date) {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();

               ps1 = con.prepareStatement("UPDATE sales SET AMOUNT = ISNULL((SELECT SUM(QTY*RATE) FROM saleitem WHERE saleitem.DOC_NO=sales.DOC_NO AND saleitem.DOC_DT=sales.DOC_DT),AMOUNT) + CGST_AMT + SGST_AMT where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE sales SET NET_AMOUNT = round(AMOUNT + FOOD_SERVICE_TAX_AMT - DIS_AMOUNT + LIQ_VAT_AMT,0) where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE sales SET PAID_AMOUNT = NET_AMOUNT WHERE cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+" and BAL_AMOUNT = 0");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE sales SET BAL_AMOUNT = NET_AMOUNT-PAID_AMOUNT where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+"");
                ps1.executeUpdate();

                //  ps1 = con.prepareStatement("insert into tabreportparameters(doc_dt,amount,dis_amount,tab_code,amount_1,amount_2,gl_desc,amount_3,amount_4,tot_amount) select doc_dt,isnull(sum(amount),0)as Amount,isnull(sum(dis_amount),0)as discount,"+m_TAB_CODE+",isnull(sum(cgst_amt),0)as cgstamt,isnull(sum(sgst_amt),0)as sgstamt,case when  'm_TABLEBILLINGTYPE'  = 'Y' then (SELECT LTRIM(STR(MIN(DOC_NO))) + ' TO ' + LTRIM(STR(MAX(DOC_NO))) FROM sales A WHERE A.DOC_DT=sales.DOC_DT) else (SELECT LTRIM(STR(count(*))) FROM sales A WHERE A.DOC_DT=sales.DOC_DT) end AS NARR,isnull(sum(food_service_tax_amt),0)as food_service_tax_amt,isnull(sum(liq_vat_amt),0)as liq_vat_amt,isnull(sum(net_amount),0)as net_Amount from sales where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+" group by doc_dt");
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_dt,amount,dis_amount,tab_code,amount_1,amount_2,amount_3,amount_4,tot_amount) select doc_dt,isnull(sum(amount),0)as Amount,isnull(sum(dis_amount),0)as discount,"+m_TAB_CODE+", isnull(sum(cgst_amt),0)as cgstamt,isnull(sum(sgst_amt),0)as sgstamt,isnull(sum(food_service_tax_amt),0)as food_service_tax_amt,isnull(sum(liq_vat_amt),0)as liq_vat_amt,isnull(sum(net_amount),0)as net_Amount from sales where cast(convert(varchar(10),doc_dt,101)as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code= "+m_compcode+" group by doc_dt");
                ps1.executeUpdate();
                if(chk_food_bar_group.isChecked())
                {
                    ps1 = con.prepareStatement("update tabreportparameters set amount_5 = amount_5+isnull((select sum(item_value) from saleitem where item_type=3 and saleitem.doc_dt=tabreportparameters.doc_dt and saleitem.item_code in(select ltrim(str(menuitem_code)) from menucarditemmast,menumast where menucarditemmast.menu_code = menumast.menu_code and bargroup_yn = 0)),0) where tab_code="+m_TAB_CODE+"");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = isnull((select sum(item_value) from saleitem where item_type<>3 and saleitem.doc_dt=tabreportparameters.doc_dt),0) + isnull((select sum(item_value) from saleitem where item_type=3 and saleitem.doc_dt=tabreportparameters.doc_dt and item_code in(select ltrim(str(menuitem_code)) from menucarditemmast,menumast where menucarditemmast.menu_code = menumast.menu_code and bargroup_yn = 1)),0) where tab_code="+m_TAB_CODE+"");
                    ps1.executeUpdate();
                }
                else
                {
                    ps1 = con.prepareStatement("update tabreportparameters set amount_5 = isnull((select sum(item_value) from saleitem where item_type=3 and saleitem.doc_dt=tabreportparameters.doc_dt),0) where tab_code= "+m_TAB_CODE+"");
                    ps1.executeUpdate();
                    ps1 = con.prepareStatement("update tabreportparameters set amount_6 = isnull((select sum(item_value) from saleitem where item_type<>3 and saleitem.doc_dt=tabreportparameters.doc_dt),0) where tab_code= "+m_TAB_CODE+"");
                    ps1.executeUpdate();
                }

            }
            pgb.setVisibility(View.GONE);


        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }
    public void insert_summary_payment(String Temp_frm_date,String Temp_to_date) {
        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();
            } else {
                ResultSet rs;
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("select sum(paid_amount-by_cash_pmt)as amount_1 from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode = 2");
                 rs = ps1.executeQuery();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('ONLINE PAYMENT SALES BREAKUP',"+rs.getString("amount_1")+","+m_TAB_CODE+","+m_srno+")");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("select (select crdt_desc from crdtmast where crdt_code=sales.crdt_code) as crdt_desc,sum(paid_amount-by_cash_pmt)as amount from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between   '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+"  and pmt_mode = 2 group by crdt_code order by crdt_desc");
                 rs = ps1.executeQuery();
                while (rs.next())
                {   m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('" +rs.getString("crdt_desc")+"','" +rs.getString("amount")+"',"+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
             //   ''''''''''New Addition On 25/07/2021 ''''''''''''''
                ps1 = con.prepareStatement("select sum(online_payment-paid_amount)as amt from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '01/01/2022' and '02/02/2022' and comp_code = 1 and comp_code = 1 and pmt_mode = 2 and online_payment > paid_amount");
                rs = ps1.executeQuery();
                int amt=0;
                if(!rs.getString("amt").equals(""))
                {
                    amt=Integer.parseInt(rs.getString("amt"));
                }
                if(amt>0)
                {   m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('ONLINE PAYMENT SALE-EXTRA',"+amt+","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
              ///-----------

                ps1 = con.prepareStatement("select (select crdt_desc from crdtmast where crdt_code=sales.crdt_code) as crdt_desc,sum(online_payment-paid_amount)as amount from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode = 2  and online_payment > paid_amount group by crdt_code order by crdt_desc");
                rs = ps1.executeQuery();
                while (rs.next())
                {   m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('" +rs.getString("crdt_desc")+"','" +rs.getString("amount")+"',"+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();

                //----''''''''''New Addition On 25/07/2021 ''''''''''''''
                m_clbal=0;
                ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyrcp.ac_head_id) as gl_desc,sum(amount) as amount from dailyrcp where crdt_code <> 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and ac_head_id <> 0 and ac_head_id in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id order by gl_desc");
                rs = ps1.executeQuery();
                int amount=0;
                if(!rs.getString("amount").equals(""))
                {
                    amount=Integer.parseInt(rs.getString("amount"));
                }
                if(amount>0)
                {   m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('RECEIPTS (REGULAR CUSTOMERS-ONLINE PAYMENT)',0,"+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                while (rs.next())
                { m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values("+rs.getString("gl_desc")+","+rs.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                    m_clbal = m_clbal +Integer.parseInt(rs.getString("amount"));
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('TOTAL =>'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+"");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();

                //----------------------------------------------------
                m_clbal=0;
                ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,ltrim(str(sum(amount),12,2)) as amount from dailyrcp where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and ac_head_id <> 0 and ac_head_id in(select PENDING_BILL_AC_HEAD_ID from profile) group by narr order by gl_desc ");
                rs = ps1.executeQuery();
                int amount1=0;
                if(!rs.getString("amount").equals(""))
                {
                    amount1=Integer.parseInt(rs.getString("amount"));
                }
                if(amount1>0)
                {   m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,tab_code,doc_no) values('RECEIPTS (OTHER CUSTOMERS)',"+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                while (rs.next())
                { m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values("+rs.getString("gl_desc")+","+rs.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                    m_clbal = m_clbal +Integer.parseInt(rs.getString("amount"));
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('TOTAL =>'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+"");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();

                //==================================================
                m_clbal=0;
                ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,ltrim(str(sum(amount),12,2)) as amount from dailyrcp where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and ac_head_id <> 0 and ac_head_id not in(select PENDING_BILL_AC_HEAD_ID from profile) and ac_head_id not in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by narr order by narr");
                rs = ps1.executeQuery();
                int amount2=0;
                if(!rs.getString("amount").equals(""))
                {
                    amount2=Integer.parseInt(rs.getString("amount"));
                }
                if(amount2>0)
                {   m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('OTHER RECEIPTS',0,"+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                while (rs.next())
                { m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values("+rs.getString("gl_desc")+","+rs.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                    m_clbal = m_clbal +Integer.parseInt(rs.getString("amount"));
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('TOTAL =>'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+"");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
             //================================================================================================
                m_clbal=0;
                ps1 = con.prepareStatement("select ltrim(narr) as gl_desc,ltrim(str(sum(amount),12,2)) as amount from dailyrcp where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and ac_head_id = 0 group by narr order by narr");
                rs = ps1.executeQuery();
                int amount3=0;
                if(!rs.getString("amount").equals(""))
                {
                    amount3=Integer.parseInt(rs.getString("amount"));
                }
                if(amount3>0)
                {   m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('OTHER RECEIPTS',0,"+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                while (rs.next())
                { m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values("+rs.getString("gl_desc")+","+rs.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                    m_clbal = m_clbal +Integer.parseInt(rs.getString("amount"));
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('TOTAL =>'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+"");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
             //___________________________________________________________________________
                //''''''''Addition On 18/4/2019=============
                ps1 = con.prepareStatement("select sum(amount)as amt from dailyrcp where crdt_code <> 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+"");
                rs = ps1.executeQuery();
                int amt1=0;
                if(!rs.getString("amt").equals(""))
                {
                    amt1=Integer.parseInt(rs.getString("amt"));
                }
                if(amt1>0)
                {   m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('ONLINE PAYMENT RECEIPTS BREAKUP',"+amt+","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();

                ps1 = con.prepareStatement("select (select crdt_desc from crdtmast where crdt_code=dailyrcp.crdt_code) as crdt_desc,sum(amount)as amount from dailyrcp where crdt_code <> 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" group by crdt_code order by crdt_desc");
                rs = ps1.executeQuery();
                while (rs.next())
                { m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values("+rs.getString("crdt_desc")+","+rs.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();

                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();

                //********************************************************

                //''''''''Addition On 18/4/2019
                m_clbal=0;

                ps1 = con.prepareStatement("select (select gl_desc From glmast Where ac_head_id=dailyrcp.ac_head_id) as gl_desc,sum(amount) as amount from dailyrcp where crdt_code = 0 and cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and ac_head_id <> 0 and ac_head_id in(select ac_head_id From glmast Where group_code in(select fatree.group_code from fagroupparameters,fatree where fagroupparameters.group_type='CUSTOMERS' and fatree.sub_groupcode=fagroupparameters.group_code)) group by ac_head_id order by gl_desc");
                rs = ps1.executeQuery();
                int amount4=0;
                if(!rs.getString("amount").equals(""))
                {
                    amount4=Integer.parseInt(rs.getString("amount"));
                }
                if(amount4>0)
                {   m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount,tab_code,doc_no) values('RECEIPTS (REGULAR CUSTOMERS-CASH)',0,"+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                while (rs.next())
                { m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values("+rs.getString("gl_desc")+","+rs.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                    m_clbal = m_clbal +Integer.parseInt(rs.getString("amount"));
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('TOTAL =>'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+"");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                //********************************************************

              //############################################
                ps1 = con.prepareStatement("select 'CASH PAYMENT FROM SALE' as gl_desc,LTRIM(STR(ISNULL((select sum(paid_amount) from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode=1),0)+ISNULL((select sum(by_cash_pmt) from sales where cast(convert(varchar(10),doc_dt,101) as datetime) between '"+Temp_frm_date+"' and '"+Temp_to_date+"' and comp_code = "+m_compcode+" and pmt_mode=2),0),12,2)) as amount");
                ps1.executeUpdate();
                while (rs.next())
                { m_srno = m_srno + 1;
                    ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values("+rs.getString("gl_desc")+","+rs.getString("amount")+","+m_TAB_CODE+","+m_srno+")");
                    ps1.executeUpdate();
                    m_clbal = m_clbal +Integer.parseInt(rs.getString("amount"));
                }
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(gl_desc,amount_1,tab_code,doc_no) values('TOTAL CASH COLLECTION'," + m_clbal+ ","+m_TAB_CODE+","+m_srno+"");
                ps1.executeUpdate();
                m_srno = m_srno + 1;
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no) values("+m_srno+")");
                ps1.executeUpdate();


            }
            pgb.setVisibility(View.GONE);


        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error.." + e, Toast.LENGTH_SHORT).show();
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
