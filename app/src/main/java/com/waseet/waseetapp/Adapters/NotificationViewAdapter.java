package com.waseet.waseetapp.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.MarkNotification;
import com.waseet.waseetapp.Models.Notification;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.NetworkStatus;
import com.waseet.waseetapp.ViewHolders.NotificationViewHolder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class NotificationViewAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

    Context mContext;
    private List<Notification> notifications;
    private AppPreferencesShared appPreferencesShared;
    private PopupWindow mPopupWindow;

    public NotificationViewAdapter(Context mContext, List<Notification> notifications) {
        this.mContext = mContext;
        this.notifications = notifications;
        appPreferencesShared = new AppPreferencesShared(mContext);

    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_view_card, parent, false);
        NotificationViewHolder notificationViewHolder = new NotificationViewHolder(view);
        return notificationViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        ConfigurationData.notificationId = notifications.get(position).getId();
        holder.content.setText(notifications.get(position).getContent());
        holder.views.setText(notifications.get(position).getUserView());

        holder.notice_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.notification_card_popup, null);
                mPopupWindow = new PopupWindow(customView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                TextView noticeContents = (TextView) customView.findViewById(R.id.noticeContents);
                noticeContents.setText(notifications.get(position).getContent());

                Button markViewBtn = (Button) customView.findViewById(R.id.markViewBtn);
                markViewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetworkStatus.isNetworkAvailable(mContext)) {
                            postMarkNotificationView();
                        } else {
                            Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mPopupWindow.showAtLocation(holder.notice_Card, Gravity.CENTER, 0, 0);
            }
        });
    }

    private void postMarkNotificationView() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<MarkNotification> responseCall = apiInterface.postMarkNotificationView(appPreferencesShared.getCSrfToken(),
                appPreferencesShared.getLoginUserId(), ConfigurationData.notificationId);
        responseCall.enqueue(new Callback<MarkNotification>() {
            @Override
            public void onResponse(Call<MarkNotification> call, Response<MarkNotification> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            mPopupWindow.dismiss();
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, "Something went wrong !", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<MarkNotification> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
