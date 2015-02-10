package com.lt.main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.lt.arclib.RayMenu;
import com.lt.bean.Picture;
import com.lt.secondwallpaper.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.polites.android.GestureImageView;

public class OnlineImageViewPagerActivity extends Activity{
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ViewPager pager;
	private ProgressDialog mSaveDialog = null; 
	private DisplayImageOptions options; 
	private ImageButton loadbutton;
	private ImageButton setwallbtn;
	private ImageButton leftbutton;
	private ImageButton rightbutton;
	private List<Picture> imageUrls ;
	private String fileName;  
	private int pagerPosition; 
	private final static String ALBUM_PATH  = Environment.getExternalStorageDirectory() + "/LovePaper/download/";  
	private AdView mAdView;  
	private boolean isloading = false;//判断是否加载中
	public ImagePagerAdapter ip;
	String page ;
	String cid ;
	private boolean ishas = true;
	boolean picloading = false;
	private static final int[] ITEM_DRAWABLES = { R.drawable.arcnext_1, R.drawable.arcnext,
		R.drawable.arcdownload, R.drawable.archome, R.drawable.arcshare2, R.drawable.arccomment };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//设置无标题
				requestWindowFeature(Window.FEATURE_NO_TITLE);
		//将系统时间作为文件名字  
		final DateFormat time=DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		pager= new ViewPager(this);
		setContentView(R.layout.ac_image_pager);  
		LinearLayout layout=(LinearLayout)findViewById(R.id.adlaout6);
		mAdView = new AdView(this);
        mAdView.setAdUnitId(getResources().getString(R.string.ad_unit_id));
        mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdListener(new ToastAdListener(this));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mAdView, params);
        mAdView.loadAd(new AdRequest.Builder().build());
