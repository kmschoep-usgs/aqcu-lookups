package gov.usgs.aqcu.model.report;

public class ReportBasicParameter {
	private String name;
	private String display;
	private String type;
	private String inputType;
	private Boolean multi;

	public ReportBasicParameter(){}
	
	public ReportBasicParameter(String name, String display, String type, String inputType, Boolean multi) {
		this.name = name;
		this.display = display;
		this.type = type;
		this.inputType = inputType;
		this.multi = multi;
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
	}
	
	public Boolean getMulti() {
		return multi;
	}

	public void setMulti(Boolean multi) {
		this.multi = multi;
	}	

	public String getInputType() {
		return inputType;
	};
	
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	
	
}