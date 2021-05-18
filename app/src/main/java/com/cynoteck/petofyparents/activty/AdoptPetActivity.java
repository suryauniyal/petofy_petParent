package com.cynoteck.petofyparents.activty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cynoteck.petofyparents.PetParentSingleton;
import com.cynoteck.petofyparents.R;
import com.cynoteck.petofyparents.adapter.AdoptionListAdopter;
import com.cynoteck.petofyparents.api.ApiClient;
import com.cynoteck.petofyparents.api.ApiResponse;
import com.cynoteck.petofyparents.api.ApiService;
import com.cynoteck.petofyparents.parameter.adoptionListRequest.AdoptionListHeader;
import com.cynoteck.petofyparents.parameter.adoptionListRequest.AdoptionListParameter;
import com.cynoteck.petofyparents.parameter.adoptionListRequest.AdoptionListRequestModel;
import com.cynoteck.petofyparents.parameter.petBreedRequest.BreedParams;
import com.cynoteck.petofyparents.parameter.petBreedRequest.BreedRequest;
import com.cynoteck.petofyparents.response.addPet.breedResponse.BreedCatRespose;
import com.cynoteck.petofyparents.response.addPet.petAgeResponse.PetAgeValueResponse;
import com.cynoteck.petofyparents.response.addPet.petColorResponse.PetColorValueResponse;
import com.cynoteck.petofyparents.response.addPet.petSizeResponse.PetSizeValueResponse;
import com.cynoteck.petofyparents.response.adoptionListResponse.AdoptionListResponse;
import com.cynoteck.petofyparents.response.adoptionListResponse.PetDonationList;
import com.cynoteck.petofyparents.response.cityResponse.CityResponseModel;
import com.cynoteck.petofyparents.response.stateResponse.StateResponse;
import com.cynoteck.petofyparents.response.updateProfileResponse.PetTypeResponse;
import com.cynoteck.petofyparents.utils.AdoptionListOnClick;
import com.cynoteck.petofyparents.utils.Config;
import com.cynoteck.petofyparents.utils.Methods;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

public class AdoptPetActivity extends AppCompatActivity implements View.OnClickListener, ApiResponse, AdoptionListOnClick {

    AppCompatSpinner petType, petGender, petAge, petSize, petColor, petBreed;
    RecyclerView adoption_RV;
    MaterialCardView back_arrow_CV;

    Methods methods;
    AdoptionListAdopter adoptionListAdopter;
    List<PetDonationList> petDonationLists;

    NestedScrollView nestedSV;
    ProgressBar progressBar;
    RadioButton cats_RB,dog_RB;
    public static RelativeLayout total_adoption_RL;
    public static TextView total_adoption_request_TV;

    int pageNumber = 1, pageSize = 10;

    ArrayList<String> petTypeList, petBreedList, petAgeList, petColorList, petSizeList, petSexList;
    HashMap<String, String> petTypeHashMap, petBreedHashMap, petAgeHashMap, petColorHashMap, petSizeHashMap, petSexHashMap;

