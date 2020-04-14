package com.alfanse.feedmycity.ui.app_upgrade

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.ui.app_maintenance.AppUpgradeViewModel
import com.alfanse.feedmycity.utils.BUNDLE_KEY_FORCE_UPGRADE
import com.alfanse.feedmycity.utils.BUNDLE_KEY_SKIP_CLICKED
import com.alfanse.feedmycity.utils.WEB_URL_UPGRADE_APP
import kotlinx.android.synthetic.main.activity_app_upgrade.*
import javax.inject.Inject


class AppUpgradeActivity : AppCompatActivity() {

    private var isForcedUpgrade = false
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: AppUpgradeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_upgrade)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.txt_update_app)

        (application as FeedMyCityApplication).appComponent.inject(this)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(AppUpgradeViewModel::class.java)

        initListener()
        val intent = intent
        if (intent.extras != null) {
            if (intent.hasExtra(BUNDLE_KEY_FORCE_UPGRADE)) {
                isForcedUpgrade = intent.getBooleanExtra(BUNDLE_KEY_FORCE_UPGRADE, false)
            }
        }

        if (isForcedUpgrade) {
            cbDontAskAgain.visibility =View.GONE
            text_skip.visibility = View.GONE
        } else {
            text_skip.visibility = View.VISIBLE
            cbDontAskAgain.visibility =View.VISIBLE
        }

        webview.settings.javaScriptEnabled = true
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap?
            ) {
                layout_loading.visibility = View.VISIBLE
                layout_content.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView, url: String) {
                layout_loading.visibility = View.GONE
                layout_content.visibility = View.VISIBLE
            }
        }
        webview.loadUrl(WEB_URL_UPGRADE_APP)
    }

    private fun initListener() {
        text_skip.setOnClickListener {
            viewModel.updateVersionUpgradeState(cbDontAskAgain.isChecked)
            setSkipResult()
        }

        button_update_app.setOnClickListener {
            goToPlayStore()
        }

    }

    private fun setSkipResult() {
        val resultIntent = Intent()
        resultIntent.putExtra(BUNDLE_KEY_SKIP_CLICKED, true)
        setResult(Activity.RESULT_OK, resultIntent)
        finish() //finishing activity
    }

    override fun onBackPressed() {
        if(isForcedUpgrade) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }else{
            setSkipResult()
        }
    }

    fun goToPlayStore() {
        val appPackageName: String = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}

