package com.example.budgetingappv1;

import static android.graphics.Color.WHITE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;

public class Add_Transaction extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button btn_save, btn_cancel, btn_upload_icon;
    private EditText transaction_title, transaction_recipient, transaction_amount, transaction_date, transaction_groceries;
    private AutoCompleteTextView transaction_category;
    private SwitchCompat transaction_recurring;
    private DatabaseHelper databaseHelper;
    private SimpleDateFormat simpleDateFormat;
    private DateFormat dateParser;
    private String input_transaction_date;
    private int id;
    private Uri imageUri;
    private final ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), this::onCropImageResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // for formatting and parsing date to work with database
        simpleDateFormat = new SimpleDateFormat("MMM/dd/yyyy");
        dateParser = new SimpleDateFormat("yyyy/MM/dd");

        databaseHelper = new DatabaseHelper(Add_Transaction.this);

        // region get references to objects in activity
        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_upload_icon = findViewById(R.id.btn_add_groceries);

        transaction_title = findViewById(R.id.transaction_title);
        transaction_recipient = findViewById(R.id.transaction_recipient);
        transaction_amount = findViewById(R.id.transaction_amount);
        transaction_recurring = findViewById(R.id.transaction_recurring);
        transaction_category = findViewById(R.id.transaction_category);
        transaction_date = findViewById(R.id.transaction_date);
        transaction_date.setText(simpleDateFormat.format(Calendar.getInstance().getTime()));
        transaction_groceries = findViewById(R.id.transaction_groceries);
        // endregion

        // check if an existing transaction is being accessed
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        Data_Transaction transaction;

        // if transaction already exists, then fill in fields
        if(id >= 0){

            transaction = databaseHelper.selectTransaction(id);
            if(transaction != null){
                transaction_title.setText(transaction.getTitle());
                transaction_recipient.setText(transaction.getRecipient());
                transaction_amount.setText(String.valueOf(transaction.getAmount()));
                transaction_date.setText(transaction.getDate());
                transaction_recurring.setChecked(transaction.isRecurring());
                input_transaction_date = transaction.getDate();
                String cat_name = transaction.getCategory();
                if(databaseHelper.getCategoryNames().contains(cat_name)) {
                    transaction_category.setText(transaction.getCategory());
                }
                else{
                    transaction_category.setText("Category");
                }
                initDatePicker(input_transaction_date);
            }
        }
        else{
            initDatePicker(null);
        }

        // create a dropdown menu of categories
        List<String> categories = databaseHelper.getCategoryNames();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Add_Transaction.this, R.layout.dropdown_item, categories);
        transaction_category.setAdapter(arrayAdapter);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Data_Transaction transaction;
                boolean success;

                // try making a transaction with input data
                try {
                    int groceries_id = -1;
                    if(transaction_category.getText().toString().equals("Groceries")) {
                        // get groceries id
                    }

                    transaction = new Data_Transaction(-1
                            , groceries_id
                            , transaction_title.getText().toString()
                            , transaction_recipient.getText().toString()
                            , Float.parseFloat(String.format(Locale.ROOT,"%.2f",Float.parseFloat(transaction_amount.getText().toString())))
                            , input_transaction_date,
                            transaction_category.getText().toString(),
                            transaction_recurring.isChecked());

                    if(transaction.getTitle().isEmpty()){
                        throw new MissingResourceException("Missing text input", "Title", "transaction.getText()");
                    }
                    else if(transaction.getRecipient().isEmpty()){
                        throw new MissingResourceException("Missing text input", "Recipient", "transaction.getRecipient()");
                    }
                    else if(transaction.getDate().isEmpty()){
                        throw new MissingResourceException("Missing text input", "Date", "transaction.getDate()");
                    }
                    else if(transaction.getCategory().equalsIgnoreCase("Category")){
                        throw new MissingResourceException("Missing category selection", "Category", "transaction.getCategory()");
                    }
                }
                catch(NumberFormatException | MissingResourceException e){
                    transaction = null;
                    Toast.makeText(Add_Transaction.this, "Entry fields are missing data", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    transaction = null;
                    Toast.makeText(Add_Transaction.this, "Something went wrong (" + e.getMessage() + ")", Toast.LENGTH_SHORT).show();
                }

                DatabaseHelper databaseHelper = new DatabaseHelper(Add_Transaction.this);

                // check that no error occurred in gathering data
                if(transaction != null) {
                    if (id >= 0) {
                        success = databaseHelper.updateOne(transaction, id);
                    } else {
                        success = databaseHelper.addOne(transaction);
                    }

                    // if adding or updating transaction was successful
                    if(success){
                        if(id >= 0) {
                            Toast.makeText(Add_Transaction.this, "Transaction Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Add_Transaction.this, "Transaction Added Successfully", Toast.LENGTH_SHORT).show();
                        }

                        // open main transaction activity
                        Intent intent = new Intent(Add_Transaction.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Add_Transaction.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_upload_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri = null;
                startCameraWithoutUri();
            }
        });

        transaction_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view);
            }
        });
    }

    // region Grocery List Screen Capture to text methods

    /**
     *  This function calls the crop image launch method which will display
     *  the choice of camera and gallery and the ability to crop images.
     */
    public void startCameraWithoutUri() {
        CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions())
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setMaxZoom(4)
                .setAutoZoomEnabled(true)
                .setMultiTouchEnabled(true)
                .setCenterMoveEnabled(true)
                .setShowCropOverlay(true)
                .setAllowFlipping(true)
                .setSnapRadius(3f)
                .setTouchRadius(48f)
                .setInitialCropWindowPaddingRatio(0.1f)
                .setBorderLineThickness(3f)
                .setBorderLineColor(Color.argb(170, 255, 255, 255))
                .setBorderCornerThickness(2f)
                .setBorderCornerOffset(5f)
                .setBorderCornerLength(14f)
                .setBorderCornerColor(WHITE)
                .setGuidelinesThickness(1f)
                .setGuidelinesColor(R.color.white)
                .setBackgroundColor(Color.argb(119, 0, 0, 0))
                .setMinCropWindowSize(24, 24)
                .setMinCropResultSize(20, 20)
                .setMaxCropResultSize(99999, 99999)
                .setActivityTitle("")
                .setActivityMenuIconColor(0)
                .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                .setOutputCompressQuality(90)
                .setAllowCounterRotation(false)
                .setFlipHorizontally(false)
                .setFlipVertically(false)
                .setCropMenuCropButtonTitle(null)
                .setCropMenuCropButtonIcon(0)
                .setAllowRotation(true)
                .setNoOutputImage(false)
                .setFixAspectRatio(false);
        cropImage.launch(options);
    }

    /**
     * This function takes in a string and displays it on the screen to tell the user
     * what went wrong with their action, if an error was to occur
     *
     * @param message a message which is displayed to show what went wrong
     */
    public void showErrorMessage(@NotNull String message) {
        Log.e("Camera Error:", message);
        Toast.makeText(Add_Transaction.this, "Crop failed: " + message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This function takes the uri of the image the user has selected and
     * displays it on the imageview on the page.
     *
     * @param uri the uri of the image the user has cropped
     */
    public void handleCropImageResult(@NotNull String uri){
        imageUri = Uri.parse(uri);
        try {
            if (imageUri != null) {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                byte[] iconBytes = Add_Category_Icon_Utils.getBytes(inputStream);
                Bitmap imageBitmap = Add_Category_Icon_Utils.getImage(iconBytes);
                getTextFromImage(imageBitmap);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function determines if the crop was successful with the argument which is passed in
     * and either calls a message to display it on the screen or display an error message to alert
     * the user that something went wrong.
     *
     * @param result this contains information on the success of the cropping action
     */
    public void onCropImageResult(@NonNull CropImageView.CropResult result) {
        if (result.isSuccessful()) {
            handleCropImageResult(Objects.requireNonNull(result.getUriContent())
                    .toString()
                    .replace("file:", ""));
        } else if (result.equals(CropImage.CancelledResult.INSTANCE)) {
            showErrorMessage("cropping image was cancelled by the user");
        } else {
            showErrorMessage("cropping image failed");
        }
    }

    /**
     * This function uses the bitmap of an image and uses a library which finds text
     * in the image and outputs it as a TextBlock array. This is then converted into
     * a string which is then displayed on screen in a text box
     *
     * @param bitmap bitmap of image which contains text
     */
    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(Add_Transaction.this).build();

        if(!textRecognizer.isOperational()){
            Toast.makeText(Add_Transaction.this, "Error Occurred!!!", Toast.LENGTH_SHORT).show();
        }
        else{
            // get a frame of the bitmap and then use textRecognizer to grab text from the frame
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();

            // add each block of text on the image into one string to be displayed
            for(int i = 0; i < textBlockSparseArray.size(); i++){
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                if(i % 2 == 1) {
                    stringBuilder.append("\n");
                }else {
                    stringBuilder.append(" : ");
                }
            }

            transaction_groceries.setText(stringBuilder.toString());
            getGroceriesAsListFromString(stringBuilder.toString());
        }
    }

    /**
     * This function takes in a string of groceries and outputs them as a list of data_grocery objects
     *
     * @param groceries the list of groceries and their prices as a string in the correct format
     * @return a list of data_grocery objects containing information of all the different groceries which have been entered
     */
    public List<Data_Grocery> getGroceriesAsListFromString(String groceries){

        List<Data_Grocery> groceries_list = new ArrayList<Data_Grocery>();
        String[] grocery_items = groceries.split(" : |\\n");

        for(int j = 0; j < grocery_items.length; j += 2){
            // try to read grocery price from string
            float grocery_price = -1;
            try {
                // replace all non alphanumeric characters in string with empty string to get rid of currency symbols etc..
                grocery_price = Float.parseFloat(grocery_items[j + 1].replaceAll("[^\\d.]", ""));
            }
            catch (Exception e){
                // something went wrong, display error on screen (for debugging)
                Toast.makeText(Add_Transaction.this, grocery_items[j+1], Toast.LENGTH_SHORT).show();
            }
            Data_Grocery grocery = new Data_Grocery(-1, grocery_items[j], grocery_price);
            groceries_list.add(grocery);
        }

//        check that groceries are being created properly (for debugging)
//        for(Data_Grocery grocery: groceries_list){
//            Toast.makeText(Add_Transaction.this, grocery.getName() + " " + String.valueOf(grocery.getPrice()), Toast.LENGTH_SHORT).show();
//        }

        return groceries_list;
    }
    // endregion

    //region Date picker dialog methods

    /**
     * This function takes in a date text and uses to initialise a date picker
     * with the correct starting values, if null is passed in, then the default
     * values are set to the current date.
     *
     * @param date_text a string of the date of transaction when updating a transaction otherwise null
     */
    private void initDatePicker(String date_text) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                // get date in a string that can be parsed
                String date = makeDateString(day, month, year);
                input_transaction_date = date;
                try {
                    // parse data and format it as necessary
                    transaction_date.setText(simpleDateFormat.format(dateParser.parse(date)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        if(date_text != null){
            try {
                int year = Integer.parseInt(date_text.substring(0, 4));
                int month = Integer.parseInt(date_text.substring(5, 7));
                int day = Integer.parseInt(date_text.substring(8));

                // parse date and set text on the activity
                transaction_date.setText(simpleDateFormat.format(dateParser.parse(date_text)));
                datePickerDialog = new DatePickerDialog(Add_Transaction.this, style, dateSetListener, year, month - 1, day);
                // this code is to make it so you cannot add any date in the future
                // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
            catch(Exception e){
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // get the current date and output on screen
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            input_transaction_date = makeDateString(day, month + 1, year);

            datePickerDialog = new DatePickerDialog(Add_Transaction.this, style, dateSetListener, year, month, day);
            // this code is to make it so you cannot add any date in the future
            // datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

    }

    /**This function creates a date string formatted correctly
     * from using the data that is passed in.
     *
     * @param day day of transaction
     * @param month month of transaction
     * @param year year of transaction
     * @return returns a string of the date formated as dd/MM/yyyy
     */
    private String makeDateString(int day, int month, int year) {
        // create a string with the necessary format
        String month_string = String.valueOf(month);
        String day_string = String.valueOf(day);
        if(month < 10){
            month_string = "0" + month;
        }
        if(day < 10){
            day_string = "0" + day;
        }

        return year + "/" + month_string + "/" + day_string;
    }

    /**This function opens the date picker dialog
     *
     * @param view context of activity
     */
    public void openDatePicker(View view){
        datePickerDialog.show();
    }
    //endregion
}