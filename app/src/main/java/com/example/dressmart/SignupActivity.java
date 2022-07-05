package com.example.dressmart;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.example.dressmart.databinding.ActivitySignupBinding;
import com.example.dressmart.models.parse.User;
import com.example.dressmart.util.UserUtil;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SignUpCallback;

import java.io.File;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Signup Activity";

    private ActivitySignupBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.btnDoneSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsernameSignup.getText().toString();
                String password = binding.etPasswordSignup.getText().toString();
                String displayName = binding.etDisplayNameSignup.getText().toString();
                // TO DO: assigning user's profile picture
                // extract the file from a the image bitmap and assign it to user.
                // use this same concept for the outfit post button
                signupUser(username, password, displayName);
            }
        });

        binding.ibUploadProfilePicSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(SignupActivity.this);
            }
        });

    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        binding.ivProfilePicSignup.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                binding.ivProfilePicSignup.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    private void signupUser(String username, String password, String displayName) {
        // Create the ParseUser
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setParseDisplayName(displayName);
        user.setParseOutfits(new ArrayList<>());
        user.setParseCloset(new ArrayList<>());
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    UserUtil.loginUser(username,password, SignupActivity.this);
                } else {
                    // Sign up didn't succeed.
                }
            }
        });
    }


}