package wadotu.orthodoxy.ortholife;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CalendarUpdateWorker extends Worker {

    public CalendarUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        
        CompletableFuture<Result> future = new CompletableFuture<>();

        OrthoCalendar.getCalendarInfo(context, new OrthoCalendar.CalendarCallback() {
            @Override
            public void onDataLoaded(OrthoCalendar.CalendarInfo info) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, OrthoCalendarWidget.class));
                
                RemoteViews views = OrthoCalendarWidget.createRemoteViews(context, info);
                for (int id : ids) {
                    appWidgetManager.updateAppWidget(id, views);
                }
                future.complete(Result.success());
            }

            @Override
            public void onError(String message) {
                // Optionally update widget with error state
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, OrthoCalendarWidget.class));
                RemoteViews errorViews = OrthoCalendarWidget.createRemoteViews(context, null);
                for (int id : ids) {
                    appWidgetManager.updateAppWidget(id, errorViews);
                }
                future.complete(Result.retry());
            }
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            return Result.failure();
        }
    }
}
