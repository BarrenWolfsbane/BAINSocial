package tv.bain.bainsocial.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.crypto.SecretKey;

import tv.bain.bainsocial.ICallback;
import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.backend.Crypt;
import tv.bain.bainsocial.utils.MyState;

public class LoginProcessViewModel extends ViewModel implements ICallback {

    private final MutableLiveData<MyState> state = new MutableLiveData<>(new MyState.LOADING());

    private final MutableLiveData<String> stepOneProgress = new MutableLiveData<>("In progress");
    private final MutableLiveData<String> stepTwoProgress = new MutableLiveData<>("In progress");
    private final MutableLiveData<String> keyDataProgress = new MutableLiveData<>("");

    private String loginPass;

    //region Getter and Setter methods
    public MutableLiveData<MyState> getState() {
        return state;
    }

    public MutableLiveData<String> getStepOneProgress() {
        return stepOneProgress;
    }

    public MutableLiveData<String> getStepTwoProgress() {
        return stepTwoProgress;
    }

    public MutableLiveData<String> getKeyDataProgress() {
        return keyDataProgress;
    }

    public void setLoginPass(String loginPass) {
        this.loginPass = loginPass;
    }
    //endregion

    public void login() {
        if (loginPass == null) {
            state.postValue(new MyState.ERROR("Login pass is null"));
            return;
        }

        String hashedPass = Crypt.md5(loginPass);
        BAINServer.getInstance().getUser().setHashedPass(hashedPass);

        stepTwoProgress.postValue("Hash:" + hashedPass);
        Crypt.generateSecret(this, hashedPass); //Sends back to loginSecretCallback(SecretKey secret)
    }

    @Override
    public void loginSecretCallback(SecretKey secret) {
//        b.stepOneLoginProgressTxt.setTextColor(Color.parseColor("#FF00FB97"));
        stepOneProgress.postValue("Complete");
        BAINServer.getInstance().getUser().setSecret(secret);

        BAINServer.getInstance().getDb().open();
        BAINServer.getInstance().getDb().getMyKeyData(this, BAINServer.getInstance().getUser(), secret); //Sends back to loginKeyDBCallback(int count)
        BAINServer.getInstance().getDb().close();
    }

    @Override
    public void loginKeyDBCallback(int count) {
        if (count > 0) {
            stepTwoProgress.postValue("Keys found");
            if (checkLoginToken()) state.postValue(new MyState.FINISHED());
            else state.postValue(new MyState.ERROR());
        } else {
            stepTwoProgress.postValue("DB Entry Not Found");
            //TODO: If Database entry not found we check for files. and create User from it if we find it
            if (!keyFileExists()) {
                if (createKeyFiles()) state.postValue(new MyState.FINISHED());
            } else if (canLoadKeyFiles()) {
                if (checkLoginToken()) {
                    state.postValue(new MyState.FINISHED());
                } else state.postValue(new MyState.ERROR("Login token check failed"));
            }
        }
    }

    private boolean keyFileExists() {
        return BAINServer.getInstance().getFc().keyChecker(BAINServer.getInstance().getUser());
    }

    private boolean createKeyFiles() {
        keyDataProgress.postValue("Writing...");
        boolean filesCreated = BAINServer.getInstance().getFc().createKeyFiles();
        keyDataProgress.postValue("Key Data has been Encrypted and Wrote");
        return filesCreated;
    }

    private boolean canLoadKeyFiles() {
        keyDataProgress.postValue("Reading...");
        boolean keyFilesLoaded = BAINServer.getInstance().getFc().loadKeyFiles(true);
        keyDataProgress.postValue("Reading BlockSize");
        return keyFilesLoaded;
    }

    public boolean checkLoginToken() { //ensures the password produces the same
        String KeyDataDirectory = "/key_data/";
        //File fileDir = new File(BAINServer.getInstance().getContext().getFilesDir(), KeyDataDirectory);
        keyDataProgress.postValue("Checking Secrets...");
        // byte[] base64Dec = fc.readFile(new File(fileDir, "LoginToken"),true);
        return true;
    }

}