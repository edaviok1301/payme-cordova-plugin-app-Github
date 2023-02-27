package sdkpayme;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alignet.payme.PaymeClient;
import com.alignet.payme.PaymeClientDelegate;
import com.alignet.payme.util.PaymeEnvironment;
import com.alignet.payme.model.*;
import com.google.gson.Gson;
/**
 * This class echoes a string called from JavaScript.
 */
public class SdkPayme extends CordovaPlugin implements PaymeClientDelegate {

    private static final String TAG = "SdkPayme";
    private CallbackContext callbackContext = null;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action,final JSONArray args, CallbackContext newcallbackContext){
        callbackContext=newcallbackContext;
        Log.d(TAG, "execute plugin");
        if (action.equals("coolMethod")) {            
            Log.d(TAG,"Into Coolmethod of If");
            this.cordova.getActivity().runOnUiThread(() -> {
                try{
                    Log.d(TAG,"Into runOnUiThread");
                    JSONObject jsonObject = new JSONObject(args.getString(0));
                    coolMethod(jsonObject);
                } catch (Exception e){
                    callbackContext.error(e.getMessage());
                    Log.d(TAG,"Error"+e.getMessage());
                }

            });
            return true;
        }
        PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        return false;
    }

    private void coolMethod(final JSONObject params) throws JSONException{

        Log.d(TAG,"Into coolMethod method");
        launchPayme(params);

    }

    private PaymeRequest setParamsMerchant(JSONObject request)throws JSONException{

        String firstName = request.getString("firstName");
        String lastName = request.getString("lastName");
        String email = request.getString("email");
        String address1 = request.getString("address1");
        String address2 = request.getString("address2");
        String countryCode = request.getString("countryCode");
        String countryNumber = request.getString("countryNumber");
        String zip = request.getString("zip");
        String city = request.getString("city");
        String state = request.getString("state");
        String homePhone = request.getString("homePhone");
        String workPhone = request.getString("workPhone");
        String mobilePhone = request.getString("mobilePhone");

        String currencyCode = request.getString("currencyCode");
        String currencySymbol = request.getString("currencySymbol");

        String operationNumber = request.getString("operationNumber");
        String operationDescription = request.getString("productDescription");
        String amount = request.getString("amount");

        String name = request.getString("name");
        String value = request.getString("value");
        HashMap<String,String> reservedData = new HashMap<>();
        reservedData.put(name,value);
        reservedData.put("reserved2","2");
        reservedData.put("reserved3","3");

        String userCode = request.getString("userCommerce");
        String planQuota = request.getString("planQuota");
        boolean installments = planQuota.equals("1");

        String authentication = request.getString("authentication");

        String locale = request.getString("locale");
        String settingBrands = request.getString("brands");
        List<String> brands = Arrays.asList(settingBrands.split(","));

        PaymePersonData paymePersonData = new PaymePersonData(firstName, lastName, email,
                address1,address2, countryCode,countryNumber,zip,
                city, state,mobilePhone,homePhone,workPhone);

        PaymeCurrencyData paymeCurrencyData = new PaymeCurrencyData(currencyCode,currencySymbol);

        PaymeOperationData paymeOperationData = new PaymeOperationData(operationNumber,operationDescription,amount,paymeCurrencyData);

        PaymeMerchantData paymeMerchantData = new PaymeMerchantData(paymeOperationData,true,paymePersonData,paymePersonData);

        PaymeSettingData paymeSettingData = new PaymeSettingData(locale,brands);

        PaymeWalletData paymeWalletData = new PaymeWalletData(true,userCode);

        PaymeFeatureData paymeFeatureData = new PaymeFeatureData(reservedData,paymeWalletData,new PaymeInstallmentsData(installments),new PaymeAuthenticationData(authentication));

        return new PaymeRequest(paymeMerchantData,paymeFeatureData,paymeSettingData);
    }

    private void launchPayme(JSONObject request) throws JSONException{
        String environment=request.getString("environment");
        PaymeEnvironment paymeEnvironment = (environment.equals("1"))?PaymeEnvironment.PRODUCTION:PaymeEnvironment.DEVELOPMENT;
        switch (paymeEnvironment){
            case PRODUCTION:
                Log.d(TAG,"GET PROD");
                break;
            case DEVELOPMENT:
                Log.d(TAG,"GET DEV");
                break;
        }

        PaymeRequest paymeRequest = setParamsMerchant(request);

        PaymeClient paymeClient = new PaymeClient(SdkPayme.this,request.getString("identifier"));
        paymeClient.setEnvironment(paymeEnvironment);
        String gson = new Gson().toJson(paymeRequest);
        Log.i(TAG+"-request",gson);
        Log.i(TAG+"-merchantId",request.getString("identifier"));
        Log.i(TAG+"-environment",paymeEnvironment.toString());

        paymeClient.authorizeTransaction(cordova.getActivity(), paymeRequest);

    }

    @Override
    public void onNotificate(@NonNull PaymeInternalAction paymeInternalAction) {
        String notificate="NOTIFICATE";
        switch (paymeInternalAction) {
            case PRESS_PAY_BUTTON : {
                Log.d(notificate,"El usuario presionó el boton pagar exitosamente.");
                break;
            }
             case START_SCORING : {
                Log.d(notificate,"Inicia el proceso de evaluación de riesgo.");
                break;
            }
            case END_SCORING : {
                Log.d(notificate,"Termina el proceso de evaluación de riesgo.");
                break;
            }
            case START_TDS : {
                Log.d(notificate,"Inicia el proceso de autenticación.");
                break;
            }
            case END_TDS : {
                Log.d(notificate,"Termina el proceso de autenticación.");
                break;
            }
            case START_AUTHORIZATION : {
                Log.d(notificate, "Se inicia la autorización.");
                break;
            }
            default: {
                Log.d(notificate+"-default", paymeInternalAction.toString());
                break;
            }
        }
    }

    @Override
    public void onRespondsPayme(@NonNull PaymeResponse paymeResponse) {
        String gson = new Gson().toJson(paymeResponse);
        Log.i("OnRespondsPayme",gson);
    }
}
