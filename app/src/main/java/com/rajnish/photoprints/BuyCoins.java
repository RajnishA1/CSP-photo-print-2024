package com.rajnish.photoprints;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BuyCoins extends AppCompatActivity {
    BillingClient billingClient;
    List<ProductDetails> productDetailsList;
    Activity activity;
    Handler handler;
    ProgressBar progress_circular;
    ArrayList<String> productIds;
    ArrayList<String> coins;
    int point = 0;
    ListView listView;
    int touchItem;
    ImageView cross_btn;

    UninstallReceiver uninstallReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coins);
        Objects.requireNonNull(getSupportActionBar()).hide();
        initWork();
        SharedPreferences sh = getSharedPreferences("Coins", 0);
        point = sh.getInt("Points", 10);
        cross_btn.setOnClickListener(view -> {
            Intent i = new Intent(BuyCoins.this, MainActivity.class);
            startActivity(i);
        });

        handler = new Handler();

        productIds = new ArrayList<>();
        coins = new ArrayList<>();
        productDetailsList = new ArrayList<>();


        activity = this;



        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {
                            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase: list){
                                    verifyPurchase(purchase);
                                }
                            }
                        }
                ).build();

        uninstallReceiver = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        intentFilter.addDataPath("com.rajnish.photoprints", 0);
        registerReceiver(uninstallReceiver, intentFilter);



        //start the connection after initializing the billing client
        connectGooglePlayBilling();



    }

    void connectGooglePlayBilling() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectGooglePlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    showProducts();
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")

    void showProducts() {

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("0_csp_photo_print")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                //Product 2
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("1_csp_photo_print")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("rj")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, list) -> {
            //Clear the list
            productDetailsList.clear();


            //Handler to delay by two seconds to wait for google play to return the list of products.
            handler.postDelayed(() -> {
                //Adding new productList, returned from google play
                productDetailsList.addAll(list);

                for (int i = 0; i < list.size(); i++) {
                    ProductDetails productDetails = list.get(i);
                    String price = Objects.requireNonNull(productDetails.getOneTimePurchaseOfferDetails()).getFormattedPrice();
                    String productName = productDetails.getName();
                    coins.add(price);
                    productIds.add(productName);


                }

                progress_circular.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                ProductListAdapter adapter = new ProductListAdapter(BuyCoins.this, productIds, coins);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((adapterView, view, i, l) -> {

                    BuyCoins.this.launchPurchaseFlow(productDetailsList.get(i));
                    touchItem = i;

                });

            }, 2000);
        });
    }

    void launchPurchaseFlow(ProductDetails productDetails) {
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();
        billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    void verifyPurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        ConsumeResponseListener listener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                giveUserCoins(purchase);
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifyPurchase(purchase);
                            }
                        }
                    }
                }
        );
    }


    void giveUserCoins(Purchase purchase) {

        if (touchItem == 0) {
            CoinCountUpdate(purchase,100);
        }
        if (touchItem == 1) {
            CoinCountUpdate(purchase,250);
        }
        if (touchItem == 2) {
            CoinCountUpdate(purchase,500);

        }
    }

    void  CoinCountUpdate(Purchase purchase ,int noOfCoins){
        SharedPreferences sharedPreferences = getSharedPreferences("Coins", 0);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        int n = purchase.getQuantity() * noOfCoins;
        int newPoint = point + n;
        myEdit.putInt("Points", newPoint);
        myEdit.putBoolean("isPurchasePoint", true);
        myEdit.apply();
        Toast.makeText(activity, "Coin granted", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(BuyCoins.this, MainActivity.class);
        startActivity(i);
    }

    private void initWork() {
        cross_btn = findViewById(R.id.cross_btn);
        listView = findViewById(R.id.listView);
        progress_circular = findViewById(R.id.progress_circular);
        listView.setVisibility(View.GONE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (uninstallReceiver != null) {
            unregisterReceiver(uninstallReceiver);
        }
    }


}