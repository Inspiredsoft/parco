function ShowHide(divId)
{
    if(document.getElementById(divId).style.display == 'none')
    {
        document.getElementById(divId).style.display='block';
    }
    else
   	{
    	document.getElementById(divId).style.display='none';
   	}
}

function showWaitDiv()
{
    document.getElementById( "id_waitDiv" ).style.visibility='visible';
    return true;
}

function hideWaitDiv()
{
 	document.getElementById( "id_waitDiv" ).style.visibility='hidden';
	return true;
}

function showWaiting(data) {
    var ajaxStatus = data.status;
    switch (ajaxStatus) {
        case "begin":
            //This is called right before ajax request is been sent.
            //Showing the div where the "loading" gif is located
        	showWaitDiv();
            break;

        case "success":
            //This is called when ajax response is successfully processed.
            //In your case, you will need to redirect to your success page
        	hideWaitDiv();
            break;
    }
    return true;
}