package com.gabriel.jg.library.ui.picker;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.gabriel.jg.library.core.Constants;
import com.gabriel.jg.library.ui.base.BasePresenter;
import com.gabriel.jg.library.utils.CheckPermissionUtil;
import com.gabriel.jg.library.utils.CommonUtils;
import com.gabriel.jg.library.utils.PermissionUtil;

import java.io.File;
import java.util.ArrayList;

public class ImagePickerAlbumsPresenter<V extends ImagePickerAlbumsMvpView> extends BasePresenter<V> implements ImagePickerAlbumsMvpPresenter<V>, ImagePickerAlbumsAdapter.ImagePickerAlbumsListener {

    private ArrayList<String> listPath = new ArrayList<>();
    private ArrayList<ArrayList<File>> listFilesAux = new ArrayList<>();
    private File[] files;
    private ArrayList<File> filesCorrectBase = new ArrayList<>(), filesCorrectAlbum = new ArrayList<>();
    private boolean isSingleImage;
    private ImagePickerAlbumsAdapter adapter = new ImagePickerAlbumsAdapter(this);
    private FileSeekerAsyncTask fileSeekerAsyncTask;

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
        getMvpView().setRvItemsAdapter(adapter);
        bundleIsSingleImage(getMvpView().getBundle());
        getMvpView().checkReadPermission();
    }


    @Override
    public void configAttach(){
        files = addFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
        if (files.length > 0) {
            addFilesCorrectType(files);
            if (filesCorrectBase.size() > 0)
                setFilesCorrectAlbum();
        }
        updateView();
    }

    private void updateView() {
        getMvpView().isShowEmpty(listFilesAux.size() == 0);
        if (listFilesAux.size() > 0) {
            adapter.setList(listFilesAux.get(listFilesAux.size() - 1));
            getMvpView().setSourceDirectory(listFilesAux.get(listFilesAux.size() - 1).get(0).getParent());
            getMvpView().visibilityBackDirectory(listFilesAux.size() > 1);
        }
    }

    private void setFilesCorrectAlbum() {
        for (File f : files) {
            for (File f1 : filesCorrectBase) {
                if (f1.getAbsoluteFile().toString().startsWith(f.getAbsolutePath())) {
                    filesCorrectAlbum.add(f);
                    break;
                }
            }
        }
        listFilesAux.add(filesCorrectAlbum);
    }

    private void addFilesCorrectType(File[] filesAux) {
        for (File file : filesAux) {
            if (file.isDirectory() && file.listFiles() != null && file.listFiles().length != 0) {
                addFilesCorrectType(new File(file.getAbsolutePath()).listFiles());
            } else if (file.isFile() && CommonUtils.isImageFile(file, getMvpView().getmContext())) {
                filesCorrectBase.add(file);
            }
        }
    }

    private File[] addFiles(File rootDir) {
        File[] filesList = rootDir.listFiles();
        ArrayList<File> filesAux = new ArrayList<>();
        if (filesList == null)
            return new File[0];
        for (File file : filesList) {
            if (!file.isHidden() && !file.getName().startsWith("Android") && !file.getName().startsWith("data") && !file.getName().startsWith("log")) {
                filesAux.add(file);
            }
        }
        filesList = filesAux.toArray(new File[filesAux.size()]);
        return filesList;
    }

    private void bundleIsSingleImage(Bundle bundle) {
        if (bundle == null)
            return;
        isSingleImage = bundle.getBoolean("isSingleImage");
    }

    @Override
    public void backDirectory() {
        listFilesAux.remove(listFilesAux.size() - 1);
        updateView();
    }

    @Override
    public ArrayList<String> getListPath() {
        return listPath;
    }

    @Override
    public boolean getListItem(String path) {
        return listPath.contains(path);
    }

    @Override
    public void setListItem(String path) {
        listPath.add(path);
    }

    @Override
    public void removeListItem(String path) {
        listPath.remove(path);
    }

    @Override
    public void setSourceDirectory(String sourceDirectory) {
        getMvpView().setSourceDirectory(sourceDirectory);
    }

    @Override
    public int getListLength() {
        return listFilesAux.size();
    }

    @Override
    public void onDetach() {
        listPath = null;
        listFilesAux = null;
        files = null;
        filesCorrectBase = null;
        filesCorrectAlbum = null;
        if (adapter != null)
            adapter.detach();
        adapter = null;
    }

    @Override
    public boolean isSingleImage() {
        return isSingleImage;
    }

    @Override
    public void setNewListFile(File rootDir) {
        fileSeekerAsyncTask = new FileSeekerAsyncTask();
        fileSeekerAsyncTask.execute(rootDir);
    }

    public ArrayList<File> setFilesCorrectAlbum(ArrayList<File> filAlbum) {
        ArrayList<File> listFiles = new ArrayList<>();
        for (File f : filAlbum) {
            for (File f1 : filesCorrectBase) {
                if (f1.getAbsoluteFile().toString().startsWith(f.getAbsolutePath())) {
                    listFiles.add(f);
                    break;
                }
            }
        }
        return listFiles;
    }

    @Override
    public void addFileAlbums(File file) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.FilePathKey, file.getAbsolutePath());
        getMvpView().setBundle(bundle);
    }

    private class FileSeekerAsyncTask extends AsyncTask<File, Void, ArrayList<File>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getMvpView().showLoading();
        }

        @Override
        protected ArrayList<File> doInBackground(File... files) {
            if (files == null || files[0] == null)
                return null;
            ArrayList<File> filesAux = new ArrayList<>();
            for (File file : files[0].listFiles()) {
                if ((file.isDirectory() && file.listFiles() != null && file.listFiles().length > 0) || (file.isFile() && CommonUtils.isImageFile(file, getMvpView().getmContext())))
                    filesAux.add(file);
            }
            listFilesAux.add(setFilesCorrectAlbum(filesAux));
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<File> arrayLists) {
            super.onPostExecute(arrayLists);
            getMvpView().hideLoading();
            updateView();
        }
    }
}
