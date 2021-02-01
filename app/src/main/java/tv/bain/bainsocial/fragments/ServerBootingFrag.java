package tv.bain.bainsocial.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import tv.bain.bainsocial.R;
import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.databinding.ServerBootingFragmentBinding;
import tv.bain.bainsocial.viewmodels.ServerBootingViewModel;

public class ServerBootingFrag extends Fragment {

    private ServerBootingViewModel mViewModel;
    private ServerBootingFragmentBinding b;


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
        startService();
        goToPermissionsFrag();
    }

    private void startService() {
        Intent service = new Intent(requireActivity(), BAINServer.class); //This service will handle all important information
        if (!BAINServer.isInstanceCreated()) requireActivity().startService(service);
    }

    private void goToPermissionsFrag() {
        NavHostFragment.findNavController(this).navigate(R.id.action_serverBootingFrag_to_permissionsFrag);
    }

}