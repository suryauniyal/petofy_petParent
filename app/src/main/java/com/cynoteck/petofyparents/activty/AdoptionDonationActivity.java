package com.cynoteck.petofyparents.activty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cynoteck.petofyparents.PetParentSingleton;
import com.cynoteck.petofyparents.R;
import com.cynoteck.petofyparents.api.ApiClient;
import com.cynoteck.petofyparents.api.ApiResponse;
import com.cynoteck.petofyparents.api.ApiService;
import com.cynoteck.petofyparents.response.getAdoptionRequestListResponse.GetAdoptionRequestListData;
import com.cynoteck.petofyparents.response.getAdoptionRequestListResponse.GetAdoptionRequestListResponse;
import com.cynoteck.petofyparents.response.getDonationRequestResponse.GetDonationRequestData;
import com.cynoteck.petofyparents.response.getDonationRequestResponse.GetDonationRequestResponse;
import com.cynoteck.petofyparents.utils.Config;
import com.cynoteck.petofyparents.utils.Methods;
import com.google.android.material.card.MaterialCardView;

import retrofit2.Response;

import static com.cynoteck.petofyparents.activty.AdoptPetActivity.total_adoption_RL;
import static com.cynoteck.petofyparents.activty.AdoptPetActivity.total_adoption_request_TV;
import static com.cynoteck.petofyparents.activty.DonationActivity.total_donation_RL;
import static com.cynoteck.petofyparents.activty.DonationActivity.total_donation_request_TV;

public class AdoptionDonationActivity extends AppCompatActivity implements View.OnClickListener , ApiResponse {

    MaterialCardView back_arrow_CV;
    ImageView adopt_IV, donate_IV;
    Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_donation);
        methods = new Methods(this);
        back_arrow_CV = findViewById(R.id.back_arrow_CV);
        adopt_IV = findViewById(R.id.adopt_IV);
        donate_IV = findViewById(R.id.donate_IV);


        back_arrow_CV.setOnClickListener(this);
        donate_IV.setOnClickListener(this);
        adopt_IV.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow_CV:
                onBackPressed();
                break;

            case R.id.adopt_IV:
                Intent adoptionIntent = new Intent(this, AdoptPetActivity.class);
                startActivity(adoptionIntent);
                break;


            case R.id.donate_IV:
                Intent donationIntent = new Intent(this, DonationActivity.class);
                startActivity(donationIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            ApiService<GetAdoptionRequestListResponse> service = new ApiService<>();
            service.get(this, ApiClient.getApiInterface().getAdoptionRequest(Config.token,"social-service/get-adoption-request-list/0"), "AdoptionRequest");


            ApiService<GetDonationRequestResponse> service1 = new ApiService<>();
            service1.get(this, ApiClient.getApiInterface().getDonationRequest(Config.token,"social-service/get-donation-request-list/0"), "DonationRequest");

    }

    @Override
    public void onResponse(Response response, String key) {

        switch (key){
            case "AdoptionRequest":
                try {
                    PetParentSingleton.getInstance().getGetAdoptionRequestListData().clear();
                    GetAdoptionRequestListResponse getAdoptionRequestListResponse = (GetAdoptionRequestListResponse) response.body();
                    int responseCode = Integer.parseInt(getAdoptionRequestListResponse.getResponse().getResponseCode());
                    if (responseCode == 109) {
                        if (getAdoptionRequestListResponse.getData().size() > 0) {
                            for (int i = 0; i < getAdoptionRequestListResponse.getData().size(); i++) {
                                GetAdoptionRequestListData getAdoptionRequestListData = new GetAdoptionRequestListData();
                                getAdoptionRequestListData.setId(getAdoptionRequestListResponse.getData().get(i).getId());
                                getAdoptionRequestListData.setUserId(getAdoptionRequestListResponse.getData().get(i).getUserId());
                                getAdoptionRequestListData.setRequestDate(getAdoptionRequestListResponse.getData().get(i).getRequestDate());
                                getAdoptionRequestListData.setRequesterName(getAdoptionRequestListResponse.getData().get(i).getRequesterName());
                                getAdoptionRequestListData.setRequestCurrentStatus(getAdoptionRequestListResponse.getData().get(i).getRequestCurrentStatus());
                                getAdoptionRequestListData.setRequestUpdateDate(getAdoptionRequestListResponse.getData().get(i).getRequestUpdateDate());
                                getAdoptionRequestListData.setPet(getAdoptionRequestListResponse.getData().get(i).getPet());

                                PetParentSingleton.getInstance().getGetAdoptionRequestListData().add(getAdoptionRequestListData);
                            }
                            total_adoption_request_TV.setText(String.valueOf(PetParentSingleton.getInstance().getGetAdoptionRequestListData().size()));
                            total_adoption_RL.setEnabled(true);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                break;

            case "DonationRequest":
                try {
                    PetParentSingleton.getInstance().getGetDonationRequestListData().clear();
                    GetDonationRequestResponse getDonationRequestResponse = (GetDonationRequestResponse) response.body();
                    int responseCode = Integer.parseInt(getDonationRequestResponse.getResponse().getResponseCode());
                    if (responseCode == 109) {
                        if (getDonationRequestResponse.getData().size() > 0) {
                            for (int i = 0; i < getDonationRequestResponse.getData().size(); i++) {
                                GetDonationRequestData getDonationRequestData = new GetDonationRequestData();
                                getDonationRequestData.setId(getDonationRequestResponse.getData().get(i).getId());
                                getDonationRequestData.setUserId(getDonationRequestResponse.getData().get(i).getUserId());
                                getDonationRequestData.setRequestDate(getDonationRequestResponse.getData().get(i).getRequestDate());
                                getDonationRequestData.setStatus(getDonationRequestResponse.getData().get(i).getStatus());
                                getDonationRequestData.setRequestUpdateDate(getDonationRequestResponse.getData().get(i).getRequestUpdateDate());
                                getDonationRequestData.setPetName(getDonationRequestResponse.getData().get(i).getPetName());
                                getDonationRequestData.setPetBreed(getDonationRequestResponse.getData().get(i).getPetBreed());

                                PetParentSingleton.getInstance().getGetDonationRequestListData().add(getDonationRequestData);
                            }
                            total_donation_request_TV.setText(String.valueOf(PetParentSingleton.getInstance().getGetDonationRequestListData().size()));
                            total_donation_RL.setEnabled(true);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }

    }

    @Override
    public void onError(Throwable t, String key) {
        Log.e("ERROR",t.getLocalizedMessage());
    }
}