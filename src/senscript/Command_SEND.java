/*----------------------------------------------------------------------------------------------------------------
 * CupCarbon: A Smart City & IoT Wireless Sensor Network Simulator
 * www.cupcarbon.com
 * ----------------------------------------------------------------------------------------------------------------
 * Copyright (C) 2013-2017 CupCarbon
 * ----------------------------------------------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *----------------------------------------------------------------------------------------------------------------
 * CupCarbon U-One is part of the research project PERSEPTEUR supported by the 
 * French Agence Nationale de la Recherche ANR 
 * under the reference ANR-14-CE24-0017-01. 
 * ----------------------------------------------------------------------------------------------------------------
 * Definition:
 * This class allows to send messages by a sensor
 * 
 * Command examples:
 * send A 4
 * -> The current sensor will send a message "A" to the sensor having an id = 4
 * send A
 * -> The current sensor will send a message "A" in a broadcast
 * send A *
 * -> The same than send A
 * send A * 3
 * -> The current sensor will send a message "A" in a broadcast mode except for the sensor having an id = 3
 * ----------------------------------------------------------------------------------------------------------------
 **/

package senscript;

import device.Channels;
import device.DeviceList;
import device.MultiChannels;
import device.SensorNode;
import radio_module.RadioPacketGenerator;
import radio_module.XBeeToArduinoFrameGenerator;
import utilities.UColor;
import wisen_simulation.SimLog;
import wisen_simulation.SimulationInputs;

/**
 * @author Ahcene Bounceur
 *
 */

public class Command_SEND extends Command {
	
	protected String arg1 = "";
	protected String arg2 = "";
	protected String arg3 = "";
	protected String arg4 = "";	
	protected String arg5 = "";	
	
	protected String rRadioName = "";
	
	protected boolean checkForAckMessage = false;
	
