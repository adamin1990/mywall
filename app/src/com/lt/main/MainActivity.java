package com.lt.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lt.fragment.LocalGridFragment;
import com.lt.secondwallpaper.R;

public class MainActivity extends SherlockFragmentActivity {

	private static final int CONTENT_VIEW_ID = 666;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
//		LocalGridFragment mainfragment=new LocalGridFragment();
//		getSupportFragmentManager().beginTransaction().replace(R.id.mainac,mainfragment ).commit();
		FrameLayout frame = new FrameLayout(this);
		frame.setId(CONTENT_VIEW_ID);
		setContentView(frame, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		if (savedInstanceState == null) {
			setInitialFragment();
		}
	}


	private void setInitialFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		LocalGridFragment loc=new LocalGridFragment();
		fragmentTransaction.add(CONTENT_VIEW_ID, MainFragment.newInstance())
				.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Dialog alertDialog = new AlertDialog.Builder(this)
					.setTitle(this.getResources().getString(R.string.dear))
					.setMessage(this.getResources().getString(R.string.notice))
					.setIcon(R.drawable.ic_launcher)
					.setPositiveButton(
							this.getResources().getString(R.string.rotate),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									// 在自己应用中直接连接到应用在android应用市场的位置去评价
									String str = "market://details?id="
											+ getPackageName();
									Intent localIntent = new Intent(
											"android.intent.action.VIEW");
									localIntent.setData(Uri.parse(str));
									try {
										startActivity(localIntent);
									} catch (ActivityNotFoundException e) {
										Intent intent = new Intent(
												Intent.ACTION_VIEW);
										intent.setData(Uri
												.parse("https://play.google.com/store/apps/details?id="
														+ getPackageName()));
										startActivity(Intent.createChooser(
												intent, getTitle()));
									}

								}
							})
					.setNegativeButton(
							this.getResources().getString(R.string.exit),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									finish();
									System.exit(0);
								}
							}).create();
			alertDialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

}
