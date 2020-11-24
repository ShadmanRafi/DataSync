package com.sr.datasync;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StatusDialogFragment extends DialogFragment {

    private TextView titleTV, dataTV;
    private Button okBtn;

    public StatusDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String title = getArguments().getString("title");
        String data = getArguments().getString("data");
        View v = inflater.inflate(R.layout.fragment_status_dialog, container, false);
        titleTV = v.findViewById(R.id.dialog_title);
        dataTV = v.findViewById(R.id.dialog_body);
        okBtn = v.findViewById(R.id.dialog_btn);

        titleTV.setText(title);
        dataTV.setText(data);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return v;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
    }
}