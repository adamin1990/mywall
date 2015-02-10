package com.lt.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.lt.main.Extra;
import com.lt.main.LocalImagePagerActivity;
import com.lt.secondwallpaper.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twotoasters.jazzylistview.JazzyGridView;
import com.twotoasters.jazzylistview.JazzyHelper;

public class DownloaderFragment extends Fragment{
	public  static int RESULT_LOAD_IMAGE = 1;
	private AdView mAdView;
	private JazzyGridView gridview;
	private String localpicturepath  = Environment.getExternalStorageDirectory() + "/LovePaper/download/";
	public  static List<String> piclist ;
	private DownloadedAdapter myadapter;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//设置无标题
//		getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view=inflater.inflate(R.layout.downloadedgrid, container, false);
		LinearLayout layout=(LinearLayout)view.findViewById(R.id.adlaout4);
		mAdView = new AdView(getActivity());
        mAdView.setAdUnitId(getResources().getString(R.string.ad_unit_id));
        mAdView.setAdSize(AdSize.BANNER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mAdView, params);
        mAdView.loadAd(new AdRequest.Builder().build());
        gridview=(JazzyGridView)view.findViewById(R.id.localpicture);
        gridview.setTransitionEffect(JazzyHelper.GROW);
        piclist = getData(localpicturepath);
        gridview.setAdapter(new DownloadedAdapter(piclist));
        gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent =new Intent();
				ArrayList list = new ArrayList();
				list.add(piclist);
				intent.putExtra(Extra.IMAGE_POSITION, position);
				intent.putExtra(Extra.IMAGES, list);
				intent.setClass(getActivity(), LocalImagePagerActivity.class);
				startActivity(intent);
				
			}
		});
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.showImageForEmptyUri(R.drawable.empty)
				.showImageOnFail(R.drawable.fail)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnLoading(R.drawable.image_progresses).cacheOnDisk(true)
				.build();
		return view;
	}
	/**
	 * 获取指定路径下的文件
	 * @param localpicturepath2
	 * @return
	 */
	private List<String> getData(String localpicturepath2) {


		List <String> list = new ArrayList<String>();
		File filedir = new File(localpicturepath2);
		if(!filedir.exists()){
			filedir.mkdirs();
		}
		File [] files = filedir.listFiles();
		for(File file : files){
			 try {
				 String path1 = file.getPath();
				 String path2 = file.getAbsolutePath(); 
				 File file2 = new File(path1); 
				list.add(path1); 
			}catch ( Exception e) { 
				System.out.println(e.getMessage().toString());
			} 
		}
		return list;
	
	
	}
	public class DownloadedAdapter extends BaseAdapter{
  private List<String> images;
 		public DownloadedAdapter(List<String> images) {
	super();
	this.images = images;
}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ImageView imageView = null;
			if(convertView==null){
				view=getActivity().getLayoutInflater().inflate(R.layout.item_downloaded, null);
				imageView=(ImageView)view.findViewById(R.id.downloadeditemimage);
			}else{
				view=convertView;
				imageView=(ImageView)view.findViewById(R.id.downloadeditemimage);
			}
			
//			File f=new File(images.get(position));
//			InputStream is;
//			try {
//				is=new FileInputStream(f);
//				Bitmap bmp=BitmapFactory.decodeStream(is);
//				imageView.setImageBitmap(bmp);
//			} catch (FileNotFoundException e) {
//				System.out.println("本地图片-------->"+e.getMessage().toString());
//				e.printStackTrace();
//			}
			imageLoader.init(ImageLoaderConfiguration
					.createDefault(getActivity()));
			imageLoader.displayImage("file:///"+images.get(position), imageView, options);
			return view;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		@Override
		public void notifyDataSetInvalidated() {
			super.notifyDataSetInvalidated();
		}
		
	}

}
