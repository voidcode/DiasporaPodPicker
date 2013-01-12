package com.voidcode.diaspora_podpicker;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;

public class SetLuncherActivity extends Activity {
	private SharedPreferences prefs;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	 	setContentView(R.layout.setluncher);
		prefs = getSharedPreferences("settings",0);
	}
	public void updateLuncher(int width, int heigth, int luncherR, String suffix)
	{
		//save 'luncherR' and 'suffix' in prefs
		Editor editor = prefs.edit();
		editor.putInt("luncherR", luncherR);
		editor.putInt("luncherWidth", width);
		editor.putInt("luncherHeigth", heigth);
		editor.putString("suffix", suffix);
		editor.commit();
		
		//update dpp-widget
    	Intent intent = new Intent(this, DppWidget.class);
    	intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
    	int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), DppWidget.class));
    	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
    	sendBroadcast(intent);
    	
    	//kill this acticity
    	this.finish();
	}
	public void updateluncher_ib_stream_click(View v)
	{
		updateLuncher(40,35, R.drawable.stream, "/stream");
	}
	public void updateluncher_ib_activity_click(View v)
	{
		updateLuncher(40, 40, R.drawable.my_activity, "/activity");
	}
	public void updateluncher_ib_notifications_click(View v)
	{
		updateLuncher(40, 40, R.drawable.notifications_grey, "/notifications");
	}
	public void updateluncher_ib_conversations_click(View v)
	{
		updateLuncher(40, 26, R.drawable.conversations, "/conversations");
	}
	public void updateluncher_ib_people_click(View v)
	{
		updateLuncher(40, 40, R.drawable.search, "/people");
	}
	public void updateluncher_ib_contacts_click(View v)
	{
		updateLuncher(40, 40, R.drawable.contacts, "/contacts");
	}
	public void updateluncher_ib_compose_click(View v)// ----> "/status_messages/new"
	{
		updateLuncher(40, 40, R.drawable.compose, "/status_messages/new");
	}
	
}