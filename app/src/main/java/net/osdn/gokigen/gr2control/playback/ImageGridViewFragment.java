package net.osdn.gokigen.gr2control.playback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;


import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.camera.ICameraFileInfo;
import net.osdn.gokigen.gr2control.camera.ICameraRunMode;
import net.osdn.gokigen.gr2control.camera.ICameraRunModeCallback;
import net.osdn.gokigen.gr2control.camera.playback.ICameraContentListCallback;
import net.osdn.gokigen.gr2control.camera.playback.IDownloadThumbnailImageCallback;
import net.osdn.gokigen.gr2control.camera.playback.IPlaybackControl;
import net.osdn.gokigen.gr2control.playback.detail.ImageContentInfoEx;
import net.osdn.gokigen.gr2control.playback.detail.ImagePagerViewFragment;
import net.osdn.gokigen.gr2control.playback.detail.MyContentDownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class ImageGridViewFragment extends Fragment implements ICameraRunModeCallback
{
	private final String TAG = this.toString();
    private final String MOVIE_SUFFIX = ".mov";
    private final String JPEG_SUFFIX = ".jpg";
    private final String DNG_RAW_SUFFIX = ".dng";
	private final String OLYMPUS_RAW_SUFFIX = ".orf";
	private final String PENTAX_RAW_PEF_SUFFIX = ".pef";

	private MyContentDownloader contentDownloader;
    private GridView gridView;
	private boolean gridViewIsScrolling;
	private IPlaybackControl playbackControl;
	private ICameraRunMode runMode;
		
    private List<ImageContentInfoEx> contentList;
	private ExecutorService executor;
	private LruCache<String, Bitmap> imageCache;

	public static ImageGridViewFragment newInstance(@NonNull IPlaybackControl playbackControl, @NonNull ICameraRunMode runMode)
	{
		ImageGridViewFragment fragment = new ImageGridViewFragment();
		fragment.setControllers(playbackControl, runMode);
		return (fragment);
	}

	private void setControllers(IPlaybackControl playbackControl, ICameraRunMode runMode)
	{
		this.playbackControl = playbackControl;
		this.runMode = runMode;
		Activity activity = getActivity();
		if (activity != null)
		{
            this.contentDownloader = new MyContentDownloader(getActivity(), playbackControl);
        }
        else
        {
            this.contentDownloader = null;
        }
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        Log.v(TAG, "ImageGridViewFragment::onCreate()");

		executor = Executors.newFixedThreadPool(1);
		imageCache = new LruCache<>(160);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.v(TAG, "ImageGridViewFragment::onCreateView()");
		View view = inflater.inflate(R.layout.fragment_image_grid_view, container, false);
		
		gridView = view.findViewById(R.id.gridView1);
		gridView.setAdapter(new GridViewAdapter(inflater));
        GridViewOnItemClickListener listener = new GridViewOnItemClickListener();
		gridView.setOnItemClickListener(listener);
        gridView.setOnItemLongClickListener(listener);
		gridView.setOnScrollListener(new GridViewOnScrollListener());
		
		return (view);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.image_grid_view, menu);
		String title = getString(R.string.app_name);
		AppCompatActivity activity = (AppCompatActivity) getActivity();
		if (activity != null)
		{
            ActionBar bar = activity.getSupportActionBar();
            if (bar != null)
            {
                bar.setTitle(title);
            }
        }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    int id = item.getItemId();
		if (id == R.id.action_refresh)
		{
			refresh();
			return (true);
		}
        if (id == R.id.action_batch_download_original_size_raw)
        {
            // オリジナルサイズのダウンロード
            startDownloadBatch(false);
            return (true);
        }
        if (id == R.id.action_batch_download_640x480_raw)
        {
            // 小さいサイズのダウンロード
            startDownloadBatch(true);
            return (true);
        }
        if (id == R.id.action_select_all)
        {
            selectUnselectAll();
            return (true);
        }
		return (super.onOptionsItemSelected(item));
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.v(TAG, "onResume() Start");
		AppCompatActivity activity = (AppCompatActivity)getActivity();
		if (activity != null)
		{
            ActionBar bar = activity.getSupportActionBar();
            if (bar != null)
            {
                // アクションバーの表示をするかどうか
                boolean isShowActionBar = false;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                if (preferences != null)
                {
                    isShowActionBar = preferences.getBoolean("use_playback_menu", false);
                }
                if (isShowActionBar)
                {
                    bar.show();  // ActionBarの表示を出す
                }
                else
                {
                    bar.hide();   // ActionBarの表示を消す
                }
            }
        }

        try
        {
            refresh();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.v(TAG, "onResume() End");
	}
	
	@Override
	public void onPause()
	{
        Log.v(TAG, "onPause() Start");
        if (!runMode.isRecordingMode())
        {
            // Threadで呼んではダメみたいだ...
            //runMode.changeRunMode(true, this);
            super.onPause();
            Log.v(TAG, "onPause() End");
            return;
        }
        postProcessChangeRunMode(true);
		super.onPause();
        Log.v(TAG, "onPause() End");
    }

    private void postProcessChangeRunMode(boolean isRecording)
    {
        try
        {
            if (isRecording)
            {
                if (!executor.isShutdown())
                {
                    executor.shutdownNow();
                }
            }
            else
            {
                refresh();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompleted(boolean isRecording)
    {
        postProcessChangeRunMode(isRecording);
    }

    @Override
    public void onErrorOccurred(boolean isRecording)
    {
        postProcessChangeRunMode(isRecording);
    }

	@Override
	public void onStop()
	{
		Log.v(TAG, "onStop()");
		super.onStop();
	}

	private void refresh()
    {
        try
        {
            if (runMode.isRecordingMode())
            {
                runMode.changeRunMode(false, this);
                return;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshImpl();
            }
        });
        try
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showHideProgressBar(true);
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showHideProgressBar(final boolean isVisible)
    {
        Activity activity = getActivity();
        if (activity != null)
        {
            ProgressBar bar = getActivity().findViewById(R.id.progress_bar);
            if (bar != null)
            {
                bar.setVisibility((isVisible) ? View.VISIBLE : View.GONE);
                bar.invalidate();
            }
        }
    }

	private void refreshImpl()
	{
		contentList = null;
		Log.v(TAG, "refreshImpl() start");

		playbackControl.downloadContentList(new ICameraContentListCallback() {
			@Override
			public void onCompleted(List<ICameraFileInfo> list) {
				// Sort contents in chronological order (or alphabetical order).
				Collections.sort(list, new Comparator<ICameraFileInfo>() {
					@Override
					public int compare(ICameraFileInfo lhs, ICameraFileInfo rhs)
					{
						long diff = rhs.getDatetime().getTime() - lhs.getDatetime().getTime();
						if (diff == 0)
                        {
							diff = rhs.getFilename().compareTo(lhs.getFilename());
						}
						return (int)Math.min(Math.max(-1, diff), 1);
					}
				});

                List<ImageContentInfoEx> contentItems = new ArrayList<>();
                HashMap<String, ImageContentInfoEx> rawItems = new HashMap<>();
                for (ICameraFileInfo item : list)
                {
                    String path = item.getFilename().toLowerCase(Locale.getDefault());
                    if ((path.toLowerCase().endsWith(JPEG_SUFFIX))||(path.toLowerCase().endsWith(MOVIE_SUFFIX)))
                    {
                        contentItems.add(new ImageContentInfoEx(item, false, ""));
                    }
                    else if (path.toLowerCase().endsWith(DNG_RAW_SUFFIX))
                    {
                        //rawItems.put(path, new ImageContentInfoEx(item, true, DNG_RAW_SUFFIX));
                        contentItems.add(new ImageContentInfoEx(item, true, DNG_RAW_SUFFIX));
                    }
                    else if (path.toLowerCase().endsWith(OLYMPUS_RAW_SUFFIX))
                    {
                        rawItems.put(path, new ImageContentInfoEx(item, true, OLYMPUS_RAW_SUFFIX));
                    }
                    else if (path.toLowerCase().endsWith(PENTAX_RAW_PEF_SUFFIX))
                    {
                        //rawItems.put(path, new ImageContentInfoEx(item, true, PENTAX_RAW_PEF_SUFFIX));
                        contentItems.add(new ImageContentInfoEx(item, true, PENTAX_RAW_PEF_SUFFIX));
                    }
                }

                //List<ImageContentInfoEx> appendRawContents = new ArrayList<>();
                for (ImageContentInfoEx item : contentItems)
                {
                    String path = item.getFileInfo().getFilename().toLowerCase(Locale.getDefault());
                    if (path.toLowerCase().endsWith(JPEG_SUFFIX))
                    {
/*
                        String target1 = path.replace(JPEG_SUFFIX, DNG_RAW_SUFFIX);
                        ImageContentInfoEx raw1 = rawItems.get(target1);
                        if (raw1 != null)
                        {
                        	// JPEGファイルとRAWファイルがあるので、それをマークする
                            item.setHasRaw(true, DNG_RAW_SUFFIX);
                            Log.v(TAG, "DETECT RAW FILE: " + target1);
                        }
                        else
                        {
                            // RAWだけあった場合、一覧に追加する
                            appendRawContents.add(rawItems.get(path));
                        }
*/
                        String target2 = path.replace(JPEG_SUFFIX, OLYMPUS_RAW_SUFFIX);
                        ImageContentInfoEx raw2 = rawItems.get(target2);
                        if (raw2 != null)
                        {
                            // RAW は、JPEGファイルがあった場合にのみリストする
                            item.setHasRaw(true, OLYMPUS_RAW_SUFFIX);
                            Log.v(TAG, "DETECT RAW FILE: " + target2);
                        }
/*
                        String target3 = path.replace(JPEG_SUFFIX, PENTAX_RAW_PEF_SUFFIX);
                        ImageContentInfoEx raw3 = rawItems.get(target3);
                        if (raw3 != null)
                        {
                            // RAW は、JPEGファイルがあった場合にのみリストする
                            item.setHasRaw(true, PENTAX_RAW_PEF_SUFFIX);
                            Log.v(TAG, "DETECT RAW FILE: " + target3);
                        }
                        else
                        {
                            // RAWだけあった場合、一覧に追加する
                            appendRawContents.add(rawItems.get(path));
                        }
*/
                    }
                }
                //contentItems.addAll(appendRawContents);
                contentList = contentItems;

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
                        showHideProgressBar(false);
                        gridView.invalidateViews();
					}
				});
			}
			
			@Override
			public void onErrorOccurred(Exception e) {
				final String message = e.getMessage();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
                        showHideProgressBar(false);
						presentMessage("Load failed", message);
					}
				});
			}
		});
        Log.v(TAG, "refreshImpl() end");
    }

    /**
     *   全選択・全選択解除
     *
     */
    private void selectUnselectAll()
    {
        if ((contentList == null)||(contentList.size() == 0))
        {
            // 選択されていない時は終わる。
            return;
        }

        int nofSelected = 0;
        for (ImageContentInfoEx content : contentList)
        {
            if (content.isSelected())
            {
                nofSelected++;
            }
        }

        // 全部選択されているときは全選択解除・そうでない時は全選択
        boolean setSelected = (nofSelected != contentList.size());
        for (ImageContentInfoEx content : contentList)
        {
            content.setSelected(setSelected);
        }

        // グリッドビューの再描画
        redrawGridView();
    }

    private void redrawGridView()
    {
        // グリッドビューの再描画
        Activity activity = getActivity();
        if (activity != null)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (gridView != null)
                    {
                        gridView.invalidateViews();
                    }
                }
            });
        }
    }

    /**
     *    一括ダウンロードの開始
     *
     * @param isSmall  小さいサイズ(JPEG)
     */
    private void startDownloadBatch(final boolean isSmall)
    {
        try
        {
            // 念のため、contentDownloader がなければ作る
            if (contentDownloader == null)
            {
                Activity activity = getActivity();
                if (activity == null)
                {
                    // activityが取れない時には終わる。
                    return;
                }
                this.contentDownloader = new MyContentDownloader(getActivity(), playbackControl);
            }
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        // ダウンロード枚数を取得
                        int totalSize = 0;
                        for (ImageContentInfoEx content : contentList)
                        {
                            if (content.isSelected())
                            {
                                totalSize++;
                            }
                        }
                        if (totalSize == 0)
                        {
                            // 画像が選択されていなかった...終了する
                            return;
                        }
                        int count = 1;
                        for (ImageContentInfoEx content : contentList)
                        {
                            if (content.isSelected())
                            {
                                contentDownloader.startDownload(content.getFileInfo(), " (" + count + "/" + totalSize + ") ", null, isSmall);
                                count++;

                                // 画像の選択を落とす
                                content.setSelected(false);
                            }
                        }

                        // グリッドビューの再描画
                        redrawGridView();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private static class GridCellViewHolder
    {
		ImageView imageView;
		ImageView iconView;
		ImageView selectView;
	}
	
	private class GridViewAdapter extends BaseAdapter
    {
		private LayoutInflater inflater;

		GridViewAdapter(LayoutInflater inflater)
		{
			this.inflater = inflater;
		}

		private List<?> getItemList()
        {
            return (contentList);
		}
		
		@Override
		public int getCount()
        {
			if (getItemList() == null)
			{
				return (0);
			}
			return getItemList().size();
		}

		@Override
		public Object getItem(int position)
        {
			if (getItemList() == null)
			{
				return null;
			}
			return (getItemList().get(position));
		}

		@Override
		public long getItemId(int position)
        {
			return (position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
        {
			GridCellViewHolder viewHolder;
			if (convertView == null)
			{
				convertView = inflater.inflate(R.layout.view_grid_cell, parent, false);
				
				viewHolder = new GridCellViewHolder();
				viewHolder.imageView = convertView.findViewById(R.id.imageViewY);
				viewHolder.iconView = convertView.findViewById(R.id.imageViewZ);
                viewHolder.selectView = convertView.findViewById(R.id.imageViewX);

                convertView.setTag(viewHolder);
			}
            else
            {
				viewHolder = (GridCellViewHolder)convertView.getTag();
			}

			ImageContentInfoEx infoEx = (ImageContentInfoEx) getItem(position);
            ICameraFileInfo item = (infoEx != null) ? infoEx.getFileInfo() : null;
			if (item == null)
            {
                viewHolder.imageView.setImageResource(R.drawable.ic_satellite_grey_24dp);
				viewHolder.iconView.setImageDrawable(null);
                viewHolder.selectView.setImageDrawable(null);
				return convertView;
			}
			String path = new File(item.getDirectoryPath(), item.getFilename()).getPath();
			Bitmap thumbnail = imageCache.get(path);
			if (thumbnail == null)
            {
                viewHolder.imageView.setImageResource(R.drawable.ic_satellite_grey_24dp);
				viewHolder.iconView.setImageDrawable(null);
				if (!gridViewIsScrolling)
                {
					if (executor.isShutdown())
                    {
						executor = Executors.newFixedThreadPool(1);
					}
					executor.execute(new ThumbnailLoader(viewHolder, path, infoEx.hasRaw()));
				}
			}
            else
            {
				viewHolder.imageView.setImageBitmap(thumbnail);
				if (path.toLowerCase().endsWith(MOVIE_SUFFIX))
                {
					viewHolder.iconView.setImageResource(R.drawable.ic_videocam_black_24dp);
				}
                else if (infoEx.hasRaw())
                {
                    viewHolder.iconView.setImageResource(R.drawable.ic_raw_black_1x);
                }
                else
                {
					viewHolder.iconView.setImageDrawable(null);
				}
			}
			if (infoEx.isSelected())
            {
                viewHolder.selectView.setImageResource(R.drawable.ic_check_green_24dp);
            }
            else
            {
                viewHolder.selectView.setImageDrawable(null);
            }
			return convertView;
		}
	}
	
	private class GridViewOnItemClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
    {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
	        ImagePagerViewFragment fragment = ImagePagerViewFragment.newInstance(playbackControl, runMode, contentList, position);
            FragmentActivity activity = getActivity();
	        if (activity != null)
	        {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(getId(), fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
		}

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id)
        {
            try
            {
                if (contentList == null)
                {
                    return (false);
                }
                ImageContentInfoEx infoEx = contentList.get(position);
                if (infoEx != null)
                {
                    boolean isChecked = infoEx.isSelected();
                    infoEx.setSelected(!isChecked);
                }
                view.invalidate();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            GridViewAdapter adapter = (GridViewAdapter) parent.getAdapter();
                            adapter.notifyDataSetChanged();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
                return (true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return (false);
        }
    }
	
	private class GridViewOnScrollListener implements AbsListView.OnScrollListener
    {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
			// No operation.
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
        {
			if (scrollState == SCROLL_STATE_IDLE)
			{
				gridViewIsScrolling = false;
				gridView.invalidateViews();
			}
			else if ((scrollState == SCROLL_STATE_FLING) || (scrollState == SCROLL_STATE_TOUCH_SCROLL))
			{
				gridViewIsScrolling = true;
				if (!executor.isShutdown())
				{
					executor.shutdownNow();
				}
			}
		}
	}

	private class ThumbnailLoader implements Runnable
    {
		private GridCellViewHolder viewHolder;
		private String path;
        private final boolean hasRaw;
		
		ThumbnailLoader(GridCellViewHolder viewHolder, String path, boolean hasRaw)
        {
			this.viewHolder = viewHolder;
			this.path = path;
            this.hasRaw = hasRaw;
		}
		
		@Override
		public void run()
        {
			class Box {
				boolean isDownloading = true;
			}
			final Box box = new Box();

			playbackControl.downloadContentThumbnail(null, path, new IDownloadThumbnailImageCallback()
            {
				@Override
				public void onCompleted(final Bitmap thumbnail, Map<String, Object> metadata)
				{
					if (thumbnail != null)
					{
                        try {
                            Log.v(TAG, "Thumbnail PATH : " + path + " size : " + thumbnail.getByteCount());
                            imageCache.put(path, thumbnail);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.imageView.setImageBitmap(thumbnail);
                                    if (path.toLowerCase().endsWith(MOVIE_SUFFIX)) {
                                        viewHolder.iconView.setImageResource(R.drawable.ic_videocam_black_24dp);
                                    } else if (hasRaw) {
                                        viewHolder.iconView.setImageResource(R.drawable.ic_raw_black_1x);
                                    } else {
                                        viewHolder.iconView.setImageDrawable(null);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
					box.isDownloading = false;  
				}
				
				@Override
				public void onErrorOccurred(Exception e)
				{
					box.isDownloading = false;
				}
			});

			// Waits to realize the serial download.
			while (box.isDownloading) {
				Thread.yield();
			}
		}
	}
	
	
	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------
	
	private void presentMessage(String title, String message)
    {
		Context context = getActivity();
		if (context == null)
		{
            return;
        }
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(message);
		builder.show();
	}
	
	private void runOnUiThread(Runnable action)
    {
		Activity activity = getActivity();
		if (activity == null)
		{
            return;
        }
		activity.runOnUiThread(action);
	}

/*
	private Bitmap createRotatedBitmap(byte[] data, Map<String, Object> metadata)
    {
		Bitmap bitmap = null;
		try
        {
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		catch (Throwable e)
        {
			e.printStackTrace();
		}
		if (bitmap == null)
		{
		    Log.v(TAG, "createRotatedBitmap() : bitmap is null : " + data.length);
			return (null);
		}
		
		int degrees = getRotationDegrees(data, metadata);
		if (degrees != 0)
		{
			Matrix m = new Matrix();
			m.postRotate(degrees);
			try
            {
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			}
			catch (Throwable e)
            {
				e.printStackTrace();
			}
		}
		return (bitmap);
	}
	
	private int getRotationDegrees(byte[] data, Map<String, Object> metadata)
    {
		int degrees = 0;
		int orientation = ExifInterface.ORIENTATION_UNDEFINED;
		
		if (metadata != null && metadata.containsKey("Orientation")) {
			orientation = Integer.parseInt((String)metadata.get("Orientation"));
		} else {
			// Gets image orientation to display a picture.
			try {
				File tempFile = File.createTempFile("temp", null);
				{
					FileOutputStream outStream = new FileOutputStream(tempFile.getAbsolutePath());
					outStream.write(data);
					outStream.close();
				}
				
				ExifInterface exifInterface = new ExifInterface(tempFile.getAbsolutePath());
				orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

				if (!tempFile.delete())
                {
                    Log.v(TAG, "File delete fail...");
                }
			}
			catch (IOException e)
            {
				e.printStackTrace();
			}
		}

		switch (orientation)
        {
            case ExifInterface.ORIENTATION_NORMAL:
                degrees = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;
            default:
                break;
		}
		return (degrees);
	}
*/
}
