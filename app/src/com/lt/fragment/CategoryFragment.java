package com.lt.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.lt.arclib.RayMenu;
import com.lt.bean.Picture;
import com.lt.dao.CommonDao;
import com.lt.main.OnlineImageGridActivity;
import com.lt.secondwallpaper.R;
import com.lt.util.AsyncImageLoader.ImageCallback;
import com.lt.util.NetUtil;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class CategoryFragment extends Fragment {
	protected static final int GETDATA = 1;
	private long exitTime = 0;
	// 网络资源
	List<Map<String, Object>> data;
	private LinearLayout ll_loading;// 正在加载模块
	private boolean isScrolling = false;// 是否正在滚动
	SimpleAdapter adapter;
	ArrayList<Picture> imagesurls;
	private JazzyListView lv_main_category;
	Intent intent;
	private AdView mAdView;
	private ArrayAdapter<String> mAdapter;
	private boolean isloading = false;// 判断是否正在加载中

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.categorylistview, container,
				false);
		ll_loading = (LinearLayout) view.findViewById(R.id.ll_main_progress);
		lv_main_category = (JazzyListView) view.findViewById(R.id.listcategory);
		lv_main_category.setTransitionEffect(JazzyHelper.GROW);
		lv_main_category.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView category = (TextView) view.findViewById(R.id.cid);
				// 种类的id
				final String cid = category.getText().toString();
				intent = new Intent(getActivity(),OnlineImageGridActivity.class);
				intent.putExtra("cat", cid);
				startActivity(intent);
				
			}
		});
		data = new ArrayList<Map<String, Object>>();
		/**
		 * 广告
		 */
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.adlaout3);
		mAdView = new AdView(getActivity());
		mAdView.setAdUnitId(getResources().getString(R.string.ad_unit_id));
		mAdView.setAdSize(AdSize.BANNER);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		layout.addView(mAdView, params);
		mAdView.loadAd(new AdRequest.Builder().build());
		/**
		 * 获取数据
		 */
		getData();
		return view;
	}

	private void getData() {
		new AsyncTask<Void, String, List<Map<String, Object>>>() {

			@Override
			protected List<Map<String, Object>> doInBackground(Void... params) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				try {
					list = CommonDao.getList(
							getResources().getString(R.string.catURL), null,
							null);
				} catch (NotFoundException e) {
					publishProgress("Failed to get the type, please in wifi connection...");
					e.printStackTrace();
				} catch (Exception e) {
					publishProgress("Failed to get the type, please in wifi connection...");
					e.printStackTrace();
				}
				return list;
			}

			@Override
			protected void onPreExecute() {
				ll_loading.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<Map<String, Object>> result) {
				data = result;
				ll_loading.setVisibility(View.GONE);
				adapter = new SimpleAdapter(getActivity(), data,
						R.layout.list_item, new String[] { "cid", "cname",
								"caddr", "cdescrip" }, new int[] { R.id.cid,
								R.id.category, R.id.photo, R.id.describe }) {

					@Override
					public Object getItem(int position) {
						return data.get(position);
					}

				};
				adapter.setViewBinder(new ListViewBinder());
				lv_main_category.setAdapter(adapter);

				super.onPostExecute(result);
			}

			@Override
			protected void onProgressUpdate(String... values) {
				super.onProgressUpdate(values);
			}

		}.execute();
	}

	private class ListViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if ((view instanceof ImageView) && (data instanceof String)) {
				ImageView imageView = (ImageView) view;
				imageView.setTag((String) data);
				if (isloading) {
					imageView.setImageResource(R.drawable.image_progresses);
				} else {
					Drawable drawable = NetUtil.asyncImageLoader.loadDrawable(
							(String) data, new ImageCallback() {
								public void imageLoaded(Drawable imageDrawable,
										String data) {
									ImageView imageViewByTag = (ImageView) lv_main_category
											.findViewWithTag(data);
									if (imageViewByTag != null) {
										imageViewByTag
												.setImageDrawable(imageDrawable);
									}
								}
							});
					if (drawable != null) {
						imageView.setImageDrawable(drawable);
					} else {
						imageView.setImageResource(R.drawable.image_progresses);
					}
				}
				return true;
			}
			return false;
		}
	}
}
