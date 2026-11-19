/**
 * Open popup help window with short text explaining the search form field.
 * @param windowURL url
 */
function openHelpWindow(url) {
	var helpWindow = window.open(url, 'helpWindow', 'width=460,height=300,toolbar=0,location=0,directories=0,status=0,menuBar=0,scrollBars=1,resizable=1');
	helpWindow.focus();
}

/**
 * Open popup window with the IP (intellectual property) item details
 * @param url
 */
function openDetailsWindow( url ) {
	var detailsWindow = window.open(url, 'detailsWindow', 'width=800,height=600,toolbar=0,location=0,directories=0,status=0,menuBar=0,scrollBars=1,resizable=1') ;
	detailsWindow.focus();
}

/**
 * Open popup window with the image
 * @param url
 */
function openImageWindow(imageUrl) {
	imageWindow = window.open(imageUrl, 'imageWindow', '');
	imageWindow.focus();
}
