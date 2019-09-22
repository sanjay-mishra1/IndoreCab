package com.example.sanjay.cab.userdetails;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.cab.R;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;

public class PersonalDetailsActivity extends AppCompatActivity {
    private   final int OPEN_CAMERA = 102;
    private   final int CHOOSE_IMAGE = 101;
    BottomSheetBehavior bottomSheetBehavior;
    TextInputEditText editText;
    TextView textView;
    TextView errorTextView;
    private DatabaseReference databaseReference;
    String loc;
    private SharedPreferences sharedPreferences;
    private String mobile;
    private ImageView imageView;
    private Uri uriProfileImage;
    private TextView textname;
    private TextView textmobile;
    private TextView textemail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        CardView linearLayout=findViewById(R.id.bottom_sheet);
        editText=findViewById(R.id.enterDetailEdit);
        textView=findViewById(R.id.inputDetail);
        errorTextView=findViewById(R.id.errorText);
        bottomSheetBehavior=BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setHideable(true);
         setUserDetail();
    }
    private void setUserDetail(){
          sharedPreferences=getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);
          imageView=findViewById(R.id.userImage);
        String img=sharedPreferences.getString("USERIMG","");
        if (!img.isEmpty())
          Glide.with(this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(img).into(imageView);
          textname=findViewById(R.id.userStartName);
        textname.setText(sharedPreferences.getString("USERNAME",""));
          textmobile=findViewById(R.id.userMobile);
          mobile=sharedPreferences.getString("MOBILE","");
        textmobile.append(mobile);
        databaseReference=FirebaseDatabase.getInstance().getReference("users/"+mobile);
          textemail=findViewById(R.id.userEmail);
        if (sharedPreferences.getString("EMAIL","Enter email id").isEmpty())
        textemail.setText("Enter email id");
        else textemail.setText(sharedPreferences.getString("EMAIL",""));
    }

    public void backOnClicked(View view) {
        finish();
    }

    public void changeProfileImageClicked(View view) {
        changeUserImage();     }

    public void EditnameClicked(View view) {
        errorTextView.setVisibility(View.GONE);
        textView.setText("Enter your first name");
        editText.setHint("First Name");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        loc="USERNAME";
    }

    public void EditLastnameClicked(View view) {
        editText.setText("");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        errorTextView.setVisibility(View.GONE);
        textView.setText("Enter your last name");
        editText.setHint("Last Name");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        loc="USERNAME";
    }

    public void EditMobileClicked(View view) {
        editText.setText("");
        errorTextView.setVisibility(View.GONE);
        textView.setText("Enter your mobile number");
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        editText.setHint("Mobile Number");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        loc="MOBILE";
    }

    public void EditEmailClicked(View view) {
        editText.setText("");
        errorTextView.setVisibility(View.GONE);
        textView.setText("Enter your email address");
        editText.setHint("Email Address");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        loc="EMAIL";
    }

    public void saveClicked(View view) {
        if (checkText()) {
            databaseReference.child(loc).setValue(editText.getText().toString());
            SharedPreferences.Editor edit=sharedPreferences.edit();
            edit.putString(loc,editText.getText().toString());
            edit.apply();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }
    private boolean checkText(){
        String data=editText.getText().toString();
        boolean result=true;

          switch (loc) {
            case "MOBILE":
                if (data.length() < 10) {
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Enter valid mobile no");
                    result = false;
                }
                if (result)
                textmobile.setText("+91 "+data);
                break;
            case "EMAIL":
                if (!Patterns.EMAIL_ADDRESS.matcher(data).matches())
                {   errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Enter a valid emailid");
                    result = false;
                }
                if (result)
                textmobile.setText(data);
                break;
            case "USERNAME":
                  if (data.isEmpty())
                  { errorTextView.setVisibility(View.VISIBLE);
                      errorTextView.setText("Enter your name");
                      result = false;
                  }
                  if (result)
                  textname.setText(data);
                  break;

        }
        return  result;
    }
     @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else
        super.onBackPressed();
    }

    private void changeUserImage() {
        start_image_dialog();
    }


    void start_image_dialog( ){


        View dialogView = View.inflate( getApplicationContext(), R.layout.settings_img_more_options, null);
        final Dialog dialog = new Dialog(this,R.style.Dialog1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);


        dialog.setContentView(dialogView);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }

                return false;
            }
        });
        dialogView.findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPermission(PersonalDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE,CHOOSE_IMAGE);
            }
        });
        dialogView.findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPermission(  PersonalDetailsActivity.this, Manifest.permission.CAMERA,OPEN_CAMERA);
            }
        });
        dialogView.findViewById(R.id.three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


                if(!mobile.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference("users/"+mobile+"/USERIMAGE")
                            .setValue("");
                    Glide.with(Objects.requireNonNull(getApplicationContext())).applyDefaultRequestOptions(RequestOptions.noTransformation()).load("").into(imageView);

                }
            }
        });


        dialog.show();
    }
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select profile image"), CHOOSE_IMAGE);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission(Activity context, String permission, int value)  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    value);
        } else {
            if (value==102)
            {Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, OPEN_CAMERA);

            }
            else  showImageChooser();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CHOOSE_IMAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImageChooser();


                }
                break;
            }
            case OPEN_CAMERA:
                 if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, OPEN_CAMERA);



                }
                break;
        }
    }



    private void uploadImageToFirebaseStorage(final Bitmap bitmap) {
         final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("users/" +mobile + ".jpg");

        final UploadTask uploadTask = profileImageRef.putFile(uriProfileImage);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                    throw Objects.requireNonNull(task.getException());

                return profileImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    try {
                         saveuserinformation(String.valueOf(task.getResult()),bitmap);
                     }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{/* */
                }
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                int   currentprogress=(int) progress;

             }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode == CHOOSE_IMAGE &&
        if ( resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                Glide.with(this).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).asBitmap().load(bitmap).into(imageView);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveuserinformation(String url, Bitmap bitmap) {
        if (!mobile.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("users/"+mobile).child("USERIMAGE").setValue(url);

             SharedPreferences.Editor edit=sharedPreferences.edit();
            edit.putString("USERIMG",url);

            edit.apply();

        }
    }






}
