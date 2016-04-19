package mobile08723.team10.hiroomi;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {

    private Uri outputFileUri;
    EditText address;
    EditText title;
    EditText description;
    EditText tenants;
    EditText fromYear;
    EditText fromMonth;
    EditText fromDay;
    EditText toYear;
    EditText toMonth;
    EditText toDay;
    EditText price;

    Button addPhotos;
    Button upload;

    static Button startdate;
    static Button enddate;

    static int FromYear, FromMonth, FromDay, ToYear, ToMonth, ToDay;

    private ImageView imageView;

    ParseUser user;
    String username;

    ParseFile file;
    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_upload, container, false);

        //View rootView =  inflater.inflate(R.layout.fragment_upload, container, false);
        address = (EditText)rootView.findViewById(R.id.address);
        title = (EditText)rootView.findViewById(R.id.title);
        description = (EditText)rootView.findViewById(R.id.description);
        tenants = (EditText)rootView.findViewById(R.id.tenants);
//        fromYear = (EditText)rootView.findViewById(R.id.startYear);
//        fromMonth = (EditText)rootView.findViewById(R.id.startMonth);
//        fromDay = (EditText)rootView.findViewById(R.id.startDay);
//        toYear = (EditText)rootView.findViewById(R.id.endYear);
//        toMonth = (EditText)rootView.findViewById(R.id.endMonth);
//        toDay = (EditText)rootView.findViewById(R.id.endDay);
        price = (EditText)rootView.findViewById(R.id.price);

        addPhotos = (Button) rootView.findViewById(R.id.addPhotoButton);
        upload = (Button) rootView.findViewById(R.id.upload_apartment);

        user = ParseUser.getCurrentUser();

        startdate = (Button)rootView.findViewById(R.id.startdate);
        enddate = (Button)rootView.findViewById(R.id.enddate);

        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        rootView.findViewById(R.id.upload_apartment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HiRoomi", "Clicked");
                Log.d("HiRoomi", address.getText().toString());
                ParseObject postInfo = new ParseObject("PostInfo");
                //postInfo.put("UserName", "poi");
                postInfo.put("UserName", user.getUsername());
                postInfo.put("Address", address.getText().toString());
                postInfo.put("FromYear", FromYear);
                postInfo.put("FromMonth", FromMonth);
                postInfo.put("FromDay", FromDay);
                postInfo.put("ToYear", ToYear);
                postInfo.put("ToMonth", ToMonth);
                postInfo.put("ToDay", ToDay);

                postInfo.put("Title", title.getText().toString());
                postInfo.put("description", description.getText().toString());
                postInfo.put("NeedTenant", Integer.parseInt(tenants.getText().toString()));
                postInfo.put("Price", Integer.parseInt(price.getText().toString()));

                postInfo.put("Image", file);


                postInfo.saveInBackground();
                Toast.makeText(UploadFragment.this.getActivity(), "Rent Info Uploaded",
                        Toast.LENGTH_SHORT).show();
//                ParseObject postInfo = new ParseObject("PostInfo");
//                postInfo.put("UserName", "quriola");
//                postInfo.put("FromYear", 2015);
//                postInfo.put("FromMonth", 9);
//                postInfo.put("FromDay", 1);
//                postInfo.put("ToYear", 2016);
//                postInfo.put("ToMonth", 9);
//                postInfo.put("ToDay", 1);
//                postInfo.put("NeedTenant", 1);
//                postInfo.put("Longitude", 40.4778941);
//                postInfo.put("Latitude", -79.9041036);
//                postInfo.put("Price", 700);
//                postInfo.put("Address", "5030 Centre Ave");
//                postInfo.put("Title", "CMU Mobile Apartment");
//                postInfo.saveInBackground();

            }
        });

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(UploadFragment.this.getActivity().getFragmentManager(), "datePicker");

            }
        });
        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment1();
                newFragment.show(UploadFragment.this.getActivity().getFragmentManager(), "datePicker");

            }
        });
        addPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Determine Uri of camera image to save.
                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
                root.mkdirs();
                //final String fname = Utils.getUniqueImageFilename();
                final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
                final File sdImageMainDirectory = new File(root, fname);
                outputFileUri = Uri.fromFile(sdImageMainDirectory);

                // Camera.
                final List<Intent> cameraIntents = new ArrayList<Intent>();
                final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                final PackageManager packageManager = UploadFragment.this.getActivity().getPackageManager();
                final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
                for(ResolveInfo res : listCam) {
                    final String packageName = res.activityInfo.packageName;
                    final Intent intent = new Intent(captureIntent);
                    intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    intent.setPackage(packageName);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    cameraIntents.add(intent);
                }

                // Filesystem.
                final Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // Chooser of filesystem options.
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

                // Add the camera options.
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

                startActivityForResult(chooserIntent, 0);

            }
        });
        return rootView ;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == UploadFragment.this.getActivity().RESULT_OK) {
            if (requestCode == 0) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }
                // emailTextView.setText(selectedImageUri.toString());

                //imageView.setImageBitmap(BitmapFactory.decodeFile(selectedImageUri.toString()));
                try {
                    imageView.setImageBitmap(getBitmapFromUri(selectedImageUri));
                    Bitmap bitmap = getBitmapFromUri(selectedImageUri);
                    // Convert it to byte
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();

                    // Create the ParseFile
                    file = new ParseFile("androidbegin.png", image);
                    // Upload the image into Parse Cloud
                    file.saveInBackground();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                this.getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(this.getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            startdate.setText(String.format("%d/%d/%d", month+1, day,  year));
            FromYear = year;
            FromMonth = month+1;
            FromDay = day;
        }
    }
    public static class DatePickerFragment1 extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(this.getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            enddate.setText(String.format("%d/%d/%d",  month+1, day,year));
            ToYear = year;
            ToMonth = month+1;
            ToDay = day;
        }
    }

}

