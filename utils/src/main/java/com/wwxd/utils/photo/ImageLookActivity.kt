package com.wwxd.utils.photo

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.wwxd.base.AppConstant
import com.wwxd.base.BaseActivity
import com.wwxd.utils.R
import com.wwxd.utils.glide.GlideUtil
import kotlinx.android.synthetic.main.activity_image_look.*

/**
 * user：LuHao
 * time：2019/12/23 9:20
 * describe：查看大图
 */
class ImageLookActivity : BaseActivity() {
    override fun isFullWindow(): Boolean {
        return true
    }

    override fun getContentView(): Int {
        return R.layout.activity_image_look
    }

    private var images = ArrayList<Image>()

    override fun init() {
        val list = getParcelableList(AppConstant.LOOK_IMAGES)
        if (list == null) {
            finish()
            overridePendingTransition(0, R.anim.image_out_anim)
            return
        }
        images = list as ArrayList<Image>
        val index = getInt(AppConstant.LOOK_IMAGES_INDEX, 0)
        vpImg!!.adapter = ImageLookAdapter()
        if (images.size <= 1) textCount!!.text = "" else {
            textCount.setText(
                StringBuilder().append(index + 1).append("/").append(images.size)
            )
        }
        vpImg.setOnClickListener { finish() }
        vpImg.currentItem = index
        vpImg.addOnPageChangeListener(OnImageViewpagerChangeListener())
    }

    /**
     * 显示图片的viewpager适配器
     */
    private inner class ImageLookAdapter : PagerAdapter() {
        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View) // 删除页卡
        }

        // 这个方法用来实例化页卡
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val context = container.context
            val itemview = View.inflate(context, R.layout.item_image_look, null)
            val imgDef: ImageLookView = itemview.findViewById(R.id.imgDef)
            val imgLong: SubsamplingScaleImageView = itemview.findViewById(R.id.imgLong)
            imgDef.setVisibility(View.GONE)
            imgLong.setVisibility(View.GONE)
            val image = images[position]
            if (image.uriId > 0) {
                imgDef.setVisibility(View.VISIBLE)
                GlideUtil.show(imgDef, image.uriId)
            } else if (!TextUtils.isEmpty(image.path)) {
                imgDef.setVisibility(View.VISIBLE)
                GlideUtil.show(imgDef, image.path!!)
            }
            imgDef.setOnPhotoTapListener(OnImageClick())
            container.addView(itemview)
            return itemview //返回该view对象
        }

        override fun getCount(): Int {
            return images.size
        }

        /**
         * 点击退出
         */
        private inner class OnImageClick : ImageLookViewAttacher.OnPhotoTapListener {
            override fun onPhotoTap(view: View?, x: Float, y: Float) {
                finish()
                overridePendingTransition(0, R.anim.image_out_anim)
            }
        }
    }

    /**
     * 翻页监听
     */
    private inner class OnImageViewpagerChangeListener : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            //在这里加载图片
            if (images.size <= 1) textCount!!.text = "" else {
                textCount.setText(
                    StringBuilder().append(position + 1).append("/")
                        .append(images.size)
                )
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

}