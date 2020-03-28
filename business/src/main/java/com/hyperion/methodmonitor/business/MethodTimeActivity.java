package com.hyperion.methodmonitor.business;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MethodTimeActivity extends Activity {

    private RecyclerView mRecyclerView;
    private MethodTimeAdapter mAdapter;
    private EditText mEditText;

    private List<String> mPackagePartNameList = new ArrayList<>();
    private int mCurrentLevel = 0;
    private long mExecTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MethodTimeManager.getInstance().setOpen(false);
        setContentView(R.layout.activity_method_time);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new MethodTimeAdapter(this);
        mAdapter.setOnOperateCallback(new MethodTimeAdapter.OnOperateCallback() {
            @Override
            public void onBack() {
                if (mCurrentLevel > 0) {
                    mCurrentLevel--;
                }
                if (mPackagePartNameList.size() > 1) {
                    mPackagePartNameList.remove(mPackagePartNameList.size() - 1);
                }
                mAdapter.setMethodTimeInfos(loadMethodTimeInfos(mExecTime));
            }

            @Override
            public void onNext(MethodTimeInfo current) {
                if (current == null) {
                    return;
                }
                mCurrentLevel++;
                mPackagePartNameList.add(current.mPackagePartName);
                mAdapter.setMethodTimeInfos(loadMethodTimeInfos(mExecTime));
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mEditText = findViewById(R.id.editText);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    long execTime = Utils.parse(mEditText.getText().toString());
                    if (execTime > 0) {
                        mExecTime = execTime;
                        reset();
                        mAdapter.setMethodTimeInfos(loadMethodTimeInfos(execTime));
                    }
                }
                return false;
            }
        });

        reset();
    }

    @Override
    protected void onDestroy() {
        MethodTimeManager.getInstance().setOpen(true);
        super.onDestroy();
    }

    private void reset() {
        mCurrentLevel = 0;
        mPackagePartNameList.clear();
        mPackagePartNameList.add("");
    }

    private List<MethodTimeInfo> loadMethodTimeInfos(long execTime) {
        String parentPackageName = null;
        if (mCurrentLevel < mPackagePartNameList.size()) {
            parentPackageName = mPackagePartNameList.get(mCurrentLevel);
        }
        return MethodTimeManager.getInstance().getMethodTimeInfo(mCurrentLevel, parentPackageName, execTime);
    }
}
