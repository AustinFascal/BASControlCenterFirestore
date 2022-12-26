package com.ptbas.controlcenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.ptbas.controlcenter.R
import com.ptbas.controlcenter.model.OnBoardingModel

class OnBoardAdapter(
    private var context: Context,
    private var onBoardingModelList: List<OnBoardingModel>) :
        PagerAdapter() {

    override fun getCount(): Int {
        return onBoardingModelList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view:View = LayoutInflater.from(context).inflate(R.layout.item_layout_onboard, null)

        val imageView: ImageView = view.findViewById(R.id.imageView)
        val title: TextView = view.findViewById(R.id.title)
        val desc: TextView = view.findViewById(R.id.desc)

        imageView.setImageResource(onBoardingModelList[position].imageUrl)
        title.text = onBoardingModelList[position].title
        desc.text = onBoardingModelList[position].desc

        container.addView(view)
        return view
    }
}