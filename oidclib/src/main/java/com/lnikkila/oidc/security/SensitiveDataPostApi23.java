package com.lnikkila.oidc.security;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Base64;
import android.util.Log;

import com.lnikkila.oidc.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * For post {@link Build.VERSION_CODES#M } data encryption.  <br/>
 * Uses <a href='http://developer.android.com/training/articles/keystore.html'>AndroidKeyStore</a> <br/>
 * Created by Camilo Montes on 14/10/2015.
 * @see <a href="https://github.com/Zlate87/android-fingerprint-example/blob/master/app/src/main/java/com/example/zlatko/fingerprintexample/MainActivity.java">https://github.com/Zlate87/android-fingerprint-example/blob/master/app/src/main/java/com/example/zlatko/fingerprintexample/MainActivity.java</a>
 */
@TargetApi(Build.VERSION_CODES.M)
public class SensitiveDataPostApi23 extends SensitiveDataUtils {

    //region Constants

    protected static final String KEYSTORE_TYPE             = "AndroidKeyStore";

    protected static final String CIPHER_BLOCKS             = KeyProperties.BLOCK_MODE_CBC;
    protected static final String CIPHER_PADDING            = KeyProperties.ENCRYPTION_PADDING_PKCS7;

    protected static final String IV_STORAGE_FILE_NAME      = "ivStorage";
    protected static final String IV_PARAM_KEY              = "ivEncryption";

    protected static final String DEFAULT_KEY_ALIAS         = "OIDCEncKey";
    protected static final boolean DEFAULT_REQUIRED_PIN     = false;
    protected static final int DEFAULT_KEYPIN_DURATION      = 5*60;

    //endregion

    private KeyStore keyStore;
    private Cipher encryptCipher;

    public SensitiveDataPostApi23(Context context) {
        super(context);
        try {
            encryptCipher = Cipher.getInstance(String.format("%1$s/%2$s/%3$s", CIPHER_ALGO, CIPHER_BLOCKS, CIPHER_PADDING));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getKeyAlias() {
        String keyAlias = DEFAULT_KEY_ALIAS;
        if (context.get() != null) {
            keyAlias = context.get().getString(R.string.oidc_encryptKeyAlias);
            keyAlias = keyAlias.isEmpty() ? DEFAULT_KEY_ALIAS : keyAlias;
        }
        return keyAlias;
    }

    public boolean isKeyPinRequired() {
        boolean encryptKeyPinRequired = DEFAULT_REQUIRED_PIN;
        if (context.get() != null) {
            encryptKeyPinRequired = context.get().getResources().getBoolean(R.bool.oidc_encryptKeyAskPin);
        }
        return encryptKeyPinRequired;
    }

    private int getKeyPinDuration() {
        int encryptKeyPinDuration = DEFAULT_KEYPIN_DURATION;
        if (context.get() != null) {
            encryptKeyPinDuration = context.get().getResources().getInteger(R.integer.oidc_encryptKeyPinDuration);
            encryptKeyPinDuration = encryptKeyPinDuration <= 0 ? DEFAULT_KEYPIN_DURATION : encryptKeyPinDuration;
        }
        return encryptKeyPinDuration;
    }

    //region SensitiveDataUtils implementation

    protected void createAndSaveSecretKey() {
        try {
            keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(null);

            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry)keyStore.getEntry(getKeyAlias(), null);
            if (entry == null) {
                generateKey();
            }
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            Log.e(TAG, "Couldn't get a reference to the AndroidKeyStore", e);
        } catch (UnrecoverableEntryException e) {
            Log.e(TAG, "Couldn't extract key from the AndroidKeyStore", e);
        }
    }

    protected SecretKey generateKey() {
        SecretKey key = null;
        try {
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                    getKeyAlias(),
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);

            KeyGenParameterSpec keySpec = builder
                    .setKeySize(CIPHER_KEY_LENGHT)
                    .setBlockModes(CIPHER_BLOCKS)
                    .setEncryptionPaddings(CIPHER_PADDING)
                    .setRandomizedEncryptionRequired(false) //FIXME: set to true because we should be using IND-CPA but this means that a IV has to be store per token (less generic than i though)
                    .setUserAuthenticationRequired(isKeyPinRequired())
                    .setUserAuthenticationValidityDurationSeconds(getKeyPinDuration())
                    .build();

            KeyGenerator  kg = KeyGenerator.getInstance(CIPHER_ALGO, KEYSTORE_TYPE);
            kg.init(keySpec);
            key = kg.generateKey();
        } catch (InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException e) {
            Log.e(TAG, "Couldn't generate secret key", e);
        }
        return key;
    }

    protected void initCipher(boolean forEncryption) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry)keyStore.getEntry(getKeyAlias(), null);
        SecretKey key = entry.getSecretKey();
        SharedPreferences sharedPreferences = context.get().getSharedPreferences(IV_STORAGE_FILE_NAME, Activity.MODE_PRIVATE);
        String base64EncryptionIv = sharedPreferences.getString(IV_PARAM_KEY, null);

        if(forEncryption) {
            if (base64EncryptionIv == null) {
                encryptCipher.init(Cipher.ENCRYPT_MODE, key);

                byte[] encryptionIV = encryptCipher.getIV();
                base64EncryptionIv = Base64.encodeToString(encryptionIV, Base64.DEFAULT);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(IV_PARAM_KEY, base64EncryptionIv);
                editor.apply();
            } else {
                byte[] encryptionIv = Base64.decode(base64EncryptionIv, Base64.DEFAULT);
                encryptCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(encryptionIv)); //FIXME: this ain't good, and we are re-using the same IV (security guidelines say we shouldn't)
            }
        } else {
            byte[] encryptionIv = Base64.decode(base64EncryptionIv, Base64.DEFAULT);

            encryptCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(encryptionIv));
        }
    }

    protected byte[] encrypt(byte[] data) throws UserNotAuthenticatedWrapperException {
        byte[] encrypted;
        try {
            initCipher(true);
            encrypted = encryptCipher.doFinal(data);
        } catch (UserNotAuthenticatedException e) {
            throw new UserNotAuthenticatedWrapperException(e);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                InvalidKeyException | BadPaddingException | UnrecoverableEntryException | KeyStoreException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return encrypted;
    }

    protected byte[] decrypt(byte[] data) throws UserNotAuthenticatedWrapperException {
        byte[] decrypted;
        try {
            initCipher(false);
            decrypted = encryptCipher.doFinal(data);
        } catch (UserNotAuthenticatedException e) {
            throw new UserNotAuthenticatedWrapperException(e);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | InvalidKeyException |
                BadPaddingException | UnrecoverableEntryException | KeyStoreException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return decrypted;
    }

    //endregion
}
