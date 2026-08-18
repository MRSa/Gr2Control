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

class Gr2ControlMain : AppCompatActivity() {

    private lateinit var interfaceProvider: IInterfaceProvider
    private lateinit var sceneUpdater: CameraSceneUpdater
    private lateinit var liveViewFragment: LiveViewFragment

    // 実行中の OS バージョンに応じて要求する権限一覧を取得
    private val requiredPermissions: Array<String>
        get()
        {
            val permissions = mutableListOf<String>()

            // 通常権限
            permissions.add(permission.ACCESS_NETWORK_STATE)
            permissions.add(permission.ACCESS_WIFI_STATE)
            permissions.add(permission.INTERNET)
            permissions.add(permission.VIBRATE)

            // API 28 (Android 9.0) 以下のみストレージ書き込み権限を要求
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            {
                permissions.add(permission.WRITE_EXTERNAL_STORAGE)
            }

            // API 33 (Android 13) 以上で Wi-Fi 機器検出権限を追加
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            {
                permissions.add(permission.NEARBY_WIFI_DEVICES)
            }

            // API 37 (Android 17) 以上向けローカルネットワーク権限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN)
            {
                permissions.add("android.permission.ACCESS_LOCAL_NETWORK")
            }

            return permissions.toTypedArray()
        }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        if (allPermissionsGranted()) {
            Log.v(TAG, "Permissions granted via launcher")
            setupAndConnect()
        } else {
            Log.v(TAG, "----- APPLICATION LAUNCH ABORTED (Permission Rejected) -----")
            Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gr2_control_main)

        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val baseLayout = findViewById<View>(R.id.base_layout)
        if (baseLayout != null) {
            setupWindowInset(baseLayout)
        }

        // 権限の確認とリクエスト
        Log.v(TAG, " ----- SET PERMISSIONS -----")
        if (allPermissionsGranted()) {
            setupAndConnect()
        } else {
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    private fun setupAndConnect() {
        initializeClass()
        onReadyClass()
    }

    private fun setupWindowInset(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
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

    private fun allPermissionsGranted(): Boolean {
        for (param in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, param) != PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, " Permission denied: $param (SDK: ${Build.VERSION.SDK_INT})")
                return false
            }
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        try {
            if (::interfaceProvider.isInitialized) {
                interfaceProvider.cameraConnection?.stopWatchWifiStatus(this)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause", e)
        }
    }

    // クラスの初期化
    private fun initializeClass() {
        try {
            if (!::sceneUpdater.isInitialized) {
                sceneUpdater = CameraSceneUpdater.newInstance(this)
            }
            if (!::interfaceProvider.isInitialized) {
                interfaceProvider = CameraInterfaceProvider.newInstance(this, sceneUpdater)
            }
            if (!::liveViewFragment.isInitialized) {
                liveViewFragment = LiveViewFragment.newInstance(sceneUpdater, interfaceProvider)
            }

            sceneUpdater.registerInterface(liveViewFragment, interfaceProvider)

            // Fragmentの重複生成帽子
            if (supportFragmentManager.findFragmentById(R.id.fragment1) == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment1, liveViewFragment)
                    .commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in initializeClass", e)
        }
    }

    // 初期化終了時の処理 (カメラへの自動接続)
    private fun onReadyClass() {
        try {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val isAutoConnectCamera = preferences.getBoolean(
                IPreferencePropertyAccessor.AUTO_CONNECT_TO_CAMERA,
                true
            )
            Log.v(TAG, "isAutoConnectCamera() : $isAutoConnectCamera")

            if (isAutoConnectCamera && ::sceneUpdater.isInitialized) {
                sceneUpdater.changeCameraConnection()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onReadyClass", e)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.v(TAG, "onKeyDown() $keyCode")
        if (event.action == KeyEvent.ACTION_DOWN &&
            (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_CAMERA)
        ) {
            if (::liveViewFragment.isInitialized) {
                return liveViewFragment.handleKeyDown(keyCode, event)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        private val TAG = Gr2ControlMain::class.java.simpleName
    }
}
