package com.andrew.prototype.Adapter;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew.prototype.Fragment.SelectedThread;
import com.andrew.prototype.Model.ForumThread;
import com.andrew.prototype.Model.Report;
import com.andrew.prototype.Model.SyncImg;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.DecodeBitmap;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.Holder> implements
        ImageSyncAdapter.onImageSyncClicked {
    private static final long DELAY_ANIMATION_START = 600;
    private List<ForumThread> list;
    private List<SyncImg> syncImgs;
    private Context context;
    private Boolean check = false, newReplyIsAvailable;
    private ImageButton btnClose, btnDownload;
    private TextView merchantName;
    private ImageView imgFrame;

    private onAccountDelete onAccountDelete;
    private onReplyDelete onReplyDelete;
    private onEdit onEdit;
    private onReplyClick onReplyClick;

    public void setImageFrame(ImageButton btnClose, ImageButton btnDownload, TextView merchantName, ImageView imgFrame) {
        this.btnClose = btnClose;
        this.btnDownload = btnDownload;
        this.merchantName = merchantName;
        this.imgFrame = imgFrame;
    }

    public void setSyncImgs(List<SyncImg> syncImgs, List<ForumThread> list) {
        this.syncImgs = syncImgs;
        this.list = list;
        newReplyIsAvailable = true;
        notifyDataSetChanged();
    }

    public void setList(List<ForumThread> list) {
        this.list = list;
    }

    public void setSyncImgs(List<SyncImg> syncImgs) {
        this.syncImgs = syncImgs;
    }

    public interface onReplyClick {
        void onReply(int pos);
    }

    public interface onEdit {
        void onThreadEdit(int pos, List<SyncImg> bitmap);
    }

    public interface onReplyDelete {
        void onDelete(int pos);
    }

    public interface onAccountDelete {
        void accountDelete(String username);
    }

    public ReplyAdapter(List<ForumThread> list, Context context, onReplyClick onReplyClick
            , onEdit onEdit, onReplyDelete onReplyDelete, onAccountDelete onAccountDelete) {
        this.list = list;
        this.onReplyDelete = onReplyDelete;
        this.onEdit = onEdit;
        this.onReplyClick = onReplyClick;
        this.context = context;
        this.onAccountDelete = onAccountDelete;
        syncImgs = new ArrayList<>();
        newReplyIsAvailable = false;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_reply, viewGroup, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int i) {
        final ImageSyncAdapter imageSyncAdapter = new ImageSyncAdapter(context, this);
        final ForumThread forumThread = list.get(i);
        holder.merchant_name.setText(forumThread.getUsername());
        holder.loc.setText(forumThread.getLocation());
        holder.time.setText(forumThread.getDate() + " - " + forumThread.getTime() + " WIB");
        holder.like.setText(forumThread.getLike() + "");
        holder.content.setText(forumThread.getContent());

        if (forumThread.isLike()) {
            holder.smile.setBackground(context.getResources().getDrawable(R.drawable.smile_press));
        } else
            holder.smile.setBackground(context.getResources().getDrawable(R.drawable.smile));

        DecodeBitmap.setScaledRoundedImageView(holder.roundedImageView, forumThread.getProfile_picture(), context);

        for (SyncImg syncImg : syncImgs) {
            if (forumThread.getUsername().toLowerCase().trim().equals(syncImg.getName().trim().toLowerCase())) {
                imageSyncAdapter.addSyncImgs(syncImg);
            }
        }

        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        holder.recyclerView.setAdapter(imageSyncAdapter);

        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReplyClick.onReply(list.size());
            }
        });

        holder.option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context wrap = new ContextThemeWrapper(context, R.style.PopupMenu);
                PopupMenu popupMenu = new PopupMenu(wrap, holder.option_menu);
                popupMenu.inflate(R.menu.recycler_option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_delete:
                                int position = SelectedThread.PAGE_NUMBER_STATE * 5 + holder.getAdapterPosition();
                                onReplyDelete.onDelete(position);
                                deleteAnimation(holder.itemView);
                                Toast.makeText(context, context.getResources().getString(R.string.thread_deleted), Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.menu_dont_show:
                                onAccountDelete.accountDelete(forumThread.getUsername());
                                Toast.makeText(context, context.getResources().getString(R.string.thread_not_appear), Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.menu_edit:
                                onEdit.onThreadEdit(SelectedThread.PAGE_NUMBER_STATE * 5 + holder.getAdapterPosition()
                                        , getSyncImgs(forumThread.getUsername()));
                                break;
                            case R.id.menu_hide:
                                int position_hide = SelectedThread.PAGE_NUMBER_STATE * 5 + holder.getAdapterPosition();
                                onReplyDelete.onDelete(position_hide);
                                deleteAnimation(holder.itemView);
                                Toast.makeText(context, context.getResources().getString(R.string.thread_hidden), Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.menu_report:
                                final List<Report> reportList = new ArrayList<>();
                                reportList.addAll(Constant.getReport());

                                AlertDialog.Builder codeBuilder = new AlertDialog.Builder(context);
                                final View codeView = LayoutInflater.from(context).inflate(R.layout.custom_report, null);

                                final TextView error = codeView.findViewById(R.id.show_error_content_report);
                                final EditText content = codeView.findViewById(R.id.etOther_Report);
                                final CheckBox checkBox = codeView.findViewById(R.id.check_other);
                                final ReportAdapter reportAdapter = new ReportAdapter(reportList, codeView.getContext());

                                TextView name = codeView.findViewById(R.id.report_name);
                                TextView thread = codeView.findViewById(R.id.report_title);
                                TextView threadTitle = codeView.findViewById(R.id.report_tv_title);
                                Button send = codeView.findViewById(R.id.btnSubmit_Report);
                                Button cancel = codeView.findViewById(R.id.btnCancel_Report);
                                RecyclerView recyclerView = codeView.findViewById(R.id.recycler_checkbox_report);

                                recyclerView.setLayoutManager(new GridLayoutManager(codeView.getContext(), 2));

                                codeBuilder.setView(codeView);
                                final AlertDialog codeAlert = codeBuilder.create();

                                name.setText(forumThread.getUsername());
                                thread.setText(forumThread.getTitle());

                                recyclerView.setAdapter(reportAdapter);

                                thread.setVisibility(View.GONE);
                                threadTitle.setVisibility(View.GONE);
                                content.setEnabled(false);

                                checkBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (!check) {
                                            content.setBackground(codeView.getContext().getDrawable(R.drawable.background_stroke_white));
                                            check = true;
                                            content.setEnabled(true);
                                        } else {
                                            content.setBackground(codeView.getContext().getDrawable(R.drawable.background_grey));
                                            check = false;
                                            content.setEnabled(false);
                                        }
                                    }
                                });

                                send.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        error.setVisibility(View.GONE);
                                        if (check) {
                                            if (content.getText().toString().isEmpty())
                                                error.setVisibility(View.VISIBLE);
                                            else {
                                                Toast.makeText(codeView.getContext(), codeView.getContext().getResources().getString(R.string.report_sent)
                                                        , Toast.LENGTH_SHORT).show();
                                                codeAlert.dismiss();
                                            }
                                        } else if (isChecked(reportList)) {
                                            Toast.makeText(codeView.getContext(), codeView.getContext().getResources().getString(R.string.report_sent)
                                                    , Toast.LENGTH_SHORT).show();
                                            codeAlert.dismiss();
                                        } else {
                                            error.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        codeAlert.dismiss();
                                    }
                                });

                                codeAlert.show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        holder.smile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (forumThread.isLike()) {
                    forumThread.setLikeAmount(forumThread.getLike() - 1);
                    forumThread.setLike(false);
                } else {
                    forumThread.setLike(true);
                    forumThread.setLikeAmount(forumThread.getLike() + 1);
                }
                notifyDataSetChanged();
            }
        });

        setAnimation(holder.itemView, i, holder.linearLayout);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView merchant_name, loc, time, like, content;
        ImageButton option_menu, smile;
        Button reply;
        RoundedImageView roundedImageView;
        RecyclerView recyclerView;
        LinearLayout linearLayout;

        Holder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.ll_recyclerview_reply);
            recyclerView = itemView.findViewById(R.id.recycler_img_inside_reply);
            merchant_name = itemView.findViewById(R.id.merchantName_Recycler);
            loc = itemView.findViewById(R.id.merchantLoc_Recycler);
            time = itemView.findViewById(R.id.time_recycler);
            like = itemView.findViewById(R.id.smile_amount_recycler);
            option_menu = itemView.findViewById(R.id.thread_more_recycler);
            reply = itemView.findViewById(R.id.btn_reply_recycler);
            smile = itemView.findViewById(R.id.img_smile_recycler);
            content = itemView.findViewById(R.id.content_recycler);
            roundedImageView = itemView.findViewById(R.id.merchantPic_Recycler);
        }
    }

    private void hideAccount(String username) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUsername().toLowerCase().trim().equals(username.toLowerCase().trim())) {
                int position = SelectedThread.PAGE_NUMBER_STATE * 5 + i;
                onReplyDelete.onDelete(position);
            }
        }
    }

    private boolean isChecked(List<Report> list) {
        for (Report reportIsChecked : list) {
            if (reportIsChecked.isReport_checked()) return true;
        }
        return false;
    }

    private List<SyncImg> getSyncImgs(String name) {
        List<SyncImg> syncImgs = new ArrayList<>();
        for (SyncImg syncImg : this.syncImgs) {
            if (name.toLowerCase().trim().equals(syncImg.getName().trim().toLowerCase())) {
                syncImgs.add(syncImg);
            }
        }
        return syncImgs;
    }

    private void downloadImage(SyncImg syncImg) {
        Toast.makeText(context, context.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
        MediaStore.Images.Media.insertImage(context.getContentResolver(), syncImg.getImg()
                , syncImg.getName(), "");
    }

    private void deleteAnimation(View v) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        v.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 400);
    }

    private void setAnimation(View v, int pos, LinearLayout linearLayout) {
        int lastPosition = list.size() - 1;
        if (pos >= lastPosition && newReplyIsAvailable) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up_recyclerview);
            final ValueAnimator animator = ObjectAnimator
                    .ofInt(linearLayout
                            , "backgroundColor"
                            , context.getResources().getColor(R.color.blue_palette)
                            , context.getResources().getColor(R.color.soft_blue_palette));
            animator.setDuration(300);
            animator.setEvaluator(new ArgbEvaluator());
            animator.setRepeatCount(9);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            v.startAnimation(animation);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animator.start();
                }
            }, DELAY_ANIMATION_START);
            newReplyIsAvailable = false;
        }
    }

    @Override
    public void imageIsClicked(final SyncImg syncImg) {
        SelectedThread.frameLayout.setVisibility(View.VISIBLE);
        SelectedThread.frameIsVisible = true;

        SelectedThread.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectedThread.frameIsVisible = false;
                SelectedThread.frameLayout.setVisibility(View.GONE);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectedThread.frameIsVisible = false;
                SelectedThread.frameLayout.setVisibility(View.GONE);
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ((Activity) context).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constant.PERMISSION_WRITE_EXTERNAL);
                    }
                } else {
                    downloadImage(syncImg);
                }
            }
        });

        Glide.with(context)
                .load(DecodeBitmap.compressBitmap(syncImg.getImg()))
                .placeholder(DecodeBitmap.setScaleDrawable(context, R.drawable.placeholder))
                .into(imgFrame);

        merchantName.setText("Merchant Name : " + syncImg.getName());
    }
}
