package ws.wolfsoft.rapid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;

import Adapter.DateDayRecycleAdapter;
import Adapter.TimeRecycleAdapter;
import ModelClass.DateDayModelClass;
import apis.ApiClient;
import at.grabner.circleprogress.CircleProgressView;
import constant.ApiConstant;
import constant.SharedPreferenceConstant;
import dto.Datedto;
import dto.GetAddressDto;
import dto.Timedto;
import dto.responses.DateResponse;
import dto.responses.SubCategoryResponse;
import dto.responses.TimeResponse;
import interfaces.ApiInterface;
import interfaces.ApiTask;
import retrofit2.Call;
import retrofit2.Response;
import utilities.ProgressDialogUtils;
import utilities.SharedPreferenceUtils;
import utilities.ToastUtils;

public class GeyserFiveActivity extends AppCompatActivity {


    TextView title, number, number1;
    ImageView back_img, search;
    Button button;

    ArrayList<Datedto> datelist = new ArrayList<>();
    ArrayList<Timedto> morninglist = new ArrayList<>();
    ArrayList<Timedto> aftrenoonlist = new ArrayList<>();
    ArrayList<Timedto> eveninglist = new ArrayList<>();
    /*horizontal date day recyerview data is here*/

    private ArrayList<DateDayModelClass> dateDayModelClasses;
    private RecyclerView recyclerView;
    private DateDayRecycleAdapter bAdapter;

    private String date[] = {"15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"};
    private String day[] = {"MON", "TUS", "WEN", "Thu", "FRI", "SAT", "SUN", "MON", "TUS", "WEN", "Thu", "FRI", "SAT", "SUN", "MON"};


    /*Morning Grid view recycerview data*/

    private ArrayList<DateDayModelClass> dateDayModelClasses1;
    private RecyclerView recyclerView1;
    private TimeRecycleAdapter bAdapter1;

    private String time[] = {"09:00", "10:00", "11:00"};
    private String hours[] = {"am", "am", "am"};

    /*After noon Grid view recycerview data*/
    private ArrayList<DateDayModelClass> dateDayModelClasses2;
    private RecyclerView recyclerView2;
    private TimeRecycleAdapter bAdapter2;
    private String time2[] = {"12:00", "01:00", "02:00", "03:00", "04:00"};
    private String hours2[] = {"pm", "pm", "pm", "pm", "pm"};
    /*Evening Grid view recycerview data*/

    private ArrayList<DateDayModelClass> dateDayModelClasses3;
    private RecyclerView recyclerView3;
    private TimeRecycleAdapter bAdapter3;

    private String time3[] = {"06:00", "07:00", "08:00"};
    private String hours3[] = {"pm", "pm", "pm"};

    CircleProgressView circleProgressView;
    Context context;
    String category, service, price, quantity;
    GetAddressDto addressDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geyser_five);
        context = this;
        circleProgressView = findViewById(R.id.circleView);
        circleProgressView.setVisibility(View.VISIBLE);
        circleProgressView.setOuterContourColor(getResources().getColor(R.color.blue));
        circleProgressView.setTextSize(20);
        circleProgressView.setBarColor(getResources().getColor(R.color.blue));
        circleProgressView.setSpinBarColor(getResources().getColor(R.color.blue));
        circleProgressView.setValue(Float.parseFloat("70"));
        category = getIntent().getStringExtra("category");
        service = getIntent().getStringExtra("service");
        price = getIntent().getStringExtra("price");
        quantity = getIntent().getStringExtra("quantity");
        addressDto = (GetAddressDto) getIntent().getSerializableExtra("address");


        title = findViewById(R.id.title);
        title.setText(category);

        search = findViewById(R.id.search);
        search.setVisibility(View.GONE);

        back_img = findViewById(R.id.back_img);
        back_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedPreferenceUtils.getBoolean(SharedPreferenceConstant.IS_LOGGED_IN)) {
                    startActivity(new Intent(context, PaymentActivity.class).putExtra("price", price));

//                    BookingWS();

                } else {
                    startActivity(new Intent(context, LoginSignupActivity.class));
                }
