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
import tv.bain.bainsocial.databinding.ServerChoiceFragmentBinding;
import tv.bain.bainsocial.viewmodels.ServerChoiceViewModel;

public class ServerChoiceFrag extends Fragment {

    private ServerChoiceViewModel mViewModel;
    private ServerChoiceFragmentBinding b;

    public static ServerChoiceFrag newInstance() {
        return new ServerChoiceFrag();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initiateDataBinding(container);
    }

    private View initiateDataBinding(ViewGroup container) {
        b = ServerChoiceFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ServerChoiceViewModel.class);
    }

}