package com.file.decryptdealercode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;

import timber.log.Timber;

import static com.file.decryptdealercode.Utility.aesDecodeAndDecrypt;
import static com.file.decryptdealercode.Utility.getEncryptedValue;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        InputStreamReader is = null;
        try {
            is = new InputStreamReader(getAssets()
                    .open("dealer_code.csv"));

        BufferedReader reader = new BufferedReader(is);

            reader.readLine();

        String line;
        while ((line = reader.readLine()) != null) {
            String data  = line;
            String[] split = data.split(",");
            Log.i("data---", data);
            decryptAndSaveData(split[0],split[1],split[2]);

        }
            Log.d("data---", new Gson().toJson(dataModelArrayList).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            exportEmailInCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void decryptAndSaveData(String rowID,String dealerCodeEncrypted,String loginIdEncrypted){
        String key = "aPOuf1BoghkeJTH%2BB6tiUg%5Cu003d%5Cu003d";
        //String key = "aPOuf1BoghkeJTH+B6tiUg\u003d\u003d";
        String encryptkey = URLDecoder.decode(key);
        encryptkey = encryptkey.replace("\\u003d", "=");

        String dealerCode = dealerCodeEncrypted;
        dealerCode =  dealerCode.replaceAll(" ", "+");

        String loginID = loginIdEncrypted;
        loginID = loginID.replace(" ", "+");

        String decrptedDealerID = aesDecodeAndDecrypt(dealerCode, encryptkey);
        String decryptedLoginId = aesDecodeAndDecrypt(loginID, encryptkey);

        DataModel model = new DataModel();
        model.setNRIC(rowID);
        model.setDCODE(dealerCodeEncrypted);
        model.setDUID(loginIdEncrypted);
        model.setDecryptedDCODE(decrptedDealerID);
        model.setDecryptedDUID(decryptedLoginId);
        dataModelArrayList.add(model);

    }
    public void exportEmailInCSV() throws IOException {
        {

            File folder = new File(getFilesDir()
                    + "/Folder");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);


            final String filename = folder.toString() + "/" + "Decrypted_Code.csv";


            // show waiting screen
            CharSequence contentTitle = "Decrypted Data";
            final ProgressDialog progDailog = ProgressDialog.show(
                    MainActivity.this, contentTitle, "",
                    true);//please wait
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {




                }
            };

            new Thread() {
                public void run() {
                    try {

                        FileWriter fw = new FileWriter(filename);

                        fw.append("S.No,NRIC,DCODE,Result,DUID,Result");
                        fw.append("\n");

                        for(int i=0;i<dataModelArrayList.size();i++) {
                            DataModel dataModel = dataModelArrayList.get(i);
                            fw.append(""+i);
                            fw.append(',');

                            fw.append(dataModel.getNRIC());
                            fw.append(',');

                            fw.append(dataModel.getDCODE());
                            fw.append(',');

                            fw.append(dataModel.getDecryptedDCODE());
                            fw.append(',');

                            fw.append(dataModel.getDUID());
                            fw.append(',');

                            fw.append(dataModel.getDecryptedDUID());
                            fw.append(',');

                            fw.append('\n');

                        }


                        // fw.flush();
                        fw.close();

                    } catch (Exception e) {
                    }
                    handler.sendEmptyMessage(0);
                    progDailog.dismiss();
                }
            }.start();

        }

    }

ArrayList<DataModel> dataModelArrayList = new ArrayList<>();
    public class DataModel {
        String NRIC;String DCODE;String DUID; String decryptedDCODE;String decryptedDUID;
        public String getNRIC() {
            return NRIC;
        }

        public void setNRIC(String NRIC) {
            this.NRIC = NRIC;
        }

        public String getDCODE() {
            return DCODE;
        }

        public void setDCODE(String DCODE) {
            this.DCODE = DCODE;
        }

        public String getDUID() {
            return DUID;
        }

        public void setDUID(String DUID) {
            this.DUID = DUID;
        }

        public String getDecryptedDCODE() {
            return decryptedDCODE;
        }

        public void setDecryptedDCODE(String decryptedDCODE) {
            this.decryptedDCODE = decryptedDCODE;
        }

        public String getDecryptedDUID() {
            return decryptedDUID;
        }

        public void setDecryptedDUID(String decryptedDUID) {
            this.decryptedDUID = decryptedDUID;
        }


    }
}