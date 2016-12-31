package a.b;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class UseGpsActivity extends Activity {
    /** Called when the activity is first created. */
	   private WifiManager Wifi;
	  
TextView t=(TextView)findViewById(R.id.text);
		BroadcastReceiver receiver;
	
		/** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        Wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	        
	        WifiInfo info = Wifi.getConnectionInfo();
			//sig.append("\n\nWiFi Status: " + info.toString());
	      t.setText("");
	        if(!Wifi.isWifiEnabled()){
	        	
	        	                if(Wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING){
	        	
	        	                        Wifi.setWifiEnabled(true);
	        	
	        	                }
	        	
	        	        }
	        
	        registerReceiver(new BroadcastReceiver(){

					@Override
					public void onReceive(Context arg0, Intent arg1) {
						// TODO Auto-generated method stub
						 WifiInfo info = Wifi.getConnectionInfo();
						t.append(info.getRssi()+"");
					}
	        		        	 
	        		        	        }, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));


	    }
}