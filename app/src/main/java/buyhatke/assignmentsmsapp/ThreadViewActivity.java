package buyhatke.assignmentsmsapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ThreadViewActivity extends AppCompatActivity {

    LinearLayout ll;
    LinearLayout msg_ll;
    boolean searchIntent;
    String wordSearched;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_view);
        ll = (LinearLayout) findViewById(R.id.thread_linear);
        searchIntent = false;
        Intent i = getIntent();

        if(i.getAction().equals(Constants.SEARCH_CLICK_ACTION)){
            searchIntent = true;
            wordSearched = i.getStringExtra(Constants.GET_SEARCH_TERM);
        }
        String num = i.getStringExtra(Constants.GET_MSG_CLICKED_NUM);
        populate_thread(num);
        actionBar = getSupportActionBar();
        actionBar.setTitle("SMS Thread: " + num);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }


    private void addMessage(String body,String time,boolean received){
        msg_ll =  (LinearLayout) getLayoutInflater().inflate(R.layout.message_layout, null);
        TextView tb = (TextView) msg_ll.findViewById(R.id.thread_text_body);
        TextView tt = (TextView) msg_ll.findViewById(R.id.thread_text_time);
        LinearLayout ll_color = (LinearLayout) msg_ll.findViewById(R.id.ll_change_color);
        if(!received){
            ll_color.setBackgroundColor(getResources().getColor(R.color.color_sent));
            View  v = msg_ll.findViewById(R.id.blank_view);
            msg_ll.removeView(v);
            msg_ll.addView(v, 0);
        }
        if(searchIntent) {
            body = body.replaceAll("(?i)"+wordSearched, "<font color='red'>" + wordSearched + "</font>");
        }
        tb.setText(Html.fromHtml(body));
        tt.setText(time);
        ll.addView(msg_ll);
    }

    private void populate_thread(String address) {
        ContentResolver contentResolver = getContentResolver();
        final String[] projection = { Constants.address_column , Constants.body_column , Constants.date_column , Constants.type_column};
        final String sa1 = "%"+address+"%"; // contains an "A"
        Cursor cursor = contentResolver.query(Uri.parse("content://sms"), projection,
                Constants.address_column + " LIKE ? OR "+Constants.body_column+" LIKE ?",
                new String[] { sa1 , sa1}, Constants.date_column+" ASC");
        int indexAddress = cursor.getColumnIndex(Constants.address_column);
        int indexBody = cursor.getColumnIndex(Constants.body_column);
        int indexDate = cursor.getColumnIndex(Constants.date_column);
        int indexType = cursor.getColumnIndex(Constants.type_column);

        if (indexBody < 0 || !cursor.moveToFirst()){
            Toast.makeText(this, "search item not found", Toast.LENGTH_SHORT).show();
            return;
        }

        //arrayAdapter.clear();
        do {
            String strAddress = cursor.getString(indexAddress);
            if(strAddress.equals(address)){
                String strBody = cursor.getString(indexBody);
                String strType = cursor.getString(indexType);
                boolean received=false;
                if(strType.equals("1")){
                    received = true;
                }else if(strType.equals("2")){
                    received = false;
                }
                long strDate = cursor.getLong(indexDate);
                Date date = new Date(strDate);
                SimpleDateFormat formatT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                String timeText = formatT.format(date);
                addMessage(strBody,timeText,received);
            }
            //smsMessagesList.add(str);
        } while (cursor.moveToNext());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            // app icon in action bar clicked; goto parent activity.
            //NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
