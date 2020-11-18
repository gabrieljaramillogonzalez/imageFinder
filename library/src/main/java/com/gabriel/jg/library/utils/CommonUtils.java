package com.gabriel.jg.library.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import com.gabriel.jg.library.R;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CommonUtils {

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static <T> String toJson(T object) {
        return object == null ? null : new GsonBuilder().create().toJson(object, object.getClass());
    }

    public static <T> T toObject(String json, Class clazz) {
        return json == null ? null : (T) new GsonBuilder().create().fromJson(json, clazz);
    }

    public static ArrayList<File> sortListFiles(ArrayList<File> files, boolean isImage) {
        ComparatorNameFile comparatorNameFile = new ComparatorNameFile();
        ArrayList<File> arrayList = new ArrayList<>(), arrayList1 = new ArrayList<>();
        for (File f : files) {
            if (isImage && (f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".jpeg"))) {
                arrayList1.add(f);
            } else if (!isImage && f.getName().endsWith(".pdf")) {
                arrayList1.add(f);
            } else {
                arrayList.add(f);
            }
        }
        Collections.sort(arrayList, comparatorNameFile);
        Collections.sort(arrayList1, comparatorNameFile);
        arrayList.addAll(arrayList1);
        return arrayList;
    }

    public static boolean isImageFile(File file, Context context) {
        Uri uri = Uri.fromFile(file);
        ContentResolver cR = context.getContentResolver();
        String mime = cR.getType(uri);
        if (mime != null && mime.contains("image"))
            return true;
        else
            return file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpeg");
    }

    public static class ComparatorNameFile implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }
}
