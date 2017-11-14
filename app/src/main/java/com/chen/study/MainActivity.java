package com.chen.study;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.chen.study.pluginRes.PluginActivityManager;
import com.chen.study.pluginRes.ResourceBean;
import com.chen.study.pluginRes.ResourceManager;
import com.chen.study.util.HookerPackageManager;
import com.chen.study.util.InputSimulator;
import com.chen.study.util.LogUtil;

import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private Button downLoadBtn;
    private ImageView imageView;
    private PluginActivityManager pluginActivityManager = PluginActivityManager.getInstance();
    private WebView webViewJs;
    private WindowManager manager;

    public void textClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setFitsSystemWindows(true);
//        WindowManager.LayoutParams attrs = getWindow().getAttributes();
//        attrs.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setAttributes(attrs);
//        //取消全屏设置
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.iv_plugin);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Build.FINGERPRINT = ++++++++++++++");
            }
        });
//        ScaleDrawable scaleDrawable = (ScaleDrawable) imageView.getBackground();
//        scaleDrawable.setLevel(2);
        ClipDrawable scaleDrawable = (ClipDrawable) imageView.getDrawable();
        scaleDrawable.setLevel(5000);
        downLoadBtn = (Button) findViewById(R.id.btn_download);
        downLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadUninstalledBundle();
                Log.d("MainActivity", "Build.FINGERPRINT = " + Build.FINGERPRINT);
                Log.d("MainActivity", "Build.MODEL = " + Build.MODEL);
                Log.d("MainActivity", "Build.SERIAL = " + Build.SERIAL);
                Log.d("MainActivity", "Build.MANUFACTURER = " + Build.MANUFACTURER);
                Log.d("MainActivity", "Build.BRAND = " + Build.BRAND);
                Log.d("MainActivity", "Build.DEVICE = " + Build.DEVICE);
                Log.d("MainActivity", "Build.PRODUCT = " + Build.PRODUCT);
                ObjectAnimator.ofFloat(imageView, "translationX", 0, 400).setDuration(300).start();
//                ValueAnimator animator = ValueAnimator.ofInt(0,1).setDuration(400);
//                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        float fraction = animation.getAnimatedFraction();
//                        Log.d("MainActivity", "fraction = " + fraction);
////                        imageView.scrollBy((int)(-100 * fraction), 0);
//                    }
//                });
//                animator.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        DownloadUtils downloadUtils = new DownloadUtils(MainActivity.this);
//                        downloadUtils.downloadAPK("http://cdn.abcdserver.com:8080/ads/apk/42261267-cb82-42cb-bbc0-057b375125fe.apk", "test.apk");
                    }
                }).start();
            }
        });
        HookerPackageManager.hook(this, new HookerPackageManager.OnPkgManagerHooker() {
            @Override
            public void onMethod(String str) {
                Log.d("MainActivity", "Sdk str.str = ++++++++++++++" + str);
            }
        });
        Log.d("MainActivity", "path = " + Environment.getExternalStorageDirectory().getAbsolutePath());
//        ResourceManager.init(this);
//        pluginActivityManager.init(this);
        testWebView();
        String url = "http://download.176.com/pkgs/lo/android/LMCN_v1.52.apk";
        url.lastIndexOf("/");
        url = url.substring(url.lastIndexOf("/") + 1);
        Log.d("MainActivity", "path url = " + url);
        "".toLowerCase();
//        loadApps();
//        Looper.prepare();
//        Thumbnails.of("").size()


        getSignature();

        Log.d("Sdk", " hasSystemFeature = " + getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.type.television"));
    }

    private void getSignature() {
        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(
                    getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo.signatures != null) {
                Log.d("Sdk", "packageInfo.signatures.length = " + packageInfo.signatures.length);
                Log.d("Sdk", "sig:"+packageInfo.signatures[0].toCharsString()+
                        "\nhashcode:"+packageInfo.signatures[0].hashCode());
            }
        } catch (Exception e2) {
        }
    }

    /**
     * 加载未安装APK资源
     *
     *
     */
    public void loadUninstalledBundle() {
        ResourceBean loadResource = ResourceManager.unInstalled().loadResource("/sdcard/start.apk");
        Drawable drawable = ResourceManager.unInstalled().getDrawable(loadResource.packageName, "pic1");
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    /**
     * 加载未安装的APK的Activity
     *
     *
     */
    public void loadUninstalledActivity() {
        PluginActivityManager.getInstance()
                .startActivity("com.example.plugin.MainActivity", "sdcard/StartActivity.apk");
    }

    private void testWebView() {
        int cupCount = Runtime.getRuntime().availableProcessors();
        webViewJs = new WebView(getApplicationContext());
        Log.i("MainActivity", "test = webViewJs density = " +getResources().getDisplayMetrics().density);
        Log.i("MainActivity", "getScaledDoubleTapSlop = " + ViewConfiguration.get(this).getScaledTouchSlop());
        webViewJs.getSettings().setJavaScriptEnabled(true);
        webViewJs.addJavascriptInterface(new JsInterfaceAutoService(), "ForeignSdk");
        webViewJs.getSettings().setDefaultTextEncodingName("UTF-8");
        webViewJs.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewJs.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("MainActivity", "test = onPageFinished" );
                String jsUrl = "http://192.168.1.69/admobJs.js";
                String invoke = "clickJsAd()";
                String js = "var newscript = document.createElement(\"script\");";
                js += "newscript.src=\"" + jsUrl + "\";";
                js += String.format("newscript.onload=function(){%s;};", invoke); // xxx()代表js中某方法
                js += "document.body.appendChild(newscript);";
//                webViewJs.loadUrl("javascript:" + js);
//                String js = "<html><head><meta http-equiv=\"Content_Type\" content=\"text/html;charset=utf-8\"/> <script type=\"text/javascript\">" +
//                        "console.log(\"+++++++++++\");jsFun();function jsFun(){if(ForeignSdk.packageInstallAccessibility()){return;}}" +
//                        "</script></head></html>";
//
//                webViewJs.loadData(js, "text/html;charset=utf-8", "utf-8");
            }
        });

