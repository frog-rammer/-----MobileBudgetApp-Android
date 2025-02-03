package com.example.mobileproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends TabActivity {
    public static Context mContext;

    myDBHelper myHelper;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        myHelper = new myDBHelper(this);

        checkDangerousPermissions();

        // TODO : test data set add
        sqlDB = myHelper.getWritableDatabase();
        /*
        LocalDate Month = LocalDate.now();
        YearMonth yearMonth;
        int inamount = 100;
        int examount = 100;
        String dates;
        for(int i = 0 ; i <12; i++ ){
            Month = LocalDate.of(Month.getYear(),i+1,Month.getDayOfMonth());
            yearMonth = YearMonth.from(Month);
            int lastDay = yearMonth.lengthOfMonth();
            for(int j = 0 ; j <lastDay; j++){
                dates = Month.getYear() + "-" + i+1 + "-" + j+1;
                sqlDB.execSQL("insert into Income (amount, time) values( ?, ?);", new String[]{ String.valueOf(inamount), dates});
                sqlDB.execSQL("insert into Expense (amount, time) values(?, ?);", new String[]{ String.valueOf(examount), dates});
                inamount++;
                examount++;
            }
        }
        sqlDB.close();
        */
        // ----------Test set  END


        FloatingActionButton fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddIncome();
            }
        });

        TabHost tabHost = getTabHost();

        TabHost.TabSpec tabSpecIncome = tabHost.newTabSpec("Income").setIndicator("수입");
        tabSpecIncome.setContent(new Intent(this, IncomeActivity.class));
        tabHost.addTab(tabSpecIncome);

        TabHost.TabSpec tabSpecExpense = tabHost.newTabSpec("Income").setIndicator("지출");
        tabSpecExpense.setContent(new Intent(this, ExpenseActivity.class));
        tabHost.addTab(tabSpecExpense);

        TabHost.TabSpec tabSpecCalendar = tabHost.newTabSpec("Income").setIndicator("달력");
        tabSpecCalendar.setContent(new Intent(this, CalendarActivity.class));
        tabHost.addTab(tabSpecCalendar);

        TabHost.TabSpec tabSpecStatistics = tabHost.newTabSpec("Income").setIndicator("통계");
        tabSpecStatistics.setContent(new Intent(this, StatisticsActivity.class));
        tabHost.addTab(tabSpecStatistics);

        tabHost.setCurrentTab(0);
    }

    public void WidgetUpdate() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat Month = new SimpleDateFormat("yyyy-MM%", Locale.KOREA);
        SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd%", Locale.KOREA);

        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor1, cursor2, cursor3;

        cursor1 = sqlDB.rawQuery("select sum(amount) from income where time like ?;", new String[]{ Month.format(calendar.getTime())});
        cursor1.moveToNext();
        editor.putInt("income", cursor1.getInt(0));
        cursor1.close();

        cursor2 = sqlDB.rawQuery("select sum(amount) from expense where time like ?;", new String[]{Month.format(calendar.getTime())});
        cursor2.moveToNext();
        editor.putInt("monthly_expense", cursor2.getInt(0));
        cursor2.close();

        cursor3 = sqlDB.rawQuery("select sum(amount) from expense where time like ?;", new String[]{Date.format(calendar.getTime())});
        cursor3.moveToNext();
        editor.putInt("today_expense", cursor3.getInt(0));
        cursor3.close();
        sqlDB.close();

        editor.apply();
    }

    public void SMSInsert(String contents) {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        String dates = year + "-" + month + "-" + date;

        int amount = Extract_amount(contents);
        String memo = Extract_memo(contents);

        sqlDB = myHelper.getWritableDatabase();
        sqlDB.execSQL("insert into Expense (memo, amount, time) values(?, ?, ?);", new String[]{memo, String.valueOf(amount), dates});
        sqlDB.close();
    }

    public int Extract_amount (String contents) {

        int amount = 0;
        int index = contents.indexOf(":") + 4;

        String tmp = contents.substring(index);
        index = tmp.indexOf("원");
        tmp = tmp.substring(0, index);
        tmp = tmp.replaceAll(",","");
        amount = Integer.parseInt(tmp);

        return amount;
    }

    public String Extract_memo (String contents) {

        int index = contents.indexOf("원")+2;

        String tmp = contents.substring(index);
        index = tmp.indexOf("사용");
        tmp = tmp.substring(0, index);

        return tmp;
    }

    public void TotalIncome() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd%", Locale.KOREA);

        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;

        cursor = sqlDB.rawQuery("select sum(amount) from income where time like ?;", new String[]{Date.format(calendar.getTime())});
        cursor.moveToNext();
        editor.putInt("today_income", cursor.getInt(0));
        cursor.close();
        sqlDB.close();

        editor.apply();
    }

    public void TotalIncome(String date) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;

        cursor = sqlDB.rawQuery("select sum(amount) from income where time like ?;", new String[]{date});
        cursor.moveToNext();
        editor.putInt("today_income", cursor.getInt(0));
        cursor.close();
        sqlDB.close();

        editor.apply();
    }

    public void TotalExpense() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd%", Locale.KOREA);

        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;

        cursor = sqlDB.rawQuery("select sum(amount) from expense where time like ?;", new String[]{Date.format(calendar.getTime())});
        cursor.moveToNext();
        editor.putInt("today_expense", cursor.getInt(0));
        cursor.close();
        sqlDB.close();

        editor.apply();
    }

    public void TotalExpense(String date) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;

        cursor = sqlDB.rawQuery("select sum(amount) from expense where time like ?;", new String[]{date});
        cursor.moveToNext();
        editor.putInt("today_expense", cursor.getInt(0));
        cursor.close();
        sqlDB.close();

        editor.apply();
    }

    public Cursor IncomeList() {
        Cursor cursor;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        sqlDB = myHelper.getReadableDatabase();
        cursor = sqlDB.rawQuery("select amount, time, memo from income where time like ?;", new String[]{Date.format(calendar.getTime())});

        return cursor;
    }

    public Cursor IncomeList(String date, String item) {
        Cursor cursor;

        sqlDB = myHelper.getReadableDatabase();

//        try {
            cursor = sqlDB.rawQuery("select amount, time, memo from income where time like ? and category like ?;", new String[]{date, item});
//        } catch (Exception e) {
//            cursor = null;
//        }

        return cursor;
    }
    public Cursor ExpenseList() {
        Cursor cursor;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat Date = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        sqlDB = myHelper.getReadableDatabase();
        cursor = sqlDB.rawQuery("select amount, time, memo from expense where time like ?;", new String[]{Date.format(calendar.getTime())});

        return cursor;
    }

    public Cursor ExpenseList(String date, String item) {
        Cursor cursor;

        sqlDB = myHelper.getReadableDatabase();

//        try {
        cursor = sqlDB.rawQuery("select amount, time, memo from expense where time like ? and category like ?;", new String[]{date, item});
//        } catch (Exception e) {
//            cursor = null;
//        }

        return cursor;
    }

    public static class myDBHelper extends SQLiteOpenHelper {

        public myDBHelper(Context context) {
            super(context, "groupDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table Expense ( id integer not null primary key autoincrement," +
                    " category varchar(255), " +
                    "amount integer not null, " +
                    "memo varchar (255)," +
                    "time varchar(255) not null);");
            sqLiteDatabase.execSQL("create table Income ( id integer not null primary key autoincrement," +
                    " amount integer not null," +
                    "category varchar(255) not null," +
                    "memo varchar (255)," +
                    "time varchar(255) not null);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("drop table if exists Expense");
            sqLiteDatabase.execSQL("drop table if exists Income");
            onCreate(sqLiteDatabase);
        }
    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.RECEIVE_SMS
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
            }
        }
    }

    private void showAddIncome(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_income_add);

        final EditText dateIncome =dialog.findViewById(R.id.dateIncome);
        final EditText priceIncome =dialog.findViewById(R.id.priceIncome);
        final EditText memoIncome =dialog.findViewById(R.id.memoIncome);
        Spinner spinnerCategory = dialog.findViewById(R.id.spinnerCategory);
        Button btnOk =dialog.findViewById(R.id.btnOk);
        Button btnCalcel=dialog.findViewById(R.id.btnCalcel);

        String[] items = {"월급","용돈","공모전 상금","복권","상여금","기타"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //API에 만들어져 있는 R.layout.simple_spinner...를 씀
                this,android.R.layout.simple_spinner_item, items
        );
        //미리 정의된 레이아웃 사용
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // 스피너 객체에다가 어댑터를 넣어줌
        spinnerCategory.setAdapter(adapter);

        btnOk.setOnClickListener((view -> {
            String date = dateIncome.getText().toString();
            String price = priceIncome.getText().toString();
            String memo = memoIncome.getText().toString();
            String category = null;
            switch(spinnerCategory.getSelectedItem().toString()) {
                case "월급": category = "salary"; break;
                case "용돈": category = "pin"; break;
                case "공모전 상금": category = "prize"; break;
                case "복권": category = "lottery"; break;
                case "상여금": category = "bonus"; break;
                case "기타": category = "etc"; break;
            }

            //-------------------------------------DB저장----------------------------------!!!!
            sqlDB = myHelper.getWritableDatabase();
            sqlDB.execSQL("insert into income (amount, category, memo, time) values ('" + price + "', '" + category + "', '" + memo + "', '" + date + "');");
            sqlDB.close();
            dialog.dismiss();

        }));

        btnCalcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}