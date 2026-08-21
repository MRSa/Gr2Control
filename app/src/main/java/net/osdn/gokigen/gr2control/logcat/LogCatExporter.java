package net.osdn.gokigen.gr2control.logcat;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import net.osdn.gokigen.gr2control.R;
import net.osdn.gokigen.gr2control.scene.ConfirmationDialog;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

class LogCatExporter implements AdapterView.OnItemLongClickListener
{
    private static final String TAG = LogCatExporter.class.getSimpleName();
    private final WeakReference<Activity> activityRef;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    LogCatExporter(@NonNull Activity context)
    {
        this.activityRef = new WeakReference<>(context);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l)
    {
        Log.v(TAG, "onItemLongClick()");

        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing())
        {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed())
            {
                return false;
            }
        }

        ConfirmationDialog confirm = ConfirmationDialog.newInstance(activity);
        confirm.show(R.string.dialog_confirm_title_output_log, R.string.dialog_confirm_message_output_log, () -> {
            Log.v(TAG, "confirm()");
            exportLog(adapterView);
        });
        return true;
    }

    private void exportLog(final AdapterView<?> adapterView)
    {
        executor.execute(() -> {
            Activity activity = activityRef.get();
            if (activity == null || activity.isFinishing())
            {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (activity.isDestroyed())
                {
                    return;
                }
            }

            try
            {
                Adapter adapter = adapterView.getAdapter();
                if (adapter == null)
                {
                    return;
                }

                int count = adapter.getCount();
                StringBuilder buf = new StringBuilder(count * 64); // メモリ再確保を低減する基本容量指定
                String lineSeparator = System.getProperty("line.separator");

                for (int index = 0; index < count; index++)
                {
                    Object item = adapter.getItem(index);
                    if (item != null)
                    {
                        buf.append(item).append(lineSeparator);
                    }
                }

                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, "debug log for " + activity.getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, buf.toString());

                // UIスレッドでIntentを発行
                new Handler(Looper.getMainLooper()).post(() -> {
                    Activity currentActivity = activityRef.get();
                    if (activity.isFinishing())
                    {
                        currentActivity.startActivity(Intent.createChooser(intent, null));
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (activity.isDestroyed())
                        {
                            currentActivity.startActivity(Intent.createChooser(intent, null));
                        }
                    }
                });
            }
            catch (Exception e)
            {
                Log.e(TAG, "Failed to export logcat data", e);
            }
        });
    }
}
