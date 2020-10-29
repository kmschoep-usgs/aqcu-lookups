package gov.usgs.aqcu.exception;

public class ReportTypeAlreadyExistsException extends RuntimeException{
    public ReportTypeAlreadyExistsException(String folder){
        super(String.format("Specified report type already exists. Please modify the report in folder '%1$s'", folder));
    }
}
