package tv.bain.bainsocial;

import android.annotation.SuppressLint;

@SuppressLint("SetTextI18n")
public interface ICallback{
    public void loginKeyDBCallback(int count);
    public void loginHashCallback(int count);
}
