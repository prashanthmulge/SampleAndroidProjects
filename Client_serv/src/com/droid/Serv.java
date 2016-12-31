package com.droid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.SlidingDrawer;

public class Serv extends Activity implements Runnable {
	
	public void run(){
	String url = "http://192.168.0.115/test.php";  
	Intent i = new Intent(Intent.ACTION_VIEW);  
	i.setData(Uri.parse(url));  
	startActivity(i);  
	
	}
}
