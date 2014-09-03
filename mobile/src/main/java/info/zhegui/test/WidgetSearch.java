package info.zhegui.test;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 */
public class WidgetSearch extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        Intent intent = new Intent(context, ActivityMain.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentVoice = new Intent(context, ActivityMain.class);
        intentVoice.putExtra("voice",true);
        PendingIntent pendingIntentVoice = PendingIntent.getActivity(context, 1, intentVoice, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_search);
        views.setOnClickPendingIntent(R.id.ibtn_mike, pendingIntentVoice);
        views.setOnClickPendingIntent(R.id.tv_keyword, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }
}


