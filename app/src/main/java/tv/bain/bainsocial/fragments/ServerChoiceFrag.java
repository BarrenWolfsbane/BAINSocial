package tv.bain.bainsocial.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import tv.bain.bainsocial.backend.Crypt;
import tv.bain.bainsocial.backend.DBManager;
import tv.bain.bainsocial.databinding.ServerChoiceFragmentBinding;
import tv.bain.bainsocial.datatypes.User;
import tv.bain.bainsocial.viewmodels.ServerChoiceViewModel;

public class ServerChoiceFrag extends Fragment {

    private ServerChoiceViewModel mViewModel;
    private ServerChoiceFragmentBinding b;
    private Context context;
    private Crypt crypt;
    String CurrentLayout = "";

    private User me;

    public User getMe() {
        return me;
    }

    private DBManager db;

    public DBManager getDb() {
        return db;
    }

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
        context = requireActivity().getApplicationContext();
    }

}