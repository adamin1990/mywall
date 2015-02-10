package com.lt.main;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.drive.internal.GetMetadataRequest;
import com.lt.fragment.CategoryFragment;
import com.lt.fragment.DownloaderFragment;
import com.lt.fragment.LocalGridFragment;
import com.lt.lib.DynamicShareActionProvider;
import com.lt.sherlocknavigationdrawer.SherlockActionBarDrawerToggle;
import com.lt.secondwallpaper.R;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

public class MainFragment extends SherlockFragment {

	private DrawerLayout mDrawerLayout;
	private JazzyListView listView;
	private TextView mContent;
	private LinearLayout lin;
	CategoryFragment categoryFramment;
	LocalGridFragment localgrid;
	private ActionBarHelper mActionBar;

	private SherlockActionBarDrawerToggle mDrawerToggle;

	public static Fragment newInstance() {
		Fragment f = new MainFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_layout, container, false);
        LocalGridFragment localfragment=new LocalGridFragment();
        getFragmentManager().beginTransaction().replace(R.id.localgridfmt, localfragment).commit();
		mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
		listView = (JazzyListView) view.findViewById(R.id.left_drawer);
		listView.setTransitionEffect(JazzyHelper.FLY);
		TextView tv=new TextView(getActivity());
        tv.setText(R.string.listviewheadertext);
        listView.addHeaderView(tv);
        
