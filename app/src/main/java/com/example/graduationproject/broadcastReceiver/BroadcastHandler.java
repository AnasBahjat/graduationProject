package com.example.graduationproject.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.graduationproject.models.Parent;
import com.example.graduationproject.ui.commonFragment.ProfileFragment;
import com.example.graduationproject.ui.parentUi.ParentActivity;
import com.example.graduationproject.ui.parentUi.ParentFragment;
import com.example.graduationproject.ui.teacherUi.TeacherActivity;

public class BroadcastHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            if(context instanceof TeacherActivity && intent.getAction().equalsIgnoreCase("SHOW_TEACHER_INFORMATION_WINDOW")){
                ((TeacherActivity) context).showTeacherInformationPopupWindow();
            }
            else if(context instanceof TeacherActivity && intent.getAction().equalsIgnoreCase("UPDATE_NOTIFICATIONS_RECYCLER_VIEW")){
                ((TeacherActivity) context).updateNotifications();
            }

            else if(context instanceof ParentActivity && intent.getAction().equalsIgnoreCase("SHOW_PARENT_INFORMATION_WINDOW")){
                ((ParentActivity) context).showParentInformationPopupWindow();
            }

            else if(context instanceof TeacherActivity && intent.getAction().equalsIgnoreCase("UPDATE_TEACHER_UI")){
                ((TeacherActivity)context).updateNotificationsList(intent.getParcelableArrayListExtra("notificationsData"));
            }
    }
}
