package com.alfanse.feedmycity.ui.intro

import android.os.Bundle
import com.alfanse.feedmycity.R
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage


class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val sliderPage = SliderPage()
        sliderPage.title = title
        sliderPage.description = getString(R.string.google_app_id)
       /* sliderPage.imageDrawable = image
        sliderPage.bgColor = backgroundColor*/
        addSlide(AppIntroFragment.newInstance(sliderPage))
    }
}
