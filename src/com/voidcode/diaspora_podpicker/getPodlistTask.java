package com.voidcode.diaspora_podpicker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class getPodlistTask extends AsyncTask<Void,  Void, String[]> {
	protected Context context;
	private ProgressDialog dialog;
	private SharedPreferences prefs;
	
	public getPodlistTask(Context c)
	{
		context = c;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
        dialog.setMessage("Gets pods from poduptime.me ...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        prefs = context.getSharedPreferences("settings",0);  
	}
	protected void onPostExecute(String[] pods) {	 
		dialog.dismiss();
	}
	@Override
	protected String[] doInBackground(Void... params) {
		//simple json parsing, to retrieve list of pods from podupti.me
		//https://github.com/voidcode/Diaspora-Webclient/pull/4
		//by: https://github.com/vipulnsward
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			List<String> list = null;
			try {
				HttpGet httpGet = new HttpGet("http://podupti.me/api.php?key=4r45tg&format=json");
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					//TODO  Notify User about failure
				}
			} catch (ClientProtocolException e) {
				//TODO handle network unreachable exception here
				e.printStackTrace();
			} catch (IOException e) {
				//TODO handle json buggy feed
				e.printStackTrace();
			}
			//Parse the JSON Data
			try {
				JSONObject j=new JSONObject(builder.toString());			
				JSONArray jr=j.getJSONArray("pods");
				list=new ArrayList<String>();
				for (int i = 0; i < jr.length(); i++) {
					JSONObject jo = jr.getJSONObject(i);
					String secure=jo.getString("secure");
					
					if(prefs.getInt("showAllPods", 0)==1)//add all-pods to the list
					{
						list.add(jo.getString("domain"));
										
					}
					else//add only https-pods
					{
						if(secure.equals("true"))
							list.add(jo.getString("domain"));	
					}
				}
			}catch (Exception e) {
				//TODO Handle Parsing errors here	
				e.printStackTrace();
			}
		    return list.toArray(new String[list.size()]);
	}
	
}
