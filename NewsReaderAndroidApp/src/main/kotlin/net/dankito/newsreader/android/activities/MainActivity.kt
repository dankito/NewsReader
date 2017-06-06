package net.dankito.newsreader.android.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.dankito.newsreader.R
import net.dankito.newsreader.android.adapter.ArticleSummaryExtractorsAdapter
import net.dankito.newsreader.android.dialogs.AddArticleSummaryExtractorDialog
import net.dankito.newsreader.android.util.AndroidFileStorageService
import net.dankito.newsreader.summary.config.ArticleSummaryExtractorConfig
import net.dankito.newsreader.summary.config.ArticleSummaryExtractorConfigManager
import net.dankito.newsreader.summary.config.ConfigChangedListener


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var extractorsConfigManager: ArticleSummaryExtractorConfigManager // TODO: inject

    private lateinit var adapter: ArticleSummaryExtractorsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractorsConfigManager = ArticleSummaryExtractorConfigManager(AndroidFileStorageService(this))
        extractorsConfigManager.addListener(articleSummaryExtractorConfigChangedListener)

        setupUI()
    }

    private fun setupUI() {
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fabAddArticleSummaryExtractor.setOnClickListener { showAddArticleSummaryExtractorView() }

//        var toggle = ActionBarDrawerToggle(
//            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer_layout.setDrawerListener(toggle);
//        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this)

        adapter = ArticleSummaryExtractorsAdapter(extractorsConfigManager.getConfigs())

        lstvwArticleSummaryExtractors.adapter = adapter
        lstvwArticleSummaryExtractors.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            articleSummaryExtractorSelected(lstvwArticleSummaryExtractors.adapter.getItem(position) as ArticleSummaryExtractorConfig)
        }
    }


    override fun onResume() {
        super.onResume()

        adapter.setItems(extractorsConfigManager.getConfigs())
    }

    override fun onDestroy() {
        super.onDestroy()

        extractorsConfigManager.removeListener(articleSummaryExtractorConfigChangedListener)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        //        if (id == R.id.action_settings) {
        //            return true;
        //        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }


    private fun articleSummaryExtractorSelected(extractorConfig: ArticleSummaryExtractorConfig) {
        val articleSummaryActivityIntent = Intent(this, ArticleSummaryActivity::class.java)

        articleSummaryActivityIntent.putExtra(ArticleSummaryActivity.EXTRACTOR_URL_INTENT_EXTRA_NAME, extractorConfig.url)

        startActivity(articleSummaryActivityIntent)
    }

    private fun showAddArticleSummaryExtractorView() {
        val editNameDialog = AddArticleSummaryExtractorDialog()
        editNameDialog.show(supportFragmentManager, AddArticleSummaryExtractorDialog.TAG)
    }


    private val articleSummaryExtractorConfigChangedListener = object : ConfigChangedListener {
        override fun configChanged(config: ArticleSummaryExtractorConfig) {
            runOnUiThread { adapter.notifyDataSetChanged() }
        }
    }

}