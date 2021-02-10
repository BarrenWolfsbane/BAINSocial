package tv.bain.bainsocial.viewmodels;

import androidx.lifecycle.ViewModel;

import java.util.List;

import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.datatypes.Post;

public class HomeViewModel extends ViewModel {
    public List<Post> getAllLocalPosts() {
        BAINServer.getInstance().getDb().open();
        List<Post> list = BAINServer.getInstance().getDb().get_Recent_Posts_Local();
        BAINServer.getInstance().getDb().close();
        return list;
    }
}