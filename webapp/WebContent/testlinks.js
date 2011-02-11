
<!--
///////////////////Hex Conversions/////////////////////


function enHex(aDigit)

{

    return("0123456789ABCDEF".substring(aDigit, aDigit+1))

}

// convert a hex digit into decimal

function deHex(aDigit)

{

    return("0123456789ABCDEF".indexOf(aDigit))

}

// Convert a 24bit number to hex

function toHex(n)

{

    return (enHex((0xf00000 & n) >> 20) +

            enHex((0x0f0000 & n) >> 16) +

            enHex((0x00f000 & n) >> 12) +

            enHex((0x000f00 & n) >>  8) +

            enHex((0x0000f0 & n) >>  4) +

            enHex((0x00000f & n) >>  0))

}

// Convert a six character hex to decimal

function toDecimal(hexNum)

{

   	var tmp = ""+hexNum.toUpperCase()

    while (tmp.length < 6) tmp = "0"+tmp

   

   	return ((deHex(tmp.substring(0,1)) << 20) +

       	    (deHex(tmp.substring(1,2)) << 16) + 

            (deHex(tmp.substring(2,3)) << 12) +

            (deHex(tmp.substring(3,4)) << 8) +

            (deHex(tmp.substring(4,5)) << 4) +

   	        (deHex(tmp.substring(5,6))))

}



///////////////////Shimmering Links/////////////////////

//global variables

var hoverColour

var numLinks;

var rate;

var numFadeLevels;

var bgR;

var bgG;

var bgB;

var currR;

var currG;

var currB;

var count;

var fadeOut;

var continuous;

var newColour;

var tID;

var redInterval;

var greenInterval;

var blueInterval;



	

function initLinks(mouseOverColour, numberOfLinks, fadeOutColour)

{

	hoverColour = mouseOverColour;

	numLinks = numberOfLinks;

	rate = 1;

	numFadeLevels = 30;

	

	function initArray(theArray, length, val)

	{

		for(i=0;i<length;i++)

		{

			theArray[i] = val;

		}

	}

	

	bgR = '0000' + fadeOutColour.substring(1,3)

	bgG = '0000' + fadeOutColour.substring(3,5)

	bgB = '0000' + fadeOutColour.substring(5,7)

	currR = new Array(numLinks);

	currG = new Array(numLinks);

	currB = new Array(numLinks);

	count = new Array(numLinks);

	fadeOut = new Array(numLinks);

	continuous = new Array(numLinks);

	newColour = new Array(numLinks);

	tID = new Array(numLinks);

	redInterval = toDecimal(bgR) / numFadeLevels;

	greenInterval = toDecimal(bgG) / numFadeLevels;

	blueInterval = toDecimal(bgB) / numFadeLevels;

	

	initArray(currR,numLinks,0);

	initArray(currG,numLinks,0);

	initArray(currB,numLinks,0);

	initArray(count,numLinks,0);

	initArray(fadeOut,numLinks,true);

	initArray(continuous,numLinks,true);

}	

function startFade(id)

{

	if(fadeOut[id] == true)

	{ /*move colour towards background colour (increment)*/

		currR[id] += redInterval;

		currG[id] += greenInterval;

		currB[id] += blueInterval;

		newColour[id] = '#' + (toHex(currR[id])).substring(4,6) + (toHex(currG[id])).substring(4,6) + (toHex(currB[id])).substring(4,6);

		if(++count[id] == numFadeLevels)

		{

			fadeOut[id] = false;

		}

	}

	else

	{

		currR[id] -= redInterval;

		currG[id] -= greenInterval;

		currB[id] -= blueInterval;

		newColour[id] = '#' + (toHex(currR[id])).substring(4,6) + (toHex(currG[id])).substring(4,6) + (toHex(currB[id])).substring(4,6);

		if(--count[id] == 0)

		{

			fadeOut[id] = true;

		}

	}

	if(continuous[id] == true)

	{

		document.getElementById(id).style.color = newColour[id];		

	}

	else

	{

		document.getElementById(id).style.color = hoverColour;

	}

	clearTimeout(tID[id]);

	tID[id]=setTimeout('startFade(' + id + ')', rate);

}

function continueFade(id)

{

	continuous[id] = true;

}

function stopFade(id)

{

	continuous[id] = false;

}

function StartTimers()

{	//set up an initial set of timers to start the shimmering effect

	for(id=0; id<numLinks; id++)

	{

		t=setTimeout('startFade(' + id + ')', id*100);

	}

}

//format = initLinks('mouse-over colour', 'number of links', 'fade-out colour')

initLinks('#0000FF', 6, '#77CC77');

//-->



