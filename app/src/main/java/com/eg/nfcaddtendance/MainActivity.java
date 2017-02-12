package com.eg.nfcaddtendance;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private PersonList personList;
    private boolean ready;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mAdapter == null)
        {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        mPendingIntent = PendingIntent.getActivity(this, 0,

                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        personList = new PersonList();
        //mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(

          //      "Message from NFC Reader :-)", Locale.ENGLISH, true) });
        //handleIntent(getIntent());
        ready = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mAdapter != null)
        {
            if (!mAdapter.isEnabled())
            {
                showWirelessSettingsDialog();
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            //mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }

        // ready = true;
    }

    private void showWirelessSettingsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable NFC?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
                finish();
            }
        });
        builder.create().show();
        return;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mAdapter != null)
        {
            mAdapter.disableForegroundDispatch(this);
            //mAdapter.disableForegroundNdefPush(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        System.out.println("New Intent: " + intent.getAction());
        System.out.println("Ready?: " + ready);
        //System.out.println(NfcAdapter.ACTION_TAG_DISCOVERED);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) && ready)
        {
            ready = false;
            //System.out.println(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG).);
            Tag myTag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String cardID = bytesToHexString(myTag.getId());
            System.out.println(cardID);

            PersonList.Person p = personList.getFromID(cardID);

            if (p == null)
            {
                Toast.makeText(this, "Not a valid user", Toast.LENGTH_LONG).show();
                return;
            }
            else
                Toast.makeText(this, p.fname + " " + p.lname + " " + p.pid + " scanned", Toast.LENGTH_LONG).show();

            System.out.println(p.fname + " " + p.lname + " " + p.pid + " scanned");

            PHPConnectorWorker backgroundWorker = new PHPConnectorWorker(this);
            backgroundWorker.execute(p.fname, p.lname, p.pid);

            //Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_TAG).toString();
            /*if (rawMessages != null)
            {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                    System.out.println(messages[i]);
                }
            // Process the messages array.

            }*/
        }
    }

    private String bytesToHexString(byte[] src)
    {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            //System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }

    public String getIP()
    {
        return ((EditText) findViewById(R.id.textView)).getText().toString();
    }

    public void setMessage(String text)
    {
        ((TextView) findViewById(R.id.textView2)).setText(text);
        findViewById(R.id.textView2).setBackgroundColor(getResources().getColor(text.startsWith("ERROR") ? R.color.success : R.color.success));

        WaitToRestart wtr = new WaitToRestart(this);
        wtr.execute(1250);
    }

    public void clearScanned()
    {
        System.out.println("reztart");
        ready = true;
        findViewById(R.id.textView2).setBackgroundColor(getResources().getColor(android.R.color.white));
        ((TextView) findViewById(R.id.textView2)).setText("");
    }
}
