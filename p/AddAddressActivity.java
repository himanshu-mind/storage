package com.zxc.demos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import apis.ApiClient;
import at.grabner.circleprogress.CircleProgressView;
import constant.ApiConstant;
import constant.SharedPreferenceConstant;
import dto.GetAddressDto;
import interfaces.ApiInterface;
import interfaces.ApiTask;
import retrofit2.Call;
import retrofit2.Response;
import utilities.ProgressDialogUtils;
import utilities.SharedPreferenceUtils;
import utilities.StringUtils;
import utilities.ToastUtils;

public class AddAddressActivity extends AppCompatActivity {
    private static final String TAG = "AddAddressActivity";

    TextView title, number, number1;
    ImageView back_img, search;
    int layout;
    Button button;
    RadioGroup pretitleRG, addresstypeRG;
    CircleProgressView circleProgressView;
    String prename, addresstype;
    EditText nameET, flatET, localityET, addressET;
    Context context;
    ImageView getlocationIV;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi mFusedLocationProviderApi = LocationServices.FusedLocationApi;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationRequest locationRequest;
    LinearLayout detectlocLAY;
    GetAddressDto dto = new GetAddressDto();

    private int REQUEST_CODE_GPS_DIALOG = 27;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        context = this;
        nameET = findViewById(R.id.name_et);
        flatET = findViewById(R.id.flat);
        localityET = findViewById(R.id.flat);
        localityET = findViewById(R.id.locality_et);
        title = findViewById(R.id.title);
        detectlocLAY = findViewById(R.id.detect_loc);
        addressET = findViewById(R.id.address_et);
        getlocationIV = findViewById(R.id.location_iv);
        pretitleRG = findViewById(R.id.pretitleRG);
        addresstypeRG = findViewById(R.id.addresstypeRG);
        circleProgressView = findViewById(R.id.circleView);
        circleProgressView.setVisibility(View.VISIBLE);
        circleProgressView.setOuterContourColor(getResources().getColor(R.color.blue));
        circleProgressView.setTextSize(20);
        circleProgressView.setBarColor(getResources().getColor(R.color.blue));
        circleProgressView.setSpinBarColor(getResources().getColor(R.color.blue));
        circleProgressView.setValue(Float.parseFloat("60"));
        title.setText("Add Address");

        detectlocLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermission();
            }
        });
        pretitleRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio1:
                        prename = "Mr.";
                        break;
                    case R.id.radio2:
                        prename = "Mrs.";
                        break;
                    case R.id.radio3:
                        prename = "Miss";
                        break;

                }
            }
        });

        addresstypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio4:
                        addresstype = "Home";
                        break;
                    case R.id.radio5:
                        addresstype = "Office";

                        break;
                    case R.id.radio6:
                        addresstype = "Others";

                        break;
                }
            }
        });

//        Intent i=getIntent();
//        layout=i.getIntExtra("layout",0);
//        if(layout==1){
//            title.setText("Salon at home for Women");
//            circleProgressView = findViewById(R.id.circleView);
//            circleProgressView.setVisibility(View.VISIBLE);
//            circleProgressView.setOuterContourColor(getResources().getColor(R.color.blue));
//            circleProgressView.setTextSize(20);
//            circleProgressView.setBarColor(getResources().getColor(R.color.blue));
//            circleProgressView.setSpinBarColor(getResources().getColor(R.color.blue));
//            circleProgressView.setValue(Float.parseFloat("70"));
//        }
//        if(layout==2){
//            title.setText("Attending Wedding, Party etc.");
//
//
//            circleProgressView = findViewById(R.id.circleView);
//            circleProgressView.setVisibility(View.VISIBLE);
//            circleProgressView.setOuterContourColor(getResources().getColor(R.color.blue));
//            circleProgressView.setTextSize(20);
//            circleProgressView.setBarColor(getResources().getColor(R.color.blue));
//            circleProgressView.setSpinBarColor(getResources().getColor(R.color.blue));
//            circleProgressView.setValue(Float.parseFloat("60"));
//        }


        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameET.getText().toString().trim();
                String flat = flatET.getText().toString().trim();
                String locality = localityET.getText().toString().trim();
                String address = addressET.getText().toString().trim();
                if (StringUtils.isBlank(addresstype)) {
                    ToastUtils.shortToast(context, "select type");
                } else if (StringUtils.isBlank(prename)) {
                    ToastUtils.shortToast(context, "select type");

                } else {
                    addAddress(name, flat, locality, addresstype, prename, address);

                }
