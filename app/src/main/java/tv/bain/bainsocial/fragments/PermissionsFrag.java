package tv.bain.bainsocial.fragments;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import tv.bain.bainsocial.databinding.PermissionsFragmentBinding;
import tv.bain.bainsocial.viewmodels.PermissionsViewModel;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PermissionsFrag extends Fragment {

    private PermissionsViewModel vm;
    private PermissionsFragmentBinding b = null;


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

        if (hasPermissions()) {
            Toast.makeText(requireActivity(), "Proceeded successfully", Toast.LENGTH_SHORT).show();
            //TODO: go to Login
        } else askForPermissions();

    }

    @Override
    public void onDestroyView() {
        b = null;
        super.onDestroyView();
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
            askForPermissions();
        });
        b.agreeAndContinueBtn.setOnClickListener(v -> {
            if (hasPermissions()) {
                Toast.makeText(requireActivity(), "Proceeded successfully", Toast.LENGTH_SHORT).show();
                //TODO: go to login
            } else
                Toast.makeText(requireActivity(), "Please provide all required permissions to proceed", Toast.LENGTH_SHORT).show();
        });

    }

}