package com.gabriel.jg.library.ui.picker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gabriel.jg.library.R;
import com.gabriel.jg.library.core.Constants;
import com.gabriel.jg.library.ui.base.BaseActivity;
import com.gabriel.jg.library.utils.CheckPermissionUtil;
import com.gabriel.jg.library.utils.CommonUtils;
import com.gabriel.jg.library.utils.PermissionUtil;


public class ImagePickerAlbumsActivity extends BaseActivity<ImagePickerAlbumsMvpPresenter> implements ImagePickerAlbumsMvpView, View.OnClickListener {

    private RecyclerView rvItems;
    private TextView tvSourceDirectory, tvEmptyList;
    private ImageView ivBackDirectory;
    private RelativeLayout rlDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        activeHomeBackButton();
        presenter = new ImagePickerAlbumsPresenter();
        presenter.onAttach(this);
    }

    @Override
    public void checkReadPermission() {
        CheckPermissionUtil.checkReadSd(this, new PermissionUtil.ReqPermissionCallback() {
            @Override
            public void onResult(boolean success) {
                if (success)
                    presenter.configAttach();
                else
                    finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (presenter.getListLength() > 1)
            presenter.backDirectory();
        else
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkReadPermission();
    }

    @Override
    protected void setUp() {
        rvItems = findViewById(R.id.GalleryImage_rvItems);
        tvSourceDirectory = findViewById(R.id.GalleryImage_tvSourceDirectory);
        ivBackDirectory = findViewById(R.id.GalleryImage_ivImageDirectory);
        rlDirectory = findViewById(R.id.GalleryImage_rlDirectory);
        tvEmptyList = findViewById(R.id.GalleryImage_tvEmptyList);
        rvItems.setLayoutManager(new LinearLayoutManager(getmContext()));
        rlDirectory.setVisibility(View.VISIBLE);
        ivBackDirectory.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (!presenter.isSingleImage())
            getMenuInflater().inflate(R.menu.menu_gallery_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        String listString = CommonUtils.toJson(presenter.getListPath().toArray());
        bundle.putString(Constants.FilePathKey, listString);
        setBundle(bundle);
        return true;
    }

    @Override
    public void setSourceDirectory(String sourceDirectory) {
        tvSourceDirectory.setText(sourceDirectory);
    }

    @Override
    public void isShowEmpty(boolean show) {
        tvEmptyList.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.GalleryImage_ivImageDirectory) {
            presenter.backDirectory();
        }
    }

    @Override
    public void setRvItemsAdapter(RecyclerView.Adapter adapter) {
        rvItems.setAdapter(adapter);
    }

    @Override
    public Bundle getBundle() {
        return getIntent() != null ? getIntent().getExtras() : null;
    }

    @Override
    public void setBundle(Bundle bundle) {
        Intent i = new Intent();
        i.putExtras(bundle);
        this.setResult(RESULT_OK, i);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        rvItems = null;
        tvSourceDirectory = null;
        tvEmptyList = null;
        ivBackDirectory = null;
        rlDirectory = null;
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void visibilityBackDirectory(boolean show) {
        ivBackDirectory.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public Context getmContext() {
        return getApplicationContext();
    }
}
