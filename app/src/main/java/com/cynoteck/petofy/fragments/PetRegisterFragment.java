package com.cynoteck.petofy.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cynoteck.petofy.utils.PetParentSingleton;
import com.cynoteck.petofy.R;
import com.cynoteck.petofy.activity.AddPetRegister;
import com.cynoteck.petofy.activity.PetProfileActivity;
import com.cynoteck.petofy.activity.SelectPetReportsActivity;
import com.cynoteck.petofy.adapter.RegisterPetAdapter;
import com.cynoteck.petofy.api.ApiClient;
import com.cynoteck.petofy.api.ApiResponse;
import com.cynoteck.petofy.api.ApiService;
import com.cynoteck.petofy.parameter.petReportsRequest.PetDataParams;
import com.cynoteck.petofy.parameter.petReportsRequest.PetDataRequest;
import com.cynoteck.petofy.response.getPetReportsResponse.getPetListResponse.GetPetListResponse;
import com.cynoteck.petofy.response.getPetReportsResponse.getPetListResponse.PetList;
import com.cynoteck.petofy.utils.Config;
import com.cynoteck.petofy.utils.Methods;
import com.cynoteck.petofy.utils.NetworkChangeReceiver;
import com.cynoteck.petofy.onClicks.ViewDeatilsAndIdCardClick;

import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.cynoteck.petofy.fragments.ProfileFragment.petListHorizontalAdapter;
import static com.cynoteck.petofy.fragments.ProfileFragment.pet_list_LL;
import static com.cynoteck.petofy.activity.DonationActivity.donatePetAdapter;

@SuppressLint("StaticFieldLeak")
public class PetRegisterFragment extends Fragment implements ApiResponse, ViewDeatilsAndIdCardClick, View.OnClickListener, TextWatcher {
    View view;
    Context context;
    Methods methods;
    CardView materialCardView;
    RecyclerView register_pet_RV;
    ImageView empty_IV, search_register_pet;
    public static RegisterPetAdapter registerPetAdapter;
    RelativeLayout search_boxRL, add_pet_RL;
    //    AutoCompleteTextView search_box;
    TextView regiter_pet_headline_TV;
    public static TextView total_pets_TV;
    static NestedScrollView nestedScrollView;
    static ProgressBar progressBar, progressBarFirst;
    public static int page = 1, pagelimit = 100;
    //    ArrayList<PetList> profileList;
    SharedPreferences sharedPreferences;
    static LinearLayout something_wrong_LL;
    static Button retry_BT;
    private final int ADD_PET = 2;

    private BroadcastReceiver mNetworkReceiver;
    static boolean isOnline = true;
    static boolean isLoaded = false;

    @Override
    public void onAttach(@Nullable Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public PetRegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_pet_register, container, false);
        sharedPreferences = getActivity().getSharedPreferences("userDetails", 0);

        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();

        Log.e("CHECK", "CKECK1");
        init();
        Log.e("CHECK", "CKECK1");

