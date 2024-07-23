package net.osdn.gokigen.gr2control

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.preference.PreferenceManager
import net.osdn.gokigen.gr2control.camera.CameraInterfaceProvider
import net.osdn.gokigen.gr2control.camera.IInterfaceProvider
import net.osdn.gokigen.gr2control.liveview.LiveViewFragment
import net.osdn.gokigen.gr2control.preference.IPreferencePropertyAccessor
import net.osdn.gokigen.gr2control.scene.CameraSceneUpdater

/**
 *
 *
 */
class Gr2ControlMain : AppCompatActivity()
{
    private lateinit var interfaceProvider: IInterfaceProvider
    private lateinit var scenceUpdater: CameraSceneUpdater
    private lateinit var liveViewFragment: LiveViewFragment

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // 画面表示の準備
        setContentView(R.layout.activity_gr2_control_main)
        try
        {
            val bar = supportActionBar
            bar?.hide()
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        try
        {
            setupWindowInset(findViewById(R.id.base_layout))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        initializeClass()

        try
        {
            ///////// SET PERMISSIONS /////////
            Log.v(TAG, " ----- SET PERMISSIONS -----")
            if (!allPermissionsGranted())
            {
                val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
                {
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_NEED_PERMISSIONS)
                    if(!allPermissionsGranted())
                    {
                        // Abort launch application because required permissions was rejected.
                        Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
                        Log.v(TAG, "----- APPLICATION LAUNCH ABORTED -----")
                        finish()
                    }
                }
                requestPermission.launch(REQUIRED_PERMISSIONS)
            }
            onReadyClass()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun setupWindowInset(view: View)
    {
        try
        {
            // Display cutout insets
            //   https://developer.android.com/develop/ui/views/layout/edge-to-edge
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            or WindowInsetsCompat.Type.displayCutout()
                )
                v.updatePadding(
                    left = bars.left,
                    top = bars.top,
                    right = bars.right,
                    bottom = bars.bottom,
                )
                WindowInsetsCompat.CONSUMED
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun allPermissionsGranted() : Boolean
    {
        var result = true
        for (param in REQUIRED_PERMISSIONS)
        {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    param
                ) != PackageManager.PERMISSION_GRANTED
            )
            {
                // ----- Permission Denied...
                if ((param == permission.ACCESS_MEDIA_LOCATION)&&(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q))
                {
                    //　この場合は権限付与の判断を除外 (デバイスが (10) よりも古く、ACCESS_MEDIA_LOCATION がない場合）
                }
                else if ((param == permission.READ_EXTERNAL_STORAGE)&&(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU))
                {
                    // この場合は、権限付与の判断を除外 (SDK: 33以上はエラーになる...)
                }
                else if ((param == permission.WRITE_EXTERNAL_STORAGE)&&(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU))
                {
                    // この場合は、権限付与の判断を除外 (SDK: 33以上はエラーになる...)
                }
                else
                {
                    // ----- 権限が得られなかった場合...
                    Log.v(TAG, " Permission: $param : ${Build.VERSION.SDK_INT}")
                    result = false
                }
            }
        }
        return (result)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.v(TAG, "------------------------- onRequestPermissionsResult() ")
        if (requestCode == REQUEST_NEED_PERMISSIONS)
        {
            if (allPermissionsGranted())
            {
                // ----- 権限が有効だった、最初の画面を開く
                Log.v(TAG, "onRequestPermissionsResult()")
                onReadyClass()
            }
            else
            {
                Log.v(TAG, "----- onRequestPermissionsResult() : false")
                Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     *
     */
    override fun onPause()
    {
        super.onPause()
        try
        {
            if (::interfaceProvider.isInitialized)
            {
                val connection = interfaceProvider.cameraConnection
                connection?.stopWatchWifiStatus(this)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * クラスの初期化 (instantiate)
     *
     */
    private fun initializeClass()
    {
        try
        {
            if (!::scenceUpdater.isInitialized)
            {
                scenceUpdater = CameraSceneUpdater.newInstance(this)
            }
            if (!::interfaceProvider.isInitialized)
            {
                interfaceProvider = CameraInterfaceProvider.newInstance(this, scenceUpdater)
            }
            if (!::liveViewFragment.isInitialized)
            {
                liveViewFragment = LiveViewFragment.newInstance(scenceUpdater, interfaceProvider)
            }
            scenceUpdater.registerInterface(liveViewFragment, interfaceProvider)
            @Suppress("DEPRECATION")
            liveViewFragment.retainInstance = true
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment1, liveViewFragment)
            transaction.commitAllowingStateLoss()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * 初期化終了時の処理 (カメラへの自動接続)
     */
    private fun onReadyClass()
    {
        try
        {
            // カメラに自動接続するかどうか確認
            val preferences = PreferenceManager.getDefaultSharedPreferences(
                this
            )
            val isAutoConnectCamera =
                preferences.getBoolean(IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA, true)
            Log.v(TAG, "isAutoConnectCamera() : $isAutoConnectCamera")

            // カメラに接続する
            if (isAutoConnectCamera)
            {
                // 自動接続の指示があったとき
                if (::scenceUpdater.isInitialized)
                {
                    scenceUpdater.changeCameraConnection()
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean
    {
        Log.v(TAG, "onKeyDown() $keyCode")
        try
        {
            if ((event.action == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_VOLUME_UP) || (keyCode == KeyEvent.KEYCODE_CAMERA)))
            {
                if (::liveViewFragment.isInitialized)
                {
                    return (liveViewFragment.handleKeyDown(keyCode, event))
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (super.onKeyDown(keyCode, event))
    }

    companion object
    {
        private val TAG = Gr2ControlMain::class.java.simpleName
        private const val REQUEST_NEED_PERMISSIONS = 1010
        private val REQUIRED_PERMISSIONS = arrayOf(
            permission.WRITE_EXTERNAL_STORAGE,
            permission.ACCESS_NETWORK_STATE,
            permission.ACCESS_WIFI_STATE,
            permission.INTERNET,
            permission.VIBRATE,
        )
    }
}
