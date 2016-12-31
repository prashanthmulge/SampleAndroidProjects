package com.droid;

//import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.R.id;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Client_servActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	JSONArray jArray;
	String result = null;
	InputStream is = null;
	StringBuilder sb=null;
	Button b;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	new Serv();
	 b= (Button)findViewById(R.id.but);
	 b.setOnClickListener(this);
	// b.setOnClickListener(this);

/*	String url = "http://192.168.0.115/test.php";  
	Intent i = new Intent(Intent.ACTION_VIEW);  
	i.setData(Uri.parse(url));  
	startActivity(i);  
	*/
	}
	/*
	public void call()
	{
	 ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	//http post
	try{
	     HttpClient httpclient = new DefaultHttpClient();
	     HttpPost httppost = new HttpPost("http://192.168.0.114/test.php");
	     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	     HttpResponse response = httpclient.execute(httppost);
	     HttpEntity entity = response.getEntity();
	     is = entity.getContent();
	     }catch(Exception e){
	         Log.e("log_tag", "Error in http connection"+e.toString());
	    }
	//convert response to string
	try{
	      BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
	       sb = new StringBuilder();
	       sb.append(reader.readLine() + "\n");

	       String line="0";
	       while ((line = reader.readLine()) != null) {
	                      sb.append(line + "\n");
	        }
	        is.close();
	        result=sb.toString();
	        }catch(Exception e){
	              Log.e("log_tag", "Error converting result "+e.toString());
	        }
	//paring data
	int ct_id;
	String ct_name;
	try{
	      jArray = new JSONArray(result);
	      JSONObject json_data=null;
	      for(int i=0;i<jArray.length();i++){
	             json_data = jArray.getJSONObject(i);
	             ct_id=json_data.getInt("CITY_CODE");
	             ct_name=json_data.getString("CITY_NAME");
	         }
	      }
	      catch(JSONException e1){
	    	  Toast.makeText(getBaseContext(), "No City Found" ,Toast.LENGTH_LONG).show();
	      } catch (ParseException e1) {
				e1.printStackTrace();
		}
	}
	*/
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show();
		Thread t=new Thread()
		{
			public void run()
			{
				String url = "http://192.168.0.115/test.php";  
				Intent i = new Intent(Intent.ACTION_VIEW);  
				i.setData(Uri.parse(url));  
				startActivity(i);
			}
		};
		t.start();
	
	/*	 new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				String url = "http://192.168.0.115/test.php";  
				Intent i = new Intent(Intent.ACTION_VIEW);  
				i.setData(Uri.parse(url));  
				startActivity(i); 	
			}
			 
		 }).start();*/
	}
	}

