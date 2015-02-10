package com.lt.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.WallpaperManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.lt.secondwallpaper.R;
import com.polites.android.GestureImageView;

public class AppPagerActivity extends SherlockFragmentActivity {
	private ViewPager viewpager;
	private ImageButton leftbtn;
	private ImageButton rightbtn;
	private ImageButton setbtn;
	private ArrayList<String> imageurls;
	private int pagerposition;
	AssetManager assetManager;
	private AdView adview;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// 无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_pager);
		/**
		 * 广告加入
		 */
		assetManager = getAssets();
		adview = new AdView(getApplicationContext());
		adview.setAdUnitId(getResources().getString(R.string.ad_unit_id));
		adview.setAdSize(AdSize.BANNER);
		LinearLayout adlinearlayout = (LinearLayout) findViewById(R.id.appadlaout6);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		adlinearlayout.addView(adview, params);
		adview.loadAd(new AdRequest.Builder().build());
		adview.setAdListener(new AdListener() {
		});
		/**
		 * 通过Bundle获取Activity传递的数据
		 */
		Bundle bundle = getIntent().getExtras();
		imageurls = bundle.getStringArrayList("list");
		pagerposition = bundle.getInt("position", 0);

		//
		viewpager = (ViewPager) findViewById(R.id.apppaper);
		viewpager.setAdapter(new AppImagePagerAdapter(imageurls));
		viewpager.setCurrentItem(pagerposition);
		/**
		 * 针对按钮的事件
		 */
		leftbtn = (ImageButton) findViewById(R.id.appleft);
		leftbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewpager.arrowScroll(v.FOCUS_LEFT);
				pagerposition=pagerposition-1;
				if(pagerposition<=0){
					pagerposition=0;
					viewpager.setCurrentItem(0);
					Toast.makeText(getApplicationContext(), "NoPictures", 500).show();
				}else{
						viewpager.setCurrentItem(pagerposition,true);
						}

			}
		});
		//触碰改变
		leftbtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.left1);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.left);
				}
				return false;
			}
		});
		setbtn = (ImageButton) findViewById(R.id.appsetwall);
		//设置壁纸
		setbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						InputStream is=null;
						Looper.prepare();
						try {
							is=assetManager.open(imageurls.get(pagerposition));
							Bitmap bitmap=BitmapFactory.decodeStream(is);
							is.close();
							WallpaperManager wpm=WallpaperManager.getInstance(getApplicationContext());
							wpm.setBitmap(bitmap);
							Toast.makeText(getApplicationContext(), "SetSuccess!", 1000).show();
						} catch (IOException e) {
							e.printStackTrace();
						}
						Looper.loop();
					}
				}).start();

			}
		});
		setbtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.ses1);
				}else if(event.getAction()==MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.ses);
				}
				return false;
			}
		});
		rightbtn = (ImageButton) findViewById(R.id.appright);
		rightbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewpager.arrowScroll(v.FOCUS_RIGHT);
				pagerposition++;
				if(pagerposition>=0&&pagerposition<=imageurls.size()-1){
					viewpager.setCurrentItem(pagerposition);
				}else if(pagerposition>imageurls.size()-1){
					pagerposition=imageurls.size()-1;
					Toast.makeText(getApplicationContext(), "NoPictures", 1000).show();
				}
			}
		});
		rightbtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					v.setBackgroundResource(R.drawable.right1);
				}
				else if(event.getAction()==MotionEvent.ACTION_UP){
					v.setBackgroundResource(R.drawable.right);
				}
				return false;
			}
		});

	}

	/**
	 * ViewPager的适配器 读取并显示assets下图片
	 * 
	 * @author adamin
	 * 
	 */
	class AppImagePagerAdapter extends PagerAdapter {
		private ArrayList<String> images;

		public AppImagePagerAdapter(ArrayList<String> images) {
			super();
			this.images = images;
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			View view = View.inflate(getApplicationContext(),
					R.layout.app_pager_item, null);
			final GestureImageView imageview = (GestureImageView) view
					.findViewById(R.id.appimage);
			if (images.get(position) == null) {
				position = 0;
			}
			/**
			 * 根据文件名先将其转换成流 再转换成bitmap形式 再设计到gestureiamgeview 关闭is；
			 */
			InputStream is = null;
			try {
				is = assetManager.open(images.get(position));
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				imageview.setImageBitmap(bitmap);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			((ViewPager) container).addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

	}

}
