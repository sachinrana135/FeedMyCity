package com.alfanse.feedmycity.ui.intro

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.alfanse.feedmycity.FeedMyCityApplication
import com.alfanse.feedmycity.R
import com.alfanse.feedmycity.factory.ViewModelFactory
import com.alfanse.feedmycity.ui.usertypes.UserTypesActivity
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import javax.inject.Inject


class IntroActivity : AppIntro() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as FeedMyCityApplication).appComponent.inject(this)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(IntroViewModel::class.java)

        val sliderPage = SliderPage()
        sliderPage.title = getString(R.string.intro_welcome_title)
        sliderPage.description = getString(R.string.intro_welcome_desc)
        sliderPage.imageDrawable = R.drawable.welcome
        sliderPage.bgColor = Color.parseColor("#00BCD4")
        addSlide(AppIntroFragment.newInstance(sliderPage))

        val sliderPage1 = SliderPage()
        sliderPage1.title = getString(R.string.intro_group_title)
        sliderPage1.description = getString(R.string.intro_group_desc)
        sliderPage1.imageDrawable = R.drawable.group_white
        sliderPage1.bgColor = Color.parseColor("#F99401")
        addSlide(AppIntroFragment.newInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = getString(R.string.intro_donor_title)
        sliderPage2.description = getString(R.string.intro_donor_desc)
        sliderPage2.imageDrawable = R.drawable.donor_white
        sliderPage2.bgColor = Color.parseColor("#4CAF50")
        addSlide(AppIntroFragment.newInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.title = getString(R.string.intro_volunteer_title)
        sliderPage3.description = getString(R.string.intro_volunteer_desc)
        sliderPage3.imageDrawable = R.drawable.volunteer_white
        sliderPage3.bgColor = Color.parseColor("#5C6BC0")
        addSlide(AppIntroFragment.newInstance(sliderPage3))

        showSkipButton(false)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        viewModel.setFirstLaunch()
        startActivity(Intent(this, UserTypesActivity::class.java))
        finish()
    }
}
