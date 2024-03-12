package com.foobnix.pdf.info;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import com.foobnix.LibreraBuildConfig;
import com.foobnix.android.utils.Apps;
import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.LOG;
import com.foobnix.model.AppState;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.ebookdroid.droids.mupdf.codec.MuPdfDocument;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppsConfig {

    public static final String PRO_LIBRERA_READER = "com.foobnix.pro.pdf.reader";
    public static final String LIBRERA_READER = "com.foobnix.pdf.reader";
    public static final boolean ADS_ON_PAGE = false;

    public static final boolean IS_FDROID = LibreraBuildConfig.FLAVOR.equals("fdroid") || LibreraBuildConfig.FLAVOR.equals("huawei");
    public static final List<String> testDevices = Arrays.asList(
            "A55432A120DBEFA4C550628C53DA8D67",
            "5A11AAB3D40A6E42F8BB4674C013B70D");
    public static final boolean IS_WRITE_LOGS = IS_FDROID;
    public static final String FLAVOR = LibreraBuildConfig.FLAVOR;
    public static final boolean IS_ENABLE_1_PAGE_SEARCH = true;
    public final static ExecutorService executorService = Executors.newFixedThreadPool(2);
    public static boolean IS_LOG = Build.MODEL.startsWith("Android SDK") || Build.DEVICE.contains("emulator");
    public static boolean IS_TEST_DEVICE = false;
    public static String MUPDF_FZ_VERSION = "";
    public static String MUPDF_1_11 = "1.11";
    public static boolean isDOCXSupported = Build.VERSION.SDK_INT >= 26;
    public static boolean isCloudsEnable = false;

    static {
        System.loadLibrary("MuPDF");
        AppsConfig.MUPDF_FZ_VERSION = MuPdfDocument.getFzVersion();
        Log.d("Librera", "MUPDF_VERSION " + MUPDF_FZ_VERSION);
    }

    public static boolean isPDF_DRAW_ENABLE() {
        return true;
    }

    public static void init(Context c) {
        IS_TEST_DEVICE = testDevices.contains(ADS.getByTestID(c));
        if (IS_TEST_DEVICE) {
            IS_LOG = true;
        }
    }

    public static boolean checkIsProInstalled(final Context a) {
        if (a == null) {
            LOG.d("no-ads error context null");
            return true;
        }
        if (!isGooglePlayServicesAvailable(a)) {
            //no ads for old android and eink
            LOG.d("no-ads isGooglePlayServicesAvailable not available");
            return true;
        }
        if (Build.VERSION.SDK_INT <= 16 || Dips.isEInk()) {
            LOG.d("no-ads old device or eink");
            //no ads for old android and eink
            return true;
        }
        if (AppState.get().isEnableAccessibility) {
            return true;
        }
        if (Apps.isAccessibilityEnable(a)) {
            return true;
        }

        boolean is_pro = isPackageExisted(a, PRO_LIBRERA_READER);
        return is_pro;
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    public static boolean isPackageExisted(final Context a, final String targetPackage) {
        try {
            final PackageManager pm = a.getPackageManager();
            pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (final NameNotFoundException e) {
            return false;
        }
        return true;
    }

}
