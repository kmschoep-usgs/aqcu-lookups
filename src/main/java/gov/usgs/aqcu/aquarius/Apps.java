package gov.usgs.aqcu.aquarius;

import java.util.ArrayList;
import net.servicestack.client.*;

public class Apps
{
    @Route(Path="/locations/search", Verbs="GET")
    public static class SearchLocationsServiceRequest implements IReturn<SearchLocationsServiceResponse>
    {
        /**
        * Location identifier
        */
        @ApiMember(Description="Page Size", IsRequired=true)
        public Integer n = null;

        /**
        * True if location attachments should be included in the results
        */
        @ApiMember(Description="Location Query String", IsRequired=true)
        public String q = null;
        
        public String getQueryString() { return q; }
        public SearchLocationsServiceRequest setQueryString(String value) { this.q = value; return this; }
        public Integer getPageSize() { return n; }
        public SearchLocationsServiceRequest setPageSize(Integer value) { this.n = value; return this; }
        private static Object responseType = SearchLocationsServiceResponse.class;
        public Object getResponseType() { return responseType; }
    }

    public static class SearchLocationsServiceResponse
    {
        /**
        * Location Search Results
        */
        @ApiMember(DataType="Array<LocationSearchResult>", Description="Location Search Results")
        public ArrayList<LocationSearchResult> Results = null;
        
        /**
        * Limit Exceeded
        */
        @ApiMember(DataType="Boolean", Description="Whether or not more results were found than returned due to the page size limit.")
        public Boolean LimitExceeded = null;
        
        public ArrayList<LocationSearchResult> getResults() { return Results; }
        public SearchLocationsServiceResponse setResults(ArrayList<LocationSearchResult> value) { this.Results = value; return this; }
        public Boolean isLimitExceeded() { return LimitExceeded; }
        public SearchLocationsServiceResponse setLimitExceeded(Boolean value) { this.LimitExceeded = value; return this; }
    }

    public static class LocationSearchResult
    {
        /**
        * Id
        */
        @ApiMember(Description="Id")
        public Integer Id = null;

        /**
        * Location Folder Id
        */
        @ApiMember(Description="Location Folder Id")
        public Integer LocationFolderId = null;

        /**
        * Identifier
        */
        @ApiMember(Description="Identifier")
        public String Identifier = null;

        /**
        * Name
        */
        @ApiMember(Description="Name")
        public String Name = null;
        
        /**
        * Description
        */
        @ApiMember(Description="Description")
        public String Description = null;
        
        /**
        * User Data
        */
        @ApiMember(Description="User Data")
        public String UserData = null;
        
        /**
        * Source
        */
        @ApiMember(Description="Source")
        public String Source = null;
        
        /**
        * Elevation Units
        */
        @ApiMember(Description="Elevation Units")
        public String ElevationUnits = null;
        
        /**
        * UTCOffset
        */
        @ApiMember(Description="UTCOffset")
        public Integer UTCOffset = null;
        
        /**
        * Elevation
        */
        @ApiMember(Description="Elevation")
        public Double Elevation = null;
        
        /**
        * Latitude
        */
        @ApiMember(Description="Latitude")
        public Double Latitude = null;
        
        /**
        * Longitude
        */
        @ApiMember(Description="Longitude")
        public Double Longitude = null;
        
        /**
        * Location Type Id
        */
        @ApiMember(Description="Location Type Id")
        public String LocationTypeId = null;
        
        /**
        * Datasets - Unsure what this is
        */
        @ApiMember(Description="Datasets")
        public Object Datasets = null;
        
        /**
        * Sub Locations - Unsure what this is
        */
        @ApiMember(Description="Sub Locations")
        public Object SubLocations = null;
        
        /**
        * Hot Folder - Unsure what this is
        */
        @ApiMember(Description="Hot Folder")
        public Object HotFolder = null;
        
        /**
        * Location Role - Unsure what this is
        */
        @ApiMember(Description="Location Role")
        public Object LocationRole = null;
        
        /**
        * Location Types - Unsure what this is
        */
        @ApiMember(Description="Location Types")
        public Object LocationTypes = null;

        public Integer getId() {return this.Id;}
        public Integer getLocationFolderId() {return this.LocationFolderId;}
        public String getIdentifier() {return this.Identifier;}
        public String getName() {return this.Name;}
        public String getDescription() {return this.Description;}
        public String getUserData() {return this.UserData;}
        public String getSource() {return this.Source;}
        public String getElevationUnits() {return this.ElevationUnits;}
        public Integer getUTCOffset() {return this.UTCOffset;}
        public Double getElevation() {return this.Elevation;}
        public Double getLatitude() {return this.Latitude;}
        public Double getLongitude() {return this.Longitude;}
        public String getLocationTypeId() {return this.LocationTypeId;}
        public Object getDatasets() {return this.Datasets;}
        public Object getSubLocations() {return this.SubLocations;}
        public Object getHotFolder() {return this.HotFolder;}
        public Object getLocationRole() {return this.LocationRole;}
        public Object getLocationTypes() {return this.LocationTypes;}

        public LocationSearchResult setId(Integer value) {this.Id = value; return this;}
        public LocationSearchResult setLocationFolderId(Integer value) {this.LocationFolderId = value; return this;}
        public LocationSearchResult setIdentifier(String value) {this.Identifier = value; return this;}
        public LocationSearchResult setName(String value) {this.Name = value; return this;}
        public LocationSearchResult setDescription(String value) {this.Description = value; return this;}
        public LocationSearchResult setUserData(String value) {this.UserData = value; return this;}
        public LocationSearchResult setSource(String value) {this.Source = value; return this;}
        public LocationSearchResult setElevationUnits(String value) {this.ElevationUnits = value; return this;}
        public LocationSearchResult setUTCOffset(Integer value) {this.UTCOffset = value; return this;}
        public LocationSearchResult setElevation(Double value) {this.Elevation = value; return this;}
        public LocationSearchResult setLatitude(Double value) {this.Latitude = value; return this;}
        public LocationSearchResult setLongitude(Double value) {this.Longitude = value; return this;}
        public LocationSearchResult setLocationTypeId(String value) {this.LocationTypeId = value; return this;}
        public LocationSearchResult setDatasets(Object value) {this.Datasets = value; return this;}
        public LocationSearchResult setSubLocations(Object value) {this.SubLocations = value; return this;}
        public LocationSearchResult setHotFolder(Object value) {this.HotFolder = value; return this;}
        public LocationSearchResult setLocationRole(Object value) {this.LocationRole = value; return this;}
        public LocationSearchResult setLocationTypes(Object value) {this.LocationTypes = value; return this;}
    }
}