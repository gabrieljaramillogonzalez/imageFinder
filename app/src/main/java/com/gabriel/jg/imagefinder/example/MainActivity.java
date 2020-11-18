package com.gabriel.jg.imagefinder.example;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gabriel.jg.imagefinder.R;
import com.gabriel.jg.library.core.Constants;
import com.gabriel.jg.library.ui.picker.ImagePickerAlbumsActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.Button);
        button.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, ImagePickerAlbumsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.isSingleImage, true);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.RC_GALLERY);
    }
}