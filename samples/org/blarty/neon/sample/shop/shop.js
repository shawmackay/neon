function doAddSubmit(){
	var checkSelected=document.getElementById('WDGT1Select');
	var i;
	
	var quantity = document.getElementById('quantity');
	var reference = document.getElementById('reference');
	quantity.value = "";
	reference.value="";
	for(i=0;i<numitems;i++){
	    var selection = document.getElementById('select'+i);
	    if(selection.checked){
	        var itemref = document.getElementById('hidden'+i);
	//alert(checkSelected.checked);
	if(document.getElementById('quantity'+i).value == "")
		    document.getElementById('quantity'+i).value=0;
		quantity.value = quantity.value + "|" +document.getElementById('quantity'+i).value;
		
		reference.value=reference.value+"|"+itemref.value;
		
		}
	}
	//quantity.value = substr(quantity.value,2);
	quantity.value = quantity.value.substring(1, quantity.value.length);
	reference.value = reference.value.substring(1, reference.value.length);
	document.getElementById('addForm').submit();
}  