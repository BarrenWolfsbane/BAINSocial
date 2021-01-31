package tv.bain.bainsocial.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tv.bain.bainsocial.R;
import tv.bain.bainsocial.databinding.LoginProcessFragmentBinding;
import tv.bain.bainsocial.databinding.ServerBootingFragmentBinding;
import tv.bain.bainsocial.viewmodels.ServerBootingViewModel;

public class ServerBootingFrag extends Fragment {

    private ServerBootingViewModel mViewModel;
    private ServerBootingFragmentBinding b;

    public static ServerBootingFrag newInstance() {
        return new ServerBootingFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initiateDataBinder(container);
    }

    private View initiateDataBinder(ViewGroup container) {
        b = ServerBootingFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ServerBootingViewModel.class);
    }

}