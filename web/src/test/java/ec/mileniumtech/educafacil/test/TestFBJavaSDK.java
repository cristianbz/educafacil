package ec.mileniumtech.educafacil.test;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.Campaign;

public class TestFBJavaSDK
{
    public static final APIContext context = new APIContext(
            "cc596fb71146a67d18379177b3602136",
            "f8087dea5350517131679c5f8a834576"
    );
    public static void main(String[] args)
    {
        AdAccount account = new AdAccount("1808113906281839", context);
        try {
            APINodeList<Campaign> campaigns = account.getCampaigns().requestAllFields().execute();
            for(Campaign campaign : campaigns) {
                System.out.println(campaign.getFieldName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}
