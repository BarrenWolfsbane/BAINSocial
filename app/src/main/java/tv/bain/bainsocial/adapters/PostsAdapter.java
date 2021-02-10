package tv.bain.bainsocial.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tv.bain.bainsocial.databinding.RecyclerPostsBinding;
import tv.bain.bainsocial.datatypes.Post;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.Holder> {

    private final List<Post> list;

    public PostsAdapter(List<Post> list) {
        this.list = list;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bindData(list.get(position));
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerPostsBinding binding = RecyclerPostsBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new Holder(binding);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        RecyclerPostsBinding b;

        public Holder(RecyclerPostsBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }

        private void bindData(Post post) {
            Date date = new Date(post.getTimeCreated());
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            b.setPostTime(df.format(date));
            b.setPost(post);
            b.executePendingBindings();
        }
    }

}
