package wadotu.orthodoxy.ortholife;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;

public class OrthoCalendarWidget extends AppWidgetProvider {

    public static final String ACTION_REFRESH = "wadotu.orthodoxy.ortholife.ACTION_REFRESH";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        enqueueUpdate(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_REFRESH.equals(intent.getAction())) {
            // 1. 사용자가 버튼을 누르면 즉시 "로딩 중" 표시 (시각적 피드백)
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, OrthoCalendarWidget.class);
            int[] ids = appWidgetManager.getAppWidgetIds(thisWidget);
            
            RemoteViews loadingViews = createRemoteViews(context, null);
            loadingViews.setTextViewText(R.id.widget_feast, context.getString(R.string.loading));
            for (int id : ids) {
                appWidgetManager.updateAppWidget(id, loadingViews);
            }

            // 2. 백그라운드 작업 요청
            enqueueUpdate(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            enqueueUpdate(context);
        }
    }

    private void enqueueUpdate(Context context) {
        // 네트워크가 연결된 상태에서만 실행되도록 제약 조건 추가
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // 즉시 실행(Expedited) 설정으로 앱이 종료된 상태에서도 최우선 실행 보장
        OneTimeWorkRequest updateRequest = new OneTimeWorkRequest.Builder(CalendarUpdateWorker.class)
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build();
                
        WorkManager.getInstance(context).enqueue(updateRequest);
    }

    public static RemoteViews createRemoteViews(Context context, OrthoCalendar.CalendarInfo info) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // 메인 앱 열기 인텐트
        android.app.PendingIntent mainPendingIntent = android.app.PendingIntent.getActivity(
                context, 0, new Intent(context, MainActivity.class), 
                android.app.PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, mainPendingIntent);

        // 새로고침 인텐트
        Intent refreshIntent = new Intent(context, OrthoCalendarWidget.class);
        refreshIntent.setAction(ACTION_REFRESH);
        android.app.PendingIntent refreshPendingIntent = android.app.PendingIntent.getBroadcast(
                context, 0, refreshIntent, 
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.btn_widget_refresh, refreshPendingIntent);

        if (info != null) {
            views.setTextViewText(R.id.widget_date, info.date);
            views.setTextViewText(R.id.widget_feast, info.feast);

            if (info.dailyVerse != null && !info.dailyVerse.isEmpty()) {
                views.setTextViewText(R.id.widget_reading, info.dailyVerse);
                views.setViewVisibility(R.id.widget_reading, android.view.View.VISIBLE);
            } else {
                views.setViewVisibility(R.id.widget_reading, android.view.View.GONE);
            }

            views.setTextViewText(R.id.widget_fasting, info.fasting);

            if (info.tone != null && !info.tone.isEmpty()) {
                views.setTextViewText(R.id.widget_tone, info.tone);
                views.setViewVisibility(R.id.widget_tone, android.view.View.VISIBLE);
            } else {
                views.setViewVisibility(R.id.widget_tone, android.view.View.GONE);
            }
        }
        return views;
    }
}
