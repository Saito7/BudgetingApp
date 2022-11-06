package com.example.budgetingappv1;

import static android.graphics.Color.WHITE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.nvt.color.ColorPickerDialog;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Objects;

public class Add_Category extends AppCompatActivity {

    private Button btn_save, btn_cancel, btn_clr_picker, btn_upload_icon;
    private ImageView iv_icon_display, iv_color_display;
    private EditText category_name, category_budget;
    private SwitchCompat category_essential;
    private DatabaseHelper databaseHelper;
    private Uri imageUri;
    private String name;
    private final ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), this::onCropImageResult);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // region get references to objects in activity
        btn_save = findViewById(R.id.btn_save);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_clr_picker = findViewById(R.id.btn_color_picker);
        btn_upload_icon = findViewById(R.id.btn_upload_icon);

        iv_color_display = findViewById(R.id.iv_color_display);
        iv_icon_display = findViewById(R.id.iv_icon_display);

        category_name = findViewById(R.id.category_name);
        category_budget = findViewById(R.id.category_budget);
        category_essential = findViewById(R.id.category_essential);
        // endregion

        databaseHelper = new DatabaseHelper(Add_Category.this);

        // check if an existing category is being accessed
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        Data_Category category;

        // if category already exists, then fill in fields
        if(name != null){
            category = databaseHelper.selectCategory(name);
            if(category != null){
                category_name.setText(category.getName());
                category_budget.setText(String.valueOf(category.getBudget()));
                category_essential.setChecked(category.isEssential());
                iv_color_display.setColorFilter(category.getColor());
                iv_color_display.setTag(Color.valueOf(category.getColor()));
                iv_icon_display.setImageBitmap(Add_Category_Icon_Utils.getImage(category.getIconBytes()));
            }
        }

        btn_clr_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on a button click, open color picker dialog
                ColorPickerDialog colorPicker = new ColorPickerDialog(
                        Add_Category.this,
                        Color.BLACK, // color init
                        true, // true is show alpha
                        new ColorPickerDialog.OnColorPickerListener() {
                            @Override
                            public void onCancel(ColorPickerDialog dialog) {

                            }

                            @Override
                            public void onOk(ColorPickerDialog dialog, int color) {
                                iv_color_display.setColorFilter(color);
                                iv_color_display.setTag(Color.valueOf(color));
                            }
                        });
                colorPicker.show();
            }
        });

        btn_upload_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri = null;
                startCameraWithUri();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                Data_Category category;
                boolean success;

                // try making a category with input data
                try {
                    Color color = (Color) iv_color_display.getTag();
                    byte[] iconBytes = null;
                    if(imageUri != null) {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        iconBytes = Add_Category_Icon_Utils.getBytes(inputStream);
                    }
                    else{
                        throw new MissingResourceException("Missing image input", "Image", "ImageURI");
                    }
                    category = new Data_Category(category_name.getText().toString(), Float.parseFloat(category_budget.getText().toString()), color.toArgb(), iconBytes, category_essential.isChecked());

                    if(category.getName().isEmpty()){
                        throw new MissingResourceException("Missing text input", "Name", "category_name.getText()");
                    }
                }
                catch(MissingResourceException e){
                    category = null;
                    Toast.makeText(Add_Category.this, "Entry fields are missing data", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    category = null;
                    Toast.makeText(Add_Category.this, "Something went wrong (" + e.getClass().getSimpleName() + ")", Toast.LENGTH_SHORT).show();
                }

                // check that no error occurred in gathering data
                if(category != null) {
                    if (name != null) {
                        success = databaseHelper.updateOne(category, -1);
                    } else {
                        success = databaseHelper.addOne(category);
                    }

                    // if adding or updating category was successful
                    if(success){
                        if(name != null) {
                            Toast.makeText(Add_Category.this, "Category Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Add_Category.this, "Category Added Successfully", Toast.LENGTH_SHORT).show();
                        }

                        // open main category activity
                        Intent intent = new Intent(Add_Category.this, MainCategory.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Add_Category.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
    }

    // region Icon picker methods

    /**
     *  This function calls the crop image launch method which will display
     *  the choice of camera and gallery and the ability to crop images.
     */
    public void startCameraWithUri() {
        CropImageContractOptions options = new CropImageContractOptions(imageUri, new CropImageOptions())
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setAspectRatio(1, 1)
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
                .setFixAspectRatio(true);
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
        Toast.makeText(Add_Category.this, "Crop failed: " + message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This function takes the uri of the image the user has selected and
     * displays it on the imageview on the page.
     *
     * @param uri the uri of the image the user has cropped
     */
    public void handleCropImageResult(@NotNull String uri) {
        imageUri = Uri.parse(uri);
        iv_icon_display.setImageURI(imageUri);
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
    // endregion
}