        return view;
    }


    @SuppressLint("SetTextI18n")
    private void init() {
        Log.e("CHECK", "CKECK1");

        methods = new Methods(context);
        something_wrong_LL = view.findViewById(R.id.something_wrong_LL);
        retry_BT = view.findViewById(R.id.retry_BT);
        empty_IV = view.findViewById(R.id.empty_IV);
        materialCardView = view.findViewById(R.id.toolbar);
        register_pet_RV = view.findViewById(R.id.register_pet_RV);
        add_pet_RL = view.findViewById(R.id.add_pet_RL);
        total_pets_TV = view.findViewById(R.id.total_pets_TV);
        search_boxRL = view.findViewById(R.id.search_boxRL);
        nestedScrollView = view.findViewById(R.id.nested_scroll_view);
        progressBar = view.findViewById(R.id.progressBar);
        progressBarFirst = view.findViewById(R.id.progressBarFirst);
        methods = new Methods(getContext());

        add_pet_RL.setOnClickListener(this);
        retry_BT.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        register_pet_RV.setLayoutManager(linearLayoutManager);
        registerPetAdapter = new RegisterPetAdapter(getContext(), PetParentSingleton.getInstance().getArrayList(), this);

        if (!PetParentSingleton.getInstance().getArrayList().isEmpty()) {
            something_wrong_LL.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
            progressBarFirst.setVisibility(View.GONE);
            pet_list_LL.setVisibility(View.VISIBLE);
            total_pets_TV.setText("You have " + PetParentSingleton.getInstance().getArrayList().size() + " pets registered ");
            register_pet_RV.setAdapter(registerPetAdapter);
            registerPetAdapter.notifyDataSetChanged();
            petListHorizontalAdapter.notifyDataSetChanged();
        }else {
                progressBarFirst.setVisibility(View.VISIBLE);
                getPetList(page, pagelimit);
        }


//        Timer timer = new Timer ();
//        TimerTask hourlyTask = new TimerTask () {
//            @Override
//            public void run () {
//                Log.e("CHECK","CKECK3");
//                getPetList(page, pagelimit);
//            }
//        };
//
//        timer.schedule (hourlyTask, 0l, 5000);
//        getPet();
    }

