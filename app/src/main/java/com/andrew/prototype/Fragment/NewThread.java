package com.andrew.prototype.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.prototype.Activity.MainActivity;
import com.andrew.prototype.Adapter.ImagePickerAdapter;
import com.andrew.prototype.Model.ForumThread;
import com.andrew.prototype.Model.ImagePicker;
import com.andrew.prototype.Model.SyncImg;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewThread extends Fragment implements View.OnClickListener
        , ImagePickerAdapter.onItemClick, View.OnTouchListener, MainActivity.onBackPressFragment {

    public static final String EDIT_THREAD = "EDIT_THREAD";
    public static final String EDIT_THREAD_SELECTED = "EDIT_THREAD_SELECTED";
    public static final String EDIT_THREAD_REPLY = "EDIT_THREAD_REPLY";
    public static final String EDIT_THREAD_REPLY_BACK = "EDIT_THREAD_REPLY_BACK";
    public static final String EDIT_THREAD_REPLY_BACK_LIST = "EDIT_THREAD_REPLY_BACK_LIST";

    public static String THREAD_CONDITION;

    private static ForumThread forumThread, thread;

    private View v;
    private RecyclerView recyclerView;
    private EditText title, content;
    private List<ImagePicker> imageList;
    private ImagePickerAdapter imagePickerAdapter;
    private TextView error_content, error_title;
    private AlertDialog codeAlert;
    private Context mContext;
    private Activity mActivity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_new_thread, container, false);
        initVar();
        return v;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initVar() {
        THREAD_CONDITION = "";
        mContext = v.getContext();

        TextView tvTitle = v.findViewById(R.id.title_new_thread);
        TextView title_only = v.findViewById(R.id.tvTitle_NewThread);
        Button submit = v.findViewById(R.id.btnSubmit_NewThread);
        ImageButton photo = v.findViewById(R.id.photo_taker_new_thread);
        ImageButton file = v.findViewById(R.id.file_taker_new_thread);
        ImageButton camera = v.findViewById(R.id.camera_taker_new_thread);

        recyclerView = v.findViewById(R.id.recyclerView_NewThread);
        title = v.findViewById(R.id.etTitle_NewThread);
        content = v.findViewById(R.id.etContent_NewThread);
        error_content = v.findViewById(R.id.show_error_content_new_thread);
        error_title = v.findViewById(R.id.show_error_title_new_thread);

        imageList = new ArrayList<>();

        setRecyclerView();

        submit.setOnClickListener(this);
        photo.setOnClickListener(this);
        file.setOnClickListener(this);
        camera.setOnClickListener(this);
        content.setOnTouchListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getParcelable(EDIT_THREAD) != null) {
                forumThread = bundle.getParcelable(EDIT_THREAD);
                if (forumThread != null)
                    content.setText(forumThread.getContent());
                imageList.addAll(Constant.getImage());
            } else if (bundle.getParcelable(EDIT_THREAD_SELECTED) != null) {
                forumThread = bundle.getParcelable(EDIT_THREAD_SELECTED);
                THREAD_CONDITION = EDIT_THREAD_SELECTED;
                if (forumThread != null)
                    content.setText(forumThread.getContent());
                imageList.addAll(Constant.getImage());
            } else if (bundle.getParcelable(EDIT_THREAD_REPLY) != null) {
                title.setVisibility(View.GONE);
                title_only.setVisibility(View.GONE);
                thread = bundle.getParcelable(EDIT_THREAD_REPLY);
                forumThread = bundle.getParcelable(EDIT_THREAD_REPLY_BACK);
                THREAD_CONDITION = EDIT_THREAD_REPLY;
                content.setText(thread.getContent());
                if (bundle.getParcelableArrayList(EDIT_THREAD_REPLY_BACK_LIST) != null) {
                    List<SyncImg> syncImgs = bundle.getParcelableArrayList(EDIT_THREAD_REPLY_BACK_LIST);
                    if (syncImgs != null)
                        for (int i = 0; i < syncImgs.size(); i++) {
                            SyncImg syncImg = syncImgs.get(i);
                            imageList.add(new ImagePicker(syncImg.getImg(), "IMG", syncImg.getFileName()));
                        }
                }
            }
            tvTitle.setText(mContext.getResources().getString(R.string.edit_thread));
            title.setText(forumThread.getTitle());
            imagePickerAdapter.setImageList(imageList);
        }
    }

    private void setRecyclerView() {
        imagePickerAdapter = new ImagePickerAdapter(mContext, imageList, this, "");
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(imagePickerAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void setPreview() {
        AlertDialog.Builder codeBuilder = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams") View codeView = getLayoutInflater().inflate(R.layout.custom_preview_thread, null);

        TextView title = codeView.findViewById(R.id.title_custom_preview);
        TextView content = codeView.findViewById(R.id.content_preview);
        TextView time = codeView.findViewById(R.id.time_preview);
        ScrollView scrollView = codeView.findViewById(R.id.sc_preview);
        RoundedImageView imageView = codeView.findViewById(R.id.merchantPic_Preview);
        TextView user = codeView.findViewById(R.id.merchantName);
        TextView loc = codeView.findViewById(R.id.merchantLoc_Preview);
        RecyclerView recyclerView_preview = codeView.findViewById(R.id.recycler_preview);
        ImageButton imageButton = codeView.findViewById(R.id.close_preview_thread);
        Button cancel = codeView.findViewById(R.id.btnCancel_Preview);
        Button save = codeView.findViewById(R.id.btnSubmit_Preview);

        scrollView.smoothScrollTo(0, 0);

        if (!THREAD_CONDITION.equals(EDIT_THREAD) && !THREAD_CONDITION.isEmpty()) {
            if (THREAD_CONDITION.equals(EDIT_THREAD_SELECTED)) {
                forumThread.setTitle(this.title.getText().toString());
                forumThread.setContent(this.content.getText().toString());
                user.setText(forumThread.getUsername());
                loc.setText(forumThread.getLocation());
            } else {
                title.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                DecodeBitmap.setScaledImageView(imageView, thread.getProfile_picture(), mContext);
//                Glide.with(mContext)
//                        .load(thread.getProfile_picture())
//                        .placeholder(mContext.getDrawable(R.drawable.placeholder))
//                        .into(imageView);
                user.setText(thread.getUsername());
                loc.setText(thread.getLocation());
            }
        } else if (THREAD_CONDITION.equals(EDIT_THREAD)) {
            forumThread.setContent(this.content.getText().toString());
            user.setText(forumThread.getUsername());
            loc.setText(forumThread.getLocation());
        }

        title.setText(this.title.getText().toString());
        content.setText(this.content.getText().toString());
        time.setText(getDate() + " WIB");

        ImagePickerAdapter imagePickerAdapter = new ImagePickerAdapter(mContext, imageList, this, ImagePickerAdapter.STATES_NO_BUTTON);
        recyclerView_preview.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView_preview.setAdapter(imagePickerAdapter);

        imageButton.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        codeBuilder.setView(codeView);
        codeAlert = codeBuilder.create();
        codeAlert.show();
    }

    private String getDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm");
        return sdf.format(new Date());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit_NewThread:
                String titles = title.getText().toString();
                String contents = content.getText().toString();

                title.setBackground(getResources().getDrawable(R.drawable.background_edit_text));
                content.setBackground(getResources().getDrawable(R.drawable.background_edit_text));
                error_content.setVisibility(View.GONE);
                error_title.setVisibility(View.GONE);

                if (titles.isEmpty()) {
                    error_title.setVisibility(View.VISIBLE);
                    title.setBackground(getResources().getDrawable(R.drawable.background_edit_text_error));
                } else if (contents.isEmpty()) {
                    error_content.setVisibility(View.VISIBLE);
                    content.setBackground(getResources().getDrawable(R.drawable.background_edit_text_error));
                } else {
                    setPreview();
                }

                break;
            case R.id.photo_taker_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_GALLERY_EXTERNAL);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                }
                break;

            case R.id.file_taker_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.PERMISSION_READ_FILE_EXTERNAL);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_FILE);
                }
                break;

            case R.id.camera_taker_new_thread:
                if (ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                        || ActivityCompat.checkSelfPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_CAMERA_TAKER);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Constant.ACTIVITY_TAKE_IMAGE);
                }
                break;
            case R.id.close_preview_thread:
                codeAlert.dismiss();
                break;
            case R.id.btnCancel_Preview:
                codeAlert.dismiss();
                break;
            case R.id.btnSubmit_Preview:
                codeAlert.dismiss();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (THREAD_CONDITION.isEmpty())
                    fragmentTransaction.replace(R.id.main_frame, new MainForum());
                else
                    onBackPress(false, mContext);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.PERMISSION_CAMERA_TAKER:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Constant.ACTIVITY_TAKE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.PERMISSION_READ_FILE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_FILE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.PERMISSION_READ_GALLERY_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, Constant.ACTIVITY_CHOOSE_IMAGE);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.permission_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constant.ACTIVITY_CHOOSE_IMAGE:
                    Bitmap bitmap;
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        File f = new File("" + targetUri);
                        try {
                            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(targetUri));
                            imageList.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri uri = item.getUri();
                            File f = new File("" + uri);
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                                imageList.add(new ImagePicker(DecodeBitmap.compressBitmap(bitmap), "IMG", f.getName()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    imagePickerAdapter.setImageList(imageList);
                    break;
                case Constant.ACTIVITY_CHOOSE_FILE:
                    if (data.getData() != null) {
                        Uri targetUri = data.getData();
                        String filePath = getFileName(targetUri);
                        imageList.add(new ImagePicker(null, ImagePickerAdapter.STATES_PDF, filePath));
                    } else if (data.getClipData() != null) {
                        ClipData clipData = data.getClipData();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            Uri targetUri = item.getUri();
                            String filePath = getFileName(targetUri);
                            imageList.add(new ImagePicker(null, ImagePickerAdapter.STATES_PDF, filePath));
                        }
                    }
                    imagePickerAdapter.setImageList(imageList);
                    break;
                case Constant.ACTIVITY_TAKE_IMAGE:
                    Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    Uri tempUri = getImageUri(mContext, photo);
                    File finalFile = new File(getRealPathFromURI(tempUri));
                    imageList.add(new ImagePicker(photo, "IMG", finalFile.getName()));
                    imagePickerAdapter.setImageList(imageList);
                    break;
            }
            recyclerView.smoothScrollToPosition(imageList.size());
        }
    }

    private String getFileName(Uri uri) throws IllegalArgumentException {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }
        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (mContext.getContentResolver() != null) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void onItemClicked(int pos, String states) {
        imageList.remove(pos);
        imagePickerAdapter.setImageList(imageList);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.etContent_NewThread) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return false;
    }

    @Override
    public void onBackPress(boolean check, Context context) {
        SelectedThread selectedThread = new SelectedThread();

        AppCompatActivity activity = (AppCompatActivity) context;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(SelectedThread.GET_THREAD_OBJECT, forumThread);

        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.main_frame, selectedThread);

        selectedThread.setArguments(bundle);
        fragmentTransaction.commit();
    }
}
