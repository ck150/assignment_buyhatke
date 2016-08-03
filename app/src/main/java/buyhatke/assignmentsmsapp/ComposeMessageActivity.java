package buyhatke.assignmentsmsapp;

import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class ComposeMessageActivity extends AppCompatActivity {

    private AutoCompleteTextView editNum;
    private EditText editMsg;
    SimpleCursorAdapter mAdapter;

    final static int[] to = new int[] { android.R.id.text1 };
    final static String[] from = new String[]{"state"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        editNum = (AutoCompleteTextView) findViewById(R.id.edit_num_compose);
        editMsg = (EditText) findViewById(R.id.edit_msg_compose);
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[] { Contacts.DISPLAY_NAME },
                new int[] {android.R.id.text1},
                0);
        editNum.setAdapter(mAdapter);

        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                return getCursor(str);
            } });

        mAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            String phoneNumber;
            public CharSequence convertToString(Cursor cur) {
                int index = cur.getColumnIndex(Contacts.DISPLAY_NAME);
                int index2 = cur.getColumnIndex(Contacts._ID);

                //return cur.getString(index)
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ cur.getString(index2),
                        null, null);
                while (phones.moveToNext()){
                    phoneNumber = phones.getString(
                            phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                return phoneNumber;
            }
        });


    }

    protected void sendSMS() {
        String toPhoneNumber = editNum.getText().toString();
        String smsMessage = editMsg.getText().toString();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null);
            Toast.makeText(getApplicationContext(), "SMS successfully sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS sending failed.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public Cursor getCursor(CharSequence str) {
        String select = Contacts.DISPLAY_NAME + " LIKE ?";
        String[] selectArgs = {"%" + str + "%"};
        String[] contactsProjection = new String[]{
                Contacts._ID,
                Contacts.DISPLAY_NAME,
                Contacts.LOOKUP_KEY };

        return getContentResolver().query(Contacts.CONTENT_URI, contactsProjection, select, selectArgs, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            if(editMsg.getText().toString().equals("")){
                Toast.makeText(this,"Type message",Toast.LENGTH_SHORT).show();
                return false;
            }
            showAlertSendSMS();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAlertSendSMS(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                switch(arg1){
                    case DialogInterface.BUTTON_POSITIVE:
                        sendSMS();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to send this SMS?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }
}