//                Intent intent = new Intent(AddAddressActivity.this, ScheduleTimeDateActivity.class);
//                startActivity(intent);

            }
        });
        search = findViewById(R.id.search);
        search.setVisibility(View.GONE);

        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void checkPermission() {
        int rc = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            init();
        } else {
            requestPermission();
        }
    }


    private void requestPermission() {
        final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 13);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0, len = permissions.length; i < len; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                String permission = permissions[i];
                boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                if (!showRationale) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Permission Required");
                    builder.setCancelable(false);
                    builder.setMessage("Location Permission Needed");
                    builder.setPositiveButton("Ok", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + context.getPackageName()));
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    });
                    builder.show();
                }
            }
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 13) {
                init();
            }
        }
    }

    private void init() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }


    /**
     * Starts requesting location from FusedLocationApi
     */

    /**
     * GoogleApiClient connection callbacks (Connected & Suspended)
     */
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            assert bundle != null;
            Log.i(TAG, "onConnected");
            Location mLocation = null;
            if (ContextCompat.checkSelfPermission(AddAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(AddAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    //Prompt the user once explanation has been shown
                    ActivityCompat.requestPermissions(AddAddressActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(AddAddressActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            } else {
                mLocation = mFusedLocationProviderApi.getLastLocation(mGoogleApiClient);

                if (mLocation != null) {
                    notifyToListeners(mLocation);
                    String lat = String.valueOf(mLocation.getLatitude());
                    String lng = String.valueOf(mLocation.getLongitude());
                    double la = Double.parseDouble(lat);
                    double ln = Double.parseDouble(lng);
                    Log.e(TAG, "notifyToListeners: " + lat + lng);
                    String s = "";
                    String loca = "";
                    String subloca = "";
                    Geocoder g = new Geocoder(context, Locale.getDefault());
                    try {
                        List<Address> l = g.getFromLocation(la, ln, 1);
                        Address a = l.get(0);
                        Log.e(TAG, "onConnected: " + a);
                        s = String.format("%s%s ", s, a.getAddressLine(0));
                        loca = String.format("%s%s ", loca, a.getLocality());
                        subloca = String.format("%s%s ", subloca, a.getSubLocality());
                        Log.e(TAG, "onConnected: " + loca);
                        Log.e(TAG, "onConnected: " + s);
                        Log.e(TAG, "onConnected: " + subloca);
                        flatET.setText(subloca);
                        localityET.setText(loca);
                        addressET.setText(s);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                String address=AppUtils.getAddress(context,Double.parseDouble(lat),Double.parseDouble(lng));
//                Log.e(TAG, "onConnected: "+address );
                }

//                else {
                // todo - always start location updates even if last location is not null
                startLocationUpdates();
//                }

            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "onConnectionSuspended : " + i);
        }
    };

    /**
     * GoogleApiClient connection failed callback
     */
    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.i(TAG, "onConnectionFailed : " + connectionResult);
        }
    };

    /**
     * Location Listener in which the updated location is received from FusedLocationApi
     */
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            notifyToListeners(location);
        }
    };

    /**
     * Broadcast the location to which ever activity is listening.
     *
     * @param location updated
     */
    private void notifyToListeners(Location location) {
        Log.i(TAG, "notifyToListeners : " + location);

    }


    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        // todo - Start location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, (LocationListener) mLocationListener
        );

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);

                        } catch (ApiException e) {
                            if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                                // todo - show gps enabling dialog
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(
                                        AddAddressActivity.this, REQUEST_CODE_GPS_DIALOG
                                );
                            } else {
                                // todo - gps disabled
                                Toast.makeText(context, "Enable GPS Manually", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_GPS_DIALOG) {
            if (resultCode == Activity.RESULT_OK) {
                // todo - user enabled the user now location updates will work
                Toast.makeText(context, "Location Updates Started", Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // todo - user did not enabled gps
                Toast.makeText(context, "User Disabled GPS", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addAddress(String name, String flat, String locality, String addresstype, String prename, String address) {
        ProgressDialogUtils.start(AddAddressActivity.this, "Please wait...");

        HashMap<String, String> bodyMap = new HashMap<>();

        bodyMap.put("user_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.USERID));
        bodyMap.put("prefix", prename);
        bodyMap.put("name", name);
        bodyMap.put("addresstype", addresstype);
        bodyMap.put("street", flat);
        bodyMap.put("locality", locality);
        bodyMap.put("address", address);
        bodyMap.put("pincode", "777777");


        ApiInterface apiInterface = ApiClient.createRequest(ApiInterface.class, ApiConstant.BASE_URL);

        Call<JsonObject> call = apiInterface.postRequest(ApiConstant.DELIVERY_ADDRESS, bodyMap);

        ApiClient.execute(call, new ApiTask() {
            @Override
            public void onComplete(Response response) {
                try {
                    if (response.body() != null) {
                        ProgressDialogUtils.stop();

                        JsonObject jsonObject = (JsonObject) response.body();
                        String status = jsonObject.get("status").getAsString();
                        String msg = jsonObject.get("msg").getAsString();
                        SharedPreferenceUtils.setString(SharedPreferenceConstant.NAME, jsonObject.get("status").getAsString());
                        if (status.equalsIgnoreCase("200")) {
                            ToastUtils.shortToast(context, msg);
                            Intent intent = new Intent(context, GeyserFourActivity.class);
                            startActivity(intent);
//                            JsonArray jsonArray = jsonObject.getAsJsonArray("data");
//                            for (int i = 0; i < jsonArray.size(); i++) {
//                                JsonObject jsonObject1 = jsonArray.get(0).getAsJsonObject();
//                                Intent intent = new Intent(context, OTPActivity.class);
//                                startActivity(intent);
//
//                            }
                        } else {
                            ProgressDialogUtils.stop();

                            ToastUtils.longToast(context, "try again");
                        }

                        // TODO: 8/13/2019 Decode jsonObject according to your json response of the api
                    } else {
                        // TODO: 8/13/2019 response is null show error message
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
