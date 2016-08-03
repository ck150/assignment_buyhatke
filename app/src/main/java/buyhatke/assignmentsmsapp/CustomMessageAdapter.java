package buyhatke.assignmentsmsapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Chandrakant on 02-08-2016.
 */
public class CustomMessageAdapter extends BaseAdapter{

    private Activity activity;
    private List<Message> messageList;
    private LayoutInflater inflater;

    public CustomMessageAdapter(Activity activity, List<Message> messages) {
        this.activity = activity;
        this.messageList = messages;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listview_item, null);

        TextView numText = (TextView) convertView.findViewById(R.id.list_message_number);
        TextView countText = (TextView) convertView.findViewById(R.id.list_message_count);
        TextView  bodyText = (TextView) convertView.findViewById(R.id.list_message_body);
        TextView  dateText = (TextView) convertView.findViewById(R.id.list_message_date);
        Message m = messageList.get(position);
        numText.setText(m.getNum());
        countText.setText(Integer.toString(m.getCount()));
        bodyText.setText(m.getBody());
        dateText.setText(m.getDate());

        return convertView;
    }
}
