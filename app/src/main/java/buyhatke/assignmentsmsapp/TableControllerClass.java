package buyhatke.assignmentsmsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Chandrakant on 03-08-2016.
 */
public class TableControllerClass extends DatabaseHelper {
    public TableControllerClass(Context context) {
        super(context);
    }

    public boolean create(Message m) {

        ContentValues values = new ContentValues();

        values.put(Constants.COULUMN_ADDRESS, m.address);
        values.put(Constants.COULUMN_PERSON, m.person);
        values.put(Constants.COULUMN_MESSAGE, m.body);
        values.put(Constants.COULUMN_TIME, m.time);
        values.put(Constants.COULUMN_TYPE, m.type);

        SQLiteDatabase db = this.getWritableDatabase();

        boolean success = db.insert(Constants.TABLE_NAME, null, values) > 0;

        db.close();

        return success;
    }



}
