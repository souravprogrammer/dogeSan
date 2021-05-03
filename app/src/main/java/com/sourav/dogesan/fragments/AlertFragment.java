package com.sourav.dogesan.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.sourav.dogesan.R;

public class AlertFragment extends AppCompatDialogFragment {

    String title, description  ;
    public AlertFragment(){
        title = "Alert";
        description = "Server under maintenance ,some function may not work properly.";
    }
    public AlertFragment(String title, String Description){
        this.title =title;
        this.description = Description ;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.alert_dilog_layout,null);
        TextView title = view.findViewById(R.id.alertTitle);
        TextView description =  view.findViewById(R.id.alertDescription);
        title.setText(this.title);
        description.setText(this.description);
        builder.setView(view);

        return builder.create();
    }
}
