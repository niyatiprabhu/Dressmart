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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dressmart.databinding.ActivitySignupBinding;
import com.example.dressmart.models.parse.User;
import com.example.dressmart.util.UserUtil;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Signup Activity";

    private ActivitySignupBinding binding;

    private ParseFile photoFile;

    private static final int TAKE_PHOTO = 0;
    private static final int CHOOSE_FROM_GALLERY = 1;



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
                try {
                    signupUser(username, password, displayName);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
                    startActivityForResult(takePicture, TAKE_PHOTO);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , CHOOSE_FROM_GALLERY);

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
                case TAKE_PHOTO:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                        byte[] imageByte = byteArrayOutputStream.toByteArray();
                        photoFile = new ParseFile("image_file.png",imageByte);
                        binding.ivProfilePicSignup.setImageBitmap(selectedImage);
                    }

                    break;
                case CHOOSE_FROM_GALLERY:
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

    private void signupUser(String username, String password, String displayName) throws ParseException {
        // Create the ParseUser
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setParseDisplayName(displayName);
        user.setParseOutfits(new ArrayList<>());
        user.setParseCloset(new ArrayList<>());
        if (photoFile != null) {
            user.setParseProfilePicture(photoFile);
            photoFile.save();
        }
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    UserUtil.loginUser(username,password, SignupActivity.this);
                } else {
                    // Sign up didn't succeed.
                    Log.e(TAG, e.getMessage());
                    Animation shake = AnimationUtils.loadAnimation(SignupActivity.this, R.anim.shake);
                    if (binding.etDisplayNameSignup.getText().toString().equals("") || binding.etUsernameSignup.getText().toString().equals("")
                            || binding.etPasswordSignup.getText().toString().equals("")) {
                        if (binding.etDisplayNameSignup.getText().toString().equals("")) {
                            binding.etDisplayNameSignup.startAnimation(shake);
                        }
                        if (binding.etUsernameSignup.getText().toString().equals("")) {
                            binding.etUsernameSignup.startAnimation(shake);

                        }
                        if (binding.etPasswordSignup.getText().toString().equals("")) {
                            binding.etPasswordSignup.startAnimation(shake);

                        }
                        Snackbar.make(findViewById(R.id.clSignupView), getString(R.string.message_empty_fields), BaseTransientBottomBar.LENGTH_LONG).show();
                    } else {
                        binding.etUsernameSignup.startAnimation(shake);
                        binding.etUsernameSignup.getText().clear();
                        Snackbar.make(findViewById(R.id.clSignupView), getString(R.string.message_user_exists), BaseTransientBottomBar.LENGTH_LONG).show();
                    }



                }
            }
        });




    }


}