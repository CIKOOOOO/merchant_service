package com.andrew.prototype.Adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.andrew.prototype.Fragment.NewThread;
import com.andrew.prototype.Model.ForumThread;
import com.andrew.prototype.Model.Report;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {
    private Context context;
    private List<ForumThread> forumList;
    private Boolean check = false;

    public void setForumList(List<ForumThread> forumList) {
        this.forumList = forumList;
        notifyDataSetChanged();
    }

    public void addForumList(List<ForumThread> forumThreads) {
        this.forumList.addAll(forumThreads);
        notifyDataSetChanged();
    }

    public void deleteList(int pos) {
        this.forumList.remove(pos);
        notifyDataSetChanged();
    }

    public interface hideAccount {
        void hide(String username, List<ForumThread> forumThreads);
    }

    public interface onItemClick {
        void onClick(int pos);
    }

    public interface onItemDelete {
        void onDelete(int pos, List<ForumThread> forumThreads);
    }

    private onItemClick onItemClick;
    private onItemDelete onItemDelete;
    private hideAccount hideAccount;

    public ThreadAdapter(Context context, List<ForumThread> forumList
            , ThreadAdapter.onItemClick onItemClick, hideAccount hideAccount, onItemDelete onItemDelete) {
        this.context = context;
        this.onItemDelete = onItemDelete;
        this.hideAccount = hideAccount;
        this.forumList = forumList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(context).inflate(R.layout.nothing_found, viewGroup, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.recycler_thread_mainforum, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return (forumList.size() == 0 ? 0 : 1);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        if (forumList.size() != 0) {
            final ForumThread forumThread = forumList.get(i);
            viewHolder.title.setText(forumThread.getTitle());
            viewHolder.time.setText(forumThread.getTime() + " WIB");
            viewHolder.date.setText(forumThread.getDate());
            viewHolder.username.setText(forumThread.getUsername());
            viewHolder.like.setText(String.valueOf(forumThread.getLike()));
            viewHolder.content.setText(forumThread.getContent());

            viewHolder.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = viewHolder.getAdapterPosition();
                    onItemClick.onClick(pos);
                }
            });

            viewHolder.rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    int pos = viewHolder.getAdapterPosition();
                    onItemClick.onClick(pos);
                }
            });

            if (forumThread.isLike()) {
                viewHolder.smile.setBackground(context.getResources().getDrawable(R.drawable.smile_press));
            } else
                viewHolder.smile.setBackground(context.getResources().getDrawable(R.drawable.smile));


//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int pos = viewHolder.getAdapterPosition();
//                    onItemClick.onClick(pos);
//                }
//            });

            viewHolder.smile.setOnClickListener(new View.OnClickListener() {
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

            viewHolder.option_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Context wrapper = new ContextThemeWrapper(context, R.style.PopupMenu);
                    PopupMenu popupMenu = new PopupMenu(wrapper, viewHolder.option_menu);
                    popupMenu.inflate(R.menu.recycler_option_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.menu_delete:
                                    onItemDelete.onDelete(viewHolder.getAdapterPosition(), forumList);
//                                    forumList.remove(viewHolder.getAdapterPosition());
//                                    notifyDataSetChanged();
                                    Toast.makeText(context, context.getResources().getString(R.string.thread_deleted), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.menu_dont_show:
                                    hideAccount.hide(forumThread.getUsername(), forumList);
//                                    hideAccount(forumThread.getUsername());
                                    Toast.makeText(context, context.getResources().getString(R.string.thread_not_appear), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.menu_edit:
                                    NewThread newThread = new NewThread();

                                    AppCompatActivity activity = (AppCompatActivity) context;

                                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(NewThread.EDIT_THREAD, forumThread);

                                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                                    fragmentTransaction.replace(R.id.main_frame, newThread);

                                    newThread.setArguments(bundle);
                                    fragmentTransaction.commit();
                                    break;
                                case R.id.menu_hide:
                                    onItemDelete.onDelete(viewHolder.getAdapterPosition(), forumList);
                                    Toast.makeText(context, context.getResources().getString(R.string.thread_hidden), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.menu_report:
                                    final List<Report> reportList = new ArrayList<>();
                                    reportList.addAll(Constant.getReport());

                                    AlertDialog.Builder codeBuilder = new AlertDialog.Builder(context);
                                    final View codeView = LayoutInflater.from(context).inflate(R.layout.custom_report, null);
                                    TextView name = codeView.findViewById(R.id.report_name);
                                    TextView thread = codeView.findViewById(R.id.report_title);
                                    final TextView error = codeView.findViewById(R.id.show_error_content_report);
                                    final EditText content = codeView.findViewById(R.id.etOther_Report);
                                    Button send = codeView.findViewById(R.id.btnSubmit_Report);
                                    Button cancel = codeView.findViewById(R.id.btnCancel_Report);
                                    RecyclerView recyclerView = codeView.findViewById(R.id.recycler_checkbox_report);
                                    final CheckBox checkBox = codeView.findViewById(R.id.check_other);
                                    final ReportAdapter reportAdapter = new ReportAdapter(reportList, codeView.getContext());

                                    recyclerView.setLayoutManager(new GridLayoutManager(codeView.getContext(), 2));

                                    codeBuilder.setView(codeView);
                                    final AlertDialog codeAlert = codeBuilder.create();

                                    name.setText(forumThread.getUsername());
                                    thread.setText(forumThread.getTitle());

                                    recyclerView.setAdapter(reportAdapter);

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
        }
    }


    @Override
    public int getItemCount() {
        return forumList.size() == 0 ? 1 : forumList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time, username, content, like;
        Button reply;
        ImageButton option_menu, smile;
        RippleView rippleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.main_forum_thread_title);
            date = itemView.findViewById(R.id.main_forum_thread_date);
            time = itemView.findViewById(R.id.main_forum_thread_clock);
            username = itemView.findViewById(R.id.main_forum_thread_username);
            content = itemView.findViewById(R.id.main_forum_thread_content);
            like = itemView.findViewById(R.id.main_forum_thread_amount_like);
            reply = itemView.findViewById(R.id.main_forum_thread_reply);
            option_menu = itemView.findViewById(R.id.main_forum_thread_more);
            smile = itemView.findViewById(R.id.main_forum_thread_smile);
            rippleView = itemView.findViewById(R.id.ripple_main_forum);
        }
    }

    private boolean isChecked(List<Report> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isReport_checked()) return true;
        }
        return false;
    }

    private void hideAccount(String username) {
        for (int i = 0; i < forumList.size(); i++) {
            if (forumList.get(i).getUsername().toLowerCase().trim().equals(username.toLowerCase().trim())) {
                forumList.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    public void addThread(List<ForumThread> forumThreads) {
        forumList.addAll(forumThreads);
        notifyDataSetChanged();
    }
}
