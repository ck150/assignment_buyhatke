package buyhatke.assignmentsmsapp;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private List<Message> messageList;
    private CustomMessageAdapter customMessageAdapter;
    private String wordSearched;
    private ListView listView;
    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        messageList = new ArrayList<Message>();
        customMessageAdapter = new CustomMessageAdapter(this,messageList);
        listView = (ListView) findViewById(R.id.search_result_list);
        listView.setAdapter(customMessageAdapter);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        handleIntent(getIntent());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, ThreadViewActivity.class);
                intent.putExtra(Constants.GET_MSG_CLICKED_NUM, messageList.get(position).address);
                intent.setAction(Constants.SEARCH_CLICK_ACTION);
                intent.putExtra(Constants.GET_SEARCH_TERM, wordSearched);
                startActivity(intent);
                //finish();
            }
        });
    }



    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query =
                    intent.getStringExtra(SearchManager.QUERY);
            wordSearched = query;
            doSearch(query);
            actionBar.setTitle("Search Results: "+query);
        }
    }

    private void doSearch(String queryStr) {
        ContentResolver contentResolver = getContentResolver();
        final String[] projection = { Constants.address_column , Constants.body_column , Constants.date_column};
        final String sa1 = "%"+queryStr+"%"; // contains an "A"
        Cursor cursor = contentResolver.query(Uri.parse("content://sms"), projection,
                Constants.address_column + " LIKE ? OR "+Constants.body_column+" LIKE ?",
                new String[] { sa1 , sa1}, Constants.date_column+" DESC");
        int indexAddress = cursor.getColumnIndex(Constants.address_column);
        int indexBody = cursor.getColumnIndex(Constants.body_column);
        int indexDate = cursor.getColumnIndex(Constants.date_column);
        if (indexBody < 0 || !cursor.moveToFirst()){
            Toast.makeText(this,"search item not found",Toast.LENGTH_SHORT).show();
            return;
        }

        //arrayAdapter.clear();
        do {
            String strAddress = cursor.getString(indexAddress);
            String strBody = cursor.getString(indexBody);
            long strDate = cursor.getLong(indexDate);

            Message m = new Message(strBody,strAddress,"","");
            messageList.add(m);
            //smsMessagesList.add(str);
        } while (cursor.moveToNext());
        customMessageAdapter.notifyDataSetChanged();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            // app icon in action bar clicked; goto parent activity.

            NavUtils.navigateUpFromSameTask(this);
            //onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
