package com.lnikkila.oidc.security;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

import javax.crypto.SecretKey;

/**
 * Created by Camilo Montes on 18/01/2016. <br/>
 * @see <a href="http://lukieb.blogspot.fr/2013/11/aes-encryptiondecryption-in-android.html">http://lukieb.blogspot.fr/2013/11/aes-encryptiondecryption-in-android.html</a>
 */
public abstract class SensitiveDataUtils {

    //region Constants

    protected static final String TAG = "SensitiveDataStorage";

    protected static final String CIPHER_ALGO = "AES";
    protected static final int CIPHER_KEY_LENGHT    = 256;

    protected static final String IO_ENCODING = "UTF-8";

    //endregion

    protected final WeakReference<Context> context;

    public SensitiveDataUtils(Context context) {
        this.context = new WeakReference<>(context);
        createAndSaveSecretKey();
    }

    /**
     * Creates and saves a new secret key
     */
    protected abstract void createAndSaveSecretKey();

    /**
     * Generate a key suitable for {@link #CIPHER_ALGO} encryption and {@link #CIPHER_KEY_LENGHT} key length.
     * <br/>
     * Usually {@link #CIPHER_ALGO} = "AES" and {@link #CIPHER_KEY_LENGHT} = 256
     * @return The generated key
     */
    protected abstract SecretKey generateKey();

    /**
     * Encrypt the given plaintext bytes using the given key
     * @param data The plaintext to encrypt
     * @return The encrypted bytes
     */
    protected abstract byte[] encrypt(byte[] data) throws UserNotAuthenticatedWrapperException;

    /**
     * Encrypt the given plaintext using the priviously created key.
     * @param data The plaintext to encrypt
     * @return The encrypted string
     */
    public String encrypt(@NonNull String data) throws UserNotAuthenticatedWrapperException {
        String encryptedString = null;
        if (!TextUtils.isEmpty(data)) {
            try {
                byte[] encrypted = encrypt(data.getBytes(IO_ENCODING));
                encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, String.format("The given data coud not be decoded using %1$s encoding", IO_ENCODING), e);
            }
        } else {
            Log.e(TAG, "Can not encrypt empty data");
        }
        return encryptedString;
    }

    /**
     * Decrypt the given data with the given key
     * @param data The data to decrypt
     * @return The decrypted bytes
     */
    protected abstract byte[] decrypt(byte[] data) throws UserNotAuthenticatedWrapperException;

    /**
     * Decrypt the given data using the priviously created key.
     * @param encryptedData The data to decrypt
     * @return The decrypted string
     */
    public String decrypt(@NonNull String encryptedData) throws UserNotAuthenticatedWrapperException {
        String data = null;
        if (!TextUtils.isEmpty(encryptedData)) {
            try {
                byte[] decrypted = decrypt(Base64.decode(encryptedData, Base64.DEFAULT));
                data = new String(decrypted, IO_ENCODING);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, String.format("The given data coud not be decoded using %1$s encoding", IO_ENCODING), e);
            }
        } else {
            Log.e(TAG, "Can not decrypt empty data");
        }
        return data;
    }
}
