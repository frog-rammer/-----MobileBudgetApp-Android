package com.example.mobileproject;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    ArrayList<LocalDate> dayList;
    LocalDate today;
    String date = "0";
    int incomeTmp = 0;
    int exTmp = 0;

    public CalendarAdapter(DayList dayList) {
        this.dayList = dayList.dayList;
        this.today = dayList.targetDay;
    }

    @NonNull
    @Override
    // 화면 연결
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.activity_calendar_item, parent, false);


        return new CalendarViewHolder(view);
    }


    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    // 데이터 연결
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        //날짜 변수에 담기
        LocalDate day = dayList.get(position);

        // ---- DB start
        // TODO: DB 에서 오늘 수입 지출 가져오기   ( Expendure 설정)
        if(day == null){
            holder.dayText.setText("");
        }else {
            date = day.getYear() + "-" + day.getMonthValue() + "-" + day.getDayOfMonth();
            ((MainActivity) MainActivity.mContext).TotalIncome(date);
            SharedPreferences sharedPreferences = MainActivity.mContext.getSharedPreferences("sharedPreferences", 0);
            incomeTmp = sharedPreferences.getInt("today_income", 0);

            ((MainActivity) MainActivity.mContext).TotalExpense(date);
            exTmp = sharedPreferences.getInt("today_expense", 0);

        }
        // -------------DB END

        if(day == null){
            holder.dayText.setText("");
        }else{
            //해당 일자를 넣는다.
            holder.dayText.setText(String.valueOf(day.getDayOfMonth()));
            // today bgcolor 변경
            if(day.equals(today)){
                holder.dayLayout.setBackgroundColor(Color.LTGRAY);
            }

            // 수입 지출 달력에 기록
            if(incomeTmp != 0){
                holder.incomeText.setText(""+incomeTmp);
                holder.incomeText.setVisibility(View.VISIBLE);
            }else{
                holder.incomeText.setVisibility(View.INVISIBLE);
            }
            if( exTmp != 0){
                holder.expenditureText.setText("-"+exTmp);
                holder.expenditureText.setVisibility(View.VISIBLE);
            }else {
                holder.expenditureText.setVisibility(View.INVISIBLE);
            }

        }

        if((position +1) % 7 == 0){// 토
            holder.dayText.setTextColor(Color.BLUE);
        }else if( position == 0 || position % 7 == 0){  //일
            holder.dayText.setTextColor(Color.RED);
        }
        //날짜 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int iYear = day.getYear(); //년
                int iMonth = day.getMonthValue();//월
                int iDay = day.getDayOfMonth();//일

                String yearMonDay = iYear + "년 " +iMonth + "월 " + iDay + "일"
                        + " 수입 : " +holder.incomeText.getText() +"원"
                        + " 지출 :" + holder.expenditureText.getText()+"원";
                //TODO:
                // 그날 수입 지출 리스트 캘린더 밑에 띄우기

                Toast.makeText(holder.itemView.getContext() , yearMonDay , Toast.LENGTH_SHORT).show();

            }

        });
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder{
        TextView dayText;
        TextView incomeText;
        TextView expenditureText;
        LinearLayout dayLayout;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            // 연결 부
            dayText = itemView.findViewById(R.id.dayText);
            incomeText =itemView.findViewById(R.id.incomeText);
            expenditureText = itemView.findViewById(R.id.expenditureText);
            dayLayout = itemView.findViewById(R.id.parentView);
        }
    }
}