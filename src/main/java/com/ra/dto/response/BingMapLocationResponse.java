package com.ra.dto.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BingMapLocationResponse {
    private String authenticationResultCode;
    //    private String brandLogoUri;
//    private String copyright;
    private List<ResourceSet> resourceSets;
    private int statusCode;
    private String statusDescription;
    private String traceId;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResourceSet {
        private int estimatedTotal;
        private List<Resource> resources;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Resource {
        private String type;
        //        private List<Double> bbox;
//        private String name;
        private Point point;
        private Address address;
//        private String confidence;
//        private String entityType;
//        private List<GeocodePoint> geocodePoints;
//        private List<String> matchCodes;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Point {
        private String type;
        private List<Double> coordinates;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String adminDistrict;
        private String adminDistrict2;
        private String countryRegion;
        private String formattedAddress;
        private String locality;
    }

//    public static class GeocodePoint{
//        private String type;
//        private List<Double> coordinates;
//        private String calculationMethod;
//        private List<String> usageTypes;
//    }
}

