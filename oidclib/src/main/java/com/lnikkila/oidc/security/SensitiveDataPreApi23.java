package com.lnikkila.oidc.security;

import android.content.Context;
import android.util.Log;

import com.lnikkila.oidc.R;

import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * For pre {@link android.os.Build.VERSION_CODES#M } data encryption.  <br/>
 * Uses <a href='https://rtyley.github.io/spongycastle/'>Spongy Castle</a> <br/>
 * Created by Camilo Montes on 18/01/2016. <br/>
 */
public class SensitiveDataPreApi23 extends SensitiveDataUtils {

    //region Constants

    private static final String DEFAULT_KEYSTORE_PATH   = "oidc_enc_key";

    //endregion

    public SensitiveDataPreApi23(Context context) {
        super(context);
    }

    private String getKeyStorePath() {
        String keyAlias = DEFAULT_KEYSTORE_PATH;
        if (context.get() != null) {
            keyAlias = context.get().getString(R.string.oidc_encryptKeyAlias);
            keyAlias = keyAlias.isEmpty() ? DEFAULT_KEYSTORE_PATH : keyAlias;
        }
        return keyAlias;
    }

    /**
     * Write the given data to private storage
     * @param data The data to store
     * @param filename The filename to store the data in
     */
    private void saveKey(byte[] data, String filename) {
        try {
            Context mContext = context.get();
            if (mContext != null) {
                FileOutputStream fOut = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
                fOut.write(data);
                fOut.flush();
                fOut.close();
            } else {
                Log.e(TAG, "Can't save the encryption key, application context is null");
            }
        } catch (IOException e) {
            Log.e(TAG, "Can't save the encryption key", e);
        }
    }

    /**
     * Read data from private storage using the given filename
     * @param filename The filename whose contents to read
     * @return The contents of the file or null
     * @throws IOException
     */
    private byte[] loadKey(String filename) throws IOException {
        byte[] key2 = null;
        try
        {
            Context mContext = context.get();
            if (mContext != null) {
                byte[] key = new byte[5096];
                Arrays.fill(key, (byte) 0);
                FileInputStream fOut = mContext.openFileInput(filename);
                int length = fOut.read(key);
                key2 = new byte[length];
                System.arraycopy(key, 0, key2, 0, length);
                fOut.close();
            } else {
                Log.e(TAG, "Can't load the encryption key, application context is null");
            }
        } catch(FileNotFoundException e) {
            Log.e(TAG, "Can't load the encryption key", e);
        }
        return key2;
    }

    /**
     * Saves the given secret key to private storage
     * @param key the key to store
     * @see #saveKey(byte[], String)
     */
    private void saveKey(SecretKey key) {
        saveKey(key.getEncoded(), getKeyStorePath());
    }

    /**
     * Reads the secret key from private storage
     * @return the secret key
     * @see #loadKey(String)
     */
    private SecretKey loadKey() {
        SecretKey secretKey = null;
        try {
            byte[] keyBytes = loadKey(getKeyStorePath()); // Hard-coded filename representing the encryption key
            secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, CIPHER_ALGO);
        }
        catch (IOException | IllegalArgumentException e) {
            Log.e(TAG, String.format("Can't read key from storage at %1$s", getKeyStorePath()), e);
        }
        return secretKey;
    }

    private byte[] cipherData(PaddedBufferedBlockCipher cipher, byte[] data) throws InvalidCipherTextException {
        int minSize = cipher.getOutputSize(data.length);
        byte[] outBuf = new byte[minSize];
        int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
        int length2 = cipher.doFinal(outBuf, length1);
        int actualLength = length1 + length2;
        byte[] result = new byte[actualLength];
        System.arraycopy(outBuf, 0, result, 0, result.length);
        return result;
    }

    // region SensitiveDataUtils implementation

    protected void createAndSaveSecretKey() {
        SecretKey key = generateKey();
        saveKey(key);
    }

    protected SecretKey generateKey() {
        SecretKey key = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER_ALGO);
            keyGenerator.init(CIPHER_KEY_LENGHT);
            key = keyGenerator.generateKey();
        } catch( NoSuchAlgorithmException e) {
            Log.e(TAG, "Could not create secret key", e);
        }
        return key;
    }

    protected byte[] encrypt(byte[] data) {
        // 16 bytes is the IV size for AES256
        try {
            SecretKey key = loadKey();

            // Random IV
            SecureRandom rng = new SecureRandom();
            byte[] ivBytes = new byte[16];                                                                  // 16 bytes is the IV size for AES256
            rng.nextBytes(ivBytes);

            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            cipher.init(true, new ParametersWithIV(new KeyParameter(key.getEncoded()), ivBytes));

            byte[] encryptedData = cipherData(cipher, data);
            byte[] encryptedDataWithIV = new byte[encryptedData.length + ivBytes.length];                   // Make room for IV
            System.arraycopy(ivBytes, 0, encryptedDataWithIV, 0, ivBytes.length);                           // Add IV
            System.arraycopy(encryptedData, 0, encryptedDataWithIV, ivBytes.length, encryptedData.length);  // Then the encrypted data
            return encryptedDataWithIV;
        }
        catch(InvalidCipherTextException e) {
            Log.e(TAG, "Can't encrypt data", e);
        }
        return null;
    }

    protected byte[] decrypt(byte[] data) {

        try {
            SecretKey key = loadKey();

            byte[] ivBytes = new byte[16];                                                                  // 16 bytes is the IV size for AES256
            System.arraycopy(data, 0, ivBytes, 0, ivBytes.length);                                          // Get IV from data
            byte[] dataWithoutIV = new byte[data.length - ivBytes.length];                                  // Remove the room made for the IV
            System.arraycopy(data, ivBytes.length, dataWithoutIV, 0, dataWithoutIV.length);                 // Then the encrypted data

            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            cipher.init(false, new ParametersWithIV(new KeyParameter(key.getEncoded()), ivBytes));

            return cipherData(cipher, dataWithoutIV);
        }
        catch(InvalidCipherTextException e) {
            Log.e(TAG, "Can't decrypt data", e);
        }
        return null;
    }

    //endregion

}
