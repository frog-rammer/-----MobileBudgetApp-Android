package com.example.mobileproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.TabActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CalendarActivity extends AppCompatActivity {

    // Calendar 관련
    TextView calHeaderText; // cal 년월 뷰
    LocalDate selectedDate; // 년월 변수
    RecyclerView recyclerView; // calDate,income expenditure per date 뷰

    // TODO:
    //  가지고 있는   총 액수 DB -> setText
    TextView totalAmount;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        // 연결 부
        calHeaderText = findViewById(R.id.calHeaderText);
        ImageButton prevBtn = findViewById(R.id.calbtn_back);
        ImageButton nextBtn = findViewById(R.id.calbtn_next);
        recyclerView = findViewById(R.id.calendar_date);
        totalAmount = findViewById(R.id.total_amount);



        SharedPreferences sharedPreferences = MainActivity.mContext.getSharedPreferences("sharedPreferences", 0);
        int total_now = sharedPreferences.getInt("today_income", 0) - sharedPreferences.getInt("today_expense", 0);
        totalAmount.setText(String.valueOf(total_now) + " 원");
        selectedDate = LocalDate.now();
        setMonthView();  // 달력 갱신

        // 이전 달 버튼 onclick
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //현재 월-1 변수에 담기
                selectedDate = selectedDate.minusMonths(1);
                setMonthView();
            }
        });
        // 다음달 버튼 onclick
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //현재 월+1 변수에 담기
                selectedDate = selectedDate.plusMonths(1);
                setMonthView();
            }
        });

    }// --------- OnCreate End


    // cal header text 포멧 설정
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String calHeaderFromDate(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월");

        return  date.format(formatter);
    }

    // !!! 달력 갱신 !!!
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        // 캘린더 헤더 text
        calHeaderText.setText(calHeaderFromDate(selectedDate));

        // DayList 클래스에 월별 일자, 오늘 날짜 담기
        DayList dl=new DayList();
        dl.dayList = daysInMonthArray(selectedDate);
        dl.targetDay = LocalDate.now();

        // 어댑터에 정보 던지기
        CalendarAdapter adapter = new CalendarAdapter(dl);

        //레이아웃 설정( 열 7개)
        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);

        //레이아웃 적용
        recyclerView.setLayoutManager(manager);

        //어뎁터 적용
        recyclerView.setAdapter(adapter);
    }

    // 해당 월별 날짜 리스트
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<LocalDate> daysInMonthArray(LocalDate date){

        ArrayList<LocalDate> dayList = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);

        //해당 월 마지막 날짜 가져오기
        int lastDay = yearMonth.lengthOfMonth();

        //해당 월의 첫번째 날 가져오기
        LocalDate firstDay = selectedDate.withDayOfMonth(1);

        //첫번째 날 요일 가져오기 (월: 1 , 일: 7)
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        for(int i = 1; i < 42; i++){
            if(i <= dayOfWeek || i > lastDay + dayOfWeek){
                dayList.add(null);
            }else{
                dayList.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }
        return dayList;
    }


} // --------- CalendarActivity End



/////
///// @RequiresApi(api = Build.VERSION_CODES.O) -> 지정한 API 보다 낮은 API에서 함수를 호출할 경우 에러가 발생 (최소 api 레벨 보다 아래 api에서 실행시 오류)
//////////