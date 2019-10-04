package gov.usgs.aqcu.model;

public class ReportBasicParameter {
	private String name;
	private String display;
	private String type;

	public ReportBasicParameter(){}
	
	public ReportBasicParameter(String name, String display, String type) {
		this.name = name;
		this.display = display;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	};
	
	
}