package buyhatke.assignmentsmsapp;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;




public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private List<Message> messageList;
    private CustomMessageAdapter customMessageAdapter;
    private ListView listView;
    Map addMap;
    public static MainActivity activity;
    private Handler handler;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_all_messages);
        messageList = new ArrayList<>();
        customMessageAdapter = new CustomMessageAdapter(this,messageList);
        listView.setAdapter(customMessageAdapter);
        ListOnClick();
        activity = this;
        addMap = new HashMap();
        refreshList();
        floatingButtonClick();

    }

    public static MainActivity getInstance(){
        return activity;
    }


    public void onResume(){
        super.onResume();
        //registerReceiver(br, new IntentFilter(Constants.BROADCAST_INTENT));

    }

    public void startAPI(){
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
    }



    private void floatingButtonClick(){
        FloatingActionButton myFab = (FloatingActionButton)  this.findViewById(R.id.fab_add);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ComposeMessageActivity.class);
                startActivity(i);
            }
        });
    }



    public void refreshList(){
        addMap.clear();
        messageList.clear();

        getSMSList();
    }

    private void getSMSList() {
        ContentResolver contentResolver = getContentResolver();
        final String[] projection = { Constants.address_column , Constants.body_column , Constants.date_column};
        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), projection,
                null, null, Constants.date_column + " DESC");
        int indexAddress = cursor.getColumnIndex(Constants.address_column);
        int indexBody = cursor.getColumnIndex(Constants.body_column);
        int indexDate = cursor.getColumnIndex(Constants.date_column);

        if (indexAddress < 0 || !cursor.moveToFirst()){
            Toast.makeText(this,"null query result",Toast.LENGTH_SHORT).show();
            return;
        }
        do {
            String strAddress = cursor.getString(indexAddress);
            String strBody = cursor.getString(indexBody);
            long strDate = cursor.getLong(indexDate);
            if(!addMap.containsKey(strAddress)){
                addMap.put(strAddress,messageList.size());
                Date date = new Date(strDate);
                SimpleDateFormat formatD = new SimpleDateFormat("dd/MM/yy");
                SimpleDateFormat formatT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                String dateText = formatD.format(date);
                String timeText = formatT.format(date);
                Message m = new Message(strBody,strAddress,dateText,timeText);
                messageList.add(m);
            }else{
                int in = (int) addMap.get(strAddress);
                Message m = messageList.get(in);
                m.incrementCount();
            }
            //arrayAdapter.add(str);
            Log.v("tag1", strAddress);

        } while (cursor.moveToNext());
        Log.v("tag1", Integer.toString(messageList.size()));
        customMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        super.onPause();

        //messageList.clear();
    }

    public void ListOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ThreadViewActivity.class);
                intent.setAction(Constants.VIEW_THREAD_ACTION);
                intent.putExtra(Constants.GET_MSG_CLICKED_NUM, messageList.get(position).address);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem mSearchMenuItem = menu.findItem(R.id.search_id);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.search_id) {
            onSearchRequested();
            return true;
        }
        if (id == R.id.backup_id) {
            progress = ProgressDialog.show(this, "Backing up messages",
                    "Please wait", true);
            new Thread(new DB_Runnable()).start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("tag1", "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        // Called whenever the API client fails to connect.
        Log.i("tag1", "GoogleApiClient connection failed: " + result.toString());

        if (!result.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }

        try {
            Log.v("tag1","startResolutionForResult");
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {

            Log.e("tag1", "Exception while starting resolution activity", e);
        }
    }


    public class DB_Runnable implements Runnable{

        @Override
        public void run() {
            startBackup();
        }
    }

    private void startBackup(){



        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + "/SmsApp/Backup");
        if(dir.exists()){
            if (dir.isDirectory())
            {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++)
                {
                    new File(dir, children[i]).delete();
                }
            }
        }
        dir.mkdir();



        Uri mSmsinboxQueryUri = Uri.parse("content://sms");
        Cursor cursor1 = getContentResolver().query(
                mSmsinboxQueryUri,
                new String[] { "_id", "thread_id", "address", "person", "date",
                        "body", "type" }, null, null, null);
        //startManagingCursor(cursor1);
        String[] columns = new String[] { "_id", "thread_id", "address", "person", "date", "body",
                "type" };
        boolean success = false;
        if (cursor1.getCount() > 0) {
            String count = Integer.toString(cursor1.getCount());
            Log.d("Count",count);
            String time1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

            while (cursor1.moveToNext()) {


                String messageId = cursor1.getString(cursor1
                        .getColumnIndex(columns[0]));

                String threadId = cursor1.getString(cursor1
                        .getColumnIndex(columns[1]));

                String address = cursor1.getString(cursor1
                        .getColumnIndex(columns[2]));

                String name = cursor1.getString(cursor1
                        .getColumnIndex(columns[3]));
                long date = cursor1.getLong(cursor1
                        .getColumnIndex(columns[4]));
                String msg = cursor1.getString(cursor1
                        .getColumnIndex(columns[5]));
                String type = cursor1.getString(cursor1
                        .getColumnIndex(columns[6]));

                Date mDate = new Date(date);
                SimpleDateFormat formatT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                String timeText = formatT.format(date);

                Message m = new Message(msg,address,"",timeText);
                m.person = name;
                m.type = type;
                success = new TableControllerClass(this).create(m);

            }
                //generateCSVFileForSMS(smsBuffer);
        } else {
            Log.e("tag1", "cursor size 0");
        }

        if (success) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Backup on sdCard Completed", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    }
                });
            }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(progress.isShowing()){
                    progress.dismiss();
                }
            }
        });
            startAPI();
        }



}

