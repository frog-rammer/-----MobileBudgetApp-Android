package com.example.mobileproject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private static final String ACTION_BTN = "ButtonClick";

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences", 0);

        views.setTextViewText(R.id.widget_monthly_income, String.valueOf(sharedPreferences.getInt("income", 0)));
        views.setTextViewText(R.id.widget_monthly_expense, String.valueOf(sharedPreferences.getInt("monthly_expense", 0)));
        views.setTextViewText(R.id.widget_today_expense, String.valueOf(sharedPreferences.getInt("today_expense", 0)));
        views.setTextViewText(R.id.widget_refresh_time, date.format(cal.getTime()));

        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(ACTION_BTN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        views.setOnClickPendingIntent(R.id.widget_refresh_button, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(ACTION_BTN)){
            ((MainActivity)MainActivity.mContext).WidgetUpdate();
            this.onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NewAppWidget.class)));
        }else {
            super.onReceive(context, intent);
        }
    }
}