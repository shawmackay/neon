function toggleArea(id){
	if(document.getElementById(id).style.visibility=="hidden"){
		document.getElementById(id).style.visibility="visible";
		document.getElementById(id).style.display="block";
	} else {
		document.getElementById(id).style.visibility="hidden";
		document.getElementById(id).style.display="none";
	}
}