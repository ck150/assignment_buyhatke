package buyhatke.assignmentsmsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Chandrakant on 03-08-2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {

        super(context, Environment.getExternalStorageDirectory() + File.separator
                        + "SmsApp/Backup" + File.separator
                        + Constants.DATABASE_NAME,
                null, Constants.DB_VERSION);

        }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+Constants.TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constants.COULUMN_ADDRESS + " TEXT NOT NULL, " +
                Constants.COULUMN_PERSON + " TEXT NOT NULL, " +
                Constants.COULUMN_MESSAGE + " TEXT NOT NULL, " +
                Constants.COULUMN_TIME + " LONG NOT NULL, " +
                Constants.COULUMN_TYPE + " TEXT NOT NULL) ";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + Constants.TABLE_NAME ;
        db.execSQL(sql);
        Log.v("tag1", oldVersion + "->" + newVersion);

        onCreate(db);
    }
}
