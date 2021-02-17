package tv.bain.bainsocial.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.databinding.RecyclerPostsBinding;
import tv.bain.bainsocial.datatypes.Post;
import tv.bain.bainsocial.datatypes.Texture;
import tv.bain.bainsocial.datatypes.User;

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
            //Format Time
            Date date = new Date(post.getTimeCreated());
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            b.setPostTime(df.format(date));

            //Setup the Profile Image
            User poster = (User)BAINServer.getInstance().Bain_Search(post.getUid());
            if(poster != null) {
                String profileImgID = poster.getProfileImageID();
                if(profileImgID != null) {
                    String textureID = BAINServer.BAINStrip(profileImgID, BAINServer.A_QUERY);
                    Texture profimage = (Texture) BAINServer.getInstance().Bain_Search(textureID);
                    b.posterImg.setImageBitmap(Texture.base64StringToBitMap(profimage.getImageString()));
                }
            }

            //Setup the Images for the post
            List<String> postImages = post.getImages();
            if(postImages != null) {
                for (String imageString : postImages) {
                    Texture image = (Texture) BAINServer.getInstance().Bain_Search(imageString);
                    if(image != null) {
                        Bitmap postImage = Texture.base64StringToBitMap(image.getImageString());
                        ImageView imageView = new ImageView(b.imageContainer.getContext());
                        imageView.setImageBitmap(postImage);
                        b.imageContainer.addView(imageView);
                    }
                }
            }
            b.setPost(post);
            b.executePendingBindings();
        }
    }

}
