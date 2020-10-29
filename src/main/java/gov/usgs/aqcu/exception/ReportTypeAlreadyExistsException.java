package gov.usgs.aqcu.exception;

public class ReportTypeAlreadyExistsException extends RuntimeException{
    public ReportTypeAlreadyExistsException(String folder){
        super(String.format("Specified report type already exist within folder '%1$s'", folder));
    }
}
