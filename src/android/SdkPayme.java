package sdkpayme;

import android.util.Log;
import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
    private Context context=null;
    private CallbackContext callbackContext = null;
    String text_amount =  "";

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action,final JSONArray args, CallbackContext newcallbackContext) throws JSONException {
        callbackContext=newcallbackContext;
        Log.d(TAG, "execute plugin");
        if (action.equals("coolMethod")) {            
            Log.d(TAG,"Into Coolmethod of If");
            this.cordova.getActivity().runOnUiThread(new Runnable(){
                public void run(){
                    try{
                        Log.d(TAG,"Into runOnUiThread");
                        JSONObject Jobject = new JSONObject(args.getString(0));
                        coolMethod(Jobject);
                    }catch(Exception e){
                        callbackContext.error(e.getMessage());
                        Log.d(TAG,"Error"+e.getMessage());
                    }
                    
                }
            });
            return true;
        }
        PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        return false;
    }

    private final void coolMethod(final JSONObject params) throws JSONException{
        Log.d(TAG,"Into coolMethod method");

        final String text_currency_code ="604"; //params.getString("code");
        final String text_currency_symbol ="S/"; //params.getString("symbol");

        final String text_number ="98765432"; //params.getString("operationNumber");//decrypt(params.getString("operationNumber"));
        
        text_amount="10.55"; //params.getString("amount");
        final String text_product_description = "Recargas"; //params.getString("productDescription");
        final String text_locale = "es_PE"; //params.getString("locale");
        final String text_user ="1234"; //params.getString("userCommerce");
        final String text_plan_quota = params.getString("planQuota");
        final String spinner_brands = "VISA,MSCD,AMEX,DINC"; //params.getString("brands");
        final String[] brandsArray = spinner_brands.split(",");

        final String signatureKey = params.getString("signatureKey");//decrypt(params.getString("signatureKey"));
        final String text_merchant ="13517"; params.getString("identifier");//decrypt(params.getString("identifier"));

        final List<String> brands = Arrays.asList(spinner_brands.split(","));

        PaymePersonData paymePersonData = new PaymePersonData("Levis", "Silvestre", "levis.silvestre@alignet.com",
                "Av casimiro Ulloa 333","Av casimiro Ulloa 333", "PE","604","18",
                "Lima", "Lima","51997047941","51997047941","51997047941");

        HashMap<String,String> reservedData = new HashMap<String,String>();
        reservedData.put("reserved1","1");
        reservedData.put("reserved2","2");

        PaymeAuthenticationData paymeAuthenticationData = new PaymeAuthenticationData("01");

        PaymeMerchantData paymeMerchantData = new PaymeMerchantData(new PaymeOperationData("000001","Recargas","10.55",new PaymeCurrencyData("604","S/")),true,paymePersonData,paymePersonData);

        PaymeFeatureData paymeFeatureData = new PaymeFeatureData(reservedData,new PaymeWalletData(true,text_user),new PaymeInstallmentsData(false),paymeAuthenticationData);

        PaymeSettingData paymeSettingData = new PaymeSettingData("es_PE",brands);



        PaymeRequest paymeRequest = new PaymeRequest(paymeMerchantData, paymeFeatureData, paymeSettingData);

        String environment=params.getString("environment");
        PaymeEnvironment paymeEnvironment = PaymeEnvironment.DEVELOPMENT;
        switch (environment){
            case "1":
                Log.d(TAG,"GET PROD");
                paymeEnvironment = PaymeEnvironment.PRODUCTION;
                break;
            case "2":
                Log.d(TAG,"GET DEV");
                paymeEnvironment = PaymeEnvironment.DEVELOPMENT;
                break;
        }

        PaymeClient paymeClient = new PaymeClient(SdkPayme.this,text_merchant);
        paymeClient.setEnvironment(paymeEnvironment);
        String gson = new Gson().toJson(paymeRequest);
        Log.i(TAG+"-request",gson);
        Log.i(TAG+"-merchantId",text_merchant);
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
