package com.lt.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.lt.bean.Picture;
import com.lt.dao.CommonDao;
import com.lt.secondwallpaper.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.twotoasters.jazzylistview.JazzyGridView;
import com.twotoasters.jazzylistview.JazzyHelper;

public class OnlineImageGridActivity extends SherlockActivity {
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ArrayList<Picture> imageUrls;
	private boolean isloading = false;// 判断是否加载中
	private String page = "1";// 已经取到了第几页
	private String cid;
	private String name;
	private JazzyGridView gridview;
	public ItemAdapter itemAdapter;
	private boolean ishas = true;
	private boolean isScrolling = false;// 是否正在滚动
	private DisplayImageOptions options;
	private AdView mAdView;

	@Override
	protected void onStop() {
		imageLoader.stop();
		super.onStop();
	}

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.onlinegriddetail);
		/**
		 * ad
		 */
		LinearLayout layout = (LinearLayout) findViewById(R.id.adlaoutonlinegrid);
		mAdView = new AdView(this);
        mAdView.setAdUnitId(getResources().getString(R.string.ad_unit_id));
        mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdListener(new ToastAdListener(this));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mAdView, params);
        mAdView.loadAd(new AdRequest.Builder().build());
		imageUrls = getIntent().getParcelableArrayListExtra(Extra.IMAGES);
		cid = getIntent().getStringExtra("cat");
		gridview = (JazzyGridView) findViewById(R.id.classifygridview);
		gridview.setTransitionEffect(JazzyHelper.TWIRL);
		getData();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.showImageForEmptyUri(R.drawable.empty)
				.showImageOnFail(R.drawable.fail)
				.showImageOnLoading(R.drawable.image_progresses).cacheOnDisk(true)
				.build();
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), OnlineImageViewPagerActivity.class);
				intent.putExtra(Extra.IMAGES, imageUrls);
				intent.putExtra(Extra.IMAGE_POSITION, position);
				intent.putExtra("page", page);
				intent.putExtra("cid", cid);
				startActivity(intent);  

			}
		});
		gridview.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:
					isScrolling = true;
					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					isScrolling = false;
					// 获取屏幕上第一个显示的位置
					int startindex = gridview.getFirstVisiblePosition();
					// 获取
					int count = gridview.getChildCount();
					for (int i = 0; i < count; i++) {
						int currentpostion = startindex + i;
						final Picture book = (Picture) gridview
								.getItemAtPosition(currentpostion);
						final View viewchildren = gridview.getChildAt(i);
						ImageView iv_icon = (ImageView) viewchildren
								.findViewById(R.id.image);

						// imageLoader.init(ImageLoaderConfiguration.createDefault(ImageListActivity.this));
						String addr = "";
						if (book == null) {
							addr = "";
						} else {
							URL url = null;
							try {
								url = new URL(book.getPaddr());
								addr = url.toString();
							} catch (Exception e) {
								System.out.println(e.getMessage().toString());
							}
						}
						imageLoader.displayImage(addr, iv_icon, options);

						// Drawable drawable =
						// NetUtil.asyncImageLoader.loadDrawable(book.getPaddr(),
						// new ImageCallback() {
						// public void imageLoaded(Drawable imageDrawable,
						// String imageUrl) {
						// ImageView imageViewByTag = (ImageView)
						// gridview.findViewWithTag(imageUrl);
						// if (imageViewByTag != null) {
						// imageViewByTag.setImageDrawable(imageDrawable);
						// }
						// }
						// });
						//
						// if (drawable != null) {
						// BitmapDrawable bd = (BitmapDrawable) drawable;
						// Bitmap bitmap = bd.getBitmap();
						// iv_icon.setImageBitmap(bitmap);
						// } else {
						// iv_icon.setImageResource(R.drawable.image_for_empty_url);
						// }
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					isScrolling = true;
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (totalItemCount <= 0) {
					return;
				}
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					if (isloading || !ishas) {
						return;
					}
					new AsyncTask<Void, String, List<Map<String, Object>>>() {
						protected List<Map<String, Object>> doInBackground(
								Void... params) {
							List<Map<String, Object>> list = null;
							try {
								list = CommonDao.getList(getResources()
										.getString(R.string.picURL), page, cid);
							} catch (Exception e) {
								e.printStackTrace();
							}
							return list;
						}

						protected void onPreExecute() {
							isloading = true;
							super.onPreExecute();
						}

						protected void onPostExecute(
								List<Map<String, Object>> result) {
							List<Map<String, Object>> picurls = new ArrayList<Map<String, Object>>();
							picurls = result;
							for (Map<String, Object> map : picurls) {
								Picture pic = new Picture();
								pic.setPid(String.valueOf(map.get("pid")));
								pic.setPaddr(String.valueOf(map.get("paddr")));
								pic.setCid(String.valueOf(map.get("cid")));
								pic.setPname(String.valueOf(map.get("pname")));
								imageUrls.add(pic);
							}
							itemAdapter.notifyDataSetChanged();
							page = String.valueOf((Integer.valueOf(page) + 1));
							isloading = false;
							if (result.size() < 10 || result == null) {
								ishas = false;
							}
							super.onPostExecute(result);
						}

						protected void onProgressUpdate(String... values) {
							Toast.makeText(OnlineImageGridActivity.this,
									values[0], Toast.LENGTH_SHORT).show();
							super.onProgressUpdate(values);
						}
					}.execute();
				}
			}
		});
	}



	private void getData() {
		new AsyncTask<Void, String, List<Map<String, Object>>>() {
			protected List<Map<String, Object>> doInBackground(Void... params) {
				List<Map<String, Object>> list = null;
				try {
					list = CommonDao.getList(
							getResources().getString(R.string.picURL), page,
							cid);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return list;
			}

			protected void onPreExecute() {
				isloading = true;
				Toast.makeText(OnlineImageGridActivity.this, "loading...", 3)
						.show();
				super.onPreExecute();
			}

			protected void onPostExecute(List<Map<String, Object>> result) {
				List<Map<String, Object>> picurls = new ArrayList<Map<String, Object>>();
				imageUrls = new ArrayList<Picture>();
				picurls = result;
				for (Map<String, Object> map : picurls) {
					Picture pic = new Picture();
					pic.setPid(String.valueOf(map.get("pid")));
					pic.setPaddr(String.valueOf(map.get("paddr")));
					pic.setCid(String.valueOf(map.get("cid")));
					pic.setPname(String.valueOf(map.get("pname")));
					imageUrls.add(pic);
				}
				itemAdapter = new ItemAdapter();
				gridview.setAdapter(itemAdapter);
				page = String.valueOf((Integer.valueOf(page) + 1));
				isloading = false;
				super.onPostExecute(result);
			}

			protected void onProgressUpdate(String... values) {
				Toast.makeText(OnlineImageGridActivity.this, values[0],
						Toast.LENGTH_SHORT).show();
				super.onProgressUpdate(values);
			}
		}.execute();
	}

	class ItemAdapter extends BaseAdapter {
		private int selected = -1;

		private class ViewHolder {
			public TextView text;
			public ImageView image;
		}

		@Override
		public int getCount() {
			return imageUrls.size();
		}

		@Override
		public Object getItem(int position) {
			return imageUrls.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image,
						null);
				holder = new ViewHolder();
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else
				holder = (ViewHolder) view.getTag();
			// if(isScrolling){
			// holder.image.setImageResource(R.drawable.ic_launcher);
			// }else{
			imageLoader.init(ImageLoaderConfiguration
					.createDefault(OnlineImageGridActivity.this));
			String addr = "";
			if (imageUrls.get(position) == null) {
				addr = "";
			} else {
				URL url = null;
				try {
					url = new URL(imageUrls.get(position).getPaddr());
					addr = url.toString();
				} catch (Exception e) {
					System.out.println(e.getMessage().toString());
				}
			}
			imageLoader.displayImage(addr, holder.image, options);
			// }
			return view;
		}
	}

}
