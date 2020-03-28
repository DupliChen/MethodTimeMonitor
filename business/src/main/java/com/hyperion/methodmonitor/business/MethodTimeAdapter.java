package com.hyperion.methodmonitor.business;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MethodTimeAdapter extends RecyclerView.Adapter {

    private static final int TYPE_PACKAGE = 1;
    private static final int TYPE_CLASS = 2;

    private Context mContext;
    private List<MethodTimeInfo> mMethodTimeInfos = new ArrayList<>();
    private OnOperateCallback mOnOperateCallback;

    public MethodTimeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setMethodTimeInfos(List<MethodTimeInfo> mMethodTimeInfos) {
        if (mMethodTimeInfos == null) {
            mMethodTimeInfos = new ArrayList<>();
        }
        this.mMethodTimeInfos.clear();
        this.mMethodTimeInfos.addAll(mMethodTimeInfos);
        notifyDataSetChanged();
    }

    public void setOnOperateCallback(OnOperateCallback mOnOperateCallback) {
        this.mOnOperateCallback = mOnOperateCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PACKAGE) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_package, parent, false);
            return new PackageHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_class, parent, false);
            return new ClassHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MethodTimeInfo methodTimeInfo = mMethodTimeInfos.get(position);
        if (holder instanceof PackageHolder) {
            PackageHolder packageHolder = (PackageHolder) holder;
            packageHolder.mNameView.setText(methodTimeInfo.mPackagePartName);
            packageHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.equals(MethodTimeManager.RETURN_TEXT, methodTimeInfo.mPackagePartName)) {
                        if (mOnOperateCallback != null) {
                            mOnOperateCallback.onBack();
                        }
                    } else {
                        if (mOnOperateCallback != null) {
                            mOnOperateCallback.onNext(methodTimeInfo);
                        }
                    }
                }
            });
        } else if (holder instanceof ClassHolder) {
            ClassHolder classHolder = (ClassHolder) holder;
            classHolder.mNameView.setText(methodTimeInfo.mClassName);
            classHolder.mMethodView.setText(methodTimeInfo.mMethodName + methodTimeInfo.mMethodDesc);
            classHolder.mStartView.setText(String.format(mContext.getString(R.string.method_time_start_time), Utils.format(methodTimeInfo.mStartTime)));
            classHolder.mDurationView.setText(String.format(mContext.getString(R.string.method_time_duration_time), String.valueOf(methodTimeInfo.mStopTime - methodTimeInfo.mStartTime)));
        }
    }

    @Override
    public int getItemCount() {
        return mMethodTimeInfos.size();
    }

    @Override
    public int getItemViewType(int position) {
        MethodTimeInfo methodTimeInfo = mMethodTimeInfos.get(position);
        if (!TextUtils.isEmpty(methodTimeInfo.mPackagePartName)) {
            return TYPE_PACKAGE;
        } else {
            return TYPE_CLASS;
        }
    }

    static class PackageHolder extends RecyclerView.ViewHolder {

        TextView mNameView;

        PackageHolder(View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.tv_name);
        }
    }

    public class ClassHolder extends RecyclerView.ViewHolder {

        TextView mNameView;
        TextView mMethodView;
        TextView mStartView;
        TextView mDurationView;

        ClassHolder(View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.tv_class);
            mMethodView = itemView.findViewById(R.id.tv_method);
            mStartView = itemView.findViewById(R.id.tv_start_time);
            mDurationView = itemView.findViewById(R.id.tv_duration);
        }
    }

    public interface OnOperateCallback {

        void onBack();

        void onNext(MethodTimeInfo current);

    }
}
