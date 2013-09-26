package com.bluehorn.diamondfusion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
 private final int SPLASH_DISPLAY_LENGHT = 2000;
  @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		 new Handler().postDelayed(new Runnable(){
	            @Override
	            public void run() {
	                /* Create an Intent that will start the Menu-Activity. */
	                Intent mainIntent = new Intent(SplashScreen.this,FusionMain.class);
	                SplashScreen.this.startActivity(mainIntent);
	                SplashScreen.this.finish();
	                overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
	            }
	        }, SPLASH_DISPLAY_LENGHT);
	}
}
