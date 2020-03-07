package com.wuxianlin.spreadtrumtools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static String[] permissionList = new String[] {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    private static final String privateKey = "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";
    private static final String SIGN_ALGORITHMS = "SHA256WithRSA";

    private static final String fileOut = "signature.bin";

    private EditText editText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.et);
        if(!checkPermissionAllGranted(permissionList))
            ActivityCompat.requestPermissions(this, permissionList, MY_PERMISSION_REQUEST_CODE);
        else
            setDeviceSerial();
    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            setDeviceSerial();
        }
    }

    private void setDeviceSerial(){
        String serial = "";
        try {
            serial = android.os.Build.getSerial();
            editText.setText(serial);
        } catch (SecurityException e) {
        }
    }

    public void bt(View view){
        if (checkPermissionAllGranted(permissionList)) {
            String serial = editText == null ? "" : editText.getText().toString();
            if (TextUtils.isEmpty(serial)) {
                try {
                    serial = android.os.Build.getSerial();
                } catch (SecurityException e) {
                }
            }
            if (!TextUtils.isEmpty(serial)){
                byte[] identifier = getIdentifier(serial);
                byte[] signed = sign(identifier, privateKey, SIGN_ALGORITHMS);
                if (signed!=null) {
                    writeToFile(signed, fileOut);
                    Toast.makeText(this, String.format("Unlock file for serial %s is on %s of IntenalStorage!", serial, fileOut), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error when generate signature.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Serial is empty.Please input!", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, permissionList, MY_PERMISSION_REQUEST_CODE);
        }
    }

    private static byte[] getIdentifier(String serial){
        byte[] serialBytes=serial.getBytes();
        byte[] paddingBytes=new byte[64-serialBytes.length];
        Arrays.fill(paddingBytes,(byte)0);
        byte[] bytes=Arrays.copyOf(serialBytes,serialBytes.length+paddingBytes.length);
        System.arraycopy(paddingBytes, 0, bytes, serialBytes.length,paddingBytes.length);
        return bytes;
    }

    private static byte[] sign(byte[] bytes, String privateKey, String algorithm) {
        try {
            PKCS8EncodedKeySpec priPKCS8    = new PKCS8EncodedKeySpec( Base64.decode(privateKey, Base64.DEFAULT) );
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(priKey);
            signature.update(bytes);
            return signature.sign();
        } catch (Exception e) {
            return null;
        }
    }

    private void writeToFile(byte[] bytes,String outBin){
        FileOutputStream fos = null;
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), outBin);
        try {
            f.createNewFile();
            fos = new FileOutputStream(f);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
        } catch (Exception e) {
        } finally {
            if(fos!=null) {
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }


}