//        webViewJs.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                Log.i("MainActivity", "contentDisposition =" + contentDisposition);
//                Log.i("MainActivity", "url =" + url);
//                Log.i("MainActivity", "userAgent =" + userAgent);
//                Log.i("MainActivity", "mimetype =" + mimetype);
//                Log.i("MainActivity", "contentLength =" + contentLength);
////                Uri uri = Uri.parse(url);
////                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
////                startActivity(intent);
//            }
//        });

        manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST,WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        params.format=1;
        params.flags=40;
        params.x = 0;//窗口位置的偏移量
        params.y = 0;

        params.alpha = 1.0f;//窗口的透明度
//        manager.addView(webViewJs, params);

//        webViewJs.loadUrl("https://lm.176.com/lp3/?and=1&type=wezonet&key=1&gclid=CPWwn5HThtcCFc2bvQodV08NYA");

        //http://download.176.com/pkgs/lo/android/LMCN_v1.51.apk
        //http://www.boomface.com/dw/igg10/igg10.htm?gclid=CKu30LSa_NYCFZAEKgodbgQJSQ
        //"https://www.googleadservices.com/pagead/aclk?fbs_aeid=-2387312835102244864&sa=L&ai=Cp4LyIWfpWeqHHYPh2ATY9LXoD8H2grRNp4Ls4YsG4Lfojs8IEAEgueiQSmCd6dWBuAWgAbK5844DqQKgeEGK1lSEPqgDAaoEigFP0IVyTMDYN_TpZnaeQAfnYS3itnepkjZbHp2FVwinTSeK0aPDMBOJ9Oqlgo-DnnIP75buJwYAmPXF89t5lYbU3Gdldh1BNz_xiT4RH6TfsQWiU7_RTxFbnXK7CzQAJN--W4IQqfSat1XOuKcEGJEFIiOL-1nPN7YnrvaX2vZI5DcNmgsJTuxtIR_ABJ2MgoalAYgFjf_4tAOQBgGgBgKAB7bGjHGYBwGoB6a-G9gHAaAIpIipBLAIAtIIBwiAYRABGAKxCbmfwETEH3EMghQcGhptb2JpbGVhcHA6OjItY29tLm1qLnNjcmVlbg&num=1&cid=CAASUeRoL0BmwRlG1Y0scwiiFeqXyRNYYQxQHKYwNiplADUIKyDWyBgeey-z0F0HH_Q3uefGbxIEvnabDHhzvx_tvAxzuxFJs7ZDWLlcRK1wtymDmw&sig=AOD64_18aUjBhYJwe7ligSfFxnLDbrQGVQ&ms=CoACQdvxX_atgo2IbFo-VLrqmSuHPLMeS7qmw7Sgcz4nKYvQ3CWzrftwGjwrvcLHXCdILD9o5m_UdklRJYjd-wNhiehdPbJy_1Zlu3yP3DDUEX02iqbQlD9eotfl0Br5autXAlQzKuMpz8FQIkEOfIa3K3FpM9ppetKJhiDitiGKOZjaVnRj_pWcfLJiuwnIN1Xpjz-7CYf9R7EdPZ4kp4UwljFzqjg1PM_CO1howdFwmmGUSIx9LiqGvsGQ8YdbSfOReaUsGRxFkvM-YQMF9C-yNN0HASqCQeniLRlOBPavjJUjwa_aeQN-47DSO-bYB4_Uohfvwcq9U5uosfHpAbWrlhIQ3fWZGrMeUINVoH7InKLT7g&adurl=http://hd.heitao.com/tfdl/gl2/1/1672/10038001&bed=0"

        new Thread().start();

