package vyrus.bikegps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by user on 4/8/2016.
 */
public class DataBase extends SQLiteOpenHelper {
    public static final String TABLE_DESCRIPTION = "bike_description";
    public static final String TABLE_COLUMN_DESC_NAME = "desc_name";
    public static final String TABLE_COLUMN_DESC_COLOR = "desc_color";
    public static final String TABLE_COLUMN_DESC_DESCRIPTION = "desc_description";
    public static final String TABLE_COLUMN_DESC_COORDINATE = "desc_coordonate";
    public static final String TABLE_COLUMN_DESC_IMG = "desc_img";
    public static final String TABLE_COLUMN_DESC_IMG_SEC = "desc_imgsec";
    public static final String TABLE_COLUMN_OWNER = "owner";
    private static final int DATABASE_VERSION = 391;
    private static final String DATABASE_NAME = "Server_bike_gps.db";
    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
    private static int buffer_size = 1024;
    private final Context mContext;
    private final File DB_FILE;
    private SQLiteDatabase mDataBase;
    private int bytes_copied = 0;
    private int blocks_copied = 0;

    private SQLiteDatabase myDataBase;

    public DataBase(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //DB_FILE = context.getDatabasePath(DATABASE_NAME);
        DB_FILE = context.getDatabasePath(DATABASE_NAME);
        this.mContext = context;

        if (!checkDataBase()) {
            bytes_copied = 0;
            blocks_copied = 0;
            createDataBase();
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
//        db.execSQL(
//                "create table bike_description "
//                        + "(id integer primary key AUTOINCREMENT NOT NULL, " + TABLE_COLUMN_DESC_NAME + "  text, "
//                        + TABLE_COLUMN_DESC_COLOR + " text, " + TABLE_COLUMN_DESC_DESCRIPTION + " text, "
//                        + TABLE_COLUMN_DESC_COORDINATE + " text, " + TABLE_COLUMN_DESC_IMG + " blob, "
//                        + TABLE_COLUMN_DESC_IMG_SEC + " blob, " + TABLE_COLUMN_OWNER + " text)"
//        );
    }

    /**
     * Creates an empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() {

        boolean dbExist = checkDataBase(); // Double check
        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            //this.getReadableDatabase();
            //<<<<<<<<<< Dimsiss the above comment
            //By calling this method an empty database IS NOT created nor are the related -shm and -wal files
            //The method that creates the database is flawed and was only used to resolve the issue
            //of the copy failing in the absence of the databases directory.
            //The dbExist method, now utilised, checks for and creates the database directory, so there
            //is then no need to create the database just to create the databases library. As a result
            //the -shm and -wal files will not exist and thus result in the error associated with
            //Android 9+ failing with due to tables not existining after an apparently successful
            //copy.
            try {
                copyDataBase();
            } catch (IOException e) {
                File db = new File(mContext.getDatabasePath(DATABASE_NAME).getPath());
                if (db.exists()) {
                    db.delete();
                }
                e.printStackTrace();
                throw new RuntimeException("Error copying database (see stack-trace above)");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        /**
         * Does not open the database instead checks to see if the file exists
         * also creates the databases directory if it does not exists
         * (the real reason why the database is opened, which appears to result in issues)
         */

        File db = new File(mContext.getDatabasePath(DATABASE_NAME).getPath()); //Get the file name of the database
        Log.d("DBPATH", "DB Path is " + db.getPath());
        if (db.exists()) return true; // If it exists then return doing nothing

        // Get the parent (directory in which the database file would be)
        File dbdir = db.getParentFile();
        // If the directory does not exits then make the directory (and higher level directories)
        /*
        if (!dbdir.exists()) {
            db.getParentFile().mkdirs();
            dbdir.mkdirs();
        }
        */
        return false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        final String TAG = "COPYDATABASE";

        //Open your local db as the input stream
        Log.d(TAG, "Initiated Copy of the database file " + DATABASE_NAME + " from the assets folder.");
        String[] mInput = mContext.getAssets().list("");
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME); // Open the Asset file
        String dbpath = mContext.getDatabasePath(DATABASE_NAME).getPath();
        Log.d(TAG, "Asset file " + DATABASE_NAME + " found so attmepting to copy to " + dbpath);

        // Path to the just created empty db
        //String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        File outfile = new File(mContext.getDatabasePath(DATABASE_NAME).toString());
        Log.d("DBPATH", "path is " + outfile.getPath());
        //outfile.setWritable(true); // NOT NEEDED as permission already applies
        //OutputStream myoutputx2 = new FileOutputStream(outfile);
        /* Note done in checkDatabase method
        if (!outfile.getParentFile().exists()) {
            outfile.getParentFile().mkdirs();
        }
        */

        OutputStream myOutput = new FileOutputStream(outfile);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[buffer_size];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            blocks_copied++;
            Log.d(TAG, "Ateempting copy of block " + String.valueOf(blocks_copied) + " which has " + String.valueOf(length) + " bytes.");
            myOutput.write(buffer, 0, length);
            bytes_copied += length;
        }
        Log.d(TAG,
                "Finished copying Database " + DATABASE_NAME +
                        " from the assets folder, to  " + dbpath +
                        String.valueOf(bytes_copied) + "were copied, in " +
                        String.valueOf(blocks_copied) + " blocks of size " +
                        String.valueOf(buffer_size) + "."
        );
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.d(TAG, "All Streams have been flushed and closed.");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        Log.d("DBCONFIGURE", "Database has been configured ");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d("DBOPENED", "Database has been opened.");
    }


    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

//        db.execSQL("DROP TABLE IF EXISTS bike_description");
//        db.execSQL("DROP TABLE IF EXISTS settings");
//        onCreate(db);
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    public boolean insertDesc(final String descName, final String descColor, final String descDescription, final String descCoordinate, final String descImg, final String descImgSec, final String owner) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();

        contentValues.put(TABLE_COLUMN_DESC_NAME, descName);
        contentValues.put(TABLE_COLUMN_DESC_COLOR, descColor);
        contentValues.put(TABLE_COLUMN_DESC_DESCRIPTION, descDescription);
        contentValues.put(TABLE_COLUMN_DESC_COORDINATE, descCoordinate);
        contentValues.put(TABLE_COLUMN_DESC_IMG, descImg);
        contentValues.put(TABLE_COLUMN_DESC_IMG_SEC, descImgSec);
        contentValues.put(TABLE_COLUMN_OWNER, owner);
        db.insert(TABLE_DESCRIPTION, null, contentValues);
        return true;
    }

    public Cursor getData(final String sqlStr) {
        try {
            final SQLiteDatabase db = this.getReadableDatabase();
            Cursor mCur = db.rawQuery(sqlStr, null);
            if (mCur != null) {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }
}

