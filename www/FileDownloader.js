/*
 * Plugin to download a file
 */
window.downloadFile = function (url, filename, successCallback, errorCallback) {
	cordova.exec(callback, errorCallback, "FileDownloader", "downloadFile", [url, filename]);
};