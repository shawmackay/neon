/*
* Copyright 2005 neon.jini.org project 
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
* 
*       http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License.
*/

package org.jini.projects.neon.agents.sensors;

/*
* TimeFilter.java
*
* Created Tue Apr 05 10:36:37 BST 2005
*/
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
/**
* A <code>SensorFilter</code> used to filter out what particular time durations a listener
* wants to know about
* @author  calum
*
*/

public class TimeFilter implements org.jini.projects.neon.agents.sensors.SensorFilter{
	
	public static final int EVERY=1;
	public static final int BOUNDARY=2;
	public static final int ABSOLUTE=4;
	
	private boolean every;
	private boolean boundary;
	private boolean called=false;
	private boolean absolute;
	private int hour;
	private int minute;
	private int second;
	private long timecreated;
	/**
	* Creates a time filter, designed to be called by the Sensor, when the time changes<br>
	* The <code>every</code> parameter, denotes that the check should be repeated.<br>
	* The <code>boundary</code> parameter denotes that the filter will accept, when the time
	* is repeats based on the time starting 00:00, otherwise it will accept every <i>duration</i> since the time the
	* filter was created<br>
	* EG<br>
	* with boundary set to true a duration of 1:30hrs will call at 12:00, 1:30, 3:00 and so on<br>
	* with boundary set to false, a Filter created at 2:14 will accept at 3:44, 5:14, 6:44....
	* <br><br>To have a filter call at a particular time of day, set the <code>onceperday</code> parameter
	*/
	public TimeFilter(int checkParms, int hour, int minute, int second){
		this.every = ((checkParms & EVERY)== EVERY);
		this.boundary = ((checkParms & BOUNDARY)  == BOUNDARY);
		this.absolute = ((checkParms & ABSOLUTE) == ABSOLUTE);
		//System.out.println("E B A: " + every + " "  + boundary + " " +absolute);
		this.hour = hour;
		this.minute=minute;
		this.second = second;
		this.timecreated=System.currentTimeMillis();
	}
	
	/**
	* accept
	* @param aObject
	* @return boolean
	*/
	public boolean accept(Object  aObject){
		Date d = (Date) aObject;
		long fireatTime = (this.hour*60*60) + (this.minute*60) + this.second;
		
		if(!every && !called){
			if(boundary && !absolute){
				return checkBoundaryTime(d);
			}
			if(absolute)
				return checkAbsoluteTime(d);
		} else {
			if(boundary && !absolute){
				return checkBoundaryTime(d);
			}
			if(absolute)
				return checkAbsoluteTime(d);
		}
		return false;
	}
	public static void main(String[] args) {
		TimeFilter tf =new TimeFilter(TimeFilter.EVERY  | TimeFilter.ABSOLUTE,0,0,15);
		try{
			int counter=0;
			for(;;){
				Thread.sleep(1000);
				if(tf.accept(new Date(System.currentTimeMillis()))){					
					if (++counter==4)
						break;
				}
					
			}
		}catch(Exception ex){
			System.err.println("Caught Exception: "+ ex.getClass().getName() + "; Msg: " + ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	private boolean checkBoundaryTime(Date d){
		long fireatTime = (this.hour*60*60) + (this.minute*60) + this.second;
		Calendar currentTimeCalendar = new GregorianCalendar();
		currentTimeCalendar.setTime(d);
		
		Calendar cal = new GregorianCalendar();
		//Midnight
		cal.set(currentTimeCalendar.get(Calendar.YEAR),currentTimeCalendar.get(Calendar.MONTH),
		currentTimeCalendar.get(Calendar.DAY_OF_MONTH), 0,0,0);
		long secondsFromMidnight=(currentTimeCalendar.get(Calendar.HOUR_OF_DAY) * 60 * 60) +
		(currentTimeCalendar.get(Calendar.MINUTE)*60) + (currentTimeCalendar.get(Calendar.SECOND));
		if(secondsFromMidnight % fireatTime==0){
			called=true;
			return true;
		}
		return false;
	}
	
	private boolean checkAbsoluteTime(Date d) {
		Calendar currentTimeCalendar = new GregorianCalendar();
		currentTimeCalendar.setTime(d);
		
		Calendar cal = new GregorianCalendar();
		cal.set(currentTimeCalendar.get(Calendar.YEAR),currentTimeCalendar.get(Calendar.MONTH),
		currentTimeCalendar.get(Calendar.DAY_OF_MONTH), this.hour,this.minute,this.second);
	
		if(boundary){
			if(currentTimeCalendar.get(Calendar.HOUR_OF_DAY)==this.hour)
				if(currentTimeCalendar.get(Calendar.MINUTE)==this.minute)
				if(currentTimeCalendar.get(Calendar.SECOND)==this.second)
				return true;
		} else {
			Date created = new Date(this.timecreated);
			GregorianCalendar createdCal = new GregorianCalendar();
			createdCal.setTime(created);
			GregorianCalendar checkAt = new GregorianCalendar();
			checkAt.setTime(created);
			checkAt.add(Calendar.HOUR_OF_DAY, this.hour);
			checkAt.add(Calendar.MINUTE, this.minute);
			checkAt.add(Calendar.SECOND, this.second);
			if(currentTimeCalendar.get(Calendar.HOUR_OF_DAY)== checkAt.get(Calendar.HOUR_OF_DAY))
				if(currentTimeCalendar.get(Calendar.MINUTE)== checkAt.get(Calendar.MINUTE))
				if(currentTimeCalendar.get(Calendar.SECOND)==checkAt.get(Calendar.SECOND))
				return true;
		}
		
		return false;
	}
	
}

