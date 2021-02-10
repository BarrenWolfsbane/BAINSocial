package tv.bain.bainsocial.fragments;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import it.beppi.tristatetogglebutton_library.TriStateToggleButton;
import tv.bain.bainsocial.R;
import tv.bain.bainsocial.adapters.PostsAdapter;
import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.databinding.HomeFragmentBinding;
import tv.bain.bainsocial.viewmodels.HomeViewModel;

public class HomeFrag extends Fragment {

    //TODO: Implement a callback to reload posts if a new one has been added (here or in the ViewModel)

    private HomeViewModel vm;
    private HomeFragmentBinding b = null;
    private PostsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initiateDataBinder(container);
    }

    private View initiateDataBinder(ViewGroup container) {
        b = HomeFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }

    ActionBarDrawerToggle toggle;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(HomeViewModel.class);
        bindData();
        initiateDrawer();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /* Manages the shape of the Drawer's Hamburger button when the configuration changes*/
        toggle.onConfigurationChanged(newConfig);
    }

    private void bindData() {
        initiateAdapter();
        b.setLifecycleOwner(getViewLifecycleOwner());
        b.setFrag(this);
        b.recycler.setAdapter(adapter);
    }

    private void initiateDrawer() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(b.toolbar);
        toggle = new ActionBarDrawerToggle(requireActivity(), b.drawerLayout, b.toolbar, R.string.openDrawer, R.string.closeDrawer);

        b.drawerLayout.addDrawerListener(toggle);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        toggle.syncState();

        b.navView.setNavigationItemSelectedListener(item ->
                {
                    if (item.getItemId() == R.id.homeFragItem) {
                        b.drawerLayout.close();
                        return true;
                    }
                    if (item.getItemId() == R.id.postCreateFragItem) {
                        goToNewPostFrag();
                        return true;
                    }
                    /*
                    if(item.getItemId() == R.id.online_Mode_Seek) {
                        b.navView.getMenu().getItem(8).getSubMenu().getItem(1).setTitle("true");
                    }
                    */
                    return false;
                }
        );
        Menu m = b.navView.getMenu();
        SubMenu sm = m.getItem(8).getSubMenu();
        final TriStateToggleButton tstb_1 = (TriStateToggleButton) sm.getItem(1).getActionView();
        tstb_1.toggleOff();

        MenuItem OnlineTog = b.navView.getMenu().getItem(8).getSubMenu().getItem(1);
        OnlineTog.setTitle("Data Mode: Offline");
        tstb_1.setOnToggleChanged(new TriStateToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(TriStateToggleButton.ToggleStatus toggleStatus, boolean booleanToggleStatus, int toggleIntValue) {
                switch (toggleStatus) {
                    case off: OnlineTog.setTitle("Data Mode: Offline"); break;
                    case mid: OnlineTog.setTitle("Data Mode: Local"); break;
                    case on: OnlineTog.setTitle("Data Mode: Online"); break;
                }
            }
        });

        setNavHeader();
    }

    public void setNavHeader(){
        int textCL = Color.BLACK;
        View headerLayout = b.navView.getHeaderView(0); //Gets the Header
        //headerLayout.setBackgroundColor(Color.GRAY); //let users define this
        //headerLayout.setBackground();//set Background to the User Defined Image

        TextView tV = headerLayout.findViewById(R.id.header_ID_Text);
        tV.setText("ID: "+ BAINServer.getInstance().getUser().getuID());
        tV.setTextColor(textCL);

        tV = headerLayout.findViewById(R.id.nvHeaderDisplayNameLabel);
        tV.setTextColor(textCL);
        tV = headerLayout.findViewById(R.id.nvHeaderDisplayNameText);
        tV.setTextColor(textCL);


        ImageView profPhoto = headerLayout.findViewById(R.id.hvProfileImage);
        //profPhoto.setImageBitmap();//we can use the Image Class to set this

        Drawable drawable= getResources().getDrawable(R.drawable.f810049d4e3320ba053d1dca055d4764676451fc);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

        b.toolbar.setNavigationIcon(newdrawable);
    }


    @Override
    public void onStart() {
        super.onStart();
        /* Close the Drawer when the screen rotates or when the user gets back to this screen from another fragment*/
        b.drawerLayout.close();
    }

    @Override
    public void onDestroyView() {
        b = null;
        super.onDestroyView();
    }

    private void initiateAdapter() {
        adapter = new PostsAdapter(vm.getAllLocalPosts());
    }


    public void goToNewPostFrag() {
        NavHostFragment.findNavController(this).navigate(R.id.action_homeFrag_to_postCreateFrag);
    }

}