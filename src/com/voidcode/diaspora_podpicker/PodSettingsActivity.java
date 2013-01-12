package com.voidcode.diaspora_podpicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class PodSettingsActivity extends Activity {
	public static final String SETTINGS_FILENAME="settings";
	public ListView lvPods;
	public String lvPods_arr[];
	private EditText editTextCurrentpod;
	JSONArray jsonArray;
	private AdView adView;
	private SharedPreferences prefs;
	private boolean OnlyHttpsPods = true;
	private String prefix;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.podsettings);
		prefs = getSharedPreferences("settings",0);
		
				try {
					//load pods from poduptime.me as a AsyncTask					
					lvPods_arr = new getPodlistTask(this).execute().get();

					//load elements from layout file podsettings.xml
					editTextCurrentpod = (EditText) findViewById(R.id.editText_currentpod);       
			        lvPods = (ListView) findViewById(R.id.listView_poduptime);
			              
			        
			        //show the currentpod to user
			        SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, 0);
			        editTextCurrentpod.setText(preferences.getString("currentpod", "joindiaspora.com"));
			        editTextCurrentpod.selectAll();
			        
			        //check if user have be get the dialog info box
			        if(preferences.getBoolean("has_show_dialog", false) == false)
			        {
				        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
					 	alert.setTitle(R.string.podsettings_dialog_title);
					 	alert.setMessage(R.string.podsettings_dialog_text);
						alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						});
						alert.show();
						//this ensure the user only see the dialogbox ones
						SharedPreferences.Editor editor = preferences.edit();
						editor.putBoolean("has_show_dialog", true);
						editor.commit();
			        }
			        
			        //fill listview with pods form http://podupti.me
			        fillListview(this.lvPods_arr);
			        
			        //podsearch, A fast find search on editTextCurrentpod, So user don�t have to scholl the podlist to finde a pod 
			        editTextCurrentpod.addTextChangedListener(new TextWatcher()
			        { 
			        	List<String> filter_podurl_list = null;
					    public void beforeTextChanged(CharSequence s, int start, int count, int after) 
					    {
					    }
					    public void onTextChanged(CharSequence s, int start, int before, int count) 
					    {
					    	filter_podurl_list=new ArrayList<String>();
					    	for(String podurl:lvPods_arr)
					    	{
						    	if(podurl.startsWith(s.toString()))
						    	{
						    		filter_podurl_list.add(podurl);
						    	}
					    	 }
					    }
						public void afterTextChanged(Editable s) 
						{
							///add reslut to listview
							if(!filter_podurl_list.equals(null))
								fillListview(filter_podurl_list.toArray(new String[filter_podurl_list.size()]));
						}
					});
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
		//AdMobil------------------
		// Create the adView
		adView = new AdView(this, AdSize.BANNER, "a150eed8a2a1ba0");

	    // Lookup your LinearLayout assuming it's been given
        // the attribute android:id="@+id/podsettings"
	    LinearLayout layout = (LinearLayout)findViewById(R.id.ll_joblist_admob);

	    
	    //TODO: REMOVE ME
		// Initiate a generic request to load it with an ad
	    AdRequest request = new AdRequest();
	    request.addTestDevice(AdRequest.TEST_EMULATOR);               // Emulator
	    request.addTestDevice("a150eed8a2a1ba0");                      // Test Android Device
		adView.loadAd(request);
	    
		
		// Add the adView to it
	    layout.addView(adView);

	    
    }
	public void fillListview(String _lvPods_arr[])
	{
        lvPods.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _lvPods_arr));
        lvPods.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				//onclick put select pod to editTextCurrentpod 	
			    editTextCurrentpod.setText(lvPods.getItemAtPosition(position).toString());
			    saveCurrentpodAndUpdateWidget();
			}
        });
	}
	//Screen orientation crashes app fix
	//http://jamesgiang.wordpress.com/2010/06/05/screen-orientation-crashes-my-app/
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
	} 
	// Handle the Back button
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg){
        if((keyCode == KeyEvent.KEYCODE_BACK))
        {
            this.finish();
        	return false;
        }
        else
            return true;
    }*/
    public void saveCurrentpodAndUpdateWidget()
    {
    	// Save the new currentpod
    	SharedPreferences preferences = getSharedPreferences(SETTINGS_FILENAME, 0);
    	SharedPreferences.Editor editor = preferences.edit();
    	editor.putString("currentpod", editTextCurrentpod.getText().toString());
    	editor.commit();		
    				
    	//update dpp-widget
    	Intent intent = new Intent(this, DppWidget.class);
    	intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
    	int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), DppWidget.class));
    	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
    	sendBroadcast(intent);
    }
	public void set_currentpod_click(View v) throws IOException
	{
		//get userinput
		String new_currentpod = editTextCurrentpod.getText().toString();
		//if user has added a pod
		if(new_currentpod.length()>0)
		{
			saveCurrentpodAndUpdateWidget();
			//open browser with, current Diaspora-pod and suffix
			Intent i = new Intent(Intent.ACTION_VIEW);
			
			prefix="https://";
			if(prefs.getInt("showAllPods", 0)==1)
				prefix="http://";
			
            i.setData(Uri.parse(prefix+new_currentpod+prefs.getString("suffix", "/stream")));
            startActivity(i);
		}
		else
		{
			Toast.makeText(getApplicationContext(), "You need to choose a pod", Toast.LENGTH_LONG).show();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_podsettings, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    	case R.id.menu_settings_setluncher:
	        	startActivity(new Intent(this, SetLuncherActivity.class));
	        break;
	    	case R.id.menu_settings_onlyhttpspods:
				Editor editor = prefs.edit();
				editor.putInt("showAllPods", 1);
				editor.commit();
			    this.finish();
			    startActivity(new Intent(this, PodSettingsActivity.class));
	        break;
	    }
	    return super.onOptionsItemSelected(item);
	}
}