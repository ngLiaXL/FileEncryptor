package com.ngliaxl.encrypt.manage;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ngliaxl.encrypt.R;
import com.ngliaxl.encrypt.event.UpdateEvent;
import com.ngliaxl.encrypt.manage.filter.CompositeFilter;
import com.ngliaxl.encrypt.manage.widget.EmptyRecyclerView;
import com.ngliaxl.encrypt.util.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;


public class DirectoryFragment extends Fragment {
    public interface FileClickListener {
        void onFileClicked(File clickedFile);
    }

    private static final String ARG_FILE_PATH = "arg_file_path";
    private static final String ARG_FILTER = "arg_filter";

    private View mEmptyView;
    private String mPath;

    private CompositeFilter mFilter;

    private EmptyRecyclerView mDirectoryRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;
    private FileClickListener mFileClickListener;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateListEvent(UpdateEvent event) {
        mDirectoryAdapter.setListData(
                FileUtils.getFileListByDirPath(mPath, mFilter));
    }




    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFileClickListener = (FileClickListener) activity;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFileClickListener = null;
        EventBus.getDefault().unregister(this);
    }

    public static DirectoryFragment getInstance(
            String path, CompositeFilter filter) {
        DirectoryFragment instance = new DirectoryFragment();

        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, path);
        args.putSerializable(ARG_FILTER, filter);
        instance.setArguments(args);

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);
        mDirectoryRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.directory_recycler_view);
        mEmptyView = view.findViewById(R.id.directory_empty_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArgs();
        initFilesList();
    }

    private void initFilesList() {
        mDirectoryAdapter = new DirectoryAdapter(getActivity(),
                FileUtils.getFileListByDirPath(mPath, mFilter));

        mDirectoryAdapter.setOnItemClickListener(new DirectoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mFileClickListener != null) {
                    mFileClickListener.onFileClicked(mDirectoryAdapter.getModel(position));
                }
            }
        });

        mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDirectoryRecyclerView.setAdapter(mDirectoryAdapter);
        mDirectoryRecyclerView.setEmptyView(mEmptyView);
    }

    @SuppressWarnings("unchecked")
    private void initArgs() {
        if (getArguments().getString(ARG_FILE_PATH) != null) {
            mPath = getArguments().getString(ARG_FILE_PATH);
        }

        mFilter = (CompositeFilter) getArguments().getSerializable(ARG_FILTER);
    }
}
