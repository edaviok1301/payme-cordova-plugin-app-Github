package sdkpayme;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
/*import android.support.v7.app.AppCompatActivity;*/

import com.alignet.api.payme.bean.*;
import com.alignet.api.payme.util.Constants;
import com.alignet.api.payme.wallet.models.EnvDomain;
import com.alignet.api.payme.wallet.pay.activities.PayActivity;

import java.util.Random;

public class GateWay extends Activity {

    private static final String TAG = "GateWay";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
    	super.onCreate(savedInstanceState);
    	send();
    }

    private void send(){
        Log.d(TAG, "send");

    	Random random = new Random();
        String generatedPassword = String.format("%04d", random.nextInt(10000));
        
    	final String text_currency_code = "PEN";
        final String text_currency_symbol = "S/";
        final String text_number = generatedPassword;
        final String text_amount =  "99.55";
        final String text_product_description = "Televisor";
        final String text_locale = "es_PE";
        final String text_user = "jperez";
        final String text_plan_quota = "1";
        final String spinner_brands = "VISA,MSCD,AMEX,DINC";
        final String[] brandsArray = spinner_brands.split(",");
        final String signatureKey = "SYbpPEwdKxErVzt@66458456";
        final String text_merchant = "8202";

        PersonaData person = new PersonaData("Javier",
                "Perez", "jperez@alignet.com",
                "Av casimiro Ulloa 333", "000", "Lima", "Lima",
                "PE", "998888444"
        );
        CurrencyData currency = new CurrencyData(text_currency_code, text_currency_symbol);
        OperationData operationData = new OperationData(
                text_number, text_amount,
                text_product_description, currency);

        MerchantData merchantData = new MerchantData(operationData, person, person, person, signatureKey);
        SettingsData settingsData = new SettingsData(text_locale, text_merchant, brandsArray);
        FeatureWalletData featureWallet = new FeatureWalletData(text_user);

        FeaturedReservedData reservedData[] = {
                new FeaturedReservedData("reserved1","1"),
                new FeaturedReservedData("reserved2","2"),
                new FeaturedReservedData("reserved3","3")};

        Boolean planQuota = text_plan_quota.equals("1");

        FeaturesData featuresData = new FeaturesData(featureWallet, reservedData, planQuota);
        MerchantOperationData merchantOperation = new MerchantOperationData(
                merchantData, settingsData, featuresData);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_MERCHANT_ENVDOMAIN, EnvDomain.DEVELOPMENT.getUrl() );
        bundle.putParcelable(Constants.EXTRA_MERCHANT_OPERATION, merchantOperation);
        bundle.putBoolean(Constants.EXTRA_ENABLED_MPOS, false);
        Intent intent = new Intent(this, PayActivity.class);
        intent.putExtra(Constants.PAYME_BUNDLE,bundle);


        startActivityForResult(intent, Constants.REQUEST_CODE_PAYME, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == GateWay.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_PAYME) {
                Log.d(TAG, "onActivityResult");
                Bundle responseBundle = data.getBundleExtra(Constants.PAYME_RESPONSE_BUNDLE);
                PaymentData parcelableResponse = responseBundle.getParcelable(Constants.EXTRA_PAYMENT);
            	Toast.makeText(GateWay.this,"GateWay: "+parcelableResponse,Toast.LENGTH_LONG).show();
                Log.d("RESULT", parcelableResponse.toString());
                Intent returnIntent =new Intent();
                returnIntent.putExtra("result","OK");
                setResult(Activity.RESULT_OK,returnIntent);
                //finish();
            }
        }
    }
}