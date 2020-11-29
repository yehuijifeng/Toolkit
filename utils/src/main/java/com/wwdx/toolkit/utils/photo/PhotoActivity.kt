package com.wwdx.toolkit.utils.photo

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.loader.app.LoaderManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wwdx.toolkit.utils.ToastUtil
import com.wwdx.toolkit.utils.glide.GlideUtil
import com.wwxd.toolkit.R
import com.wwxd.toolkit.base.BaseActivity
import com.wwxd.toolkit.base.NoDoubleClickListener
import kotlinx.android.synthetic.main.activity_photo.*

/**
 * user：LuHao
 * time：2019/12/20 13:43
 * describe：系统相册
 */
class PhotoActivity : BaseActivity() {
    private var maxImageNum = 0
    private var folders: List<Folder>? = null //系统相册
    private var selectImages = ArrayList<Image>()
    private var folderListPow: FolderListPow? = null  //系统相册选择
    private var folderName = ""//选择的相册名字
    private val recyclerAdapter = RecyclerAdapter()
    override fun setContentView(): Int {
        return R.layout.activity_photo
    }

    override fun init() {
        maxImageNum = getInt(PhotoConstant.MAX_IMAGE_NUM, 0)
        val list = getParcelableList(PhotoConstant.LOOK_IMAGES)
        if (list != null)
            selectImages.addAll(list as ArrayList<Image>)
        imgClose.setOnClickListener { finish() }
        llPhoto.setOnClickListener {
            showFolderListPow()
        }
        textPreview.setOnClickListener {
            //预览大图
            if (selectImages.size > 0) {
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    PhotoConstant.LOOK_IMAGES, selectImages
                )
                startActivity(ImageLookActivity::class, bundle)
                overridePendingTransition(R.anim.image_in_anim, 0)
            }
        }
        textSave.setOnClickListener {
            //选择完成
            if (selectImages.size > 0) {
                intent.putParcelableArrayListExtra(
                    PhotoConstant.LOOK_IMAGES,
                    selectImages
                )
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        updateSelectFolder()
        rvPhoto.adapter = recyclerAdapter
        rvPhoto.setLayoutManager(GridLayoutManager(this, 3))
        LoaderManager.getInstance(this).initLoader(
            1,
            null,
            OnPhotoLoaderCallBack(
                this,
                OnPhotoLoadListener()
            )
        )
    }

    private inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView
        var viewSelect: View
        var imgSelect: ImageView

        init {
            imgPhoto = itemView.findViewById(R.id.imgPhoto)
            viewSelect = itemView.findViewById(R.id.viewSelect)
            imgSelect = itemView.findViewById(R.id.imgSelect)
        }
    }

    //列表适配器
    private inner class RecyclerAdapter : RecyclerView.Adapter<ImageViewHolder>() {
        var data = ArrayList<Image>()
        private var itemHeight = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
            return ImageViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val image: Image = data.get(position)
            holder.itemView.setOnClickListener(OnLookImageClick(image.uriId))
            if (itemHeight == 0) {
                holder.imgPhoto.viewTreeObserver.addOnPreDrawListener(
                    OnShowSignImageWidth(
                        holder.imgPhoto,
                        GlideUtil.getUri(image.uriId)
                    )
                )
            } else {
                GlideUtil.showRound(
                    holder.imgPhoto,
                    GlideUtil.getUri(image.uriId),
                    itemHeight,
                    itemHeight,
                    10F,
                    R.drawable.ic_pow_photo
                )
            }
            for (image1 in selectImages) {
                if (image1.uriId > 0 && image1.uriId == image.uriId) {
                    holder.imgSelect.isSelected = true
                    holder.viewSelect.visibility = View.INVISIBLE
                } else if (!TextUtils.isEmpty(image1.path) && image1.path.equals(image.path)) {
                    holder.imgSelect.isSelected = true
                    holder.viewSelect.visibility = View.INVISIBLE
                } else {
                    holder.imgSelect.isSelected = false
                    holder.viewSelect.visibility = View.INVISIBLE
                }
            }
            holder.imgSelect.setOnClickListener(
                OnSelectImageClick(
                    image,
                    holder.viewSelect
                )
            )
        }

        //查看大图
        private inner class OnLookImageClick(private val imageUriId: Long) :
            NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                val bundle = Bundle()
                val image = Image()
                image.uriId = imageUriId
                val list = ArrayList<Image>()
                list.add(image)
                bundle.putParcelableArrayList(PhotoConstant.LOOK_IMAGES, list)
                startActivity(ImageLookActivity::class, bundle)
                overridePendingTransition(R.anim.image_in_anim, 0)
            }
        }

        //选中图片
        private inner class OnSelectImageClick(
            private val image: Image,
            private val rbSelect: View
        ) :
            NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                v.isSelected = !v.isSelected
                if (!v.isSelected) { //取消选中
                    for (image1 in selectImages) {
                        if (image1.uriId > 0 && image1.uriId == image.uriId) {
                            selectImages.remove(image1)
                        } else if (!TextUtils.isEmpty(image1.path) && image1.path.equals(image.path)) {
                            selectImages.remove(image1)
                        }
                    }
                    rbSelect.visibility = View.INVISIBLE
                    updateSelectFolder()
                } else {
                    if (selectImages.size >= maxImageNum) {
                        ToastUtil.showLongToast(
                            getString(R.string.str_max_select_images_num_one) + maxImageNum + getString(
                                R.string.str_str_max_select_images_num_two
                            )
                        )
                        v.isSelected = false
                    } else {
                        selectImages.add(image)
                        rbSelect.visibility = View.VISIBLE
                        updateSelectFolder()
                    }
                }
            }
        }

        //测量一张图片的宽度
        private inner class OnShowSignImageWidth(
            private val imageView: ImageView,
            private val uri: Uri
        ) :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                if (itemHeight == 0) {
                    itemHeight = imageView.measuredWidth
                }
                GlideUtil.showRound(
                    imageView,
                    uri,
                    itemHeight,
                    itemHeight,
                    10F,
                    R.drawable.ic_pow_photo
                )
                return true
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }

    //加载系统相册回调
    private inner class OnPhotoLoadListener : ILoadPhotoListener {
        override fun over(list: List<Folder>) {
            if (folders == null) {
                if (list.size > 0) {
                    folders = list
                    //更改选择的相册
                    val defFolder: Folder = folders!!.get(0)
                    folderName = defFolder.name
                    recyclerAdapter.data = defFolder.images
                    recyclerAdapter.notifyDataSetChanged()
                    updateSelectFolder()
                } else {
                    recyclerAdapter.data = ArrayList()
                    recyclerAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    //刷新数据
    private fun updateSelectFolder() {
        textPhoto.text = folderName
        textSave.text =
            StringBuilder(getString(R.string.str_coment) + "(" + selectImages.size + "/" + maxImageNum + ")")
        textPreview.isSelected = selectImages.size > 0
        textSave.isSelected = selectImages.size > 0
    }


    //系统相册选择
    private fun showFolderListPow() {
        if (folderListPow == null) {
            if (folders == null || folders!!.size == 0) return
            folderListPow = FolderListPow(this, folders, object : OnSelectFolderListener {
                override fun onSelect(folder: Folder) {
                    recyclerAdapter.data.clear()
                    recyclerAdapter.data = folder.images
                    folderName = folder.name
                    updateSelectFolder()
                }
            })
        }
        folderListPow!!.showAtLocation(llPhoto)
    }

}