package com.example.twoscreenapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class PreviewPhoto extends AppCompatActivity {

    private ImageButton sharebtn, shopbtn;
    private Bitmap myBitmap;
    private String fileName;
  /*  private String[] links = new String[40];*/
    private String[] links =
          {       "https://www.smartbuyglasses.com.sg/designer-eyeglasses/Dior/Dior-DIOR-STELLAIREO-1-010-410364.html?feed=sg&gclid=Cj0KCQiAk53-BRD0ARIsAJuNhpu8RnL2D5598Fe-OtYiSrHG3ziHywR3TFJf9RXnXmByWbqtvQ9v0vEaArwjEALw_wcB&gclsrc=aw.ds",
                  "https://www.eyebuydirect.com/eyeglasses/frames/milo-mint-l-21284",
                  "https://www.coolframes.com/glasses/silhouette-rimless-eyewear/lite-spirit-full-rim-2925-eyeglasses.html",
                  "https://eu.lindafarrow.com/products/joanna-oversized-optical-frame-in-white-gold-and-silver-996",
                  "https://www.coastal.com/glasses/joseph-marc/joseph-marc-norton-4143?v=cypress",
                  "https://frontierfashion.com/products/m10641-clr",
                  "https://www.gentlemonster.com/shop/ver1_detail.php?it_id=1528954072&cata=i0",
                  "https://sunglass.la/collections/womens-collection/products/classic-metal-rectangle-eyeglasses-slim-arms-clear-lens-52mm",
                  "https://www.blickers.com/en/glasses/nike-nike-4287-c51/5040629267038208/?gclid=Cj0KCQiAk53-BRD0ARIsAJuNhpuK3_IRg0fE8QCtFD802AF-UcZczoyG9e3h2GpVZ-Nwk_yPDGHtOHUaAjc7EALw_wcB&fc=sg",
                  "https://www.zennioptical.com/p/womens-stainless-steel-rectangle-eyeglass-frames/3287?skuId=328717",
                  "https://www.coastal.com/glasses/kam-dhillon/kam-dhillon-rockaway-51?v=blue",
                  "https://www.smartbuyglasses.com.sg/designer-eyeglasses/Guess/Guess-GU-3009-095-494518.html?feed=sg&gclid=Cj0KCQiAk53-BRD0ARIsAJuNhptvmaO25qyt3Mge7Rimf95kGbtxZI4KETRkANWonUWv0C4qlrFrLKMaAsSMEALw_wcB&gclsrc=aw.ds",
                  "https://marveloptics.com/shop/eyeglasses/rectangle-eyeglasses/",
                  "https://www.zennioptical.com/p/stylish-plastic-full-rim-frame-same-appearance-as-frame-3371/8071?skuId=807117",
                  "https://optimaloptic.com/prescription-glasses/gucci-logo-gg0094o-006.html?SubmitCurrency=1&id_currency=24&gclid=Cj0KCQiAk53-BRD0ARIsAJuNhptB8gmzgbHaiJ-OoHW20gRE-nLp9dmwYOfkQnsmSmWOshlVRv-yTLoaAjkKEALw_wcB",
                  "https://www.revolve.com/gucci-logo-wayfarer-in-shiny-black-grey/dp/GUCR-WA57/?d=F&currency=SGD&countrycode=SG&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WHyLMPOzCsCxPUAMFj2cXnX52ITTal-i03WzSV6-sCu7xEtQZroH2QaAtBrEALw_wcB&gclsrc=aw.ds&product=GUCR-WA57&bneEl=false&",
                  "https://www.asos.com/asos-design/asos-design-mini-rectangle-sunglasses-in-black-with-blue-lens/prd/20743486?affid=25876&_cclid=Google_Cj0KCQiAtqL-BRC0ARIsAF4K3WHa8ioQT4u5TLXdu400CFE1z-wDWswZfbrHF-pWdaM4GkjRzL8icOEaAqL7EALw_wcB&channelref=product+search&mk=abc&ppcadref=11153290942%7C108166580134%7Cpla-293946777986&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WHa8ioQT4u5TLXdu400CFE1z-wDWswZfbrHF-pWdaM4GkjRzL8icOEaAqL7EALw_wcB&gclsrc=aw.ds",
                  "https://www.jomashop.com/tommy-hilfiger-mens-blue-rectangular-sunglasses-th1254-s04jwdk54.html",
                  "https://www.asos.com/monokel-eyewear/monokel-eyewear-robotnik-unisex-square-sunglasses-in-dark-green/prd/22224133?affid=25876&_cclid=Google_Cj0KCQiAtqL-BRC0ARIsAF4K3WFiC33016BIH9s_xHq0TIHBZMOqTD5RO1mCI9Ra2ybFqr-qGsfZ1BQaAmJeEALw_wcB&channelref=product+search&mk=abc&ppcadref=11153290942%7C108166580134%7Cpla-293946777986&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WFiC33016BIH9s_xHq0TIHBZMOqTD5RO1mCI9Ra2ybFqr-qGsfZ1BQaAmJeEALw_wcB&gclsrc=aw.ds",
                  "https://www.blickers.com/en/glasses/tom-ford-ft0646-marco-02-c53/4816701441441792/?gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WEvsdTAg3nUR8jSyID91yjMoK8wTja5eWjjse3vsBEQNHrYsYVuEFoaAtNGEALw_wcB&fc=sg",
                  "https://www.amazon.co.uk/Inlefen-Glasses-Vintage-Circle-Sunglasses/dp/B07CQTH3WR",
                  "https://www.eyebuydirect.com/eyeglasses/frames/etched-yellow-m-19973",
                  "https://www.zalora.sg/ray-ban-aviator-large-metal-ii-rb3026-sunglasses-170375.html",
                  "https://www.asos.com/hot-futures/hot-futures-slim-oval-retro-sunglasses-in-red-with-arm-logo/prd/20121624?affid=25876&_cclid=Google_Cj0KCQiAtqL-BRC0ARIsAF4K3WF4dPLuxOCvrZFMekaAnlDt1X7kRIp1BHE4U-qUT-U01O85L--zlYkaAg74EALw_wcB&channelref=product+search&mk=abc&ppcadref=11153290942%7C108166580134%7Cpla-293946777986&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WF4dPLuxOCvrZFMekaAnlDt1X7kRIp1BHE4U-qUT-U01O85L--zlYkaAg74EALw_wcB&gclsrc=aw.ds",
                  "https://www.smartbuyglasses.com.sg/designer-eyeglasses/Italia-Independent/Italia-Independent-I-I-MOD-5629-NEW-I-THIN-051.000-473485.html?feed=sg&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WHO7mXxKF-bTLfTbGYwMgkduxfa7Kik9brq6dKkNkhQG3NetJ1VPxoaApxXEALw_wcB&gclsrc=aw.ds",
                  "https://www.ray-ban.com/usa/sunglasses/RB2132%20UNISEX%20001-new%20wayfarer%20color%20mix-red/8056597140171",
                  "https://www.smartbuyglasses.com.sg/designer-sunglasses/Balenciaga/Balenciaga-BB0046S-004-500290.html?feed=sg&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WF0g66hkpzUw5K-o4u11nZq90redjm0-PBSLUH5GCgvI4A8zmptu7AaAreDEALw_wcB&gclsrc=aw.ds",
                  "https://www.jomashop.com/gucci-grey-rectangular-mens-sunglasses-gg0516s-004-52.html?&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WFxH_gcLRGgADJbo0x2TXmoXX-Vpl8YkL6sRUTwZqfGxa8cjN9C61waAmUGEALw_wcB",
                  "https://www.zennioptical.com/p/other-plastic-square-eyeglass-frames/1241?skuId=124122",
                  "https://www.smartbuyglasses.com.sg/designer-sunglasses/Ray-Ban/Ray-Ban-RB4368-652580-539192.html?feed=sg&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WFxzjHb--nnHgY8MEC_eTs0ysMuQKxMKF8_TM8X4VCCmzdYxvNlBUwaAvNSEALw_wcB&gclsrc=aw.ds",
                  "https://www.farfetch.com/sg/shopping/women/tom-ford-eyewear-square-frame-oversized-glasses-item-15449216.aspx?size=31&storeid=9945&utm_source=google&utm_medium=cpc&utm_keywordid=13896967&utm_shoppingproductid=15449216-5352&pid=google_search&af_channel=Search&c=807025567&af_c_id=807025567&af_siteid=&af_keywords=pla-297368499494&af_adset_id=47428751172&af_ad_id=191711447965&af_sub1=13896967&af_sub5=15449216-5352&is_retargeting=true&shopping=yes&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WGL5CBGktZKbzED4MhJzBHZQMw-0GBeAO8D2hzMNcOhW2sV7jYZHjQaAjBbEALw_wcB",
                  "https://www.jomashop.com/ray-ban-sunglasses-rb2185-12993m-52.html?&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WG6G0JKc8m2rCZiy762hAjeCjtftmryVfPMGrqBWgoEO2_QfP7QI9gaAkPSEALw_wcB",
                  "https://www.ray-ban.com/uk/sunglasses/RB2132%20UNISEX%20045-new%20wayfarer%20online%20exclusive-blue/8053672517392",
                  "https://www.newlook.com/row/mens/accessories/red-lens-round-metal-frame-sunglasses/p/594339560",
                  "https://www.zalora.sg/ray-ban-aviator-large-metal-ii-rb3026-sunglasses-170376.html",
                  "https://www.zalora.sg/ray-ban-aviator-large-metal-rb3025-polarized-sunglasses-170356.html",
                  "https://www.jomashop.com/fendi-sunglasses-ff-0248-s-1ed-xr-53.html",
                  "https://www.zalora.sg/ray-ban-justin-rb4165-sunglasses-303228.html",
                  "https://www.zalora.sg/ray-ban-aviator-large-metal-rb3025-sunglasses-170354.html",
                  "https://www.versace.com/international/en/versace-virtus-cat-eye-sunglasses-onul/O4383-O52807356_RTU_TU_ONUL__.html?glCountry=SG&lgw_code=35790-O4383-O52807356_RTU_TU_ONUL__&wt_mc=sg.shopping.google.link.shopping&s_kwcid=AL!6089!3!468068516107!!!g!345731462791!&gclid=Cj0KCQiAtqL-BRC0ARIsAF4K3WF6FsAD_j4rdlCvZo7RMK-I82_l5FctL269tQNj5XVnhM6XvdT_6DQaAg-eEALw_wcB"
  };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_page);

        sharebtn = findViewById(R.id.share);
        // Assign the button to the share btn on the xml then set listener
        sharebtn.setOnClickListener(shareOnClickListener);

        shopbtn = findViewById(R.id.shop);
        // Assign the button to the share btn on the xml then set listener
        shopbtn.setOnClickListener(shopOnClickListener);


        Intent intent = getIntent();
        //get the message of the intent
        fileName = intent.getStringExtra(CameraPage.fileNameMsg);
        File imgFile = new File(fileName);
        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = findViewById(R.id.previewPhoto);
            //Place the image
            myImage.setImageBitmap(myBitmap);
        }
    }

    public void back(View view){
        Intent intent = new Intent(this, CameraPage.class);
        startActivity(intent);
    }

    public void download(View view){
        Toast.makeText(getApplicationContext(), "Photo Saved!", Toast.LENGTH_LONG).show();
    }

    private View.OnClickListener shareOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareButtonClicked();
        }
    };

    private void shareButtonClicked() {
        Intent i = new Intent(Intent.ACTION_SEND);

        i.setType("image/*");

        File imgFile1 = new  File(fileName);
        // the code to try to share the image
        if(imgFile1.exists()) {
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imgFile1));
            try {
                startActivity(Intent.createChooser(i, "My Profile ..."));
            } catch (android.content.ActivityNotFoundException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }
        }
    }

    private View.OnClickListener shopOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shopButtonClicked();
        }
    };

    private void shopButtonClicked() {
        if (CameraPage.model_shopcart.equals("")){
            Toast.makeText(getApplicationContext(), "No Model selected!", Toast.LENGTH_LONG).show();
        }
        else{
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url;
            url = links[Integer.parseInt(CameraPage.model_shopcart)-1];
            i.setData(Uri.parse(url));
            startActivity(i);
        }

    }
}