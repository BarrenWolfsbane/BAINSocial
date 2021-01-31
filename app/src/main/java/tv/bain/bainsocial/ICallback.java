package tv.bain.bainsocial;

import android.annotation.SuppressLint;

import javax.crypto.SecretKey;

@SuppressLint("SetTextI18n")
public interface ICallback{
    public void loginSecretCallback(SecretKey secret);
    public void loginKeyDBCallback(int count);
}
