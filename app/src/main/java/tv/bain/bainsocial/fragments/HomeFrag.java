package tv.bain.bainsocial.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;

import it.beppi.tristatetogglebutton_library.TriStateToggleButton;
import tv.bain.bainsocial.R;
import tv.bain.bainsocial.adapters.PostsAdapter;
import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.databinding.HomeFragmentBinding;
import tv.bain.bainsocial.databinding.NavHeaderBinding;
import tv.bain.bainsocial.viewmodels.HomeViewModel;

public class HomeFrag extends Fragment {
    private static final int RESULT_OK = 0;

    //TODO: Implement a callback to reload posts if a new one has been added (here or in the ViewModel)

    private final int SELECT_PHOTO = 1;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /* Useful for setting the Toolbar's menu items */
        setHasOptionsMenu(true);

        vm = new ViewModelProvider(this).get(HomeViewModel.class);
        bindData();

        initiateToolbar();
        initiateDrawer();
        setToolbarIcon();
        initiateTrisStateSwitch();
        setNavHeader();
    }

    private void bindData() {
        initiateAdapter();
        b.setLifecycleOwner(getViewLifecycleOwner());
        b.setFrag(this);
        b.recycler.setAdapter(adapter);
    }

    private void initiateToolbar() {
        //TODO: create a resized drawable
        ((AppCompatActivity) requireActivity()).setSupportActionBar(b.toolbar);
        ActionBar bar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (bar == null) return;

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        // added this to the onCreate to make it work: setHasOptionsMenu(true);
        inflater.inflate(R.menu.posts_toolbar_menu, menu);

        initiateTheSearchView(menu);
    }

    private void initiateTheSearchView(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem == null) return;

        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        if (searchView == null) return;

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setQueryHint("Search for posts");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    Toast.makeText(requireActivity(), "Search not implemented", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }


    private void initiateDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(), b.drawerLayout, b.toolbar, R.string.openDrawer, R.string.closeDrawer);

        b.drawerLayout.addDrawerListener(toggle);
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
    }

    private void setToolbarIcon() {
        /* Toolbar icon must be set after initializing the Drawer */
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.f810049d4e3320ba053d1dca055d4764676451fc, null);
        if (drawable == null) return;

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable finalDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));

        b.toolbar.setNavigationIcon(finalDrawable);
    }

    private void initiateTrisStateSwitch() {
        Menu m = b.navView.getMenu();
        SubMenu sm = m.findItem(R.id.subItemsItem).getSubMenu();

        final TriStateToggleButton tripleSwitch = (TriStateToggleButton) sm.findItem(R.id.online_Mode_Seek).getActionView();

        MenuItem connectionModeTxt = sm.findItem(R.id.online_Mode_Seek);
        connectionModeTxt.setTitle("Data Mode: Offline");

        tripleSwitch.setOnToggleChanged((toggleStatus, booleanToggleStatus, toggleIntValue) -> {
            switch (toggleStatus) {
                case off:
                    connectionModeTxt.setTitle("Data Mode: Offline");
                    break;
                case mid:
                    connectionModeTxt.setTitle("Data Mode: Local");
                    break;
                case on:
                    connectionModeTxt.setTitle("Data Mode: Online");
                    break;
            }
        });
    }

    public void setNavHeader() {
        //TODO: let users decide what background and profile image they want
        //TODO: Careful with memory leaks caused by the binding
        NavHeaderBinding binding = NavHeaderBinding.inflate(getLayoutInflater());
        binding.headerIDText.setText("ID: " + BAINServer.getInstance().getUser().getuID());


        View headerLayout = b.navView.getHeaderView(0); //Gets the Header
        TextView tV = headerLayout.findViewById(R.id.header_ID_Text);
        tV.setText("ID: "+ BAINServer.getInstance().getUser().getuID());
        ImageView iV = headerLayout.findViewById(R.id.hvProfileImage);

        iV.setOnLongClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            Intent chooser = new Intent(Intent.ACTION_CHOOSER);
            chooser.putExtra(Intent.EXTRA_INTENT, photoPickerIntent);
            chooser.putExtra(Intent.EXTRA_TITLE, "Profile Image Selection");

            Intent[] intentArray =  {cameraIntent};
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooser, SELECT_PHOTO);
            return false;
        });
    }




    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == SELECT_PHOTO) {
                File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Test" + File.separator);
                if (!root.exists()) root.mkdirs();
                String fname = "img_" + System.currentTimeMillis() + ".jpg";
                File sdImageMainDirectory = new File(root, fname);
                Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);
                Log.println(Log.ASSERT,"HOMEFRAG:", "fName: "+fname);

                View headerLayout = b.navView.getHeaderView(0); //Gets the Header
                TextView tV = headerLayout.findViewById(R.id.header_ID_Text);
                tV.setText("Image: "+ fname);

                BAINServer.getInstance().SendToast(fname);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Toast.makeText(requireActivity(), "Searching", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
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