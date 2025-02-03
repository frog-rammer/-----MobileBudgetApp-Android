package com.example.mobileproject;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;

public class StatisticsActivity  extends AppCompatActivity {
    //선 그래프
    private BarChart barChart;

    Spinner monSpinner ;
    TextView exGraphText, totalnow,compExText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_statistics);
        barChart = (BarChart) findViewById(R.id.chart);
        exGraphText = (TextView)findViewById(R.id.exGraphText);
        monSpinner = (Spinner)findViewById(R.id.monSpinner);
        totalnow = findViewById(R.id.totalnow);
        compExText= findViewById(R.id.compExText);

        SharedPreferences sharedPreferences = MainActivity.mContext.getSharedPreferences("sharedPreferences", 0);

        // TODO : DB -> 지출 전월과  비교 밑 현재 잔고(총액 )조회 (완)
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        String date = year + "-" + month + "-%";

        ((MainActivity) MainActivity.mContext).TotalIncome(date);
        ((MainActivity) MainActivity.mContext).TotalExpense(date);

        int total_now = sharedPreferences.getInt("today_income", 0) - sharedPreferences.getInt("today_expense", 0);
        totalnow.setText(String.valueOf(total_now) + " 원");


        monSpinner.setSelection(c.get(Calendar.MONTH));

        monSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int mon, long l) {
                exGraphText.setText(adapterView.getItemAtPosition(mon) +"월 지출 그래프");


                ArrayList<BarEntry> entry_chart = new ArrayList<>(); // 데이터를 담을 Arraylist

                BarData barData = new BarData(); // 차트에 담길 데이터

                // TODO: 월 선택 란 만들기 (완)
                LocalDate Month = LocalDate.now();
                Month = LocalDate.of(Month.getYear(),mon+1,Month.getDayOfMonth());
                // 월 선택
                YearMonth yearMonth = YearMonth.from(Month);

                // 해당 월 마지막 날짜
                int lastDay = yearMonth.lengthOfMonth();

                // TODO: 달별 (완)
                //entry_chart1에 좌표 데이터를 담는다.
                String date;
                int exTmp;

                for(int i =  1; i  <=  lastDay ; i ++){
                    //TODO: Expenditure로 바꾸기 (완)
                    date = Month.getYear() + "-" + Month.getMonthValue() + "-" + i;
                    ((MainActivity) MainActivity.mContext).TotalExpense(date);
                    exTmp = sharedPreferences.getInt("today_expense", 0);
                    entry_chart.add(new BarEntry(i,exTmp));
                }
                BarDataSet barDataSet = new BarDataSet(entry_chart, "지출"); // 데이터가 담긴 Arraylist 를 BarDataSet 으로 변환한다.
                barDataSet.setColor(Color.RED); // 해당 BarDataSet 색 설정 :: 각 막대 과 관련된 세팅은 여기서 설정한다.
                barData.addDataSet(barDataSet); // 해당 BarDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.
                barChart.setData(barData); // 차트에 위의 DataSet 을 넣는다.
                barChart.invalidate(); // 차트 업데이트
                barChart.setTouchEnabled(false); // 차트 터치 비활성화


                // 전 월 대비 지출 비교 (완)
                int tmp = sharedPreferences.getInt("today_expense", 0);

                if(mon == 0) {
                    Month  = LocalDate.of(Month.getYear()-1,12 ,Month.getDayOfMonth());;
                }
                date = Month.getYear() + "-" +mon+ "-%";
                ((MainActivity) MainActivity.mContext).TotalExpense(date);
                int expense_gap = tmp - sharedPreferences.getInt("today_expense", 0);
                compExText.setText(String.valueOf(expense_gap)+" 원");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}
