package com.example.graduationproject.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.graduationproject.models.Parent;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.ui.commonFragment.ProfileFragment;
import com.example.graduationproject.ui.parentUi.ParentActivity;
import com.example.graduationproject.ui.parentUi.ParentFragment;
import com.example.graduationproject.ui.teacherUi.TeacherActivity;
import com.example.graduationproject.ui.teacherUi.TeacherFragment;

public class BroadcastHandler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Intent action ---->"+intent.getAction(),"Intent action ---->"+intent.getAction());
            if(context instanceof TeacherActivity && intent.getAction().equalsIgnoreCase("SHOW_TEACHER_INFORMATION_WINDOW")){
                ((TeacherActivity) context).showTeacherInformationPopupWindow();
            }
            else if(context instanceof TeacherActivity && intent.getAction().equalsIgnoreCase("UPDATE_NOTIFICATIONS_RECYCLER_VIEW")){
                ((TeacherActivity) context).updateNotifications();
            }

            else if(context instanceof ParentActivity && "SHOW_PARENT_INFORMATION_WINDOW".equals(intent.getAction())){
                ((ParentActivity) context).showParentInformationPopupWindow();
            }

            else if(context instanceof TeacherActivity && intent.getAction().equalsIgnoreCase("UPDATE_TEACHER_UI")){
                ((TeacherActivity)context).updateNotificationsList(intent.getParcelableArrayListExtra("notificationsData"));
            }
            else if(context instanceof TeacherActivity && "START_MATCHING_FRAGMENT_FOR_TEACHER".equals(intent.getAction())){
                TeacherMatchModel teacherModel =(TeacherMatchModel)intent.getSerializableExtra("teacherMatchModel");
                if(teacherModel != null)
                    ((TeacherActivity)context).startCardViewData(teacherModel);
            }
    }
}
