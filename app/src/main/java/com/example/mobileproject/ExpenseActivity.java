package com.example.mobileproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseActivity extends AppCompatActivity {
    LinearLayout layoutDate;
    Button btnCategory;
    ImageButton daybtn_back, daybtn_next;
    TextView txtDate,txtTotal;
    ListView listExpense;
    Dialog category;
    int mYear,mMonth,mDay;
    String selected_category = "%";
    String date;
    int selected_index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        layoutDate =(LinearLayout) findViewById(R.id.layoutDate);

        daybtn_back = (ImageButton) findViewById(R.id.daybtn_back);
        daybtn_next = (ImageButton) findViewById(R.id.daybtn_next);

        btnCategory = (Button) findViewById(R.id.btnCategory);
        txtDate = (TextView) findViewById(R.id.txtDate);

        txtTotal = (TextView) findViewById(R.id.txtTotal);

        listExpense = (ListView) findViewById(R.id.listExpense);

        //날짜 txtView 설정
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy월 MM월 DD일");
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        //오늘날짜

        txtDate.setText(mYear + "년" + mMonth + "월" + mDay +"일");
        date = mYear + "-" + mMonth + "-" + mDay;

        //수입 리스트 -----DB에서
        final ArrayList<String> expenseList = new ArrayList<String>();
        ListView list = findViewById(R.id.listExpense);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,expenseList);
        list.setAdapter(adapter);
        Cursor cursor = ((MainActivity)MainActivity.mContext).ExpenseList();

        while(cursor.moveToNext()) {
            String string = cursor.getString(1) + "\t\t\t\t\t" + cursor.getString(0) + "원\t\t\t\t\t" + cursor.getString(2);
            expenseList.add(string);
            adapter.notifyDataSetChanged();
        }

        // 1일 -  달력에서 날짜 선택 후 거기서 +-가 안됨..
        daybtn_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                c.add(Calendar.DAY_OF_MONTH, -1);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH) + 1;
                mDay = c.get(Calendar.DAY_OF_MONTH);
                txtDate.setText(mYear + "년" + (mMonth) + "월" + mDay +"일");

                String date = mYear + "-" + mMonth + "-" + mDay;
                totalupdate(date);
                update(date, expenseList, adapter, selected_category);
            }
        });

        // 1일 +
        daybtn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                c.add(Calendar.DAY_OF_MONTH,1);
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH) + 1;
                mDay = c.get(Calendar.DAY_OF_MONTH);
                txtDate.setText(mYear + "년" + (mMonth) + "월" + mDay +"일");

                date = mYear + "-" + mMonth + "-" + mDay;
                totalupdate(date);
                update(date, expenseList, adapter, selected_category);
            }
        });

        //달력에서 날짜 선택
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mYear = year;
                mMonth = month + 1;
                mDay = dayOfMonth;
                txtDate.setText(mYear +"년" + mMonth + "월" + mDay +"일");

                date = mYear + "-" + mMonth + "-" + mDay;

                totalupdate(date);
                update(date, expenseList, adapter, selected_category);
            }
        }, mYear, mMonth, mDay);

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtDate.isClickable()) {
                    datePickerDialog.show();
                }
            }
        });

        //txtTotal.setText(그날 총 수입)  ------------DB
        totalupdate();

        //카테고리 선택
        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategory(expenseList, adapter);}
        });

    }
    private void showCategory(ArrayList expenseList, ArrayAdapter adapter){
        AlertDialog.Builder dlg_category = new AlertDialog.Builder(this);
        LayoutInflater inflater = (this).getLayoutInflater();
        View dialogView= inflater.inflate(R.layout.dialog_expense_add, null);
        dlg_category.setView(dialogView);
        //data source
        final CharSequence[] categoryList = {"전체","월급","용돈","공모전 상금","복권","상여금","기타"};
        dlg_category.setTitle("지출 입력");
        dlg_category.setSingleChoiceItems(categoryList, selected_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //categoryList[which] 가 선택된 카테고리.
                selected_index = which;

                switch (which) {
                    case 0: selected_category = "%"; break;
                    case 1: selected_category = "salary"; break;
                    case 2: selected_category = "pin"; break;
                    case 3: selected_category = "prize"; break;
                    case 4: selected_category = "lottery"; break;
                    case 5: selected_category = "bonus"; break;
                    case 6: selected_category = "etc"; break;
                }
            }
        });
        final AlertDialog ad = dlg_category.show();
        dialogView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update(date, expenseList, adapter, selected_category);;
                ad.dismiss();
            }
        });
    }

    public void totalupdate() {
        ((MainActivity)MainActivity.mContext).TotalExpense();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", 0);
        txtTotal.setText(String.valueOf(sharedPreferences.getInt("today_expense", 0)));
    }

    public void totalupdate(String date) {
        ((MainActivity)MainActivity.mContext).TotalExpense(date);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", 0);
        txtTotal.setText(String.valueOf(sharedPreferences.getInt("today_expense", 0)));
    }

    public void update(String date, ArrayList expenseList, ArrayAdapter adapter, String item) {
        Cursor cursor = ((MainActivity)MainActivity.mContext).ExpenseList(date, item);

        expenseList.clear();
        adapter.notifyDataSetChanged();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String string = cursor.getString(1) + "\t\t\t\t\t" + cursor.getString(0) + "원\t\t\t\t\t" + cursor.getString(2);
                expenseList.add(string);
                adapter.notifyDataSetChanged();
            }
        }
    }
}