		mDrawerLayout.setDrawerListener(new DemoDrawerListener());
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		listView.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, Shakespeare.TITLES));
		listView.setOnItemClickListener(new DrawerItemClickListener());
		listView.setCacheColorHint(0);
		listView.setScrollingCacheEnabled(false);
		listView.setScrollContainer(false);
		listView.setFastScrollEnabled(true);
		listView.setSmoothScrollbarEnabled(true);

		mActionBar = createActionBarHelper();
		mActionBar.init();

		// ActionBarDrawerToggle provides convenient helpers for tying together
		// the
		// prescribed interactions between a top-level sliding drawer and the
		// action bar.
		mDrawerToggle = new SherlockActionBarDrawerToggle(this.getActivity(), mDrawerLayout, R.drawable.ic_drawer_light, R.string.drawer_open, R.string.drawer_close);
		mDrawerToggle.syncState();
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater = ((SherlockFragmentActivity)getActivity()).getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		DynamicShareActionProvider provider = (DynamicShareActionProvider) menu.findItem(R.id.share).getActionProvider();
        provider.setShareDataType("text/plain");
        provider.setOnShareIntentUpdateListener(new DynamicShareActionProvider.OnShareIntentUpdateListener() {

            @Override
            public Bundle onShareIntentExtrasUpdate() {
                Bundle extras = new Bundle();
                extras.putString(android.content.Intent.EXTRA_TEXT, "If U like this Application,plesase share it to your friends,thanks!  https://play.google.com/store/apps/details?id="+getActivity().getPackageName());
                
                return extras;
            }

        });
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * The action bar home/up action should open or close the drawer.
		 * mDrawerToggle will take care of this.
		 */
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch(item.getItemId()){
		case R.id.about:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Dear User!"
				+ "Thank you for your suggestion to me,"
				+ "Click on the ‘visitpage’button give me a message.Thanks!");
			builder.setTitle("About");
			builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.setNeutralButton("Visit Page", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent in = new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://user.qzone.qq.com/14846869"));
					in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(in);
					dialog.dismiss();
				}
			});
			builder.create().show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * This list item click listener implements very simple view switching by
	 * changing the primary content text. The drawer is closed when a selection
	 * is made.
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		private FragmentTransaction a;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch(position){
			case 0:
				mDrawerLayout.closeDrawer(listView);
				mActionBar.setTitle(getResources().getString(R.string.app_name));
				break;
			case 1:
				 localgrid=new LocalGridFragment();
				FragmentTransaction trans=getFragmentManager().beginTransaction();
				trans.remove(localgrid);
				trans.replace(R.id.localgridfmt, localgrid);
				trans.commit();
				mDrawerLayout.closeDrawer(listView);
				mActionBar.setTitle(Shakespeare.TITLES[position-1]);
				break;
			case 2:
				categoryFramment=new CategoryFragment();
				FragmentTransaction transaction2=getFragmentManager().beginTransaction();
				transaction2.remove(categoryFramment);
				transaction2.replace(R.id.localgridfmt, categoryFramment);
				transaction2.commit();
				mActionBar.setTitle(Shakespeare.TITLES[position-1]);
				mDrawerLayout.closeDrawer(listView);
				break;
			case 3:
				DownloaderFragment downloadFragment=new DownloaderFragment();
				FragmentTransaction transaction3=getFragmentManager().beginTransaction();
						transaction3.remove(downloadFragment);
				transaction3.replace(R.id.localgridfmt, downloadFragment);
				transaction3.commit();
				mActionBar.setTitle(Shakespeare.TITLES[position-1]);
				mDrawerLayout.closeDrawer(listView);
				break;
			case 4:
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("Dear User!"
					+ "Thank you for your suggestion to me,"
					+ "Click on the ‘visitpage’button give me a message.Thanks!");
				builder.setTitle("About");
				builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.setNeutralButton("Visit Page", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent in = new Intent(Intent.ACTION_VIEW,
							Uri.parse("http://user.qzone.qq.com/14846869"));
						in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(in);
						dialog.dismiss();
					}
				});
				builder.create().show();
				mActionBar.setTitle(Shakespeare.TITLES[position-1]);
				mDrawerLayout.closeDrawer(listView);
				break;
			case 5:


				// 在自己应用中直接连接到应用在android应用市场的位置去评价
				String str = "market://details?id="
						+ getActivity().getPackageName();
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
									+ getActivity().getPackageName()));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
					
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 6:
				 PackageManager packageManager = getActivity().getPackageManager();
		           // getPackageName()是你当前类的包名，0代表是获取版本信息
		           PackageInfo packInfo = null;
				try {
					packInfo = packageManager.getPackageInfo(getActivity().getPackageName(),0);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
		           String version = packInfo.versionName;
		           AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
					builder2.setMessage("The Versioncode of is app is:"+
		           version+"     " +
		           		"Thanks!");
					builder2.setTitle("Version");
					builder2.create().show();
					mDrawerLayout.closeDrawer(listView);
			break;
			case 7:
				mDrawerLayout.closeDrawer(listView);
				System.exit(0);
				break;
			case 8:
				String str2 = "market://details?id="
						+ "com.lt.cameralivewallpaper";
				Intent localIntent2 = new Intent(
						"android.intent.action.VIEW");
				localIntent2.setData(Uri.parse(str2));
				try {
					startActivity(localIntent2);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.cameralivewallpaper"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 9:
				String str3 = "market://details?id="
						+ "com.lt.car11";
				Intent localIntent3 = new Intent(
						"android.intent.action.VIEW");
				localIntent3.setData(Uri.parse(str3));
				try {
					startActivity(localIntent3);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.car11"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 10:
				String str4 = "market://details?id="
						+ "com.lt.NeedforracingSpeed";
				Intent localIntent4 = new Intent(
						"android.intent.action.VIEW");
				localIntent4.setData(Uri.parse(str4));
				try {
					startActivity(localIntent4);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.NeedforracingSpeed"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 11:
				String str5 = "market://details?id="
						+ "com.lt.gonglusaiwallpaper";
				Intent localIntent5 = new Intent(
						"android.intent.action.VIEW");
				localIntent5.setData(Uri.parse(str5));
				try {
					startActivity(localIntent5);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.gonglusaiwallpaper"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 12:
				String str6 = "market://details?id="
						+ "com.lt.CrazyUndergroundRacing";
				Intent localIntent6 = new Intent(
						"android.intent.action.VIEW");
				localIntent6.setData(Uri.parse(str6));
				try {
					startActivity(localIntent6);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.CrazyUndergroundRacing"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 13:
				String str7 = "market://details?id="
						+ "com.lt.speedrallywallpaper";
				Intent localIntent7 = new Intent(
						"android.intent.action.VIEW");
				localIntent7.setData(Uri.parse(str7));
				try {
					startActivity(localIntent7);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.speedrallywallpaper"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 14:
				String str8 = "market://details?id="
						+ "com.lt.policecar";
				Intent localIntent8 = new Intent(
						"android.intent.action.VIEW");
				localIntent8.setData(Uri.parse(str8));
				try {
					startActivity(localIntent8);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.policecar"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 15:
				String str9 = "market://details?id="
						+ "com.lt.motowallpaper";
				Intent localIntent9 = new Intent(
						"android.intent.action.VIEW");
				localIntent9.setData(Uri.parse(str9));
				try {
					startActivity(localIntent9);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.motowallpaper"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 16:
				String str10 = "market://details?id="
						+ "com.lt.aircraftwallpaper";
				Intent localIntent10 = new Intent(
						"android.intent.action.VIEW");
				localIntent10.setData(Uri.parse(str10));
				try {
					startActivity(localIntent10);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.aircraftwallpaper"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 17:
				String str11 = "market://details?id="
						+ "com.lt.NeedforracingSpeed";
				Intent localIntent11 = new Intent(
						"android.intent.action.VIEW");
				localIntent11.setData(Uri.parse(str11));
				try {
					startActivity(localIntent11);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.NeedforracingSpeed"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 18:
				String str12 = "market://details?id="
						+ "com.lt.threedspeed";
				Intent localIntent12 = new Intent(
						"android.intent.action.VIEW");
				localIntent12.setData(Uri.parse(str12));
				try {
					startActivity(localIntent12);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.threedspeed"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 19:
				String str13 = "market://details?id="
						+ "com.lt.DragRacing";
				Intent localIntent13 = new Intent(
						"android.intent.action.VIEW");
				localIntent13.setData(Uri.parse(str13));
				try {
					startActivity(localIntent13);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.DragRacing"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
			case 20:
				String str14 = "market://details?id="
						+ "com.lt.RoadMotoRacing";
				Intent localIntent14 = new Intent(
						"android.intent.action.VIEW");
				localIntent14.setData(Uri.parse(str14));
				try {
					startActivity(localIntent14);
				} catch (ActivityNotFoundException e) {
					Intent intent = new Intent(
							Intent.ACTION_VIEW);
					intent.setData(Uri
							.parse("https://play.google.com/store/apps/details?id="
									+"com.lt.RoadMotoRacing"));
					startActivity(Intent.createChooser(
							intent, getActivity().getTitle()));
				}
				mDrawerLayout.closeDrawer(listView);
				break;
				
				
				default:
					break;
				
				
			}
//		LocalGridFragment localfragment=new LocalGridFragment();
//		FragmentTransaction transaction =getFragmentManager().beginTransaction();
//		transaction.remove(localfragment);
//		transaction.replace(R.id.localgridfmt, localfragment);
//		transaction.commit();
////		 getChildFragmentManager().beginTransaction().replace(R.id.addlayout, localfragment).commit();
////		lin.addView(view);
////			mContent.setText(Shakespeare.DIALOGUE[position]);
//			mActionBar.setTitle(Shakespeare.TITLES[position]);
//			mDrawerLayout.closeDrawer(listView);
		}
	}

	/**
	 * A drawer listener can be used to respond to drawer events such as
	 * becoming fully opened or closed. You should always prefer to perform
	 * expensive operations such as drastic relayout when no animation is
	 * currently in progress, either before or after the drawer animates.
	 * 
	 * When using ActionBarDrawerToggle, all DrawerLayout listener methods
	 * should be forwarded if the ActionBarDrawerToggle is not used as the
	 * DrawerLayout listener directly.
	 */
	private class DemoDrawerListener implements DrawerLayout.DrawerListener {
		@Override
		public void onDrawerOpened(View drawerView) {
			mDrawerToggle.onDrawerOpened(drawerView);
			mActionBar.onDrawerOpened();
		}

		@Override
		public void onDrawerClosed(View drawerView) {
			mDrawerToggle.onDrawerClosed(drawerView);
			mActionBar.onDrawerClosed();
		}

		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {
			mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			mDrawerToggle.onDrawerStateChanged(newState);
		}
	}

	/**
	 * Create a compatible helper that will manipulate the action bar if
	 * available.
	 */
	private ActionBarHelper createActionBarHelper() {
		return new ActionBarHelper();
	}

	

	private class ActionBarHelper {
		private final ActionBar mActionBar;
		private CharSequence mDrawerTitle;
		private CharSequence mTitle;

		private ActionBarHelper() {
			mActionBar = ((SherlockFragmentActivity)getActivity()).getSupportActionBar();
		}

		public void init() {
			mActionBar.setDisplayHomeAsUpEnabled(true);
			mActionBar.setHomeButtonEnabled(true);
			mTitle = mDrawerTitle = getActivity().getTitle();
		}

		/**
		 * When the drawer is closed we restore the action bar state reflecting
		 * the specific contents in view.
		 */
		public void onDrawerClosed() {
			mActionBar.setTitle(mTitle);
		}

		/**
		 * When the drawer is open we set the action bar to a generic title. The
		 * action bar should only contain data relevant at the top level of the
		 * nav hierarchy represented by the drawer, as the rest of your content
		 * will be dimmed down and non-interactive.
		 */
		public void onDrawerOpened() {
			mActionBar.setTitle(mDrawerTitle);
		}

		public void setTitle(CharSequence title) {
			mTitle = title;
		}
	}

}