//                Intent intent = new Intent(GeyserFiveActivity.this, GeyserSixActivity.class);
//                startActivity(intent);
            }
        });

        /*horizontal category recyclerview code is here*/

        recyclerView = findViewById(R.id.recyclerview_date);
        recyclerView3 = findViewById(R.id.recyclerview_evening);

        recyclerView1 = findViewById(R.id.recyclerview_morning);
        recyclerView2 = findViewById(R.id.recyclerview_afternoon);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GeyserFiveActivity.this);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		recyclerView1.setItemAnimator(new DefaultItemAnimator());
		bAdapter1 = new TimeRecycleAdapter(GeyserFiveActivity.this);
		recyclerView1.setAdapter(bAdapter1);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GeyserFiveActivity.this);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        bAdapter2 = new TimeRecycleAdapter(GeyserFiveActivity.this);
		recyclerView2.setAdapter(bAdapter2);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GeyserFiveActivity.this);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        bAdapter3 = new TimeRecycleAdapter(GeyserFiveActivity.this);
		recyclerView3.setAdapter(bAdapter3);
		
		bAdapter1.addListener(morningTimeSelected);
		bAdapter2.addListener(afternoonTimeSelected);
		bAdapter3.addListener(eveningTimeSelected);
						
        dateWS();
        morning();
        aftrnoon();
        evening();
    }

	private TimeRecycleAdapter.OnTimeSelected morningTimeSelected = () -> {
        bAdapter2.clearSelections();
		bAdapter3.clearSelections();
    };

	private TimeRecycleAdapter.OnTimeSelected afternoonTimeSelected = () -> {
        bAdapter1.clearSelections();
		bAdapter3.clearSelections();    
    };
	
	private TimeRecycleAdapter.OnTimeSelected eveningTimeSelected = () -> {
        bAdapter1.clearSelections();
		bAdapter2.clearSelections();
    };
	
    public void dateWS() {
        ProgressDialogUtils.start(GeyserFiveActivity.this, "Please wait");
        HashMap<String, String> bodyMap = new HashMap<>();

//        bodyMap.put("category_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.CATEGORY_ID));
//        bodyMap.put("subcategory_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.SUB_CATEGORY_ID));
        bodyMap.put("category_id", "2");
        bodyMap.put("subcategory_id", "7");

        ApiInterface apiInterface = ApiClient.createRequest(ApiInterface.class, ApiConstant.BASE_URL);
        Call<JsonObject> call = apiInterface.postRequest(ApiConstant.SERVICE_DATE, bodyMap);

        ApiClient.execute(call, response -> {
            try {
                if (response.body() != null) {
                    ProgressDialogUtils.stop();
                    JsonObject jsonObject = (JsonObject) response.body();
                    DateResponse dateresponse = new Gson().fromJson(jsonObject, DateResponse.class);
                    if (dateresponse.getStatus() == 200) {
                        datelist.addAll(dateresponse.getDatelist());
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GeyserFiveActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        bAdapter = new DateDayRecycleAdapter(GeyserFiveActivity.this, datelist);
                        recyclerView.setAdapter(bAdapter);


//                        bAdapter1.refreshData(datelist);

                    }
                    // TODO: 8/13/2019 Decode jsonObject according to your json response of the api
                } else {
                    // TODO: 8/13/2019 response is null show error message
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void morning() {
        ProgressDialogUtils.start(GeyserFiveActivity.this, "Please wait");
        HashMap<String, String> bodyMap = new HashMap<>();

//        bodyMap.put("category_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.CATEGORY_ID));
//        bodyMap.put("subcategory_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.SUB_CATEGORY_ID));
        bodyMap.put("category_id", "2");
        bodyMap.put("subcategory_id", "7");

        ApiInterface apiInterface = ApiClient.createRequest(ApiInterface.class, ApiConstant.BASE_URL);
        Call<JsonObject> call = apiInterface.postRequest(ApiConstant.MORNING, bodyMap);

        ApiClient.execute(call, response -> {
            try {
                if (response.body() != null) {
                    ProgressDialogUtils.stop();
                    JsonObject jsonObject = (JsonObject) response.body();
                    TimeResponse timeResponse = new Gson().fromJson(jsonObject, TimeResponse.class);
					
					// Clear list before adding data to handle duplicate entries
					morninglist.clear();
					
                    if (timeResponse.getStatus() == 200) {
						
                        morninglist.addAll(timeResponse.getTimelist());
                        
						bAdapter1.submitList(morninglist);

//                        bAdapter1.refreshData(datelist);

                    }
                    // TODO: 8/13/2019 Decode jsonObject according to your json response of the api
                } else {
                    // TODO: 8/13/2019 response is null show error message
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void aftrnoon() {
        ProgressDialogUtils.start(GeyserFiveActivity.this, "Please wait");
        HashMap<String, String> bodyMap = new HashMap<>();

//        bodyMap.put("category_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.CATEGORY_ID));
//        bodyMap.put("subcategory_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.SUB_CATEGORY_ID));
        bodyMap.put("category_id", "4");
        bodyMap.put("subcategory_id", "6");

        ApiInterface apiInterface = ApiClient.createRequest(ApiInterface.class, ApiConstant.BASE_URL);
        Call<JsonObject> call = apiInterface.postRequest(ApiConstant.AfTRNOON, bodyMap);

        ApiClient.execute(call, response -> {
            try {
                if (response.body() != null) {
                    ProgressDialogUtils.stop();
                    JsonObject jsonObject = (JsonObject) response.body();
                    TimeResponse timeResponse = new Gson().fromJson(jsonObject, TimeResponse.class);
                    
					// Clear list before adding data to handle duplicate entries
					aftrenoonlist.clear();
					
					if (timeResponse.getStatus() == 200) {
                        aftrenoonlist.addAll(timeResponse.getTimelist());
                        
						bAdapter2.submitList(aftrenoonlist);

//                        bAdapter1.refreshData(datelist);

                    }
                    // TODO: 8/13/2019 Decode jsonObject according to your json response of the api
                } else {
                    // TODO: 8/13/2019 response is null show error message
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void evening() {
        ProgressDialogUtils.start(GeyserFiveActivity.this, "Please wait");
        HashMap<String, String> bodyMap = new HashMap<>();

//        bodyMap.put("category_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.CATEGORY_ID));
//        bodyMap.put("subcategory_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.SUB_CATEGORY_ID));
        bodyMap.put("category_id", "3");
        bodyMap.put("subcategory_id", "4");

        ApiInterface apiInterface = ApiClient.createRequest(ApiInterface.class, ApiConstant.BASE_URL);
        Call<JsonObject> call = apiInterface.postRequest(ApiConstant.EVENING, bodyMap);

        ApiClient.execute(call, response -> {
            try {
                if (response.body() != null) {
                    ProgressDialogUtils.stop();
                    JsonObject jsonObject = (JsonObject) response.body();
                    TimeResponse timeResponse = new Gson().fromJson(jsonObject, TimeResponse.class);
                    
					// Clear list before adding data to handle duplicate entries
					eveninglist.clear();
					
					if (timeResponse.getStatus() == 200) {
                        eveninglist.addAll(timeResponse.getTimelist());
                        
						bAdapter3.submitList(eveninglist);
						
//                        bAdapter1.refreshData(datelist);

                    }
                    // TODO: 8/13/2019 Decode jsonObject according to your json response of the api
                } else {
                    // TODO: 8/13/2019 response is null show error message
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //orderi id toh ismai milegi na.. haa
    public void BookingWS() {
        ProgressDialogUtils.start(GeyserFiveActivity.this, "Please wait");
        HashMap<String, String> bodyMap = new HashMap<>();
        bodyMap.put("user_id", SharedPreferenceUtils.getString(SharedPreferenceConstant.USERID));
        bodyMap.put("address", addressDto.getLocality());
        bodyMap.put("price", price);
        bodyMap.put("mobile", SharedPreferenceUtils.getString(SharedPreferenceConstant.PHONE));
        bodyMap.put("name", addressDto.getName());
        bodyMap.put("qty", quantity);
        bodyMap.put("categoryName", category);
        bodyMap.put("subcategoryName", service);

        ApiInterface apiInterface = ApiClient.createRequest(ApiInterface.class, ApiConstant.BASE_URL);
        Call<JsonObject> call = apiInterface.postRequest(ApiConstant.BOOKING, bodyMap);

        ApiClient.execute(call, response -> {
            try {
                if (response.body() != null) {
                    ProgressDialogUtils.stop();
                    JsonObject jsonObject = (JsonObject) response.body();
                    String status = jsonObject.get("status").getAsString();
                    SharedPreferenceUtils.setString(SharedPreferenceConstant.NAME, jsonObject.get("status").getAsString());
                    if (status.equalsIgnoreCase("200")) {
                        ToastUtils.longToast(context, "Service booked");
                        startActivity(new Intent(context, PaymentActivity.class));

//                        JsonArray jsonArray = jsonObject.getAsJsonArray("data");
//                        for (int i = 0; i < jsonArray.size(); i++) {
//                            JsonObject jsonObject1 = jsonArray.get(0).getAsJsonObject();
//                                SharedPreferenceUtils.setString(SharedPreferenceConstant.USERID, jsonObject1.get("id").getAsString());
//                                SharedPreferenceUtils.setString(SharedPreferenceConstant.PHONE, jsonObject1.get("mobile").getAsString());
//                                SharedPreferenceUtils.setString(SharedPreferenceConstant.NAME, jsonObject1.get("name").getAsString());
//                                SharedPreferenceUtils.setString(SharedPreferenceConstant.IS_ACTIVE, jsonObject1.get("is_active").getAsString());
//                                Intent intent = new Intent(GeyserFiveActivity.this, OTPActivity.class);
//                                startActivity(intent);
//                        }
                    } else {
                        ToastUtils.longToast(context, "try again");
                    }

                    // TODO: 8/13/2019 Decode jsonObject according to your json response of the api
                } else {
                    // TODO: 8/13/2019 response is null show error message
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
