package com.gabriel.jg.library.ui.picker;

import com.gabriel.jg.library.ui.base.MvpPresenter;
import com.gabriel.jg.library.ui.base.MvpView;

import java.util.ArrayList;

interface ImagePickerAlbumsMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void backDirectory();

    void setSourceDirectory(String sourceDirectory);

    int getListLength();

    boolean isSingleImage();

    ArrayList<String> getListPath();

    void configAttach();
}