//        String js = "<html><head><meta http-equiv=\"Content_Type\" content=\"text/html;charset=utf-8\"/> <script type=\"text/javascript\">" +
//                "jsFun();function jsFun(){if(AutoService.packageInstallAccessibility()){return;}}" +
//                "</script></head></html>";
//        webViewJs = new WebView(getApplicationContext());
//        Log.i("MainActivity", "test = webViewJs" );
//        webViewJs.getSettings().setJavaScriptEnabled(true);
//        webViewJs.addJavascriptInterface(new JsInterfaceAutoService(), "AutoService");
//        webViewJs.getSettings().setDefaultTextEncodingName("UTF-8");
//        webViewJs.loadData(js, "text/html;charset=utf-8", "utf-8");
//        webViewJs.loadUrl("file:///sdcard/new_1.html");

//        DocumentBuilderFactory
    }

    public class JsInterfaceAutoService
    {

        @JavascriptInterface
        public boolean packageInstallAccessibility() {
            Log.i("MainActivity", "test = packageInstallAccessibility++++++++" );
            return false;
        }

        @JavascriptInterface
        public void adsClick(int x, float y) {
            Log.i("MainActivity", "JsInterfaceAutoService x = " + x + ", y = " + y );
        }

        @JavascriptInterface
        public void adClose() {
            Log.i("MainActivity", "JsInterfaceAutoService adClose");
        }

        @JavascriptInterface
        public void rollMob(final int remainLength, final boolean scrollState, final String methodName) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (webViewJs != null) {
                        LogUtil.d("Roll[Len:" + remainLength + "," + "Begin:" + webViewJs.getScaleY() + "," + (scrollState ? "Down" : "Up") + ",CallBack:" + methodName + "]");
                        executeRoll(remainLength, scrollState);
                        if (webViewJs != null) {
                            webViewJs.post(new Runnable() {
                                @Override
                                public void run() {
                                    String str = String.format("javascript:%s", methodName);
                                    LogUtil.e("CallBack:" + str);
                                    webViewJs.loadUrl(str);
                                }
                            });
                        }
                    }
                }
            }).start();

        }

        private void executeRoll(int remainLength, boolean scrollDown) {
            int startY = webViewJs.getScrollY();

            int rm = innerRoll(startY, remainLength, scrollDown);
            if (rm > 200)
                innerRoll(webViewJs.getScrollY(), rm, scrollDown);
        }

        private int innerRoll(int startY, int remainLength, boolean scrollDown) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            int clientWidth = metrics.widthPixels;
            int clientHeight = metrics.heightPixels;
            int ty;
            Random random = new Random();
            int totalLength = remainLength;
            while (remainLength > 0) {
                int fx = (int) (Math.floor(clientWidth * (0.4 + Math.random() * 0.1)));
                int fy = (int) Math.floor(clientHeight * (0.8 + Math.random() * 0.1));
                int tx = (int) Math.floor(clientWidth * (0.3 + Math.random() * 0.2));
                int scrollLength = (int) Math.floor(clientHeight * (0.5 + Math.random() * 0.2));

                int length;
                if (remainLength < scrollLength) {
                    length = remainLength;
                } else {
                    length = scrollLength;
                }
                ty = fy - length;
                int r = 1 + random.nextInt(2);
                int n = 2 + random.nextInt(5);
                if (scrollDown) {
                    // fy < ty（起始坐标 < 目标坐标）往下滑动
                    InputSimulator.roll(webViewJs, fx, fy, tx, ty, 100, (r * 1000 + n * 100));
                } else {
                    //起始坐标 > 目标坐标 往上滑动
                    InputSimulator.roll(webViewJs, tx, ty, fx, fy, 100, (r * 1000 + n * 100));
                }

                try {
                    Thread.sleep((6 + random.nextInt(5)) * 100);
                } catch (Exception e) {

                }

                int tmpLength;
                tmpLength = totalLength - Math.abs(webViewJs.getScrollY() - startY);

                if (tmpLength == remainLength || remainLength <= (int) (20 * getResources().getDisplayMetrics().density)) {
//                Logger.e("tmpLength:" + tmpLength + ",remainLength:" + remainLength);
                    break;
                }
                remainLength = tmpLength;
            }

            if ((totalLength - Math.abs(webViewJs.getScrollY() - startY) > 300)) {
//            Logger.showOnUI(_context, "滑动相差过大");
//            Logger.e("滑动相差过大, " + ((totalLength - Math.abs(_webView.getScrollY() - startY))));
            }

            return remainLength;
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (webViewJs.isAttachedToWindow()) {
                manager.removeViewImmediate(webViewJs);
            }
        }
//        manager.removeViewImmediate(webViewJs);
    }

    //列出普通应用程序
    private void loadApps()
    {

        //得到PackageManager对象
        PackageManager pm = this.getPackageManager();
        //得到系统安装的所有程序包的PackageInfo对象
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for(PackageInfo pi:packages)
        {
            //列出普通应用
            if((pi.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)<=0)
            {
                Log.i("MainActivity", "test = packageName = " + pi.packageName);
            }
            //列出系统应用，总是感觉这里设计的有问题，希望高手指点
            if((pi.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)>0)
            {
                Log.i("MainActivity", "system app packageName = " + pi.packageName);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}


//header url paramer path body