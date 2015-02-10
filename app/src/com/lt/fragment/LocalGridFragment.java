package com.lt.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.lt.arclib.ArcMenu;
import com.lt.arclib.RayMenu;
import com.lt.main.AppPagerActivity;
import com.lt.secondwallpaper.R;
import com.twotoasters.jazzylistview.JazzyGridView;
import com.twotoasters.jazzylistview.JazzyHelper;

public class LocalGridFragment extends Fragment {
	private JazzyGridView gridview;
	AssetManager assetManager;
	private ArrayList<String> lists;
	private static final int[] ITEM_DRAWABLES = {R.drawable.local,R.drawable.more,R.drawable.arcdownload,R.drawable.arccomment};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.localcontent, container,false);
		RayMenu arcMenu = (RayMenu)view.findViewById(R.id.arcmenu);
		initArcMenu(arcMenu, ITEM_DRAWABLES);
		assetManager=getActivity().getAssets();
		lists = (ArrayList<String>) getData();
		gridview = (JazzyGridView) view.findViewById(R.id.localgrid);
		gridview.setTransitionEffect(JazzyHelper.HELIX);
		gridview.setAdapter(new ItemAdapter(lists));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
Intent intent=new Intent();
intent.putExtra("position", position);
intent.putStringArrayListExtra("list", lists);
intent.setClass(getActivity(), AppPagerActivity.class);
startActivity(intent);
			}
		});
		
		return view;
	}

	private void initArcMenu(RayMenu arcMenu, int[] itemDrawables) {

        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(getActivity());
            item.setImageResource(itemDrawables[i]);

            final int position = i;
            arcMenu.addItem(item, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    		
	}

	private ArrayList<String> getData() {
		String path ="im";
		String photoname=null;
		List<String> lists=new ArrayList<String>();
		try {
			String[] photos=assetManager.list(path);
			for(int i=0;i<photos.length;i++){
				photoname=photos[i];
				String str = path + "/" + photoname;
				lists.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
 		return (ArrayList<String>) lists;
	}


	class ItemAdapter extends BaseAdapter {
		private List<String> images;

		public ItemAdapter(List<String> images) {
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
			ImageView imageview;
			if (convertView == null) {
				imageview = (ImageView)getActivity().getLayoutInflater()
						.inflate(R.layout.localgriditem, null);
			} else {
				imageview = (ImageView) convertView;
			}
			asynLoadBitmap(imageview, position);// “Ï≤Ω»•º”‘ÿÕº∆¨
			return imageview;
		}

		private void asynLoadBitmap(ImageView imageview, int position) {
			InputStream is = null;
			try {
				is = assetManager.open(images.get(position));
			} catch (IOException e) {
				e.printStackTrace();
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inSampleSize = 5;
			Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
			imageview.setImageBitmap(bmp);
		}
	}

}
