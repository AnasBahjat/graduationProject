package com.example.graduationproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.graduationproject.databinding.CustomTeacherMatchingLayoutForParentBinding;
import com.example.graduationproject.listeners.ParentPostRequestClickListener;
import com.example.graduationproject.models.TeacherMatchModel;

import java.util.List;

public class ParentPostedRequestsAdapter extends RecyclerView.Adapter<ParentPostedRequestsAdapter.MyViewHolder> {
    List<TeacherMatchModel> postedRequestsDataList ;
    Context context;
    CustomTeacherMatchingLayoutForParentBinding binding;
    ParentPostRequestClickListener listener;

    public ParentPostedRequestsAdapter(List<TeacherMatchModel> postedRequestsDataList, Context context, ParentPostRequestClickListener listener){
        this.postedRequestsDataList = postedRequestsDataList;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CustomTeacherMatchingLayoutForParentBinding binding = CustomTeacherMatchingLayoutForParentBinding.inflate(inflater,parent,false);
        return new MyViewHolder(binding,context,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TeacherMatchModel requestModel= postedRequestsDataList.get(position);
        holder.bind(requestModel);
    }

    @Override
    public int getItemCount() {
        return postedRequestsDataList.size();
    }

    public void filteredList(List<TeacherMatchModel> filteredPostedRequestList){
        this.postedRequestsDataList=filteredPostedRequestList;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CustomTeacherMatchingLayoutForParentBinding binding;
        Context context;

        ParentPostRequestClickListener listener ;
        public MyViewHolder(CustomTeacherMatchingLayoutForParentBinding binding,Context context,ParentPostRequestClickListener listener){
            super(binding.getRoot());
            this.binding=binding;
            this.context=context;
            this.listener = listener;
        }
        public void bind(TeacherMatchModel requestModel){

            binding.childNameTextView.setText(requestModel.getCustomChildData().getChildName());
            binding.coursesTextView.setText(requestModel.getCourses());
            binding.locationTextView.setText(requestModel.getLocation());
            binding.teachingMethodTextView.setText(requestModel.getTeachingMethod());
            binding.timeTextView.setText(requestModel.getStartTime()+" - "+requestModel.getEndTime());
            binding.priceTextView.setText(requestModel.getPriceMinimum()+"$"+" - "+requestModel.getPriceMaximum()+"$");

           /* binding.childNameTextView.setText(requestModel.getChildren().getChildName());
            binding.coursesTextView.setText(requestModel.getCourses());
            binding.locationTextView.setText(requestModel.getLocation());
            binding.teachingMethodTextView.setText(requestModel.getTeachingMethod());
            binding.timeTextView.setText(requestModel.getStartTime()+" - "+requestModel.getEndTime());*/

            binding.matchTeacherCardView.setOnClickListener(x->{
                listener.onClick(requestModel);
            });
        }
    }
}
