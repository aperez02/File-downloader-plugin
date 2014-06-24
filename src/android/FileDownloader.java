package edu.csusb.advising;

import java.util.List;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

public class FileDownloader extends CordovaPlugin{
	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
	    if ("downloadFile".equals(action)) {
	        final String fileURL = args.getString(0);
	        final String fileName = args.getString(1);
	        
	        cordova.getActivity().runOnUiThread(new Runnable() {
	            public void run() {
	            	if(!downloadFile(fileURL, fileName))
	            		callbackContext.error("Unable to download file");
	            	
	                callbackContext.success("Successfully downloaded file"); // Thread-safe.
	            }
	        });
	        return true;
	    }
	    return false;
	}
	public static boolean isDownloadManagerAvailable(Context context)
	{
		try 
		{
	        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) 
	        {
	            return false;
	        }
	        Intent intent = new Intent(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
	        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
	                PackageManager.MATCH_DEFAULT_ONLY);
	        return list.size() > 0;
	    } 
		catch (Exception e) 
	    {
	        return false;
	    }
	}
	public boolean downloadFile(String url, String filename)
	{
		if (isDownloadManagerAvailable(this.cordova.getActivity().getApplication().getApplicationContext()))
		{
			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
			request.setDescription("Files downloading from: " + url);
			request.setTitle(filename);
			// in order for this if to run, you must use the android 3.2 to compile your app
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				request.allowScanningByMediaScanner();
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			}
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

			// get download service and enqueue file
			DownloadManager manager = (DownloadManager) this.cordova.getActivity().getApplication().getSystemService(Context.DOWNLOAD_SERVICE);
			manager.enqueue(request);
			return true;
		}
		return false;
	}

}
