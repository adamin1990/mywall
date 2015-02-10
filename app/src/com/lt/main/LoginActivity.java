package com.lt.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;

import com.lt.secondwallpaper.R;

public class LoginActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//ÉèÖÃÈ«ÆÁÏÔÊ¾
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
				setContentView(R.layout.loginlayout);
				new CountDownTimer(2000,1000) {
					
					@Override
					public void onTick(long millisUntilFinished) {
						
					}
					
					@Override
					public void onFinish() {
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, MainActivity.class);
						startActivity(intent);
						int VERSION=Integer.parseInt(android.os.Build.VERSION.SDK);
						if(VERSION >= 5){
							LoginActivity.this.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
						}
						LoginActivity.this.finish();						
					}
				}.start();
	}
	

}
