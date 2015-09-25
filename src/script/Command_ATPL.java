package script;

import arduino.XBeeFrameGenerator;
import wisen_simulation.SimLog;
import device.DataInfo;
import device.SensorNode;

public class Command_ATPL extends Command {

	protected String arg = "" ;
	
	public Command_ATPL(SensorNode sensor, String arg) {
		this.sensor = sensor ;
		this.arg = arg ;
	}

	@Override
	public long execute() {
		SimLog.add("S" + sensor.getId() + " ATPL "+arg);
		String args = sensor.getScript().getVariableValue(arg);
		sensor.setPl(Integer.valueOf(args));

		double ratio = (DataInfo.ChDataRate*1.0)/(DataInfo.UartDataRate);
		String message = "ATPL "+args;
		return (long)(Math.round(message.length()*8.*ratio));
		
		//return 0;
	}
	
	@Override
	public String getArduinoForm() {
		String s = XBeeFrameGenerator.at("PL"+arg); 
		return s;
	}
	
	@Override
	public String toString() {
		return "ATPL";
	}
}