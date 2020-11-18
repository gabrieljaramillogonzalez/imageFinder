package com.gabriel.jg.library.ui.picker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gabriel.jg.library.R;
import com.gabriel.jg.library.utils.CommonUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ImagePickerAlbumsAdapter extends RecyclerView.Adapter<ImagePickerAlbumsAdapter.ImagePickerAlbumViewHolder> {

    private ArrayList<File> list = new ArrayList<>();
    private ImagePickerAlbumsListener listener;

    public ImagePickerAlbumsAdapter(ImagePickerAlbumsListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImagePickerAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImagePickerAlbumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_gallery_image_albums, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePickerAlbumViewHolder holder, int position) {
        holder.bindView(list.get(position));
    }

    public void setList(ArrayList<File> files) {
        list.clear();
        if (files == null || files.size() == 0) {
            return;
        }
        list.addAll(CommonUtils.sortListFiles(files, true));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void detach() {
        list = null;
        listener = null;
    }

    public interface ImagePickerAlbumsListener {
        boolean getListItem(String path);

        void setListItem(String path);

        void removeListItem(String path);

        boolean isSingleImage();

        void setNewListFile(File rootDir);

        void addFileAlbums(File file);
    }

    public class ImagePickerAlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        private final ImageView ivImage;
        private final TextView tvNameImage;
        private final TextView tvSizeImage;
        private final RelativeLayout rlImage;
        private final CheckBox cbImage;
        private final CardView mcvImage;
        private boolean isCheck = false;

        public ImagePickerAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ItemGalleryImageAlbums_ivImage);
            rlImage = itemView.findViewById(R.id.ItemGalleryImageAlbums_rlImage);
            tvNameImage = itemView.findViewById(R.id.ItemGalleryImageAlbums_tvNameImage);
            tvSizeImage = itemView.findViewById(R.id.ItemGalleryImageAlbums_tvSizeImage);
            cbImage = itemView.findViewById(R.id.ItemGalleryImageAlbums_cbImage);
            mcvImage = itemView.findViewById(R.id.ItemGalleryImageAlbums_mcvImage);
            cbImage.setOnCheckedChangeListener(this);
            ivImage.setOnClickListener(this);
            rlImage.setOnClickListener(this);
        }

        public void bindView(File file) {
            tvNameImage.setText(file.getName());
            tvSizeImage.setVisibility(file.isDirectory() ? View.GONE : View.VISIBLE);
            if (file.isDirectory()) {
                Glide.with(ivImage.getContext())
                        .load(R.drawable.ic_folder_white)
                        .into(ivImage);
                cbImage.setVisibility(View.GONE);
            } else if (CommonUtils.isImageFile(file, rlImage.getContext())) {
                Glide.with(ivImage.getContext())
                        .load(file)
                        .into(ivImage);
                tvSizeImage.setText(sizeFile(file.length()));
                if (!listener.isSingleImage()) {
                    cbImage.setVisibility(View.VISIBLE);
                    isCheck = listener.getListItem(list.get(getAdapterPosition()).getAbsolutePath());
                    cbImage.setChecked(listener.getListItem(list.get(getAdapterPosition()).getAbsolutePath()));
                }
            }
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.ItemGalleryImageAlbums_rlImage) {
                if (list.get(getAdapterPosition()).isDirectory()) {
                    listener.setNewListFile(list.get(getAdapterPosition()));
                } else if (list.get(getAdapterPosition()).isFile()) {
                    if (listener.isSingleImage())
                        listener.addFileAlbums(list.get(getAdapterPosition()));
                    else
                        cbImage.setChecked(!cbImage.isChecked());
                }
            }
        }

        private String sizeFile(long size) {
            float sizeFile = 0;
            String textSize = "";
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            if (size <= 1024L * 1024L) {
                sizeFile = (float) size / 1024L;
                textSize = decimalFormat.format(sizeFile) + " KB";
            } else {
                sizeFile = (float) size / (1024L * 1024L);
                textSize = decimalFormat.format(sizeFile) + " MB";
            }
            return textSize;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (compoundButton.getId() == R.id.ItemGalleryImageAlbums_cbImage) {
                if (b) {
                    if (!isCheck)
                        listener.setListItem(list.get(getAdapterPosition()).getAbsolutePath());
                } else
                    listener.removeListItem(list.get(getAdapterPosition()).getAbsolutePath());
            }
        }
    }
}
