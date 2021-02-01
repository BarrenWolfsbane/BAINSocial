package tv.bain.bainsocial.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import tv.bain.bainsocial.R;
import tv.bain.bainsocial.databinding.PermissionsFragmentBinding;
import tv.bain.bainsocial.viewmodels.PermissionsViewModel;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PermissionsFrag extends Fragment {

    private PermissionsViewModel vm;
    private PermissionsFragmentBinding b = null;

    //TODO: Permissions managing should be handled in a separate class
    //TODO: Disable/Enable buttons according to permissions


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initiateDataBinder(container);
    }


    private View initiateDataBinder(ViewGroup container) {
        b = PermissionsFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(PermissionsViewModel.class);
        setOnClickListeners();

        if (hasPermissions()) goToLogin();
        else askForPermissions();

    }

    @Override
    public void onDestroyView() {
        b = null;
        super.onDestroyView();
    }

    private void goToLogin() {
        NavHostFragment.findNavController(this).navigate(R.id.action_permissionsFrag_to_loginFrag);
    }

    private boolean hasPermissions() {
        int result = ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE);
        b.extStoragePermBtn.setEnabled(result != PackageManager.PERMISSION_GRANTED);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void askForPermissions() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                1);
    }

    private void setOnClickListeners() {
        b.extStoragePermBtn.setOnClickListener(v -> {
            if (hasPermissions())
                Toast.makeText(requireActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
            else askForPermissions();
        });
        b.agreeAndContinueBtn.setOnClickListener(v -> {
            if (hasPermissions()) goToLogin();
            else {
                Toast.makeText(requireActivity(), "Please provide all required permissions to proceed", Toast.LENGTH_SHORT).show();
            }
        });

    }

}