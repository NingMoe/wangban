var docViewer;
var start = new Date();
var starttime = start.getTime();
var staytime = 0;
function getDocViewer(){
    if(docViewer)
        return docViewer;
    else if(window.FlexPaperViewer)
        return window.FlexPaperViewer;
    else if(document.FlexPaperViewer)
	return document.FlexPaperViewer;
    else 
	return null;
}

/**
 * Handles the event of external links getting clicked in the document. 
 *
 * @example onExternalLinkClicked("http://www.google.com")
 *
 * @param String link
 */
function onExternalLinkClicked(link){
   // alert("link " + link + " clicked" );
   window.location.href = link;
}

/**
 * Recieves progress information about the document being loaded
 *
 * @example onProgress( 100,10000 );
 *
 * @param int loaded
 * @param int total
 */
function onProgress(loadedBytes,totalBytes){
}

/**
 * Handles the event of a document is in progress of loading
 *
 */
function onDocumentLoading(){
}

/**
 * Receives messages about the current page being changed
 *
 * @example onCurrentPageChanged( 10 );
 *
 * @param int pagenum
 */
function onCurrentPageChanged(pagenum){
    var end = new Date();
    var endtime = end.getTime();
    var temptime = endtime-starttime;
    if(temptime>3000){
    	staytime = temptime+staytime;
    	var postdata = {read_page:pagenum,doc_id:doc_id,stay_time:staytime};
    	$.ajax({ 
    		url: CONST.SERVICE_URL + "/index.php?r=Docpreview/updataReadPage",
    		data:postdata,
    		type:"POST",
    		success: function(){
    	}});
    }
	starttime = endtime;
}
/**
 * Receives messages about the document being loaded
 *
 * @example onDocumentLoaded( 20 );
 *
 * @param int totalPages
 */
function onDocumentLoaded(totalPages){
}

/**
 * Receives error messages when a document is not loading properly
 *
 * @example onDocumentLoadedError( "Network error" );
 *
 * @param String errorMessage
 */
function onDocumentLoadedError(errMessage){
}