    String strSpnerItemPetNm = "", getStrSpnerItemPetNmId = "2", strSpnrBreed = "", strSpnrBreedId = "3.0", strSpnrAge = "",
            strSpnrAgeId = "1.0", strSpnrColor = "", strSpnrColorId = "1.0", strSpnrSize = "", strSpneSizeId = "1.0", strSpnrSex = "",
            strSpnrSexId = "1.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_you_want_adopt);
        init();
    }

    private void init() {
        methods = new Methods(this);
        progressBar = findViewById(R.id.progressBar);
        adoption_RV = findViewById(R.id.adoption_RV);
        nestedSV = findViewById(R.id.nestedScrollView);
        dog_RB = findViewById(R.id.dog_RB);
        cats_RB = findViewById(R.id.cats_RB);
        back_arrow_CV = findViewById(R.id.back_arrow_CV);
        total_adoption_RL = findViewById(R.id.total_adoption_RL);
        total_adoption_request_TV = findViewById(R.id.total_adoption_request_TV);


        total_adoption_RL.setOnClickListener(this);
        cats_RB.setOnClickListener(this);
        dog_RB.setOnClickListener(this);
        back_arrow_CV.setOnClickListener(this);

        petSexList = new ArrayList<>();
        petSexList.add("Pet Sex");
        petSexList.add("Male");
        petSexList.add("Female");

        petSexHashMap = new HashMap<>();
        petSexHashMap.put("Pet Sex", "0");
        petSexHashMap.put("Male", "1");
        petSexHashMap.put("Female", "2");

        if (methods.isInternetOn()) {
            getAdoptionList(getStrSpnerItemPetNmId);
        } else {
            methods.isInternetOn();
        }

        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    pageSize++;
                    progressBar.setVisibility(View.VISIBLE);
                    getAdoptionList(getStrSpnerItemPetNmId);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PetParentSingleton.getInstance().getGetAdoptionRequestListData().isEmpty()){
            total_adoption_request_TV.setText(String.valueOf(PetParentSingleton.getInstance().getGetAdoptionRequestListData().size()));
            total_adoption_RL.setEnabled(true);
            total_adoption_RL.setEnabled(true);
        }else {
            total_adoption_RL.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back_arrow_CV:

                onBackPressed();

                break;
            case R.id.cats_RB:

                getStrSpnerItemPetNmId ="2";
                cats_RB.setChecked(true);
                dog_RB.setChecked(false);
                getAdoptionList(getStrSpnerItemPetNmId);

                break;


            case R.id.dog_RB:

                getStrSpnerItemPetNmId ="1";
                cats_RB.setChecked(false);
                dog_RB.setChecked(true);
                getAdoptionList(getStrSpnerItemPetNmId);

                break;

            case R.id.total_adoption_RL:

                Intent adoptionRequestActivityIntent = new Intent(this, AdoptionRequestActivity.class);
                startActivity(adoptionRequestActivityIntent);
                break;
        }

    }


    private void getAdoptionList(String petCategoryId) {
        AdoptionListParameter adoptionListParameter = new AdoptionListParameter();
        adoptionListParameter.setPetCategoryId(petCategoryId);
        adoptionListParameter.setPetSexId("0.0");
        adoptionListParameter.setPetAgeId("0.0");
        adoptionListParameter.setPetSizeId("0.0");
        adoptionListParameter.setPetColorId("0.0");
        adoptionListParameter.setPetBreedId("0.0");

        AdoptionListHeader adoptionListHeader = new AdoptionListHeader();
        adoptionListHeader.setPageNumber(pageNumber);
        adoptionListHeader.setPageSize(pageSize);
        adoptionListHeader.setSearchData("");

        AdoptionListRequestModel adoptionListRequestModel = new AdoptionListRequestModel();
        adoptionListRequestModel.setData(adoptionListParameter);
        adoptionListRequestModel.setHeader(adoptionListHeader);

        ApiService<AdoptionListResponse> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getAdoptionList(Config.token, adoptionListRequestModel), "getAdoptionList");
        Log.e("DIOLOG====>", "" + adoptionListRequestModel);
    }


    @Override
    public void onResponse(Response arg0, String key) {
        switch (key) {
            case "getAdoptionList":
                try {
                    Log.d("getAdoptionList", arg0.body().toString());
                    AdoptionListResponse adoptionListResponse = (AdoptionListResponse) arg0.body();
                    int responseCode = Integer.parseInt(adoptionListResponse.getResponse().getResponseCode());
                    if (responseCode == 109) {
                        adoption_RV.setLayoutManager(new GridLayoutManager(this, 2));
                        adoptionListAdopter = new AdoptionListAdopter(this, adoptionListResponse.getData().getPetDonationList(), this);
                        adoption_RV.setAdapter(adoptionListAdopter);
                        adoptionListAdopter.notifyDataSetChanged();
                        petDonationLists = adoptionListResponse.getData().getPetDonationList();
                        progressBar.setVisibility(View.GONE);

                    } else if (responseCode == 614) {
                        Toast.makeText(this, adoptionListResponse.getResponse().getResponseMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "GetPetTypes":
                try {
                    Log.d("GetPetTypes", arg0.body().toString());
                    PetTypeResponse petTypeResponse = (PetTypeResponse) arg0.body();
                    int responseCode = Integer.parseInt(petTypeResponse.getResponse().getResponseCode());
                    if (responseCode == 109) {
                        petTypeList = new ArrayList<>();
                        petTypeList.add("Select Pet Type");
                        petTypeHashMap = new HashMap<>();
                        petTypeHashMap.put("Select Pet Type", "0");
                        Log.d("lalal", "" + petTypeResponse.getData().size());
                        for (int i = 0; i < petTypeResponse.getData().size(); i++) {
                            Log.d("petttt", "" + petTypeResponse.getData().get(i).getPetType1());
                            petTypeList.add(petTypeResponse.getData().get(i).getPetType1());
                            petTypeHashMap.put(petTypeResponse.getData().get(i).getPetType1(), petTypeResponse.getData().get(i).getId());
                        }
                        setPetTypeSpinner();

                    } else if (responseCode == 614) {
                        Toast.makeText(this, petTypeResponse.getResponse().getResponseMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "GetPetBreed":
                try {
                    Log.d("GetPetBreed", arg0.body().toString());
                    BreedCatRespose breedCatRespose = (BreedCatRespose) arg0.body();
                    int responseCode = Integer.parseInt(breedCatRespose.getResponse().getResponseCode());
                    if (responseCode == 109) {
                        petBreedList = new ArrayList<>();
                        petBreedList.add("Pet Breed");
                        petBreedHashMap = new HashMap<>();
                        petBreedHashMap.put("Pet Breed", "0");
                        Log.d("lalal", "" + breedCatRespose.getData().size());
                        for (int i = 0; i < breedCatRespose.getData().size(); i++) {
                            Log.d("petttt", "" + breedCatRespose.getData().get(i).getBreed());
                            petBreedList.add(breedCatRespose.getData().get(i).getBreed());
                            petBreedHashMap.put(breedCatRespose.getData().get(i).getBreed(), breedCatRespose.getData().get(i).getId());
                        }
                        setPetBreeSpinner();
                    } else if (responseCode == 614) {
                        Toast.makeText(this, breedCatRespose.getResponse().getResponseMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "GetPetAge":
                try {
                    Log.d("GetPetAge", arg0.body().toString());
                    PetAgeValueResponse petAgeValueResponse = (PetAgeValueResponse) arg0.body();
                    int responseCode = Integer.parseInt(petAgeValueResponse.getResponse().getResponseCode());
                    if (responseCode == 109) {
                        petAgeList = new ArrayList<>();
                        petAgeList.add("Select Pet Age");
                        petAgeHashMap = new HashMap<>();
                        petAgeHashMap.put("0", "Select Pet Age");
                        Log.d("lalal", "" + petAgeValueResponse.getData().size());
                        for (int i = 0; i < petAgeValueResponse.getData().size(); i++) {
                            Log.d("petttt", "" + petAgeValueResponse.getData().get(i).getAge());
                            petAgeList.add(petAgeValueResponse.getData().get(i).getAge());
                            petAgeHashMap.put(petAgeValueResponse.getData().get(i).getAge(), petAgeValueResponse.getData().get(i).getId());
                        }
                        setPetAgeSpinner();
                    } else if (responseCode == 614) {
                        Toast.makeText(this, petAgeValueResponse.getResponse().getResponseMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "GetPetColor":
                try {
                    Log.d("GetPetColor", arg0.body().toString());
                    PetColorValueResponse petColorValueResponse = (PetColorValueResponse) arg0.body();
                    int responseCode = Integer.parseInt(petColorValueResponse.getResponse().getResponseCode());
                    if (responseCode == 109) {
                        petColorList = new ArrayList<>();
                        petColorList.add("Pet Color");
                        petColorHashMap = new HashMap<>();
                        petColorHashMap.put("0", "Pet Color");
                        Log.d("lalal", "" + petColorValueResponse.getData().size());
                        for (int i = 0; i < petColorValueResponse.getData().size(); i++) {
                            Log.d("petttt", "" + petColorValueResponse.getData().get(i).getColor());
                            petColorList.add(petColorValueResponse.getData().get(i).getColor());
                            petColorHashMap.put(petColorValueResponse.getData().get(i).getColor(), petColorValueResponse.getData().get(i).getId());
                        }
                        setPetColorSpinner();
                    } else if (responseCode == 614) {
                        Toast.makeText(this, petColorValueResponse.getResponse().getResponseMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "GetPetSize":
                try {
                    Log.d("GetPetSize", arg0.body().toString());
                    PetSizeValueResponse petSizeValueResponse = (PetSizeValueResponse) arg0.body();
                    int responseCode = Integer.parseInt(petSizeValueResponse.getResponse().getResponseCode());
                    if (responseCode == 109) {
                        petSizeList = new ArrayList<>();
                        petSizeList.add("Pet Size");
                        petSizeHashMap = new HashMap<>();
                        petSizeHashMap.put("0", "Pet Size");
                        Log.d("lalal", "" + petSizeValueResponse.getData().size());
                        for (int i = 0; i < petSizeValueResponse.getData().size(); i++) {
                            Log.d("petttt", "" + petSizeValueResponse.getData().get(i).getSize());
                            petSizeList.add(petSizeValueResponse.getData().get(i).getSize());
                            petSizeHashMap.put(petSizeValueResponse.getData().get(i).getSize(), petSizeValueResponse.getData().get(i).getId());
                        }
                        setPetSizeSpinner();
                    } else if (responseCode == 614) {
                        Toast.makeText(this, petSizeValueResponse.getResponse().getResponseMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }


    }

    @Override
    public void onError(Throwable t, String key) {

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(AdoptPetActivity.this, AdoptionPetDetailsActivity.class);
        intent.putExtra("image", petDonationLists.get(position).getPetImageList().get(0).getPetImageUrl());
        intent.putExtra("pet_id", petDonationLists.get(position).getId());
        intent.putExtra("pet_name", petDonationLists.get(position).getPetName());
        intent.putExtra("pet_gender", petDonationLists.get(position).getPetCategory());
        intent.putExtra("pet_age", petDonationLists.get(position).getPetAge());
        intent.putExtra("pet_breed", petDonationLists.get(position).getPetBreed());
        intent.putExtra("pet_color", petDonationLists.get(position).getPetColor());
        intent.putExtra("pet_size", petDonationLists.get(position).getPetSize());
        intent.putExtra("donar_name", petDonationLists.get(position).getDonarName());
        intent.putExtra("donar_phone", petDonationLists.get(position).getPhoneNumber());
        intent.putExtra("donar_mail", "");
        intent.putExtra("donar_address", petDonationLists.get(position).getAddress());
        intent.putExtra("from", "AdoptPetActivity");

        startActivity(intent);

    }
    private void petType() {
        ApiService<PetTypeResponse> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().petTypeApi(), "GetPetTypes");
    }

    private void getPetBreed() {
        BreedRequest breedRequest = new BreedRequest();
        breedRequest.setGetAll("false");
        if (!getStrSpnerItemPetNmId.equals("0"))
            breedRequest.setPetCategoryId(getStrSpnerItemPetNmId);
        else
            breedRequest.setPetCategoryId("1");
        BreedParams breedParams = new BreedParams();
        breedParams.setData(breedRequest);

        ApiService<BreedCatRespose> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getGetPetBreedApi(breedParams), "GetPetBreed");
        Log.d("Diolog_Breed", "===>" + breedParams);
    }

    private void getPetAge() {
        BreedRequest breedRequest = new BreedRequest();
        breedRequest.setGetAll("false");
        if (!getStrSpnerItemPetNmId.equals("0"))
            breedRequest.setPetCategoryId(getStrSpnerItemPetNmId);
        else
            breedRequest.setPetCategoryId("1");
        BreedParams breedParams = new BreedParams();
        breedParams.setData(breedRequest);

        ApiService<PetAgeValueResponse> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getGetPetAgeApi(breedParams), "GetPetAge");
    }

    private void getPetColor() {
        BreedRequest breedRequest = new BreedRequest();
        breedRequest.setGetAll("false");
        if (!getStrSpnerItemPetNmId.equals("0"))
            breedRequest.setPetCategoryId(getStrSpnerItemPetNmId);
        else
            breedRequest.setPetCategoryId("1");
        BreedParams breedParams = new BreedParams();
        breedParams.setData(breedRequest);

        ApiService<PetColorValueResponse> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getGetPetColorApi(breedParams), "GetPetColor");
    }

    private void getPetSize() {
        BreedRequest breedRequest = new BreedRequest();
        breedRequest.setGetAll("false");
        if (!getStrSpnerItemPetNmId.equals("0"))
            breedRequest.setPetCategoryId(getStrSpnerItemPetNmId);
        else
            breedRequest.setPetCategoryId("1");
        BreedParams breedParams = new BreedParams();
        breedParams.setData(breedRequest);

        ApiService<PetSizeValueResponse> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getGetPetSizeApi(breedParams), "GetPetSize");
    }

    private void getState() {
        ApiService<StateResponse> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getState(), "getState");
    }

    private void getCity() {
        ApiService<CityResponseModel> service = new ApiService<>();
        service.get(this, ApiClient.getApiInterface().getCity(), "GetCity");
    }

    private void setPetTypeSpinner() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, petTypeList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        petType.setAdapter(aa);
        petType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                strSpnerItemPetNm = item;
                Log.d("spnerType", "" + strSpnerItemPetNm);
                getStrSpnerItemPetNmId = petTypeHashMap.get(strSpnerItemPetNm);
                if (!getStrSpnerItemPetNmId.equals("0")) {
                    getAdoptionList(getStrSpnerItemPetNmId);
                    getPetBreed();
                    getPetAge();
                    getPetColor();
                    getPetSize();
                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setPetBreeSpinner() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, petBreedList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        petBreed.setAdapter(aa);
        petBreed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                strSpnrBreed = item;
                Log.d("spnerType", "" + strSpnrBreed);
                strSpnrBreedId = petBreedHashMap.get(strSpnrBreed);
                getAdoptionList(getStrSpnerItemPetNmId);

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setPetAgeSpinner() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, petAgeList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        petAge.setAdapter(aa);
        petAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                strSpnrAge = item;
                Log.d("spnerType", "" + strSpnrAge);
                strSpnrAgeId = petAgeHashMap.get(strSpnrAge);
                getAdoptionList(getStrSpnerItemPetNmId);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setPetColorSpinner() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, petColorList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        petColor.setAdapter(aa);
        petColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                strSpnrColor = item;
                Log.d("spnerType", "" + strSpnrColor);
                strSpnrColorId = petColorHashMap.get(strSpnrColor);
                getAdoptionList(getStrSpnerItemPetNmId);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setPetSizeSpinner() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, petSizeList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        petSize.setAdapter(aa);
        petSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                strSpnrSize = item;
                Log.d("spnerType", "" + strSpnrSize);
                strSpneSizeId = petSizeHashMap.get(strSpnrSize);
                getAdoptionList(getStrSpnerItemPetNmId);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSpinnerPetSex() {
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, petSexList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        petGender.setAdapter(aa);
        petGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                strSpnrSex = item;
                Log.d("spnerType", "" + strSpnrSex);
                strSpnrSexId = petSexHashMap.get(strSpnrSex);
                getAdoptionList(getStrSpnerItemPetNmId);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}