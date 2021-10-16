package com.example.todolist2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.todolist2.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import models.Mission;

public class PopUp_contact extends Activity {

    private static final int WRITE_CONTACT_PERMISSION_CODE =100;
    private static final int IMAGE_PICK_GALLERY_CODE =200;
    private String[] contactPermission;
    private Uri image_uri; //URI is a uniform resource identifier
    public static ArrayList<String> mystrings= new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_popup);

        //our floating  display
        DisplayMetrics display= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        int width=display.widthPixels;
        int height=display.heightPixels;
        getWindow().setLayout((int) (width*.8),(int) (height*.8));


        contactPermission=new String[]{Manifest.permission.WRITE_CONTACTS};


        Mission mission = (Mission) getIntent().getSerializableExtra("mission");

        EditText Name = ((EditText)(findViewById(R.id.name_popupWindow)));
        EditText LastName = ((EditText)(findViewById(R.id.lastName_popupWindow)));
        EditText phone = ((EditText)(findViewById(R.id.phone_popupWindow)));
        EditText email = ((EditText) findViewById(R.id.email_popupWindow));



        //setting the mail and the phone from the mission as option
        phone.setText(mission.getPhone().toString());
        email.setText(mission.getemail().toString());

        // pick photo from gallery
        findViewById(R.id.pic_contact).setOnClickListener(pic->{

            Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
        });

        // add and save contact in phone book app
        findViewById(R.id.btn_popupWindow).setOnClickListener(btnSave->{
            mystrings.add(Name.getText().toString());
            mystrings.add(LastName.getText().toString());
            mystrings.add(phone.getText().toString());
            mystrings.add(email.getText().toString());
            mystrings.add(mission.getAddress());
            if(isWriteContactPermissionEnabled()){
                saveContact();
            }
            else {
                requestWriteContactPermission();
            }
        });
    }




    private void saveContact() {
        // prepare a lot of "Operation"
        ArrayList<ContentProviderOperation> cpo = new ArrayList<>();
        int cpoArrSize = cpo.size();



        cpo.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());


        //first and Last name
        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, cpoArrSize)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,mystrings.get(0))
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, mystrings.get(1)).build());

        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, cpoArrSize)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mystrings.get(2))
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());

        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, cpoArrSize)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, mystrings.get(3))
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE,ContactsContract.CommonDataKinds.Email.TYPE_HOME).build());


        cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, cpoArrSize)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.SipAddress.DATA,mystrings.get(4))
                .withValue(ContactsContract.CommonDataKinds.SipAddress.TYPE,ContactsContract.CommonDataKinds.SipAddress.TYPE_WORK).build());


        //get image convert image to bytes to store in contact
        byte[] imageBytes= imageURIToBytes();
        if (imageBytes!=null){
            //add image
            cpo.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, cpoArrSize)
                    .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,imageBytes).build());
        }


        try{
            ContentProviderResult[] results= getContentResolver().applyBatch(ContactsContract.AUTHORITY,cpo);
            Toast.makeText(this,"Contact added successfully!",Toast.LENGTH_LONG).show();
            mystrings.removeAll(mystrings);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"savecontact:"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }

    private byte[] imageURIToBytes() {
        Bitmap bitmap;
        ByteArrayOutputStream baos= null;
        try {
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),image_uri);
            baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos);
            return baos.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isWriteContactPermissionEnabled(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // request Permission
    private void requestWriteContactPermission(){
        ActivityCompat.requestPermissions(this,contactPermission,WRITE_CONTACT_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0){
            if (requestCode==WRITE_CONTACT_PERMISSION_CODE){
                boolean haveWriteContactPermission=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(haveWriteContactPermission){
                    saveContact();
                }
                else {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();

                }

            }





        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode==RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                image_uri=data.getData();
                if(image_uri != null)
                    ((ImageView)findViewById(R.id.pic_contact)).setImageURI(image_uri);
            }
        }
        else {
            Toast.makeText(this,"not working",Toast.LENGTH_SHORT).show();

        }

    }








}
