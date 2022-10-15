package com.example.admin_beerbar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Date_Wise_Cash_Withdrawal_Fragment extends Fragment {
    int mYear, mMonth, mDay;
    TextView edt_frm_date,edt_to_date,txt_total,txt_cash_with_total,txt_card_payment_total,txt_purchase_total,txt_challan_total,txt_expenses_total,txt_sale_amt_food_total,txt_sale_amt_liqr_total,txt_purchase_liqr_total;
    ProgressBar pgb;
    PreparedStatement ps1;
    String classs = "net.sourceforge.jtds.jdbc.Driver";
   // String db = "WINESHOP";
   // String db = "BEERBAR";
    String un = "SA";
    String password = "PIMAGIC";
    Connection con = null;
    String formattedDate,Temp_frm_date,Temp_to_date,str_month="",str_day="",db;
    TransparentProgressDialog pd;
    double amt_total1=0.00;
    double amt_total2=0.00;
    double amt_total3=0.00;
    double amt_total4=0.00;
    double amt_total5=0.00;
    double amt_total6=0.00;
    double amt_total7=0.00;
    double amt_total8=0.00;
    int m_srno=0;
    String m_date;
    int m_compcode,m_TAB_CODE;
    Button btn_report;
    String IMEINumber,con_ipaddress,portnumber;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    public Date_Wise_Cash_Withdrawal_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.date_wise_cash_withdrawal, container, false);
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

        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) view.findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(getActivity(), menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);

        pgb=(ProgressBar)view.findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.hourglass);
        btn_report=(Button)view.findViewById(R.id.btn_report);
        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);

        txt_expenses_total=(TextView) view.findViewById(R.id.txt_expenses_total);
        txt_sale_amt_food_total=(TextView) view.findViewById(R.id.txt_sale_amt_food_total);
        txt_sale_amt_liqr_total=(TextView) view.findViewById(R.id.txt_sale_amt_liqr_total);
        txt_purchase_liqr_total=(TextView) view.findViewById(R.id.txt_purchase_liqr_total);

        txt_cash_with_total=(TextView) view.findViewById(R.id.txt_cash_with_total);
        txt_card_payment_total=(TextView) view.findViewById(R.id.txt_card_payment_total);
        txt_purchase_total=(TextView) view.findViewById(R.id.txt_purchase_total);
        txt_challan_total=(TextView) view.findViewById(R.id.txt_challan_total);
        txt_total=(TextView) view.findViewById(R.id.txt_total);
        edt_frm_date=(TextView) view.findViewById(R.id.edt_frm_date);
        edt_to_date=(TextView) view.findViewById(R.id.edt_to_date);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        formattedDate = df.format(c);
        edt_to_date.setText(formattedDate);
        edt_frm_date.setText(formattedDate);
        Date  d = Calendar.getInstance().getTime();

        SimpleDateFormat out = new SimpleDateFormat("M/d/yyyy");
        Temp_frm_date=out.format(d);

        Date dd = Calendar.getInstance().getTime();
        SimpleDateFormat ot = new SimpleDateFormat("M/d/yyyy");
        Temp_to_date=ot.format(dd);

        edt_frm_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_cash_with_total.setText("");
                txt_card_payment_total.setText("");
                txt_purchase_total.setText("");
                txt_challan_total.setText("");
                txt_expenses_total.setText("");
                txt_sale_amt_food_total.setText("");
                txt_sale_amt_liqr_total.setText("");
                txt_purchase_liqr_total.setText("");
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
                txt_cash_with_total.setText("");
                txt_card_payment_total.setText("");
                txt_purchase_total.setText("");
                txt_challan_total.setText("");
                txt_expenses_total.setText("");
                txt_sale_amt_food_total.setText("");
                txt_sale_amt_liqr_total.setText("");
                txt_purchase_liqr_total.setText("");
                menu_card_arryList.clear();
                attendance_recyclerAdapter.notifyDataSetChanged();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //--------------
                                menu_card_arryList.clear();
                                if (attendance_recyclerAdapter != null) {
                                    attendance_recyclerAdapter.notifyDataSetChanged();
                                    System.out.println("Adapter " + attendance_recyclerAdapter.toString());
                                }
                                //-----------------
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

                txt_cash_with_total.setText("");
                txt_card_payment_total.setText("");
                txt_purchase_total.setText("");
                txt_challan_total.setText("");
                txt_expenses_total.setText("");
                txt_sale_amt_food_total.setText("");
                txt_sale_amt_liqr_total.setText("");
                txt_purchase_liqr_total.setText("");
                menu_card_arryList.clear();
                attendance_recyclerAdapter.notifyDataSetChanged();
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run()
                    {
                        grid_data(Temp_frm_date,Temp_to_date);
                        if(menu_card_arryList.size()==0)
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
                }, 1000);
            }
        });
    }

    public void grid_data(String temp_frm_date,String temp_to_date) {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            con = CONN(con_ipaddress,portnumber,db);
            if (con == null) {
                Toast.makeText(getActivity(), "Error In Connection With SQL Server", Toast.LENGTH_SHORT).show();

            } else {

                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+"");
                ps1.executeUpdate();

                SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(temp_frm_date));
                c.add(Calendar.DATE,0);  // number of days to add
                m_date = sdf.format(c.getTime());  // dt is now the new date

                while (true) {
                    if (m_date.equals(temp_to_date)) {
                        break;
                    }
                    ps1 = con.prepareStatement("insert into tabreportparameters(doc_dt,tab_code,doc_type) values('" +m_date+ "'," + m_TAB_CODE + ",'A')");
                    ps1.executeUpdate();

                    c.add(Calendar.DATE, 1);  // number of days to add
                    m_date = sdf.format(c.getTime());  // dt is now the new date
                }

                ps1 = con.prepareStatement("insert into tabreportparameters(doc_dt,tab_code,doc_type) values('" +m_date+ "'," + m_TAB_CODE + ",'A')");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE tabreportparameters SET amount_1 = ISNULL((SELECT SUM(withdrawal_amount) from datewiseopcash where datewiseopcash.doc_dt = tabreportparameters.doc_dt),0) where tab_code = " + m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE tabreportparameters SET amount_2 = ISNULL((SELECT SUM(card_amount) from datewiseopcash where datewiseopcash.doc_dt = tabreportparameters.doc_dt),0) where tab_code = " + m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE tabreportparameters SET amount_3 = ISNULL((SELECT SUM(amount) from dailyexp where dailyexp.doc_dt = tabreportparameters.doc_dt),0) where tab_code = " + m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE tabreportparameters SET amount_4 = ISNULL((SELECT SUM(item_value) from saleitem where item_type = 3 and saleitem.doc_dt = tabreportparameters.doc_dt),0) where tab_code = " + m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE tabreportparameters SET amount_5 = ISNULL((SELECT SUM(item_value) from saleitem where item_type <> 3 and saleitem.doc_dt = tabreportparameters.doc_dt),0) where tab_code = " + m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE tabreportparameters SET amount_6 = ISNULL((SELECT SUM(net_amount) from purchase where pur_type = 2  and purchase.doc_dt = tabreportparameters.doc_dt),0) where tab_code = " + m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE tabreportparameters SET tot_sale = ISNULL((SELECT SUM(net_amount) from purchase where pur_type = 1  and purchase.doc_dt = tabreportparameters.doc_dt),0) where tab_code = " + m_TAB_CODE +"");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+" and doc_type = 'B'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("insert into tabreportparameters(doc_no,doc_dt,amount,tab_code,doc_type) select doc_no,doc_dt,sum(basic_amt)," + m_TAB_CODE +",'B' from chalanitem where doc_dt between '" + temp_frm_date + "' and '" +temp_to_date+"' group by doc_no,doc_dt");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set dis_amount = (select sum(add_amount) from chalanitem where chalanitem.doc_no=tabreportparameters.doc_no and chalanitem.doc_dt=tabreportparameters.doc_dt and chalanitem.doc_srno = 1 and doc_dt between '" + temp_frm_date + "' and '" +temp_to_date+"') where tab_code = "+m_TAB_CODE+" and doc_type = 'B'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("update tabreportparameters set tot_amount = amount + dis_amount where tab_code = "+m_TAB_CODE+" and doc_type = 'B'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("UPDATE tabreportparameters SET gl_clbal = ISNULL((SELECT SUM(tot_amount) from tabreportparameters a where a.doc_dt = tabreportparameters.doc_dt and a.tab_code = " + m_TAB_CODE +" and a.doc_type = 'B'),0) where tab_code = " + m_TAB_CODE +" and doc_type = 'A'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where tab_code="+m_TAB_CODE+" and doc_type = 'B'");
                ps1.executeUpdate();
                ps1 = con.prepareStatement("delete from tabreportparameters where (amount_1+amount_2+amount_3+amount_4+amount_5+amount_6+tot_sale+gl_clbal) = 0 and tab_code = " + m_TAB_CODE +" ");
                ps1.executeUpdate();
                PreparedStatement ps = con.prepareStatement("select convert(varchar(10),doc_dt,103) as docdt,case when amount_1 <> 0 then ltrim(str(amount_1,16,2)) else '' end as CASH_WITHDRAWAL, case when amount_2 <> 0 then ltrim(str(amount_2,16,2)) else '' end as CARD_PAYMENT, case when amount_3 <> 0 then ltrim(str(amount_3,16,2)) else '' end as EXP_AMOUNT, case when amount_4 <> 0 then ltrim(str(amount_4,16,2)) else '' end as SALE_AMOUNT_FOOD, case when amount_5 <> 0 then ltrim(str(amount_5,16,2)) else '' end as SALE_AMOUNT_LIQR, case when amount_6 <> 0 then ltrim(str(amount_6,16,2)) else '' end as PUR_AMOUNT_OTHER, case when tot_sale <> 0 then ltrim(str(tot_sale,16,2)) else '' end as PUR_AMOUNT_LIQR, case when gl_clbal <> 0 then ltrim(str(gl_clbal,16,2)) else '' end as CHALLAN_AMOUNT,amount_1,amount_2,amount_3,amount_4,amount_5,amount_6,tot_sale,gl_clbal from tabreportparameters where tab_code = "+m_TAB_CODE+" order by doc_dt");
                ResultSet rs = ps.executeQuery();
                menu_card_arryList.clear();
                amt_total1=0;
                amt_total2=0;
                amt_total3=0;
                amt_total4=0;
                amt_total5=0;
                amt_total6=0;
                amt_total7=0;
                amt_total8=0;
                m_srno=0;
                //ArrayList data1 = new ArrayList();
                while (rs.next()) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    m_srno = m_srno+1;
                    map.put("srno", ""+m_srno);
                    map.put("docdt", rs.getString("docdt"));
                    map.put("CASH_WITHDRAWAL", rs.getString("CASH_WITHDRAWAL"));
                    map.put("CARD_PAYMENT", rs.getString("CARD_PAYMENT"));
                    map.put("EXP_AMOUNT", rs.getString("EXP_AMOUNT"));
                    map.put("SALE_AMOUNT_FOOD", rs.getString("SALE_AMOUNT_FOOD"));
                    map.put("SALE_AMOUNT_LIQR", rs.getString("SALE_AMOUNT_LIQR"));
                    map.put("PUR_AMOUNT_OTHER", rs.getString("PUR_AMOUNT_OTHER"));
                    map.put("PUR_AMOUNT_LIQR", rs.getString("PUR_AMOUNT_LIQR"));
                    map.put("CHALLAN_AMOUNT", rs.getString("CHALLAN_AMOUNT"));


                    amt_total1=amt_total1+Double.parseDouble(rs.getString("amount_1"));
                    amt_total2=amt_total2+Double.parseDouble(rs.getString("amount_2"));
                    amt_total3=amt_total3+Double.parseDouble(rs.getString("amount_3"));
                    amt_total4=amt_total4+Double.parseDouble(rs.getString("amount_4"));
                    amt_total5=amt_total5+Double.parseDouble(rs.getString("amount_5"));
                    amt_total6=amt_total6+Double.parseDouble(rs.getString("amount_6"));
                    amt_total7=amt_total7+Double.parseDouble(rs.getString("tot_sale"));
                    amt_total8=amt_total8+Double.parseDouble(rs.getString("gl_clbal"));

                    menu_card_arryList.add(map);
                }

                NumberFormat nf1 =new DecimalFormat(".00");
                txt_cash_with_total.setText(""+nf1.format(amt_total1));
                txt_card_payment_total.setText(""+nf1.format(amt_total2));
                txt_expenses_total.setText(""+nf1.format(amt_total3));
                txt_sale_amt_food_total.setText(""+nf1.format(amt_total4));
                txt_sale_amt_liqr_total.setText(""+nf1.format(amt_total5));
                txt_purchase_total.setText(""+nf1.format(amt_total6));
                txt_purchase_liqr_total.setText(""+nf1.format(amt_total7));
                txt_challan_total.setText(""+nf1.format(amt_total8));
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_wise_cash_withdrawal_list, parent, false);
            atnds_recyclerAdapter.Pex_ViewHolder viewHolder = new atnds_recyclerAdapter.Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final atnds_recyclerAdapter.Pex_ViewHolder holder, final int position) {

            holder.list_d1.setText(attendance_list.get(position).get("srno"));
            holder.list_d2.setText(attendance_list.get(position).get("docdt"));
            holder.list_d3.setText(attendance_list.get(position).get("CASH_WITHDRAWAL"));
            holder.list_d4.setText(attendance_list.get(position).get("CARD_PAYMENT"));
            holder.list_d5.setText(attendance_list.get(position).get("EXP_AMOUNT"));
            holder.list_d6.setText(attendance_list.get(position).get("SALE_AMOUNT_FOOD"));
            holder.list_d7.setText(attendance_list.get(position).get("SALE_AMOUNT_LIQR"));
            holder.list_d8.setText(attendance_list.get(position).get("PUR_AMOUNT_OTHER"));
            holder.list_d9.setText(attendance_list.get(position).get("PUR_AMOUNT_LIQR"));
            holder.list_d10.setText(attendance_list.get(position).get("CHALLAN_AMOUNT"));

        }

        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3, list_d4, list_d5,list_d6,list_d7,list_d8,list_d9,list_d10;
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
                this.list_d7 = (TextView) itemView.findViewById(R.id.list_d7);
                this.list_d8 = (TextView) itemView.findViewById(R.id.list_d8);
                this.list_d9 = (TextView) itemView.findViewById(R.id.list_d9);
                this.list_d10 = (TextView) itemView.findViewById(R.id.list_d10);

            }
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
