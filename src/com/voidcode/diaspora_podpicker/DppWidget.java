package com.voidcode.diaspora_podpicker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

public class DppWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            
            // Create an Intent to launch defalut webbrowser base on currentpod 
            SharedPreferences prefs = context.getSharedPreferences("settings",0);  
            Intent openwwwIntent = new Intent(Intent.ACTION_VIEW);
            openwwwIntent.setData(Uri.parse("https://"+prefs.getString("currentpod", "joindiaspora.com")));//joindiaspora.com is the default-pod
            PendingIntent openwww_pendingIntent = PendingIntent.getActivity(context, 0, openwwwIntent, 0);
            
            // Create an Intent to launch PodSettingsActivity
            Intent PodSettingsIntent = new Intent(context, PodSettingsActivity.class);
            PendingIntent podsettings_pendingIntent = PendingIntent.getActivity(context, 0, PodSettingsIntent, 0);
            
            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.dpp_widget);
            views.setOnClickPendingIntent(R.id.widget_ib_openwww, openwww_pendingIntent);
            views.setOnClickPendingIntent(R.id.widget_ib_podsettings, podsettings_pendingIntent);          
            
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