	// ---------------------------------------------------------------------------------------------------------------------
	// Constructor 
	// ---------------------------------------------------------------------------------------------------------------------
	public Command_SEND(SensorNode sensor, String arg1, String arg2) {
		this.sensor = sensor ;
		this.arg1 = arg1 ;
		this.arg2 = arg2.split(":")[0] ;
		if(arg2.split(":").length>1)
			rRadioName = arg2.split(":")[1] ;
		writtenInUART = false ;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	// Constructor 
	// ---------------------------------------------------------------------------------------------------------------------
	public Command_SEND(SensorNode sensor, String arg1, String arg2, String arg3) {
		this.sensor = sensor ;
		this.arg1 = arg1 ;
		this.arg2 = arg2 ;
		this.arg3 = arg3 ;
		writtenInUART = false ;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	// Constructor 
	// ---------------------------------------------------------------------------------------------------------------------
	public Command_SEND(SensorNode sensor, String arg1, String arg2, String arg3, String arg4) {
		this.sensor = sensor ;
		this.arg1 = arg1 ;
		this.arg2 = arg2 ;
		this.arg3 = arg3 ;
		this.arg4 = arg4 ;
		writtenInUART  = false ;
	}	
	
	// ---------------------------------------------------------------------------------------------------------------------
	// execute send
	// First we write in a UART (if writtenInUART=false)
	// Then the message will be sent by radio (if writtenInUART=true) 
	// Than we will wait for an ACK if this mode is activated (ie. SimulationInputs.ack=true)
	// No ACK for the broadcast sending : arg2.equals("*") && arg3.equals("")
	// ---------------------------------------------------------------------------------------------------------------------
	@Override
	public double execute() {	
		//System.out.println("===================================");
		//System.out.println("           CALL  " + sensor.getId()+" "+sensor.ackOk());
		//System.out.println("===================================");
		if (arg1.equals("!color")) {
			sensor.setRadioLinkColor(UColor.colorTab2[Integer.parseInt(arg2)]);
			return 0;
		}
		String message = arg1;
		message = sensor.getScript().getVariableValue(message);
		int messageLength = message.length();
		
		// In ACK mode (SimulationInputs.ack = true)
		// After sending a message (ackBlock = true), we verify if the ASK message is received (sensor.ackOk)
		// Otherwise, we recend the message
		if(SimulationInputs.ack && checkForAckMessage) {
			//System.out.println("ACK BLOCK");
			if(sensor.isAckReceived()) {
				//System.out.println(WisenSimulation.time + " ACK RECEIVED");
				sensor.setAckReceived(false);
				sensor.setAckWaiting(false);
				sensor.setAttempts(0);
				writtenInUART = false;
				executing = false;
				checkForAckMessage = false;
				return 0 ;
			}
			else {
				//System.out.println("RESEND");
				sensor.incAttempts();
				writtenInUART = true;
			}
		}
		
		// First operation, we start by writing the message in the UART of the card
		if(!writtenInUART) {
			//System.out.println("UART");
			sensor.setMessageLost(false);
			sensor.setAckReceived(false);
			sensor.setAttempts(0);
			SimLog.add("S" + sensor.getId() + " is writing the message : \"" + message + "\" in its buffer.");
			writtenInUART = true ;
			executing = true;
			double ratio = 1.0/(sensor.getUartDataRate());			
			return (ratio * (messageLength*8)) ;
		}
		
		//System.out.println(writtenInUART+ " "+sensor.ackOk() +" " + sensor.getAttempts() + " " + sensor.getNumberOfSends() );
		
		// ---------------------------------------------------------------------------------------------------------------------
		// Once the message is written in the UART, here we send it without ack if :
		// Non ACK mode (!SimulationInputs.ack)
		// or in a broadcast mode (send x *)
		// ---------------------------------------------------------------------------------------------------------------------		
		if (writtenInUART && (!SimulationInputs.ack || (arg2.equals("*") && arg3.equals("")))) {
			SimLog.add("S" + sensor.getId() + " starts sending the message : \"" + message + "\".");
			//System.out.println(WisenSimulation.time + " SEND " + message);
			//System.out.println("SEND");	
			sensor.setAckReceived(false);
			sensor.setAckWaiting(false);
			writtenInUART = false;
			executing = false;
			sensor.setAttempts(0);
			sendOperation(message);
			return 0;
		}

		// ---------------------------------------------------------------------------------------------------------------------
		// Once the message is written in the UART, here we send it with ack mode
		// ---------------------------------------------------------------------------------------------------------------------
		if (writtenInUART && SimulationInputs.ack && !sensor.isAckWaiting()) {
		//if (writtenInUART && SimulationInputs.ack) {
			if(sensor.getAttempts() < sensor.getNumberOfSends()) {
				 SimLog.add("S" + sensor.getId() + " starts sending the message : \"" + message + "\".");
				 //System.out.println(WisenSimulation.time+ " "+ sensor.getAttempts() + " SEND " + message);
				 sensor.setAckWaiting(true);
				 //System.out.println();				 
				 sendOperation(message);
				 checkForAckMessage = true;
				 return sensor.getTimeToResend();
			}
			else {
				Channels.numberOfLostMessages++;
				//System.out.println("MESSAGE LOST");
				sensor.setAckWaiting(false);
				checkForAckMessage = false;
				sensor.setAckReceived(false);
				writtenInUART = false;
				executing = false;
				sensor.setAttempts(0);
				sensor.setMessageLost(true);
				return 0;
			}
		}
		
		return 0;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	// Generate Arduino Code for the SEND function
	// ---------------------------------------------------------------------------------------------------------------------
	@Override
	public String getArduinoForm() {
		String s1 = sensor.getScript().getVariableValue(arg1);
		String s2 = sensor.getScript().getVariableValue(arg2);
		String s3 = sensor.getScript().getVariableValue(arg3);
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s3);
		String s = XBeeToArduinoFrameGenerator.data16(s1, s2, s3);
		return s;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
	// toString()
	// ---------------------------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		if(writtenInUART)
			return "SEND [BY RADIO]";	
		else 
			return "SEND [TO UART]"; 
		
	}
	
	// ---------------------------------------------------------------------------------------------------------------------
		// Operation of sending a message (prepare the packets according to the standard and the type of the command)
		// Calculate the energy consumption of the sending ooperation
		// ---------------------------------------------------------------------------------------------------------------------
		public void sendOperation(String message) {
			//String packet = RadioPacketGenerator.generate(sensor.getStandard());
			
			sensor.consumeTx(RadioPacketGenerator.packetLengthInBits(0, sensor.getStandard()));
			
			if (arg2.equals("*")) {
				SimLog.add("S" + sensor.getId() + " has finished sending in a broadcast the message : \"" + message + "\" to the nodes: ");
				double v = 0;
				if (!arg3.equals("")) {
					v = Double.valueOf(sensor.getScript().getVariableValue(arg3));
				}
				for (SensorNode rnode : sensor.getSensorNodeNeighbors()) {
					if (sensor.propagationDetect(rnode) && sensor.canCommunicateWith(rnode) && rnode.getId()!=v) {
						MultiChannels.addPacketEvent(2, message, sensor, rnode);
					}
				}
			}
			else {
				String dest = arg2;
				dest = sensor.getScript().getVariableValue(dest);
				double destNodeId;	
				if(!dest.equals("0")) {
					destNodeId = Double.valueOf(dest);
					SensorNode rnode = DeviceList.getSensorNodeById((int) destNodeId);
					if (rnode != null) {
						SimLog.add("S" + sensor.getId() + " has finished sending the message : \"" + message + "\" to the node: ");
						if (sensor.propagationDetect(rnode) && sensor.canCommunicateWith(rnode)) {
							if(rRadioName.equals("") || rRadioName.equals(rnode.getCurrentRadioModule().getName())) 
								MultiChannels.addPacketEvent(0, message, sensor, rnode);
						}
						else
							SimLog.add("S" + sensor.getId() + " and S" + destNodeId + "have not the same protocol!");
					}
					else 
						SimLog.add("S" + sensor.getId() + " can not send the message : \"" + message + "\" to the non-existent node: "+destNodeId);
				}
				else {
					dest = arg3;
					dest = sensor.getScript().getVariableValue(dest);
					destNodeId = Double.valueOf(dest);
					
					if(!dest.equals("0")) {
						SimLog.add("S" + sensor.getId() + " has finished sending the message : \"" + message + "\" to the nodes with MY="+destNodeId+": ");
						for(SensorNode rnode : DeviceList.sensors) {
							if ((sensor.propagationDetect(rnode)) && (rnode.getCurrentRadioModule().getMy()==destNodeId) && sensor.canCommunicateWith(rnode)) {
								SimLog.add("  -> S" + rnode.getId() + " ");							
								//rnode.setReceiving(true);
								MultiChannels.addPacketEvent(0, message, sensor, rnode);
							}						
						}
					}
					else {
						dest = arg4;
						dest = sensor.getScript().getVariableValue(dest);
						if(!dest.equals("0")) {
							destNodeId = Integer.valueOf(dest);
							SensorNode rnode = DeviceList.getSensorNodeById((int)destNodeId);
							if (rnode != null) {
								SimLog.add("S" + sensor.getId() + " has finished sending the message : \"" + message + "\" to the node: ");
								if (sensor.canCommunicateWith(rnode)) {
									MultiChannels.addPacketEvent(0, message, sensor, rnode);
								}
							}
							else 
								SimLog.add("S" + sensor.getId() + " can not send the message : \"" + message + "\" to the non-existent node: "+destNodeId);
						}
					}
				}
			}
		}
			
}