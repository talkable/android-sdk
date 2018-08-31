package com.talkable.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManifestInfo {
    public static final String SCHEME_PREFIX = "tkbl-";
    public static final String API_KEY_PREFIX = "tkbl-api-key-";
    public static final String SERVER_PROPERTY_NAME = "tkbl-server";
    public static final String DEFAULT_SITE_SLUG_KEY = "tkbl-default-site-slug";

    private static ApplicationInfo getApplicationInfo(Context context, int flag) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), flag);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static Bundle getApplicationMetadata(Context context) {
        ApplicationInfo ai = getApplicationInfo(context, PackageManager.GET_META_DATA);
        if (ai != null && ai.metaData != null) {
            return ai.metaData;
        } else {
            throw new IncorrectInstallationException("Can't find configuration for Talkable SDK. " +
                    "Make sure you have setup it inside metadata elements as specified in the documentation.");
        }
    }

    public static String getAppScheme(String siteSlug) {
        return SCHEME_PREFIX + siteSlug;
    }

    public static void checkDeepLinkingScheme(Context context, Set<String> talkableSlugs) {
        for (String siteSlug : talkableSlugs) {
            String scheme = getAppScheme(siteSlug);
            if (!isManifestContainScheme(context, scheme)) {
                throw new IncorrectInstallationException("Deep linking scheme `" + scheme +
                        "` for Talkable SDK is not specified");
            }
        }
    }

    private static Boolean isManifestContainScheme(Context context, String scheme) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(scheme + "://"));

        List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo info : infos) {
            IntentFilter filter = info.filter;
            if (filter != null && filter.hasAction(Intent.ACTION_VIEW) && filter.hasCategory(Intent.CATEGORY_BROWSABLE)) {
                return true;
            }
        }
        return false;
    }

    public static String getServer(Context context) {
        return getApplicationMetadata(context).getString(SERVER_PROPERTY_NAME);
    }

    public static String getDefaultSiteSlug(Context context) {
        return getApplicationMetadata(context).getString(DEFAULT_SITE_SLUG_KEY);
    }

    public static Map<String, String> getCredentialsConfiguration(Context context) {
        Bundle metaData = getApplicationMetadata(context);
        Map<String, String> configuration = new HashMap<>();
        for (String key : metaData.keySet()) {
            if (key.startsWith(API_KEY_PREFIX)) {
                String prefixRegex = "^" + API_KEY_PREFIX;
                String slug = key.replaceFirst(prefixRegex, "");
                String apiKey = metaData.getString(key);
                if (apiKey == null || apiKey.isEmpty()) {
                    throw new IncorrectInstallationException("API key for `" + slug + "` can not be blank. " +
                            "Please check your AndroidManifest file");
                }
                configuration.put(slug, apiKey);
            }
        }
        return configuration;
    }
}
