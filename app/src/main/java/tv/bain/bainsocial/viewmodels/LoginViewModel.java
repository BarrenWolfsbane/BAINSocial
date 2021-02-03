package tv.bain.bainsocial.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import tv.bain.bainsocial.utils.MyException;

public class LoginViewModel extends ViewModel {


    public static String PASSPHRASE_LOGIN_TYPE = "Passphrase";
    public static String BLOCKCHAIN_LOGIN_TYPE = "Blockchain";

    // This LiveData is bound to the Switch view (see layout), it will change its value according to the switch (realtime)
    public MutableLiveData<Boolean> loginTypeSwitch = new MutableLiveData<>(false);


    //Passphrase login fields
    private final MutableLiveData<String> loginPass = new MutableLiveData<>("test");


    //Crypto recovery fields
    MutableLiveData<String> transactionAddress = new MutableLiveData<>("");
    MutableLiveData<String> cryptoLoginPass = new MutableLiveData<>("");

    //region Setters and Setters
    public String getLoginType() {
        return loginTypeSwitch.getValue() ? BLOCKCHAIN_LOGIN_TYPE : PASSPHRASE_LOGIN_TYPE;
    }

    public String getLoginPass() {
        return loginPass.getValue();
    }

    public void setLoginPass(String string) {
        loginPass.setValue(string);
    }

    private final MutableLiveData<String> loginPassRepeat = new MutableLiveData<>("test");

    public String getLoginPassRepeat() {
        return loginPassRepeat.getValue();
    }

    public void setLoginPassRepeat(String string) {
        loginPassRepeat.setValue(string);
    }

    //endregion

    /**
     * Returns true if the login succeeded, otherwise throws an exception
     */
    public boolean login() throws MyException {
        if (!loginTypeSwitch.getValue()) {
            return loginWithPassphrase();
        } else {
            return loginWithBlockChain();
        }
    }

    public boolean loginWithBlockChain() throws MyException {
        String loginType = BLOCKCHAIN_LOGIN_TYPE;
        throw new MyException("Blockchain Backups are not Functioning at this time");
        //TODO: Make Blockchain Backups a thing
    }

    public boolean loginWithPassphrase() throws MyException {
        String pass = loginPass.getValue();
        String passRepeat = loginPassRepeat.getValue();

        if (pass.isEmpty() || passRepeat.isEmpty())
            throw new MyException("Neither the Passphrase not the check may be Empty");

        if (!pass.matches(passRepeat))
            throw new MyException("Both the Passphrase and the Check must match");

        return true;
    }
}