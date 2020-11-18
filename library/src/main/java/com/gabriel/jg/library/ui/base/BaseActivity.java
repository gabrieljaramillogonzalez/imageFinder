package com.gabriel.jg.library.ui.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gabriel.jg.library.R;
import com.gabriel.jg.library.utils.CommonUtils;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Field;

public abstract class BaseActivity<T extends MvpPresenter> extends AppCompatActivity implements MvpView, BaseFragment.Callback {

    protected T presenter;
    protected boolean hasInputErrorFocused;
    private ProgressDialog mProgressDialog;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setUp();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (presenter == null)
            throw new MvpPresenterNotAttachedException();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void hideAppBar() {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
    }

    public void activeHomeBackButton() {
        if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(this);
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        try {
            Field accManagerField = BaseTransientBottomBar.class.getDeclaredField("mAccessibilityManager");
            accManagerField.setAccessible(true);
            AccessibilityManager accManager = (AccessibilityManager) accManagerField.get(snackbar);
            Field isEnabledField = AccessibilityManager.class.getDeclaredField("mIsEnabled");
            isEnabledField.setAccessible(true);
            isEnabledField.setBoolean(accManager, false);
            accManagerField.set(snackbar, accManager);
        } catch (Exception e) {
            Log.d("Snackbar", "Reflection error: " + e.toString());
        }
        snackbar.show();
    }

    @Override
    public void onError(String message) {
        if (message != null) {
            showSnackBar(message);
        } else {
            showSnackBar(getString(R.string.some_error));
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    @Override
    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }

    protected void setInputError(View view, @StringRes int resString) {
        if (view instanceof TextInputLayout)
            setTextInputError((TextInputLayout) view, resString);
        else if (view instanceof TextView)
            setTextViewError((TextView) view, resString);
    }

    private void setTextViewError(TextView view, int resString) {
        view.setText(resString);
        view.requestFocus();
        view.setVisibility(View.VISIBLE);
    }

    private void setTextInputError(TextInputLayout view, int resString) {
        if (!hasInputErrorFocused) {
            hasInputErrorFocused = true;
            view.requestFocus();
        }
        view.setErrorEnabled(true);
        view.setError(getString(resString));
    }

    protected void disableInputError(View view) {
        if (view instanceof TextInputLayout)
            disableTextInputError((TextInputLayout) view);
        else if (view instanceof TextView)
            disableTextViewError(view);
    }

    private void disableTextViewError(View view) {
        view.setVisibility(View.GONE);
    }

    private void disableTextInputError(TextInputLayout view) {
        view.setError(null);
        view.setErrorEnabled(false);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        presenter = null;
        super.onDestroy();
    }


    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    protected abstract void setUp();

    @Override
    public Context getmContext() {
        return getApplicationContext();
    }

    public static class MvpPresenterNotAttachedException extends RuntimeException {
        public MvpPresenterNotAttachedException() {
            super("Please call View.onAttach(MvpView) at onCreate method");
        }
    }
}
