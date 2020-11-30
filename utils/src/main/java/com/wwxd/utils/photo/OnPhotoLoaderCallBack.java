package com.wwxd.utils.photo;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * user：LuHao
 * time：2019/12/20 10:46
 * describe：查询系统相册
 */
public class OnPhotoLoaderCallBack implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private List<Folder> folders = new ArrayList<>();
    private ILoadPhotoListener iLoadPhotoListener;

    public OnPhotoLoaderCallBack(Context context, ILoadPhotoListener iLoadPhotoListener) {
        this.context = context;
        this.iLoadPhotoListener = iLoadPhotoListener;
    }

    //查询参数值
    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE};
    private int minWidth, minHeight, minSize;//参数配置
    private String[] mimeType;//查询图片后缀类型

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public void setMimeType(String[] mimeType) {
        this.mimeType = mimeType;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // 根据图片设置参数新增验证条件
        StringBuilder selectionArgs = new StringBuilder();
        if (minWidth != 0) {
            selectionArgs.append(MediaStore.Images.Media.WIDTH).append(" >= ").append(minWidth);
        }
        if (minHeight != 0) {
            selectionArgs.append("".equals(selectionArgs.toString()) ? "" : " and ");
            selectionArgs.append(MediaStore.Images.Media.HEIGHT).append(" >= ").append(minHeight);
        }
        if (minSize != 0) {
            selectionArgs.append("".equals(selectionArgs.toString()) ? "" : " and ");
            selectionArgs.append(MediaStore.Images.Media.SIZE).append(" >= ").append(minSize);
        }
        if (mimeType != null) {
            for (String suffix : mimeType) {
                selectionArgs.append(" and ").append("not like '%.").append(suffix).append("'");
            }
        }
        //时间倒叙排序查询
        return new CursorLoader(context,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                selectionArgs.toString(),
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //查询结果
        if (cursor != null && cursor.getCount() > 0 && folders.size() == 0) {
            cursor.moveToFirst();//移动到第一个
            do {
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                int uriId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                if (TextUtils.isEmpty(filePath) || uriId == 0) continue;
                Image image = new Image();
                image.setPath(filePath);
                image.setUriId(uriId);
                image.setSize(size);
                File imageFile = new File(filePath);
                File folderFile = imageFile.getParentFile();
                if (folderFile == null || !imageFile.exists()) {
                    continue;
                }
                Folder folder = null;
                for (Folder folder1 : folders) {
                    if (!TextUtils.isEmpty(folder1.getPath())
                            && !TextUtils.isEmpty(folderFile.getAbsolutePath())
                            && folder1.getPath().equals(folderFile.getAbsolutePath())) {
                        folder = folder1;
                        break;
                    }
                }
                if (folder == null) {
                    folder = new Folder();
                    folder.setCover(image);
                    folder.setName(folderFile.getName());
                    folder.setPath(folderFile.getAbsolutePath());
                    folders.add(folder);
                }
                folder.addImage(image);
            } while (cursor.moveToNext());
        }
        if (iLoadPhotoListener != null)
            iLoadPhotoListener.over(folders);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
