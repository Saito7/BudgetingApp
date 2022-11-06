package com.example.budgetingappv1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    // region Transaction Table Static Definitions

    public static final String TRANSACTIONS_TABLE = "TRANSACTIONS";
    public static final String COLUMN_TRANSACTION_ID = "ID";
    public static final String COLUMN_TRANSACTION_GROCERIES_ID = "GROCERIES_ID";
    public static final String COLUMN_TRANSACTION_TITLE = "TITLE";
    public static final String COLUMN_TRANSACTION_RECIPIENT = "RECIPIENT";
    public static final String COLUMN_TRANSACTION_AMOUNT = "AMOUNT";
    public static final String COLUMN_TRANSACTION_DATE = "DATE";
    public static final String COLUMN_TRANSACTION_CATEGORY = "CATEGORY";
    public static final String COLUMN_TRANSACTION_RECURRING = "RECURRING";

    // endregion

    // region Category Table Static Definitions

    public static final String CATEGORIES_TABLE = "CATEGORIES";
    public static final String COLUMN_CATEGORY_NAME = "NAME";
    public static final String COLUMN_CATEGORY_COLOUR = "COLOUR";
    public static final String COLUMN_CATEGORY_ICON = "IMAGE";
    public static final String COLUMN_CATEGORY_BUDGET = "BUDGET";
    public static final String COLUMN_CATEGORY_ESSENTIAL = "ESSENTIAL";

    // endregion

    // region Groceries Transactions Table Static Definitions

    public static final String GROCERIES_TRANSACTIONS_TABLE = "GROCERIES_TRANSACTIONS";
    public static final String COLUMN_GROCERIES_ID = "ID";
    public static final String COLUMN_GROCERIES_ITEMS = "ITEMS";

    // endregion

    // region Items will be stored in a csv format to make it easy to read all items that were stored

    // Groceries Table Static Definitions

    public static final String GROCERIES_TABLE = "GROCERIES";
    public static final String COLUMN_GROCERY_ID = "ID";
    public static final String COLUMN_GROCERY_NAME = "NAME";
    public static final String COLUMN_GROCERY_PRICE = "PRICE";

    // endregion

    public static final List<Data_Category> CATEGORIES = new ArrayList<>();

    private Boolean added_categories = false;

    public DatabaseHelper(@Nullable Context context) {
        super(context, "Transactions.db", null, 1);
        Data_Category category = new Data_Category("Lifestyle", 200, Color.GREEN, null, false);
        Data_Category category2 = new Data_Category("Entertainment", 200, Color.BLUE, null, false);
        Data_Category category3 = new Data_Category("Groceries", 200, Color.DKGRAY, null, true);
        Data_Category category4 = new Data_Category("Transportation", 200, Color.MAGENTA, null, true);
        Data_Category category5 = new Data_Category("Utilities", 200, Color.YELLOW, null, true);

        CATEGORIES.add(category);
        CATEGORIES.add(category2);
        CATEGORIES.add(category3);
        CATEGORIES.add(category4);
        CATEGORIES.add(category5);
    }

    // This is called the first time a database is accessed.
    // This should have code to create a new database.
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTransactionsTable = "CREATE TABLE " + TRANSACTIONS_TABLE + " (" +
                COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRANSACTION_GROCERIES_ID + " INTEGER DEFAULT -1, " +
                COLUMN_TRANSACTION_TITLE + " TEXT, " +
                COLUMN_TRANSACTION_RECIPIENT + " TEXT, " +
                COLUMN_TRANSACTION_AMOUNT + " FLOAT, " +
                COLUMN_TRANSACTION_DATE + " DATE, " +
                COLUMN_TRANSACTION_CATEGORY + " TEXT, " +
                COLUMN_TRANSACTION_RECURRING + " BOOL)";

        String createCategoriesTable = "CREATE TABLE " + CATEGORIES_TABLE + " (" +
                COLUMN_CATEGORY_NAME + " TEXT PRIMARY KEY, " +
                COLUMN_CATEGORY_COLOUR + " INTEGER, " +
                COLUMN_CATEGORY_ICON + " BLOB NOT NULL, " +
                COLUMN_CATEGORY_BUDGET + " FLOAT, " +
                COLUMN_CATEGORY_ESSENTIAL + " BOOL)";

        String createGroceryTransactionsTable = "CREATE TABLE " + GROCERIES_TRANSACTIONS_TABLE + " (" +
                COLUMN_GROCERIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROCERIES_ITEMS + " TEXT)";

        String createGroceriesTable = "CREATE TABLE " + GROCERIES_TABLE + " (" +
                COLUMN_GROCERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROCERY_NAME + " TEXT, " +
                COLUMN_GROCERY_PRICE + " FLOAT)";

        db.execSQL(createTransactionsTable);
        db.execSQL(createCategoriesTable);
        db.execSQL(createGroceryTransactionsTable);
        db.execSQL(createGroceriesTable);

    }

    // This is called if the database version number changes.
    // It prevents previous users apps from breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTIONS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
        onCreate(db);
    }

    /**
     * This function takes in either a transaction or category
     * and adds it to the corresponding table.
     *
     * @param obj an object of type Category or Transaction
     * @return the boolean value corresponding to the success of adding to a table
     */
    public boolean addOne(Object obj){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        long insert = -1;

        if(obj instanceof Data_Transaction) {
            Data_Transaction transaction = (Data_Transaction) obj;

            if (transaction.getId() != -1) {
                cv.put(COLUMN_TRANSACTION_ID, transaction.getId());
            }
            cv.put(COLUMN_TRANSACTION_TITLE, transaction.getTitle());
            cv.put(COLUMN_TRANSACTION_RECIPIENT, transaction.getRecipient());
            cv.put(COLUMN_TRANSACTION_AMOUNT, transaction.getAmount());
            cv.put(COLUMN_TRANSACTION_DATE, transaction.getDate());
            cv.put(COLUMN_TRANSACTION_CATEGORY, transaction.getCategory());
            cv.put(COLUMN_TRANSACTION_RECURRING, transaction.isRecurring());

            insert = db.insert(TRANSACTIONS_TABLE, null, cv);

        }
        else if(obj instanceof Data_Category){
            Data_Category category = (Data_Category) obj;

            cv.put(COLUMN_CATEGORY_NAME, category.getName());
            cv.put(COLUMN_CATEGORY_BUDGET, category.getBudget());
            cv.put(COLUMN_CATEGORY_COLOUR, category.getColor());
            cv.put(COLUMN_CATEGORY_ICON, category.getIconBytes());
            cv.put(COLUMN_CATEGORY_ESSENTIAL, category.isEssential());

            insert = db.insert(CATEGORIES_TABLE, null, cv);
        }

        db.close();

        return insert != -1;
    }

    /**
     * This function takes in either a transaction or category
     * and updates it's fields accordingly.
     *
     * @param obj an object of type Category or Transaction
     * @param id the unique id that represents a category or transaction
     * @return the boolean value corresponding to the success of updating a record
     */
    public boolean updateOne(Object obj, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        int success = -1;

        if(obj instanceof Data_Transaction) {
            Data_Transaction transaction = (Data_Transaction) obj;

            cv.put(COLUMN_TRANSACTION_TITLE, transaction.getTitle());
            cv.put(COLUMN_TRANSACTION_RECIPIENT, transaction.getRecipient());
            cv.put(COLUMN_TRANSACTION_AMOUNT, transaction.getAmount());
            cv.put(COLUMN_TRANSACTION_DATE, transaction.getDate());
            cv.put(COLUMN_TRANSACTION_CATEGORY, transaction.getCategory());
            cv.put(COLUMN_TRANSACTION_RECURRING, transaction.isRecurring());

            success = db.update(TRANSACTIONS_TABLE, cv, COLUMN_TRANSACTION_ID + "=?", new String[]{String.valueOf(id)});
        }
        else if(obj instanceof Data_Category){
            Data_Category category = (Data_Category) obj;

            cv.put(COLUMN_CATEGORY_BUDGET, category.getBudget());
            cv.put(COLUMN_CATEGORY_COLOUR, category.getColor());
            cv.put(COLUMN_CATEGORY_ESSENTIAL, category.isEssential());

            success = db.update(CATEGORIES_TABLE, cv, COLUMN_CATEGORY_NAME + "=?", new String[]{category.getName()});
        }

        db.close();
        
        return success == 1;
    }

    /**
     * This function takes in either a transaction or category and
     * deletes it's record from the corresponding table.
     *
     * @param obj an object of type Category or Transaction
     * @return the boolean value corresponding to the success of deleting a record
     */
    public boolean deleteOne(Object obj){
        // find transaction in the database, if found then delete and return true
        // if not found, return false
        int delete = -1;

        SQLiteDatabase db = this.getWritableDatabase();
        if(obj instanceof Data_Transaction) {
            Data_Transaction transaction = (Data_Transaction) obj;
            delete = db.delete(TRANSACTIONS_TABLE, COLUMN_TRANSACTION_ID + " = ?", new String[]{String.valueOf(transaction.getId())});
        }
        else if(obj instanceof Data_Category){
            Data_Category category = (Data_Category) obj;
            delete = db.delete(CATEGORIES_TABLE, COLUMN_CATEGORY_NAME + " = ?", new String[]{category.getName()});
        }

        db.close();
        
        return delete == 1;
    }


    /**
     * This function wipes all data from the database
     */
    public void clearData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TRANSACTIONS_TABLE);
        db.execSQL("DELETE FROM " + CATEGORIES_TABLE);
        db.close();
    }

    // region Transaction Table Methods

    /**
     * This function takes in the id for a transaction record
     * and returns the fields storing the data in a object of
     * class transaction.
     *
     * @param id unique id of record
     * @return an object containing data from transaction record
     */
    @SuppressLint("Range")
    public Data_Transaction selectTransaction(int id){
        Data_Transaction transaction;

        String queryString = "SELECT * FROM " + TRANSACTIONS_TABLE + " WHERE " + COLUMN_TRANSACTION_ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor is the result set from a SQL statement
        Cursor cursor = db.rawQuery(queryString, null);

        // True if items were selected
        if(cursor.moveToFirst()){

            int transaction_ID = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_ID));
            int groceries_ID = cursor.getInt(cursor.getColumnIndex(COLUMN_GROCERY_ID));
            String transaction_title = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TITLE));
            String transaction_recipient = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_RECIPIENT));
            float transaction_amount = cursor.getFloat(cursor.getColumnIndex(COLUMN_TRANSACTION_AMOUNT));
            String transaction_date = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_DATE));
            String transaction_category = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_CATEGORY));
            boolean transaction_recurring = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_RECURRING)) == 1;

            transaction = new Data_Transaction(transaction_ID,
                    groceries_ID,
                    transaction_title,
                    transaction_recipient,
                    transaction_amount,
                    transaction_date,
                    transaction_category,
                    transaction_recurring);

        }
        else{
            // failure, do not add anything to the list
            transaction = null;
        }

        // close both the cursor and the db when done
        cursor.close();
        db.close();

        return transaction;
    }

    /**
     * This function gets a list of all the titles, of all
     * the transactions and returns them.
     *
     * @return a list of the titles of all transactions
     */
    @SuppressLint("Range")
    public List<String> getTransactionTitles(){
        List<String> returnList = new ArrayList<>();

        // get data from the database
        String queryString = "SELECT " + COLUMN_TRANSACTION_TITLE + " FROM " + TRANSACTIONS_TABLE;


        SQLiteDatabase db = this.getReadableDatabase();

        // cursor is the result set from a SQL statement
        Cursor cursor = db.rawQuery(queryString, null);

        // true if items were selected
        if(cursor.moveToFirst()){
            do{
                returnList.add(cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TITLE)));
            } while(cursor.moveToNext());
        }
        else{
            // failure, do not add anything to the list
        }

        // close both the cursor and the db when done
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * This function selects all records in the database matching
     * the search query, if this is null, then all records are selected.
     * The records are returned as a list of transaction objects.
     *
     * @param query the string which the title must match
     * @return a list of all transactions as transaction objects
     */
    @SuppressLint("Range")
    public List<Data_Transaction> getAllTransactionsLike(String query){
        List<Data_Transaction> returnList = new ArrayList<>();
        Data_Transaction transaction;
        String queryString = null;

        // get data from the database
        if (query != null) {
            queryString = "SELECT * FROM "
                    + TRANSACTIONS_TABLE + " WHERE " + COLUMN_TRANSACTION_TITLE + " LIKE '%" + query + "%'" +
                    " ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, "
                    + "LOWER(" + COLUMN_TRANSACTION_TITLE + ") ASC,"
                    + COLUMN_TRANSACTION_TITLE + " ASC";
        } else {
            queryString = "SELECT * FROM " + TRANSACTIONS_TABLE
                    + " ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, "
                    + "LOWER(" + COLUMN_TRANSACTION_TITLE + ") ASC,"
                    + COLUMN_TRANSACTION_TITLE + " ASC";
        }

        SQLiteDatabase db = this.getReadableDatabase();

        // cursor is the result set from a SQL statement
        Cursor cursor = db.rawQuery(queryString, null);

        // true if items were selected
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new transaction objects
            // put them into return list
            do {
                int transaction_ID = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_ID));
                int groceries_ID = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_GROCERIES_ID));
                String transaction_title = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TITLE));
                String transaction_recipient = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_RECIPIENT));
                float transaction_amount = cursor.getFloat(cursor.getColumnIndex(COLUMN_TRANSACTION_AMOUNT));
                String transaction_date = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_DATE));
                String transaction_category = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_CATEGORY));
                boolean transaction_recurring = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_RECURRING)) == 1;

                transaction = new Data_Transaction(transaction_ID,
                        groceries_ID,
                        transaction_title,
                        transaction_recipient,
                        transaction_amount,
                        transaction_date,
                        transaction_category,
                        transaction_recurring);

                returnList.add(transaction);

            } while (cursor.moveToNext());
        }

        // close both the cursor and the db when done
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * This function finds the total amount spent on each category and
     * creates a map that takes a category name and gives the amount
     * spent.
     *
     * @return a map which maps a category to the total amount spent
     * on that category.
     */
    public Map<String, Float> getCategorySpendings(){
        Map<String, Float> map = new HashMap<String, Float>();

        List<String> categories = getCategoryNames();

        for(String cat_name: categories){
            String queryString = "SELECT SUM(" + COLUMN_TRANSACTION_AMOUNT + ") FROM "
                    + TRANSACTIONS_TABLE + " WHERE "
                    + COLUMN_TRANSACTION_CATEGORY + " = '" + cat_name + "'";

            SQLiteDatabase db = this.getReadableDatabase();

            // Cursor is the result set from a SQL statement
            Cursor cursor = db.rawQuery(queryString, null);

            // True if items were selected
            if(cursor.moveToFirst()){

                Float sum = cursor.getFloat(0);
                map.put(cat_name, sum);

            }
        }

        return  map;
    }

    public float getTotalSpendings(){
        String queryString = "SELECT SUM(" + COLUMN_TRANSACTION_AMOUNT + ") FROM " + TRANSACTIONS_TABLE;
        float sum = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        // True if items were selected
        if(cursor.moveToFirst()){
            sum = cursor.getFloat(0);
        }

        return sum;
    }

    /**
     * This function selects all records in the database matching the search
     * query and that are recurring.
     *
     * @param query the string which the title must match
     * @return a list of all recurring transactions as transaction objects
     */
    @SuppressLint("Range")
    public List<Data_Transaction> getRecurringTransactionsLike(String query){
        List<Data_Transaction> returnList = new ArrayList<>();
        Data_Transaction transaction;
        String queryString = null;

        // get data from the database
        if (query != null) {
            queryString = "SELECT * FROM "
                    + TRANSACTIONS_TABLE + " WHERE " + COLUMN_TRANSACTION_TITLE + " LIKE '%" + query + "%'"
                    + " AND " + COLUMN_TRANSACTION_RECURRING + " = 1"
                    + " ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, "
                    + "LOWER(" + COLUMN_TRANSACTION_TITLE + ") ASC,"
                    + COLUMN_TRANSACTION_TITLE + " ASC";
        } else {
            queryString = "SELECT * FROM " + TRANSACTIONS_TABLE
                    + " WHERE " + COLUMN_TRANSACTION_RECURRING + " = 1"
                    + " ORDER BY " + COLUMN_TRANSACTION_DATE + " DESC, "
                    + "LOWER(" + COLUMN_TRANSACTION_TITLE + ") ASC,"
                    + COLUMN_TRANSACTION_TITLE + " ASC";
        }

        SQLiteDatabase db = this.getReadableDatabase();

        // cursor is the result set from a SQL statement
        Cursor cursor = db.rawQuery(queryString, null);

        // true if items were selected
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new transaction objects
            // put them into return list
            do {
                int transaction_ID = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_ID));
                int groceries_ID = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_GROCERIES_ID));
                String transaction_title = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_TITLE));
                String transaction_recipient = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_RECIPIENT));
                float transaction_amount = cursor.getFloat(cursor.getColumnIndex(COLUMN_TRANSACTION_AMOUNT));
                String transaction_date = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_DATE));
                String transaction_category = cursor.getString(cursor.getColumnIndex(COLUMN_TRANSACTION_CATEGORY));
                boolean transaction_recurring = cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_RECURRING)) == 1;

                transaction = new Data_Transaction(transaction_ID,
                        groceries_ID,
                        transaction_title,
                        transaction_recipient,
                        transaction_amount,
                        transaction_date,
                        transaction_category,
                        transaction_recurring);

                returnList.add(transaction);

            } while (cursor.moveToNext());
        }

        // close both the cursor and the db when done
        cursor.close();
        db.close();
        return returnList;
    }

    // endregion

    // region Category Table Methods

    /**
     * This function takes in a list of categories. This is used
     * to add the list of default categories to the database upon
     * boot.
     *
     * @param categories the list of default categories
     */
    public void addCategories(List<Data_Category> categories){
        List<String> category_names = getCategoryNames();
        if(!category_names.isEmpty()){
            return;
        }

        if(!added_categories) {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues cv = new ContentValues();


            for (Data_Category category : categories) {

                if(category_names.contains(category.getName())){
                    break;
                }

                cv.put(COLUMN_CATEGORY_NAME, category.getName());
                cv.put(COLUMN_CATEGORY_BUDGET, category.getBudget());
                cv.put(COLUMN_CATEGORY_COLOUR, category.getColor());
                cv.put(COLUMN_CATEGORY_ESSENTIAL, category.isEssential());

                db.insert(CATEGORIES_TABLE, null, cv);

            }

            added_categories = true;

            db.close();
        }

    }

    /**
     * This function takes in the name for a category record
     * and returns the fields storing the data in a object of
     * class category.
     *
     * @param name unique name of record
     * @return an object containing data from category record
     */
    @SuppressLint("Range")
    public Data_Category selectCategory(String name){
        Data_Category category;

        String queryString = "SELECT * FROM " + CATEGORIES_TABLE + " WHERE " + COLUMN_CATEGORY_NAME + " = '" + name + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor is the result set from a SQL statement
        Cursor cursor = db.rawQuery(queryString, null);

        // True if items were selected
        if(cursor.moveToFirst()){

            String category_name = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME));
            float category_budget = cursor.getFloat(cursor.getColumnIndex(COLUMN_CATEGORY_BUDGET));
            int category_colour = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_COLOUR));
            byte[] category_icon = cursor.getBlob(cursor.getColumnIndex(COLUMN_CATEGORY_ICON));
            Boolean category_essential = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ESSENTIAL)) == 1;

            category = new Data_Category(category_name, category_budget, category_colour, category_icon, category_essential);
        }
        else{
            // failure, do not add anything to the list
            category = null;
        }

        // close both the cursor and the db when done
        cursor.close();
        db.close();

        return category;
    }

    /**
     * This function gets a list of all the category names,
     * of all the categories and returns them.
     *
     * @return a list of the names of all categories
     */
    @SuppressLint("Range")
    public List<String> getCategoryNames(){
        List<String> returnList = new ArrayList<>();

        // get data from the database
        String queryString = "SELECT " + COLUMN_CATEGORY_NAME + " FROM " + CATEGORIES_TABLE;


        SQLiteDatabase db = this.getReadableDatabase();

        // cursor is the result set from a SQL statement
        Cursor cursor = db.rawQuery(queryString, null);

        // true if items were selected
        if(cursor.moveToFirst()){
            do{
                returnList.add(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
            } while(cursor.moveToNext());
        }
        else{
            // failure, do not add anything to the list
        }

        // close both the cursor and the db when done
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * This function selects all records in the database matching
     * the search query, if this is null, then all records are selected.
     * The records are returned as a list of category objects.
     *
     * @param query the string which the name must match
     * @return a list of all categories as category objects
     */
    @SuppressLint("Range")
    public List<Data_Category> getAllCategoriesLike(String query){
        List<Data_Category> returnList = new ArrayList<>();
        Data_Category category;
        String queryString = null;

        // get data from the database
        if (query != null) {
            queryString = "SELECT * FROM "
                    + CATEGORIES_TABLE + " WHERE " + COLUMN_CATEGORY_NAME + " LIKE '%" + query + "%'" +
                    " ORDER BY " + "LOWER(" + COLUMN_CATEGORY_NAME + ") ASC,"
                    + COLUMN_CATEGORY_NAME + " ASC";
        } else {
            queryString = "SELECT * FROM " + CATEGORIES_TABLE
                    + " ORDER BY " + "LOWER(" + COLUMN_CATEGORY_NAME + ") ASC,"
                    + COLUMN_CATEGORY_NAME + " ASC";
        }

        SQLiteDatabase db = this.getReadableDatabase();

        // cursor is the result set from a SQL statement
        Cursor cursor = db.rawQuery(queryString, null);

        // true if items were selected
        if (cursor.moveToFirst()) {
            // loop through the cursor and create new category objects
            // put them into return list
            do {
                String category_name = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME));
                float category_budget = cursor.getFloat(cursor.getColumnIndex(COLUMN_CATEGORY_BUDGET));
                int category_colour = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_COLOUR));
                byte[] category_icon = cursor.getBlob(cursor.getColumnIndex(COLUMN_CATEGORY_ICON));
                Boolean category_essential = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ESSENTIAL)) == 1;

                category = new Data_Category(category_name, category_budget, category_colour, category_icon, category_essential);

                returnList.add(category);

            } while (cursor.moveToNext());
        }


        // close both the cursor and the db when done
        cursor.close();
        db.close();
        return returnList;

    }

    // endregion

    // region Groceries Transactions Table Methods

    /**
     * This function takes in the id for a grocery shop record
     * and returns the string of item ids that were bought
     * during that shop
     *
     * @param id unique id of record
     * @return a list of grocery items bought on this occasion
     */
    @SuppressLint("Range")
    public List<Data_Grocery> selectGroceries(int id){
        List<Data_Grocery> groceries_list = new ArrayList<Data_Grocery>();
        String str_item_IDs = new String(",");

        String queryString = "SELECT * FROM " + GROCERIES_TRANSACTIONS_TABLE + " WHERE " + COLUMN_GROCERIES_ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor is the result set from a SQL statement
        Cursor cursor = db.rawQuery(queryString, null);

        // True if items were selected
        if(cursor.moveToFirst()){
            str_item_IDs = cursor.getString(cursor.getColumnIndex(COLUMN_GROCERIES_ITEMS));
        }
        else{
            // failure, do not add anything to the list
        }

        // get all item ids in a array list of type string and then convert them into integers
        List<String> str_list_item_IDS = Arrays.asList(str_item_IDs.split(","));
        List<Integer> int_list_item_IDS = new ArrayList<Integer>();
        for(String str_item_id : str_list_item_IDS){
            int_list_item_IDS.add(Integer.parseInt(str_item_id));
        }

        // then loop through each id to create the groceries list of grocery names
        for(int int_item_id: int_list_item_IDS){
            Data_Grocery grocery = selectGrocery(int_item_id);
            groceries_list.add(grocery);
        }

        // close both the cursor and the db when done
        cursor.close();
        db.close();

        return groceries_list;
    }
    // endregion

    // region Groceries Table Methods

    /**
     * This function takes in the id for a grocery record
     * and returns the name of the corresponding item
     *
     * @param id unique id of record
     * @return the name of the grocery item
     */
    @SuppressLint("Range")
    public Data_Grocery selectGrocery(int id){
        Data_Grocery grocery;

        String queryString = "SELECT * FROM " + GROCERIES_TABLE + " WHERE " + COLUMN_GROCERY_ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();

        // Cursor is the result set from a SQL statement
        Cursor cursor = db.rawQuery(queryString, null);

        // True if items were selected
        if(cursor.moveToFirst()){

            int grocery_id = cursor.getInt(cursor.getColumnIndex(COLUMN_GROCERY_ID));
            String grocery_name = cursor.getString(cursor.getColumnIndex(COLUMN_GROCERY_NAME));
            float grocery_price = cursor.getFloat(cursor.getColumnIndex(COLUMN_GROCERY_PRICE));

            grocery = new Data_Grocery(grocery_id,
                    grocery_name,
                    grocery_price);
        }
        else{
            // failure, do not add anything to the list
            grocery = null;
        }

        // close both the cursor and the db when done
        cursor.close();
        db.close();

        return grocery;
    }

    // endregion
}