//		adView=new AdView(this,AdSize.BANNER,"a1536a09095cdce");
//		adlayout.addView(adView);
//		AdRequest adRequest=new AdRequest();
//
//		adView.loadAd(adRequest);
        RayMenu rayMenu = (RayMenu) findViewById(R.id.ray_menu);
        final int itemCount = ITEM_DRAWABLES.length;
		for (int i = 0; i < itemCount; i++) {
			ImageView item = new ImageView(this);
			item.setImageResource(ITEM_DRAWABLES[i]);

			final int position = i;
			rayMenu.addItem(item, new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch(position){
					case 0:

						pager.arrowScroll(v.FOCUS_LEFT);
							System.out.println("左边position的位置："+pagerPosition);
							pagerPosition=pagerPosition-1;
							if(pagerPosition<=0)
							{
								pagerPosition=0;
								pager.setCurrentItem(0);
							}			 
							else{
								pager.setCurrentItem(pagerPosition,true);
							}
					break;
					case 1:

						pager.arrowScroll(v.FOCUS_RIGHT);
						
						//pager.setCurrentItem(pagerPosition-1,true);
						pagerPosition++;
						System.out.println("右边position的位置："+pagerPosition);
						 if(pagerPosition>=0&&pagerPosition<=imageUrls.size()-1){
							 pager.setCurrentItem(pagerPosition);	
						}
						else if(pagerPosition>imageUrls.size()-1){
							getMoreData();
						} 
					break;
					case 2:

//						Toast.makeText(ImagePagerActivity.this, "loading..", 2000).show();
						mSaveDialog = ProgressDialog.show(OnlineImageViewPagerActivity.this, "loading", "loading，wait...", true);
					   new Thread(new Runnable() {
						@Override
						public void run() {
							Looper.prepare();
		                	try {
		                		picloading = true;
		                		String addr ;
		            			if(imageUrls.get(pagerPosition)==null){
		            				addr = "";
		            			}else{
		            				addr=imageUrls.get(pagerPosition).getPaddr();
		            			}
		        				URL	url = new URL(addr);
		        					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		        	                conn.connect();
		                            conn.setConnectTimeout(5000);
		        	                InputStream in = conn.getInputStream();
		        	                Bitmap map = BitmapFactory.decodeStream(in);
		        	                File dirFile = new File(ALBUM_PATH);  
		        	                if(!dirFile.exists()){  
		        	                    dirFile.mkdir();  
		        	                }
		        	                Date now =new Date(); 
		        	        		fileName=time.format(now)+".jpg";
		        	                File myCaptureFile = new File(ALBUM_PATH + fileName);  
		        	                Toast.makeText(OnlineImageViewPagerActivity.this, "load success!", Toast.LENGTH_LONG).show();
		        	                mSaveDialog.dismiss();
		        	                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
		        	                map.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
		        	                bos.flush();  
		        	                bos.close();  
		        	                }
		                	catch (MalformedURLException e) {
		    					e.printStackTrace();
		    				} catch (IOException e) {
		    					e.printStackTrace(); 
		    				}
		                	Looper.loop();
		                	
		                	} 
					}).start();
				break;
					case 3:
						break;
					case 4:
						break;
					case 5:
						break;
						default:
							break;
						
						
					}
					Toast.makeText(OnlineImageViewPagerActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
				}
			});// Add a menu item
		}
		Bundle bundle = getIntent().getExtras();
		imageUrls =getIntent().getParcelableArrayListExtra(Extra.IMAGES);
		 cid = getIntent().getStringExtra("cid");
		 page = getIntent().getStringExtra("page");
		//获取每个图片的position
		pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);
		System.out.println("获得position的位置："+pagerPosition);
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.empty)
		.imageScaleType(ImageScaleType.EXACTLY)
		.build();
		pager = (ViewPager) findViewById(R.id.paper);
		ip = new ImagePagerAdapter(imageUrls);
		pager.setAdapter(ip);
		pager.setCurrentItem(pagerPosition);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			public void onPageSelected(int arg0) { 
				pagerPosition = arg0;
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			public void onPageScrollStateChanged(int arg0) {
				
				if(pagerPosition == imageUrls.size() -1&&arg0==1){
					getMoreData();
				}
			}
		});
		
		loadbutton=(ImageButton)findViewById(R.id.downloadbtn);
	    setwallbtn=(ImageButton)findViewById(R.id.setwall); 
	    leftbutton=(ImageButton)findViewById(R.id.left); 
	    rightbutton=(ImageButton)findViewById(R.id.right); 
	    //需要设置监听
	    leftbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.arrowScroll(v.FOCUS_LEFT);
					System.out.println("左边position的位置："+pagerPosition);
					pagerPosition=pagerPosition-1;
					if(pagerPosition<=0)
					{
						pagerPosition=0;
						pager.setCurrentItem(0);
					}			 
					else{
						pager.setCurrentItem(pagerPosition,true);
					}
			}
		});
	    leftbutton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 if(event.getAction() == MotionEvent.ACTION_DOWN){         
	                 v.setBackgroundResource(R.drawable.left1);     
	         }else if(event.getAction() == MotionEvent.ACTION_UP){ 
	        	 v.setBackgroundResource(R.drawable.left);     
	         }     
			return false;
			}
		});
	    rightbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
 
				pager.arrowScroll(v.FOCUS_RIGHT);
				
				//pager.setCurrentItem(pagerPosition-1,true);
				pagerPosition++;
				System.out.println("右边position的位置："+pagerPosition);
				 if(pagerPosition>=0&&pagerPosition<=imageUrls.size()-1){
					 pager.setCurrentItem(pagerPosition);	
				}
				else if(pagerPosition>imageUrls.size()-1){
					getMoreData();
				} 
			}
		});
	    rightbutton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.right1);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(R.drawable.right);
				}
				return false;
			}
		});
	    loadbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Toast.makeText(ImagePagerActivity.this, "loading..", 2000).show();
				mSaveDialog = ProgressDialog.show(OnlineImageViewPagerActivity.this, "loading", "loading，wait...", true);
			   new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();
                	try {
                		picloading = true;
                		String addr ;
            			if(imageUrls.get(pagerPosition)==null){
            				addr = "";
            			}else{
            				addr=imageUrls.get(pagerPosition).getPaddr();
            			}
        				URL	url = new URL(addr);
        					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        	                conn.connect();
                            conn.setConnectTimeout(5000);
        	                InputStream in = conn.getInputStream();
        	                Bitmap map = BitmapFactory.decodeStream(in);
        	                File dirFile = new File(ALBUM_PATH);  
        	                if(!dirFile.exists()){  
        	                    dirFile.mkdir();  
        	                }
        	                Date now =new Date(); 
        	        		fileName=time.format(now)+".jpg";
        	                File myCaptureFile = new File(ALBUM_PATH + fileName);  
        	                Toast.makeText(OnlineImageViewPagerActivity.this, "load success!", Toast.LENGTH_LONG).show();
        	                mSaveDialog.dismiss();
        	                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        	                map.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
        	                bos.flush();  
        	                bos.close();  
        	                }
                	catch (MalformedURLException e) {
    					e.printStackTrace();
    				} catch (IOException e) {
    					e.printStackTrace(); 
    				}
                	Looper.loop();
                	
                	} 
			}).start();
		}
		});
	    loadbutton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 if(event.getAction() == MotionEvent.ACTION_DOWN){         
	                 v.setBackgroundResource(R.drawable.ded1);     
	         }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                 v.setBackgroundResource(R.drawable.ded);     
	         }     
				return false;
			}
		});
     setwallbtn.setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                 v.setBackgroundResource(R.drawable.ses1);     
         }else if(event.getAction() == MotionEvent.ACTION_UP){     
                 v.setBackgroundResource(R.drawable.ses);     
         }     
         return false;     
		}
	});
	 setwallbtn.setOnClickListener(new OnClickListener() {  
			@Override
			public void onClick(View v) {
				mSaveDialog = ProgressDialog.show(OnlineImageViewPagerActivity.this, "loading", "loading image...", true);
				new Thread(new Runnable(){
	                @Override
	                public void run() {
	                	Looper.prepare();
	                	try {
	                		String addr ;
	            			if(imageUrls.get(pagerPosition)==null){
	            				addr = "";
	            			}else{
	            				addr=imageUrls.get(pagerPosition).getPaddr();
	            			}
	        				URL	url = new URL(addr);
	        					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	        	                conn.connect(); 
	        	                InputStream in = conn.getInputStream();
	        	                Bitmap map = BitmapFactory.decodeStream(in);
	        	                File dirFile = new File(ALBUM_PATH);  
	        	                if(!dirFile.exists()){  
	        	                    dirFile.mkdir();  
	        	                }
	        	                Date now =new Date(); 
	        	        		fileName=time.format(now)+".jpg";
	        	                File myCaptureFile = new File(ALBUM_PATH + fileName);  
	        	                Toast.makeText(OnlineImageViewPagerActivity.this, "load success!", Toast.LENGTH_LONG).show();
	        	                mSaveDialog.dismiss(); 
	        	                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
	        	                map.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
	        	                bos.flush();  
	        	                bos.close();  
	        	                WallpaperManager wallpaperManager = WallpaperManager.getInstance(OnlineImageViewPagerActivity.this);
	        	                		Resources res = OnlineImageViewPagerActivity.this.getResources();
	        	                		try {
	        	                		wallpaperManager.setBitmap(map);
	        	                		} catch (IOException e) {
	        	                		e.printStackTrace();
	        	                		}
	        	               Toast.makeText(OnlineImageViewPagerActivity.this, "set success!", Toast.LENGTH_LONG).show();
	        				}catch (Exception e) {
	        					e.printStackTrace(); 
	        				}
	                	Looper.loop();
	                }
	            }).start();
			}
		});
	
	}
	
	protected void getMoreData() {
		// TODO Auto-generated method stub
		
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<Picture>  images; 
		ImagePagerAdapter(List<Picture> images) {
			this.images = images; 
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		} 
		@Override
		public int getCount() {
			return images.size();
		} 
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = View.inflate(OnlineImageViewPagerActivity.this, R.layout.item_pager_image, null);
			final GestureImageView imageView = (GestureImageView) view.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) view.findViewById(R.id.loading); 
			imageLoader.init(ImageLoaderConfiguration.createDefault(OnlineImageViewPagerActivity.this));
			String addr ;
			if(images.get(position)==null){
				position = 1;
			} 
			addr=images.get(position).getPaddr(); 
			imageLoader.displayImage(addr, imageView, options, new ImageLoadingListener() {

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1,
						Bitmap arg2) {
					spinner.setVisibility(View.GONE);
					Animation anim = AnimationUtils.loadAnimation(OnlineImageViewPagerActivity.this, R.anim.fade_in);
					imageView.setAnimation(anim);
					anim.start();					
				}

				@Override
				public void onLoadingFailed(String arg0, View arg1,
						FailReason arg2) {
					String message = null;
					Toast.makeText(OnlineImageViewPagerActivity.this, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
					imageView.setImageResource(android.R.drawable.ic_delete);
				}

				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					spinner.setVisibility(View.VISIBLE);					
				}});
			((ViewPager) container).addView(view, 0);
			return view;
		}  
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}  
	} 


  

}