//    private void getPet() {
//        search_box.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                Log.d("dataChange", "afterTextChanged" + new String(editable.toString()));
//                String value = editable.toString();
//                petSearchDependsOnPrefix(value);
//            }
//        });
//
//
//        search_box.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String value = search_box.getText().toString();
//                String[] city_array = value.split("\\(");
//
//                search_box.setText(city_array[0]);
//            }
//        });
//
//    }

    private void petSearchDependsOnPrefix(String prefix) {
        PetDataParams getPetDataParams = new PetDataParams();
        getPetDataParams.setPageNumber(0);//0
        getPetDataParams.setPageSize(10);//0
        getPetDataParams.setSearch_Data(prefix);
        PetDataRequest getPetDataRequest = new PetDataRequest();
        getPetDataRequest.setData(getPetDataParams);

        ApiService<GetPetListResponse> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getPetList(Config.token, getPetDataRequest), "GetPetListBySearch");
        Log.e("DATALOG", "check1=> " + getPetDataRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PET) {
            if (resultCode == RESULT_OK) {
                PetList petList = new PetList();
                petList.setId(data.getStringExtra("pet_id"));
                petList.setPetUniqueId(data.getStringExtra("pet_unique_id"));
                petList.setPetProfileImageUrl(data.getStringExtra("pet_image_url"));
                petList.setPetBreed(data.getStringExtra("pet_breed"));
                petList.setPetAge(data.getStringExtra("pet_age"));
                petList.setPetSex(data.getStringExtra("pet_sex"));
                petList.setPetName(data.getStringExtra("pet_name"));
                petList.setPetParentName(data.getStringExtra("pet_parent"));
                petList.setPetCategory(data.getStringExtra("pet_category"));
                petList.setDateOfBirth(data.getStringExtra("pet_date_of_birth"));
                petList.setPetColor(data.getStringExtra("pet_color"));
                PetParentSingleton.getInstance().getArrayList().add(0, petList);
                registerPetAdapter.notifyDataSetChanged();
                total_pets_TV.setText("You have " + PetParentSingleton.getInstance().getArrayList().size() + " pets registered ");
                petListHorizontalAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getFromScroll(int page, int pageLimit) {
        PetDataParams getPetDataParams = new PetDataParams();
        getPetDataParams.setPageNumber(page);//0
        getPetDataParams.setPageSize(pageLimit);//0
        getPetDataParams.setSearch_Data("");
        PetDataRequest getPetDataRequest = new PetDataRequest();
        getPetDataRequest.setData(getPetDataParams);

        ApiService<GetPetListResponse> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getPetList(Config.token, getPetDataRequest), "GetFromScroll");

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_pet_RL:
                Intent adNewIntent = new Intent(getActivity(), AddPetRegister.class);
                adNewIntent.putExtra("intent_from", "add");
                startActivityForResult(adNewIntent, ADD_PET);
                break;

            case R.id.retry_BT:
                if (isOnline) {
                    something_wrong_LL.setVisibility(View.GONE);
                    progressBarFirst.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.GONE);
                    getPetList(page, pagelimit);

                } else {
                    somethingWrongMethod();
                }

                break;
        }

    }

    public void getPetList(int page, int pageLimit) {
        PetDataParams getPetDataParams = new PetDataParams();
        getPetDataParams.setPageNumber(page);//0
        getPetDataParams.setPageSize(pageLimit);//0
        getPetDataParams.setSearch_Data("");
        PetDataRequest getPetDataRequest = new PetDataRequest();
        getPetDataRequest.setData(getPetDataParams);

        ApiService<GetPetListResponse> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getPetList(Config.token, getPetDataRequest), "GetPetList");

        Log.e("REQUESTT", methods.getRequestJson(getPetDataRequest));

    }


    @Override
    public void onResponse(Response response, String key) {
        switch (key) {
            case "GetPetList":
                try {
                    isLoaded = true;
                    GetPetListResponse getPetListResponse = (GetPetListResponse) response.body();
                    int responseCode = Integer.parseInt(getPetListResponse.getResponse().getResponseCode());
                    something_wrong_LL.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                    progressBarFirst.setVisibility(View.GONE);
                    if (responseCode == 109) {
                        if (getPetListResponse.getData().getPetList().isEmpty()) {
                            total_pets_TV.setText("No pet registered ! ");
                            empty_IV.setVisibility(View.VISIBLE);
                            search_register_pet.setVisibility(View.INVISIBLE);
                            pet_list_LL.setVisibility(View.GONE);
                        } else {
                            PetParentSingleton.getInstance().getArrayList().clear();
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                            register_pet_RV.setLayoutManager(linearLayoutManager);
                            if (getPetListResponse.getData().getPetList().size() > 0) {
                                for (int i = 0; i < getPetListResponse.getData().getPetList().size(); i++) {
                                    PetList petList = new PetList();
                                    petList.setPetUniqueId(getPetListResponse.getData().getPetList().get(i).getPetUniqueId());
                                    petList.setDateOfBirth(getPetListResponse.getData().getPetList().get(i).getDateOfBirth());
                                    petList.setPetName(getPetListResponse.getData().getPetList().get(i).getPetName());
                                    petList.setPetSex(getPetListResponse.getData().getPetList().get(i).getPetSex());
                                    petList.setPetParentName(getPetListResponse.getData().getPetList().get(i).getPetParentName());
                                    petList.setPetProfileImageUrl(getPetListResponse.getData().getPetList().get(i).getPetProfileImageUrl());
                                    petList.setEncryptedId(getPetListResponse.getData().getPetList().get(i).getEncryptedId());
                                    petList.setId(getPetListResponse.getData().getPetList().get(i).getId());
                                    petList.setPetAge(getPetListResponse.getData().getPetList().get(i).getPetAge());
                                    petList.setPetCategoryId(getPetListResponse.getData().getPetList().get(i).getPetCategoryId());
                                    petList.setLastVisitEncryptedId(getPetListResponse.getData().getPetList().get(i).getLastVisitEncryptedId());
                                    petList.setPetBreed(getPetListResponse.getData().getPetList().get(i).getPetBreed());
                                    petList.setPetCategory(getPetListResponse.getData().getPetList().get(i).getPetCategory());
                                    petList.setPetColor(getPetListResponse.getData().getPetList().get(i).getPetColor());

                                    PetParentSingleton.getInstance().getArrayList().add(petList);
                                }

                                total_pets_TV.setText("You have " + PetParentSingleton.getInstance().getArrayList().size() + " pets registered ");
                                registerPetAdapter = new RegisterPetAdapter(getContext(), PetParentSingleton.getInstance().getArrayList(), this);
                                register_pet_RV.setAdapter(registerPetAdapter);
                                registerPetAdapter.notifyDataSetChanged();
                                petListHorizontalAdapter.notifyDataSetChanged();
                                pet_list_LL.setVisibility(View.VISIBLE);
                                donatePetAdapter.notifyDataSetChanged();

                            }
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;


        }
    }

    @Override
    public void onError(Throwable t, String key) {
        Log.e("ERROR", t.getLocalizedMessage());
        somethingWrongMethod();

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onViewDetailsClick(int position) {
        Intent intent = new Intent(getActivity(), PetProfileActivity.class);
        intent.putExtra("pet_list_position", String.valueOf(position));
        intent.putExtra("pet_id", PetParentSingleton.getInstance().getArrayList().get(position).getId().substring(0, PetParentSingleton.getInstance().getArrayList().get(position).getId().length() - 2));
        intent.putExtra("pet_unique_id", PetParentSingleton.getInstance().getArrayList().get(position).getPetUniqueId());
        intent.putExtra("pet_image_url", PetParentSingleton.getInstance().getArrayList().get(position).getPetProfileImageUrl());
        intent.putExtra("pet_breed", PetParentSingleton.getInstance().getArrayList().get(position).getPetBreed());
        intent.putExtra("pet_age", PetParentSingleton.getInstance().getArrayList().get(position).getPetAge());
        intent.putExtra("pet_sex", PetParentSingleton.getInstance().getArrayList().get(position).getPetSex());
        intent.putExtra("pet_name", PetParentSingleton.getInstance().getArrayList().get(position).getPetName());
        intent.putExtra("pet_category", PetParentSingleton.getInstance().getArrayList().get(position).getPetCategory());
        intent.putExtra("pet_DOB", PetParentSingleton.getInstance().getArrayList().get(position).getDateOfBirth());
        intent.putExtra("pet_color", PetParentSingleton.getInstance().getArrayList().get(position).getPetColor());

        startActivity(intent);

    }

    @Override
    public void onViewReportsClick(int position) {
        Intent selectReportsIntent = new Intent(getActivity().getApplication(), SelectPetReportsActivity.class);
        Bundle data = new Bundle();
        data.putString("pet_id", PetParentSingleton.getInstance().getArrayList().get(position).getId().substring(0, PetParentSingleton.getInstance().getArrayList().get(position).getId().length() - 2));
        data.putString("pet_name", PetParentSingleton.getInstance().getArrayList().get(position).getPetName());
        data.putString("pet_unique_id", PetParentSingleton.getInstance().getArrayList().get(position).getPetUniqueId());
        data.putString("pet_sex", PetParentSingleton.getInstance().getArrayList().get(position).getPetSex());
        data.putString("pet_owner_name", PetParentSingleton.getInstance().getArrayList().get(position).getPetParentName());
        data.putString("pet_owner_contact", PetParentSingleton.getInstance().getArrayList().get(position).getContactNumber());
        data.putString("pet_DOB", PetParentSingleton.getInstance().getArrayList().get(position).getDateOfBirth());
        data.putString("pet_encrypted_id", PetParentSingleton.getInstance().getArrayList().get(position).getEncryptedId());
        data.putString("pet_age", PetParentSingleton.getInstance().getArrayList().get(position).getPetAge());
        data.putString("pet_image_url", PetParentSingleton.getInstance().getArrayList().get(position).getPetProfileImageUrl());
        selectReportsIntent.putExtras(data);
        startActivity(selectReportsIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onIdAddClinicClick(int position) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getActivity().registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    public static void dialog(boolean value) {
        if (value) {
            Log.e("Connected", "Yes");
            isOnline = true;
            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    somethingWrongMethod();
                }
            };
            handler.postDelayed(delayrunnable, 3000);
        } else {
            isOnline = false;
            Log.e("Connected", "NO");
            somethingWrongMethod();
        }
    }

    private static void somethingWrongMethod() {
        progressBarFirst.setVisibility(View.GONE);
        Log.e("RUN", "RUN");
        if (isLoaded) {
            Log.e("RUN", "NOT_EMPTY");
            something_wrong_LL.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
        } else {
            Log.e("RUN", "EMPTY");
            something_wrong_LL.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
        }
    }

}