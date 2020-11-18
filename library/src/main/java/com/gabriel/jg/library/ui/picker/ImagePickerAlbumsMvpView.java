package com.gabriel.jg.library.ui.picker;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.gabriel.jg.library.ui.base.MvpView;

interface ImagePickerAlbumsMvpView extends MvpView {

    void setSourceDirectory(String sourceDirectory);

    void isShowEmpty(boolean show);

    void setRvItemsAdapter(RecyclerView.Adapter adapter);

    Bundle getBundle();

    void setBundle(Bundle bundle);

    void visibilityBackDirectory(boolean show);

    void checkReadPermission();
}
